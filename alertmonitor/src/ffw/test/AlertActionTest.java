package ffw.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ffw.alertlistener.AlertMessage;
import ffw.alertlistener.AlertMessageFactory;
import ffw.alertmonitor.AlertActionManager;
import ffw.util.config.ConfigFile;



public class AlertActionTest  {

  private final String pathToTestFiles = "test/";
  
  public static boolean action1WasExecuted = false;
  public static boolean action2WasExecuted = false;
  public static boolean action3WasExecuted = false;
  
  @Test
  public void testAlertActionManager() {
    ConfigFile.setFileName(pathToTestFiles + "validExampleConfig.xml");
    
    AlertMessage message = new AlertMessageTestClass("");
    message.evaluateMessageHead();
    AlertActionManager.executeActions(message);
    assertTrue(action1WasExecuted);
    assertFalse(action1WasExecuted);
    assertTrue(action3WasExecuted);
    
    // TODO: How to test caching mechanism?
  }
  
  @Test
  public void testAlertActionSandbox() {
    // Create and execute and action which operates on a null pointer
    AlertActionTestClass faultyAlertAction1 = new AlertActionTestClass();
    faultyAlertAction1.execute(null);
    // Make sure that this line is reached (= exception was caught)
    assertTrue(true);
    
    AlertActionTestClass faultyAlertAction2 = new AlertActionTestClass();
    faultyAlertAction2.execute(AlertMessageFactory.create(""));
    // Make sure that this line is reached (= exception was caught)
    assertTrue(true);
    
    AlertActionTestClass validAlertAction = new AlertActionTestClass();
    validAlertAction.execute(AlertMessageFactory.create("POCSAG string"));
    assertTrue(true);
  }
  

  
  

}
