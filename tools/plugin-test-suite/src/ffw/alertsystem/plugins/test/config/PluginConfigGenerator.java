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
import java.util.Map;

import ffw.alertsystem.core.plugin.PluginConfig;



public class PluginConfigGenerator {
  
  public static void fill(String packageName, String className,
                            String instanceName, boolean isActive,
                            Map<String, String> paramList,
                            PluginConfig config) {
    try {
      Field _packageName = PluginConfig.class.getDeclaredField("packageName");
      _packageName.setAccessible(true);
      _packageName.set(config, packageName);
      
      Field _className = PluginConfig.class.getDeclaredField("className");
      _className.setAccessible(true);
      _className.set(config, className);
      
      Field _instanceName = PluginConfig.class.getDeclaredField("instanceName");
      _instanceName.setAccessible(true);
      _instanceName.set(config, instanceName);
      
      Field _isActive = PluginConfig.class.getDeclaredField("isActive");
      _isActive.setAccessible(true);
      _isActive.set(config, isActive);
      
      Field _description = PluginConfig.class.getDeclaredField("description");
      _description.setAccessible(true);
      _description.set(config, "JUNIT_" + instanceName);
      
      Field _paramList = PluginConfig.class.getDeclaredField("paramList");
      _paramList.setAccessible(true);
      _paramList.set(config, paramList);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
