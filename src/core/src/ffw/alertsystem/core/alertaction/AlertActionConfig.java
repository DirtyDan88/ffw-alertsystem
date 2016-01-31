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

import java.util.List;

import ffw.alertsystem.core.plugin.PluginConfig;



/**
 * Describes the properties of a alert-action. Extends the abstract base-class
 * @PluginConfig by following properties:<br>
 * - List with RICs.<br>
 * - Name of corresponding @AlertActionManager of this action.
 */
public class AlertActionConfig extends PluginConfig {
  
  /**
   * List with RICs. If a new message is received the first check is if this
   * list contains the message's RIC ('*' is wildcard -> action will process
   * all messages).
   * <br>
   * Indeed the RIC was already checked in the @MonitorPlugin, to check it again
   * before the action-execution opens the possibility to branch the activities
   * to be taken.
   */
  private List<String> ricList;
  
  /**
   * Has no functional relevance, just for informational purpose so its possible
   * to determine the @AlertActionManager of this action.
   */
  private String executerName;
  
  
  
  /**************************************************************************
   ***             Getter- and setter-methods are following               ***
   **************************************************************************/
  
  public final List<String> ricList() {
    return ricList;
  }
  
  protected final void setRicList(List<String> ricList) {
    this.ricList = ricList;
  }
  
  public final String executerName() {
    return executerName;
  }
  
  protected final void setExecuterName(String executerName) {
    this.executerName = executerName;
  }
  
}
