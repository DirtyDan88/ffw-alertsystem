package ffw.alertmonitor;

import java.util.LinkedList;
import java.util.List;

import ffw.alertmonitor.actions.AlertAction;
import ffw.util.ApplicationLogger;
import ffw.util.ConfigReader;
import ffw.util.ApplicationLogger.Application;

public class AlertActionManager {
    
    
    
    public static void executeActions(Message message) {
        
        StringBuilder actions = new StringBuilder();
        List<AlertAction> actionList = loadAlertActions();
        
        for (AlertAction action : actionList) {
            action.execute(message);
            actions.append(action.getDescription());
        }
        
        ApplicationLogger.log("## alert was triggered, following actions were "
                + "executed: " + actions, Application.ALERTMONITOR);
    }
    
    
    
    private static List<AlertAction> loadAlertActions() {
        String pkg = "ffw.alertmonitor.actions";
        String[] actionClassNames = ConfigReader.getConfigVar("actionClassNames").split(",");
        
        
        List<AlertAction> actionList = new LinkedList<AlertAction>();
        
        
        
        for (String actionClassName : actionClassNames) {
            try {
                Class<?> actionClass = Class.forName(pkg + "." + actionClassName);
                Class<?> superClass = actionClass.getSuperclass();
                
                if (superClass.equals(AlertAction.class)) {
                    actionList.add(
                        (AlertAction) actionClass.newInstance()
                    );
                }
                /*
                for(int i = 0; i < interfaces.length; i++) {
                    if (interfaces[i].getName().equals(pkg + "." + "AlertAction")) {
                        actionList.add(
                            (AlertAction) actionClass.newInstance()
                        );
                        break;
                    }
                }*/
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        return actionList;
    }
}
