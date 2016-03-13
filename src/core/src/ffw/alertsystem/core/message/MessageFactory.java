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


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import ffw.alertsystem.util.Logger;



/**
 * Creates @Message objects from strings or database-tupel.
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
      log.warn("unknow message-type: " + messageString);
      return null;
    }
  }
  
  /**
   * @param messageTupel java.sql.ResultSet which points to the message-tupel.
   * @return The new created @Message or null.
   */
  public static Message create(ResultSet messageTupel, Logger log) {
    try {
      String messageString = messageTupel.getString("messageString");
      
      if (messageString.startsWith("POCSAG")) {
        return createPOCSAG(messageTupel);
      } else if (messageString.startsWith("TETRA")) {
        log.error("TETRA messages are not yet implemented",
                  new Exception("NotYetImplementedException"), true);
      } else {
        log.warn("unknow message-type: " + messageString);
      }
      
    } catch (SQLException e) {
      log.error("could not create message from database-tupel", e, true);
    }
    
    return null;
  }
  
  /**
   * 
   * @param messageString The message-string.
   * @param timestamp 
   * @return The new created @Message or null.
   */
  public static Message create(String messageString, String timestamp,
                               Logger log) {
    if (messageString == null) {
      log.warn("message-string was null");
      return null;
    }
    
    messageString = messageString.trim();
    
    if (messageString.startsWith("POCSAG")) {
      return new POCSAGMessage(messageString, timestamp);
      
    } else if (messageString.startsWith("TETRA")) {
      // message = new TETRAMessage(messageString, timestamp);
      log.error("TETRA messages are not yet implemented",
                new Exception("NotYetImplementedException"), true);
      return null;
      
    } else {
      log.warn("unknow message-type: " + messageString);
      return null;
    }
  }
  
  
  
  private static Message createPOCSAG(ResultSet messageTupel) throws SQLException {
    List<String> furtherPlaceDescList = new LinkedList<String>();
    String places = messageTupel.getString("furtherPlaceDesc");
    if (places != null) {
      for (String place : places.split(",")) {
        furtherPlaceDescList.add(place);
      }
    }
  
    List<String> keywordList = new LinkedList<String>();
    String keywords = messageTupel.getString("keywords");
    if (keywords != null) {
      for (String keyword : keywords.split(",")) {
        keywordList.add(keyword);
      }
    }
    
    // TODO: set message-string?
    
    return new POCSAGMessage(
             String.valueOf(messageTupel.getInt("timestamp")),
             messageTupel.getString("address"),
             messageTupel.getString("function"),
             "",
             
             (messageTupel.getInt("isComplete")         == 1) ? true : false,
             (messageTupel.getInt("isEncrypted")        == 1) ? true : false,
             (messageTupel.getInt("isTestAlert")        == 1) ? true : false,
             (messageTupel.getInt("isFireAlert")        == 1) ? true : false,
             (messageTupel.getInt("unknownMessageType") == 1) ? true : false,
              
              messageTupel.getString("alertNumber"),
              messageTupel.getString("alertSymbol"),
              messageTupel.getString("alertLevel"),
              messageTupel.getString("alertKeyword"),
              
             (messageTupel.getInt("hasCoordinates") == 1) ? true : false,
              messageTupel.getString("latitude"),
              messageTupel.getString("longitude"),
              messageTupel.getString("street"),
              messageTupel.getString("village"),
              
              furtherPlaceDescList,
              keywordList
           );
  }
  
}
