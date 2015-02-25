package ffw.watchdog;

import java.net.InetAddress;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {
    public static void send(String userName, String passWord, String recipients, 
                            String subject, String text) {
        MailAuthenticator auth = new MailAuthenticator(userName, passWord);
        Properties properties  = new Properties();
        
        properties.put("mail.smtp.host",            "smtp.web.de");
        properties.put("mail.smtp.port",            "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth",            "true");
        
        Session session = Session.getDefaultInstance(properties, auth);
        
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(userName));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(
                              recipients, false));
            msg.setSubject(subject);
            msg.setText(text + "Watchdog on host: " + InetAddress.getLocalHost());
            msg.setSentDate(new Date());
            
            Transport.send(msg);
        }
        catch (Exception e) {
            e.printStackTrace( );
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
