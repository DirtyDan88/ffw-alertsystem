package ffw.test;
import ffw.alertmonitor.actions.AlertAction;



public class AlertActionTestClass extends AlertAction {
  @Override
  public String getDescription() {
    return "Alertaction only for test purpose.";
  }
  
  @Override
  public void run() {
    @SuppressWarnings("unused")
    String messageString = message.getMessageString();
  }
}