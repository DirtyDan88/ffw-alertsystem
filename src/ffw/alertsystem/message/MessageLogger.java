package ffw.alertsystem.message;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageLogger {
    
    private MessageLogger() {}
    
    public static void log(String pocsag1200Str) {
        BufferedWriter bufWriter = null;
        
        Date now = new java.util.Date();
        SimpleDateFormat sdfDate        = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat sdfDateAndTime = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String date = sdfDate.format(now);
        String dateAndTime = sdfDateAndTime.format(now);
        
        try {
            File logFile = new File("log/log-" + date + ".txt");
            
            bufWriter = new BufferedWriter(new FileWriter(logFile, true));
            bufWriter.write(dateAndTime + ":    " + pocsag1200Str + "\n");
            
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(bufWriter != null) {
                try {
                    bufWriter.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
