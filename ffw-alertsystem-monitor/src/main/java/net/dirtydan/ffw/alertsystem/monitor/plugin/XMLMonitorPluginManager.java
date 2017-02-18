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

package net.dirtydan.ffw.alertsystem.monitor.plugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import net.dirtydan.ffw.alertsystem.common.message.Message;
import net.dirtydan.ffw.alertsystem.common.plugin.PluginManager;



/**
 * A @PluginManager which creates and controls @MonitorPlugin objects by using
 * a xml-plugin-config-file. The plugin-code is loaded from external jar-files.
 */
public class XMLMonitorPluginManager extends PluginManager<MonitorPlugin,
                                                           MonitorPluginConfig> {
  
  /**
   * Creates the XML-based config-source @MonitorPluginConfigFile for the
   * monitor-plugins.
   * @see @XMLPluginSource
   */
  public XMLMonitorPluginManager(String xmlFileName) {
    super(new XMLMonitorPluginConfigFile(xmlFileName));
  }
  
  
  
  /**
   * Notifies all loaded plugins that a new message was received.
   * @param message The received @Message object.
   */
  public void receivedMessage(Message message) {
    plugins().forEach(plugin -> plugin.notifyReceivedMessage(message));
  }
  
  /**
   * Notifies all loaded plugins that the state of the monitor has changed (e.g.
   * new plugin was loaded, plugin-error occurred etc.)
   */
  public void notifyMonitorObserver() {
    plugins().forEach(plugin -> plugin.notifyMonitorObserver());
  }
  
  
  
  @Override
  protected MonitorPlugin newInstance(MonitorPluginConfig config) {
    MonitorPlugin plugin = null;
    
    try {
      URL jarFile = new URL("jar", "", "file:" + config.getJarFile() + "!/");
      ClassLoader loader = URLClassLoader.newInstance(
                             new URL[] { jarFile },
                             getClass().getClassLoader()
                           );
      try {
        plugin = (MonitorPlugin) loader.loadClass(
                                          config.getPackageName() + "." +
                                          config.getClassName()
                                        ).newInstance();
        
      } catch (InstantiationException | IllegalAccessException |
               ClassNotFoundException e) {
        log.error("could not create monitor-plugin", e, true);
      } catch (NoClassDefFoundError e) {
        log.error("could not create monitor-plugin, maybe some dependencies " +
                  "are missing?", e, true);
      }
      
    } catch (MalformedURLException e) {
      log.error("could not find jar-file of monitor-plugin " +
                config.getInstanceName() + "(" + config.getJarFile() + ")",
                e, true);
    }
    
    return plugin;
  }
  
  @Override
  protected String pluginTypeName() {
    return "MonitorPlugin";
  }
  
}
