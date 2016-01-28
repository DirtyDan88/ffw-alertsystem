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

package ffw.alertsystem.core.receiver;

import ffw.alertsystem.core.Application;



/**
 * The ffw-alertsystem-receiver is responsible for receiving and distributing
 * (POCSAG-)messages. It receives strings via the local network sent from the
 * receiver script which is connected to the antenna. After parsing and
 * seperating the messages the distribution happens through a websocket-based
 * server.<br>
 * 
 * @see @Application
 * @see @MessageReceiver
 * @see @ReceiverServer
 */
public class ReceiverApplication extends Application {
  
  /**
   * Entry point for the ffw-alertsystem-receiver-application.
   * @param args Command-line parameters.
   */
  public static void main(String[] args) {
    new ReceiverApplication(args).start();
  }
  
  
  
  private MessageReceiver receiver;
  
  private Thread receiverThread;
  
  public ReceiverApplication(String[] args) {
    super(Application.ApplicationType.ALERTRECEIVER, args);
  }
  
  
  
  @Override
  protected void onApplicationStarted() {
    receiver = new MessageReceiver(this);
    
    receiverThread = new Thread(receiver);
    receiverThread.setName("receiver-thread");
    receiverThread.setUncaughtExceptionHandler(errHandler);
    
    receiverThread.start();
  }
  
  @Override
  protected void onApplicationStopped() {
    if (receiver != null) {
      receiver.stop();
    }
  }
  
  @Override
  protected void onApplicationErrorOccured(Throwable t) {
    //receiver.receiverErrorOccured(t);
  }
  
}
