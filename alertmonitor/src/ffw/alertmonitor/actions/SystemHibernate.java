package ffw.alertmonitor.actions;

import ffw.util.ConfigReader;
import ffw.util.ShellScript;
import ffw.util.TVController;
import ffw.util.TVController.TVAction;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;

public class SystemHibernate extends AlertAction {

    @Override
    public String getDescription() {
        return "switches off TV and closes open applications after a given time";
    }
    
    @Override
    public void run() {
        int time = Integer.parseInt(ConfigReader.getConfigVar("system-hibernate-time", 
                                                              Application.ALERTMONITOR));
        try {
            Thread.sleep(time * 1000 * 60);
        } catch (InterruptedException e) {
            ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                  Application.ALERTMONITOR);
        }
        
        ShellScript.execute("close-applications");
        TVController.sendCommand(TVAction.TURN_OFF);
    }
}