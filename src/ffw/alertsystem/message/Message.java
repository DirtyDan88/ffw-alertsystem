package ffw.alertsystem.message;

import java.util.Vector;

/**
 * represents a POCSAG1200 message
 * @author max
 *
 */
public class Message {
    
    private String pocsag1200Str;
    
    private String address;
    private String function;
    private String alpha;
    private boolean complete = false;
    
    private String latitude;
    private String longitude;
    private Vector<String> keywords = new Vector<String>();
    
    public Message(String pocsag1200Str) {
        this.pocsag1200Str = pocsag1200Str;
    }
    
    
    
    public void evaluate() {
        //pocsag1200Str.startsWith("POCSAG1200:")
        
        this.complete =  pocsag1200Str.contains("Address:") && 
                         pocsag1200Str.contains("Function:") && 
                         pocsag1200Str.contains("Alpha:");
        
        if (this.complete) {
            int startIndex, endIndex;
            
            startIndex     = pocsag1200Str.indexOf("Address:") + 8;
            endIndex       = startIndex + 8;
            this.address   = pocsag1200Str.substring(startIndex, endIndex).trim();
            
            startIndex     = pocsag1200Str.indexOf("Function:") + 9;
            endIndex       = startIndex + 4;
            this.function  = pocsag1200Str.substring(startIndex, endIndex).trim();;
            
            startIndex     = pocsag1200Str.indexOf("Alpha:") + 6;
            this.alpha     = pocsag1200Str.substring(startIndex).trim();
        }
        
    }
    
    /*
    String[] alphaStr = this.cleanAlphaString(this.alpha);
    
    this.latitude = msgStr[0];
    this.longitude = msgStr[1];
    for (int i = 2; i < msgStr.length; i++) {
        this.keywords.add(msgStr[i]);
    }
    */
    
    
    private String[] cleanAlphaString(String alphaStr) {
        String[] params = alphaStr.split("/");
        String msgStr = "";
        
        for (int i = 0; i < params.length; i++) {
            String tmp = params[i].trim();
            
            if (!(tmp.startsWith("<") && tmp.endsWith(">")) && 
                    tmp.length() != 0) {
                msgStr = msgStr.concat(params[i] + "#");
            }
        }
        
        msgStr = msgStr.replace("�", "ae");
        msgStr = msgStr.replace("�", "oe");
        msgStr = msgStr.replace("�", "ue");
        msgStr = msgStr.replace("�", "ss");
        
        return msgStr.split("#");
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
        return this.complete;
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
