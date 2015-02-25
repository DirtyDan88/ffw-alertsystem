package ffw.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MessageLogger {
    
    public enum LogEvent {
        WATCHDOG, 
        ALERT;
        
        public String test() {
            return "asdasd";
        }
        
        @Override
        public String toString() {
            switch(this) {
                case WATCHDOG: return "WATCHDOG-RESET";
                case ALERT:    return "ALERT-TRIGGER ";
                default: throw new IllegalArgumentException();
            }
        }
    }
    
    public static void log(String pocsag1200Str, LogEvent event) {
        BufferedWriter bufWriter = null;
        
        try {
            File logFile = new File("log/log-" + DateAndTime.getDate() + ".txt");
            bufWriter = new BufferedWriter(new FileWriter(logFile, true));
            bufWriter.write("[" + DateAndTime.get() + "]    " + event + 
                            "    " + pocsag1200Str + "\n");
            
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
