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

import java.io.File;
import java.sql.*;



/**
 * Provides methods to opens and close SQLite-database-connections.
 */
public class SQLiteConnection {
  
  private Connection con;
  
  private final Logger log;
  
  public SQLiteConnection(Logger log) {
    this.log = log;
  }
  
  
  
  public void openCurrent(String dir) {
    String dbDir  = dir + DateAndTime.getYearAndMonthName();
    String dbName = ""  + DateAndTime.getDate() + ".db";
    
    open(dbDir, dbName);
  }
  
  public void open(String dbDir, String dbName) {
    File databaseDir = new File(dbDir);
    databaseDir.mkdirs();
    
    open(dbDir + "/" + dbName);
  }
  
  public boolean open(String db) {
    con = null;
    
    try {
      Class.forName("org.sqlite.JDBC");
      con = DriverManager.getConnection("jdbc:sqlite:" + db);
    } catch (Exception e) {
      log.error("could not open database: " + db, e, true);
      return false;
    }
    
    return true;
  }
  
  public Connection getHandle() {
    return con;
  }
  
  public boolean close() {
    try {
      con.close();
      con = null;
    } catch (Exception e) {
      log.error("could not close database", e, true);
      return false;
    }
    
    return true;
  }
  
}
