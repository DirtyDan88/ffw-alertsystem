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

import ffw.util.ConfigReader;
import ffw.util.DateAndTime;
import ffw.util.Mail;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;

public class AlertMailInformer extends AlertAction {
    
    @Override
    public String getDescription() {
        return "sends an eMail to given recipients";
    }
    
    @Override
    public void run() {
      String userName   = ConfigReader.getConfigVar("email-address");
      String passWord   = ConfigReader.getConfigVar("email-password");
      String recipients = ConfigReader.getConfigVar("email-alert-recipients");
      
      String subject    = "[ffw-alertsystem] !! ALARM !! ";
      String text       = "Alarm eingegangen am " + DateAndTime.get() + "\n"
                        + "Kurzstichwort: " + this.message.getAlertSymbol()
                                            + this.message.getAlertLevel()
                                            + " " 
                                            + this.message.getAlertKeyword() 
                                            + "\n\n"
                        + "Weitere Einsatzstichwoerter: \n";
      for (int i = 0; i < this.message.getKeywords().size(); i++) {
        text += this.message.getKeywords().get(i) + "\n";
      }
      
      Mail.send(userName, passWord, recipients, subject, text, 
                Application.WATCHDOG);
      ApplicationLogger.log("## send alert mail to: " + recipients, 
                            Application.ALERTMONITOR, false);
    }
}
