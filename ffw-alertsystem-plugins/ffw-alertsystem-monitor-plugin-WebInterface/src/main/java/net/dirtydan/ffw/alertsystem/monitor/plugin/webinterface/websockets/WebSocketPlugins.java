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

package net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.websockets;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.dirtydan.ffw.alertsystem.common.plugin.Plugin;
import net.dirtydan.ffw.alertsystem.common.plugin.PluginConfig;
import net.dirtydan.ffw.alertsystem.common.plugin.PluginConfig.PluginParam;
import net.dirtydan.ffw.alertsystem.monitor.plugin.MonitorPlugin;
import net.dirtydan.ffw.alertsystem.monitor.plugin.MonitorPluginConfig;
import net.dirtydan.ffw.alertsystem.monitor.plugin.WebInterface;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.jetty.HtmlTemplate;


public class WebSocketPlugins extends WebSocketBase {
  
  private static final List<WebSocketBase> connections = new LinkedList<>();
  
  
  @Override
  protected String getName() {
    return "PluginsWebSocket";
  }
  
  @Override
  protected void onConnect() {
    connections.add(this);
    sendMessage(buildHtml());
  }
  
  @Override
  protected void onClose() {
    connections.remove(this);
  }
  
  @Override
  protected void onError(Throwable t) {
    connections.remove(this);
  }
  
  
  public static void updateAll() {
    connections.forEach(socket -> socket.sendMessage(buildHtml()));
  }
  
  private static HtmlTemplate template = new HtmlTemplate(
                                           "/web/templates/template-plugin.html"
                                         );
  
  private static String buildHtml() {
    StringBuilder html = new StringBuilder("");
    
    List<MonitorPluginConfig> configs = WebInterface.instance.status.getMonitorPluginConfigs();
    List<MonitorPlugin>       plugins = WebInterface.instance.status.getMonitorPlugins();
    
    html.append("<table width='100%'>");
    configs.forEach(config -> {
      buildPluginHtml(config, plugins);
      html.append("<tr><td>" + template.toString() + "</td></tr>");
    });
    html.append("</table>");
    
    return html.toString();
  }
  
  private static void buildPluginHtml(MonitorPluginConfig config,
                                      List<MonitorPlugin> plugins) {
    setIcons(config, plugins);
    template.setElement("#instance-name", config.getInstanceName());
    template.setElement("#type",          config.getClassName());
    template.setElement("#description",   config.getDescription());
    template.setElement("#ric-list",      getRicList(config.ricList()));
    template.setElement("#param-list",    getConfigParams(config.paramList()));
  }
  
  private static void setIcons(PluginConfig config, List<MonitorPlugin> plugins) {
    String statusIcon = "";
    String powerBtnTitle = "";
    String powerBtnFunction = "";
    String disabled = "";
    
    if (config.isActive()) {
      Plugin<?> plugin = plugins.stream()
                                .filter(p -> p.config().getInstanceName()
                                             .equals(config.getInstanceName()))
                                .findFirst()
                                .get();
      switch (plugin.state()) {
        case CREATED:
        case STOPPED:
          statusIcon = "<img src='images/state-inactive.png'" +
                       " title='Plugin ist nicht aktiv'>";
          powerBtnTitle = "starten";
          powerBtnFunction = "pluginActivate";
          break;
        case STARTED:
        case SLEEPING:
        case RELOADING:
        case RUNNING: 
          statusIcon = "<img src='images/state-active.png'" +
                       " title='Plugin ist aktiv'>";
          powerBtnTitle = "stoppen";
          powerBtnFunction = "pluginDeactivate";
          break;
        case ERROR:
          String errorMessage = plugin.getError().getMessage();
          statusIcon = "<img src='images/state-error.png'" +
                       " title='Fehler aufgetreten: " + errorMessage + "'>";
          powerBtnTitle = "stoppen";
          powerBtnFunction = "pluginDeactivate";
          break;
        default: break;
      }
      
      if (plugin.config().getInstanceName().equals(
          WebInterface.instance.config().getInstanceName())) {
        disabled = " class='disabled'";
      }
    
    } else {
      statusIcon = "<img src='images/state-inactive.png'" +
                   " title='Plugin ist nicht aktiv'>";
      powerBtnTitle = "starten";
      powerBtnFunction = "pluginActivate";
    }
    
    template.setElement("#status-icon", statusIcon);
    template.setElement("#button-power",
      "<img src='images/btn-power.png' title='Plugin " + powerBtnTitle + "'" +
      " onclick='" + powerBtnFunction + "(\"" + config.getInstanceName() +
      "\");'" + disabled + ">");
    template.setElement("#button-restart",
      "<img src='images/btn-restart.png' title='Plugin neu starten'" +
      " onclick='pluginRestart(\"" + config.getInstanceName() +
      "\");'" + disabled + ">");
  }
  
  private static String getRicList(List<String> rics) {
    String ricList;
    
    if (!rics.isEmpty()) {
      StringBuilder s = new StringBuilder("");
      rics.forEach(ric -> {
        if (ric.equals("*")) {
          s.append("alle, ");
        } else {
          s.append(ric + ", ");
        }
      });
      
      ricList = s.substring(0, s.length() - 2);
    } else {
      ricList = "-";
    }
    
    return ricList;
  }
  
  private static String getConfigParams(Map<String, PluginParam> params) {
    StringBuilder paramHtml = new StringBuilder("<table>");
    
    params.forEach((paramKey, param) -> {
      if (!param.hide()) {
        paramHtml.append(
          "<tr><td><b>" + paramKey + ":</b> " + param.val() + "</td></tr>"
        );
      }
    });
    paramHtml.append("</table>");
    
    return paramHtml.toString();
  }
  
}
