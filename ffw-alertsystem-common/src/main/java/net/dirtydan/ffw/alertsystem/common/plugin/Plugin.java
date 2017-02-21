/*
  Copyright (c) 2015-2017, Max Stark <max.stark88@web.de>
    All rights reserved.
  
  This file is part of ffw-alertsystem, which is free software: you
  can redistribute it and/or modify it under the terms of the GNU
  General Public License as published by the Free Software Foundation,
  either version 2 of the License, or (at your option) any later
  version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, see <http://www.gnu.org/licenses/>.
*/

package net.dirtydan.ffw.alertsystem.common.plugin;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashSet;
import java.util.Set;

import net.dirtydan.ffw.alertsystem.common.util.Logger;



/**
 * Abstract base-class for all plugins within the ffw-alertsystem. Provides
 * basic functionalty like starting, reloading (config), running, stopping
 * and killing a plugin. <br>
 * All user-defined code of an plugin runs within the plugin-thread, this
 * ensures that an exception has no effect for the core-system or the other
 * plugins.
 * 
 * @param <PluginConfigT> Class which describes the properties of a plugin
 *                        (has to extend the base-class @PluginConfig)
 */
public abstract class Plugin<PluginConfigT extends PluginConfig>
                                           implements Runnable {
  
  /**
   * The logger for the plugin. The @PluginLogger class acts as a fassade for
   * an instance of the Logger-class.
   */
  protected PluginLogger log;
  
  /**
   * The plugin's config-file. Private and no setter-method, so nobody except
   * the plugin itself is able to set a new config.
   */
  private PluginConfigT _config;
  
  /**
   * The Thread in which context the plugin-code is executed. Will be created
   * and started in the method {@link Plugin#start()}.
   */
  private Thread _thread;
  
  /**
   * Set with state-observer of the plugin. Observer have to implement the
   * @PluginObserver interface and will be called in the method
   * {@link Plugin#notifyObserver()}.
   */
  private final Set<PluginObserver> _observer;
  
  /**
   * Enum which represents the possible states of a plugin.
   */
  public enum PluginState {
    CREATED,
    STOPPED,
    STARTED,
    SLEEPING,
    RUNNING,
    RELOADING,
    ERROR;
    
    @Override
    public String toString() {
      switch(this) {
        case CREATED:   return "created";
        case STOPPED:   return "stopped";
        case STARTED:   return "started";
        case SLEEPING:  return "sleeping";
        case RUNNING:   return "running";
        case RELOADING: return "reloading";
        case ERROR:     return "error";
        default: throw new IllegalArgumentException();
      }
    }
  }
  
  /**
   * Field which represents the current state of a plugin.
   */
  private PluginState _state;
  
  /**
   * Internal flag which signals the {@link Plugin#run()}-method to raise a
   * reload by calling {@link Plugin#onPluginReload()}.
   */
  private volatile boolean _raiseReload = false;
  
  /**
   * If the wakeUp()-method is called and the plugin isn't sleeping but in
   * STARTED/RELOADING/RUNNING state, this flag indicates that the plugin shall
   * not got sleeping and execute run()/reload() again.
   */
  private volatile boolean _keepOnRunning = false;
  
  /**
   * Flag for plugin-running-loop.
   */
  private volatile boolean _stopped = true;
  
  /**
   * If an uncaught error occured the resulting @Throwable object will be
   * stored in this field.
   */
  private Throwable _error;
  
  
  
  /**
   * Constructor sets the plugin in CREATED-state. The plugin is not capable to
   * run, first the {@link Plugin#created()} method has to be called.
   */
  protected Plugin() {
    _observer = new HashSet<>();
    _state = PluginState.CREATED;
  }
  
  /**
   * Sets the config-file of the plugin and creates the @PluginLogger.
   * @param config The plugins config-file.
   */
  protected final void setConfig(PluginConfigT config) {
    _config = config;
    
    log = new PluginLogger(Logger.getApplicationLogger(),
            _config.getInstanceName(),
            _config.getLogLevel()
          );
  }
  
  /**
   * Can be called from the @PluginManager when and only when a plugin is newly
   * created.
   */
  protected final void created() {
    notifyObserver();
//    onPluginCreated();
  }
  
//  TODO: is not called within plugin-thread
//  protected void onPluginCreated() {}
  
  
  
  /**
   * Starts the plugin-thread and sets the exception-handler, see
   * {@link Plugin#getExceptionHandler()}. If the plugin is already running this
   * method has no effect; the plugin-state has to be INITIALIZED.
   */
  protected final void start() {
    if (_state == PluginState.CREATED) {
      // (re-)set the error-object
      _error = null;
      
      _thread = new Thread(this);
      _thread.setName(_config.getInstanceName() + "-thread");
      _thread.setUncaughtExceptionHandler(new PluginExceptionHandler());
      _thread.start();
    }
  }
  
  /**
   * Restarts the plugin, only when state is ERROR or STOPPED.
   */
  protected final void restart() {
    if (_state == PluginState.ERROR ||
        _state == PluginState.STOPPED) {
      _state = PluginState.CREATED;
      start();
    }
  }
  
  /**
   * The method is intended for user-defined code which should be executed 
   * before the plugin actual runs.
   * Is called from {@link Plugin#run()}-method when the plugin starts or when
   * it was restarted.
   */
  protected void onPluginStart() {}
  
  
  
  /**
   * Sets a new config-file for the plugin and raises a reload, which is then
   * executed in the {@link Plugin#run()}-method.
   * This method is not executed within the plugin-thread (called from
   * @PluginManager thread), hence the {@link Plugin#onPluginReload()}-method
   * with user code is not called here in order to avoid an exception or
   * blocking code, which would affect the core-system.
   * 
   * @param config The plugins updated config-file.
   */
  protected final void reload(PluginConfigT config) {
    setConfig(config);
    
    _raiseReload = true;
    callRun();
  }
  
  /**
   * The method is intended for user-defined code which shall be executed
   * every time the plugin was reloaded, which means the config-file has
   * changed.
   * Is called from {@link Plugin#run()}-method.
   */
  protected void onPluginReload() {}
  
  
  
  /**
   * Implements the @Runnable-run-method and contains all the code, which is
   * executed within the plugin-thread. This includes following methods: <br>
   * - {@link Plugin#onPluginStart()} <br>
   * - {@link Plugin#onRun()} <br>
   * - {@link Plugin#onPluginReload()} <br>
   * - {@link Plugin#onPluginStop()} <br>
   */
  @Override
  public final void run() {
    _stopped = false;
    
    log.debug("plugin was started");
    _state = PluginState.STARTED;
    notifyObserver();
    onPluginStart();
    
    while (!_stopped) {
      if (_keepOnRunning) {
        _keepOnRunning = false;
      } else {
        try {
          _state = PluginState.SLEEPING;
          notifyObserver();
          _thread.join();
        } catch (InterruptedException e) {
          if (_stopped) break;
          log.debug("plugin was woken up");
        }
      }
      
      if (_raiseReload) {
        log.info("plugin reload was raised", true);
        _state = PluginState.RELOADING;
        notifyObserver();
        onPluginReload();
        _raiseReload = false;
      
      } else {
        log.debug("plugin calls run-method");
        _state = PluginState.RUNNING;
        notifyObserver();
        onRun();
      }
    }
    
    log.debug("plugin was stopped");
    _state = PluginState.STOPPED;
    notifyObserver();
    onPluginStop();
  }
  
  /**
   * The actual code of the plugin is executed in this method. The plugin-sub-
   * classes must override this method.
   * Is called from the {@link Plugin#run()}-method if the plugin is active.
   */
  protected abstract void onRun();
  
  /**
   * Wakes the plugin if it is sleeping or sets a flag that it shall keep
   * running if it is currently executing something. Has no effect if plugin is
   * not yet started (INITIALIZED), STOPPED or in ERROR state.
   */
  protected final void callRun() {
    if (_state == PluginState.SLEEPING) {
      _thread.interrupt();
    } else if (_state == PluginState.STARTED ||
               _state == PluginState.RUNNING ||
               _state == PluginState.RELOADING) {
      _keepOnRunning = true;
    }
  }
  
  
  
  /**
   * Stops the plugin: sets the internal stopped-flag to true. The state of the
   * plugin ({@link Plugin#state}) is not changed here, this is done when the
   * plugin has indeed stopped its execution and left its execution loop, see
   * {@link Plugin#run}. Can be restarted with {@link Plugin#start()}.<br>
   * <b>Note:</b> It is not ensured that the plugin actually stops after the
   * call. For this use {@link PluginManager#stopPlugin()} instead.
   */
  protected final synchronized void stop() {
    log.debug("plugin was asked to stop");
    
    if (_state == PluginState.CREATED ||
        _state == PluginState.STOPPED ||
        _state == PluginState.ERROR) {
      log.debug("... but is not running anyways");
    } else {
      _stopped = true;
      callRun();
    }
  }
  
  /**
   * The method is intended for user-defined code which shall be executed every
   * time the plugin was stopped.
   * Is called at the end of the {@link Plugin#run()}-method.
   */
  protected void onPluginStop() {}
  
  /**
   * Calls the plugin-thread's {@link Thread#join()}-method and waits at most
   * the given time for the thread to die.
   * @param time The time to wait in seconds.
   */
  protected void join(int time) {
    if (isAlive()) {
      try {
        log.debug("wait for plugin to finish");
        _thread.join(time * 1000);
      } catch (InterruptedException e) {
        log.error("interrupted while waiting for the plugin to finish", e);
      }
    }
  }
  
  /**
   * Forwards the result of the plugin-thread's {@link Thread#isAlive()}-method.
   * If the thread is null the method returns false.
   * @return Whether the plugin-thread is alive or not.
   */
  protected boolean isAlive() {
    return (_thread != null) ? _thread.isAlive() : false;
  }
  
  /**
   * Can be called if a plugin is not responding. It kill the plugin-thread's
   * by calling the {@link Thread#stop()}-method, which is deprecated. This
   * method should only be called to kill a plugin which is not responding!
   */
  @SuppressWarnings("deprecation")
  protected void kill() {
    _thread.stop();
  }
  
  
  
  /**
   * Adds an observer to the set of plugin observers.
   * @param observer The observer to add.
   * @return Result of the {@link Set#add()}-method.
   */
  public final boolean addObserver(PluginObserver observer) {
    return _observer.add(observer);
  }
  
  /**
   * Removes an observer from the set of plugin observers.
   * @param observer The observer to remove.
   * @return Result of the {@link Set#remove()}-method.
   */
  public final boolean removeObserver(PluginObserver observer) {
    return _observer.remove(observer);
  }
  
  /**
   * Notifies the {@link Plugin#observer} according to the current value of
   * the {@link Plugin#state}-object.
   */
  private final void notifyObserver() {
    switch (_state) {
      case CREATED:   _observer.forEach(observer ->
                        observer.onPluginCreated(_config.getInstanceName())
                      );
        break;
      case STARTED:   _observer.forEach(observer ->
                        observer.onPluginStarted(_config.getInstanceName())
                      );
        break;
      case SLEEPING:  _observer.forEach(observer ->
                        observer.onPluginGoesSleeping(_config.getInstanceName())
                      );
        break;
      case RUNNING:   _observer.forEach(observer ->
                        observer.onPluginIsRunning(_config.getInstanceName())
                      );
        break;
      case RELOADING: _observer.forEach(observer ->
                        observer.onPluginIsReloading(_config.getInstanceName())
                      );
        break;
      case STOPPED:   _observer.forEach(observer ->
                        observer.onPluginStopped(_config.getInstanceName())
                      );
        break;
      case ERROR:     _observer.forEach(observer ->
                        observer.onPluginError(_config.getInstanceName(), _error)
                      );
        break;
        
      default: break;
    }
  }
  
  
  
  /**
   * This method is called in case an exception occured. It is either called by
   * the plugin-thread exception handler
   * {@link PluginExceptionHandler#uncaughtException()} or - also possible -
   * from the plugin's user-code to signal that some serious process went wrong
   * and the plugin is not able to work properly anymore.
   * This method calls both the plugins {@link Plugin#onPluginError()}- and the
   * observers {@link PluginObserver#onPluginError()}-method and it is ensured
   * that this code is executed within the plugins thread context.
   * @param t The @Throwable instance which contains information about the
   *          occured exception.
   */
  protected final void errorOccured(Throwable t) {
    _state = PluginState.ERROR;
    _error  = t;
    
    log.debug("plugin error occured: " + t.toString());
    
    notifyObserver();
    onPluginError(t);
  }
  
  /**
   * Can be overriden to define user-defined error handling in case an error
   * occured.
   * @param t The @Throwable instance which contains information about the
   *          occured exception.
   */
  protected void onPluginError(Throwable t) {}
  
  /**
   * In case of an exception this method returns the resulting @Throwable
   * instance.
   * @return The @Throwable instance which contains information about the
   *         occured exception or null.
   */
  public final Throwable getError() {
    return _error;
  }
  
  /**
   * Exception-handler for all plugins; catches uncaught exceptions and
   * calls the {@link Plugin#errorOccured()}-method for error-handling.
   */
  class PluginExceptionHandler implements UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
      // executed in plugin-thread-context
      errorOccured(e);
    }
  }
  
  
  
  /**
   * Provides access to the plugin's settings, e.g. the config-file.
   * @return The plugin's config-file.
   */
  public final PluginConfigT config() {
    return _config;
  }
  
  /**
   * @return The current @PluginState of the plugin.
   */
  public final PluginState state() {
    return _state;
  }
  
}
