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

package ffw.alertsystem.test.common;

import static org.junit.Assert.fail;



public class _timeout {
  
  public static void waitfor(BooleanRef flag) {
    // wait max. 12*250ms = 3s before timeout
    int timeout = 12;
    
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
  
  // since both, the primitive boolean type and the Boolean wrapper class, are
  // not offering pass-by-reference, we have to implement our own wrapper.
  public static class BooleanRef {
    public boolean is = false;
  }
  
}
