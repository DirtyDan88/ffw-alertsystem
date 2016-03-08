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

package ffw.alertsystem.test.core.alertaction;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Arrays;

import ffw.alertsystem.core.plugin.Plugin.PluginState;
import ffw.alertsystem.plugins.test.AlertActionTestManager;
import ffw.alertsystem.plugins.test.config.AlertActionConfigGenerator;
import ffw.alertsystem.test.common.CommonJunitTest;
import ffw.alertsystem.test.common.PluginObserverTestClass;
import ffw.alertsystem.test.common._waitfor;



public class _Test_AlertAction extends CommonJunitTest {
  
  static {
    log.info("start junit-tests for alert-actions [general]", true);
  }
  
  private AlertActionConfigGenerator cs;
  
  private AlertActionTestManager aam;
  
  public _Test_AlertAction() {
    cs = new AlertActionConfigGenerator();
    aam = new AlertActionTestManager(cs, log);
    
    aam.addPluginObserver(new PluginObserverTestClass());
  }
  
  
  
//new HashMap<String, String>(){
//private static final long serialVersionUID = 1L;
//{
//put("alert-action-xsd", "");
//put("alert-action-xml", "");
//}},

//try {
//log.info("try to wait");
//Thread.sleep(1000000000);
//} catch (InterruptedException e) {
//e.printStackTrace();
//}
  
  
  
  @Test
  public void testAlertActionLifecycle() {
    log.info("+++++++++++ running testAlertActionLifecycle ++++++++++++", true);
    resetAllFlags();
    loadDefaultAlertAction();
    
    aam.loadAll();
    // TODO: the actions should have a init-method, which is called after
    //       loading
    
    // no matter how often the action is executed, the action lifecycle should
    // be the same: start -> execute -> stop
    for (int i = 0; i < 3; ++i) {
      assertFalse(AlertActionTestClass.executeWasCalled.is);
      
      aam.startAll(null);
      _waitfor.timeout(PluginObserverTestClass.startWasCalled);
      _waitfor.timeout(AlertActionTestClass.executeWasCalled);
      _waitfor.timeout(PluginObserverTestClass.stopWasCalled);
      
      assertEquals(PluginState.STOPPED, aam.plugins().get(0).state());
    }
  }
  
  @Test
  public void testObserverMethodsWereCalled() {
    log.info("++++++++ running testObserverMethodsWereCalled ++++++++++", true);
    resetAllFlags();
    loadDefaultAlertAction();
    aam.loadAll();
    
    // after load, init should be called
    _waitfor.timeout(PluginObserverTestClass.initWasCalled);
    
    // start the action
    aam.startAll(null);
    _waitfor.timeout(PluginObserverTestClass.startWasCalled);
    // action stops after its execution
    _waitfor.timeout(PluginObserverTestClass.stopWasCalled);
    
    // start again, this time with error during execution
    AlertActionTestClass.throwUncaughtException = true;
    aam.startAll(null);
    _waitfor.timeout(PluginObserverTestClass.errorWasCalled);
    
    // action can never have one of the following states, hence observer-methods
    // will never be called
    assertFalse(PluginObserverTestClass.sleepingWasCalled.is);
    assertFalse(PluginObserverTestClass.reloadingWasCalled.is);
    assertFalse(PluginObserverTestClass.runningWasCalled.is);
  }
  
  @Test
  public void testActionState() {
    log.info("++++++++++++++++ running testActionState ++++++++++++++++", true);
    resetAllFlags();
    loadDefaultAlertAction();
    
    // after loading state should be INITIALIZED
    aam.loadAll();
    assertEquals(PluginState.INITIALIZED, aam.plugins().get(0).state());
    
    // the STARTED-state is only 'catchable' if we simulate some work in the
    // plugin's start-method, otherwise the plugin goes immediately sleeping
    AlertActionTestClass.simulateWorkForNextCall = true;
    aam.startAll(null);
    _waitfor.timeout(PluginObserverTestClass.startWasCalled);
    assertEquals(PluginState.STARTED, aam.plugins().get(0).state());
    
    // alert-actions skip the plugin-execution loop and stop immediately after
    // the start-method
    _waitfor.timeout(PluginObserverTestClass.stopWasCalled);
    assertEquals(PluginState.STOPPED, aam.plugins().get(0).state());
  }
  
  @Test
  public void testRestartAfterError() {
    log.info("+++++++++++++ running testRestartAfterError +++++++++++++", true);
    resetAllFlags();
    loadDefaultAlertAction();
    aam.loadAll();
    
    // start and throw exception
    AlertActionTestClass.throwUncaughtException = true;
    aam.startAll(null);
    _waitfor.timeout(PluginObserverTestClass.startWasCalled);
    _waitfor.timeout(AlertActionTestClass.executeWasCalled);
    _waitfor.timeout(PluginObserverTestClass.errorWasCalled);
    
    // restart, expect normal execution
    aam.startAll(null);
    _waitfor.timeout(PluginObserverTestClass.startWasCalled);
    _waitfor.timeout(AlertActionTestClass.executeWasCalled);
    _waitfor.timeout(PluginObserverTestClass.stopWasCalled);
  }
  
  @Test
  public void testRestartWhileRunning() {
    log.info("+++++++++++++ running testRestartAfterError +++++++++++++", true);
    resetAllFlags();
    loadDefaultAlertAction();
    aam.loadAll();
    
    // start and throw exception
    AlertActionTestClass.simulateWorkForNextCall = true;
    AlertActionTestClass.simulateWorkDuration = 10;
    aam.startAll(null);
    _waitfor.timeout(PluginObserverTestClass.startWasCalled);
    _waitfor.timeout(AlertActionTestClass.executeWasCalled);

    // TODO: Atm nothing happens in this situation, the action cannot be started
    // again because it has the STARTED and ignores the message. Is this the
    // prefered behavior in this situation?
    // let action decide what to do in this situation:
    // - new config field: restartAfterNewMessageWithSameAlertNumber
    // - method which then decides if new message is 'better' than the already
    //   received one
    aam.startAll(null);
  }
  
  
  
  private void loadDefaultAlertAction() {
    cs.addConfig(
         "ffw.alertsystem.test.core.alertaction", "AlertActionTestClass",
         "JUNIT_TEST_ACTION", true, null, Arrays.asList("*")
       );
  }
  
  private void resetAllFlags() {
    AlertActionTestClass.executeWasCalled.is = false;
    
    PluginObserverTestClass.initWasCalled     .is = false;
    PluginObserverTestClass.startWasCalled    .is = false;
    PluginObserverTestClass.sleepingWasCalled .is = false;
    PluginObserverTestClass.runningWasCalled  .is = false;
    PluginObserverTestClass.reloadingWasCalled.is = false;
    PluginObserverTestClass.stopWasCalled     .is = false;
    PluginObserverTestClass.errorWasCalled    .is = false;
  }
  
}
