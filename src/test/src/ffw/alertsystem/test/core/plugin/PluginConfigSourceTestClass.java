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

package ffw.alertsystem.test.core.plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ffw.alertsystem.core.plugin.PluginConfig;
import ffw.alertsystem.core.plugin.PluginConfigSource;



public class PluginConfigSourceTestClass implements
                                    PluginConfigSource<PluginConfigTestClass> {

  protected List<PluginConfigTestClass> configs;
  
  public PluginConfigSourceTestClass() {}
  
  
  
  protected void loadValidConfig(boolean active) {
    loadValidConfig(active, "", "");
  }
  
  protected void loadValidConfig(boolean active, String paramKey,
                                                 String paramVal) {
    Map<String, String> params = new HashMap<>();
    params.put(paramKey, paramVal);
    
    PluginConfigTestClass c1 = createPluginConfig(
                                 "TestPlugin-1", active,
                                 params
                               );
    
    configs = new ArrayList<>();
    configs.add(c1);
  }
  
  protected void loadEmptyList() {
    configs = new ArrayList<>();
  }
  
  
  
  private PluginConfigTestClass createPluginConfig(String instanceName,
                                                   boolean active,
                                                   Map<String, String> params) {
    PluginConfigTestClass config = new PluginConfigTestClass();
    
    try {
      Method method;
      
      method = PluginConfig.class.getDeclaredMethod(
                 "setInstanceName", String.class
               );
      method.setAccessible(true);
      method.invoke(config, instanceName);
      
      method = PluginConfig.class.getDeclaredMethod(
                 "setActive", boolean.class
               );
      method.setAccessible(true);
      method.invoke(config, active);
      
      method = PluginConfig.class.getDeclaredMethod(
                 "setParamList", Map.class
               );
      method.setAccessible(true);
      method.invoke(config, params);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return config;
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
