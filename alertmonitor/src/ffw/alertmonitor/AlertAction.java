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

package ffw.alertmonitor;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.Map;

import ffw.alertlistener.AlertMessage;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;


// TODO: renew all alertactions with new available data from AlertMessage 


public abstract class AlertAction implements Runnable {
  
  protected AlertMessage message = null;
  
  private String instanceName = null;
  private String description  = null;
  
  private boolean isActive = false;
  
  private List<String> ricList = null;
  protected Map<String, String> paramList = null;
  
  
  
  public abstract String getInfo();
  
  /* to make sure each action is executed in his own thread it is not 
    allowed to override this method */
  public final void execute(AlertMessage alertMessage) {
   this.message = alertMessage;
   
   Thread thread = new Thread(this);
   thread.setUncaughtExceptionHandler(new ExceptionHandler(this));
   thread.start();
  }
  
  public String getInstanceName() {
    return this.instanceName;
  }
  
  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public boolean isActive() {
    return this.isActive;
  }
  
  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }
  
  public List<String> getRicList() {
    return this.ricList;
  }
  
  public void setRicList(List<String> ricList) {
    this.ricList = ricList;
  }
  
  public Map<String, String> getParamList() {
    return this.paramList;
  }
  
  public void setParamList(Map<String, String> paramList) {
    this.paramList = paramList;
  }
  
  
  
  class ExceptionHandler implements UncaughtExceptionHandler {
    
    private AlertAction alertAction;
    
    public ExceptionHandler(AlertAction alertAction) {
      this.alertAction = alertAction;
    }
    
    @Override
    public void uncaughtException(Thread t, Throwable e) {
      ApplicationLogger.log("## ERROR: unexpected and uncaught exception in " + 
                            "alert-action (name: " + alertAction.getInstanceName() + ") " + 
                            "thread: " + e.getMessage(), 
                            Application.ALERTMONITOR);
    }
  }
}
