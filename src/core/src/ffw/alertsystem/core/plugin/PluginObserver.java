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



/**
 * In case the state of a plugin has changed it will inform its observer, which
 * have to implement this interface.
 * 
 * @see @Plugin
 * @see {@link Plugin#state}
 */
public interface PluginObserver {
  
  /**
   * Called right after the plugin was loaded. Is called only once in the
   * lifetime of a plugin.
   * @param instanceName The unique instance-name of the plugin.
   */
  public void onPluginInitialized(String instanceName);
  
  /**
   * Called when the plugin was started and also when it was restarted.
   * @param instanceName The unique instance-name of the plugin.
   */
  public void onPluginStarted(String instanceName);
  
  /**
   * Called every time the plugin has finished its current execution and is now
   * waiting to be woken up again.
   * @param instanceName The unique instance-name of the plugin.
   */
  public void onPluginGoesSleeping(String instanceName);
  
  /**
   * Called right before the plugin starts its execution of user-code.
   * @param instanceName The unique instance-name of the plugin.
   */
  public void onPluginIsRunning(String instanceName);
  
  /**
   * Called if the config of the plugin has changed and a reload is raised.
   * @param instanceName The unique instance-name of the plugin.
   */
  public void onPluginIsReloading(String instanceName);
  
  /**
   * Called if the plugin was stopped.
   * @param instanceName The unique instance-name of the plugin.
   */
  public void onPluginStopped(String instanceName);
  
  /**
   * Called in case of an error (can be an intentionally or an uncaught error).
   * @param instanceName The unique instance-name of the plugin.
   */
  public void onPluginError(String instanceName, Throwable t);
  
}
