/*
  Copyright (c) 2015-2016, Max Stark <max.stark88@web.de>
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

package ffw.alertsystem.core.plugin;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashSet;
import java.util.Set;

import ffw.alertsystem.util.Logger;



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
   * The Thread in which context the plugin-code is executed. Will be created
   * and started in the method {@link Plugin#start()}.
   */
  private Thread thread;
  
  /**
   * The plugin's config-file. Private and no setter-method, so nobody except
   * the plugin itself is able to set a new config.
   */
  private PluginConfigT config;
  
  /**
   * The logger for the plugin. The @PluginLogger class acts as a fassade for
   * an instance of the Logger-class.
   */
  protected PluginLogger log;
  
  /**
   * Set with state-observer of the plugin. Observer have to implement the
   * @PluginObserver interface and will be called in the method
   * {@link Plugin#notifyObserver()}.
   */
  private Set<PluginObserver> observer = new HashSet<>();
  
  /**
   * Enum which states the possible states of a plugin.
   */
  public enum PluginState {
    INITIALIZED,
    STOPPED,
    STARTED,
    SLEEPING,
    RUNNING,
    RELOADING,
    ERROR;
    
    @Override
    public String toString() {
      switch(this) {
        case INITIALIZED: return "initialized";
        case STOPPED:     return "stopped";
        case STARTED:     return "started";
        case SLEEPING:    return "sleeping";
        case RUNNING:     return "running";
        case RELOADING:   return "reloading";
        case ERROR:       return "error";
        default: throw new IllegalArgumentException();
      }
    }
  }
  
  /**
   * Field which represents the current state of a plugin.
   */
  private PluginState state;
  
  /**
   * Internal flag which signals the {@link Plugin#run()}-method to raise a
   * reload by calling {@link Plugin#onPluginReload()}.
   */
  private boolean raiseReload = false;
  
  /**
   * If the wakeUp()-method is called and the plugin isn't sleeping but in
   * STARTED/RELOADING/RUNNING state, this flag indicates that the plugin shall
   * not got sleeping and execute run()/reload() again.
   */
  private boolean keepOnRunning = false;
  
  private boolean stopped = true;
  
  /**
   * If an uncaught error occured the resulting @Throwable object will be
   * stored in this field.
   */
  private Throwable error;
  
  
  
  /**
   * Sets the config-file and the logger for the plugin. Is called from
   * @PluginManager when and only when a plugin is newly created.
   * @param config The plugins config-file.
   * @param log    The encapsulated logger for the @PluginLogger class.
   */
  protected final void init(PluginConfigT config, Logger log) {
    this.config = config;
    createPluginLogger(log);
    
    state = PluginState.INITIALIZED;
    notifyObserver();
  }
  
  /**
   * Provides access to the plugin's settings, e.g. the config-file.
   * @return The plugin's config-file.
   */
  public final PluginConfigT config() {
    return config;
  }
  
  /**
   * @return The current @PluginState of the plugin.
   */
  public final PluginState state() {
    return state;
  }
  
  
  
  /**
   * Starts the plugin-thread and sets the exception-handler, see
   * {@link Plugin#getExceptionHandler()}. If the plugin is already running this
   * method has no effect; actually the plugin-state has to be INITIALIZED,
   * STOPPED or ERROR, otherwise the same applies.
   */
  protected final void start() {
    if (state == PluginState.INITIALIZED ||
        state == PluginState.STOPPED ||
        state == PluginState.ERROR) {
      // (re-)set the error-object
      error = null;
      
      thread = new Thread(this);
      thread.setName(config.getInstanceName() + "-thread");
      thread.setUncaughtExceptionHandler(new PluginExceptionHandler());
      thread.start();
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
   * @param newConfig The plugins updated config-file.
   * @param log       The encapsulated logger for the @PluginLogger class.
   *                  (since the log-settings could have changed we are creating
   *                  a new plugin-logger here)
   */
  protected final void reload(PluginConfigT newConfig, Logger log) {
    config = newConfig;
    createPluginLogger(log);
    
    raiseReload = true;
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
    stopped = false;
    
    log.debug("plugin was started");
    state = PluginState.STARTED;
    notifyObserver();
    onPluginStart();
    
    while (!stopped) {
      if (keepOnRunning) {
        keepOnRunning = false;
      } else {
        try {
          state = PluginState.SLEEPING;
          notifyObserver();
          thread.join();
        } catch (InterruptedException e) {
          if (stopped) break;
          log.debug("plugin was woken up");
        }
      }
      
      if (raiseReload) {
        log.info("plugin reload was raised", true);
        state = PluginState.RELOADING;
        notifyObserver();
        onPluginReload();
        raiseReload = false;
      
      } else {
        log.debug("plugin calls run-method");
        state = PluginState.RUNNING;
        notifyObserver();
        onRun();
      }
    }
    
    log.debug("plugin was stopped");
    state = PluginState.STOPPED;
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
    if (state == PluginState.SLEEPING) {
      thread.interrupt();
    } else if (state == PluginState.STARTED ||
               state == PluginState.RUNNING ||
               state == PluginState.RELOADING) {
      keepOnRunning = true;
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
    
    if (state == PluginState.INITIALIZED ||
        state == PluginState.STOPPED ||
        state == PluginState.ERROR) {
      log.debug("... but is not running anyways");
    } else {
      stopped = true;
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
        thread.join(time * 1000);
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
    if (thread != null) {
      return thread.isAlive();
    }
    
    return false;
  }
  
  /**
   * Can be called if a plugin is not responding. It kill the plugin-thread's
   * by calling the {@link Thread#stop()}-method, which is deprecated. This
   * method should only be called to kill a plugin which is not responding!
   */
  @SuppressWarnings("deprecation")
  protected void kill() {
    thread.stop();
  }
  
  
  
  /**
   * Adds an observer to the set of plugin observers.
   * @param o The observer to add.
   * @return Result of the {@link Set#add()}-method.
   */
  public final boolean addObserver(PluginObserver o) {
    return observer.add(o);
  }
  
  /**
   * Removes an observer from the set of plugin observers.
   * @param o The observer to remove.
   * @return Result of the {@link Set#remove()}-method.
   */
  public final boolean removeObserver(PluginObserver o) {
    return observer.remove(o);
  }
  
  /**
   * Notifies the {@link Plugin#observer} according to the current value of
   * the {@link Plugin#state}-object.
   */
  private final void notifyObserver() {
    switch (state) {
      case INITIALIZED: observer.forEach(observer ->
                          observer.onPluginInitialized(config.getInstanceName())
                        );
        break;
      case STARTED:     observer.forEach(observer ->
                          observer.onPluginStarted(config.getInstanceName())
                         );
        break;
      case SLEEPING:    observer.forEach(observer ->
                          observer.onPluginGoesSleeping(config.getInstanceName())
                        );  
        break;
      case RUNNING:     observer.forEach(observer ->
                          observer.onPluginIsRunning(config.getInstanceName())
                        );
        break;
      case RELOADING:   observer.forEach(observer ->
                          observer.onPluginIsReloading(config.getInstanceName())
                        );
        break;
      case STOPPED:     observer.forEach(observer ->
                          observer.onPluginStopped(config.getInstanceName())
                        );
        break;
      case ERROR:       observer.forEach(observer ->
                          observer.onPluginError(config.getInstanceName(), error)
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
    error  = t;
    state = PluginState.ERROR;
    
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
    return error;
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
   * Creates the @PluginLogger, either with DEBUG-level if plugin is in debug-
   * mode or with level of encapsulated Logger.
   */
  private final void createPluginLogger(Logger encapsulatedLogger) {
    if (config.isInDebugMode()) {
      log = new PluginLogger(encapsulatedLogger, config.getInstanceName(),
                             Logger.DEBUG);
      log.debug("plugin is in debug mode");
    } else {
      log = new PluginLogger(encapsulatedLogger, config.getInstanceName(),
                             encapsulatedLogger.getLogLevel());
    }
  }
  
}
