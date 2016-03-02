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

import static org.junit.Assert.*;
import org.junit.Test;

import ffw.alertsystem.core.ApplicationLogger;
import ffw.alertsystem.core.Application.ApplicationType;
import ffw.alertsystem.core.plugin.Plugin.PluginState;



public class PluginSystemTest {
  
  // flags for the plugin-class itself
  static BooleanRef onPluginStartWasCalled  = new BooleanRef();
  static BooleanRef onPluginReloadWasCalled = new BooleanRef();
  static BooleanRef onPluginRunWasCalled    = new BooleanRef();
  static BooleanRef onPluginStopWasCalled   = new BooleanRef();
  static BooleanRef onPluginErrorWasCalled  = new BooleanRef();
  
  // flags for the plugin-observer
  static BooleanRef onObserverInitWasCalled     = new BooleanRef();
  static BooleanRef onObserverStartWasCalled    = new BooleanRef();
  static BooleanRef onObserverSleepingWasCalled = new BooleanRef();
  static BooleanRef onObserverRunningWasCalled  = new BooleanRef();
  static BooleanRef onObserverStopWasCalled     = new BooleanRef();
  static BooleanRef onObserverErrorWasCalled    = new BooleanRef();
  
  private ApplicationLogger l;
  
  private PluginConfigSourceTestClass c;
  
  private PluginManagerTestClass m;
  
  public PluginSystemTest() {
    l = new ApplicationLogger(1, ApplicationType.ALERTMONITOR, false);
    c = new PluginConfigSourceTestClass();
    m = new PluginManagerTestClass(c, l);
  }
  
  
  
  @Test
  public void testPluginList() {
    // expect the plugin-list to be empty before loadAll()-method is called the
    // first time
    assertTrue(m.plugins().isEmpty());
    
    // after loading a plugin it is expected that list is still empty, because
    // the loaded plugin is inactive
    c.loadValidConfig(false);
    m.loadAll();
    assertTrue(m.plugins().isEmpty());
    
    // set the plugin active -> one plugin in list
    c.loadValidConfig(true);
    m.loadAll();
    assertEquals(1, m.plugins().size());
    
    // load a empty config-list -> no plugins
    c.loadEmptyList();
    m.loadAll();
    assertTrue(m.plugins().isEmpty());
  }
  
  @Test
  public void testPluginMethodsWereCalled() {
    resetAll();
    c.loadValidConfig(true);
    
    // start plugin, expect that the plugin's start-method is called
    m.loadAll();
    m.startAll();
    _wait(onPluginStartWasCalled);
    
    // plugin was already started, start-method should not be called again
    assertFalse(onPluginStartWasCalled.is);
    m.startAll();
    assertFalse(onPluginStartWasCalled.is);
    
    // after reloading the config-source but without any changes it is expected
    // that nothing happens
    m.loadAll();
    assertFalse(onPluginReloadWasCalled.is);
    
    // after reloading the config-source with new plugin-properties a
    // reload should be raised
    c.loadValidConfig(true, "testParamKey", "testParamVal");
    m.loadAll();
    _wait(onPluginReloadWasCalled);
    
    // waking the plugin -> run should be called
    m.wakeUpPlugins();
    _wait(onPluginRunWasCalled);
    
    // stop the plugin
    m.stopAll();
    _wait(onPluginStopWasCalled);
    
    // restart after plugin was stopped
    assertFalse(onPluginStartWasCalled.is);
    m.startAll();
    _wait(onPluginStartWasCalled);
    
    // and stop again
    assertFalse(onPluginStopWasCalled.is);
    m.stopAll();
    _wait(onPluginStopWasCalled);
    
    // TODO: TEST: onPluginErrorWasCalled
  }
  
  @Test
  public void testObserverMethodsWereCalled() {
    resetAll();
    c.loadValidConfig(true);
    
    // after loading, the init-method of observers should be called
    m.loadAll();
    _wait(onObserverInitWasCalled);
    
    // first start-method
    m.startAll();
    _wait(onObserverStartWasCalled);
    _wait(onObserverSleepingWasCalled);
    
    // plugun is not stopped -> no restart
    assertFalse(onObserverStartWasCalled.is);
    m.startAll();
    assertFalse(onObserverStartWasCalled.is);
    
    // wake-up plugin should call the run-method
    assertFalse(onObserverSleepingWasCalled.is);
    m.wakeUpPlugins();
    _wait(onObserverRunningWasCalled);
    _wait(onObserverSleepingWasCalled);
    
    // TODO: notify observer when plugin is reloading?
    //       add state: RELOADING
    
    // and stop the plugin
    m.stopAll();
    _wait(onObserverStopWasCalled);
    
    //TODO: TEST: onObserverErrorWasCalled
  }
  
  @Test
  public void testPluginState() {
    resetAll();
    c.loadValidConfig(true);
    
    m.loadAll();
    assertEquals(PluginState.INITIALIZED, m.plugins().get(0).state());
    
    // the STARTED-state is only 'catchable' if we simulate some work in the
    // plugin's start-method, otherwise the plugin goes immediately sleeping
    PluginTestClass.simulateWorkForNextCall = true;
    m.startAll();
    _wait(onObserverStartWasCalled);
    assertEquals(PluginState.STARTED, m.plugins().get(0).state());
    _wait(onObserverSleepingWasCalled);
    assertEquals(PluginState.SLEEPING, m.plugins().get(0).state());
    
    // the RUNNING-state is only 'catchable' if we simulate some work in the
    // plugin's run-method, otherwise the plugin goes immediately sleeping
    PluginTestClass.simulateWorkForNextCall = true;
    assertFalse(onObserverSleepingWasCalled.is);
    m.wakeUpPlugins();
    _wait(onObserverRunningWasCalled);
    assertEquals(PluginState.RUNNING, m.plugins().get(0).state());
    _wait(onObserverSleepingWasCalled);
    assertEquals(PluginState.SLEEPING, m.plugins().get(0).state());
    
    // TODO: BUG: When the plugin is reloaded, the state is always SLEEPING
    
    m.stopAll();
    _wait(onObserverStopWasCalled);
    assertEquals(PluginState.STOPPED, m.plugins().get(0).state());
    
    // restart plugin (without simulating work this time)
    assertFalse(onObserverSleepingWasCalled.is);
    m.startAll();
    _wait(onObserverSleepingWasCalled);
    assertEquals(PluginState.SLEEPING, m.plugins().get(0).state());
    
    // TODO: TEST: PluginState.ERROR;
  }
  
  
  
  private void _wait(BooleanRef flag) {
    // wait max. 12*250ms = 3s before timeout
    int timeout = 12;
    
    try {
      while (!flag.is && timeout > 0) {
        timeout--;
        Thread.sleep(250);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    // reset flag for next test
    flag.is = false;
    
    if (timeout == 0) {
      fail("Timeout when waiting for flag");
    }
  }
  
  // since both, the primitive boolean type and the Boolean wrapper class, are
  // not offering pass-by-reference, we have to implement our own wrapper.
  static class BooleanRef {
    boolean is = false;
  }
  
  private void resetAll() {
    onPluginStartWasCalled .is = false;
    onPluginReloadWasCalled.is = false;
    onPluginRunWasCalled   .is = false;
    onPluginStopWasCalled  .is = false;
    onPluginErrorWasCalled.is = false;
    
    onObserverInitWasCalled    .is = false;
    onObserverStartWasCalled   .is = false;
    onObserverSleepingWasCalled.is = false;
    onObserverRunningWasCalled .is = false;
    onObserverStopWasCalled    .is = false;
    onObserverErrorWasCalled   .is = false;
  }
  
}
