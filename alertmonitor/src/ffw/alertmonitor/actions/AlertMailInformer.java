/*
  Copyright (c) 2015, Max Stark <max.stark88@web.de> 
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

package ffw.alertmonitor.actions;

import ffw.alertmonitor.AlertAction;
import ffw.util.Mail;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class AlertMailInformer extends AlertAction {
  
  @Override
  public String getInfo() {
      return "sends an eMail to given recipients";
  }
  
  @Override
  public void run() {
    String userName   = paramList.get("email-address");
    String passWord   = paramList.get("email-password");
    String recipients = paramList.get("email-alert-recipients");
    
    String subject = "[ffw-alertsystem] !! ALARM !! ";
    String text    = message.buildText();
    
    Mail.send(userName, passWord, recipients, subject, text, 
              Application.WATCHDOG);
    ApplicationLogger.log("## send alert mail to: " + recipients, 
                          Application.ALERTMONITOR, false);
  }
}
