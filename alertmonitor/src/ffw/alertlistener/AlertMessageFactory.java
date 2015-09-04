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

package ffw.alertlistener;

import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class AlertMessageFactory {
  
  public static AlertMessage create(String messageString) {
    AlertMessage alertMessage;
    
    if (messageString.startsWith("POCSAG")) {
      alertMessage = new POCSAGMessage(messageString);
    /* } else if (messageString.startsWith("TETRA")) {
      alertMessage = new TETRAMessage(messageString); */
    } else {
      ApplicationLogger.log("## ERROR: Could not create AlertMessage, " + 
                            "unknown message type", 
                            Application.ALERTLISTENER);
      alertMessage = null;
    }
    
    return alertMessage;
  }
}
