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

import java.net.MalformedURLException;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import ffw.alertsystem.core.ApplicationConfig;
import ffw.alertsystem.core.ApplicationLogger;
import ffw.alertsystem.util.JettyLogger;



/**
 * The ReceiverServer opens a websocket @ReceiverWebSocket which is accessible
 * via 'wss://{IP or URL}/receiver/'. As a result of the fact that the receiver
 * only accepts secure connections (wss), the listener needs the same
 * certificate as the receiver.
 */
public final class ReceiverServer {
  
  private static ReceiverServer instance = null;
  
  public final static String receiverURL = "/receiver/";
  
  
  
  private ApplicationConfig config;
  
  private ApplicationLogger log;
  
  private JettyLogger jettyLog;
  
  private Server server;
  
  
  
  /**
   * Constructor is private -> creation only in create()-method possible.
   */
  private ReceiverServer(ApplicationConfig config,
                         ApplicationLogger log) {
    this.config = config;
    this.log    = log;
    
    // disable jetty-internal-logger and set own logger instead
    jettyLog = new JettyLogger("receiver");
    Log.setLog(jettyLog);
  }
  
  /**
   * @return @ReceiverServer reference.
   */
  public static ReceiverServer create(ApplicationConfig config,
                                      ApplicationLogger log) {
    if (instance == null) {
      instance = new ReceiverServer(config, log);
    }
    
    return instance;
  }
  
  
  
  /**
   * Creates the websocket and starts the jetty-server.
   */
  protected void start() {
    server = new Server();
    server.addConnector(createSSLConnector());
    server.setHandler(createContextHandler());
    
    try {
      server.start();
      
      log.info(
        "receiver-server started (" +
        "port "    + Integer.parseInt(config.getParam("server-port")) + ", " +
        "timeout " + Integer.parseInt(config.getParam("websocket-timeout")) + "min)",
        true
      );
    } catch (Exception e) {
      log.error("could not start receiver-server", e, true);
    }
  }
  
  /**
   * Stops the jetty-server.
   */
  protected void stop() {
    if (server != null) {
      try {
        server.stop();
        log.info("receiver-server stopped", true);
      } catch (Exception e) {
        log.error("could not stop receiver-server", e, true);
      }
    }
  }
  
  
  
  private ServerConnector createSSLConnector() {
    // create SSL context
    SslContextFactory sslCtx = new SslContextFactory();
    
    try {
      sslCtx.setKeyStoreResource(
        Resource.newResource(config.getParam("server-keystore"))
      );
    } catch (MalformedURLException e) {
      log.error("could not load keystore", e);
    }
    
    sslCtx.setKeyStorePassword  (config.getParam("server-keystore-password"));
    sslCtx.setKeyManagerPassword(config.getParam("server-keymanager-password"));
    
    // create SSL and HTTP connection
    SslConnectionFactory sslCon = new SslConnectionFactory(
                                    sslCtx, HttpVersion.HTTP_1_1.asString()
                                  );
    HttpConnectionFactory httpCon = new HttpConnectionFactory(
                                      new HttpConfiguration()
                                    );
    
    // create the connector and set the server-port
    ServerConnector connector = new ServerConnector(server, sslCon, httpCon);
    connector.setPort(Integer.parseInt(config.getParam("server-port")));
    
    //connector.getIdleTimeout();
    //connector.getStopTimeout();
    
    return connector;
  }
  
  private ContextHandler createContextHandler() {
    // this timeout applies only for active websocket-connections
    int timeout = Integer.parseInt(config.getParam("websocket-timeout"));
    ReceiverWebSocket.timeout   = timeout;
    ReceiverWebSocket.log       = jettyLog;
    ReceiverWebSocket.appLogger = log;
    
    ContextHandler wsContextHandler = new ContextHandler();
    wsContextHandler.setContextPath(receiverURL);
    wsContextHandler.setHandler(new WebSocketHandler() {
      @Override
      public void configure(WebSocketServletFactory factory) {
        factory.register(ReceiverWebSocket.class);
      }
    });
    
    return wsContextHandler;
  }
  
}
