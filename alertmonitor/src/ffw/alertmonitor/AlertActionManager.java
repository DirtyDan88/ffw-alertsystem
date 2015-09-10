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
import ffw.util.config.ConfigFile;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class AlertActionManager {
  
  private static List<AlertAction> actionList = null;
  
  public static void executeActions(AlertMessage alertMessage) {
    ApplicationLogger.log("## received message (" + alertMessage.getType() + "), " +
                          "following actions will be executed: ", 
                          Application.ALERTMONITOR);
    boolean atLeastOne = false;
    
    loadAlertActions();
    for (AlertAction action : actionList) {
      if (action.isActive()) {
        if (action.getRicList().contains(alertMessage.getAddress()) ||
            action.getRicList().contains("*")) {
          action.execute(alertMessage);
          
          ApplicationLogger.log(action.getClass().getName() + ": " + 
                                action.getInfo() + " " +
                                "(" + action.getDescription() + ")",
                                Application.ALERTMONITOR, false);
          atLeastOne = true;
        }
      }
    }
    
    if (!atLeastOne) {
      ApplicationLogger.log("## no matching action for this RIC", 
                            Application.ALERTMONITOR, false);
    }
  }
  
  
  
  private static void loadAlertActions() {
    if (actionList == null || ConfigFile.hasChanged()) {
      actionList = new ArrayList<AlertAction>();
      
      List<AlertActionDesc> actionDescList = ConfigFile.getAlertActionDescs();
      if (actionDescList.isEmpty()) {
        ApplicationLogger.log("## No actions were specified in config-file",
                              Application.ALERTMONITOR, false);
      } else {
        for (AlertActionDesc actionDesc : actionDescList) {
          try {
            Class<?> actionClass = Class.forName(actionDesc.packageName + "." + 
                                                 actionDesc.className);
            Class<?> superClass  = actionClass.getSuperclass();
            
            if (superClass.equals(AlertAction.class)) {
              AlertAction action = (AlertAction) actionClass.newInstance();
              action.setInstanceName(actionDesc.instanceName);
              action.setActive(actionDesc.isActive);
              action.setDescription(actionDesc.description);
              
              action.setRicList(
                ConfigFile.getAlertActionRics(
                  action.getInstanceName()
                )
              );
              action.setParamList(
                ConfigFile.getAlertActionParams(
                  action.getInstanceName()
                )
              );
              
              actionList.add(action);
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
  }
  
  public static class AlertActionDesc {
    public String packageName  = null;
    public String className    = null;
    public String instanceName = null;
    public boolean isActive    = false;
    public String description  = null;
    
    public AlertActionDesc(String packageName,
                           String className,
                           String instanceName,
                           String isActive,
                           String description) {
      this.packageName  = packageName;
      this.className    = className;
      this.instanceName = instanceName;
      this.isActive     = (isActive.equals("true")) ? true : false ;
      this.description  = description;
    }
  }
}
