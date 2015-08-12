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

import ffw.util.DateAndTime;



public class ApplicationLogger {
  
  public static boolean inFile = false;
  
  public enum Application {
    ALERTMONITOR, WATCHDOG;
    
    @Override
    public String toString() {
      switch(this) {
        case ALERTMONITOR: return "alertmonitor";
        case WATCHDOG:     return "watchdog";
        default: throw new IllegalArgumentException();
      }
    }
  }
  
  public static void log(String text, Application application) {
    log(text, application, true);
  }
  
  public static void log(String text, Application application, boolean withTime) {
    String dateAndTime = "";
    if (withTime) {
        dateAndTime = "[" + DateAndTime.get() + "]";
    } else {
        dateAndTime = "                       ";
    }
      
    if (inFile) {
      String fileName = "log-" + DateAndTime.getDate() + 
                        "-" + application + ".txt";
      Logger.log(fileName, dateAndTime +" " + text + "\n");
      
    } else {
      System.out.println(dateAndTime +" " + text);
    }
  }
}