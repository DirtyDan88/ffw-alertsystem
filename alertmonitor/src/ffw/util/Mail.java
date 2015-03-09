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

import ffw.util.ApplicationLogger.Application;

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
        
        properties.put("mail.smtp.host",            "smtp.web.de");
        properties.put("mail.smtp.port",            "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth",            "true");
        
        Session session = Session.getDefaultInstance(properties, auth);
        
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(this.userName));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(
                              this.recipients, false));
            msg.setSubject(this.subject);
            msg.setText(this.text);
            msg.setSentDate(new Date());
            
            Transport.send(msg);
        }
        catch (Exception e) {
            ApplicationLogger.log("ERROR: " + e.getMessage(), app);
        }
    }
    
    private static class MailAuthenticator extends Authenticator {
        private final String user;
        private final String password;
        
        public MailAuthenticator(String user, String password) {
            this.user     = user;
            this.password = password;
        }
        
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(this.user, this.password);
        }
    }
}