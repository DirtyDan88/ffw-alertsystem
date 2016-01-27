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

import java.util.List;

import ffw.alertsystem.core.alertaction.AlertAction;
import ffw.alertsystem.core.alertaction.AlertActionConfig;
import ffw.alertsystem.core.alertaction.AlertActionManager;
import ffw.alertsystem.core.message.Message;



/**
 * Describes the functionality a ffw-alertsystem-monitor has to provide.
 */
public interface MonitorInterface {
  
  /**
   * Insert a new message as @Message-object.
   */
  public void insertMessage(Message message);
  
  /**
   * Insert a new message as string.
   */
  public void insertMessage(String message);
  
  
  
  /**
   * Restart the plugin.
   * @param instanceName The unique instance-name of the plugin.
   */
  public void restartMonitorPlugin(String instanceName);
  
  /**
   * Activate the plugin (= set entry in config-file to true).
   * @param instanceName The unique instance-name of the plugin.
   */
  public void activateMonitorPlugin(String instanceName);
  
  /**
   * Deactivate the plugin (= set entry in config-file to false).
   * @param instanceName The unique instance-name of the plugin.
   */
  public void deactivateMonitorPlugin(String instanceName);
  
  
  
  /**
   * @return List with all currently loaded (= activated) monitor-plugins.
   */
  public List<MonitorPlugin> getMonitorPlugins();
  
  /**
   * @return List with all specified monitor-plugins in the config-file (this
   *         includes also the deactivated plugins).
   */
  public List<MonitorPluginConfig> getMonitorPluginConfigs();
  
  
  
  /**
   * Adds a manager of @AlertAction objects to the monitor. This is supposed to
   * serve only information purpose (which actions are loaded and their
   * configurations) and not to actual execute actions.
   * @param m The @AlertActionManager to add.
   */
  public void addAlertActionManager(AlertActionManager m);
  
  /**
   * Removes a manager of @AlertAction objects from the monitor.
   * @param m The @AlertActionManager to remove.
   */
  public void removeAlertActionManager(AlertActionManager m);
  
  /**
   * @return List with all active alert-actions of all registered
   *         @AlertActionManager (= will be executed in case of an alert).
   */
  public List<AlertAction> getAlertActions();
  
  /**
   * @return List with all specified alert-actions in the config-file(s) of all
   *         registered @AlertActionManager (this includes also the deactivated
   *         alert-actions).
   */
  public List<AlertActionConfig> getAlertActionConfigs();
  
}
