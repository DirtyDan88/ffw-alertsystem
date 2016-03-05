package ffw.alertsystem.test.common;

import ffw.alertsystem.core.message.Message;



public class MessageTestClass extends Message {
  
  public MessageTestClass() {
    super("");
  }
  
  @Override
  public void evaluateMessageHead() {
    address = "";
    
    //isEncrypted = 
  }
  
  @Override
  public void evaluateMessage() {
    
  }
  
  @Override
  public boolean isValid() {
    return true;
  }
  
  @Override
  public String getType() {
    return "JUNIT_TEST_MESSAGE";
  }
  
}
