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

package ffw.alertsystem.core;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import ffw.alertsystem.util.Logger;
import ffw.alertsystem.util.XMLFile;



/**
 * Provides access to the xml-config-file of the corresponding application.<br>
 * 
 * @see @XMLFile
 */
public class ApplicationConfig extends XMLFile {
  
  private final Logger log;
  
  
  
  /**
   * Initiates the application's config-file. The xsd-schema for the config is
   * determined through the given application-type.
   * @param app         The application-type.
   * @param xmlFileName The path+name of the application's config-file.
   * @param log         The application logger.
   */
  public ApplicationConfig(Application.ApplicationType app, 
                           String xmlFileName, 
                           Logger log) {
    super(app.configSchemaFile(), xmlFileName, log);
    this.log = log;
  }
  
  /**
   * Reads the config-file and returns the desired parameter. This method causes
   * an error if the parameter is not found or the config-file is not valid 
   * against the xsd-schema-file. If this happens the application is terminated
   * via 'System.exit(1)'.
   * @param paramName The name of the parameter
   * @return          The value of the parameter
   */
  public final String getParam(String paramName) {
    if (isValid()) {
      Document config = open();
      NodeList nodes = config.getElementsByTagName(paramName);
      
      if (nodes.getLength() > 0) {
        return nodes.item(0).getTextContent();
      }
    }
    
    log.error("could not get param '" + paramName + "' in config-file",
              new Exception("critical error in plugin-config, " +
                            "application will be terminated"));
    System.exit(1);
    return "";
  }
  
}
