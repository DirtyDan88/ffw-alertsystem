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
import ffw.alertsystem.util.SQLiteConnection;
import ffw.alertsystem.util.TwilioSMS;
import ffw.alertsystem.util.TwilioSMS.TwilioAccount;



public class AlertSMSInformer extends AlertAction {
  
  @Override
  protected void execute(Message message) {
    if (config().paramList().get("source").equals("text-files")) {
      loadFromTextFiles(message);
    } else if (config().paramList().get("source").equals("database")) {
      loadFromDB(message);
    }
  }
  
  private void loadFromTextFiles(Message message) {
    String[] textFiles = config().paramList().get("file-list").split(",");
    String text = "[ffw-alertsystem] !! ALARM !! \n" + message.buildShortText();
    
    for (int i = 0; i < textFiles.length; i++) {
      String fileName = textFiles[i].trim();
      if (fileName.equals("")) continue;
      TwilioAccount acc = TwilioSMS.getTwilioAccFromTextFile(fileName);
      
      if (acc != null) {
        boolean ok = TwilioSMS.send(acc, text);
        
        if (ok) {
          log.info("sent SMS to " + acc.surName + acc.foreName);
        } else {
          log.error("TwilioRestException occurred when try to send SMS to " +
                      acc.surName + ", " + acc.foreName, new Exception());
        }
      } else {
        log.error("could not find twilio account in file '" + fileName + "'",
                    new Exception());
      }
    }
  }
  
  private void loadFromDB(Message message) {
    SQLiteConnection con = new SQLiteConnection();
    con.open(config().paramList().get("database"));
    
    String text = "[ffw-alertsystem] !! ALARM !! \n" + message.buildShortText();
    String[] recipients = config().paramList().get("sms-alert-recipients").split(",");
    
    for (int i = 0; i < recipients.length; i++) {
      String id = recipients[i].trim();
      TwilioAccount acc = TwilioSMS.getTwilioAccFromDB(con, id);
      
      if (acc != null) {
        boolean ok = TwilioSMS.send(acc, text);
        
        if (ok) {
          log.info("sent SMS to " + acc.surName + ", " + acc.foreName);
        } else {
          log.error("TwilioRestException occurred when try to send SMS to " +
                      acc.surName + ", " + acc.foreName, new Exception());
        }
      } else {
        log.error("could not find twilio account in database with id '" + 
                    id + "'", new Exception());
      }
    }
    
    con.close();
  }
  
}