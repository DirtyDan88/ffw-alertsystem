package ffw.test;

import ffw.alertlistener.AlertMessage;

public class AlertMessageTestClass extends AlertMessage {

  public AlertMessageTestClass(String messageString) {
    super(messageString);
  }

  @Override
  public void evaluateMessageHead() {
    this.address = "42";
  }

  @Override
  public boolean evaluateMessage() {
    return false;
  }

  
  @Override
  public String getType() {
    return "test message";
  }
}
