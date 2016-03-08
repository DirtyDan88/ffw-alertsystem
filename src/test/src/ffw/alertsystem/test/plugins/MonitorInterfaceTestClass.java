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

package ffw.alertsystem.test.plugins;

import java.util.List;

import ffw.alertsystem.core.alertaction.AlertAction;
import ffw.alertsystem.core.alertaction.AlertActionConfig;
import ffw.alertsystem.core.alertaction.AlertActionManager;
import ffw.alertsystem.core.message.Message;
import ffw.alertsystem.core.message.MessageFactory;
import ffw.alertsystem.core.monitor.MonitorInterface;
import ffw.alertsystem.core.monitor.MonitorPlugin;
import ffw.alertsystem.core.monitor.MonitorPluginConfig;
import ffw.alertsystem.core.plugin.PluginConfigSource;
import ffw.alertsystem.plugins.test.MonitorPluginTestManager;
import ffw.alertsystem.test.common._waitfor.BooleanRef;
import ffw.alertsystem.util.Logger;



public class MonitorInterfaceTestClass implements MonitorInterface {
  
  public static BooleanRef insertMessageObjectWasCalled = new BooleanRef();
  public static BooleanRef insertMessageStringWasCalled = new BooleanRef();
  public static BooleanRef addActionManagerWasCalled    = new BooleanRef();
  public static BooleanRef removeActionManagerWasCalled = new BooleanRef();
  
  private MonitorPluginTestManager mpm;
  
  private Logger log;
  
  
  
  public MonitorInterfaceTestClass(PluginConfigSource<MonitorPluginConfig> cs,
                                  Logger log) {
    this.mpm = new MonitorPluginTestManager(cs, this, log);
    this.log = log;
  }
  
  public MonitorPluginTestManager getPluginManager() {
    return mpm;
  }
  
  
  
  @Override
  public void insertMessage(Message message) {
    insertMessageObjectWasCalled.is = true;
    
    message.evaluateMessageHead();
    message.evaluateMessage();
    
    mpm.receivedMessage(message);
  }
  
  @Override
  public void insertMessage(String messageString) {
    insertMessageStringWasCalled.is = true;
    insertMessage(MessageFactory.create(messageString, log));
  }
  
  
  
  @Override
  public void restartMonitorPlugin(String instanceName) {}
  
  @Override
  public void activateMonitorPlugin(String instanceName) {}
  
  @Override
  public void deactivateMonitorPlugin(String instanceName) {}
  
  
  
  @Override
  public List<MonitorPlugin> getMonitorPlugins() { return null; }
  
  @Override
  public List<MonitorPluginConfig> getMonitorPluginConfigs() { return null; }
  
  
  
  @Override
  public void addAlertActionManager(AlertActionManager m) {
    addActionManagerWasCalled.is = true;
  }
  
  @Override
  public void removeAlertActionManager(AlertActionManager m) {
    removeActionManagerWasCalled.is = true;
  }
  
  @Override
  public List<AlertAction> getAlertActions() { return null; }
  
  @Override
  public List<AlertActionConfig> getAlertActionConfigs() { return null; }
  
}
