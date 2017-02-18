/*
  Copyright (c) 2015-2017, Max Stark <max.stark88@web.de>
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

package net.dirtydan.ffw.alertsystem.common.plugin;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.dirtydan.ffw.alertsystem.common.util.Logger;



public class _waitfor {
  
  public static void timeout(BooleanRef flag, int time) {
    int timeout = (time*1000) / 250;
    
    try {
      while (!flag.is && timeout > 0) {
        timeout--;
        Thread.sleep(250);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    // reset flag for next test
    flag.is = false;
    
    if (timeout == 0) {
      fail("Timeout when waiting for flag");
    }
  }
  
  public static void timeout(BooleanRef flag) {
    timeout(flag, 3);
  }
  
  // since both, the primitive boolean type and the Boolean wrapper class, are
  // not offering pass-by-reference, we have to implement our own wrapper.
  public static class BooleanRef {
    public boolean is = false;
  }
  
  
  
  public static void user(Logger log) {
    BufferedReader cin = new BufferedReader(
      new InputStreamReader(System.in)
    );
    
    log.info("Press enter to continue execution", true);
    
    try {
      cin.read();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  
  
  public static void countdown(int sec) {
    try {
      Thread.sleep(sec * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
}
