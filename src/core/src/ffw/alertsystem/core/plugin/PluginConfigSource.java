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

import java.util.List;



/**
 * Specifies the interface a plugin-config-source has to implement. The duty of
 * a plugin-config-source is to deliver @PluginConfigT objects, thereby the
 * @PluginManager is able to create or update plugins.
 * 
 * @param <PluginConfigType> The type of the plugin-config which a concrete
 *                           implementation is capable to deliver.
 */
public interface PluginConfigSource<PluginConfigT extends PluginConfig> {
  
  /**
   * @return Whether the plugin-config-source has changed since last call of
   *         {@link PluginConfigSource#getPluginConfigs()} or not.
   */
  public boolean hasChanged();
  
  /**
   * Creates and delivers @PluginConfigT objects.
   * @param setLastReadTime This parameter is determined to affect the
   *                        behaviour of the
   *                        {@link PluginConfigSource#hasChanged()}-method; this
   *                        method compares the last read time with the last
   *                        modified time of the config. If setLastReadTime is
   *                        false the reading of the plugin-configs should be
   *                        done 'transparently', which means no last read time
   *                        is set and hasChanged() ignores the call.
   * @return A list with @PluginConfigT objects.
   */
  public List<PluginConfigT> getPluginConfigs(boolean setLastReadTime);
  
  /**
   * Instruction to the config-source to activate a given plugin.
   * @param instanceName The unique instance-name of the plugin to activate.
   */
  public void activatePlugin(String instanceName);
  
  /**
   * Instruction to the config-source to deactivate a given plugin.
   * @param instanceName The unique instance-name of the plugin to deactivate.
   */
  public void deactivatePlugin(String instanceName);
  
}
