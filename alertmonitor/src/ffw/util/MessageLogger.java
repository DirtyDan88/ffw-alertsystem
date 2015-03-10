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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MessageLogger {
    
    public enum LogEvent {
        WATCHDOG, ALERT;
        
        @Override
        public String toString() {
            switch(this) {
                case WATCHDOG: return "WATCHDOG-RESET";
                case ALERT:    return "ALERT-TRIGGER ";
                default: throw new IllegalArgumentException();
            }
        }
    }
    
    public static void log(String pocsag1200Str, LogEvent event) {
        BufferedWriter bufWriter = null;
        
        try {
            File logFile = new File("log/log-" + DateAndTime.getDate() + ".txt");
            bufWriter = new BufferedWriter(new FileWriter(logFile, true));
            bufWriter.write("[" + DateAndTime.get() + "]    " + event + 
                            "    " + pocsag1200Str + "\n");
            
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
