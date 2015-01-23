package ffw.alertsystem.message;

import java.util.Vector;

/**
 * represents a POCSAG1200 message
 * @author max
 *
 */
public class Message {
    
    private String pocsag1200Str;
    
    private String address = null;
    private String function = null;
    private String alpha = null;
    
    private boolean isComplete     = false;
    private boolean hasCoordinates = false;
    
    private String latitude;
    private String longitude;
    private Vector<String> keywords = new Vector<String>();
    
    public Message(String pocsag1200Str) {
        this.pocsag1200Str = pocsag1200Str;
    }
    
    
    
    public void evaluateMessageHead() {
        //pocsag1200Str.startsWith("POCSAG1200:")
        
        int startIndex, endIndex;
        boolean addressExits  = pocsag1200Str.contains("Address:");
        boolean functionExits = pocsag1200Str.contains("Function:");
        boolean alphaExits    = pocsag1200Str.contains("Alpha:");
        
        if (addressExits) {
            startIndex   = pocsag1200Str.indexOf("Address:") + 8;
            endIndex     = startIndex + 8;
            this.address = pocsag1200Str.substring(startIndex, endIndex).trim();
        }
        
        if (functionExits) {
            startIndex    = pocsag1200Str.indexOf("Function:") + 9;
            endIndex      = startIndex + 3;
            this.function = pocsag1200Str.substring(startIndex, endIndex).trim();
        }
        
        if (alphaExits) {
            startIndex = pocsag1200Str.indexOf("Alpha:") + 6;
            this.alpha = pocsag1200Str.substring(startIndex).trim();
        }
        
        this.isComplete =  addressExits && functionExits && alphaExits;
    }
    
    
    public void evaluateAlphaString() {
        if (this.getAlpha() != null) {
            String[] alphaStr = this.cleanAlphaString().split("#");
            
            
            if (isLatOrLong(alphaStr[0]) && isLatOrLong(alphaStr[1])) {
                /* alert with latitude and longitude */
                this.latitude  = alphaStr[0];
                this.longitude = alphaStr[1];
                
                for (int i = 2; i < alphaStr.length; i++) {
                    this.keywords.add(alphaStr[i]);
                }
                
                this.hasCoordinates = true;
                
            } else {
                /* alert without geo coordinates */
                for (int i = 0; i < alphaStr.length; i++) {
                    this.keywords.add(alphaStr[i]);
                }
            }
            
            
        }
    }
    
    private boolean isLatOrLong(String latOrlong) {
        return latOrlong.matches("\\d{1,2}\\.\\d{5,}");
    }
    
    private String cleanAlphaString() {
        String[] alphaStr  = this.getAlpha().split("/");
        String newAlphaStr = "";
        
        for (int i = 0; i < alphaStr.length; i++) {
            String tmp = alphaStr[i].trim();
            
            if (!(tmp.startsWith("<") && tmp.endsWith(">")) && 
                    tmp.length() != 0) {
                newAlphaStr = newAlphaStr.concat(alphaStr[i] + "#");
            }
        }
        
        newAlphaStr = newAlphaStr.replace("�", "ae");
        newAlphaStr = newAlphaStr.replace("�", "oe");
        newAlphaStr = newAlphaStr.replace("�", "ue");
        newAlphaStr = newAlphaStr.replace("�", "ss");
        
        return newAlphaStr;
    }
    
    public String getPocsag1200Str() {
        return this.pocsag1200Str;
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public String getFunction() {
        return this.function;
    }
    
    public String getAlpha() {
        return this.alpha;
    }
    
    public boolean isComplete() {
        return this.isComplete;
    }
    
    public boolean hasCoordinates() {
        return this.hasCoordinates;
    }
    
    public String getLatitude() {
        return this.latitude;
    }
    
    public String getLongitude() {
        return this.longitude;
    }
    
    public Vector<String> getKeywords() {
        return this.keywords;
    }
}
