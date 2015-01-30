package ffw.alertsystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ConfigReader {
    
    private ConfigReader() {}
    
    public static String getConfigVar(String varName) {
        BufferedReader bufReader = null;
        String varValue = "";
        String line = null;
        Boolean found = false;
        
        try {
            File configFile = new File("config.txt");
            bufReader = new BufferedReader(new FileReader(configFile));
            
            
            while((line = bufReader.readLine()) != null) {
                if (!line.startsWith("#") && line.startsWith(varName)) {
                    varValue = line.split("=")[1];
                    found = true; 
                    break;
                }
            }
            
            if (!found) {
                /* TODO do something very intelligent */
            }
            
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(bufReader != null) {
                try {
                    bufReader.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return varValue;
    }
}
