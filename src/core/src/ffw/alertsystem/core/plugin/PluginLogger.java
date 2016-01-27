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

import ffw.alertsystem.util.Logger;



/**
 * Implements the abstract @Logger class and acts as a fassade for other logger-
 * instances (encapsulates an object of type @Logger).<br>
 * This is the prefered logger for all plugins; everytime a plugin wants to log
 * a message this intermediate step (resp. {@link PluginLogger#log()}) adds the
 * plugin's instance-name to the message, but this is done totally transparently
 * for the plugin.
 */
public class PluginLogger extends Logger {
  
  private Logger encapsulatedLogger;
  
  private String pluginInstanceName;
  
  
  
  /**
   * @param encapsulatedLogger This logger instance is actually used for the
   *                           logging; log()-calls will be forwarded to this.
   * @param pluginInstanceName Name of the plugin.
   * @param logLevel           Level of log-granularity.
   */
  public PluginLogger(Logger encapsulatedLogger, String pluginInstanceName,
                      int logLevel) {
    super(logLevel);
    
    this.encapsulatedLogger = encapsulatedLogger;
    this.pluginInstanceName = pluginInstanceName;
    
    while (this.pluginInstanceName.length() < 20) {
      this.pluginInstanceName += " ";
    }
  }
  
  
  
  /**
   * Adds the {@link PluginLogger#pluginInstanceName} in front of the 
   * message-strings and then just forwards the message to the actual logger.
   */
  @Override
  public void log(String message, boolean printWithTime) {
    int pos = message.lastIndexOf("]") + 2;
    
    String newMessage = message.substring(0, pos) + 
                        pluginInstanceName + " >> " +
                        message.substring(pos);
    
    encapsulatedLogger.log(newMessage, printWithTime);
  }
  
  /**
   * Get the encapsulated @Logger instance. This is helpful if a plugin wants
   * to pass the logger to an other class/plugin. 
   * @return The encapsulated @Logger instance.
   */
  public Logger getEncapsulatedLogger() {
    return encapsulatedLogger;
  }
  
}
