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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class POCSAGMessage extends Message {
  
  public POCSAGMessage(String pocsagString) {
    super(pocsagString);
  }
  
  public POCSAGMessage(String pocsagString, 
                       String timestamp) {
    super(pocsagString, timestamp);
  }
  
  public POCSAGMessage(String timestamp,
                       String address,
                       String function,
                       String alpha,
                       boolean isComplete,
                       boolean isEncrypted,
                       boolean isTestAlert,
                       boolean isFireAlert,
                       boolean unknownMessageType,
                       String alertNumber,
                       String alertSymbol,
                       String alertLevel,
                       String alertKeyword,
                       boolean hasCoordinates,
                       String latitude,
                       String longitude,
                       String street,
                       String village,
                       List<String> furtherPlaceDesc,
                       List<String> keywords) {
    super(
      timestamp,
      address,
      function,
      alpha,
      isComplete,
      isEncrypted,
      isTestAlert,
      isFireAlert,
      unknownMessageType,
      alertNumber,
      alertSymbol,
      alertLevel,
      alertKeyword,
      hasCoordinates,
      latitude,
      longitude,
      street,
      village,
      furtherPlaceDesc,
      keywords
    );
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
      endIndex   = startIndex + 2; //3;
      function   = messageString.substring(startIndex, endIndex).trim();
    } 
    
    if (alphaExits) {
      startIndex = messageString.indexOf("Alpha:") + 6;
      alpha      = messageString.substring(startIndex).trim();
      if (alpha.equals("")) {
        alphaExits = false;
      }
    }
    
    isComplete  = addressExits && functionExits && alphaExits;
    
    if (isComplete) {
      isEncrypted = checkEncryption();
    }
  }
  
  public void evaluateMessage() {
    if (isValid()) {
      // matches '[AlertInfo]//[PlaceDescription]//[EmergencyDescription]' 
      Pattern pattern = Pattern.compile(".*\\/\\/.*\\/\\/.*");
      Matcher matcher = pattern.matcher(alpha);
      
      if (matcher.find()) {
        String[] message = alpha.split("//");
        
        try {
          int i = 0;
          for (String messagePart : message) {
            if (isStringClean(messagePart)) {
              if (i == 0) evaluateAlertInfo    (messagePart.split("/"));
              if (i == 1) evaluatePlaceDesc    (messagePart.split("/"));
              if (i == 2) evaluateEmergencyDesc(messagePart.split("/"));
              i++;
            }
          }
          
        } catch (Exception e) {
          // sets valid to false
          isComplete = false;
        }
        
      } else {
        // message is not seperated with double slashs '//'
        String[] alphaStr = getCleanAlphaString().split("#");
        
        if (alphaStr.length > 2) {
          evaluateMessage(alphaStr);
        } else {
          unknownMessageType = true;
        }
      }
    }
  }
  
  public boolean isValid() {
    return (!isEncrypted() && isComplete());
  }
  
  @Override
  public String getType() {
    return "POCSAG";
  }
  
  
  
  private boolean checkEncryption() {
    if (!isStringClean(alpha)) {
      return true;
    }
    
    // matches double '<[...]>[...]<[...]>', eg. '<STX>e<STX>' 
    Pattern pattern = Pattern.compile("\\<[^>]*\\>[^>]+\\<[^>]*\\>");
    if (pattern.matcher(alpha).find()) {
      return true;
    }
    
    /*
    // matches double '<[...]><[...]>', eg. '<FF><CAN>'
    pattern = Pattern.compile("\\<[^>]*\\>\\<[^>]*\\>");
    if (pattern.matcher(alpha).find()) {
      return true;
    }
    */
    
    // matches single '<[...]>' and counts them 
    pattern = Pattern.compile("\\<[^>]*\\>");
    Matcher matcher = pattern.matcher(alpha);
    int count = 0;  
    while (matcher.find()) {
      count++;
    }
    int threshold = 8;
    if (count >= threshold) {
      return true;
    }
    
    // if all checks are passed, most probably not encrypted 
    return false;
  }
  
  private void evaluateAlertInfo(String[] alertInfo) {
    // Lang- and longitude, alertnumber, short-keyword
    int i = 0;
    
    if (isLatOrLong(alertInfo[0]) && isLatOrLong(alertInfo[1])) {
      // alert with latitude and longitude
      hasCoordinates = true;
      latitude  = alertInfo[i++];
      longitude = alertInfo[i++];
    } else {
      // alert without gps coordinates
      hasCoordinates = false;
      latitude  = null;
      longitude = null;
    }
    
    alertNumber = alertInfo[i++];
    if (!isNumeric(alertNumber)) {
      // happens when message has no alertnumber
      alertNumber = "";
      i--;
    }
    
    isFireAlert = setShortKeyword(alertInfo[i]);
    
    // TODO: THW or RedCross, String is often unordered
    for (i++; i < alertInfo.length; i++) {
      if (isStringClean(alertInfo[i])) {
        keywords.add(alertInfo[i]);
      }
    }
  }
  
  private void evaluatePlaceDesc(String[] placeDesc) {
    // Last index is always village, second-last is street
    village = placeDesc[placeDesc.length - 1];
    if (!isStringClean(village)) {
      village = "";
    }
    street  = placeDesc[placeDesc.length - 2];
    if (!isStringClean(street)) {
      street = "";
    }
    
    if (!isVillage(village) || !isStreet(street)) {
      // TODO: What to do then?
    }
    
    for (int i = 0; i <= placeDesc.length - 3; i++) {
      if (isStringClean(placeDesc[i])) {
        furtherPlaceDesc.add(placeDesc[i]);
      }
    }
  }
  
  private void evaluateEmergencyDesc(String[] emergencyDesc) {
    for (int i = 0; i < emergencyDesc.length; i++) {
      
      if (isStringClean(emergencyDesc[i])) {
      //if (!(emergencyDesc[i].startsWith("<") && emergencyDesc[i].endsWith(">")) &&
      //      emergencyDesc[i].trim().length() > 0) {
        keywords.add(emergencyDesc[i]);
      }
    }
  }
  
  
  
  private void evaluateMessage(String[] alphaStr) {
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
    
    isFireAlert = setShortKeyword(alphaStr[index++]);
    
    street = "-";
    village = "-";
    
    for (int i = index; i < alphaStr.length; i++) {
      // TODO: Try to find street and village in string
      // use Levenshtein-Distanz, isVillage() and isStreet()
      // 2822/F2 Zimmerbrand///Senefelderstr. 10/Leimen/ Geb. Berraucht
      if (isStringClean(alphaStr[i])) {
        keywords.add(alphaStr[i]);
      }
    }
  }
  
  private String getCleanAlphaString() {
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
  
  
  
  private boolean isStringClean(String str) {
    if (!(str.trim().startsWith("<") && str.trim().endsWith(">")) &&
        str.trim().length() > 0) {
      return true;
    }
    return false;
  }
  
  private boolean isNumeric(String str) {
    try {
      Double.parseDouble(str);
    } catch(NumberFormatException e) {
      return false;
    }
    return true;
  }
  
  private boolean setShortKeyword(String shortKeyword) {
    if (shortKeyword.substring(0, 2).matches("[F|B|H|T|G|W][1-7]")) {
      alertSymbol  = shortKeyword.substring(0, 1);
      alertLevel   = shortKeyword.substring(1, 2);
      alertKeyword = shortKeyword.substring(3);
      return true;
      
    } else if (shortKeyword.startsWith("BMA")) {
      alertSymbol  = "BMA";
      alertLevel   = "-";
      alertKeyword = shortKeyword;
      return true;
    
    } else {
      alertSymbol  = "-";
      alertLevel   = "-";
      alertKeyword = shortKeyword;
      return false;
      
    }
  }
  
  private boolean isLatOrLong(String latOrlong) {
    return latOrlong.matches("\\d{1,2}\\.\\d{5,}");
  }
  
  private boolean isVillage(String keyword) {
    // TODO: verify that string is village name 
    return true;
  }
  
  private boolean isStreet(String keyword) {
    // TODO: verify that string is a valid street
    //       use txt file
    //       check for 'str.', 'straße', 'weg' 
    //       Levenshtein-Distanz
    return true;
  }
  
}
