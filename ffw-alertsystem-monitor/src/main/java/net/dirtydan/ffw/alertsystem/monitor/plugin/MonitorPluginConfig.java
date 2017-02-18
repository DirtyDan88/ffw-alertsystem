/*
  Copyright (c) 2015-2017, Max Stark <max.stark88@web.de>
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

package net.dirtydan.ffw.alertsystem.monitor.plugin;

import java.util.List;
import net.dirtydan.ffw.alertsystem.common.plugin.PluginConfig;



/**
 * Describes the properties of a monitor-plugin. Extends the abstract base-class
 * @PluginConfig by following properties:<br>
 * - List with RICs<br>
 * - Usage of invalid or already received messages<br>
 * - A message history<br>
 * - Flag if plugin is monitor-observer.
 */
public class MonitorPluginConfig extends PluginConfig {
  
  /**
   * List with RICs. If a new message is received the first check is if this
   * list contains the message's RIC ('*' is wildcard -> plugin will process
   * all messages).
   */
  private List<String> ricList;
  
  /**
   * If true, plugin also proccesses invalid messages.
   */
  private boolean useInvalidMessages = false;
  
  /**
   * If true, plugin also proccesses message-duplicates.
   */
  private boolean useMessageCopies = false;
  
  /**
   * Maximum number of messages the plugin will store as its message-history.
   */
  private int messageHistory = 20;
  
  /**
   * If true, plugin will receive notifications in case of certain events
   * happened in the monitor.
   */
  private boolean monitorObserver = false;
  
  
  
  /**
   * Builder-method for MonitorPluginConfig objects.
   */
  public static MonitorPluginConfig buildFrom(PluginConfig basicConfig) {
    MonitorPluginConfig config = new MonitorPluginConfig();
    
    config.setJarFile(basicConfig.getJarFile());
    config.setPackageName(basicConfig.getPackageName());
    config.setClassName(basicConfig.getClassName());
    config.setInstanceName(basicConfig.getInstanceName());
    config.setActive(basicConfig.isActive());
    config.setDescription(basicConfig.getDescription());
    config.setParamList(basicConfig.paramList());
    config.setLogLevel(basicConfig.getLogLevel());
    config.setLastModifiedTime(basicConfig.getLastModifiedTime());
    
    return config;
  }
  
  
  
  /**
   * {@inheritDoc}<br><br>
   * The method first calls the overriden super-method and then adds the new
   * properties to the 'isDifferent' check.<br>
   */
  @Override
  public boolean isDifferent(PluginConfig o) {
    if (super.isDifferent(o)) {
      return true;
    }
    
    MonitorPluginConfig other = (MonitorPluginConfig) o;
    
    if (!ricList.equals(other.ricList)) {
      return true;
    }
    
    if (useInvalidMessages != other.useInvalidMessages) {
      return true;
    }
    
    if (useMessageCopies != other.useMessageCopies) {
      return true;
    }
    
    if (messageHistory != other.messageHistory) {
      return true;
    }
    
    if (monitorObserver != other.monitorObserver) {
      return true;
    }
    
    return false;
  }
  
  
  
  /**************************************************************************
   ***             Getter- and setter-methods are following               ***
   **************************************************************************/
  
  public final List<String> ricList() {
    return ricList;
  }
  
  protected final void setRicList(List<String> ricList) {
    this.ricList = ricList;
  }
  
  public final boolean useInvalidMessages() {
    return useInvalidMessages;
  }
  
  protected final void useInvalidMessages(boolean useInvalidMessages) {
    this.useInvalidMessages = useInvalidMessages;
  }

  public final boolean useMessageCopies() {
    return useMessageCopies;
  }

  protected final void useMessageCopies(boolean useMessageCopies) {
    this.useMessageCopies = useMessageCopies;
  }
  
  public final int messageHistory() {
    return messageHistory;
  }
  
  protected final void setMessageHistory(int messageHistory) {
    this.messageHistory = messageHistory;
  }
  
  public final boolean isMonitorObserver() {
    return monitorObserver;
  }
  
  protected final void setMonitorObserver(boolean monitorObserver) {
    this.monitorObserver = monitorObserver;
  }
  
}
