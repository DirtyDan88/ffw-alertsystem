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

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import ffw.alertsystem.core.monitor.MonitorPlugin;
import ffw.alertsystem.core.receiver.ReceiverServer;
import ffw.alertsystem.util.JettyLogger;
import ffw.alertsystem.util.JettyWebSocket;



public class MessageListener extends MonitorPlugin {
  
  private JettyLogger jettyLog;
  private WebSocketClient client;
  private boolean closeIsExpected = false;
  private boolean reconncetIsRunning = false;
  
  
  
  @Override
  protected void onMonitorPluginStart() {
    openConnection();
  }
  
  @Override
  protected void onMonitorPluginReload() {
    closeConnection();
    openConnection();
  }
  
  @Override
  protected void onMonitorPluginStop() {
    closeConnection();
  }
  
  @Override
  protected void onPluginError(Throwable t) {
    String tryToReconnect = config().paramList().get("automatically-reconnect");
    
    if (tryToReconnect.equals("true") && !reconncetIsRunning) {
      log.info("try to reconnect in " +
               config().paramList().get("reconnect-waiting-time") + " min");
      
      // TODO: BUG: reconnect is working, but the plugin-status is not updated
      //       -> web-interface still shows error even the listener is working
      tryReconnect();
    }
  }
  
  
  
  private void openConnection() {
    // disable jetty-internal-logger and set own logger instead
    jettyLog = new JettyLogger("message-listener");
    Log.setLog(jettyLog);
    
    try {
      client = new WebSocketClient(createSSLContext());
      client.start();
      
      Future<Session> f = client.connect(
        new ListenerWebSocket(),
        URI.create(
          "wss://" + config().paramList().get("receiver-uri") +
          ReceiverServer.receiverURL
        )
      );
      
      f.get();
      //Session server = f.get();
      //server.setIdleTimeout();
      
      log.info("web-socket client started (connected to " +
               config().paramList().get("receiver-uri") + ")");
    } catch (Exception e) {
      // web-socket error will be thrown, hence no need to call errorOccured
      // again
      //errorOccured(e);
    }
  }
  
  private void closeConnection() {
    if (client != null) {
      try {
        closeIsExpected = true;
        client.stop();
        closeIsExpected = false;
        log.info("web-socket client stopped");
      } catch (Exception e) {
        log.error("could not stop web-socket client", e);
      }
    }
  }
  
  private void tryReconnect() {
    reconncetIsRunning = true;
    
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      int reconnect = Integer.parseInt(config().paramList().get(
                        "reconnect-waiting-time"
                      )) * 60;
      int counter = 0;
      
      @Override
      public void run() {
        if (state() != PluginState.ERROR) {
          log.info("cancel reconnect-try");
          reconncetIsRunning = false;
          timer.cancel();
        }
        
        if (++counter == reconnect) {
          log.info("try to reconnect to " +
                   config().paramList().get("receiver-uri"), true);
          reconncetIsRunning = false;
          openConnection();
          timer.cancel();
        }
      }
    }, 0, 1000);
  }
  
  private SslContextFactory createSSLContext() {
    SslContextFactory sslCtx = new SslContextFactory();
    
    try {
      sslCtx.setKeyStoreResource(
        Resource.newResource(config().paramList().get("keystore"))
      );
    } catch (MalformedURLException e) {
      log.error("could not load keystore", e);
    }
    
    sslCtx.setKeyStorePassword  (config().paramList().get("keystore-password"));
    sslCtx.setKeyManagerPassword(config().paramList().get("keymanager-password"));
    
    return sslCtx;
  }
  
  @WebSocket
  public class ListenerWebSocket extends JettyWebSocket {
    
    public ListenerWebSocket() {
      log = MessageListener.this.jettyLog;
    }
    
    @Override
    public void onWebSocketMessage(String message) {
      MessageListener.this.log.info(
        "received message from " +
        config().paramList().get("receiver-uri"), true
      );
      
      monitor().insertMessage(message);
      sendString("received message!");
    }
    
    @Override
    public void onWebSocketClose() {
      if (!closeIsExpected) {
        errorOccured(new Exception("unexpected close of listener-web-socket"));
      }
    }
    
    @Override
    public void onWebSocketError(Throwable t) {
      errorOccured(t);
    }
    
  }
  
}
