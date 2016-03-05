package ffw.alertsystem.test.common;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import ffw.alertsystem.core.ApplicationLogger;
import ffw.alertsystem.core.Application.ApplicationType;



public abstract class CommonJunitTest {
  
  protected static ApplicationLogger log;
  
  protected static Thread loggerThread;
  
  
  
  @BeforeClass
  public static void setup() {
    log = new ApplicationLogger(5, ApplicationType.JUNIT_TESTS, false);
    
    loggerThread = new Thread(log);
    loggerThread.start();
  }
  
  @AfterClass
  public static void cleanup() {
    log.info("+++++++++++++++++ finished junit-tests ++++++++++++++++++", true);
    try {
      log.stop();
      loggerThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
}
