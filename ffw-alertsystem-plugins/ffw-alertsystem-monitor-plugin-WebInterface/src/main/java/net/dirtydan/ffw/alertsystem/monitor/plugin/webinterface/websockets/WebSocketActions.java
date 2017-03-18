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

import net.dirtydan.ffw.alertsystem.monitor.action.RemoteAction;
import net.dirtydan.ffw.alertsystem.monitor.plugin.WebInterface;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.jetty.HtmlTemplate;


public class WebSocketActions extends WebSocketBase {
  
  private static final List<WebSocketBase> connections = new LinkedList<>();
  
  
  @Override
  protected String getName() {
    return "ActionsWebSocket";
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
                                           "/web/templates/template-action.html"
                                         );

  private static String buildHtml() {
    StringBuilder html = new StringBuilder("");
    
    List<RemoteAction> actions = WebInterface.instance.status.getRemoteActions();
    
    html.append("<table width='100%'>");
    actions.forEach(action -> {
      buildActionHtml(action);
      html.append("<tr><td>" + template.toString() + "</td></tr>");
    });
    html.append("</table>");
    
    return html.toString();
  }
  
  private static void buildActionHtml(RemoteAction action) {
    setStatusIcon(action);
    
    template.setElement("#action-name", action.config().getName());
    template.setElement("#heartbeat-interval", String.valueOf(action.config().getHeartbeat()) + "s");
    
    template.setElement("#ric-list",    getRicList(action.config().getRicList()));
    template.setElement("#action-host", action.config().getHost() + ":" + String.valueOf(action.config().getPort()));
    template.setElement("#last-heartbeat", "[" + String.valueOf(action.getLastHeartbeat()) + "]");
    template.setElement("#last-execution", "[" + String.valueOf(action.getLastExecTime()) + "]");
    
    // TODO: action desc
    template.setElement("#description", "-");
  }
  
  private static void setStatusIcon(RemoteAction action) {
    String statusIcon = "";
    String powerBtnTitle = "";
    String powerBtnFunction = "";
    boolean reconnectBtn = false;
    
    if (action.config().isActive()) {
      switch (action.getState()) {
        case DISCONNECTED:
          statusIcon = "<img src='images/state-error.png'" +
                       " title='remote-action is DISCONNECTED'>";
          reconnectBtn = true;
          break;
        case TERMINATED:
          statusIcon = "<img src='images/state-error.png'" +
                       " title='remote-action TERMINATED'>";
          reconnectBtn = true;
          break;
        case UNKNOWN:
          statusIcon = "<img src='images/state-error.png'" +
                       " title='remote-action state is UNKNOWN'>";
          reconnectBtn = true;
          break;
        case CONNECTED:
          statusIcon = "<img src='images/state-connected.png'" +
                       " title='remote-action is connected and ready to receive messages'>";
          break;
        case RUNNING:
          statusIcon = "<img src='images/state-active.png'" +
                       " title='remote-action is executing'>";
          break;
        default: break;
      }
      
      powerBtnTitle = "deaktivieren";
      powerBtnFunction = "actionDeactivate";
      
    } else {
      statusIcon = "<img src='images/state-inactive.png'" +
                   " title='RemoteAction ist nicht aktiv'>";
      powerBtnTitle = "aktivieren";
      powerBtnFunction = "actionActivate";
    }
    
    template.setElement("#status-icon", statusIcon);
    template.setElement("#button-power",
        "<img src='images/btn-power.png' title='RemoteAction " + powerBtnTitle + "'" +
        " onclick='" + powerBtnFunction + "(\"" + action.config().getName() + "\");'>");
    if (reconnectBtn) {
      template.setElement("#button-reconnect",
          "<img src='images/btn-restart.png' title='try to reconnect'" +
          " onclick='actionReconnect(\"" + action.config().getName() + "\");'>");
    } else {
      template.setElement("#button-reconnect",
          "<img src='images/btn-restart.png' class='disabled'>");
    }
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
  
}
