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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ffw.util.ApplicationLogger.Application;

public class ConfigReader {
    
    public static String fileName = "config.txt";
    
    public static String getConfigVar(String varName) {
        return getConfigVar(varName, Application.ALERTMONITOR);
    }
    
    public static String getConfigVar(String varName, Application app) {
        BufferedReader bufReader = null;
        String varValue = "";
        String line = null;
        Boolean found = false;
        
        try {
            File configFile = new File(fileName);
            bufReader = new BufferedReader(new FileReader(configFile));
            
            
            while((line = bufReader.readLine()) != null) {
                if (!line.startsWith("#") && line.startsWith(varName)) {
                    varValue = line.split("=")[1];
                    found = true; 
                    break;
                }
            }
            
            if (!found) {
                /* TODO do something very intelligent */
            }
            
        } catch(IOException e) {
            ApplicationLogger.log("ERROR: " + e.getMessage() + "\n" +
                                  "varName: " + varName, app);
        } finally {
            if(bufReader != null) {
                try {
                    bufReader.close();
                } catch(IOException e) {
                    ApplicationLogger.log("ERROR: " + e.getMessage(), app);
                }
            }
        }
        
        return varValue;
    }
}
