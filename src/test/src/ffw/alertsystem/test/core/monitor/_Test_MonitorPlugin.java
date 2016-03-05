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

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import ffw.alertsystem.core.plugin.Plugin.PluginState;
import ffw.alertsystem.plugins.test.MonitorPluginTestManager;
import ffw.alertsystem.plugins.test.config.MonitorPluginConfigGenerator;
import ffw.alertsystem.test.common.CommonJunitTest;
import ffw.alertsystem.test.common.MessageTestClass;
import ffw.alertsystem.test.common.PluginObserverTestClass;
import ffw.alertsystem.test.common._timeout;



public class _Test_MonitorPlugin extends CommonJunitTest {
  
  static {
    log.info("start junit-tests for monitor-plugins [general]", true);
  }
  
  private MonitorPluginConfigGenerator cs;
  
  private MonitorPluginTestManager mpm;
  
  public _Test_MonitorPlugin() {
    cs  = new MonitorPluginConfigGenerator();
    mpm = new MonitorPluginTestManager(cs, log);
    
    mpm.addPluginObserver(new PluginObserverTestClass());
  }
  
  
  
  @Test
  public void testMonitorPluginLifecycle() {
    log.info("++++++++++ running testMonitorPluginLifecycle +++++++++++", true);
    resetAllFlags();
    loadDefaultMonitorPlugin();
    
    mpm.loadAll();
    // TODO: onMonitorPluginInit-method?
    
    // the MonitorPlugin-lifecycle: start -> (optional: sleeping)
    // -> receivedMessage -> (optional: sleeping) -> [...] -> stop
    for (int i = 0; i < 3; ++i) {
      assertFalse(MonitorPluginTestClass.startWasCalled.is);
      assertFalse(PluginObserverTestClass.sleepingWasCalled.is);
      assertFalse(MonitorPluginTestClass.stopWasCalled.is);
      
      mpm.startAll();
      _timeout.waitfor(MonitorPluginTestClass.startWasCalled);
      _timeout.waitfor(PluginObserverTestClass.sleepingWasCalled);
      
      for (int j = 0; j < 3; ++j) {
        assertFalse(MonitorPluginTestClass.recvMessageWasCalled.is);
        assertFalse(PluginObserverTestClass.sleepingWasCalled.is);
        
        mpm.receivedMessage(new MessageTestClass());
        _timeout.waitfor(MonitorPluginTestClass.recvMessageWasCalled);
        _timeout.waitfor(PluginObserverTestClass.sleepingWasCalled);
      }
      
      mpm.stopAll();
      _timeout.waitfor(MonitorPluginTestClass.stopWasCalled);
    }
    
    assertEquals(PluginState.STOPPED, mpm.plugins().get(0).state());
  }
  
  @Test
  public void testObserverMethodsWereCalled() {
    log.info("++++++++ running testObserverMethodsWereCalled ++++++++++", true);
    resetAllFlags();
    loadDefaultMonitorPlugin();
    
    mpm.loadAll();
    _timeout.waitfor(PluginObserverTestClass.initWasCalled);
    
    mpm.startAll();
    _timeout.waitfor(PluginObserverTestClass.startWasCalled);
    _timeout.waitfor(PluginObserverTestClass.sleepingWasCalled);
    
    mpm.receivedMessage(new MessageTestClass());
    _timeout.waitfor(PluginObserverTestClass.runningWasCalled);
    
    // TODO: reloadingWasCalled
    
    mpm.stopAll();
    _timeout.waitfor(PluginObserverTestClass.stopWasCalled);
    
    // start again
    mpm.startAll();
    _timeout.waitfor(PluginObserverTestClass.startWasCalled);
    MonitorPluginTestClass.throwUncaughtException = true;
    mpm.receivedMessage(new MessageTestClass());
    _timeout.waitfor(PluginObserverTestClass.runningWasCalled);
    _timeout.waitfor(PluginObserverTestClass.errorWasCalled);
    assertFalse(PluginObserverTestClass.stopWasCalled.is);
  }
  
  @Test
  public void testMonitorPluginState() {
    log.info("++++++++++++ running testMonitorPluginState +++++++++++++", true);
    resetAllFlags();
    loadDefaultMonitorPlugin();
    
    // after loading state should be INITIALIZED
    mpm.loadAll();
    assertEquals(PluginState.INITIALIZED, mpm.plugins().get(0).state());
    
    // the STARTED-state is only 'catchable' if we simulate some work in the
    // plugin's start-method, otherwise the plugin goes immediately sleeping
    MonitorPluginTestClass.simulateWorkForNextCall = true;
    mpm.startAll();
    _timeout.waitfor(MonitorPluginTestClass.startWasCalled);
    assertEquals(PluginState.STARTED, mpm.plugins().get(0).state());
    
    // simulate some work to catch RUNNING-state and insert two message in the
    // queue
    MonitorPluginTestClass.simulateWorkForNextCall = true;
    mpm.receivedMessage(new MessageTestClass());
    mpm.receivedMessage(new MessageTestClass());
    _timeout.waitfor(MonitorPluginTestClass.recvMessageWasCalled);
    assertEquals(PluginState.RUNNING, mpm.plugins().get(0).state());
    // after processing the first message, the plugin should not go back
    // sleeping but processing the next message in the queue
    assertFalse(PluginObserverTestClass.sleepingWasCalled.is);
    _timeout.waitfor(MonitorPluginTestClass.recvMessageWasCalled);
    // after processing every message in the queue -> sleeping
    _timeout.waitfor(PluginObserverTestClass.sleepingWasCalled);
    assertEquals(PluginState.SLEEPING, mpm.plugins().get(0).state());
    
    // and stop
    mpm.stopAll();
    assertEquals(PluginState.STOPPED, mpm.plugins().get(0).state());
  }
  
  
  
  private void loadDefaultMonitorPlugin() {
    cs.addConfig(
         "ffw.alertsystem.test.core.monitor", "MonitorPluginTestClass",
         "JUNIT_TEST_PLUGIN", true, null, Arrays.asList("*")
       );
  }
  
  private void resetAllFlags() {
    MonitorPluginTestClass.startWasCalled      .is = false;
    MonitorPluginTestClass.reloadWasCalled     .is = false;
    MonitorPluginTestClass.recvMessageWasCalled.is = false;
    MonitorPluginTestClass.stopWasCalled       .is = false;
    
    PluginObserverTestClass.initWasCalled     .is = false;
    PluginObserverTestClass.startWasCalled    .is = false;
    PluginObserverTestClass.sleepingWasCalled .is = false;
    PluginObserverTestClass.runningWasCalled  .is = false;
    PluginObserverTestClass.reloadingWasCalled.is = false;
    PluginObserverTestClass.stopWasCalled     .is = false;
    PluginObserverTestClass.errorWasCalled    .is = false;
  }
  
}
