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

package ffw.alertmonitor.actions;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import ffw.alertmonitor.AlertAction;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class WatchdogResetter extends AlertAction {
  
  @Override
  public String getInfo() {
    return "sends a network message to reset the watchdogs";
  }
  
  @Override
  public void run() {
    int port          = Integer.parseInt(paramList.get("watchdog-port"));
    String addressStr =                  paramList.get("watchdog-addr");
    byte[] buf        = "I am alive!".getBytes();
    
    ApplicationLogger.log("## reset watchdog on: " + addressStr + ":" + port,
                          Application.ALERTMONITOR, false);
    
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
                            Application.ALERTMONITOR, false);
    }
  }
}
