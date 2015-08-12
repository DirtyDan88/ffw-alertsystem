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

package ffw.util.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ffw.util.DateAndTime;



public class Logger {
  
  public static void log(String fileName, String text) {
    BufferedWriter bufWriter = null;
    
    try {
      //String dirName  =  + "/";
      File logFileDir = new File("log/" + DateAndTime.getYearAndMonthName());
      logFileDir.mkdirs();
      File logFile = new File(logFileDir, fileName);
      
      bufWriter = new BufferedWriter(new FileWriter(logFile, true));
      bufWriter.write(text);
      
    } catch(IOException e) {
      e.printStackTrace();
      
    } finally {
      if(bufWriter != null) {
        try {
          bufWriter.close();
        } catch(IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}