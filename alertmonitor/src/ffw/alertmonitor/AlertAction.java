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

package ffw.alertmonitor.actions;

import ffw.alertlistener.AlertMessage;


// TODO: renew all alertactions with new available data from AlertMessage 


public abstract class AlertAction implements Runnable {
  
  protected AlertMessage message;
  
  public abstract String getDescription();
  
  /* to make sure each action is executed in his own thread it is not 
     allowed to override this method */
  public final void execute(AlertMessage alertMessage) {
    this.message = alertMessage;
    new Thread(this).start();
  }
}
