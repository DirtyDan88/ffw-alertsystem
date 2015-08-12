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

package ffw.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;

public class Mail implements Runnable {
  private String userName;
  private String passWord;
  private String recipients;
  private String subject;
  private String text;
  private Application app;
  
  public Mail(String userName, String passWord, String recipients, 
              String subject, String text, Application app) {
    this.userName   = userName;
    this.passWord   = passWord;
    this.recipients = recipients;
    this.subject    = subject;
    this.text       = text;
    this.app        = app;
  }
  
  public static void send(String userName, String passWord, String recipients, 
                          String subject, String text, Application app) {
    new Thread(new Mail(userName, passWord, recipients, subject, text, app)).start();
  }
  
  @Override
  public void run() {
    MailAuthenticator auth = new MailAuthenticator(this.userName, this.passWord);
    Properties properties  = new Properties();
    
    /* TODO: move this into config file, like email-address and pw */
    properties.put("mail.transport.protocol",   "smtp");
    properties.put("mail.smtp.host",            "smtp.web.de");
    properties.put("mail.smtp.port",            "587");
    properties.put("mail.smtp.starttls.enable", "true");
    properties.put("mail.smtp.auth",            "true");
    
    Session session = Session.getInstance(properties, auth);
    
    try {
      MimeMessage msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(this.userName));
      msg.setRecipients(Message.RecipientType.TO, 
                        InternetAddress.parse(this.recipients, false));
      msg.setSubject(this.subject);
      msg.setText(this.text);
      msg.setSentDate(new Date());
      
      Transport.send(msg);

      /*
      Transport transport = session.getTransport();
      transport.connect();
      transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
      transport.close();
      */
      
    } catch (Exception e) {
      ApplicationLogger.log("ERROR in Mail class: " + e.getMessage(), app);
    }
  }
  
  private static class MailAuthenticator extends Authenticator {
    private final String userName;
    private final String passWord;
    
    public MailAuthenticator(String user, String password) {
      this.userName = user;
      this.passWord = password;
    }
    
    protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(this.userName, this.passWord);
    }
  }
}
