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

package ffw.alertsystem.tools.log2db;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ffw.alertsystem.core.Application;
import ffw.alertsystem.core.message.Message;
import ffw.alertsystem.core.message.MessageFactory;
import ffw.alertsystem.util.DateAndTime;
import ffw.alertsystem.util.SQLiteConnection;



/**
 * Reads log-files generated from MonitorPlugin @MessageLogger and writes them
 * to database-files.
 */
public class Log2DB extends Application {
  
  public static void main(String[] args) {
    new Log2DB(args).start();
  }
  
  public Log2DB(String[] args) {
    super(Application.ApplicationType.Log2DB, args);
  }
  
  
  
  private int numLogFiles = 0;
  
  private int numMessages = 0;
  
  private String databaseDir;
  
  private List<String> ricList;
  
  private boolean useInvalidMessages;
  
  private boolean useMessageCopies;
  
  private List<Message> prevMessages = new LinkedList<Message>() {
    private static final long serialVersionUID = 1L;
    
    @Override
    public boolean add(Message message) {
      if (size() >= 50) {
        super.removeFirst();
      }
      
      return super.add(message);
    }
  };
  
  
  
  @Override
  protected void onApplicationStarted() {
    log.info("log2db converter started", true);
    Thread.currentThread().setUncaughtExceptionHandler(errHandler);
    
    databaseDir = config.getParam("database-dir");
    
    ricList = Arrays.asList(config.getParam("rics").split(","));
    ricList.replaceAll(ric -> ric.trim());
    
    useInvalidMessages = config.getParam("use-invalid-messages")
                           .equals("true") ? true : false;
    useMessageCopies   = config.getParam("use-message-copies")
                           .equals("true") ? true : false;
    
    // start recursive scanning
    scanDirectory(new File(config.getParam("logfile-dir")));
    
    System.exit(0);
  }
  
  @Override
  protected void onApplicationStopped() {
    log.info("Read " + numLogFiles + " log-files " +
             "with " + numMessages + " messages", true);
    log.info("log2db converter finished", true);
  }
  
  @Override
  protected void onApplicationErrorOccured(Throwable t) {
    System.exit(0);
  }
  
  
  
  private void scanDirectory(File directory) {
    log.info("enter directory " + directory.getPath(), true);
    
    for (String curFileName : directory.list()) {
      File curFile = new File(directory.getPath() + "/" + curFileName);
      
      if (curFile.isDirectory()) {
        scanDirectory(curFile);
        
      } else {
        if (  curFileName.endsWith(".txt") &&
            (!curFileName.endsWith("alertmonitor.txt") &&
             !curFileName.endsWith("watchdog.txt") &&
             !curFileName.endsWith("alertlistener.txt")) ||
              curFileName.endsWith("messages.txt")) {
          readLogFile(directory.getPath(), curFileName);
        }
      }
    }
  }
  
  private void readLogFile(String path, String fileName) {
    log.info("open log-file " + fileName);
    ++numLogFiles;
    
    List<String> lines = null;
    try {
      lines = Files.readAllLines(new File(path + "/" + fileName).toPath());
    } catch (IOException e) {
      log.warn("could not read file " + path + "/" + fileName, true);
      return;
    }
    
    int n = 0;
    
    for (String line : lines) {
      if (line.startsWith("[")) {
        String timestamp     = DateAndTime.getTimestamp(line.substring(1, 22));
        String messageString = getMessageString(line);
        
        if (timestamp != null && messageString != null) {
          Message message = MessageFactory.create(messageString, timestamp, log);
          
          if (checkMessage(message)) {
            writeMessage(message, timestamp);
            ++n;
          }
          prevMessages.add(message);
        }
      }
    }
    
    log.info("    >> " + n + " messages in " + fileName);
    numMessages += n;
  }
  
  private String getMessageString(String line) {
    int i = line.indexOf("POCSAG1200");
    
    if (i != -1) {
      return line.substring(i);
    }
    
    return null;
  }
  
  private boolean checkMessage(Message message) {
    message.evaluateMessageHead();
    message.evaluateMessage();
    
    if (ricList.contains("*") ||
        ricList.contains(message.getAddress())) {
      
      boolean alreadyReceived = prevMessages.contains(message);
      if (!alreadyReceived || (alreadyReceived && useMessageCopies)) {
        
        boolean isValid = message.isValid();
        if (isValid || (!isValid && useInvalidMessages)) {
          
          return true;
          
        }
      }
    }
    
    return false;
  }
  
  private void writeMessage(Message message, String timestamp) {
    SQLiteConnection con = new SQLiteConnection(log);
    con.open(
          databaseDir + "/" +
          DateAndTime.getYearAndMonthName(timestamp),
          DateAndTime.getDate(timestamp) + ".db"
        );
    
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
      
    } catch (Exception e) {
      log.error("could not write message to database", e);
    }
    
    con.close();
  }
  
}
