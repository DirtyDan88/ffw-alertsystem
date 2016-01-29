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

package ffw.alertsystem.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;



/**
 * Uses the Twilio-API to send SMS.
 */
public class TwilioSMS {
  
  //TODO: check if text is > 160 chars?
  public static boolean send(TwilioAccount acc, String text) {
    try {
      TwilioRestClient client = new TwilioRestClient(
                                  acc.ACCOUNT_SID, 
                                  acc.AUTH_TOKEN
                                );
      
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("Body", text));
      params.add(new BasicNameValuePair("To", acc.To));
      params.add(new BasicNameValuePair("From", acc.From));
   
      MessageFactory messageFactory = client.getAccount().getMessageFactory();
      messageFactory.create(params);
      
    } catch (TwilioRestException e) {
      return false;
    }
    
    return true;
  }
  
  public static TwilioAccount getTwilioAccFromDB(SQLiteConnection con, 
                                                 String id) {
    TwilioAccount acc = null;
    
    try {
      String sqlString = FileReader.getContent("data/twilio/sql-selectTwilioSMS.sql");
      if (sqlString == null) {
        return null;
      }
      
      PreparedStatement sqlPStmt = con.getHandle().prepareStatement(sqlString);
      sqlPStmt.setString(1, id);
      
      ResultSet rs = sqlPStmt.executeQuery();
      while (rs.next()) {
        acc = new TwilioAccount(
          rs.getString("surName"),
          rs.getString("foreName"),
          rs.getString("ACCOUNT_SID"),
          rs.getString("AUTH_TOKEN"), 
          rs.getString("to"), 
          rs.getString("from")
        );
      }
      
      sqlPStmt.close();
      
    } catch (Exception e) {
      return null;
    }
    
    return acc;
  }
  
  public static TwilioAccount getTwilioAccFromTextFile(String fileName) {
    ConfigReader config = new ConfigReader(fileName);
    
    return new TwilioAccount(
      config.getConfigVar("surName"),
      config.getConfigVar("foreName"),
      config.getConfigVar("ACCOUNT_SID"),
      config.getConfigVar("AUTH_TOKEN"),
      config.getConfigVar("To"),
      config.getConfigVar("From")
    );
  }
  
  
  
  public static class TwilioAccount {
    public String surName  = null;
    public String foreName = null;
    
    public String ACCOUNT_SID = null;
    public String AUTH_TOKEN  = null;
    
    public String To   = null;
    public String From = null;
    
    public TwilioAccount(String surName, 
                         String foreName,
                         String ACCOUNT_SID,
                         String AUTH_TOKEN,
                         String To, 
                         String From) {
      this.surName  = surName;
      this.foreName = foreName;
      
      this.ACCOUNT_SID = ACCOUNT_SID;
      this.AUTH_TOKEN  = AUTH_TOKEN;
      
      this.To   = To;
      this.From = From;
    }
  }
  
}
