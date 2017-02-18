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

import net.dirtydan.ffw.alertsystem.common.plugin._waitfor.BooleanRef;



public class PluginTestClass extends Plugin<PluginConfigTestClass> {
  
  static boolean simulateWorkForNextCall = false;
  static int     simulateWorkDuration    = 1;
  static boolean throwUncaughtException  = false;
  
  static BooleanRef startWasCalled  = new BooleanRef();
  static BooleanRef reloadWasCalled = new BooleanRef();
  static BooleanRef runWasCalled    = new BooleanRef();
  static BooleanRef stopWasCalled   = new BooleanRef();
  static BooleanRef errorWasCalled  = new BooleanRef();
  
  @Override
  protected void onPluginStart() {
    startWasCalled.is = true;
    simulateWork();
  }
  
  @Override
  protected void onPluginReload() {
    reloadWasCalled.is = true;
    simulateWork();
  }
  
  @Override
  protected void onRun() {
    runWasCalled.is = true;
    simulateWork();
    
    if (throwUncaughtException) {
      throwUncaughtException = false;
      throw new NullPointerException();
    }
  }
  
  @Override
  protected void onPluginStop() {
    stopWasCalled.is = true;
    simulateWork();
  }
  
  @Override
  protected void onPluginError(Throwable t) {
    errorWasCalled.is = true;
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
