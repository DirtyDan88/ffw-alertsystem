package ffw.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ffw.alertlistener.AlertMessageFactory;



public class AlertActionTest  {


  
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
