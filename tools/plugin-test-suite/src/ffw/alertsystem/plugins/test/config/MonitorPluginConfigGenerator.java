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

package ffw.alertsystem.plugins.test.config;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ffw.alertsystem.core.monitor.MonitorPluginConfig;
import ffw.alertsystem.core.plugin.PluginConfigSource;



public class MonitorPluginConfigGenerator implements
                                      PluginConfigSource<MonitorPluginConfig> {
  
  private List<MonitorPluginConfig> configs = new LinkedList<>();
  
  
  
  public void addConfig(String packageName, String className,
                        String instanceName, boolean isActive,
                        Map<String, String> paramList, List<String> ricList) {
    MonitorPluginConfig config = new MonitorPluginConfig();
    PluginConfigGenerator.fill(
                            packageName, className, instanceName,
                            isActive, paramList, config
                          );
    try {
      Field _ricList = MonitorPluginConfig.class.getDeclaredField("ricList");
      _ricList.setAccessible(true);
      _ricList.set(config, ricList);
      
      // useInvalidMessages
      // useMessageCopies
      // messageHistory
      // monitorObserver
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    configs.add(config);
  }
  
  
  
  /**************************************************************************
   ***           Interface implementation: @PluginConfigSource            ***
   **************************************************************************/
  
  @Override
  public boolean hasChanged() {
    return true;
  }
  
  @Override
  public List<MonitorPluginConfig> getPluginConfigs(boolean setLastReadTime) {
    return configs;
  }
  
  @Override
  public void activatePlugin(String instanceName) {}
  
  @Override
  public void deactivatePlugin(String instanceName) {}
  
}
