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

package net.dirtydan.ffw.alertsystem.monitor.plugin;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.dirtydan.ffw.alertsystem.common.plugin.PluginConfig;
import net.dirtydan.ffw.alertsystem.common.util.XMLPluginSource;
import net.dirtydan.ffw.alertsystem.monitor.plugin.MonitorPluginConfig.Builder;



/**
 * XML-based config-source for @MonitorPlugin objects. Creates and
 * provides @MonitorPluginConfig objects.
 * @see Base-class:   @XMLPluginSource
 * @see Config-class: @MonitorPluginConfig
 */
public class XMLMonitorPluginConfigFile extends XMLPluginSource<MonitorPluginConfig> {
  
  /**
   * Path to the xsd-schema-file which describes MonitorPlugins.
   */
  private static final String schemaFile = "schema-monitor-plugin.xsd";
  
  /**
   * Forwards XSD- and XML-file-name to base-class @XMLPluginSource.
   */
  public XMLMonitorPluginConfigFile(String xmlFileName) {
    super(schemaFile, xmlFileName);
  }
  
  @Override
  protected MonitorPluginConfig newInstance(PluginConfig basicConfig, Element xml) {
    Builder builder = new Builder();
    builder.withJarFile(basicConfig.getJarFile())
           .withPackageName(basicConfig.getPackageName())
           .withClassName(basicConfig.getClassName())
           .withInstanceName(basicConfig.getInstanceName())
           .withIsActive(basicConfig.isActive())
           .withDescription(basicConfig.getDescription())
           .withParamList(basicConfig.paramList())
           .withLogLevel(basicConfig.getLogLevel())
           .withLastModifiedTime(basicConfig.getLastModifiedTime());
    
    // optional, default is empty list
    ArrayList<String> ricList = new ArrayList<>();
    NodeList ricNodes = xml.getElementsByTagName("ric");
    for (int j = 0; j < ricNodes.getLength(); j++) {
      Element ric = (Element) ricNodes.item(j);
      ricList.add(ric.getTextContent());
    }
    builder.withRicList(ricList);
    
    // the following properties are optional; the builder-method will be called
    // only if the value is present
    NodeList node;
    
    node = xml.getElementsByTagName("use-invalid-messages");
    if (node.getLength() != 0) {
      String value = node.item(0).getTextContent();
      builder.withUseInvalidMessages((value.equals("true")) ? true : false);
    }
    node = xml.getElementsByTagName("use-message-copies");
    if (node.getLength() != 0) {
      String value = node.item(0).getTextContent();
      builder.withUseMessageCopies((value.equals("true")) ? true : false);
    }
    node = xml.getElementsByTagName("message-history");
    if (node.getLength() != 0) {
      String value = node.item(0).getTextContent();
      builder.withMessageHistory(Integer.parseInt(value));
    }
    node = xml.getElementsByTagName("monitor-observer");
    if (node.getLength() != 0) {
      String value = node.item(0).getTextContent();
      builder.withIsMonitorObserver((value.equals("true")) ? true : false);
    }
    
    return builder.build();
  }
  
  @Override
  protected String XMLPluginNodeName() {
    return "monitor-plugin";
  }
  
}