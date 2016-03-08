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

package ffw.alertsystem.test.plugins;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ffw.alertsystem.plugins.test.MonitorPluginTestManager;
import ffw.alertsystem.plugins.test.config.MonitorPluginConfigGenerator;
import ffw.alertsystem.test.common.CommonJunitTest;
import ffw.alertsystem.test.common._waitfor;
import ffw.alertsystem.util.DateAndTime;



public class _Test_TestAlerter extends CommonJunitTest {
  
  static {
    log.info("start junit-tests for plugin TestAlerter", true);
  }
  
  private MonitorPluginConfigGenerator cs;
  
  private MonitorPluginTestManager mpm;
  
  private MonitorInterfaceTestClass monitor;
  
  public _Test_TestAlerter() {
    cs  = new MonitorPluginConfigGenerator();
    monitor = new MonitorInterfaceTestClass(cs, log);
    mpm = monitor.getPluginManager();
  }
  
  
  
  @Test
  public void testInsertMessage() {
    log.info("+++++++++++++++ running testInsertMessage +++++++++++++++", true);
    // execute the test-alert in one minute from now
    long time = System.currentTimeMillis() + (60*1000);
    loadConfig(time);
    
    mpm.loadAll();
    mpm.startAll();
    
    // wait max 63s for the test-alert
    _waitfor.timeout(MonitorInterfaceTestClass.insertMessageStringWasCalled, 63);
    _waitfor.timeout(MonitorInterfaceTestClass.insertMessageObjectWasCalled);
    
    mpm.stopAll();
  }
  
  @Test
  public void testCancel() {
    log.info("++++++++++++++++++ running testCancel +++++++++++++++++++", true);
    // execute the test-alert in one minute from now
    long time = System.currentTimeMillis() + (60*1000);
    loadConfig(time);
    
    mpm.loadAll();
    mpm.startAll();
    
    _waitfor.countdown(3);
    mpm.stopAll();
    _waitfor.countdown(60);
    
    // plugin was stopped -> timer should be canceled and the methods should not
    // be called (log-output should contain message that timer was canceled)
    assertFalse(MonitorInterfaceTestClass.insertMessageStringWasCalled.is);
    assertFalse(MonitorInterfaceTestClass.insertMessageObjectWasCalled.is);
  }
  
  
  
  private void loadConfig(long time) {
    Map<String, String> params = new HashMap<>();
    params.put("time", DateAndTime.getTime(time));
    params.put("day",  DateAndTime.getWeekday());
    params.put("999999",  DateAndTime.getWeekday());
    cs.addConfig(
         "ffw.alertsystem.plugins.monitor", "TestAlerter",
         "JUNIT_TestAlerter", true, params, Arrays.asList("999999")
       );
  }
  
}
