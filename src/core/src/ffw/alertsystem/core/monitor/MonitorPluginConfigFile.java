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

package ffw.alertsystem.core.monitor;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ffw.alertsystem.core.plugin.XMLPluginSource;
import ffw.alertsystem.util.Logger;



/**
 * XML-based config-source for @MonitorPlugin objects. Creates and
 * delivers @MonitorPluginConfig objects.
 * @see Base-class:   @XMLPluginSource
 * @see Config-class: @MonitorPluginConfig
 */
public class MonitorPluginConfigFile extends XMLPluginSource<MonitorPluginConfig> {
  
  /**
   * Forwards XSD- and XML-file-name to base-class @XMLPluginSource.
   */
  public MonitorPluginConfigFile(String xsdFileName, String xmlFileName,
                                 Logger log) {
    super(xsdFileName, xmlFileName, log);
  }
  
  
  
  @Override
  protected void extendPluginConfig(MonitorPluginConfig config, Element xml) {
    // optional, default is empty list
    ArrayList<String> ricList = new ArrayList<>();
    NodeList ricNodes = xml.getElementsByTagName("ric");
    for (int j = 0; j < ricNodes.getLength(); j++) {
      Element ric = (Element) ricNodes.item(j);
      ricList.add(ric.getTextContent());
    }
    config.setRicList(ricList);
    
    NodeList node;
    
    // optional, default is false
    node = xml.getElementsByTagName("use-invalid-messages");
    if (node.getLength() != 0) {
      String useInvalidMessage = node.item(0).getTextContent();
      config.useInvalidMessages((useInvalidMessage.equals("true")) ? true : false);
    }
    
    // optional, default is false
    node = xml.getElementsByTagName("use-message-copies");
    if (node.getLength() != 0) {
      String useMessageCopies = node.item(0).getTextContent();
      config.useMessageCopies((useMessageCopies.equals("true")) ? true : false);
    }
    
    // optional, default is 20
    node = xml.getElementsByTagName("message-history");
    if (node.getLength() != 0) {
      String messageHistory = node.item(0).getTextContent();
      config.setMessageHistory(Integer.parseInt(messageHistory));
    }
    
    // optional, default is false
    node = xml.getElementsByTagName("monitor-observer");
    if (node.getLength() != 0) {
      String isMonitorObserver = node.item(0).getTextContent();
      config.setMonitorObserver((isMonitorObserver.equals("true")) ? true : false);
    }
  }
  
  @Override
  protected String XMLPluginNodeName() {
    return "monitor-plugin";
  }
  
  @Override
  protected MonitorPluginConfig newInstance() {
    return new MonitorPluginConfig();
  }
  
}