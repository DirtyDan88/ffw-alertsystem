package ffw.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ApplicationLogger {
    
    public static boolean inFile = false;
    
    public enum Application {
        ALERTMONITOR, WATCHDOG;
        
        @Override
        public String toString() {
            switch(this) {
                case ALERTMONITOR: return "alertmonitor";
                case WATCHDOG:     return "watchdog";
                default: throw new IllegalArgumentException();
            }
        }
    }
    
    public static void log(String text, Application application) {
        log(text, application, true);
    }
    
    public static void log(String text, Application application, boolean withTime) {
        BufferedWriter bufWriter = null;
        
        String dateAndTime = "";
        if (withTime) {
            dateAndTime = "[" + DateAndTime.get() + "]";
        } else {
            dateAndTime = "                       ";
        }
        
        if (inFile) {
            try {
                File logFile = new File("log/log-" + DateAndTime.getDate() + 
                                        "-" + application + ".txt");
                bufWriter = new BufferedWriter(new FileWriter(logFile, true));
                
                
                bufWriter.write(dateAndTime +" " + text + "\n");
                
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
        } else {
            System.out.println(dateAndTime +" " + text);
        }
    }
}