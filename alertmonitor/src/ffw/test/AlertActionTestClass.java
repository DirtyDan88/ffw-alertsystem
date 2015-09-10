package ffw.test;
import ffw.alertmonitor.AlertAction;



public class AlertActionTestClass extends AlertAction {
  
  @Override
  public String getInfo() {
    return "test class";
  }
  
  @Override
  public void run() {
    @SuppressWarnings("unused")
    String messageString = message.getMessageString();
    
    if (this.getInstanceName().equals("AlertActionExample1")) {
      AlertActionTest.action1WasExecuted = true;
    }
    if (this.getInstanceName().equals("AlertActionExample2")) {
      AlertActionTest.action2WasExecuted = true;
    }
    if (this.getInstanceName().equals("AlertActionExample3")) {
      AlertActionTest.action3WasExecuted = true;
    }
  }
}