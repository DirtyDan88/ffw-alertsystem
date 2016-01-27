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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import ffw.alertsystem.core.plugin.Plugin.PluginState;
import ffw.alertsystem.util.Logger;



/**
 * Abstract base-class to control plugins of a given type. Provides basic
 * functionality like loading, starting, stopping and killing all managed
 * plugins. This class is the preferred way to control plugins.
 * 
 * @param <PluginT>       The plugin-type which the manager shall control.
 * @param <PluginConfigT> The config-type of the plugins.
 */
public abstract class PluginManager<PluginT       extends Plugin<PluginConfigT>,
                                    PluginConfigT extends PluginConfig> {
  
  /**
   * The object which delivers the plugin-configs. Has to implement the
   * @PluginConfigSource interface.
   */
  private final PluginConfigSource<PluginConfigT> configSource;
  
  /**
   * The logger for the plugin-manager; access in sub-classes is possible
   * but the logger cannot be changed (since it is final).
   */
  protected final Logger log;
  
  /**
   * List with the currently loaded plugins.
   */
  private List<PluginT> plugins;
  
  /**
   * Set with the plugin-observer. Is used to add all registered observer when
   * a new plugin is created in {@link PluginManager#createPlugin()}.
   */
  private Set<PluginObserver> pluginObserver;
  
  
  
  /**
   * Constructor sets the plugin-config-source and the logger.
   */
  public PluginManager(PluginConfigSource<PluginConfigT> configSource,
                       Logger log) {
    this.configSource = configSource;
    this.log = log;
    
    plugins        = new LinkedList<>();
    pluginObserver = new HashSet<>();
  }
  
  
  
  /**
   * @return A view of the list with the currently loaded plugins.
   */
  public List<PluginT> plugins() {
    return Collections.unmodifiableList(plugins);
  }
  
  /**
   * @return List with plugin-configs defined in
   * {@link PluginManager#configSource()}.
   */
  public List<PluginConfigT> pluginConfigs() {
    // since the list is new created we don't need to make it unmodifiable
    return configSource.getPluginConfigs(false);
  }
  
  /**
   * Searchs {@link PluginManager#plugins()}-list for a specific plugin.
   * @param instanceName The unique instance-name of the searched plugin.
   * @return The searched plugin-object or null if it is not in the list.
   */
  private PluginT getPluginByName(String instanceName) {
    for (PluginT plugin : plugins) {
      if (plugin.config().getInstanceName().equals(instanceName)) {
        return plugin;
      }
    }
    
    return null;
  }
  
  
  
  /**
   * Checks if the plugins config-source has changed and if so, reloads changed
   * plugins, add new defined plugins and possibly remove plugins.
   */
  public final void loadAll() {
    //log.debug("check config-file for " + pluginTypeName() + "s", true);
    
    if (configSource.hasChanged() || checkForChangedJars()) {
      log.info("config-source for " + pluginTypeName() + "s has changed", true);
      
      List<PluginConfigT> newConfigList = configSource.getPluginConfigs(true);
      checkForInactivePlugins(newConfigList);
      checkForRemovedPlugins (newConfigList);
      
      if (newConfigList.isEmpty()) {
        log.warn("no " + pluginTypeName() + "s were specified in config-file");
        plugins.clear();
      } else {
        updatePluginList(newConfigList);
      }
    }
  }
  
  /**
   * Starts all loaded plugins.
   */
  public final void startAll() {
    plugins.forEach(plugin -> plugin.start());
  }
  
  /**
   * Stops and restarts the plugin (only if plugin-state is error).
   * @param instanceName The instanceName of the plugin to restart.
   */
  public final void restart(String instanceName) {
    PluginT plugin = getPluginByName(instanceName);
    
    if (plugin != null) {
      if (plugin.state() == PluginState.ERROR) {
        log.info("try to restart plugin: " + plugin.config().getInstanceName());
        stopPlugin(plugin);
        plugin.start();
      }
    } else {
      log.warn("could not find plugin " + instanceName);
    }
  }
  
  /**
   * Instructs the @PluginConfigSource to active a given plugin.
   * @param instanceName The unique instance-name of the plugin to activate.
   */
  public void activatePlugin(String instanceName) {
    configSource.activatePlugin(instanceName);
  }
  
  /**
   * Instructs the @PluginConfigSource to deactive a given plugin.
   * @param instanceName The unique instance-name of the plugin to deactivate.
   */
  public void deactivatePlugin(String instanceName) {
    configSource.deactivatePlugin(instanceName);
  }
  
  /**
   * Stops all loaded plugins.
   */
  public final void stopAll() {
    plugins.forEach(plugin -> stopPlugin(plugin));
  }
  
  /**
   * Calls the {@link Plugin#stop()}-method and checks afterwards if the plugin
   * is still runnning. If it is still alive {@link Plugin#join()} is called
   * which waits a certain amount of time for the plugin-thread to join. If this
   * doesn't happen, which means the plugin-thread is stuck somewhere in the
   * user-code, the plugin will be killed.
   * @param plugin The plugin to stop.
   */
  private void stopPlugin(PluginT plugin) {
    plugin.stop();
    
    if (plugin.isAlive()) {
      plugin.join(1);
      
      if (plugin.isAlive()) {
        log.warn("could not stop plugin: " + plugin.config().getInstanceName());
        killPlugin(plugin);
      }
    }
  }
  
  /**
   * Can be called if a plugin is not responding. This method tries to stop the
   * plugin (at most 3 times). If it is still not responding the plugin will be
   * killed. This method should only be called to kill a plugin which is not
   * responding!
   */
  private void killPlugin(PluginT plugin) {
    int tries = 0;
    
    while (plugin.isAlive() && tries < 3) {
      tries++;
      log.warn("try to stop plugin: " + plugin.config().getInstanceName());
      
      plugin.stop();
      plugin.join(2);
    }
    
    if (plugin.isAlive()) {
      log.warn("plugin is not responding, have to kill it: " +
               plugin.config().getInstanceName());
      plugin.kill();
    }
  }
  
  
  
  /**
   * TODO: results in fatal error
   * @return
   */
  private boolean checkForChangedJars() {
    return false;
    
    /*
    boolean jarHasChanged = false;
    List<PluginT> removePlugins = new LinkedList<>();
    
    for (PluginT plugin : pluginList) {
      long modifiedTime = new File(plugin.config().getJarFile()).lastModified();
      
      if (modifiedTime != plugin.config().getLastModifiedTime()) {
        log.info("jar-file of " + plugin.config().getInstanceName() + " changed");
        removePlugins.add(plugin);
        
        jarHasChanged = true;
      }
    }
    
    for (PluginT plugin : removePlugins) {
      pluginList.remove(plugin);
      stopPlugin(plugin);
    }
    
    return jarHasChanged;
    */
  }
  
  /**
   * Removes all inactive plugins from list with @PluginConfigT objects.
   * @param configs The list to verify.
   */
  private void checkForInactivePlugins(List<PluginConfigT> configs) {
    Predicate<PluginConfigT> filter = new Predicate<PluginConfigT>() {
      @Override
      public boolean test(PluginConfigT config) {
        return !config.isActive();
      }
    };
    
    configs.removeIf(filter);
  }
  
  /**
   * Checks the list {@link PluginManager#plugins()} against another (updated)
   * list with plugins (actually a list with plugin-configs, comparison is done
   * with the unique plugin property instance-name). If a plugin of the
   * currently loaded plugin-list is missing in the new list, it was obviously
   * removed from the config-source and hence it will also be removed from the
   * plugin-list. The sorting of the lists doesn't matter.
   * 
   * @param newConfigs The (updated) list for comparison.
   */
  private void checkForRemovedPlugins(List<PluginConfigT> newConfigs) {
    List<PluginT> removedPlugins = new LinkedList<>();
    
    for (PluginT plugin : plugins) {
      boolean stillThere = false;
     
      for (PluginConfigT newConfig : newConfigs) {
        if (plugin.config().getInstanceName().equals(
                  newConfig.getInstanceName())) {
          stillThere = true;
          break;
        }
      }
      
      if (!stillThere) {
        removedPlugins.add(plugin);
      }
    }
    
    for (PluginT plugin : removedPlugins) {
      plugins.remove(plugin);
      
      stopPlugin(plugin);
      log.info("removed " + pluginTypeName() + ": " +
               plugin.config().getInstanceName());
    }
  }
  
  
  
  /**
   * Creates new or reloads (if already existing) plugins, based on the given
   * list with plugin-configs.
   * @param newConfigList List with plugin-configs, which stores the properties
   *                      of a plugin.
   */
  private void updatePluginList(List<PluginConfigT> newConfigList) {
    for (PluginConfigT newConfig : newConfigList) {
      PluginT curPlugin = null;
      
      // check if plugin already exists
      for (PluginT plugin : plugins) {
        if (newConfig.getInstanceName().equals(plugin.config().getInstanceName())) {
          curPlugin = plugin;
          break;
        }
      }
      
      // if plugin already exists then reload it if its config has changed, 
      // else create the plugin
      if (curPlugin != null) {
        if (curPlugin.config().isDifferent(newConfig)) {
          curPlugin.reload(newConfig, log);
        }
      } else {
        createPlugin(newConfig);
      }
    }
  }
  
  /**
   * Calls {@link PluginManager#newInstance()} to create a new plugin-object.
   * Also sets the plugin's observer and calls the {@link Plugin#init()}.
   * @param config The plugin's config-file.
   */
  private void createPlugin(PluginConfigT config) {
    PluginT plugin = newInstance(config);
    
    if (plugin != null) {
      plugins.add(plugin);
      pluginObserver.forEach(observer -> plugin.addObserver(observer));
      
      plugin.init(config, log);
      
      String debug = "";
      if (config.isInDebugMode()) debug = " (debug-mode)";
      
      log.info("loaded new " + pluginTypeName() + ": " +
               config.getInstanceName() + debug);
    }
  }
  
  /**
   * The plugin-manager is not able to know at compile-time, which kind of
   * plugins it is supposed to managed, and hence the creation has to be done
   * it the concrete manager-class.
   * 
   * @param config The plugin's config-object.
   * @return A new instance of the plugin which will be managed by the plugin-
   *         manager.
   */
  protected abstract PluginT newInstance(PluginConfigT config);
  
  
  
  /**
   * In order to have some proper log-messages the type-name of the plugins is
   * needed.
   * @return The plugins type-name managed by this PluginManager.
   */
  protected abstract String pluginTypeName();
  
  
  
  /**
   * Registers a new @PluginObserver to both the internal set
   * {@link PluginManager#pluginObserver()} and to all currently loaded plugins.
   * @param observer The new plugin-observer.
   */
  public void addPluginObserver(PluginObserver observer) {
    pluginObserver.add(observer);
    plugins.forEach(plugin -> plugin.addObserver(observer));
  }
  
  /**
   * Removes a previously registered @PluginObserver from both the internal set
   * {@link PluginManager#pluginObserver()} and from all currently loaded
   * plugins.
   * @param observer The plugin-observer to remove.
   */
  public void removePluginObserver(PluginObserver observer) {
    pluginObserver.remove(observer);
    plugins.forEach(plugin -> plugin.removeObserver(observer));
  }
  
}
