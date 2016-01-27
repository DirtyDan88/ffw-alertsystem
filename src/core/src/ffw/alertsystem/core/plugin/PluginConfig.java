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

package ffw.alertsystem.core.plugin;

import java.util.Map;



/**
 * Describes the properties of a @Plugin, e.g. represents the content of the
 * plugin-config-file. Is not responsibly for creating a config or the actual
 * content!
 */
public abstract class PluginConfig {
  
  /**
   * The name of the jar-file which contains the code.
   */
  private String jarFile;
  
  /**
   * The package-name of the class inside the jar-file, which inherits from
   * the plugin-class. (= main-class and entry point for the plugin)
   */
  private String packageName;
  
  /**
   * The class-name of the class inside the jar-file, which inherits from
   * the plugin-class. (= main-class and entry point for the plugin)
   */
  private String className;
  
  /**
   * The unambiguously name of the plugin.
   */
  private String instanceName;
  
  /**
   * Determines whether the plugin is currently active or not. If not, the
   * plugin will not be created at all!
   */
  private boolean isActive = false;
  
  /**
   * Description of the functionality of the plugin.
   */
  private String description;
  
  /**
   * The parameters of the plugin; accessible via the parameter-name.
   */
  private Map<String, String> paramList;
  
  /**
   * In debug-mode the level of the @PluginLogger will be set to DEBUG
   * (otherwise the same level as the encapsulated logger).
   */
  private boolean debugMode = false;
  
  /**
   * Is being considered to store the last build-time of the plugin's jar-file,
   * hence it is possible to check if the jar has changed. Should be set during
   * the creation of the config-object.
   */
  private long lastModifiedTime = 0;
  
  
  
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
    if (debugMode != other.debugMode) {
      return true;
    }
    
    if (!paramList.equals(other.paramList)) {
      return true;
    }
    
    return false;
  }
  
  
  
  /**************************************************************************
   ***             Getter- and setter-methods are following               ***
   **************************************************************************/
  
  public final String getJarFile() {
    return jarFile;
  }
  
  protected final void setJarFile(String jarFile) {
    this.jarFile = jarFile;
  }
  
  public final String getPackageName() {
    return packageName;
  }

  protected final void setPackageName(String packageName) {
    this.packageName = packageName;
  }
  
  public final String getClassName() {
    return className;
  }

  protected final void setClassName(String className) {
    this.className = className;
  }
  
  public final String getInstanceName() {
    return instanceName;
  }
  
  protected final void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }
  
  public final boolean isActive() {
    return isActive;
  }
  
  protected final void setActive(boolean isActive) {
    this.isActive = isActive;
  }
  
  public final String getDescription() {
    return description;
  }
  
  protected final void setDescription(String description) {
    this.description = description;
  }
  
  public final Map<String, String> paramList() {
    return paramList;
  }
  
  protected final void setParamList(Map<String, String> paramList) {
    this.paramList = paramList;
  }
  
  public final boolean isInDebugMode() {
    return debugMode;
  }
  
  protected final void setDebugMode(boolean debugMode) {
    this.debugMode = debugMode;
  }
  
  public final long getLastModifiedTime() {
    return lastModifiedTime;
  }
  
  protected final void setLastModifiedTime(long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }
  
}
