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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import net.dirtydan.ffw.alertsystem.common.message.Message;
import net.dirtydan.ffw.alertsystem.common.util.DateAndTime;



public class MessageLogger extends MonitorPlugin {
  
  @Override
  protected void onReceivedMessage(Message message) {
    File logFileDir = new File(config().paramList().get("log-dir").val() +
                               DateAndTime.getYearAndMonthName());
    logFileDir.mkdirs();
    
    String fileName = "log-" + DateAndTime.getDate() + "-" +
                      config().paramList().get("log-file-name").val() + ".txt";
    File logFile = new File(logFileDir, fileName);
    
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(logFile, true);
    } catch (FileNotFoundException e) {
      log.error("could not get log-file for message-logger", e);
    }
    
    PrintStream s = new PrintStream(fos);
    s.println("[" + DateAndTime.get() + "] " + message.getMessageString());
    
    log.info("wrote message to file");
  }
  
}
