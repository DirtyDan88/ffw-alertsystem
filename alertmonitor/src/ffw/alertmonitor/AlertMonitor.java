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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import ffw.alertlistener.AlertMessage;
import ffw.util.ConfigReader;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.MessageLogger;
import ffw.util.logging.ApplicationLogger.Application;
import ffw.util.logging.MessageLogger.LogEvent;



public class AlertMonitor implements Runnable {
  private boolean stopped = false;
  
  private Queue<AlertMessage> messageQueue = null;
  private List<String>        alertNumbers = null;
  
  
  
  public AlertMonitor(Queue<AlertMessage> messageQueue) {
    this.messageQueue = messageQueue;
    this.alertNumbers = new ArrayList<String>();
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
    String[] watchdogRICs = ConfigReader.getConfigVar("watchdog-rics").split(",");
    String[] alertRICs    = ConfigReader.getConfigVar("alert-rics").split(",");
    
    alertMessage.evaluateMessageHead();
    String msgRIC = alertMessage.getAddress();
    
    if (msgRIC != null) {
      for (String curRIC : watchdogRICs) {
        if (msgRIC.equals(curRIC) || curRIC.equals("*")) {
          MessageLogger.log(alertMessage.getMessageString(), LogEvent.WATCHDOG);
          resetWatchdog();
          break;
        }
      }
      
      for (String curRIC : alertRICs) {
        if (msgRIC.equals(curRIC) || curRIC.equals("*")) {
          MessageLogger.log(alertMessage.getMessageString(), LogEvent.ALERT);
          executeAlertActions(alertMessage);
          break;
        }
      }
    }
    
    // TODO: Write alertnumber + message in file for statistical evaluation
  }
    
  private void executeAlertActions(AlertMessage alertMessage) {
    alertMessage.evaluateMessage();
    
    /* prevent multiple alerting by checking the alertnumber */
    if (alertNumbers.contains(alertMessage.getAlertNumber())) {
      // TODO: check also if message string are equal
      ApplicationLogger.log("## multiple alerting detected, no actions " + 
                            "will be executed", 
                            Application.ALERTMONITOR);
    } //else {
      alertNumbers.add(alertMessage.getAlertNumber());
      AlertActionManager.executeActions(alertMessage);
    //}
  }
    
  private void resetWatchdog() {
    int port          = Integer.parseInt(ConfigReader.getConfigVar("watchdog-port"));
    String addressStr = ConfigReader.getConfigVar("watchdog-addr");
    byte[] buf        = "I am alive!".getBytes();
    
    ApplicationLogger.log("## reset watchdog on: " + addressStr + ":" + port,
                          Application.ALERTMONITOR);
    
    try {
      InetAddress address   = InetAddress.getByName(addressStr);
      DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
      DatagramSocket socket = new DatagramSocket();
      
      if (addressStr.equals("255.255.255.255")) {
        socket.setBroadcast(true);
      }
      socket.send(packet);
      socket.close();
        
    } catch (IOException e) {
      ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                            Application.ALERTMONITOR);
    }
  }
}