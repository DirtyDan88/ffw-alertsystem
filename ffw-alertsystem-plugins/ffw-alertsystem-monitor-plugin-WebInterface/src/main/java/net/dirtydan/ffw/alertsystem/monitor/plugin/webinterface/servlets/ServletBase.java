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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.dirtydan.ffw.alertsystem.common.util.Logger;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.jetty.JettyServer;


public abstract class ServletBase extends HttpServlet {
  
  private static final long serialVersionUID = 1L;
  
  protected final Logger log = JettyServer.jettyLogger;
  
  
  @Override
  protected final void doGet(HttpServletRequest  request,
                             HttpServletResponse response)
                               throws ServletException, IOException
  {
    log.info("GET request: " + request.getRequestURI() +
             " [" + request.getRemoteAddr() + "]", true);
    _GET_Request(request, response);
  }
  
  protected void _GET_Request(HttpServletRequest  request,
                     HttpServletResponse response) throws IOException {}
  
  
//  @Override
//  protected final void doPost(HttpServletRequest  request,
//                              HttpServletResponse response)
//                               throws ServletException, IOException
//  {
//    log.info("POST request: " + request.getRequestURI() +
//             " [" + request.getRemoteAddr() + "]", true);
//    _POST_Request(request, response);
//  }
  
  
  protected void returnResource(String resource, HttpServletResponse response)
                                 throws IOException {
    BufferedInputStream  is = null;
    BufferedOutputStream os = null;
    
    try {
      is = new BufferedInputStream(
             ServletImage.class.getResourceAsStream(resource)
           );
      os = new BufferedOutputStream(
             response.getOutputStream()
           );
      
      byte[] buf = new byte[1024];
      int count = 0;
      while ((count = is.read(buf)) >= 0) {
        os.write(buf, 0, count);
      }
      
    } catch (IOException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      log.error("could not load resource: " + resource, e);
      
    } finally {
      if (os != null) try { os.close(); } catch (IOException e) {}
      if (is != null) try { is.close(); } catch (IOException e) {}
    }
    
  }
  
}
