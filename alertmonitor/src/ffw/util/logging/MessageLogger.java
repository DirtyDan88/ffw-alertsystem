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



public class MessageLogger {
  
  public static void log(String text) {
    String fileName = "log-" + DateAndTime.getDate() + "-messages.txt";
    Logger.log(fileName, "[" + DateAndTime.get() + "]" +
                         "    " + text + "\n");
  }
}
