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

import java.util.ArrayList;
import java.util.List;

import ffw.util.DateAndTime;



public abstract class AlertMessage {
  
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
  
  
  
  public AlertMessage(String messageString) {
    this.timestamp     = String.valueOf(new java.util.Date().getTime() / 1000);
    this.messageString = messageString;
  }
  
  public AlertMessage(String messageString, String timestamp) {
    this.timestamp     = timestamp;
    this.messageString = messageString;
  }
  
  public abstract void evaluateMessageHead();
  
  public abstract boolean evaluateMessage();
  
  public abstract String getType();
  
  @Override
  public boolean equals(Object o) {
    AlertMessage other = (AlertMessage) o;
    
    if (this.isEncrypted || other.isEncrypted()) {
      return false;
    }
    
    if (!this.isComplete || !other.isComplete) {
      return false;
    }
    
    if (!this.messageString.equals(other.messageString)) {
      return false;
    }
    
    return true;
  }
  
  
  
  /**
   * Getter-Methods
   */
  
  public String getMessageString() {
    return this.messageString;
  }
  
  public String getTimestamp() {
    return this.timestamp;
  }
  
  public String getAddress() {
    return this.address;
  }
  
  public String getFunction() {
    return this.function;
  }
  
  public String getAlpha() {
    return this.alpha;
  }
  
  
  
  public boolean isComplete() {
    return this.isComplete;
  }
  
  public boolean isEncrypted() {
    return this.isEncrypted;
  }
  
  public boolean isTestAlert() {
    return this.isTestAlert;
  }
  
  public boolean isFireAlert() {
    return this.isFireAlert;
  }
  
  public boolean isUnknownMessageType() {
    return this.unknownMessageType;
  }
  
  
  
  public String getAlertNumber() {
    return this.alertNumber;
  }
  
  public String getAlertSymbol() {
    return this.alertSymbol;
  }
  
  public String getAlertLevel() {
    return this.alertLevel;
  }
  
  public String getAlertKeyword() {
    return this.alertKeyword;
  }
  
  
  
  public boolean hasCoordinates() {
    return this.hasCoordinates;
  }
  
  public String getLatitude() {
    return this.latitude;
  }
  
  public String getLongitude() {
    return this.longitude;
  }
  
  public String getStreet() {
    return this.street;
  }
  
  public String getVillage() {
    return this.village;
  }
  
  public List<String> getFurtherPlaceDesc() {
    return this.furtherPlaceDesc;
  }
  
  public String getFurtherPlaceDescAsString() {
    String places = "";
    for (String place : this.furtherPlaceDesc) {
      places = places.concat(place).concat(",");
    }
    
    return (places.equals("")) ? "" : places.substring(0, places.length() - 1);
  }
  
  public List<String> getKeywords() {
    return this.keywords;
  }
  
  public String getKeywordsAsString() {
    String furtherKeywords = "";
    for (String keyword : this.keywords) {
      furtherKeywords = furtherKeywords.concat(keyword).concat(",");
    }
    
    return (furtherKeywords.equals("")) ? "" : 
            furtherKeywords.substring(0, furtherKeywords.length() - 1);
  }
  
  
  
  public String buildText() {
    long timestamp = Long.parseLong(getTimestamp());
    
    String text = "Eingegangen am " + DateAndTime.get(timestamp) + "\n\n";
    
    text += "Kurzstichwort: " + getAlertSymbol() + getAlertLevel() + " "
                              + getAlertKeyword() + "\n";
    text += "Ort:           " + getStreet() + ", " +  getVillage() + "\n" +
            "               " + getFurtherPlaceDescAsString() + "\n";
    text += "Beschreibung:  ";
    for (int i = 0; i < getKeywords().size(); i++) {
      text += getKeywords().get(i);
    }
    
    return text;
  }
}
