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

import org.junit.AfterClass;
import org.junit.BeforeClass;

import ffw.alertsystem.core.ApplicationLogger;
import ffw.alertsystem.core.Application.ApplicationType;



public abstract class CommonJunitTest {
  
  protected static ApplicationLogger log;
  
  protected static Thread loggerThread;
  
  
  
  @BeforeClass
  public static void setup() {
    log = new ApplicationLogger(5, ApplicationType.JUNIT_TESTS, false);
    
    loggerThread = new Thread(log);
    loggerThread.start();
  }
  
  @AfterClass
  public static void cleanup() {
    log.info("+++++++++++++++++ finished junit-tests ++++++++++++++++++", true);
    try {
      log.stop();
      loggerThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
}
