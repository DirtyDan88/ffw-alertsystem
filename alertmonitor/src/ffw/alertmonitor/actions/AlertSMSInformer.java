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

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;

import ffw.util.ConfigReader;
import ffw.util.DateAndTime;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class AlertSMSInformer extends AlertAction {
    
    @Override
    public String getDescription() {
        return "sends an SMS to given recipients";
    }
    
    @Override
    public void run() {
        ConfigReader.fileName = "data/twilio.txt";
        String ACCOUNT_SID = ConfigReader.getConfigVar("ACCOUNT_SID");
        String AUTH_TOKEN  = ConfigReader.getConfigVar("AUTH_TOKEN");
        String To          = ConfigReader.getConfigVar("To");
        String From        = ConfigReader.getConfigVar("From");
        
        // TODO: changes this when config file is read in via command line
        ConfigReader.fileName = "config.txt";
        
        try {
            TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
            
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Body", this.buildText()));
            params.add(new BasicNameValuePair("To", To));
            params.add(new BasicNameValuePair("From", From));
         
            MessageFactory messageFactory = client.getAccount().getMessageFactory();
            messageFactory.create(params);
            
        } catch (TwilioRestException e) {
            ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                  Application.ALERTMONITOR);
        }
    }
    
    private String buildText() {
        String text = "[ffw-alertsystem] !! ALARM !! "
                    + "Alarm eingegangen am " + DateAndTime.get() + "\n"
                    + "Kurzstichwort: " + this.message.getAlertKeyword() 
                    + this.message.getAlertLevel() + "\n\n"
                    + "Weitere Einsatzstichwoerter: \n";
        for (int i = 0; i < this.message.getKeywords().size(); i++) {
            text += this.message.getKeywords().get(i) + "\n";
        }
        
        return text;
    }
}