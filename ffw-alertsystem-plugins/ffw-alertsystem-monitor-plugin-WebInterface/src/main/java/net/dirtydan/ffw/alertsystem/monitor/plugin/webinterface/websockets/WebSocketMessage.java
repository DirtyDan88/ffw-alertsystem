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

import net.dirtydan.ffw.alertsystem.common.message.Message;
import net.dirtydan.ffw.alertsystem.common.util.DateAndTime;
import net.dirtydan.ffw.alertsystem.monitor.plugin.WebInterface;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.jetty.HtmlTemplate;


// TODO: test if socket is accessible without user and pw


public class WebSocketMessage extends WebSocketBase {
  
  private static final List<WebSocketMessage> connections = new LinkedList<>();
  
  public boolean onlyValidMessages = false;
  
  
  @Override
  protected String getName() {
    return "MessageWebSocket";
  }
  
  @Override
  protected void onConnect() {
    connections.add(this);
    sendMessage(buildHtml(null, onlyValidMessages));
  }
  
  @Override
  protected void onClose() {
    connections.remove(this);
  }
  
  @Override
  protected void onError(Throwable t) {
    connections.remove(this);
  }
  
  @Override
  protected void onText(String text) {
    if (text.equals("ONLY_VALID_MESSAGES")) {
      onlyValidMessages = true;
    } else if (text.equals("ALL_MESSAGES")) {
      onlyValidMessages = false;
    }
    
    sendMessage(buildHtml(null, onlyValidMessages));
  }
  
  
  public static void updateAll(Message message) {
    connections.forEach(socket -> {
      socket.sendMessage(buildHtml(message, socket.onlyValidMessages));
    });
  }
  
  private static HtmlTemplate template = new HtmlTemplate(
                                          "/web/templates/template-message.html"
                                        );
  
  private static String buildHtml(Message message, boolean onlyValidMessages) {
    StringBuilder html = new StringBuilder("");
    
    // the just received message
    if (message != null) {
      if (!onlyValidMessages || (onlyValidMessages && message.isValid())) {
        buildMessageHtml(message);
        template.addClass("#message", "just-received");
        html.append(template.toString());
        template.removeClass("#message", "just-received");
      }
    }
    
    // the list with previous received messages
    List<Message> prevMessages;
    if (onlyValidMessages) {
      prevMessages = WebInterface.instance.prevValidMessages();
    } else {
      prevMessages = WebInterface.instance.prevMessages();
    }
    
    for (int i = prevMessages.size() - 1; i >= 0; --i) {
      buildMessageHtml(prevMessages.get(i));
      html.append(template.toString());
    }
    
    return html.toString();
  }
  
  private static void buildMessageHtml(Message message) {
    template.setElement("#time", "[" + DateAndTime.get(message.getTimestamp()) + "]");
    template.setElement("#ric", message.getAddress());
    
    if (!message.isValid()) {
      template.addClass("#message", "invalid");
    } else {
      template.removeClass("#message", "invalid");
    }
    
//    String icon;
//    if (m.isValid()) {
//      icon = "<img width='16' src='images/message-valid.png' " +
//               "title='Nachricht gueltig'>";
//    } else {
//      icon = "<img width='16' src='images/message-invalid.png' " +
//               "title='Nachricht ungueltig'>";
//    }
//    template.setElement("#valid", icon);
    
//    m.getFurtherPlaceDescAsString()
//    m.getKeywordsAsString()
    
    String alpha = message.getAlpha();
    if (alpha != null) {
      alpha.replace("<", "&lt;"); alpha.replace(">", "&gt;");
      template.setElement("#alpha", alpha);
    }
  }
  
}
