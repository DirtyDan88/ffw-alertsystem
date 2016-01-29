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

package ffw.alertsystem.watchdog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import ffw.alertsystem.util.DateAndTime;
import ffw.alertsystem.util.Mail;
import ffw.alertsystem.util.TwilioSMS;
import ffw.alertsystem.util.TwilioSMS.TwilioAccount;
import ffw.alertsystem.core.Application;



public class Watchdog extends Application implements Runnable {
  
  private DatagramSocket socket;
  
  private int timeoutsInSuccession = 0;
  
  private Thread watchdogThread;
  
  private boolean stopped = false;
  
  
  
  public static void main(String[] args) {
    new Watchdog(args).start();
  }
  
  
  
  public Watchdog(String[] args) {
    super(Application.ApplicationType.WATCHDOG, args);
  }
  
  @Override
  protected void onApplicationStarted() {
    watchdogThread = new Thread(this);
    
    watchdogThread.setName("watchdog-thread");
    watchdogThread.setUncaughtExceptionHandler(errHandler);
    
    watchdogThread.start();
  }
  
  @Override
  protected void onApplicationStopped() {
    stopped = true;
  }
  
  
  
  @Override
  public void run() {
    int port    = Integer.parseInt(config.getParam("watchdog-port"));
    int timeout = Integer.parseInt(config.getParam("watchdog-timeout"));
    
    try {
      socket = new DatagramSocket(port);
      socket.setSoTimeout(1000 * 60 * timeout);
      
    } catch (SocketException e) {
      log.error("could not create socket", e, true);
      return;
    }
    
    log.info("watchdog started (timeout: " + timeout + " min)", true);
    
    while (!stopped) {
      DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
      
      try {
        // wait for watchdog-reset
        socket.receive(packet);
        
        int    length = packet.getLength();
        byte[] data   = packet.getData();
        log.info("watchdog says: " + new String(data, 0, length), true);
        timeoutsInSuccession = 0;
        
      } catch (SocketTimeoutException e) {
        log.info("watchdog says: oh nooo! (timeouts in succession: " + 
                (++timeoutsInSuccession) + ")", true);
        watchdogTimeout();
        
      } catch (IOException e) {
        log.error("error while waiting for watchdog-reset message", e, true);
      }
    }
    
    log.info("watchdog stopped", true);
    socket.close();
  }
  
  
  
  private void watchdogTimeout() {
    String eMailRecipients = config.getParam("email-watchdog-recipients");
    if (!eMailRecipients.isEmpty()) {
      sendMail(eMailRecipients);
    } else {
      log.warn("no watchdog email-recipients specified in config-file");
    }
    
    int smsThreshold = Integer.parseInt(config.getParam("sms-threshold"));
    if (timeoutsInSuccession > smsThreshold) {
      String SMSRecipients = config.getParam("sms-recipients");
      if (!SMSRecipients.isEmpty()) {
        sendSMS(SMSRecipients);
      } else {
        log.warn("no watchdog SMS-recipients specified in config-file");
      }
    }
    
    /*
    boolean reboot = Boolean.valueOf(ConfigReader.getConfigVar(
                        "watchdog-reboot", Application.WATCHDOG));
    if (reboot) {
      ApplicationLogger.log("reboot", Application.WATCHDOG);
      ShellScript.execute("reboot");
    }
    */
  }
  
  private void sendMail(String recipients) {
    String userName = config.getParam("email-address");
    String passWord = config.getParam("email-password");
    
    String subject  = "[ffw-alertsystem] !! watchdog timeout !!";
    String text     = getTimeoutText();
    
    Mail.send(userName, passWord, recipients, subject, text, log);
    log.info("sent mail to: " + recipients);
  }
  
  private void sendSMS(String recipients) {
    String text = getTimeoutText();
    
    String[] fileNames = recipients.split(",");
    for (String fileName : fileNames) {
      if (!fileName.trim().isEmpty()) {
        TwilioAccount acc = TwilioSMS.getTwilioAccFromTextFile(fileName);
        boolean ok = TwilioSMS.send(acc, text);
        if (ok) {
          log.info("sent SMS to: " + acc.foreName + " " + acc.surName +
                   " (" + acc.To + ")");
        } else {
          log.error("could not send watchdog-SMS to: " + acc.foreName + 
                    " " + acc.surName + " (" + acc.To + ")", new Exception());
        }
      }
    }
  }
  
  private String getTimeoutText() {
    String text = "Watchdog timeout at " + DateAndTime.get() + "\n";
    try {
      text += "Watchdog on host: " + InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      log.error("Could not get host of watchdog", e);
    }
    
    return text;
  }
  
}