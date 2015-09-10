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

package ffw.util;

import java.io.File;
import java.sql.*;

import ffw.util.DateAndTime;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class SQLiteConnection {
  
  public String pathSQLCommands = "data/sql-commands/";
  private Connection connection;
  
  public void openCurrent() {
    String dbDir  = "data/" + DateAndTime.getYearAndMonthName();
    String dbName = "" + DateAndTime.getDate() + ".db";
    
    open(dbDir, dbName);
  }
  
  public void open(String dbDir, String dbName) {
    try {
      File databaseDir = new File(dbDir);
      databaseDir.mkdirs();
    } catch (Exception e) {
      ApplicationLogger.log("## ERROR: could not create database dir", 
                            Application.ALERTMONITOR, false);
    }
    
    open(dbDir + "/" + dbName);
  }
  
  public void open(String db) {
    connection = null;
    
    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:" + db);
    } catch (Exception e) {
      ApplicationLogger.log("## ERROR during database connection opening", 
                            Application.ALERTMONITOR, false);
    }
  }
  
  public Connection getHandle() {
    return connection;
  }
  
  
  public void close() {
    try {
      connection.close();
      connection = null;
    } catch (Exception e) {
      ApplicationLogger.log("## ERROR during database closing", 
                            Application.ALERTMONITOR, false);
    }
  }
}
