package ffw.alertsystem.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.jetty.util.log.Logger;


public class JettyLogger extends ffw.alertsystem.util.Logger 
                         implements org.eclipse.jetty.util.log.Logger {
  
  private String appName;
  
  public JettyLogger(String appName) {
    super(ffw.alertsystem.util.Logger.DEBUG);
    this.appName = "-jetty-" + appName;
    
    log("========================================================", true);
    log("JettyLogger setting: logLevel=" + getLogLevel() +
                            " application="+ appName , true);
  }
  
  
  
  @Override
  public void log(String message, boolean printWithTime) {
    File logFileDir = new File("data/logs/" + DateAndTime.getYearAndMonthName());
    logFileDir.mkdirs();
    
    String fileName = "log-" + DateAndTime.getDate() + appName + ".txt";
    File logFile = new File(logFileDir, fileName);
    
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(logFile, true);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    
    String dateAndTime = "";
    if (printWithTime) {
      dateAndTime = "[" + DateAndTime.get() + "]";
    } else {
      dateAndTime = "                       ";
    }
    
    new PrintStream(fos).println(dateAndTime + " " + message);
  }
  
  
  
  @Override
  public void debug(Throwable t) {
    StringWriter stackTrace = new StringWriter();
    t.printStackTrace(new PrintWriter(stackTrace));
    
    debug(stackTrace.toString(), true);
  }
  
  @Override
  public void debug(String arg0, Object... arg1) {
    StringBuilder txt = new StringBuilder(arg0);
    for (Object o : arg1) {
      txt.append(o.toString());
    }
    
    debug(txt.toString(), true);
  }
  
  @Override
  public void debug(String arg0, long arg1) {
    debug(arg0 + arg1, true);
  }
  
  @Override
  public void debug(String arg0, Throwable t) {
    StringWriter stackTrace = new StringWriter();
    t.printStackTrace(new PrintWriter(stackTrace));
    
    debug(arg0 + stackTrace.toString(), true);
  }
  
  
  
  @Override
  public Logger getLogger(String arg0) {
    return this;
  }
  
  @Override
  public String getName() {
    return "ffw-jetty-logger";
  }
  
  @Override
  public void ignore(Throwable arg0) {}
  
  
  
  @Override
  public void info(Throwable t) {
    StringWriter stackTrace = new StringWriter();
    t.printStackTrace(new PrintWriter(stackTrace));
    
    info(stackTrace.toString(), true);
  }
  
  @Override
  public void info(String arg0, Object... arg1) {
    StringBuilder txt = new StringBuilder(arg0);
    for (Object o : arg1) {
      txt.append(o.toString());
    }
    
    info(txt.toString(), true);
  }
  
  @Override
  public void info(String arg0, Throwable t) {
    StringWriter stackTrace = new StringWriter();
    t.printStackTrace(new PrintWriter(stackTrace));
    
    info(arg0 + stackTrace.toString(), true);
  }
  
  
  
  @Override
  public boolean isDebugEnabled() { return false; }
  
  @Override
  public void setDebugEnabled(boolean arg0) {}
  
  
  
  @Override
  public void warn(Throwable t) {
    StringWriter stackTrace = new StringWriter();
    t.printStackTrace(new PrintWriter(stackTrace));
    
    warn(stackTrace.toString(), true);
  }
  
  @Override
  public void warn(String arg0, Object... arg1) {
    StringBuilder txt = new StringBuilder(arg0);
    for (Object o : arg1) {
      txt.append(o.toString());
    }
    
    warn(txt.toString(), true);
  }
  
  @Override
  public void warn(String arg0, Throwable t) {
    StringWriter stackTrace = new StringWriter();
    t.printStackTrace(new PrintWriter(stackTrace));
    
    warn(arg0 + stackTrace.toString(), true);
  }

  
}
