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


public class ServletImage extends ServletBase {
  
  private static final long serialVersionUID = 1L;
  
  
  @Override
  protected void _GET_Request(HttpServletRequest  request,
                              HttpServletResponse response)
                                throws IOException
  {
    String image = request.getRequestURI().substring(
                     request.getRequestURI().lastIndexOf("/") + 1
                   );
    if (image.equals("favicon.ico")) image = "fw.png";
    
    // intentional fallthroughs
    switch (image) {
      case "bg.jpg":
      case "fw.png":
      
      case "ws-open.png":
      case "ws-closed.png":
      
      case "btn-power.png":
      case "btn-restart.png":
      case "state-active.png":
      case "state-inactive.png":
      case "state-connected.png":
      case "state-error.png":
      case "state-error-critical":
      
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Type", "images");
        returnResource("/web/images/" + image, response);
        break;
      
      default:
        log.warn("unknown image-resource, return 404 error");
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }
  
}
