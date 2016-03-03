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

package ffw.alertsystem.core.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ffw.alertsystem.core.Application;
import ffw.alertsystem.core.ApplicationLogger;
import ffw.alertsystem.core.alertaction.AlertAction;
import ffw.alertsystem.core.alertaction.AlertActionConfig;
import ffw.alertsystem.core.alertaction.AlertActionManager;
import ffw.alertsystem.core.message.Message;
import ffw.alertsystem.core.message.MessageFactory;
import ffw.alertsystem.core.monitor.MonitorPluginManager;
import ffw.alertsystem.core.plugin.PluginObserver;



/**
 * This class is the heart of the ffw-alertsystem-monitor application. The
 * member {@link MessageMonitor#pluginManager} holds and manages all monitor-
 * plugins. The implementation of the @MonitorInterface allows plugins to insert
 * messages into the monitors message-queue from where the messages will be
 * forwarded to all plugins (depending on their configuration).<br>
 * 
 * @see @Message
 * @see @MonitorInterface
 * @see @PluginObserver
 */
public class MessageMonitor implements Runnable,
                                       MonitorInterface,
                                       PluginObserver {
  
  private final ApplicationLogger log;
  
  private final Application       app;
  
  private final Queue<Message> messageQueue;
  
  private final MonitorPluginManager pluginManager;
  
  private final List<AlertActionManager> actionManagers;
  
  private boolean stopped = false;
  
  
  
  /**
   * Initiates the ffw-alertsystem-monitor, creates the @MonitorPluginManager
   * and adds itself as an observer for all plugins.
   * @param app The @Application object which provides logger, config and
   *            error-handling. The logger and hence its settings will also be
   *            used for all monitor-plugins.
   */
  public MessageMonitor(Application app) {
    this.app = app;
    this.log = app.log;
    
    messageQueue  = new ConcurrentLinkedQueue<>();
    pluginManager = new MonitorPluginManager("src/core/schema-monitor-plugin.xsd",
                                             app.config.getParam("plugin-config"),
                                             new MonitorProxy(this), log);
    pluginManager.addPluginObserver(this);
    actionManagers = new ArrayList<>();
  }
  
  
  
  /**
   * Runs the ffw-alertsystem-monitor application-loop, which briefly does
   * following: <br>
   * - checks if there are new or changed plugins (not everytime, only after
   *   100 loops which takes ~1s (100*10ms delay in every loop). <br>
   * - checks if there are messages in the message-queue and treats them all 
   *   before going to sleep. <br>
   * - Sleeps for a short time (10ms) and repeat the process after that. <br>
   * The loop and hence the ffw-alertsystem-monitor application can be stopped
   * by calling the {@link MessageMonitor#stop()}-method.
   */
  @Override
  public void run() {
    log.info("monitor is waiting for messages", true);
    
    Message message;
    int i = 0;
    
    while (!stopped) {
      // look if there are new plugins (~ 100*10ms = 1s)
      if (i == 100) {
        updatePluginManager();
        i = 0;
      }
      
      // check if there are new messages
      while ((message = messageQueue.poll()) != null) {
        handleMessage(message);
      }
      
      // wait a little bit
      try {
        Thread.sleep(10);
        ++i;
      } catch (Exception e) {
        log.error("exception while monitor is waiting for messages", e);
      }
    }
  }
  
  /**
   * Stops the ffw-alertsystem-monitor application and all running plugins.
   */
  public final void stop() {
    stopped = true;
    
    log.info("try to stop all plugins", true);
    pluginManager.stopAll();
    pluginManager.removePluginObserver(this);
    
    log.info("monitor stopped", true);
  }
  
  /**
   * Called from the @MonitorApplication in case of an unexpected exception.
   * @param t The occured error.
   */
  public void monitorErrorOccured(Throwable t) {
    // TODO: FEATURE: Notify webinterface
  }
  
  /**
   * Look into the plugin-config and the action-config(s) if there are new
   * definied or changed plugins.
   */
  private void updatePluginManager() {
    pluginManager.loadAll();
    pluginManager.startAll();
    
    actionManagers.forEach(actions -> actions.loadAll());
  }
  
  /**
   * Evaluates a received @Message and distributes it to all monitor-plugins.
   * @param message The message to handle.
   */
  private void handleMessage(Message message) {
    message.evaluateMessageHead();
    message.evaluateMessage();
    
    String validity = "";
    if (message.isValid()) {
      validity = "valid";
    } else {
      validity = "not valid";
    }
    
    log.info("new message (" + validity + "), notify plugins", true);
    pluginManager.receivedMessage(message);
  }
  
  
  
  /**************************************************************************
   ***            Interface implementation: @MonitorInterface             ***
   **************************************************************************/
  
  @Override
  public synchronized void insertMessage(Message message) {
    if (message != null) {
      //log.debug("inserted message into monitor-queue");
      messageQueue.offer(message);
    }
  }
  
  @Override
  public synchronized void insertMessage(String messageString) {
    insertMessage(MessageFactory.create(messageString, log));
  }
  
  
  
  @Override
  public void restartMonitorPlugin(String instanceName) {
    log.info("request to restart plugin: " + instanceName, true);
    pluginManager.restart(instanceName);
  }
  
  @Override
  public void activateMonitorPlugin(String instanceName) {
    log.debug("request to activate plugin: " + instanceName, true);
    pluginManager.activatePlugin(instanceName);
  }
  
  @Override
  public void deactivateMonitorPlugin(String instanceName) {
    log.debug("request to deactivate plugin: " + instanceName, true);
    pluginManager.deactivatePlugin(instanceName);
  }
  
  
  
  @Override
  public List<MonitorPlugin> getMonitorPlugins() {
    return pluginManager.plugins();
  }
  
  @Override
  public List<MonitorPluginConfig> getMonitorPluginConfigs() {
    return pluginManager.pluginConfigs();
  }
  
  
  
  @Override
  public void addAlertActionManager(AlertActionManager m) {
    if (m != null) {
      actionManagers.add(m);
      m.addPluginObserver(this);
    }
  }
  
  @Override
  public void removeAlertActionManager(AlertActionManager m) {
    if (m != null) {
      actionManagers.remove(m);
      m.removePluginObserver(this);
    }
  }
  
  @Override
  public List<AlertAction> getAlertActions() {
    List<AlertAction> actions = new ArrayList<>();
    for (AlertActionManager m : actionManagers) {
      actions.addAll(m.plugins());
    }
    
    return actions;
  }
  
  @Override
  public List<AlertActionConfig> getAlertActionConfigs() {
    List<AlertActionConfig> configs = new ArrayList<>();
    for (AlertActionManager m : actionManagers) {
      configs.addAll(m.pluginConfigs());
    }
    
    return configs;
  }
  
  
  
  /**************************************************************************
   ***            Interface implementation: @PluginObserver               ***
   **************************************************************************/
  
  @Override
  public synchronized void onPluginInitialized(String instanceName) {
    //log.debug("plugin was initialized: " + instanceName, true);
    pluginManager.notifyMonitorObserver();
  }
  
  @Override
  public synchronized void onPluginStarted(String instanceName) {
    log.info("plugin was started: " + instanceName, true);
    pluginManager.notifyMonitorObserver();
  }
  
  @Override
  public synchronized void onPluginGoesSleeping(String instanceName) {
    //log.debug("plugin goes sleeping: " + instanceName);
  }
  
  @Override
  public synchronized void onPluginIsRunning(String instanceName) {
    //log.debug("plugin is running: " + instanceName);
    
    /*
    if (!instanceName.equals("WebInterface")) {
      pluginManager.notifyMonitorObserver();
    }
    */
  }
  
  @Override
  public void onPluginIsReloading(String instanceName) {
    log.info("plugin is reloading: " + instanceName, true);
  }
  
  @Override
  public synchronized void onPluginStopped(String instanceName) {
    log.info("plugin was stopped: " + instanceName);
    pluginManager.notifyMonitorObserver();
  }
  
  @Override
  public synchronized void onPluginError(String instanceName, Throwable t) {
    log.error("error in plugin " + instanceName + " occured", t, true);
    app.errHandler.reportError("PluginError '" + instanceName + "'", t);
    
    pluginManager.notifyMonitorObserver();
  }



  
}
