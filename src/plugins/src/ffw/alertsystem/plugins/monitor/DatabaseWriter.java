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

package ffw.alertsystem.plugins.monitor;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.Statement;

import ffw.alertsystem.core.message.Message;
import ffw.alertsystem.core.monitor.MonitorPlugin;
import ffw.alertsystem.util.FileReader;
import ffw.alertsystem.util.SQLiteConnection;



public class DatabaseWriter extends MonitorPlugin {
  
  private String sqlStringCreateTable;
  
  private String sqlStringInsertMessage;
  
  
  
  @Override
  protected void onMonitorPluginStart() {
    loadSQLStrings();
  }
  
  @Override
  protected void onMonitorPluginReload() {
    loadSQLStrings();
  }
  
  @Override
  protected void onReceivedMessage(Message message) {
    SQLiteConnection con = new SQLiteConnection();
    con.openCurrent(config().paramList().get("database-dir"));
    
    Statement         sqlStmt  = null;
    PreparedStatement sqlPStmt = null;
    
    try {
      // create table if it not exists
      sqlStmt = con.getHandle().createStatement();
      sqlStmt.executeUpdate(sqlStringCreateTable);
      sqlStmt.close();
      
      // insert the alert-message
      sqlPStmt = con.getHandle().prepareStatement(sqlStringInsertMessage);
      
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
      
      log.info("wrote message to database");
      
    } catch (Exception e) {
      log.error("could not write message to database", e);
    }
    
    con.close();
  }
  
  
  
  private void loadSQLStrings() {
    log.info("reading SQL-strings ...");
    
    try {
      InputStream in = getClass().getResourceAsStream("../../../../sql-createAlertMessageTable.sql");
      sqlStringCreateTable = FileReader.getContent(new InputStreamReader(in));
      
      if (sqlStringCreateTable == null) {
          throw new Exception("could not get sql-string from file " +
                              "sql-createAlertMessageTable.sql");
      }
      
      in = getClass().getResourceAsStream("../../../../sql-insertAlertMessage.sql");
      sqlStringInsertMessage = FileReader.getContent(new InputStreamReader(in));
      
      if (sqlStringInsertMessage == null) {
        throw new Exception("could not get sql-string from file " +
                            "sql-insertAlertMessage.sql");
      }
      
    } catch (Exception e) {
      log.error("could not read SQL-string", e);
      return;
    }
    
    log.info("done!");
  }
  
}
