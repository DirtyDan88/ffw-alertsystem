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
 * The ffw-alertsystem-receiver is responsible for receiving and publishing
 * (POCSAG-)messages. It receives strings via the local network sent from the
 * receiver script which is connected to the antenna. After parsing and
 * seperating the messages the @MessageReceiver hands it over to a publisher,
 * which is responsible for distribution of the message.<br>
 * 
 * @see @Application
 * @see @MessageReceiver
 * @see @MessagePublisher
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
  
  private MessagePublisher publisher;
  
  
  
  public ReceiverApplication(String[] args) {
    super(Application.ApplicationType.ALERTRECEIVER, args);
  }
  
  
  
  @Override
  protected void onApplicationStarted() {
    publisher = createPublisher();
    publisher.init(config, log);
    publisher.start();
    
    receiver = new MessageReceiver(this, publisher);
    
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
    
    if (publisher != null) {
      publisher.stop();
    }
  }
  
  @Override
  protected void onApplicationErrorOccured(Throwable t) {
    //receiver.receiverErrorOccured(t);
  }
  
  
  
  /**
   * Creates a @MessagePublisher based on the applications configuration-file.
   * @return The ffw-alertreceiver message-publisher
   */
  private MessagePublisher createPublisher() {
    MessagePublisher publisher = null;
    
    try {
      ClassLoader loader = Class.forName(
                             config.getParam("publisher-package") + "." +
                             config.getParam("publisher-class")
                           ).getClassLoader();
      
      publisher = (MessagePublisher) loader.loadClass(
                                       config.getParam("publisher-package") + "." +
                                       config.getParam("publisher-class")
                                     ).newInstance();
      
      log.info("created publisher of type " +
               config.getParam("publisher-class"), true);
      
    } catch (ClassNotFoundException | InstantiationException |
             IllegalAccessException | SecurityException e) {
      log.error("could not create a message-publisher instance", e, true);
    }
    
    return publisher;
  }
  
}
