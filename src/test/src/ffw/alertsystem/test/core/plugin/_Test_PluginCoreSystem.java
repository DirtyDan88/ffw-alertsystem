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

import ffw.alertsystem.core.plugin.Plugin.PluginState;
import ffw.alertsystem.test.common.CommonJunitTest;
import ffw.alertsystem.test.common.PluginObserverTestClass;
import ffw.alertsystem.test.common._timeout;



public class _Test_PluginCoreSystem extends CommonJunitTest {
  
  static {
    log.info("start junit-tests for plugin-core-system", true);
  }
  
  private PluginConfigSourceTestClass cs;
  
  private PluginManagerTestClass pm;
  
  public _Test_PluginCoreSystem() {
    cs = new PluginConfigSourceTestClass();
    pm = new PluginManagerTestClass(cs, log);
    
    pm.addPluginObserver(new PluginObserverTestClass());
  }
  
  
  
  @Test
  public void testPluginList() {
    log.info("++++++++++++++++ running testPluginList ++++++++++++++++", true);
    // expect the plugin-list to be empty before loadAll()-method is called the
    // first time
    assertTrue(pm.plugins().isEmpty());
    
    // after loading a plugin it is expected that list is still empty, because
    // the loaded plugin is inactive
    cs.loadConfig(false);
    pm.loadAll();
    assertTrue(pm.plugins().isEmpty());
    
    // set the plugin active -> one plugin in list
    cs.loadConfig(true);
    pm.loadAll();
    assertEquals(1, pm.plugins().size());
    
    // load a empty config-list -> no plugins
    cs.loadEmptyList();
    pm.loadAll();
    assertTrue(pm.plugins().isEmpty());
  }
  
  @Test
  public void testPluginMethodsWereCalled() {
    log.info("++++++++++ running testPluginMethodsWereCalled ++++++++++", true);
    resetAllFlags();
    cs.loadConfig(true);
    
    // load plugin
    pm.loadAll();
    // plugin not started yet -> stop-call should have no effect
    pm.stopAll();
    assertFalse(PluginTestClass.stopWasCalled.is);
    
    // start plugin, expect that the plugin's start-method is called
    pm.startAll();
    _timeout.waitfor(PluginTestClass.startWasCalled);
    
    // plugin was already started, start-method should not be called again
    assertFalse(PluginTestClass.startWasCalled.is);
    pm.startAll();
    assertFalse(PluginTestClass.startWasCalled.is);
    
    // after reloading the config-source but without any changes it is expected
    // that nothing happens
    pm.loadAll();
    assertFalse(PluginTestClass.reloadWasCalled.is);
    
    // after reloading the config-source with new plugin-properties a
    // reload should be raised
    cs.loadConfig(true, "testParamKey", "testParamVal");
    pm.loadAll();
    _timeout.waitfor(PluginTestClass.reloadWasCalled);
    
    // waking the plugin -> run should be called
    pm.wakeUpPlugins();
    _timeout.waitfor(PluginTestClass.runWasCalled);
    
    // stop the plugin
    pm.stopAll();
    _timeout.waitfor(PluginTestClass.stopWasCalled);
    
    // restart after plugin was stopped
    assertFalse(PluginTestClass.startWasCalled.is);
    pm.startAll();
    _timeout.waitfor(PluginTestClass.startWasCalled);
    
    // throw exception in run-method -> error method should be called
    PluginTestClass.throwUncaughtException = true;
    pm.wakeUpPlugins();
    _timeout.waitfor(PluginTestClass.errorWasCalled);
    
    // and stop again, since we have a plugin-error the stop-method is not
    // called this time
    assertFalse(PluginTestClass.stopWasCalled.is);
    pm.stopAll();
    assertFalse(PluginTestClass.stopWasCalled.is);
  }
  
  @Test
  public void testObserverMethodsWereCalled() {
    log.info("+++++++++ running testObserverMethodsWereCalled +++++++++", true);
    resetAllFlags();
    cs.loadConfig(true);
    
    // after loading, the init-method of observers should be called
    pm.loadAll();
    _timeout.waitfor(PluginObserverTestClass.initWasCalled);
    
    // first start-method
    pm.startAll();
    _timeout.waitfor(PluginObserverTestClass.startWasCalled);
    _timeout.waitfor(PluginObserverTestClass.sleepingWasCalled);
    
    // plugun is not stopped -> no restart
    assertFalse(PluginObserverTestClass.startWasCalled.is);
    pm.startAll();
    assertFalse(PluginObserverTestClass.startWasCalled.is);
    
    // wake-up plugin should call the run-method
    assertFalse(PluginObserverTestClass.sleepingWasCalled.is);
    pm.wakeUpPlugins();
    _timeout.waitfor(PluginObserverTestClass.runningWasCalled);
    _timeout.waitfor(PluginObserverTestClass.sleepingWasCalled);
    
    // after reloading the config-source but without any changes it is expected
    // that nothing happens
    pm.loadAll();
    assertFalse(PluginObserverTestClass.reloadingWasCalled.is);
    // after reloading the config-source with new plugin-properties a
    // reload should be raised
    cs.loadConfig(true, "testParamKey", "testParamVal");
    pm.loadAll();
    _timeout.waitfor(PluginObserverTestClass.reloadingWasCalled);
    
    // and stop the plugin
    pm.stopAll();
    _timeout.waitfor(PluginObserverTestClass.stopWasCalled);
    
    // restart
    pm.startAll();
    _timeout.waitfor(PluginObserverTestClass.startWasCalled);
    _timeout.waitfor(PluginObserverTestClass.sleepingWasCalled);
    
    // throw exception in run-method -> error method should be called
    PluginTestClass.throwUncaughtException = true;
    pm.wakeUpPlugins();
    _timeout.waitfor(PluginObserverTestClass.errorWasCalled);
    
    // and stop the plugin
    pm.stopAll();
    assertFalse(PluginObserverTestClass.stopWasCalled.is);
  }
  
  @Test
  public void testPluginState() {
    log.info("++++++++++++++++ running testPluginState ++++++++++++++++", true);
    resetAllFlags();
    cs.loadConfig(true);
    
    pm.loadAll();
    assertEquals(PluginState.INITIALIZED, pm.plugins().get(0).state());
    
    // the STARTED-state is only 'catchable' if we simulate some work in the
    // plugin's start-method, otherwise the plugin goes immediately sleeping
    PluginTestClass.simulateWorkForNextCall = true;
    pm.startAll();
    _timeout.waitfor(PluginObserverTestClass.startWasCalled);
    assertEquals(PluginState.STARTED, pm.plugins().get(0).state());
    _timeout.waitfor(PluginObserverTestClass.sleepingWasCalled);
    assertEquals(PluginState.SLEEPING, pm.plugins().get(0).state());
    
    // the RUNNING-state is only 'catchable' if we simulate some work in the
    // plugin's run-method, otherwise the plugin goes immediately sleeping
    PluginTestClass.simulateWorkForNextCall = true;
    assertFalse(PluginObserverTestClass.sleepingWasCalled.is);
    pm.wakeUpPlugins();
    _timeout.waitfor(PluginObserverTestClass.runningWasCalled);
    assertEquals(PluginState.RUNNING, pm.plugins().get(0).state());
    _timeout.waitfor(PluginObserverTestClass.sleepingWasCalled);
    assertEquals(PluginState.SLEEPING, pm.plugins().get(0).state());
    
    // reload; simulate some work in the reload-method so that the RELOADING-
    // state is 'catchable' for the test
    PluginTestClass.simulateWorkForNextCall = true;
    cs.loadConfig(true, "testParamKey", "testParamVal");
    pm.loadAll();
    _timeout.waitfor(PluginObserverTestClass.reloadingWasCalled);
    assertEquals(PluginState.RELOADING, pm.plugins().get(0).state());
    
    // stop plugin
    pm.stopAll();
    _timeout.waitfor(PluginObserverTestClass.stopWasCalled);
    assertEquals(PluginState.STOPPED, pm.plugins().get(0).state());
    
    // restart plugin (without simulating work this time)
    assertFalse(PluginObserverTestClass.sleepingWasCalled.is);
    pm.startAll();
    _timeout.waitfor(PluginObserverTestClass.sleepingWasCalled);
    assertEquals(PluginState.SLEEPING, pm.plugins().get(0).state());
    
    // throw exception in run-method -> state should be ERROR
    PluginTestClass.throwUncaughtException = true;
    pm.wakeUpPlugins();
    _timeout.waitfor(PluginObserverTestClass.errorWasCalled);
    assertEquals(PluginState.ERROR, pm.plugins().get(0).state());
    
    // stop the plugin, because of the error the stop method is not called, also
    // the state of the plugin stays in ERROR
    pm.stopAll();
    assertEquals(PluginState.ERROR, pm.plugins().get(0).state());
    
    // to 'properly' stop the plugin resp. to set the plugin from ERROR to
    // STOPPED -> restart and then stop
    pm.startAll();
    _timeout.waitfor(PluginObserverTestClass.sleepingWasCalled);
    pm.stopAll();
    assertEquals(PluginState.STOPPED, pm.plugins().get(0).state());
  }
  
  @Test
  public void testKillPlugin() {
    log.info("++++++++++++++++ running testKillPlugin ++++++++++++++++", true);
    resetAllFlags();
    cs.loadConfig(true);
    
    pm.loadAll();
    pm.startAll();
    _timeout.waitfor(PluginTestClass.startWasCalled);
    
    // simulate some huge workload in run-method
    PluginTestClass.simulateWorkForNextCall = true;
    PluginTestClass.simulateWorkDuration = 100;
    pm.wakeUpPlugins();
    _timeout.waitfor(PluginTestClass.runWasCalled);
    
    // now stop, plugin is still running -> framework should first try to stop
    // and then kill it
    pm.stopAll();
    assertFalse(PluginTestClass.stopWasCalled.is);
  }
  
  @Test
  public void testWakeUp() {
    log.info("++++++++++++++++++ running testWakeUp ++++++++++++++++++", true);
    resetAllFlags();
    cs.loadConfig(true);
    
    pm.loadAll();
    pm.startAll();
    _timeout.waitfor(PluginTestClass.startWasCalled);
    
    // wake and run with some workload
    PluginTestClass.simulateWorkForNextCall = true;
    PluginTestClass.simulateWorkDuration = 3;
    pm.wakeUpPlugins();
    _timeout.waitfor(PluginTestClass.runWasCalled);
    
    // wake again, plugin is still in run-method
    pm.wakeUpPlugins();
    _timeout.waitfor(PluginTestClass.runWasCalled);
    
    // log-output for this test should contain:
    //   >> plugin was woken up
    //   >> plugin calls run-method
    //   >> plugin calls run-method
    // note: the run-method is called twice!
  }
  
  @Test
  public void testPluginRestart() {
    log.info("+++++++++++++++ running testPluginRestart ++++++++++++++", true);
    resetAllFlags();
    cs.loadConfig(true);
    
    pm.loadAll();
    pm.startAll();
    _timeout.waitfor(PluginTestClass.startWasCalled);
    
    // force restart
    pm.restart(pm.plugins().get(0).config().getInstanceName());
    _timeout.waitfor(PluginTestClass.stopWasCalled);
    _timeout.waitfor(PluginTestClass.startWasCalled);
    
    // throw exception in run-method
    PluginTestClass.throwUncaughtException = true;
    pm.wakeUpPlugins();
    _timeout.waitfor(PluginTestClass.errorWasCalled);
    assertFalse(PluginTestClass.stopWasCalled.is);
    assertEquals(PluginState.ERROR, pm.plugins().get(0).state());
    
    // restart: this works because the plugin is in ERROR state
    pm.startAll();
    _timeout.waitfor(PluginTestClass.startWasCalled);
    
    // and stop
    pm.stopAll();
    _timeout.waitfor(PluginTestClass.stopWasCalled);
  }
  
  @Test
  public void testGetParam() {
    log.info("+++++++++++++++ running testGetParam ++++++++++++++", true);
    resetAllFlags();
    
    cs.loadConfig(true, "testParamName", "testParamValue");
    pm.loadAll();
    
    // param should be available before start
    _timeout.waitfor(PluginObserverTestClass.initWasCalled);
    assertEquals(
      "testParamValue",
      pm.plugins().get(0).config().paramList().get("testParamName")
    );
    
    // and of course after start
    pm.startAll();
    _timeout.waitfor(PluginTestClass.startWasCalled);
    assertEquals(
      "testParamValue",
      pm.plugins().get(0).config().paramList().get("testParamName")
    );
    assertNotEquals(
      "someOtherValue",
      pm.plugins().get(0).config().paramList().get("testParamName")
    );
    
    // param should change when config was changed
    cs.loadConfig(true, "testParamName", "testParamValue-CHANGED");
    pm.loadAll();
    _timeout.waitfor(PluginTestClass.reloadWasCalled);
    assertEquals(
      "testParamValue-CHANGED",
      pm.plugins().get(0).config().paramList().get("testParamName")
    );
  }
  
  
  
  private void resetAllFlags() {
    PluginTestClass.startWasCalled .is = false;
    PluginTestClass.reloadWasCalled.is = false;
    PluginTestClass.runWasCalled   .is = false;
    PluginTestClass.stopWasCalled  .is = false;
    PluginTestClass.errorWasCalled .is = false;
    
    PluginObserverTestClass.initWasCalled     .is = false;
    PluginObserverTestClass.startWasCalled    .is = false;
    PluginObserverTestClass.sleepingWasCalled .is = false;
    PluginObserverTestClass.runningWasCalled  .is = false;
    PluginObserverTestClass.reloadingWasCalled.is = false;
    PluginObserverTestClass.stopWasCalled     .is = false;
    PluginObserverTestClass.errorWasCalled    .is = false;
  }
  
}
