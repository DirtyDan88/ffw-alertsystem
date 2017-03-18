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

package net.dirtydan.ffw.alertsystem.monitor.plugin;

import net.dirtydan.ffw.alertsystem.common.message.Message;
import net.dirtydan.ffw.alertsystem.common.plugin.PluginController;
import net.dirtydan.ffw.alertsystem.common.plugin.PluginDependency;
import net.dirtydan.ffw.alertsystem.monitor.MessageConsumer;
import net.dirtydan.ffw.alertsystem.monitor.MonitorStatus;
import net.dirtydan.ffw.alertsystem.monitor.action.RemoteActionController;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.jetty.JettyServer;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.websockets.WebSocketActions;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.websockets.WebSocketMessage;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.websockets.WebSocketPlugins;


// TODO: FEATURE: Fahrzeugstatus anzeigen
// TODO: FEATURE: Uebungsplan anzeigen


public class WebInterface extends MonitorPlugin {
  
  @PluginDependency
  public MessageConsumer monitor;
  
  @PluginDependency
  public PluginController pluginCtrl;
  
  @PluginDependency
  public RemoteActionController actionCtrl;
  
  @PluginDependency
  public MonitorStatus status;
  
  private JettyServer server;
  
  public static WebInterface instance;
  
  public WebInterface() { instance = this; }
  
  
  
  @Override
  protected void onMonitorPluginStart() {
    server = JettyServer.create(this, log);
    server.start();
  }
  
  @Override
  protected void onMonitorPluginReload() {
    server.stop();
    server.start();
  }
  
  @Override
  protected void onReceivedMessage(Message message) {
    // update all registered web-socket-clients
    WebSocketMessage.updateAll(message);
  }
  
  @Override
  protected void onMonitorObserverNotification() {
    // update all registered web-socket-clients
    WebSocketPlugins.updateAll();
    WebSocketActions.updateAll();
  }
  
  @Override
  protected void onMonitorPluginStop() {
    server.stop();
  }
  
}
