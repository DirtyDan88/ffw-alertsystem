package ffw.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ffw.util.DateAndTime;

public class DateAndTimeTest {
  
  @Test
  public void testGet() {
    long timestamp = 1440799204;
    
    String formattedDate = DateAndTime.get(timestamp);
    // "dd-MM-yyyy # HH:mm:ss"
    assertEquals("29-08-2015 # 00:00:04", formattedDate);
  }
  
  // TODO: test all DateAndTime methods;
}
