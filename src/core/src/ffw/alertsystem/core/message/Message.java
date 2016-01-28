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

import java.util.ArrayList;
import java.util.List;

import ffw.alertsystem.util.DateAndTime;



public abstract class Message {
  
  /* original received string */
  protected String messageString;
  
  /* Message meta data */
  protected String timestamp = null;
  protected String address   = null;
  protected String function  = null;
  protected String alpha     = null;
  
  protected boolean isComplete         = false;
  protected boolean isEncrypted        = false;
  protected boolean isTestAlert        = false;
  protected boolean isFireAlert        = false;
  protected boolean unknownMessageType = false;
  
  /* Alert info */
  protected String  alertNumber  = null;
  protected String  alertSymbol  = null;
  protected String  alertLevel   = null;
  protected String  alertKeyword = null;
  
  /* Place description */
  protected boolean      hasCoordinates   = false;
  protected String       latitude         = null;
  protected String       longitude        = null;
  protected String       street           = null;
  protected String       village          = null;
  protected List<String> furtherPlaceDesc = new ArrayList<String>();
  
  /* Emergency description */
  protected List<String> keywords = new ArrayList<String>();
  
  
  
  public Message(String messageString) {
    this.timestamp     = DateAndTime.getTimestamp();
    this.messageString = messageString;
  }
  
  public Message(String messageString, 
                 String timestamp) {
    this.timestamp     = timestamp;
    this.messageString = messageString;
  }
  
  public Message(String timestamp,
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
    this.timestamp = timestamp;
    this.address   = address;
    this.function  = function;
    this.alpha     = alpha;
    
    this.isComplete         = isComplete;
    this.isEncrypted        = isEncrypted;
    this.isTestAlert        = isTestAlert;
    this.isFireAlert        = isFireAlert;
    this.unknownMessageType = unknownMessageType;
    
    this.alertNumber  = alertNumber;
    this.alertSymbol  = alertSymbol;
    this.alertLevel   = alertLevel;
    this.alertKeyword = alertKeyword;
    
    this.hasCoordinates   = hasCoordinates;
    this.latitude         = latitude;
    this.longitude        = longitude;
    this.street           = street;
    this.village          = village;
    this.furtherPlaceDesc = furtherPlaceDesc;
    
    this.keywords = keywords;
  }
  
  
  
  public abstract void evaluateMessageHead();
  
  public abstract void evaluateMessage();
  
  public abstract boolean isValid();
  
  public abstract String getType();
  
  @Override
  public boolean equals(Object o) {
    Message other = (Message) o;
    
    if (isEncrypted || other.isEncrypted()) {
      return false;
    }
    
    if (!isComplete || !other.isComplete) {
      return false;
    }
    
    if (!messageString.equals(other.messageString)) {
      return false;
    }
    
    return true;
  }
  
  
  
  public String buildText() {
    String text = "Eingegangen am " + DateAndTime.get(getTimestamp()) + "\n\n";
    
    text += "Kurzstichwort: " + getAlertSymbol() + getAlertLevel() + " "
                              + getAlertKeyword() + "\n";
    text += "Ort:           " + getStreet() + ", " +  getVillage() + "\n" +
            "               " + getFurtherPlaceDescAsString() + "\n";
    text += "Stichworte:  ";
    for (int i = 0; i < getKeywords().size(); i++) {
      text += getKeywords().get(i);
    }
    
    return text;
  }
  
  public String buildShortText() {
    String text = "Eingegangen am " + DateAndTime.get(getTimestamp()) + "\n\n";
    text += "Kurzstichwort: " + getAlertSymbol() + getAlertLevel() + " "
                              + getAlertKeyword() + "\n";
    text += "Ort: " + getStreet() + ", " +  getVillage() + "\n" 
                    + getFurtherPlaceDescAsString() + "\n";
    text += "Stichworte: ";
    for (int i = 0; i < getKeywords().size(); i++) {
      text += getKeywords().get(i);
    }
    
    return text;
  }
  
  
  
  /**************************************************************************
   ***                   Getter-methods are following                     ***
   **************************************************************************/
  
  
  
  public String getMessageString() {
    return messageString;
  }
  
  public String getTimestamp() {
    return timestamp;
  }
  
  public String getAddress() {
    return address;
  }
  
  public String getFunction() {
    return function;
  }
  
  public String getAlpha() {
    return alpha;
  }
  
  
  
  public boolean isComplete() {
    return isComplete;
  }
  
  public boolean isEncrypted() {
    return isEncrypted;
  }
  
  public boolean isTestAlert() {
    return isTestAlert;
  }
  
  public boolean isFireAlert() {
    return isFireAlert;
  }
  
  public boolean isUnknownMessageType() {
    return unknownMessageType;
  }
  
  
  
  public String getAlertNumber() {
    return alertNumber;
  }
  
  public String getAlertSymbol() {
    return alertSymbol;
  }
  
  public String getAlertLevel() {
    return alertLevel;
  }
  
  public String getAlertKeyword() {
    return alertKeyword;
  }
  
  
  
  public boolean hasCoordinates() {
    return hasCoordinates;
  }
  
  public String getLatitude() {
    return latitude;
  }
  
  public String getLongitude() {
    return longitude;
  }
  
  public String getStreet() {
    return street;
  }
  
  public String getVillage() {
    return village;
  }
  
  public List<String> getFurtherPlaceDesc() {
    return furtherPlaceDesc;
  }
  
  public String getFurtherPlaceDescAsString() {
    String places = "";
    for (String place : furtherPlaceDesc) {
      places = places.concat(place).concat(",");
    }
    
    return (places.equals("")) ? "" : places.substring(0, places.length() - 1);
  }
  
  public List<String> getKeywords() {
    return keywords;
  }
  
  public String getKeywordsAsString() {
    String furtherKeywords = "";
    for (String keyword : keywords) {
      furtherKeywords = furtherKeywords.concat(keyword).concat(",");
    }
    
    return (furtherKeywords.equals("")) ? "" : 
            furtherKeywords.substring(0, furtherKeywords.length() - 1);
  }
  
}
