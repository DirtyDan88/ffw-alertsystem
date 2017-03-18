package net.dirtydan.ffw.alertsystem.common.plugin;

import net.dirtydan.ffw.alertsystem.common.util.DateAndTime;
import net.dirtydan.ffw.alertsystem.common.util.Logger;



public class JunitLogger extends Logger {
  
  public JunitLogger() {
    super(5);
  }
  
  @Override
  public void log(String message, boolean printWithTime) {
    String dateAndTime = "";
    if (printWithTime) {
        dateAndTime = "[" + DateAndTime.get() + "] ";
    } else {
        dateAndTime = "                        ";
    }
    
    System.out.println(dateAndTime + message);
  }
  
}
