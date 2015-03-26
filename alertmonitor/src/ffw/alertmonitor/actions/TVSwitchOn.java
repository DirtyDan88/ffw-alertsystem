package ffw.alertmonitor.actions;

import ffw.util.TVController;
import ffw.util.TVController.TVAction;

public class TVSwitchOn extends AlertAction {
    
    @Override
    public String getDescription() {
        return "switch on the TV";
    }
    
    @Override
    public void run() {
        TVController.sendCommand(TVAction.SWITCH_ON);
    }
}
