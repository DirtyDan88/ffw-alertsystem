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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ffw.alertsystem.core.message.Message;
import ffw.alertsystem.core.plugin.Plugin;



/**
 * Abstract base-class for monitor-plugins which extends the @Plugin-class by
 * the possibility to receive and process @Message-objects and also to act as
 * an @MessageMonitor-observer.
 */
public abstract class MonitorPlugin extends Plugin<MonitorPluginConfig> {
  
  /**
   * Queue with not-yet-processed messages. Is filled with new messages from
   * the @MessageMonitor via {@link MonitorPlugin#notifyReceivedMessage()}.
   */
  private Queue<Message> messageQueue;
  
  /**
   * List with previously received and already processed messages (message-
   * history). Length of the list is determined through the config-file, see
   * implementation and creation in {@link MonitorPlugin#onPluginStart()}.
   */
  private List<Message>  prevMessages;
  
  /**
   * This interface provides access to the @MessageMonitor and enables the
   * plugin to communicate with the monitor. Each plugin belongs to exactly one
   * monitor (n:1 relation).
   */
  protected MonitorInterface monitor;
  
  /**
   * Plugins which are monitor-observers won't be notified by a direct method-
   * call, instead this flag will be set and the actual notification happens
   * then in the thread of the plugin (resp. {@link MonitorPlugin#onRun()}).
   */
  private boolean notifyMonitorObserver = false;
  
  
  
  @Override
  protected final void onPluginStart() {
    messageQueue = new ConcurrentLinkedQueue<>();
    // List with max <config().messageHistory()> entries, a new entry will
    // remove the oldest one
    prevMessages = new LinkedList<Message>() {
      private static final long serialVersionUID = 1L;
      
      @Override
      public boolean add(Message message) {
        if (config().messageHistory() <= 0) {
          return false;
        }
        
        if (size() >= config().messageHistory()) {
          super.removeFirst();
        }
        
        return super.add(message);
      }
    };
    
    onMonitorPluginStart();
  }
  
  protected void onMonitorPluginStart() {}
  
  
  
  @Override
  protected final void onPluginReload() {
    onMonitorPluginReload();
  }
  
  protected void onMonitorPluginReload() {}
  
  
  
  /**
   * Inserts a @Message into the queue of the monitor-plugin and wakes it up.
   * @param message The message to store in the plugins message-queue.
   */
  protected final void notifyReceivedMessage(Message message) {
    // only wake up if the plugin is not stopped
    if (state() != PluginState.STOPPED) {
      messageQueue.offer(message);
      log.debug("inserted message into plugin-queue");
      wakeUp();
    }
  }
  
  /**
   * If the plugin is a monitor-observer, this method sets the
   * {@link MonitorPlugin#notifyMonitorObserver()} flag and wakes the plugin
   * which leads into a call of
   * {@link MonitorPlugin#onMonitorObserverNotification()}.
   */
  protected final void notifyMonitorObserver() {
    if (config().isMonitorObserver()) {
      // only wake up if the plugin is not stopped
      if (state() != PluginState.STOPPED) {
        notifyMonitorObserver = true;
        wakeUp();
      }
    }
  }
  
  
  
  /**
   * Is called when the plugin was woken up and there is either a new message or
   * a monitor-observer-notification. Is executed within the plugin's thread-
   * context. This method does the check for invalid messages and message copies
   * (multiple alerting) and also the RIC-check.
   */
  @Override
  protected final void onRun() {
    // process all messages before go back sleeping
    Message message;
    while ((message = messageQueue.poll()) != null) {
      
      // check the ric list, '*' is wildcard
      if (config().ricList().contains("*") ||
          config().ricList().contains(message.getAddress())) {
      
        boolean alreadyReceived = prevMessages.contains(message);
        // always proceed if message not yet received; if message already
        // received but plugin processes message copies, proceed as well
        if (!alreadyReceived ||
           (alreadyReceived && config().useMessageCopies())) {
          
          // always proceed if message is valid; if message is not valid but
          // plugin processes invalid messages, proceed as well
          boolean isValid = message.isValid();
          if (isValid || (!isValid && config().useInvalidMessages())) {
            log.debug("plugin consumes message");
            onReceivedMessage(message);
            
          } else {
            log.debug("plugin ignores message (invalid message)");
          }
        } else {
          log.info("plugin ignores message (message copy)");
        }
      } else {
        log.debug("plugin ignores message (not in ric-list)");
      }
      
      prevMessages.add(message);
    }
    
    // notify observer if desired
    if (notifyMonitorObserver) {
      log.debug("plugin is monitor-observer and will be notified");
      onMonitorObserverNotification();
    }
    notifyMonitorObserver = false;
  }
  
  /**
   * If a monitor-plugin is supposed to process @Message it needs to override
   * this method. But since the method is not abstract, its not mandatory to
   * implement this.
   */
  protected void onReceivedMessage(Message message) {}
  
  /**
   * If a monitor-plugin wants to receive notifications when there is a change
   * or event in the @MessageMonitor it needs to override this method. But since
   * the method is not abstract, its not mandatory to implement this.
   */
  protected void onMonitorObserverNotification() {}
  
  
  
  @Override
  protected final void onPluginStop() {
    onMonitorPluginStop();
  }
  
  protected void onMonitorPluginStop() {}
  
  
  
  /**
   * @return The list with the previously received and already processed
   * messages (= message-history).
   */
  public final List<Message> prevMessages() {
    return Collections.unmodifiableList(prevMessages);
  }
  
  /**
   * @return The monitor-interface which provides access to the @MessageMonitor
   * and enables the plugin to communicate with the monitor.
   */
  public final MonitorInterface monitor() {
    return monitor;
  }
  
}
