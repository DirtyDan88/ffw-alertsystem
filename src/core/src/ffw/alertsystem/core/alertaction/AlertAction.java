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

package ffw.alertsystem.core.alertaction;

import ffw.alertsystem.core.message.Message;
import ffw.alertsystem.core.plugin.Plugin;



/**
 * Abstract base-class for alert-actions which extends the @Plugin-class. Alert-
 * actions will be started in case of an alert, performs its execution and then
 * stopped. The {@link AlertAction#onRun()}-method will not be called.
 */
public abstract class AlertAction extends Plugin<AlertActionConfig> {
  
  /**
   * The @Message which raised the alert.
   */
  private Message message;
  
  
  
  // TODO: onPluginInitialized() method?
  
  
  
  /**
   * Sets the message which raised the alert. Has to be set before the action
   * is started, otherwise a NullPointerException occurs.
   */
  protected final void setMessage(Message message) {
    this.message = message;
  }
  
  /**
   * {@inheritDoc}
   * <br><br>
   * Triggers the execution of the alertaction.
   * <br>
   * Indeed the RIC was already checked in the @MonitorPlugin, to check it again
   * before the action-execution opens the possibility to branch the activities
   * to be taken.<br>
   */
  @Override
  protected final void onPluginStart() {
    if (config().ricList().contains("*") ||
        config().ricList().contains(message.getAddress())) {
      log.debug("alert-action executes message");
      execute(message);
    } else {
      log.info("alert-action ignores message (not in ric-list)");
    }
    
    stop();
  }
  
  /**
   * This method is supposed to contain the code which shall be executed in case
   * of an alert.
   * @param message The @Message which raised the alert.
   */
  protected abstract void execute(Message message);
  
  
  
  /**
   * Just prevent against overriding, because this method will not be called.
   */
  @Override
  protected final void onPluginReload() {}
  
  /**
   * Just prevent against overriding, because this method will not be called.
   * The actual execution of the action is done in onPluginStart().
   */
  @Override
  protected final void onRun() {}
  
  @Override
  protected final void onPluginStop() {
    log.debug("alert-action execution has finished");
  }
  
}
