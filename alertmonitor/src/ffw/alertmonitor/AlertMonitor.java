/*
  Copyright (c) 2015, Max Stark <max.stark88@web.de> 
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

package ffw.alertmonitor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ffw.alertlistener.AlertMessage;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.MessageLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class AlertMonitor implements Runnable {
  private boolean stopped = false;
  
  private Queue<AlertMessage> messageQueue = null;
  private Queue<AlertMessage> prevMessages = null;
  private List<String>        alertNumbers = null;
  
  
  
  public AlertMonitor(Queue<AlertMessage> messageQueue) {
    this.messageQueue = messageQueue;
    this.alertNumbers = new ArrayList<String>();
    
    /* Queue with max 20 entries, a new entry will remove the oldest one */
    this.prevMessages = new LinkedList<AlertMessage>() {
      private static final long serialVersionUID = 1L;
      
      @Override
      public boolean add(AlertMessage alertMessage) {
        if (this.size() >= 20) {
          super.removeFirst();
        }
        
        return super.add(alertMessage);
      }
    };
  }
  
  @Override
  public void run() {
    ApplicationLogger.log("## monitor is waiting for messages", 
                          Application.ALERTMONITOR);
    
    while (!this.stopped) {
      /* check if there are new messages */
      AlertMessage message = this.messageQueue.poll();
      if (message != null) {
        this.handleMessage(message);
      } else {
        /* wait a little bit */
        try {
          Thread.sleep(10);
        } catch (Exception e) {
          ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                Application.ALERTMONITOR);
        }
      }
    }
    
    ApplicationLogger.log("## monitor stopped", 
                          Application.ALERTMONITOR);
  }
  
  public synchronized void stop() {
    this.stopped = true;
  }
  
  
  
  private void handleMessage(AlertMessage alertMessage) {
    MessageLogger.log(alertMessage.getMessageString());
    alertMessage.evaluateMessageHead();
    
    if (!prevMessages.contains(alertMessage)) {
      if (alertMessage.evaluateMessage()) {
        /* prevent multiple alerting by checking the alertnumber */
        if (alertNumbers.contains(alertMessage.getAlertNumber())) {
          ApplicationLogger.log("## multiple alerting with different " + 
                                "message-strings detected", 
                                Application.ALERTMONITOR, false);
          // TODO: some actions could benefit from an other message string, 
          //       for example the HtmlBuilder from coordinates
          //       possibility to re-execute actions? 
        } else {
          alertNumbers.add(alertMessage.getAlertNumber());
          AlertActionManager.executeActions(alertMessage);
        }
      } else {
        // TODO: trigger sqlite action also for encrypted messages
        ApplicationLogger.log("## Alertmessage is either encrypted or empty", 
                              Application.ALERTMONITOR, false);
      }
    } else {
      ApplicationLogger.log("## message already received",
                            Application.ALERTMONITOR, false);
    }
    
    prevMessages.add(alertMessage);
  }
}