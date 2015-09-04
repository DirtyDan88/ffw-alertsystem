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



public class POCSAGMessage extends AlertMessage {
  
  public POCSAGMessage(String pocsagString) {
    super(pocsagString);
  }

  public void evaluateMessageHead() {
    int startIndex, endIndex;
    
    boolean addressExits  = messageString.contains("Address:");
    boolean functionExits = messageString.contains("Function:");
    boolean alphaExits    = messageString.contains("Alpha:");
    
    if (addressExits) {
      startIndex = messageString.indexOf("Address:") + 8;
      endIndex   = startIndex + 8;
      address    = messageString.substring(startIndex, endIndex).trim();
    }
    
    if (functionExits) {
      startIndex = messageString.indexOf("Function:") + 9;
      endIndex   = startIndex + 3;
      function   = messageString.substring(startIndex, endIndex).trim();
    }
    
    if (alphaExits) {
      startIndex = messageString.indexOf("Alpha:") + 6;
      alpha      = messageString.substring(startIndex).trim();
    }
    
    isComplete = addressExits && functionExits && alphaExits;
  }
  
  public void evaluateMessage() {
    if (getAlpha() != null) {
      String[] alphaStr = cleanAlphaString().split("#");
      int index = 0;
      
      if (isLatOrLong(alphaStr[0]) && isLatOrLong(alphaStr[1])) {
        /* alert with latitude and longitude */
        hasCoordinates = true;
        latitude  = alphaStr[index++];
        longitude = alphaStr[index++];
      } else {
        /* alert without gps coordinates */
        hasCoordinates = false;
        latitude  = null;
        longitude = null;
      }
      
      alertNumber = alphaStr[index++];
      
      if (isShortKeyword(alphaStr[index])) {
        shortKeyword = alphaStr[index].substring(0, 1);
        alertLevel   = alphaStr[index].substring(1, 2);
        if (alphaStr[index].length() > 3) {
          keywords.add(alphaStr[index].substring(3));
        }
        index++;
        
      } else {
        shortKeyword = "-";
        alertLevel   = "-";
      }
      
      // TODO: implement address detection from message string
      //       alphaStr[index++] := address on index == 4?
      
      for (int i = index; i < alphaStr.length; i++) {
        keywords.add(alphaStr[i]);
      }
    }
  }
  
  
  
  private Boolean isShortKeyword(String shortKeyword) {
    return shortKeyword.substring(0, 2).matches("[F|B|H|T|G|W][1-7]");
  }
  
  private boolean isLatOrLong(String latOrlong) {
    return latOrlong.matches("\\d{1,2}\\.\\d{5,}");
  }
  
  private String cleanAlphaString() {
    // TODO: check for double //
    
    String[] alphaStr  = getAlpha().split("/");
    String newAlphaStr = "";
    
    for (int i = 0; i < alphaStr.length; i++) {
      String tmp = alphaStr[i].trim();
      
      if (!(tmp.startsWith("<") && tmp.endsWith(">")) && tmp.length() != 0) {
        newAlphaStr = newAlphaStr.concat(alphaStr[i] + "#");
      }
    }
    
    return newAlphaStr;
  }
}
