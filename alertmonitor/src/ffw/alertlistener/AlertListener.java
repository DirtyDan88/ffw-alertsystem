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

package ffw.alertlistener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Queue;

import ffw.util.ConfigReader;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class AlertListener implements Runnable {
  private boolean stopped = false;
  
  private int            port;
  private DatagramSocket socket;
  private StringBuilder  buffer;
  private Queue<AlertMessage> messageQueue = null;
  
  
  
  public AlertListener(Queue<AlertMessage> messageQueue) {
    this.messageQueue = messageQueue;
    this.buffer       = new StringBuilder();
    
    this.port = Integer.parseInt(ConfigReader.getConfigVar("pocsag-port"));
    
    try {
      this.socket = new DatagramSocket(this.port);
      this.socket.setSoTimeout(10);
      
    } catch (SocketException e) {
      ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                            Application.ALERTLISTENER);
    }
  }
  
  @Override
  public void run() {
    ApplicationLogger.log(">> listener is listening (port: " + port + ")", 
                          Application.ALERTLISTENER);
    
    while (!this.stopped) {
      /* listen for alerts */
      DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
      try {
        this.socket.receive(packet);
      } catch (SocketTimeoutException e) {
        continue;
      } catch (IOException e) {
        ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                              Application.ALERTLISTENER);
      }
      
      /* create new message from recieved string */
      int    length  = packet.getLength();
      byte[] data    = packet.getData();
      String recvStr = new String(data, 0, length);
      recvStr        = recvStr.replaceAll("[\n\r]", "#");
      
      this.buffer.append(recvStr);
      ApplicationLogger.log(">> received data, buffer is:    '" + buffer + "'", 
                            Application.ALERTLISTENER);
      
      this.checkMessageComplete();
    }
    
    this.socket.close();
    ApplicationLogger.log(">> listener stopped (port: " + port + ")",
                          Application.ALERTLISTENER);
  }
  
  public synchronized void stop() {
    this.stopped = true;
  }
  
  
  
  private void checkMessageComplete() {
    while (buffer.indexOf("#") != -1) {
      int start = buffer.indexOf("POCSAG1200:");
      int end   = buffer.indexOf("#");
      
      if (start == -1) {
        buffer.setLength(0);
        
      } else if (start > end) {
        buffer.delete(0, end+1);
          
      } else if (start < end) {
        String message = buffer.substring(start, end);
        this.messageQueue.offer(AlertMessageFactory.create(message));
        
        buffer.delete(0, end+1);
        ApplicationLogger.log(">> message complete, " +
                              "buffer is: '" + buffer + "'", 
                              Application.ALERTLISTENER, false);
      }
    }
  }
}
