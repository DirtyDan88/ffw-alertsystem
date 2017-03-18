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

package net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface;

import net.dirtydan.ffw.alertsystem.common.plugin.PluginConfigSource;
import net.dirtydan.ffw.alertsystem.common.plugin.PluginManager;
import net.dirtydan.ffw.alertsystem.monitor.plugin.MonitorPlugin;
import net.dirtydan.ffw.alertsystem.monitor.plugin.MonitorPluginConfig;
import net.dirtydan.ffw.alertsystem.monitor.plugin.WebInterface;


public class JunitMonitorPluginManager extends PluginManager<MonitorPlugin,
                                                             MonitorPluginConfig> {
  
  public JunitMonitorPluginManager(PluginConfigSource<MonitorPluginConfig> cs) {
    super(cs);
  }
  
  @Override
  protected MonitorPlugin newInstance(MonitorPluginConfig config) {
    return new WebInterface();
  }
  
  @Override
  protected String pluginTypeName() {
    return "Junit_MonitorPlugin";
  }
  
}
