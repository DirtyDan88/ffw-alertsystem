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

import ffw.alertsystem.core.plugin.PluginManager;
import ffw.alertsystem.util.Logger;



public class PluginManagerTestClass extends PluginManager<PluginTestClass,
                                                          PluginConfigTestClass> {
  
  public PluginManagerTestClass(PluginConfigSourceTestClass c, Logger log) {
    super(c, log);
  }
  
  @Override
  protected PluginTestClass newInstance(PluginConfigTestClass config) {
    return new PluginTestClass();
  }
  
  @Override
  protected String pluginTypeName() {
    return "JUNIT_TEST_PLUGIN";
  }
  
  public void wakeUpPlugins() {
    plugins().forEach(plugin -> plugin.wakeMeUp());
  }
  
}
