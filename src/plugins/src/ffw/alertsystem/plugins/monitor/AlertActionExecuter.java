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

import java.util.ArrayList;
import java.util.List;

import ffw.alertsystem.core.alertaction.AlertActionManager;
import ffw.alertsystem.core.message.Message;
import ffw.alertsystem.core.monitor.MonitorPlugin;



public class AlertActionExecuter extends MonitorPlugin {
  
  private AlertActionManager actions;
  
  private List<String> alertNumbers;
  
  
  
  @Override
  protected void onMonitorPluginStart() {
    alertNumbers = new ArrayList<>();
    initActions();
  }
  
  @Override
  protected void onMonitorPluginReload() {
    stopActions();
    initActions();
  }
  
  @Override
  protected void onReceivedMessage(Message message) {
    log.info("!! " + config().getInstanceName() + " !! alert detected");
    
    // prevent multiple alerting by checking the alertnumber 
    if (alertNumbers.contains(message.getAlertNumber())) {
      log.info("multiple alerting with different message-strings detected");
      
      // TODO: some actions could benefit from an other message string, 
      //       for example the HtmlBuilder from coordinates
      //       -> possibility to re-execute actions?
    } else {
      alertNumbers.add(message.getAlertNumber());
      actions.executeAll(message);
    }
  }
  
  @Override
  protected void onMonitorPluginStop() {
    stopActions();
  }
  
  @Override
  protected void onPluginError(Throwable t) {
    monitor().removeAlertActionManager(actions);
  }
  
  
  
  private void initActions() {
    actions = new AlertActionManager(
      config().paramList().get("alert-action-xsd"),
      config().paramList().get("alert-action-xml"),
      config().getInstanceName(), log
    );
    
    monitor().addAlertActionManager(actions);
    log.info("added action-manager: " + config().getDescription());
  }
  
  private void stopActions() {
    if (actions != null) {
      log.info("stop all actions and remove action-manager");
      actions.stopAll();
      monitor().removeAlertActionManager(actions);
    }
  }
  
}
