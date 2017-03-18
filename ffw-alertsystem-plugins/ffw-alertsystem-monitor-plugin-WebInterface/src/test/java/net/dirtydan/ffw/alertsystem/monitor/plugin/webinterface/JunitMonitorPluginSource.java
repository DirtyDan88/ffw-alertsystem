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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.dirtydan.ffw.alertsystem.common.plugin.PluginConfigSource;
import net.dirtydan.ffw.alertsystem.common.plugin.PluginConfig.PluginParam;
import net.dirtydan.ffw.alertsystem.common.util.Logger;
import net.dirtydan.ffw.alertsystem.monitor.plugin.MonitorPluginConfig;


public class JunitMonitorPluginSource implements PluginConfigSource<MonitorPluginConfig> {
  
  private static List<MonitorPluginConfig> _configs;
  
  static {
    _configs = new LinkedList<>();
    
    String port = String.valueOf(RESTControllerTest.port);
    HashMap<String, PluginParam> paramList = new HashMap<String, PluginParam>();
    paramList.put("server-port", new PluginParam(port, false));
    paramList.put("user-name", new PluginParam("junit-tester", false));
    paramList.put("user-password", new PluginParam("test", false));
    
    _configs.add(
      ((MonitorPluginConfig.Builder) new MonitorPluginConfig.Builder()
        .withRicList(Arrays.asList("*"))
        .withInstanceName("JunitPluginInstance")
        .withIsActive(true)
        .withParamList(paramList)
        .withLogLevel(Logger.DEBUG))
        .build()
      );
  }
  
  
  @Override
  public boolean hasChanged() {
    return true;
  }

  @Override
  public List<MonitorPluginConfig> getPluginConfigs(boolean setLastReadTime) {
    return _configs;
  }

  @Override
  public void activatePlugin(String instanceName) {}

  @Override
  public void deactivatePlugin(String instanceName) {}

}
