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

package net.dirtydan.ffw.alertsystem.common.util;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.dirtydan.ffw.alertsystem.common.plugin.PluginConfig;
import net.dirtydan.ffw.alertsystem.common.plugin.PluginConfig.Builder;
import net.dirtydan.ffw.alertsystem.common.plugin.PluginConfig.PluginParam;
import net.dirtydan.ffw.alertsystem.common.plugin.PluginConfigSource;



/**
 * Implements a @PluginConfigSource based on a XML-file. Is capable of creating
 * and delivering @PluginConfig objects (resp. sub-class objects).
 */
public abstract class XMLPluginSource<PluginConfigT extends PluginConfig>
                      implements PluginConfigSource<PluginConfigT> {
  
  /**
   * The name of the XML-file which contains plugin definitions.
   */
  private final XMLFile _xmlFile;
  
  /**
   * Last time the XML-file was read (e.g. checked for new plugins).
   */
  private long _lastReadTime = -1;
  
  
  
  /**
   * Constructor creates @XMLFile object.
   * @param xsdFileName The XSD-schema-file of the config-file. The schema-file
   *                    should fit to the content of the @PluginConfig; of
   *                    course this also applies to all sub-classes of this
   *                    class resp. of the @PluginConfig-class.
   * @param xmlFileName The name of the XML-file with the plugin descriptions.
   */
  public XMLPluginSource(String xsdFileName, String xmlFileName) {
    _xmlFile = new XMLFile(xsdFileName, xmlFileName);
  }
  
  
  
  /**************************************************************************
   ***            Interface implementation: @PluginConfigSource           ***
   **************************************************************************/
  
  @Override
  public boolean hasChanged() {
    return !(_lastReadTime == _xmlFile.getLastModifiedTime());
  }
  
  @Override
  public List<PluginConfigT> getPluginConfigs(boolean setLastReadTime) {
    LinkedList<PluginConfigT> configList = new LinkedList<>();
    
    Document configFile = _xmlFile.open();
    if (setLastReadTime) _lastReadTime = _xmlFile.getLastModifiedTime();
    
    if (configFile != null) {
      NodeList pluginNodes = configFile.getElementsByTagName(XMLPluginNodeName());
      for (int i = 0; i < pluginNodes.getLength(); i++) {
        Element pluginXML = (Element) pluginNodes.item(i);
        configList.add(
          newInstance(createBasicConfig(pluginXML), pluginXML)
        );
      }
    }
    
    return configList;
  }
  
  @Override
  public void activatePlugin(String instanceName) {
    write(instanceName, "active", "true");
  }
  
  @Override
  public void deactivatePlugin(String instanceName) {
    write(instanceName, "active", "false");
  }
  
  
  
  /**
   * The plugin-config-source is not able to know at compile-time, which kind of
   * plugin-configs it is supposed to deliver, hence the creation of the object
   * has to be done in the concrete implementation of this class.
   * @param basicConfig Contains the basic information about a plugin, defined
   *                    in the @PluginConfig class.
   * @param xml         The XML-tree of the plugin-config.
   * 
   * @return A new instance of the plugin-config-class this config-source shall
   *         provide.
   */
  protected abstract PluginConfigT newInstance(PluginConfig basicConfig, Element xml);
  
  /**
   * @return The XML-node-name of a plugin in the XML-plugin-config-file.
   */
  protected abstract String XMLPluginNodeName();
  
  
  
  /**
   * Sets the basic properties of a @Plugin, defined in the base-class of all
   * plugin-configs: @PluginConfig.
   * @param xml The XML-tree of the plugin-config.
   * @return A @PluginConfig object based on the XML input.
   */
  private PluginConfig createBasicConfig(Element xml) {
    String instanceName = xml.getAttribute("instanceName");
    String jarFileName  = xml.getElementsByTagName("jarfile")
                                .item(0).getTextContent();
    String packageName  = xml.getElementsByTagName("packageName")
                                .item(0).getTextContent();
    String className    = xml.getElementsByTagName("className")
                                .item(0).getTextContent();
    String isActive     = xml.getElementsByTagName("active")
                                .item(0).getTextContent();
    
    // the parameter-list of the plugin is optional, default value is an empty
    // HashMap
    Map<String, PluginParam> paramList = new HashMap<>();
    NodeList paramNodes = xml.getElementsByTagName("param");
    for (int j = 0; j < paramNodes.getLength(); ++j) {
      Element param = (Element) paramNodes.item(j);
      paramList.put(
                  param.getAttribute("name"),
                  new PluginParam(
                    param.getTextContent(),
                    param.getAttribute("hide").equals("true") ? true : false
                  )
                );
    }
    
    Builder builder = new Builder()
        .withJarFile(jarFileName)
        .withPackageName(packageName)
        .withClassName(className)
        .withInstanceName(instanceName)
        .withIsActive((isActive.equals("true")) ? true : false)
        .withParamList(paramList)
        .withLastModifiedTime(new File(jarFileName).lastModified());
    
    // the following properties are optional; the builder-method will be called
    // only if the value is present
    NodeList node;
    
    node = xml.getElementsByTagName("description");
    if (node.getLength() != 0) {
      builder.withDescription(node.item(0).getTextContent());
    }
    // log-level is optional
    node = xml.getElementsByTagName("log-level");
    if (node.getLength() != 0) {
      int logLevel = Integer.parseInt(node.item(0).getTextContent());
      builder.withLogLevel(logLevel);
    }
    
    return builder.build();
  }
  
  /**
   * Writes the value of a given XML-property (e.g. XML-tag) of a given plugin.
   * @param instanceName The unique instance-name of the plugin.
   * @param nodeName     The XML-node-name of the value to write.
   * @param value        The new value to write.
   */
  private void write(String instanceName, String nodeName, String value) {
    Document configFile = _xmlFile.open();
    
    NodeList pluginNodes = configFile.getElementsByTagName(XMLPluginNodeName());
    for (int i = 0; i < pluginNodes.getLength(); i++) {
      Element pluginXML = (Element) pluginNodes.item(i);
      
      if (pluginXML.getAttribute("instanceName").equals(instanceName)) {
        NodeList node = pluginXML.getElementsByTagName("active");
        if (node.getLength() != 0) {
          node.item(0).setTextContent(value);
          _xmlFile.write(configFile);
        }
        break;
      }
    }
  }
  
}
