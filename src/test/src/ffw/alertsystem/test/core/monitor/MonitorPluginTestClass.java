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

package ffw.alertsystem.test.core.monitor;

import ffw.alertsystem.core.message.Message;
import ffw.alertsystem.core.monitor.MonitorPlugin;
import ffw.alertsystem.test.common._timeout.BooleanRef;



public class MonitorPluginTestClass extends MonitorPlugin {
  
  static boolean simulateWorkForNextCall = false;
  static int     simulateWorkDuration    = 1;
  static boolean throwUncaughtException  = false;
  
  static BooleanRef startWasCalled       = new BooleanRef();
  static BooleanRef reloadWasCalled      = new BooleanRef();
  static BooleanRef recvMessageWasCalled = new BooleanRef();
  static BooleanRef stopWasCalled        = new BooleanRef();
  
  @Override
  protected void onMonitorPluginStart() {
    startWasCalled.is = true;
    simulateWork();
  }
  
  @Override
  protected void onMonitorPluginReload() {
    startWasCalled.is = true;
    simulateWork();
  }
  
  @Override
  protected void onReceivedMessage(Message message) {
    recvMessageWasCalled.is = true;
    simulateWork();
    
    if (throwUncaughtException) {
      throwUncaughtException = false;
      throw new NullPointerException();
    }
  }
  
  @Override
  protected void onMonitorPluginStop() {
    stopWasCalled.is = true;
    simulateWork();
  }
  
  // TODO: TEST onPluginError
  
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
