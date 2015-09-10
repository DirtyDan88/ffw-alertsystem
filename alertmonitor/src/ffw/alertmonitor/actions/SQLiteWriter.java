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

import java.sql.PreparedStatement;
import java.sql.Statement;

import ffw.alertmonitor.AlertAction;
import ffw.util.FileReader;
import ffw.util.SQLiteConnection;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;

public class SQLiteWriter extends AlertAction {
  
  @Override
  public String getInfo() {
    return "writes alert-message to database";
  }
  
  @Override
  public void run() {
    SQLiteConnection con = new SQLiteConnection();
    con.openCurrent();
    
    Statement         sqlStmt  = null;
    PreparedStatement sqlPStmt = null;
    
    try {
      /* Create table if it not exist */
      sqlStmt = con.getHandle().createStatement();
      sqlStmt.executeUpdate(
        FileReader.getContent(
          con.pathSQLCommands + "sql-createAlertMessageTable.sql",
          Application.ALERTMONITOR
        )
      );
      sqlStmt.close();
      
      /* Insert the alert-message */
      sqlPStmt = con.getHandle().prepareStatement(
        FileReader.getContent(
          con.pathSQLCommands + "sql-insertAlertMessage.sql", 
          Application.ALERTMONITOR
        )
      );
      
      sqlPStmt.setString( 1, message.getTimestamp());
      sqlPStmt.setString( 2, message.getAddress());
      sqlPStmt.setString( 3, message.getFunction());
      
      sqlPStmt.setInt   ( 4, ((message.isComplete())           ? 1 : 0));
      sqlPStmt.setInt   ( 5, ((message.isEncrypted())          ? 1 : 0));
      sqlPStmt.setInt   ( 6, ((message.isTestAlert())          ? 1 : 0));
      sqlPStmt.setInt   ( 7, ((message.isFireAlert())          ? 1 : 0));
      sqlPStmt.setInt   ( 8, ((message.isUnknownMessageType()) ? 1 : 0));
      
      sqlPStmt.setString( 9, message.getAlertNumber());
      sqlPStmt.setString(10, message.getAlertSymbol());
      sqlPStmt.setString(11, message.getAlertLevel());
      sqlPStmt.setString(12, message.getAlertKeyword());
      
      sqlPStmt.setInt   (13, ((message.hasCoordinates()) ? 1 : 0));
      sqlPStmt.setString(14, message.getLatitude());
      sqlPStmt.setString(15, message.getLongitude());
      sqlPStmt.setString(16, message.getStreet());
      sqlPStmt.setString(17, message.getVillage());
      sqlPStmt.setString(18, message.getFurtherPlaceDescAsString());
      
      sqlPStmt.setString(19, message.getKeywordsAsString());
      sqlPStmt.setString(20, message.getMessageString());
      
      sqlPStmt.executeUpdate();
      sqlPStmt.close();
      
      ApplicationLogger.log("## saved alert-message to DB", 
                            Application.ALERTMONITOR, false);
      
    } catch (Exception e) {
      ApplicationLogger.log("## ERROR during database writing", 
                            Application.ALERTMONITOR, false);
    }
    
    con.close();
  }
}
