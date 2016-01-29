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

package ffw.alertsystem.core.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ffw.alertsystem.util.Logger;
import ffw.alertsystem.util.XMLFile;



/**
 * Implements a @PluginConfigSource based on a XML-file. Is capable of creating
 * and delivering @PluginConfig objects (resp. sub-class objects).
 */
public abstract class XMLPluginSource<PluginConfigT extends PluginConfig>
                      implements PluginConfigSource<PluginConfigT> {
  
  /**
   * The name of the XML-file which contains plugin definitions.
   */
  private final XMLFile xmlFile;
  
  /**
   * Last time the XML-file was read (e.g. checked for new plugins).
   */
  private long lastReadTime = -1;
  
  
  
  /**
   * Constructor creates @XMLFile object.
   * @param xsdFileName The XSD-schema-file of the config-file. The schema-file
   *                    should fit to the content of the @PluginConfig; of
   *                    course this also applies to all sub-classes of this
   *                    class resp. of the @PluginConfig-class.
   * @param xmlFileName The name of the XML-file with the plugin descriptions.
   * @param log         @Logger object.
   */
  public XMLPluginSource(String xsdFileName, String xmlFileName, Logger log) {
    xmlFile = new XMLFile(xsdFileName, xmlFileName, log);
  }
  
  
  
  /**************************************************************************
   ***            Interface implementation: @PluginConfigSource           ***
   **************************************************************************/
  
  @Override
  public boolean hasChanged() {
    if (lastReadTime == xmlFile.getLastModifiedTime()) {
      return false;
    }
    
    return true;
  }
  
  @Override
  public List<PluginConfigT> getPluginConfigs(boolean setLastReadTime) {
    LinkedList<PluginConfigT> configList = new LinkedList<>();
    
    if (xmlFile.isValid()) {
      if (setLastReadTime) lastReadTime = xmlFile.getLastModifiedTime();
      Document configFile = xmlFile.open();
      
      if (configFile != null) {
        NodeList pluginNodes = configFile.getElementsByTagName(XMLPluginNodeName());
        for (int i = 0; i < pluginNodes.getLength(); i++) {
          Element pluginXML = (Element) pluginNodes.item(i);
          
          PluginConfigT config = newInstance();
          initPluginConfig  (config, pluginXML);
          extendPluginConfig(config, pluginXML);
          
          configList.add(config);
        }
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
   * Sets the basic properties of a @Plugin, defined in the base-class of all
   * plugin-configs: @PluginConfig.
   * @param config The plugin-config object.
   * @param xml    The XML-tree of the plugin-config.
   */
  private void initPluginConfig(PluginConfigT config, Element xml) {
    String instanceName = xml.getAttribute("instanceName");
    String jarFileName  = xml.getElementsByTagName("jarfile").
                                item(0).getTextContent();
    String packageName  = xml.getElementsByTagName("packageName").
                                item(0).getTextContent();
    String className    = xml.getElementsByTagName("className").
                                item(0).getTextContent();
    String isActive     = xml.getElementsByTagName("active").
                                item(0).getTextContent();
    String description  = xml.getElementsByTagName("description").
                                item(0).getTextContent();
    // the parameter-list of the plugin
    Map<String, String> paramList = new HashMap<>();
    NodeList paramNodes = xml.getElementsByTagName("param");
    for (int j = 0; j < paramNodes.getLength(); j++) {
      Element param = (Element) paramNodes.item(j);
      paramList.put(param.getAttribute("name"), param.getTextContent());
    }
    
    // set the basics, all mandatory
    config.setInstanceName(instanceName);
    config.setJarFile     (jarFileName);
    config.setPackageName (packageName);
    config.setClassName   (className);
    config.setDescription (description);
    config.setActive      ((isActive.equals("true")) ? true : false);
    config.setParamList   (paramList);
    
    // debug-mode is optional, default is false
    NodeList node = xml.getElementsByTagName("debug-mode");
    if (node.getLength() != 0) {
      String debugMode = node.item(0).getTextContent();
      config.setDebugMode((debugMode.equals("true")) ? true : false);
    }
    
    // last modified time of jar-file
    config.setLastModifiedTime(new File(jarFileName).lastModified());
  }
  
  /**
   * This method is supposed to read additional properties defined by sub-
   * classes which extends the PluginConfigT.
   * @param config The plugin-config object.
   * @param xml    The XML-tree of the plugin-config.
   */
  protected abstract void extendPluginConfig(PluginConfigT config, Element xml);
  
  
  
  /**
   * @return The XML-node-name of a plugin in the XML-plugin-config-file.
   */
  protected abstract String XMLPluginNodeName();
  
  /**
   * The plugin-config-source is not able to know at compile-time, which kind of
   * plugin-configs it is supposed to deliver, hence the creation of the object
   * has to be done in the concrete implementation of this class.
   * @return A new instance of the plugin-config-class this config-source shall
   *         provide.
   */
  protected abstract PluginConfigT newInstance();
  
  /**
   * Writes the value of a given XML-property (e.g. XML-tag) of a given plugin.
   * @param instanceName The unique instance-name of the plugin.
   * @param nodeName     The XML-node-name of the value to write.
   * @param value        The new value to write.
   */
  private void write(String instanceName, String nodeName, String value) {
    Document configFile = xmlFile.open();
    
    NodeList pluginNodes = configFile.getElementsByTagName(XMLPluginNodeName());
    for (int i = 0; i < pluginNodes.getLength(); i++) {
      Element pluginXML = (Element) pluginNodes.item(i);
      
      if (pluginXML.getAttribute("instanceName").equals(instanceName)) {
        NodeList node = pluginXML.getElementsByTagName("active");
        if (node.getLength() != 0) {
          node.item(0).setTextContent(value);
          xmlFile.write(configFile);
        }
        break;
      }
    }
  }
  
}
