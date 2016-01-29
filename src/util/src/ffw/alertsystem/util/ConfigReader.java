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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;



/**
 * Reads text files and returns variable-values pairs:
 * - varName=varValue
 * - lines starting with '#' are interpreted as comments
 */
public class ConfigReader {
  
  public String fileName;
  
  public ConfigReader(String fileName) {
    this.fileName = fileName;
  }
  
  public String getConfigVar(String varName) {
    BufferedReader bufReader = null;
    String varValue = null;
    String line;
    
    try {
      File configFile = new File(fileName);
      bufReader = new BufferedReader(new FileReader(configFile));
      
      while ((line = bufReader.readLine()) != null) {
        if (!line.startsWith("#") && line.startsWith(varName)) {
          String s[] = line.split("=");
          
          if (s.length > 1) {
            varValue = s[1];
          } else {
            varValue = "";
          }
          
          break;
        }
      }
      
    } catch (IOException e) {
      return null;
      
    } finally {
      if (bufReader != null) {
        try {
          bufReader.close();
        } catch (IOException e) {
          return null;
        }
      }
    }
    
    return varValue;
  }
  
}
