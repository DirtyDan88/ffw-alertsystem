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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.dirtydan.ffw.alertsystem.common.util.Logger;
import net.dirtydan.ffw.alertsystem.monitor.MessageConsumer;
import net.dirtydan.ffw.alertsystem.monitor.plugin.WebInterface;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.jetty.JettyServer;


@Path("message")
public class RESTControllerMessage {
  
  private final Logger log = JettyServer.jettyLogger;
  
  private final MessageConsumer monitor = WebInterface.instance.monitor;
  
  
  @POST
  @Path("insert")
  public Response activate(
      @Context HttpServletRequest request,
      String message) // contains the request body
  {
    log.info(request.getRemoteAddr() + " wants to insert message " +
             message, true);
    monitor.insertMessage(message);
    return Response.status(Status.OK).build();
  }
  
}
