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

package net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.servlets;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.dirtydan.ffw.alertsystem.monitor.plugin.WebInterface;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.jetty.HtmlTemplate;


public final class ServletIndex extends ServletBase {
  
  private static final long serialVersionUID = 1L;
  
  
  @Override
  protected void _GET_Request(HttpServletRequest  request,
                              HttpServletResponse response)
                                throws IOException
  {
    if (request.getRequestURI().equals("/webinterface")) {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("text/html; charset=utf-8");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(loadIndexSite());
      
    } else {
      log.warn("unknown index-resource, return 404 error");
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }
  
  // TODO: website for mobile-devices
  /*
  if(request.getHeader("User-Agent").indexOf("Mobile") != -1) {
    //you're in mobile land
    //log.info("mobile device");
  } else {
    //nope, this is probably a desktop
    //log.info("desktop device");
  }
  //log.info("USER: " + request.getRemoteUser());
  */
  
  
  private String loadIndexSite() {
    HtmlTemplate template = new HtmlTemplate("/web/index.html");
    template.setElement("#subheadline",
        WebInterface.instance.config().paramList().get("headline").val());
    template.setElement("#remote-action-port",
        "port: " + WebInterface.instance.status.getRemoteActionPort());
    
    return template.toString();
  }
  
}
