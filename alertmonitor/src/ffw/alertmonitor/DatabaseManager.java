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

package ffw.alertmonitor;

import java.io.File;
import java.sql.*;

import ffw.alertlistener.AlertMessage;
import ffw.util.DateAndTime;
import ffw.util.FileReader;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class DatabaseManager {
  private static Connection connection;
  
  public static void connectToCurrent() {
    String dbDir  = "data/" + DateAndTime.getYearAndMonthName();
    String dbName = "" + DateAndTime.getDate() + ".db";
    
    connect(dbDir, dbName);
  }
  
  public static void connect(String dbDir, String dbName) {
    connection = null;
    
    try {
      File databaseDir = new File(dbDir);
      databaseDir.mkdirs();
      String databaseFilename = dbName;
      
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:" + 
                                               databaseDir + "/" + 
                                               databaseFilename);
      
    } catch (Exception e) {
      ApplicationLogger.log("## ERROR during database connection opening", 
                            Application.ALERTMONITOR, false);
    }
  }
  
  public static void insertAlertMessage(AlertMessage alertMessage) {
    Statement         sqlStmt  = null;
    PreparedStatement sqlPStmt = null;
    
    try {
      /* Create table if it not exist */
      sqlStmt = connection.createStatement();
      sqlStmt.executeUpdate(
        FileReader.getContent(
          "data/sql-createAlertMessageTable.sql", 
          Application.ALERTMONITOR
        )
      );
      sqlStmt.close();
      
      /* Insert the alert-message */
      sqlPStmt = connection.prepareStatement(
        FileReader.getContent(
          "data/sql-insertAlertMessage.sql", 
          Application.ALERTMONITOR
        )
      );
      
      sqlPStmt.setString( 1, alertMessage.getTimestamp());
      sqlPStmt.setString( 2, alertMessage.getAddress());
      sqlPStmt.setString( 3, alertMessage.getFunction());
      sqlPStmt.setInt   ( 4, ((alertMessage.isComplete()) ? 1 : 0));
      sqlPStmt.setInt   ( 5, ((alertMessage.isEncrypted()) ? 1 : 0));
      sqlPStmt.setInt   ( 6, ((alertMessage.isTestAlert()) ? 1 : 0));
      sqlPStmt.setInt   ( 7, ((alertMessage.hasCoordinates()) ? 1 : 0));
      sqlPStmt.setString( 8, alertMessage.getLatitude());
      sqlPStmt.setString( 9, alertMessage.getLongitude());
      sqlPStmt.setString(10, alertMessage.getStreet());
      sqlPStmt.setString(11, alertMessage.getVillage());
      sqlPStmt.setString(12, alertMessage.getAlertNumber());
      sqlPStmt.setString(13, alertMessage.getShortKeyword());
      sqlPStmt.setString(14, alertMessage.getAlertLevel());
      sqlPStmt.setString(15, alertMessage.getMessageString());
      
      sqlPStmt.executeUpdate();
      sqlPStmt.close();
      
    } catch (Exception e) {
      ApplicationLogger.log("## ERROR during database writing", 
                            Application.ALERTMONITOR, false);
    }
  }
  
  public static void close() {
    try {
      connection.close();
    } catch (Exception e) {
      ApplicationLogger.log("## ERROR during database closing", 
                            Application.ALERTMONITOR, false);
    }
  }
}
