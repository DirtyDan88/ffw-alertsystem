/*
  Copyright (c) 2015-2017, Max Stark <max.stark88@web.de>
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

package net.dirtydan.ffw.alertsystem.monitor.plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import net.dirtydan.ffw.alertsystem.common.message.Message;
import net.dirtydan.ffw.alertsystem.common.util.DateAndTime;



public class DatabaseWriter extends MonitorPlugin {
  
  @Override
  protected void onReceivedMessage(Message message) {
    String dbDir  = config().paramList().get("database-dir").val() +
                    DateAndTime.getYearAndMonthName();
    String dbName = ""  + DateAndTime.getDate() + ".db";
    
    File databaseDir = new File(dbDir);
    databaseDir.mkdirs();
    
    Connection connection = null;
    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection(
                                   "jdbc:sqlite:" + dbDir + "/" + dbName
                                 );
    } catch (SQLException | ClassNotFoundException e) {
      errorOccured(e);
      return;
    }
    
    try {
      // create table if it not exists
      Statement sqlStmt = connection.createStatement();
      sqlStmt.executeUpdate(Message.getSqlCreateTable());
      sqlStmt.close();
      
      // insert the alert-message
      PreparedStatement sqlPStmt = connection.prepareStatement(
                                     Message.getSqlInsertMessage()
                                   );
      message.fillSqlStatement(sqlPStmt);
      sqlPStmt.executeUpdate();
      sqlPStmt.close();
      
      log.info("wrote message to database");
    } catch (SQLException e) {
      errorOccured(e);
      
    } finally {
      try {
        connection.close();
      } catch (SQLException e) {
        errorOccured(e);
      }
    }
  }
  
}
