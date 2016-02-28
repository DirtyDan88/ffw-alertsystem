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

package ffw.alertsystem.core.message;


import ffw.alertsystem.util.Logger;



/**
 * Creates @Message objects from strings.
 */
public class MessageFactory {
  
  /**
   * @param messageString The message-string.
   * @return The new created @Message or null.
   */
  public static Message create(String messageString, Logger log) {
    if (messageString == null) {
      log.warn("message-string was null");
      return null;
    }
    
    messageString = messageString.trim();
    
    if (messageString.startsWith("POCSAG")) {
      return new POCSAGMessage(messageString);
      
    } else if (messageString.startsWith("TETRA")) {
      // message = new TETRAMessage(messageString);
      log.error("TETRA messages are not yet implemented",
                new Exception("NotYetImplementedException"), true);
      return null;
      
    } else {
      log.warn("could not create message from string: " + messageString);
      return null;
    }
  }
  
}
