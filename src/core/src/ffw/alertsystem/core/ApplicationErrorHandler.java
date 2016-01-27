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

package ffw.alertsystem.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.net.UnknownHostException;

import ffw.alertsystem.util.Mail;



/**
 * Uncaught exception handler for ffw-alertsystem-applications. Besides the
 * ability to handle and notify ({@link Application#onApplicationErrorOccured()})
 * about occurred errors it also sends an eMail to the specified recipient(s) in
 * the application's config-file ({@link ApplicationErrorHandler#reportError()}).
 */
public class ApplicationErrorHandler implements UncaughtExceptionHandler {
  
  private final Application app;
  
  public ApplicationErrorHandler(Application app) {
    this.app = app;
  }
  
  
  
  @Override
  public void uncaughtException(Thread t, Throwable e) {
    app.log.error("uncaught exception in " + app.appType, e);
    
    app.onApplicationErrorOccured(e);
    reportError(e);
  }
  
  
  
  public final void reportError(Throwable t) {
    String subject = app.appType.error() +
                     "'" + app.config.getParam("application-name") + "'";
    reportError(subject, t);
  }
  
  public final void reportError(String subject, Throwable t) {
    subject = "[ffw-alertsystem] " + subject;
    
    String text;
    try {
      text = app.appType + " on host: " + InetAddress.getLocalHost() + "\n";
    } catch (UnknownHostException e) {
      text = app.appType + " on host: unknown\n";
    }
    
    text += app.appType + "-name: " +
            app.config.getParam("application-name") +
            "\n\n";
    
    StringWriter stackTrace = new StringWriter();
    t.printStackTrace(new PrintWriter(stackTrace));
    text += "Message: " + t.getMessage() + "\n" + stackTrace.toString();
    
    sendMail(subject, text);
  }
  
  private void sendMail(String subject, String text) {
    String userName   = app.config.getParam("email-address");
    String passWord   = app.config.getParam("email-password");
    String recipients = app.config.getParam("email-error-recipients");
    
    Mail.send(userName, passWord, recipients, subject, text, app.log);
    app.log.info("sent mail-notification to: " + recipients, true);
  }
  
}
