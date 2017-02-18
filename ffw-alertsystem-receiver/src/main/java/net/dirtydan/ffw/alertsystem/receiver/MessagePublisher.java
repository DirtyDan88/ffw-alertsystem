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

package net.dirtydan.ffw.alertsystem.receiver;

import net.dirtydan.ffw.alertsystem.common.application.ApplicationConfig;



/**
 * A message-publisher gets complete messages from the @MessageReceiver and is
 * responsible for the distribution.
 */
public interface MessagePublisher {
  
  /**
   * Is called right after creation to handover init-stuff.
   * @param config The application-config which might contain some config-params
   *               for the message-publisher.
   */
  public void init(ApplicationConfig config);
  
  /**
   * Intended to run some init-code, like connecting to server etc. Is called
   * before the @MessageReceiver starts its receiving-loop.
   */
  public void start();
  
  /**
   * This method is called from the @MessageReceiver each time a message was
   * (completely) received.
   * @param message The received message-string.
   */
  public void newMessage(String message);
  
  /**
   * Is called when the application stops, intended for cleanup activities.
   */
  public void stop();
  
}
