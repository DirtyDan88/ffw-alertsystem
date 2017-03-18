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

package net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.dirtydan.ffw.alertsystem.common.util.Logger;
import net.dirtydan.ffw.alertsystem.monitor.action.RemoteActionController;
import net.dirtydan.ffw.alertsystem.monitor.plugin.WebInterface;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.jetty.JettyServer;


@Path("action")
public class RESTControllerActions {
  
  private final Logger log = JettyServer.jettyLogger;
  
  private final RemoteActionController actionCtrl = WebInterface.instance.actionCtrl;
  
  
  @GET
  @Path("activate/{action-name}")
  public Response activate(
    @Context HttpServletRequest request,
    @PathParam("action-name") String actionName)
  {
    log.info(request.getRemoteAddr() + " wants to activate remote-action " +
             actionName, true);
    actionCtrl.activate(actionName);
    return Response.status(Status.OK).build();
  }
  
  @GET
  @Path("deactivate/{action-name}")
  public Response deactivate(
    @Context HttpServletRequest request,
    @PathParam("action-name") String actionName)
  {
    log.info(request.getRemoteAddr() + " wants to deactivate remote-action " +
             actionName, true);
    actionCtrl.deactivate(actionName);
    return Response.status(Status.OK).build();
  }
  
  @GET
  @Path("reconnect/{action-name}")
  public Response reconnect(
    @Context HttpServletRequest request,
    @PathParam("action-name") String actionName)
  {
    log.info(request.getRemoteAddr() + " wants to reconnect remote-action " +
             actionName, true);
    actionCtrl.connect(actionName);
    return Response.status(Status.OK).build();
  }
  
  @GET
  @Path("disconnect/{action-name}")
  public Response disconnect(
    @Context HttpServletRequest request,
    @PathParam("action-name") String actionName)
  {
    log.info(request.getRemoteAddr() + " wants to disconnect remote-action " +
             actionName, true);
    actionCtrl.disconnect(actionName);
    return Response.status(Status.OK).build();
  }
  
}
