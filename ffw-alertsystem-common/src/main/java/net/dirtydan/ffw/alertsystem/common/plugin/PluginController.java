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



/**
 * A PluginController provides steering-capabilities for plugins which shall be
 * accessible for other plugins. This is used to provide plugins the possibility
 * to influence the loaded plugin-list.
 */
public interface PluginController {
  
  /**
   * @param instanceName The unique instance-name of the plugin to restart.
   */
  public void restartPlugin(String instanceName);
  
  /**
   * @param instanceName The unique instance-name of the plugin to activate.
   */
  public void activatePlugin(String instanceName);
  
  /**
   * @param instanceName The unique instance-name of the plugin to deactivate.
   */
  public void deactivatePlugin(String instanceName);
  
}
