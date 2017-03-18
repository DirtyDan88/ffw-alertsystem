/*
  Copyright (c) 2015-2017, Max Stark <max.stark88@web.de>
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

package net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.websockets;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

import net.dirtydan.ffw.alertsystem.common.util.Logger;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.jetty.JettyServer;


public abstract class WebSocketBase implements WebSocketListener {
  
  protected final Logger log = JettyServer.jettyLogger;
  
  private static final int timeout = 10;
  
  private Session _session;
  
  
  @Override
  public final void onWebSocketConnect(Session session) {
    _session = session;
    _session.setIdleTimeout(1000 * 60 * timeout);
    
    log.info(getName() + ": connected to " +
             _session.getRemoteAddress().getAddress(), true);
    
    onConnect();
  }
  
  protected void onConnect() {}
  
  @Override
  public final void onWebSocketBinary(byte[] payload, int offset, int len) {}
  
  @Override
  public  final void onWebSocketText(String text) {
    if (text != null) {
      log.info(getName() + ": received message '" + text + "' from " +
               _session.getRemoteAddress(), true);
      onText(text);
    } else {
      log.warn(getName() + ": received null-message from " + _session.getRemoteAddress(), true);
    }
  }
  
  protected void onText(String message) {}
  
  protected void sendMessage(String message) {
    try {
      _session.getRemote().sendString(message);
    } catch (IOException e) {
      log.error(getName() + ": could not sent string: " + message, e, true);
      onError(e);
    }
  }
  
  @Override
  public final void onWebSocketClose(int statusCode, String reason) {
    onClose();
    log.info(getName() + ": disconnected from " +
             _session.getRemoteAddress().getAddress() + ", closed with code: " +
             statusCode + " (" + reason + ")", true);
  }
  
  protected void onClose() {}
  
  @Override
  public final void onWebSocketError(Throwable t) {
    log.error(getName() + ": error (" +
              _session.getRemoteAddress().getAddress() + ")", t, true);
    onError(t);
  }
  
  protected void onError(Throwable t) {}
  
  protected abstract String getName();
  
}
