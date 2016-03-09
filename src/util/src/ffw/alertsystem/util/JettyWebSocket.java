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

package ffw.alertsystem.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;



/**
 * Abstract jetty-based websocket; implements some boilercode which is needed in
 * every ffw-alertsystem websocket.
 */
@WebSocket
public abstract class JettyWebSocket {
  
  public    static Logger log;
  public    static int timeout = 10;
  protected        Session session;
  
  
  
  @OnWebSocketConnect
  public void onConnect(Session session) {
    this.session = session;
    session.setIdleTimeout(1000 * 60 * timeout);
    
    log.info("web-socket connected: " + session.getRemoteAddress().getAddress(),
             true);
    onWebSocketConnect();
  }
  
  protected void onWebSocketConnect() {}
  
  
  
  @OnWebSocketMessage
  public void onMessage(String message) {
    if (message != null) {
      log.info("received message '" + message + "' from " +
               session.getRemoteAddress(), true);
      onWebSocketMessage(message);
    } else {
      log.warn("received null-message from " + session.getRemoteAddress(), true);
    }
  }
  
  protected void onWebSocketMessage(String message) {}
  
  protected void sendString(String message) {
    try {
      session.getRemote().sendString(message);
    } catch (IOException e) {
      log.error("web-socket could not sent string: " + message, e, true);
    }
  }
  
  
  
  public static void sendAll(List<JettyWebSocket> sockets, String txt) {
    sendAll(sockets, txt, false);
  }
  
  public static void sendAll(List<JettyWebSocket> sockets, String txt,
                                boolean withLog) {
    List<JettyWebSocket> invalidSockets = new ArrayList<>();
    
    for (int i = 0; i < sockets.size(); ++i) {
      Session s = sockets.get(i).session;
      
      try {
        s.getRemote().sendString(txt);
        if (withLog) {
          log.info("send string '" + txt + "' to " + s.getRemoteAddress(), true);
        }
      } catch (Exception e) {
        invalidSockets.add(sockets.get(i));
      }
    }
    
    for (JettyWebSocket socket : invalidSockets) {
      log.warn("web-socket not available (will remove its session)", true);
      sockets.remove(socket);
    }
  }
  
  
  
  @OnWebSocketClose
  public void onClose(int statusCode, String reason) {
    onWebSocketClose();
    log.info("web-socket closed with code: " + statusCode + " (" + reason + ")",
             true);
  }
  
  protected void onWebSocketClose() {}
  
  
  
  @OnWebSocketError
  public void onError(Throwable t) {
    log.error("web-socket error", t, true);
    onWebSocketError(t);
  }
  
  protected void onWebSocketError(Throwable t) {}
  
}
