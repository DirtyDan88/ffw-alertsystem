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

package ffw.alertsystem.core.monitor;

import ffw.alertsystem.core.Application;



/**
 * The ffw-alertsystem-monitor is a plugin-based application and steers all
 * actions after receiving a @Message. But this is all implemented and
 * configured through the plugins, the actual monitor-application is only
 * responsible for controlling the plugins. Main class therefore is the
 * @MessageMonitor, which also runs the application loop.<br>
 * 
 * @see @Application
 * @see @MonitorPluginManager
 * @see @MonitorPlugin
 */
public class MonitorApplication extends Application {
  
  /**
   * Entry point for the ffw-alertsystem-monitor-application.
   * @param args Command-line parameters.
   */
  public static void main(String[] args) {
    new MonitorApplication(args).start();
  }
  
  
  
  private MessageMonitor monitor;
  
  private Thread monitorThread;
  
  public MonitorApplication(String[] args) {
    super(Application.ApplicationType.ALERTMONITOR, args);
  }
  
  
  
  @Override
  protected void onApplicationStarted() {
    monitor = new MessageMonitor(this);
    
    monitorThread = new Thread(monitor);
    monitorThread.setName("monitor-thread");
    monitorThread.setUncaughtExceptionHandler(errHandler);
    
    monitorThread.start();
  }
  
  @Override
  protected void onApplicationStopped() {
    if (monitor != null) {
      monitor.stop();
      /*
      try {
        monitorThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      */
    }
  }
  
  @Override
  protected void onApplicationErrorOccured(Throwable t) {
    monitor.monitorErrorOccured(t);
  }
  
}
