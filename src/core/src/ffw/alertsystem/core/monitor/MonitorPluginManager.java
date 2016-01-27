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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import ffw.alertsystem.core.message.Message;
import ffw.alertsystem.core.plugin.PluginManager;
import ffw.alertsystem.util.Logger;



/**
 * A @PluginManager which creates and controls @MonitorPlugin objects.
 */
public class MonitorPluginManager extends PluginManager<MonitorPlugin,
                                                        MonitorPluginConfig> {
  
  /**
   * The monitor which holds the plugin-manager. This object reference is passed
   * to the plugins and enables them to communicate with the monitor via the
   * methods defined in @MonitorInterface.
   */
  private final MonitorInterface monitor;
  
  
  
  /**
   * Creates the XML-based config-source @MonitorPluginConfigFile for the
   * monitor-plugins.
   * 
   * @see @XMLPluginSource
   */
  public MonitorPluginManager(String xsdFileName, String xmlFileName,
                              MonitorInterface monitor, Logger log) {
    super(new MonitorPluginConfigFile(xsdFileName, xmlFileName, log), log);
    
    this.monitor  = monitor;
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
    MonitorPlugin plugin;
    
    try {
      URL jarFile = new URL("jar", "", "file:" + "" + config.getJarFile() + "!/");
      ClassLoader loader = URLClassLoader.newInstance(
                             new URL[] { jarFile },
                             getClass().getClassLoader()
                           );
      
      plugin = (MonitorPlugin) loader.loadClass(
                                        config.getPackageName() + "." + 
                                        config.getClassName()
                                      ).newInstance();
      //plugin.setMonitor(monitor);
      plugin.monitor = monitor;
      
    } catch (MalformedURLException | IllegalAccessException |
             InstantiationException | ClassNotFoundException e) {
      log.error("could not create monitor-plugin", e);
      return null;
      
    } catch (NoClassDefFoundError e) {
      log.error("could not create monitor-plugin, maybe some dependencies " +
                "are missing?", e);
      return null;
    }
    
    return plugin;
  }
  
  @Override
  protected String pluginTypeName() {
    return "MonitorPlugin";
  }
  
}
