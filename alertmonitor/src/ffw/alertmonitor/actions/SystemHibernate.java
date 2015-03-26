package ffw.alertmonitor.actions;

import ffw.util.TVController.TVAction;

public class SystemHibernate extends AlertAction {

    @Override
    public String getDescription() {
        return "switch off TV and close all open applications";
    }
    
    @Override
    public void run() {
        // TODO: after x min: 
        //TVController.sendCommand(TVAction.TURN_OFF);
    }
}
