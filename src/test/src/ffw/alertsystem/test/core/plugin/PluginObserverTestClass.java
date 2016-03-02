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

import ffw.alertsystem.core.plugin.PluginObserver;



public class PluginObserverTestClass implements PluginObserver {
  
  @Override
  public void onPluginInitialized(String instanceName) {
    PluginSystemTest.onObserverInitWasCalled.is = true;
  }
  
  @Override
  public void onPluginStarted(String instanceName) {
    PluginSystemTest.onObserverStartWasCalled.is = true;
  }
  
  @Override
  public void onPluginGoesSleeping(String instanceName) {
    PluginSystemTest.onObserverSleepingWasCalled.is = true;
  }
  
  @Override
  public void onPluginIsRunning(String instanceName) {
    PluginSystemTest.onObserverRunningWasCalled.is = true;
  }
  
  @Override
  public void onPluginStopped(String instanceName) {
    PluginSystemTest.onObserverStopWasCalled.is = true;
  }
  
  @Override
  public void onPluginError(String instanceName, Throwable t) {
    PluginSystemTest.onObserverErrorWasCalled .is = true;
  }
  
}
