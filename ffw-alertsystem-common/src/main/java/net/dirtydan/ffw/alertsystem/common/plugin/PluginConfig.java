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

package net.dirtydan.ffw.alertsystem.common.plugin;

import java.util.Map;

import net.dirtydan.ffw.alertsystem.common.util.Logger;



/**
 * Describes the properties of a @Plugin, e.g. represents the content of the
 * plugin-config-file. Is not responsibly for creating a config or the actual
 * content!
 */
public class PluginConfig {
  
  /**
   * The name of the jar-file which contains the code.
   */
  private String _jarFile;
  
  /**
   * The package-name of the class inside the jar-file, which inherits from
   * the plugin-class. (= main-class and entry point for the plugin)
   */
  private String _packageName;
  
  /**
   * The class-name of the class inside the jar-file, which inherits from
   * the plugin-class. (= main-class and entry point for the plugin)
   */
  private String _className;
  
  /**
   * The unambiguously name of the plugin.
   */
  private String _instanceName;
  
  /**
   * Determines whether the plugin is currently active or not. If not, the
   * plugin will not be created at all!
   */
  private boolean _isActive = false;
  
  /**
   * Description of the purpose of the plugin.
   */
  private String _description;
  
  /**
   * The parameters of the plugin; accessible via the parameter-name.
   */
  private Map<String, PluginParam> _paramList;
  
  /**
   * The log-level of the plugin-logger, see granularity of log-levels at
   * @Logger class, default is INFO.
   */
  private int _logLevel = Logger.INFO;
  
  /**
   * Is being considered to store the last build-time of the plugin's jar-file,
   * hence it is possible to check if the jar has changed. Should be set during
   * the creation of the config-object.
   */
  private long _lastModifiedTime = 0;
  
  
  
  protected PluginConfig() {}
  
  /**
   * Builder-method for PluginConfig objects.
   */
  public static PluginConfig build(String jarFile,
                                   String packageName,
                                   String className,
                                   String instanceName,
                                   boolean isActive,
                                   String description,
                                   Map<String, PluginParam> paramList,
                                   int logLevel,
                                   long lastModifiedTime) {
    PluginConfig config = new PluginConfig();
    
    config._jarFile          = jarFile;
    config._packageName      = packageName;
    config._className        = className;
    config._instanceName     = instanceName;
    config._isActive         = isActive;
    config._description      = description;
    config._paramList        = paramList;
    if (logLevel > 0 && logLevel < 5) config._logLevel = logLevel;
    config._lastModifiedTime = lastModifiedTime;
    
    return config;
  }
  
  
  
  /**
   * Compares this config-object with another config-object. This method is
   * supposed to be used to check whether a plugin-config has changed or not,
   * hence a plugin-config is different to an other config if one of the
   * members has changed which shall raise a reload of the plugin.
   * 
   * @param other The config-object for comparison.
   * @return True if the plugin is different, false else.
   */
  public boolean isDifferent(PluginConfig other) {
    if (_logLevel != other._logLevel) {
      return true;
    }
    
    if (!_paramList.equals(other._paramList)) {
      return true;
    }
    
    return false;
  }
  
  
  
  /**
   * A @PluginParam consists of a value and a flag, which indicates whether the
   * parameter should be hidden when displaying the content of the config-file
   * to the public (e.g. WebInterface-plugion).
   */
  public static class PluginParam {
    
    String _value;
    
    boolean _hide;
    
    public PluginParam(String value, boolean hide) {
      _value = value;
      _hide = hide;
    }
    
    public final String val() { return _value; }
    
    public final boolean hide() { return _hide; }
    
    @Override
    public boolean equals(Object o) {
      PluginParam other = (PluginParam) o;
      return (val().equals(other.val()) &&
              hide() == other.hide());
    }
  }
  
  
  
  /**************************************************************************
   ***             Getter- and setter-methods are following               ***
   **************************************************************************/
  
  public final String getJarFile() {
    return _jarFile;
  }
  
  protected final void setJarFile(String jarFile) {
    _jarFile = jarFile;
  }
  
  public final String getPackageName() {
    return _packageName;
  }

  protected final void setPackageName(String packageName) {
    _packageName = packageName;
  }
  
  public final String getClassName() {
    return _className;
  }

  protected final void setClassName(String className) {
    _className = className;
  }
  
  public final String getInstanceName() {
    return _instanceName;
  }
  
  protected final void setInstanceName(String instanceName) {
    _instanceName = instanceName;
  }
  
  public final boolean isActive() {
    return _isActive;
  }
  
  protected final void setActive(boolean isActive) {
    _isActive = isActive;
  }
  
  public final String getDescription() {
    return _description;
  }
  
  protected final void setDescription(String description) {
    _description = description;
  }
  
  public final Map<String, PluginParam> paramList() {
    return _paramList;
  }
  
  protected final void setParamList(Map<String, PluginParam> paramList) {
    _paramList = paramList;
  }
  
  public final int getLogLevel() {
    return _logLevel;
  }
  
  protected void setLogLevel(int logLevel) {
    _logLevel = logLevel;
  }
  
  public final long getLastModifiedTime() {
    return _lastModifiedTime;
  }
  
  protected final void setLastModifiedTime(long lastModifiedTime) {
    _lastModifiedTime = lastModifiedTime;
  }
  
}
