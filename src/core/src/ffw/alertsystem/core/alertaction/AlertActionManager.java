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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import ffw.alertsystem.core.message.Message;
import ffw.alertsystem.core.plugin.Plugin;
import ffw.alertsystem.core.plugin.PluginManager;
import ffw.alertsystem.util.Logger;



/**
 * A @PluginManager which creates and controls @AlertAction objects.
 */
public class AlertActionManager extends PluginManager<AlertAction,
                                                      AlertActionConfig> {
  
  /**
   * Creates the XML-based config-source @AlertActionConfigFile for the
   * alert-actions.
   * 
   * @see @XMLPluginSource
   */
  public AlertActionManager(String xsdFileName, String configFileName,
                            String executerName, Logger log) {
    super(new AlertActionConfigFile(xsdFileName, configFileName, executerName,
                                    log), log);
  }
  
  
  
  /**
   * In case of an alert, this method starts the execution of all actions which
   * belongs to this action-manager.
   * @param message The @Message object which raised the alert.
   * 
   * @see {@link Plugin#start()}
   * @see {@link AlertAction#onPluginStart()}
   */
  public void executeAll(Message message) {
    // handover the message to each action
    plugins().forEach(action -> action.setMessage(message));
    // run all actions
    startAll();
  }
  
  
  
  @Override
  protected AlertAction newInstance(AlertActionConfig config) {
    AlertAction action;
    
    try {
      URL jarFile = new URL("jar", "", "file:" + "" + config.getJarFile() + "!/");
      ClassLoader loader = URLClassLoader.newInstance(
                             new URL[] { jarFile },
                             getClass().getClassLoader()
                           );
      
      action = (AlertAction) loader.loadClass(
                                      config.getPackageName() + "." + 
                                      config.getClassName()
                                    ).newInstance();
      
    } catch (MalformedURLException | IllegalAccessException | 
             InstantiationException | ClassNotFoundException e) {
      log.error("Could not create alert-action", e);
      return null;
      
    } catch (NoClassDefFoundError e) {
      log.error("could not create alert-action, maybe some dependencies " +
                "are missing?", e);
      return null;
    }
    
    return action;
  }
  
  @Override
  protected String pluginTypeName() {
    return "AlertAction";
  }
  
}
