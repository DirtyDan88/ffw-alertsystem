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

package ffw.alertsystem.test.common;

import ffw.alertsystem.core.plugin.PluginObserver;
import ffw.alertsystem.test.common._timeout.BooleanRef;



public class PluginObserverTestClass implements PluginObserver {
  
  public static BooleanRef initWasCalled      = new BooleanRef();
  public static BooleanRef startWasCalled     = new BooleanRef();
  public static BooleanRef sleepingWasCalled  = new BooleanRef();
  public static BooleanRef runningWasCalled   = new BooleanRef();
  public static BooleanRef reloadingWasCalled = new BooleanRef();
  public static BooleanRef stopWasCalled      = new BooleanRef();
  public static BooleanRef errorWasCalled     = new BooleanRef();
  
  @Override
  public void onPluginInitialized(String instanceName) {
    initWasCalled.is = true;
  }
  
  @Override
  public void onPluginStarted(String instanceName) {
    startWasCalled.is = true;
  }
  
  @Override
  public void onPluginGoesSleeping(String instanceName) {
    sleepingWasCalled.is = true;
  }
  
  @Override
  public void onPluginIsRunning(String instanceName) {
    runningWasCalled.is = true;
  }
  
  @Override
  public void onPluginIsReloading(String instanceName) {
    reloadingWasCalled.is = true;
  }
  
  @Override
  public void onPluginStopped(String instanceName) {
    stopWasCalled.is = true;
  }
  
  @Override
  public void onPluginError(String instanceName, Throwable t) {
    errorWasCalled.is = true;
  }
  
}
