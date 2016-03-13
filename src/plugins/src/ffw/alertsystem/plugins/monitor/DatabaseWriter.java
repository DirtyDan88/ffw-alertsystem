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

import java.sql.PreparedStatement;
import java.sql.Statement;

import ffw.alertsystem.core.message.Message;
import ffw.alertsystem.core.monitor.MonitorPlugin;
import ffw.alertsystem.util.SQLiteConnection;



public class DatabaseWriter extends MonitorPlugin {
  
  @Override
  protected void onReceivedMessage(Message message) {
    SQLiteConnection con = new SQLiteConnection(log);
    con.openCurrent(config().paramList().get("database-dir"));
    
    Statement         sqlStmt  = null;
    PreparedStatement sqlPStmt = null;
    
    try {
      // create table if it not exists
      sqlStmt = con.getHandle().createStatement();
      sqlStmt.executeUpdate(Message.getSqlCreateTable());
      sqlStmt.close();
      
      // insert the alert-message
      sqlPStmt = con.getHandle().prepareStatement(Message.getSqlInsertMessage());
      message.fillSqlStatement(sqlPStmt);
      
      sqlPStmt.executeUpdate();
      sqlPStmt.close();
      
      log.info("wrote message to database");
      
    } catch (Exception e) {
      log.error("could not write message to database", e);
    }
    
    con.close();
  }
  
}
