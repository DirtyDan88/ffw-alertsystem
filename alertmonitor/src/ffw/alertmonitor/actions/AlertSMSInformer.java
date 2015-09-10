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
import ffw.util.SQLiteConnection;
import ffw.util.TwilioSMS;
import ffw.util.TwilioSMS.TwilioAccount;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class AlertSMSInformer extends AlertAction {
  
  @Override
  public String getInfo() {
    return "sends via twilio account an SMS to given recipients";
  }
  
  @Override
  public void run() {
    SQLiteConnection con = new SQLiteConnection();
    con.open(paramList.get("database"));
    
    String text = "[ffw-alertsystem] !! ALARM !! \n" + message.buildText();
    String[] recipients = paramList.get("sms-alert-recipients").split(",");
    for (int i = 0; i < recipients.length; i++) {
      String id = recipients[i].trim();
      TwilioAccount acc = TwilioSMS.getTwilioAccFromDB(con, id);
      
      if (acc != null) {
        boolean ok = TwilioSMS.send(acc, text);
        
        if (ok) {
          ApplicationLogger.log("## sent SMS to " + acc.surName + acc.foreName, 
                                Application.ALERTMONITOR, false);
        }
      } else {
        ApplicationLogger.log("## ERROR: no twilo account with id " + id + " " +
                              "in database", 
                              Application.ALERTMONITOR, false);
      }
    }
    
    con.close();
  }
}