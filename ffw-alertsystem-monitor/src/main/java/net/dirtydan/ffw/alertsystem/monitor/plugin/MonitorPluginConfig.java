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

import java.util.ArrayList;
import java.util.List;
import net.dirtydan.ffw.alertsystem.common.plugin.PluginConfig;



/**
 * Describes the properties of a monitor-plugin. Extends the base-class
 * @PluginConfig by following properties:<br>
 * - List with RICs<br>
 * - Usage of invalid or already received messages<br>
 * - Flag if plugin is monitor-observer.
 * - A message history<br>
 */
public class MonitorPluginConfig extends PluginConfig {
  
  /**
   * List with RICs. If a new message is received the first check is if this
   * list contains the message's RIC ('*' is wildcard -> plugin will process
   * all messages).
   */
  private final List<String> _ricList;
  
  /**
   * If true, plugin also proccesses invalid messages.
   */
  private final boolean _useInvalidMessages;
  
  /**
   * If true, plugin also proccesses message-duplicates.
   */
  private final boolean _useMessageCopies;
  
  /**
   * If true, plugin will receive notifications in case of certain events
   * happened in the monitor.
   */
  private final boolean _isMonitorObserver;
  
  /**
   * Maximum number of messages the plugin will store as its message-history.
   */
  private final int _messageHistory;
  
  
  
  /**
   * Creates an instance of a @MonitorPluginConfig.
   * @param builder The @MonitorPluginConfig.Builder which is responsible for
   *                the creation of the object.
   */
  protected MonitorPluginConfig(Builder builder) {
    super(builder);
    _ricList            = builder._ricList;
    _useInvalidMessages = builder._useInvalidMessages;
    _useMessageCopies   = builder._useMessageCopies;
    _isMonitorObserver  = builder._isMonitorObserver;
    _messageHistory     = builder._messageHistory;
  }
  
  
  
  /**
   * {@inheritDoc}<br><br>
   * The method first calls the overriden super-method and then adds the new
   * properties to the 'isDifferent' check.<br>
   */
  @Override
  public boolean isDifferent(PluginConfig o) {
    if (super.isDifferent(o)) return true;
    
    MonitorPluginConfig other = (MonitorPluginConfig) o;
    if (!_ricList.equals(other._ricList)) return true;
    if (_useInvalidMessages != other._useInvalidMessages) return true;
    if (_useMessageCopies != other._useMessageCopies) return true;
    if (_isMonitorObserver != other._isMonitorObserver) return true;
    if (_messageHistory != other._messageHistory) return true;
    
    return false;
  }
  
  
  
  public final List<String> ricList() {
    return _ricList;
  }
  
  public final boolean useInvalidMessages() {
    return _useInvalidMessages;
  }
  
  public final boolean useMessageCopies() {
    return _useMessageCopies;
  }
  
  public final boolean isMonitorObserver() {
    return _isMonitorObserver;
  }
  
  public final int messageHistory() {
    return _messageHistory;
  }
  
  
  
  public static class Builder extends PluginConfig.Builder {
    
    private List<String> _ricList = new ArrayList<>();
    private boolean _useInvalidMessages = false;
    private boolean _useMessageCopies = false;
    private boolean _isMonitorObserver = false;
    private int _messageHistory = 20;
    
    public final Builder withRicList(List<String> ricList) {
      _ricList = ricList;
      return this;
    }
    
    public final Builder withUseInvalidMessages(boolean useInvalidMessages) {
      _useInvalidMessages = useInvalidMessages;
      return this;
    }
    
    public final Builder withUseMessageCopies(boolean useMessageCopies) {
      _useMessageCopies = useMessageCopies;
      return this;
    }
    
    public final Builder withIsMonitorObserver(boolean isMonitorObserver) {
      _isMonitorObserver = isMonitorObserver;
      return this;
    }
    
    public final Builder withMessageHistory(int messageHistory) {
      _messageHistory = messageHistory;
      return this;
    }
    
    @Override
    public MonitorPluginConfig build() {
      return new MonitorPluginConfig(this);
    }
    
  }
  
}
