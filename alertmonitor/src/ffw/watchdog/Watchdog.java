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

package ffw.watchdog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import ffw.util.DateAndTime;
import ffw.util.Mail;
import ffw.util.TwilioSMS;
import ffw.util.TwilioSMS.TwilioAccount;
import ffw.util.config.ConfigReader;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class Watchdog implements Runnable {
  private boolean stopped = false;
  private DatagramSocket socket;
  private int timeoutsInARow = 0;
  
  @Override
  public void run() {
    int port    = Integer.parseInt(ConfigReader.getConfigVar("watchdog-port", 
                                   Application.WATCHDOG));
    int timeout = Integer.parseInt(ConfigReader.getConfigVar("watchdog-timeout", 
                                   Application.WATCHDOG));
    
    try {
      this.socket = new DatagramSocket(port);
      this.socket.setSoTimeout(1000 * 60 * timeout);
      
    } catch (SocketException e) {
      ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                            Application.WATCHDOG);
    }
    
    ApplicationLogger.log("watchdog started (timeout: " + timeout + " min)", 
                          Application.WATCHDOG);
    
    while (!this.stopped) {
      DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
      
      try {
        /* wait for watchdog-reset */
        this.socket.receive(packet);
        
        int    length = packet.getLength();
        byte[] data   = packet.getData();
        ApplicationLogger.log("watchdog says: " + new String(data, 0, length), 
                              Application.WATCHDOG);
        timeoutsInARow = 0;
        
      } catch (SocketTimeoutException e) {
        ApplicationLogger.log("watchdog says: oh nooo!", 
                              Application.WATCHDOG);
        timeoutsInARow++;
        
        try {
          String eMailRecipients = ConfigReader.getConfigVar(
                                     "watchdog-email-recipients", 
                                     Application.WATCHDOG
                                   );
          if (!eMailRecipients.isEmpty()) {
            sendMail(eMailRecipients);
          }
        } catch (Exception ex) {
          ApplicationLogger.log("ERROR: Was not able to send eMail: " +
                                ex.getMessage() + "\n" + e.getStackTrace(),
                                Application.WATCHDOG);
        }
        
        try {
          int smsThreshold = Integer.parseInt(ConfigReader.getConfigVar(
                                                "watchdog-sms-threshold", 
                                                Application.WATCHDOG)
                                             );
          if (timeoutsInARow > smsThreshold) {
            String SMSRecipients = ConfigReader.getConfigVar(
                                     "watchdog-sms-recipients", 
                                     Application.WATCHDOG
                                   );
            if (!SMSRecipients.isEmpty()) {
              sendSMS(SMSRecipients);
            }
            
          }
        } catch (Exception ex) {
          ApplicationLogger.log("ERROR: Was not able to send SMS: " +
                                ex.getMessage() + "\n" + e.getStackTrace(),
                                Application.WATCHDOG);
        }
        
        /*
        boolean reboot = Boolean.valueOf(ConfigReader.getConfigVar(
                            "watchdog-reboot", Application.WATCHDOG));
        if (reboot) {
          ApplicationLogger.log("reboot", Application.WATCHDOG);
          ShellScript.execute("reboot");
        }
        */
          
      } catch (IOException e) {
        ApplicationLogger.log(e.getMessage(), Application.WATCHDOG);
      }
    }
  }
  
  private void sendMail(String recipients) {
    String userName = ConfigReader.getConfigVar("email-address");
    String passWord = ConfigReader.getConfigVar("email-password");
    
    String subject  = "[ffw-alertsystem] !! watchdog timeout !!";
    String text     = getTimeoutText();
    
    Mail.send(userName, passWord, recipients, subject, text, 
              Application.WATCHDOG);
    ApplicationLogger.log("send mail to: " + recipients, 
                          Application.WATCHDOG, false);
  }
  
  private void sendSMS(String recipients) {
    String text = getTimeoutText();
    
    String[] fileNames = recipients.split(",");
    for (String fileName : fileNames) {
      if (!fileName.trim().isEmpty()) {
        TwilioAccount acc = TwilioSMS.getTwilioAccFromTextFile(fileName);
        TwilioSMS.send(acc, text);
      }
    }
  }
  
  private String getTimeoutText() {
    String text = "Watchdog timeout at " + DateAndTime.get() + "\n";
    try {
      text += "Watchdog on host: " + InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                            Application.WATCHDOG);
    }
    
    return text;
  }
  
  public synchronized void stop() {
    this.socket.close();
    this.stopped = true;
  }
  
  
  
  public static void main(String[] args) {
    if (args.length > 0) {
      if (args[0].equals("-logInFile")) {
        ApplicationLogger.inFile = true;
      }
    }
    
    Watchdog watchdog     = new Watchdog();
    Thread watchdogThread = Thread.currentThread();
    
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        watchdog.stop();
        try {
          watchdogThread.join();
        } catch (InterruptedException e) {
          ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                Application.WATCHDOG);
        }
        
        ApplicationLogger.log("watchdog stopped", Application.WATCHDOG);
      }
    });
    
    watchdog.run();
  }
}