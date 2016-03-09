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

package ffw.alertsystem.core.receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import ffw.alertsystem.core.Application;
import ffw.alertsystem.core.ApplicationConfig;
import ffw.alertsystem.core.ApplicationLogger;



/**
 * Heart of the ffw-alertsystem-receiver application. Waits for new incoming
 * strings from the antenna (dataflow is: antenna -> receiver-script -> local
 * network broadcast), slot together (if necessay) the string fragments and
 * forwards complete messages to the message-publisher.
 */
public class MessageReceiver implements Runnable {
  
  private final ApplicationLogger log;
  
  private final ApplicationConfig config;
  
  private final MessagePublisher publisher;
  
  private StringBuilder  buffer;
  
  private int            port;
  
  private DatagramSocket socket;
  
  private boolean stopped = false;
  
  
  
  /**
   * Constructor just sets local members, initialization of all network stuff is
   * done in {@link MessageReceiver#init()}.
   * @param app The @Application object which provides logger, config and
   *            error-handling.
   * @param publisher The message-publisher to forward complete messages for
   *                  distribution.
   */
  public MessageReceiver(Application app, MessagePublisher publisher) {
    this.log      = app.log;
    this.config   = app.config;
    this.publisher = publisher;
  }
  
  
  
  /**
   * Runs the ffw-alertsystem-receiver application-loop, which is waiting for
   * new messages from the local network. Buffers incoming string-fragments
   * and checks if we have a new complete message.
   */
  @Override
  public void run() {
    init();
    log.info("local-socket is listening on port " + port);
    
    while (!stopped) {
      // waiting for messages
      DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
      try {
        socket.receive(packet);
      } catch (SocketTimeoutException e) {
        continue;
      } catch (IOException e) {
        log.error("exception while listener was waiting for messages", e);
      }
      
      // create new message from recieved string
      int    length  = packet.getLength();
      byte[] data    = packet.getData();
      String recvStr = new String(data, 0, length);
      recvStr        = recvStr.replaceAll("[\n\r]", "#");
      
      buffer.append(recvStr);
      log.debug("received data, buffer is:    '" + buffer + "'");
      
      checkMessageComplete();
    }
    
    socket.close();
    log.info("local-socket closed (port: " + port + ")");
  }
  
  /**
   * Setup the local network interface for receiving messages.
   */
  private void init() {
    buffer = new StringBuilder();
    port   = Integer.parseInt(config.getParam("receiver-port"));
    
    try {
      socket = new DatagramSocket(port);
      socket.setSoTimeout(10);
    } catch (SocketException e) {
      log.error("could not create local-socket on port " + port, e, true);
    }
  }
  
  /**
   * Checks if the {@link MessageReceiver#buffer} contains complete messages,
   * and if so, forwards them to the @MessagePublisher which distributes the
   * message.
   */
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
        
        publisher.newMessage(message);
        
        buffer.delete(0, end+1);
        log.debug("message delivered and buffer refreshed, is now: " +
                  "'" + buffer + "'");
      }
    }
  }
  
  /**
   * Stops the ffw-alertsystem-receiver application.
   */
  public final synchronized void stop() {
    stopped = true;
    log.info("receiver stopped", true);
  }
  
}
