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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



public class FileReader {
  
  public static String getContent(InputStreamReader reader) {
    BufferedReader bufReader = null;
    String line = null;
    StringBuilder content = new StringBuilder("");
    
    try {
      bufReader = new BufferedReader(reader);
      
      while ((line = bufReader.readLine()) != null) {
        if (!line.startsWith("#")) {
          content.append(line);
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
    
    return content.toString();
  }
  
  public static String getContent(String fileName) {
    File file = new File(fileName);
    
    try {
      return getContent(new java.io.FileReader(file));
    } catch (FileNotFoundException e) {
      return null;
    }
  }
  
  public static String streamToString(InputStream is) {
    BufferedReader bufReader = null;
    StringBuilder allLines = new StringBuilder("");
    String line = null;
    
    try {
      bufReader = new BufferedReader(new InputStreamReader(is));
      
      while ((line = bufReader.readLine()) != null) {
        allLines.append(line + "\n");
      }
      
      is.close();
      
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
    
    
    return allLines.toString();
  }
  
}
