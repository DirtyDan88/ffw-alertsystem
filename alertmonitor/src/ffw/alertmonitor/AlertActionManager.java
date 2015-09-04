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

package ffw.alertmonitor;

import java.util.ArrayList;
import java.util.List;

import ffw.alertlistener.AlertMessage;
import ffw.alertmonitor.actions.AlertAction;
import ffw.util.ConfigReader;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class AlertActionManager {
  
  public static void executeActions(AlertMessage alertMessage) {
    ApplicationLogger.log("## alert was triggered, following actions were " +
                          "executed: ", Application.ALERTMONITOR);
    
    List<AlertAction> actionList = loadAlertActions();
    for (AlertAction action : actionList) {
      try {
        action.execute(alertMessage);
      } catch (Exception e) {
        ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                              Application.ALERTMONITOR);
      }
      
      ApplicationLogger.log(action.getClass().getName() + ": " + 
                            action.getDescription(), 
                            Application.ALERTMONITOR, false);
    }
  }
  
  private static List<AlertAction> loadAlertActions() {
    List<AlertAction> actionList = new ArrayList<AlertAction>();
    
    String actionPackageName = "ffw.alertmonitor.actions";
    String actionClassNames  = ConfigReader.getConfigVar("actionClassNames");
    
    if (actionClassNames.isEmpty()) {
      ApplicationLogger.log("## No actions were specified in config-file",
                            Application.ALERTMONITOR, false);
    } else {
      String[] actionClassNamesArray = actionClassNames.split(",");
      
      if (actionClassNamesArray.length == 0) {
        ApplicationLogger.log("## No actions were specified in config-file",
                              Application.ALERTMONITOR, false);
      } else {
        for (String actionClassName : actionClassNamesArray) {
          try {
            Class<?> actionClass = Class.forName(actionPackageName + "." + 
                                                 actionClassName);
            Class<?> superClass  = actionClass.getSuperclass();
            
            if (superClass.equals(AlertAction.class)) {
              actionList.add(
                (AlertAction) actionClass.newInstance()
              );
            }
              
          } catch (ClassNotFoundException | 
                   InstantiationException | 
                   IllegalAccessException e) {
            ApplicationLogger.log("## ERROR: Could not load action class", 
                                  Application.ALERTMONITOR, false);
          }
        }
      }
    }
    
    return actionList;
  }
}
