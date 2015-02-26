package ffw.watchdog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import ffw.util.ApplicationLogger;
import ffw.util.ConfigReader;
import ffw.util.DateAndTime;
import ffw.util.ApplicationLogger.Application;

public class Watchdog {
    private DatagramSocket socket;
    
    private void run() {
        int port    = Integer.parseInt(ConfigReader.getConfigVar("watchdog-port"));
        int timeout = Integer.parseInt(ConfigReader.getConfigVar("watchdog-timeout"));
        
        try {
            this.socket = new DatagramSocket(port);
            this.socket.setSoTimeout(1000 * 60 * timeout);
            
        } catch (SocketException e) {
            ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                  Application.WATCHDOG);
        }
        
        ApplicationLogger.log("watchdog started (timeout: " + timeout + " min)", 
                              Application.WATCHDOG);
        
        while (true) {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            
            try {
                /* wait for watchdog-reset */
                this.socket.receive(packet);
                
                int    length = packet.getLength();
                byte[] data   = packet.getData();
                ApplicationLogger.log("watchdog says: " + new String(data, 0, length), 
                                      Application.WATCHDOG);
                
            } catch (SocketTimeoutException e) {
                ApplicationLogger.log("watchdog says: oh nooo!", 
                                      Application.WATCHDOG);
                this.sendMail();
                
            } catch (IOException e) {
                ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                      Application.WATCHDOG);
            }
        }
    }
    
    private void sendMail() {
        String userName   = "ffw-moe-geraetehaus@web.de";
        String passWord   = "R8A825Tm";
        String recipients = ConfigReader.getConfigVar("watchdog-recipients");
        String subject    = "[ffw-alertsystem] !! watchdog timeout !!";
        String text       = "Watchdog timeout at " + DateAndTime.get() + "\n";
        
        Mail.send(userName, passWord, recipients, subject, text);
        ApplicationLogger.log("send mail to: " + recipients, 
                              Application.WATCHDOG, false);
    }
    
    
    
    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("-logInFile")) {
                ApplicationLogger.inFile = true;
            }
        }
        
        new Watchdog().run();
    }
}