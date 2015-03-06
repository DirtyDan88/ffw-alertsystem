package ffw.alertmonitor.actions;

import ffw.alertmonitor.Message;
import ffw.util.ApplicationLogger;
import ffw.util.ConfigReader;
import ffw.util.DateAndTime;
import ffw.util.Mail;
import ffw.util.ApplicationLogger.Application;

public class AlertMailInformer {

    public static void send(Message message) {
        String userName   = "ffw-moe-geraetehaus@web.de";
        String passWord   = "R8A825Tm";
        String recipients = ConfigReader.getConfigVar("alert-recipients");
        
        String subject    = "[ffw-alertsystem] !! ALARM !! ";
        String text       = "Alarm eingegangen am " + DateAndTime.get() + "\n"
                          + "Kurzstichwort: " + message.getShortKeyword() 
                                              + message.getAlertLevel() + "\n\n"
                          + "Weitere Einsatzstichwoerter: \n";
        for (int i=0; i<message.getKeywords().size(); i++) {
            text += message.getKeywords().get(i) + "\n";
        }
        
        Mail.send(userName, passWord, recipients, subject, text);
        ApplicationLogger.log("## send alert mail to: " + recipients, 
                              Application.ALERTMONITOR, false);
    }
}
