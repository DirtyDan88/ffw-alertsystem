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

package net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.jetty;

import org.eclipse.jetty.security.Authenticator;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import net.dirtydan.ffw.alertsystem.common.plugin.PluginConfig;
import net.dirtydan.ffw.alertsystem.common.util.Logger;
import net.dirtydan.ffw.alertsystem.monitor.plugin.WebInterface;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.controller.*;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.servlets.*;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.websockets.*;


public final class JettyServer {
  
  private static JettyServer instance = null;
  
  public static JettyLogger jettyLogger;
  
  public static JettyServer create(WebInterface plugin, Logger log) {
    if (instance == null) {
      instance = new JettyServer(plugin, log);
      
      int pluginLogLevel = plugin.config().getLogLevel();
      boolean inFile = (pluginLogLevel == Logger.DEBUG) ? false : true;
      if (inFile) log.info("log-output will be written to jetty-log-file");
      
      // disable jetty-internal-logger and set own logger
      jettyLogger = new JettyLogger("web-interface", pluginLogLevel, inFile);
      Log.setLog(jettyLogger);
      
    }
    
    return instance;
  }
  
  
  private final Logger log;
  
  private final PluginConfig config;
  
  private Server server;
  
  private JettyServer(WebInterface plugin, Logger log) {
    this.log    = log;
    this.config = plugin.config();
  }
  
  
  public void start() {
    int port = Integer.parseInt(config.paramList().get("server-port").val());
    server = new Server(port);
    server.setHandler(createSecurityHandler());
    
    try {
      server.start();
      log.info("web-interface started (port " + port + ")");
    } catch (Exception e) {
      log.error("could not start web-interface", e);
    }
  }
  
  public void stop() {
    if (server != null) {
      try {
        server.stop();
        jettyLogger.stop();
        server.join();
        log.info("web-interface stopped");
      } catch (Exception e) {
        log.error("could not stop web-interface", e);
      } finally {
        if (server.isStopped()) server.destroy();
      }
    }
  }
  
  
  private ConstraintSecurityHandler createSecurityHandler() {
    // securityHandler is responsible for authentication
    ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
    securityHandler.addConstraintMapping(getConstraintMapping());
    securityHandler.setAuthenticator    (getAuthenticator());
    securityHandler.setLoginService     (getLoginService());
    
    // add the content-handlers to the securityHandler
    ContextHandlerCollection handler = new ContextHandlerCollection();
    handler.addHandler(getServletContextHandler());
    handler.addHandler(getWebSocketHandler());
//    handler.addHandler(getRequestLogHandler());
    securityHandler.setHandler(handler);
    
    return securityHandler;
  }
  
  private LoginService getLoginService() {
    HashLoginService loginService = new HashLoginService();
    
    String userName = config.paramList().get("user-name").val();
    String passWord = config.paramList().get("user-password").val();
    loginService.putUser(
                   userName,               // user name
                   new Password(passWord), // password for user
                   new String[] {"admin"}  // user roles
                 );
    
    return loginService;
  }
  
  private ConstraintMapping getConstraintMapping() {
    Constraint authConstraint = new Constraint();
    authConstraint.setName(Constraint.__BASIC_AUTH);
    authConstraint.setRoles(new String[] {"admin"});
    authConstraint.setAuthenticate(true);
    
    ConstraintMapping mapping = new ConstraintMapping();
    mapping.setConstraint(authConstraint);
    mapping.setPathSpec("/*");
    
    return mapping;
  }
  
  private Authenticator getAuthenticator() {
    return new BasicAuthenticator();
  }
  
  private ServletContextHandler getServletContextHandler() {
    ServletContextHandler servletHandler = new ServletContextHandler(
                                             ServletContextHandler.SESSIONS |
                                             ServletContextHandler.SECURITY
                                           );
    // Classic servlets
    // TODO ResourceHandler?
    servletHandler.addServlet(ServletIndex.class,  "/");
    servletHandler.addServlet(ServletImage.class,  "/images/*");
    servletHandler.addServlet(ServletImage.class,  "/favicon.ico");
    servletHandler.addServlet(ServletScript.class, "/js/*");
    servletHandler.addServlet(ServletStyle.class,  "/css/*");
    
    // REST endpoints
    ServletHolder holder = new ServletHolder(new ServletContainer(
            new ResourceConfig()
              .register(RESTControllerPlugins.class)
              .register(RESTControllerActions.class)
              .register(RESTControllerMessage.class)
        ));
    servletHandler.addServlet(holder, "/api/*");
    
    return servletHandler;
  }
  
  private ContextHandlerCollection getWebSocketHandler() {
//    ServletContextHandler socketHandler = new ServletContextHandler(
//                                            ServletContextHandler.SESSIONS |
//                                            ServletContextHandler.SECURITY
//                                          );
    ContextHandlerCollection socketHandler = new ContextHandlerCollection();
    
    ContextHandler wshMessage = new ContextHandler();
    wshMessage.setContextPath("/sockets/messages/");
    wshMessage.setHandler(new WebSocketHandler() {
      @Override
      public void configure(WebSocketServletFactory factory) {
        factory.register(WebSocketMessage.class);
      }
    });
    
    ContextHandler wshPlugins = new ContextHandler();
    wshPlugins.setContextPath("/sockets/plugins/");
    wshPlugins.setHandler(new WebSocketHandler() {
      @Override
      public void configure(WebSocketServletFactory factory) {
        factory.register(WebSocketPlugins.class);
      }
    });
    
    ContextHandler wshActions = new ContextHandler();
    wshActions.setContextPath("/sockets/actions/");
    wshActions.setHandler(new WebSocketHandler() {
      @Override
      public void configure(WebSocketServletFactory factory) {
        factory.register(WebSocketActions.class);
      }
    });
    
    ContextHandler wshHWInfo = new ContextHandler();
    wshHWInfo.setContextPath("/sockets/hwinfo/");
    wshHWInfo.setHandler(new WebSocketHandler() {
      @Override
      public void configure(WebSocketServletFactory factory) {
        factory.register(WebSocketHWInfo.class);
      }
    });
    
    socketHandler.addHandler(wshMessage);
    socketHandler.addHandler(wshPlugins);
    socketHandler.addHandler(wshActions);
    socketHandler.addHandler(wshHWInfo);
    
    return socketHandler;
  }
  
//  private Handler getRequestLogHandler() {
//    RequestLogHandler logHandler = new RequestLogHandler();
//    RequestLog requestLogger = new RequestLog() {
//      @Override
//      public void log(Request request, Response response) {
//        System.out.println("HERE");
//        log.info("request: " + request.getMethod() + " " + request.getContextPath());
//      }
//    };
//    
//    logHandler.setRequestLog(requestLogger);
//    return logHandler;
//  }
  
}
