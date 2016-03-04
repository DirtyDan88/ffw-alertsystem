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

import ffw.alertsystem.core.plugin.Plugin;



public class PluginTestClass extends Plugin<PluginConfigTestClass> {
  
  static boolean simulateWorkForNextCall = false;
  static int     simulateWorkDuration    = 1;
  
  static boolean throwUncaughtException = false;
  
  
  
  @Override
  protected void onPluginStart() {
    _Test_PluginCoreSystem.onPluginStartWasCalled.is = true;
    simulateWork();
  }
  
  @Override
  protected void onPluginReload() {
    _Test_PluginCoreSystem.onPluginReloadWasCalled.is = true;
    simulateWork();
  }
  
  @Override
  protected void onRun() {
    _Test_PluginCoreSystem.onPluginRunWasCalled.is = true;
    simulateWork();
    
    if (throwUncaughtException) {
      throwUncaughtException = false;
      throw new NullPointerException();
    }
  }
  
  @Override
  protected void onPluginStop() {
    _Test_PluginCoreSystem.onPluginStopWasCalled.is = true;
    simulateWork();
  }
  
  @Override
  protected void onPluginError(Throwable t) {
    _Test_PluginCoreSystem.onPluginErrorWasCalled.is = true;
    simulateWork();
  }
  
  
  
  public void wakeMeUp() {
    callRun();
  }
  
  private void simulateWork() {
    int time = simulateWorkDuration * 1000;
    simulateWorkDuration = 1;
    
    if (!simulateWorkForNextCall) return;
    simulateWorkForNextCall = false;
    
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
}
