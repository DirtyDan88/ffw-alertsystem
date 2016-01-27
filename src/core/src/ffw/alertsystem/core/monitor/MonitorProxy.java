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
 * Proxy class which delegates calls from a plugin to the actual implementation
 * (@MessageMonitor). Without this proxy a plugin would be able to cast its 
 * interface-object to an actual @MessageMonitor and can then call all public
 * methods of it, e.g. the stop()-method.
 */
public class MonitorProxy implements MonitorInterface {
  
  private MonitorInterface monitor;
  
  public MonitorProxy(MonitorInterface monitor) {
    this.monitor = monitor;
  }
  
  
  
  @Override
  public void insertMessage(Message message) {
    monitor.insertMessage(message);
  }
  
  @Override
  public void insertMessage(String message) {
    monitor.insertMessage(message);
  }
  
  @Override
  public void restartMonitorPlugin(String instanceName) {
    monitor.restartMonitorPlugin(instanceName);
  }
  
  @Override
  public void activateMonitorPlugin(String instanceName) {
    monitor.activateMonitorPlugin(instanceName);
  }
  
  @Override
  public void deactivateMonitorPlugin(String instanceName) {
    monitor.deactivateMonitorPlugin(instanceName);
  }
  
  @Override
  public List<MonitorPlugin> getMonitorPlugins() {
    return monitor.getMonitorPlugins();
  }
  
  @Override
  public List<MonitorPluginConfig> getMonitorPluginConfigs() {
    return monitor.getMonitorPluginConfigs();
  }
  
  @Override
  public void addAlertActionManager(AlertActionManager m) {
    monitor.addAlertActionManager(m);
  }
  
  @Override
  public void removeAlertActionManager(AlertActionManager m) {
    monitor.removeAlertActionManager(m);
  }
  
  @Override
  public List<AlertAction> getAlertActions() {
    return monitor.getAlertActions();
  }
  
  @Override
  public List<AlertActionConfig> getAlertActionConfigs() {
    return monitor.getAlertActionConfigs();
  }
  
}
