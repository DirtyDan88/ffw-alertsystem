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

package ffw.alertsystem.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * Collection of helper methods for timestamp <-> date transformation.
 */
public class DateAndTime {
  
  // in order to get the english day names, independent of system-locale
  private static String[] dayNames = {
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday",
    "Sunday"
  };
  
  // in order to get the english month names, independent of system-locale
  private static String[] monthNames = { 
    "Jan", //"January", 
    "Feb", //"February",
    "Mar", //"March",
    "Apr", //"April",
    "May", //"May",
    "June", //"June",
    "July", //"July",
    "Aug", //"August",
    "Sept", //"September",
    "Oct", //"October",
    "Nov", //"November",
    "Dec" //"December"
  };
  
  
  
  public static String getTimestamp() {
    return String.valueOf(new java.util.Date().getTime() / 1000);
  }
  
  public static String get() {
    long timestamp = new java.util.Date().getTime() / 1000;
    return get(String.valueOf(timestamp));
  }
  
  public static String get(String timestamp) {
    Date date = new Date(
                  new Timestamp(Long.parseLong(timestamp) * 1000).getTime()
                );
    
    SimpleDateFormat sdfDateAndTime = new SimpleDateFormat("dd-MM-yyyy # HH:mm:ss");
    String dateAndTime = sdfDateAndTime.format(date);
    
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
  
  public static String getYearAndMonthName() {
    Date now = new java.util.Date();
    SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
    SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
    
    int monthNumber = Integer.parseInt(sdfMonth.format(now));
    String yearAndMonthName = sdfYear.format(now) + "-" + 
                              sdfMonth.format(now) + "-" +
                              monthNames[monthNumber - 1];
    
    return yearAndMonthName;
  }
  
  public static String getWeekday() {
    Date now = new java.util.Date();
    SimpleDateFormat sdfDay = new SimpleDateFormat("u");
    
    int dayNumber = Integer.parseInt(sdfDay.format(now));
    return dayNames[dayNumber - 1];
  }
  
}