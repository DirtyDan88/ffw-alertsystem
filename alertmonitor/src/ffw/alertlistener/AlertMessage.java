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

import java.util.Vector;



public abstract class AlertMessage {
  protected String messageString;
  
  protected boolean isComplete = false;
  protected String  address    = null;
  protected String  function   = null;
  protected String  alpha      = null;
  
  protected boolean hasCoordinates = false;
  protected String  latitude       = null;
  protected String  longitude      = null;
  protected String  alertNumber    = null;
  protected String  shortKeyword   = null;
  protected String  alertLevel     = null;
  
  protected Vector<String> keywords = new Vector<String>();
  
  
  
  public AlertMessage(String messageString) {
      this.messageString = messageString;
  }
  
  public abstract void evaluateMessageHead();
  
  public abstract void evaluateMessage();
  
  
  
  /**
   * Getter-Methods
   */
  
  public String getMessageString() {
    return this.messageString;
  }
  
  public boolean isComplete() {
    return this.isComplete;
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
   
  public boolean hasCoordinates() {
    return this.hasCoordinates;
  }
  
  public String getLatitude() {
    return this.latitude;
  }
  
  public String getLongitude() {
    return this.longitude;
  }
  
  public String getAlertNumber() {
    return this.alertNumber;
  }
  
  public String getShortKeyword() {
    return this.shortKeyword;
  }
  
  public String getAlertLevel() {
    return this.alertLevel;
  }
  
  public Vector<String> getKeywords() {
    return this.keywords;
  }
}
