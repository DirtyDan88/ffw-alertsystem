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

package ffw.alertsystem.plugins.alertaction;

import ffw.alertsystem.core.alertaction.AlertAction;
import ffw.alertsystem.core.message.Message;
import ffw.alertsystem.util.Mail;



public class AlertMailInformer extends AlertAction {
  
  @Override
  protected void execute(Message message) {
    String userName   = config().paramList().get("email-address");
    String passWord   = config().paramList().get("email-password");
    String recipients = config().paramList().get("email-alert-recipients");
    
    String subject = "[ffw-alertsystem] !! ALARM !! ";
    String text    = message.buildText();
    
    Mail.send(userName, passWord, recipients, subject, text, log);
    log.info("sent alert mail to: " + recipients);
  }
  
}
