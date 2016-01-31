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

package ffw.alertsystem.core.alertaction;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ffw.alertsystem.core.plugin.XMLPluginSource;
import ffw.alertsystem.util.Logger;



/**
 * XML-based config-source for @AlertAction objects. Creates and
 * delivers @AlertActionConfig objects.
 * @see Base-class:   @XMLPluginSource
 * @see Config-class: @AlertActionConfig
 */
public class AlertActionConfigFile extends XMLPluginSource<AlertActionConfig> {
  
  /**
   * Has no functional relevance, just for informational purpose so its possible
   * to determine the @AlertActionManager of this config-source.
   */
  private final String executerName;
  
  
  
  /**
   * Forwards XSD- and XML-file-name to base-class @XMLPluginSource.
   */
  public AlertActionConfigFile(String xsdFileName, String xmlFileName,
                               String executerName, Logger log) {
    super(xsdFileName, xmlFileName, log);
    this.executerName = executerName;
  }
  
  
  
  @Override
  protected void extendPluginConfig(AlertActionConfig config, Element xml) {
    ArrayList<String> ricList = new ArrayList<>();
    NodeList ricNodes = xml.getElementsByTagName("ric");
    
    for (int j = 0; j < ricNodes.getLength(); j++) {
      Element ric = (Element) ricNodes.item(j);
      ricList.add(ric.getTextContent());
    }
    
    config.setRicList(ricList);
    config.setExecuterName(executerName);
  }
  
  @Override
  protected String XMLPluginNodeName() {
    return "alert-action";
  }
  
  @Override
  protected AlertActionConfig newInstance() {
    return new AlertActionConfig();
  }
  
}