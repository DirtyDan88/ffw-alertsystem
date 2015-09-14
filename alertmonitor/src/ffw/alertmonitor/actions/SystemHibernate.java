/*
  Copyright (c) 2015, Max Stark <max.stark88@web.de> 
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

package ffw.alertmonitor.actions;

import ffw.alertmonitor.AlertAction;
import ffw.util.ShellScript;
import ffw.util.TVController;
import ffw.util.TVController.TVAction;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class SystemHibernate extends AlertAction {

  @Override
  public String getInfo() {
    return "switches off TV and closes open applications after a given time";
  }
  
  @Override
  public void run() {
    int time = Integer.parseInt(paramList.get("system-hibernate-time"));
    
    try {
      Thread.sleep(time * 1000 * 60);
    } catch (InterruptedException e) {
      ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                            Application.ALERTMONITOR);
    }
    
    ShellScript.execute("close-applications");
    TVController.sendCommand(TVAction.TURN_OFF, paramList.get("serial-port"));
  }
}