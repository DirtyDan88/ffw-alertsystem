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

package net.dirtydan.ffw.alertsystem.common.application;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import net.dirtydan.ffw.alertsystem.common.util.Logger;
import net.dirtydan.ffw.alertsystem.common.util.XMLFile;



/**
 * Provides access to the xml-config-file of the corresponding application.<br>
 * @see @XMLFile
 */
public class ApplicationConfig extends XMLFile {
  
  private final Logger log = Logger.getApplicationLogger();
  
  /**
   * Initiates the application's config-file.
   * @param xsdFileName The name of the schema-file for the config-file
   * @param xmlFileName The path+name of the application's config-file
   */
  public ApplicationConfig(String xsdFileName, String xmlFileName) {
    super(xsdFileName, xmlFileName);
  }
  
  /**
   * Reads the config-file and returns the desired parameter or null if the
   * parameter does not exist.
   * @param paramName The name of the parameter
   * @return          The value of the parameter or null if the parameter does
   *                  not exist
   */
  public final String getParam(String paramName) {
    Document config = this.open();
    if (config != null) {
      NodeList nodes = config.getElementsByTagName(paramName);
      if (nodes.getLength() > 0) {
        return nodes.item(0).getTextContent();
      }
    }
    
    log.warn("could not get param '" + paramName + "' in config-file", true);
    return null;
  }
  
}
