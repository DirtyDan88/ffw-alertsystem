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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.dirtydan.ffw.alertsystem.common.plugin.PluginConfig.PluginParam;
import net.dirtydan.ffw.alertsystem.common.util.Logger;



public class PluginConfigSourceTestClass implements
                                    PluginConfigSource<PluginConfig> {
  
  private List<PluginConfig> _configs = new LinkedList<>();
  
  
  
  void loadConfig(boolean active) {
    loadConfig(active, "", "");
  }
  
  void loadConfig(boolean isActive, String paramKey, String paramVal) {
    Map<String, PluginParam> paramList = new HashMap<>();
    paramList.put(paramKey, new PluginParam(paramVal, false));
    
    _configs.add(
      new PluginConfig.Builder()
        .withInstanceName("JUNIT_TEST_PLUGIN")
        .withIsActive(isActive)
        .withParamList(paramList)
        .withLogLevel(Logger.DEBUG)
        .build()
      );
  }
  
  void loadEmptyList() {
    _configs = new LinkedList<>();
  }
  
  
  
  @Override
  public boolean hasChanged() {
    return true;
  }
  
  @Override
  public List<PluginConfig> getPluginConfigs(boolean setLastReadTime) {
    return _configs;
  }
  
  @Override
  public void activatePlugin(String instanceName) {}
  
  @Override
  public void deactivatePlugin(String instanceName) {}
  
}
