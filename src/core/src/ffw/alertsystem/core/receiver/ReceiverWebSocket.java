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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import ffw.alertsystem.util.JettyWebSocket;
import ffw.alertsystem.util.Logger;



/**
 * Jetty-based websocket, stores all connection-requests in static list and
 * distributes new messages to connected listeners.
 */
@WebSocket
public class ReceiverWebSocket extends JettyWebSocket {
  
  public  static Logger appLogger;
  
  private static final List<JettyWebSocket> cons = new ArrayList<>();
  
  
  
  public static void sendAll(String message) {
    appLogger.info("new message, notify all connected listeners", true);
    sendAll(cons, message, true);
  }
  
  
  
  @Override
  protected void onWebSocketConnect() {
    cons.add(this);
    appLogger.info("new listener connected: " + session.getRemoteAddress(), true);
  }
  
  @Override
  protected void onWebSocketMessage(String message) {}
  
  @Override
  protected void onWebSocketClose() {
    cons.remove(this);
    appLogger.info("listener disconnected: " + session.getRemoteAddress(), true);
  }
  
  @Override
  protected void onWebSocketError(Throwable t) {
    cons.remove(this);
  }
  
}
