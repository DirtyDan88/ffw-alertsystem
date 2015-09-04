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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class FileReader {
  
  public static String getContent(String fileName, Application app) {
    BufferedReader bufReader = null;
    String line = null;
    StringBuilder content = new StringBuilder("");
    
    try {
      File file = new File(fileName);
      bufReader = new BufferedReader(new java.io.FileReader(file));
      
      while((line = bufReader.readLine()) != null) {
        if (!line.startsWith("#")) {
          content.append(line);
        }
      }
      
    } catch(IOException e) {
      ApplicationLogger.log("ERROR: " + e.getMessage(), app);
    } finally {
      if(bufReader != null) {
        try {
          bufReader.close();
        } catch(IOException e) {
          ApplicationLogger.log("ERROR: " + e.getMessage(), app);
        }
      }
    }
    
    return content.toString();
  }
  
  
  
  public static List<String> getAllLines(String fileName, Application app) {
    BufferedReader bufReader = null;
    String line = null;
    List<String> allLines = new ArrayList<String>();
    
    try {
      File file = new File(fileName);
      bufReader = new BufferedReader(new java.io.FileReader(file));
      
      while((line = bufReader.readLine()) != null) {
        if (!line.startsWith("#")) {
          allLines.add(line);
        }
      }
      
    } catch(IOException e) {
      ApplicationLogger.log("ERROR: " + e.getMessage(), app);
    } finally {
      if(bufReader != null) {
        try {
          bufReader.close();
        } catch(IOException e) {
          ApplicationLogger.log("ERROR: " + e.getMessage(), app);
        }
      }
    }
    
    return allLines;
  }
}
