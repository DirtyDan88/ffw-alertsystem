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
                                    PluginConfigSource<PluginConfigTestClass> {
  
  private List<PluginConfigTestClass> configs = new LinkedList<>();
  
  
  
  void loadConfig(boolean active) {
    loadConfig(active, "", "");
  }
  
  void loadConfig(boolean isActive, String paramKey, String paramVal) {
    Map<String, PluginParam> paramList = new HashMap<>();
    paramList.put(paramKey, new PluginParam(paramVal, false));
    
    PluginConfigTestClass config = new PluginConfigTestClass();
    config.setJarFile("");
    config.setPackageName("net.dirtydan.ffw.alertsystem.common.plugin");
    config.setClassName("PluginTestClass");
    config.setInstanceName("JUNIT_TEST_PLUGIN");
    config.setActive(isActive);
    config.setParamList(paramList);
    config.setLogLevel(Logger.DEBUG);
    
    configs.add(config);
  }
  
  void loadEmptyList() {
    configs = new LinkedList<>();
  }
  
  
  
  @Override
  public boolean hasChanged() {
    return true;
  }
  
  @Override
  public List<PluginConfigTestClass> getPluginConfigs(boolean setLastReadTime) {
    return configs;
  }
  
  @Override
  public void activatePlugin(String instanceName) {}
  
  @Override
  public void deactivatePlugin(String instanceName) {}
  
}
