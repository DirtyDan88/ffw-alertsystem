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

import java.io.IOException;

import ffw.util.ApplicationLogger.Application;

public class ShellScript {
    
    public static void execute(String name) {
        ShellScript.execute(name, "");
    }
    
    public static void execute(String name, String params) {
        try {
            String osName = System.getProperty("os.name");
            
            if (osName.contains("Windows")) {
                Runtime.getRuntime().exec("script/" + name + ".bat " + params);
            } else {
                Runtime.getRuntime().exec("sh script/" + name + ".sh " + params);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                  Application.ALERTMONITOR);
        }
    }
}
