/*
  Copyright (c) 2015-2017, Max Stark <max.stark88@web.de>
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

package net.dirtydan.ffw.alertsystem.common.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



/**
 * Allows to send eMails.
 * - static class, no instantiation possible
 * - accessible only via static method {@link Mail#send()}
 * - sending a mail happens in own thread
 * 
 * TODO: atm only web.de senders possible
 *       use apache.commons.mail
 */
public final class Mail {
  
  private Mail() {}
  
  public static void send(String userName, String passWord, String recipients,
                          String subject, String text) {
    new Thread(
        new _Mail(userName, passWord, recipients, subject, text))
      .start();
  }
  
  
  
  private static class _Mail implements Runnable {
    
    private final Logger log = Logger.getApplicationLogger();
    
    private String userName;
    
    private String passWord;
    
    private String recipients;
    
    private String subject;
    
    private String text;
    
    public _Mail(String userName, String passWord, String recipients,
        String subject, String text) {
      this.userName   = userName;
      this.passWord   = passWord;
      this.recipients = recipients;
      this.subject    = subject;
      this.text       = text;
    }
    
    @Override
    public void run() {
      MailAuthenticator auth = new MailAuthenticator(userName, passWord);
      Properties properties  = new Properties();
      
      properties.put("mail.transport.protocol",   "smtp");
      properties.put("mail.smtp.host",            "smtp.web.de");
      properties.put("mail.smtp.port",            "587");
      properties.put("mail.smtp.starttls.enable", "true");
      properties.put("mail.smtp.auth",            "true");
      
      Session session = Session.getInstance(properties, auth);
      MimeMessage msg = new MimeMessage(session);
      
      try {
        msg.setFrom(new InternetAddress(userName));
        msg.setRecipients(
              Message.RecipientType.TO,
              InternetAddress.parse(recipients, false)
            );
        
        msg.setSubject(subject);
        msg.setText(text);
        msg.setSentDate(new Date());
        
        Transport.send(msg);
        log.info("sent mail to: " + recipients);
        
      } catch (MessagingException e) {
        log.error("could not sent mail (from: " + userName + ", " +
                                       "to: " + recipients + ")", e);
      }
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
      return new PasswordAuthentication(userName, passWord);
    }
    
  }
  
}
