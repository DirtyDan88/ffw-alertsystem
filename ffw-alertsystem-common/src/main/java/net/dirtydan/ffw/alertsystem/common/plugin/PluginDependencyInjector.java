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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.dirtydan.ffw.alertsystem.common.util.Logger;



/**
 * If a plugin-field is annotated with @PluginDependency it is checked during
 * the plugin-creation if the @PluginDependencyInjector has an implementation
 * of the needed dependency in the registry, and if so, it injects this
 * implementation into the plugin.
 * 
 * @see @PluginDependency
 */
public class PluginDependencyInjector {
  
  private final Logger log = Logger.getApplicationLogger();
  
  /**
   * Registry which maps dependency -> dependency-implementation. Only one
   * implementation of a dependency can be registered.
   */
  private Map<Class<?>, DependencyImpl> registry;
  
  
  
  public PluginDependencyInjector() {
    this.registry = new HashMap<>();
  }
  
  
  
  /**
   * Registers a plugin-dependency an its implementation.
   * @param dependency     Class which can be looked up as a dependency. Usually
   *                       a interface or an abstract class. The type of a field
   *                       annotated with @PluginDependency has to match this
   *                       class.
   * @param dependencyImpl Class which implements the dependecy. If a plugin
   *                       asks for a dependency, the injector creates a new
   *                       instance of this class.
   * @param ctor_args      Optional. Pairs of <Class<?>, Object> which
   *                       represents the constructor to be called when creating
   *                       an instance of the dependencyImpl-class.
   */
  public void register(Class<?> dependency, Class<?> dependencyImpl,
                       ctor_arg... ctor_args) {
    if (!registry.containsKey(dependency)) {
      registry.put(dependency, new DependencyImpl(dependencyImpl, ctor_args));
      log.debug("registered plugin-dependency: '" + dependency.getSimpleName() +
                "' is implemented by " + dependencyImpl.getSimpleName(), true);
    } else {
      log.warn("dependency-registration failed: '" + dependency.getSimpleName() +
               "' was already registered", true);
    }
  }
  
  /**
   * Injects dependencies into a plugin. For each field of the plugin which is
   * annotated with @PluginDependency it will be checked, if the injector has a
   * matching dependency-implementation in the registry.
   * @param plugin       The plugin to inject the dependencies.
   */
  public void inject(Plugin<?> plugin) {
    for (Field _field : plugin.getClass().getDeclaredFields()) {
      PluginDependency annotation = _field.getAnnotation(PluginDependency.class);
      
      // if field is annotated -> try to inject dependency
      if (annotation != null) {
        String dependencyName = _field.getType().getSimpleName();
        // lookup in the registry
        DependencyImpl dependencyImpl = registry.get(_field.getType());
        
        if (dependencyImpl != null) {
          try {
            // get constructor which takes the matching arg-types
            Constructor<?> c = dependencyImpl.clazz.getConstructor(
                                                      dependencyImpl.argTypes
                                                    );
            // call constructor with matching arg-values
            _field.setAccessible(true);
            _field.set(plugin, c.newInstance(dependencyImpl.argValues));
            
            log.debug("injected dependency '" + dependencyName + "' into " +
                      "plugin " + plugin.config().getInstanceName());
            
          } catch (// thrown by getConstructor():
                   NoSuchMethodException | SecurityException |
                   // thrown by newInstance():
                   IllegalArgumentException | IllegalAccessException | 
                   InstantiationException | InvocationTargetException e) {
            log.error("could not inject dependency '" + dependencyName + "'" +
                      "into plugin " + plugin.config().getInstanceName(), e, true);
          }
        } else {
          log.warn("could not find a dependency implementation for '" +
                   dependencyName + "' in the registry", true);
        }
      }
    }
  }
  
  
  
  /**
   * Represents <Class<?>, Object> pairs, which is used in this context as the
   * argument-types and -values of the dependency-implementation constructor.
   */
  public static class ctor_arg {
    private Class<?> clazz;
    private Object   object;
    
    public ctor_arg(Class<?> clazz, Object object) {
      this.clazz  = clazz;
      this.object = object;
    }
  }
  
  /**
   * A DependencyImpl consists of a class which implements a dependency (the
   * dependency itself is not specified here; is the key of the registry-map)
   * and the arg-types and arg-values of the implementations constructor.
   */
  class DependencyImpl {
    
    /**
     * Class which implements the dependency.
     */
    private Class<?> clazz;
    
    /**
     * List with the class-types for the constructor-arguments of clazz.
     */
    private Class<?>[] argTypes;
    
    /**
     * List with the matching values for argTypes.
     */
    private Object[] argValues;
    
    public DependencyImpl(Class<?> clazz, ctor_arg[] ctor_args) {
      this.clazz     = clazz;
      this.argTypes  = new Class<?>[ctor_args.length];
      this.argValues = new Object  [ctor_args.length];
      
      for (int i = 0; i < ctor_args.length; ++i) {
        argTypes [i] = ctor_args[i].clazz;
        argValues[i] = ctor_args[i].object;
      }
    }
    
  }
  
}
