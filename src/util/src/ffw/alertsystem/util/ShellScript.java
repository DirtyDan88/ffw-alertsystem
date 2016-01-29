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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



/**
 * Executes shell-script-code, given either as file or string.
 */
public class ShellScript {
  
  public static void execString(String scriptCode) throws IOException {
    execString(scriptCode, "");
  }
  
  public static void execString(String scriptCode, String params)
      throws IOException {
    File tmpDir = new File("data/tmp/");
    tmpDir.mkdirs();
    
    File scriptFile = new File(tmpDir.toString() +
                               "/script-" + DateAndTime.getTimestamp() + ".sh");
    
    FileWriter w = new FileWriter(scriptFile);
    w.write(scriptCode);
    w.close();
    
    execScript(scriptFile.toString(), params);
    
    scriptFile.deleteOnExit();
  }
  
  public static void execScript(String name) throws IOException {
    ShellScript.execScript(name, "");
  }
  
  public static void execScript(String name, String params) throws IOException {
    /*
    String osName = System.getProperty("os.name");
    String cmdString = "";
    
    if (osName.contains("Windows")) {
      cmdString = name + ".bat " + params;
    } else {
      cmdString = "sh " + name + " " + params;
    }
    */
    
    String cmdString = "sh " + name + " " + params;
    Runtime.getRuntime().exec(cmdString);
  }
  
}
