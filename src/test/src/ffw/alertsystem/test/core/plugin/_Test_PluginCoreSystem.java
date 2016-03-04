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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ffw.alertsystem.core.ApplicationLogger;
import ffw.alertsystem.core.Application.ApplicationType;
import ffw.alertsystem.core.plugin.Plugin.PluginState;



public class _Test_PluginCoreSystem {
  
  // flags for the plugin-class itself
  static BooleanRef onPluginStartWasCalled  = new BooleanRef();
  static BooleanRef onPluginReloadWasCalled = new BooleanRef();
  static BooleanRef onPluginRunWasCalled    = new BooleanRef();
  static BooleanRef onPluginStopWasCalled   = new BooleanRef();
  static BooleanRef onPluginErrorWasCalled  = new BooleanRef();
  
  // flags for the plugin-observer
  static BooleanRef onObserverInitWasCalled      = new BooleanRef();
  static BooleanRef onObserverStartWasCalled     = new BooleanRef();
  static BooleanRef onObserverSleepingWasCalled  = new BooleanRef();
  static BooleanRef onObserverRunningWasCalled   = new BooleanRef();
  static BooleanRef onObserverReloadingWasCalled = new BooleanRef();
  static BooleanRef onObserverStopWasCalled      = new BooleanRef();
  static BooleanRef onObserverErrorWasCalled     = new BooleanRef();
  
  
  
  private static ApplicationLogger log;
  
  private static Thread loggerThread;
  
  @BeforeClass
  public static void setup() {
    log = new ApplicationLogger(5, ApplicationType.JUNIT_TESTS, false);
    
    loggerThread = new Thread(log);
    loggerThread.start();
    
    log.info("start junit-tests for plugin-core-system", true);
  }
  
  @AfterClass
  public static void cleanup() {
    log.info("finished junit-tests for plugin-core-system", true);
    try {
      log.stop();
      loggerThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  
  
  private PluginConfigSourceTestClass c;
  
  private PluginManagerTestClass m;
  
  public _Test_PluginCoreSystem() {
    c = new PluginConfigSourceTestClass();
    m = new PluginManagerTestClass(c, log);
  }
  
  
  
  @Test
  public void testPluginList() {
    log.info("++++++++++++++++ running testPluginList ++++++++++++++++", true);
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
    log.info("++++++++++ running testPluginMethodsWereCalled ++++++++++", true);
    resetAll();
    c.loadValidConfig(true);
    
    // load plugin
    m.loadAll();
    // plugin not started yet -> stop-call should have no effect
    m.stopAll();
    assertFalse(onPluginStopWasCalled.is);
    
    // start plugin, expect that the plugin's start-method is called
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
    
    // throw exception in run-method -> error method should be called
    PluginTestClass.throwUncaughtException = true;
    m.wakeUpPlugins();
    _wait(onPluginErrorWasCalled);
    
    // and stop again, since we have a plugin-error the stop-method is not
    // called this time
    assertFalse(onPluginStopWasCalled.is);
    m.stopAll();
    assertFalse(onPluginStopWasCalled.is);
  }
  
  @Test
  public void testObserverMethodsWereCalled() {
    log.info("+++++++++ running testObserverMethodsWereCalled +++++++++", true);
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
    
    // after reloading the config-source but without any changes it is expected
    // that nothing happens
    m.loadAll();
    assertFalse(onObserverReloadingWasCalled.is);
    // after reloading the config-source with new plugin-properties a
    // reload should be raised
    c.loadValidConfig(true, "testParamKey", "testParamVal");
    m.loadAll();
    _wait(onObserverReloadingWasCalled);
    
    // and stop the plugin
    m.stopAll();
    _wait(onObserverStopWasCalled);
    
    // restart
    m.startAll();
    _wait(onObserverStartWasCalled);
    _wait(onObserverSleepingWasCalled);
    
    // throw exception in run-method -> error method should be called
    PluginTestClass.throwUncaughtException = true;
    m.wakeUpPlugins();
    _wait(onObserverErrorWasCalled);
    
    // and stop the plugin
    m.stopAll();
    assertFalse(onObserverStopWasCalled.is);
  }
  
  @Test
  public void testPluginState() {
    log.info("++++++++++++++++ running testPluginState ++++++++++++++++", true);
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
    
    // reload; simulate some work in the reload-method so that the RELOADING-
    // state is 'catchable' for the test
    PluginTestClass.simulateWorkForNextCall = true;
    c.loadValidConfig(true, "testParamKey", "testParamVal");
    m.loadAll();
    _wait(onObserverReloadingWasCalled);
    assertEquals(PluginState.RELOADING, m.plugins().get(0).state());
    
    // stop plugin
    m.stopAll();
    _wait(onObserverStopWasCalled);
    assertEquals(PluginState.STOPPED, m.plugins().get(0).state());
    
    // restart plugin (without simulating work this time)
    assertFalse(onObserverSleepingWasCalled.is);
    m.startAll();
    _wait(onObserverSleepingWasCalled);
    assertEquals(PluginState.SLEEPING, m.plugins().get(0).state());
    
    // throw exception in run-method -> state should be ERROR
    PluginTestClass.throwUncaughtException = true;
    m.wakeUpPlugins();
    _wait(onObserverErrorWasCalled);
    assertEquals(PluginState.ERROR, m.plugins().get(0).state());
    
    // stop the plugin, because of the error the stop method is not called, also
    // the state of the plugin stays in ERROR
    m.stopAll();
    assertEquals(PluginState.ERROR, m.plugins().get(0).state());
    
    // to 'properly' stop the plugin resp. to set the plugin from ERROR to
    // STOPPED -> restart and then stop
    m.startAll();
    _wait(onObserverSleepingWasCalled);
    m.stopAll();
    assertEquals(PluginState.STOPPED, m.plugins().get(0).state());
  }
  
  @Test
  public void testKillPlugin() {
    log.info("++++++++++++++++ running testKillPlugin ++++++++++++++++", true);
    resetAll();
    c.loadValidConfig(true);
    
    m.loadAll();
    m.startAll();
    _wait(onPluginStartWasCalled);
    
    // simulate some huge workload in run-method
    PluginTestClass.simulateWorkForNextCall = true;
    PluginTestClass.simulateWorkDuration = 100;
    m.wakeUpPlugins();
    _wait(onPluginRunWasCalled);
    
    // now stop, plugin is still running -> framework should first try to stop
    // and then kill it
    m.stopAll();
    assertFalse(onPluginStopWasCalled.is);
  }
  
  @Test
  public void testWakeUp() {
    log.info("++++++++++++++++++ running testWakeUp ++++++++++++++++++", true);
    resetAll();
    c.loadValidConfig(true);
    
    m.loadAll();
    m.startAll();
    _wait(onPluginStartWasCalled);
    
    // wake and run with some workload
    PluginTestClass.simulateWorkForNextCall = true;
    PluginTestClass.simulateWorkDuration = 3;
    m.wakeUpPlugins();
    _wait(onPluginRunWasCalled);
    
    // wake again, plugin is still in run-method
    m.wakeUpPlugins();
    _wait(onPluginRunWasCalled);
    
    // log-output for this test should contain:
    //   >> plugin was woken up
    //   >> plugin calls run-method
    //   >> plugin calls run-method
    // note: the run-method is called twice!
  }
  
  @Test
  public void testPluginRestart() {
    log.info("+++++++++++++++ running testPluginRestart ++++++++++++++", true);
    resetAll();
    c.loadValidConfig(true);
    
    m.loadAll();
    m.startAll();
    _wait(onPluginStartWasCalled);
    
    // force restart
    m.restart(m.plugins().get(0).config().getInstanceName());
    _wait(onPluginStopWasCalled);
    _wait(onPluginStartWasCalled);
    
    // throw exception in run-method
    PluginTestClass.throwUncaughtException = true;
    m.wakeUpPlugins();
    _wait(onPluginErrorWasCalled);
    assertFalse(onPluginStopWasCalled.is);
    assertEquals(PluginState.ERROR, m.plugins().get(0).state());
    
    // restart: this works because the plugin is in ERROR state
    m.startAll();
    _wait(onPluginStartWasCalled);
    
    // and stop
    m.stopAll();
    _wait(onPluginStopWasCalled);
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
    onPluginErrorWasCalled. is = false;
    
    onObserverInitWasCalled     .is = false;
    onObserverStartWasCalled    .is = false;
    onObserverSleepingWasCalled .is = false;
    onObserverRunningWasCalled  .is = false;
    onObserverReloadingWasCalled.is = false;
    onObserverStopWasCalled     .is = false;
    onObserverErrorWasCalled    .is = false;
  }
  
}
