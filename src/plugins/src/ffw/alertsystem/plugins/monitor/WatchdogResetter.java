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

package ffw.alertsystem.plugins.monitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import ffw.alertsystem.core.message.Message;
import ffw.alertsystem.core.monitor.MonitorPlugin;



public class WatchdogResetter extends MonitorPlugin {
  
  @Override
  protected void onReceivedMessage(Message message) {
    int port       = Integer.parseInt(config().paramList().get("watchdog-port"));
    String addrStr =                  config().paramList().get("watchdog-addr");
    byte[] buf     = "I am alive!".getBytes();
    
    try {
      InetAddress address   = InetAddress.getByName(addrStr);
      DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
      DatagramSocket socket = new DatagramSocket();
      
      if (addrStr.equals("255.255.255.255")) {
        socket.setBroadcast(true);
      }
      socket.send(packet);
      socket.close();
      
      log.info("reseted watchdog on: " + addrStr + ":" + port);
        
    } catch (IOException e) {
      log.error("watchdog could not sent alive-message", e);
    }
  }
  
}
