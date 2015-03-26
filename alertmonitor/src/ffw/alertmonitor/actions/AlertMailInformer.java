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

import ffw.util.ApplicationLogger;
import ffw.util.ConfigReader;
import ffw.util.DateAndTime;
import ffw.util.Mail;
import ffw.util.ApplicationLogger.Application;

public class AlertMailInformer extends AlertAction {
    
    @Override
    public String getDescription() {
        return "mail-module";
    }
    
    @Override
    public void run() {
        String userName   = "ffw-moe-geraetehaus@web.de";
        String passWord   = "R8A825Tm";
        String recipients = ConfigReader.getConfigVar("alert-recipients");
        
        String subject    = "[ffw-alertsystem] !! ALARM !! ";
        String text       = "Alarm eingegangen am " + DateAndTime.get() + "\n"
                          + "Kurzstichwort: " + this.message.getShortKeyword() 
                                              + this.message.getAlertLevel() + "\n\n"
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
