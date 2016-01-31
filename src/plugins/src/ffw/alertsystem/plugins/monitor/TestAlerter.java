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

package ffw.alertsystem.plugins.monitor;

import java.util.Timer;
import java.util.TimerTask;

import ffw.alertsystem.core.monitor.MonitorPlugin;
import ffw.alertsystem.util.DateAndTime;



public class TestAlerter extends MonitorPlugin {
  
  @Override
  protected void onMonitorPluginStart() {
    String time = config().paramList().get("time");
    String day  = config().paramList().get("day");
    log.info("test-alert will be executed on " + day + " at " +
             time + "h", true);
    
    Timer t = new Timer();
    t.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        if (state() == PluginState.STOPPED ||
            state() == PluginState.ERROR) {
          t.cancel();
        }
        
        String time = config().paramList().get("time");
        String day  = config().paramList().get("day");
        
        log.debug("test-alert will be executed on " + day + " at " +
                  time + "h", true);
        
        if (DateAndTime.getWeekday().equals(day) &&
            DateAndTime.getTime().startsWith(time)) {
          log.info("insert test alert-message", true);
          String ric = config().paramList().get("ric");
          String message  = "POCSAG1200: Address:  " + ric + "  Function: 0  " +
                            "Alpha:   TEST-TEST-TEST";
          monitor().insertMessage(message);
        }
      }
    }, 0, 1000 * 60); // run every minute -> seconds in config will be ignored
  }
  
  @Override
  protected void onMonitorPluginReload() {
    String time = config().paramList().get("time");
    String day  = config().paramList().get("day");
    
    log.info("test-alert will be executed on " + day + " at " +
             time + "h", true);
  }
  
}
