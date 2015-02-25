package ffw.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAndTime {
    public static String get() {
        Date now = new java.util.Date();
        SimpleDateFormat sdfDateAndTime = new SimpleDateFormat("dd-MM-yyyy # HH:mm:ss");
        String dateAndTime = sdfDateAndTime.format(now);
        
        return dateAndTime;
    }
    
    public static String getDate() {
        Date now = new java.util.Date();
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
        String date = sdfDate.format(now);
        
        return date;
    }
    
    public static String getTime() {
        Date now = new java.util.Date();
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        String time = sdfTime.format(now);
        
        return time;
    }
}