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

package net.dirtydan.ffw.alertsystem.common.plugin;

import net.dirtydan.ffw.alertsystem.common.util.Logger;



/**
 * Implements the abstract @Logger class and acts as a fassade for other logger-
 * instances (encapsulates an object of type @Logger).<br>
 * This is the prefered logger for all plugins; everytime a plugin wants to log
 * a message this intermediate step (resp. {@link PluginLogger#log()}) adds the
 * plugin's instance-name to the message, but this is done totally transparently
 * for the plugin.
 */
public class PluginLogger extends Logger {
  
  private Logger _encapsulatedLogger;
  
  private String _pluginInstanceName;
  
  
  
  /**
   * @param encapsulatedLogger This logger instance is actually used for the
   *                           logging; log()-calls will be forwarded to this.
   * @param pluginInstanceName Name of the plugin.
   * @param logLevel           Level of log-granularity.
   */
  public PluginLogger(Logger encapsulatedLogger, String pluginInstanceName,
                      int logLevel) {
    super(logLevel);
    
    _encapsulatedLogger = encapsulatedLogger;
    _pluginInstanceName = pluginInstanceName;
    
    while (_pluginInstanceName.length() < 20) {
      _pluginInstanceName += " ";
    }
  }
  
  
  
  /**
   * Adds the {@link PluginLogger#pluginInstanceName} in front of the 
   * message-strings and then just forwards the message to the actual logger.
   */
  @Override
  public void log(String text, boolean printWithTime) {
    String newText = text.substring(0, 7) +
                     _pluginInstanceName + " >> " +
                     text.substring(7);
    
    _encapsulatedLogger.log(newText, printWithTime);
  }
  
  /**
   * Get the encapsulated @Logger instance. This is helpful if a plugin wants
   * to pass the logger to an other class/plugin. 
   * @return The encapsulated @Logger instance.
   */
  public Logger getEncapsulatedLogger() {
    return _encapsulatedLogger;
  }
  
}
