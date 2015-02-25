package ffw.watchdog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import ffw.util.ConfigReader;
import ffw.util.DateAndTime;

public class Watchdog {
    private DatagramSocket socket;
    
    private void run() {
        int port    = Integer.parseInt(ConfigReader.getConfigVar("watchdog-port"));
        int timeout = Integer.parseInt(ConfigReader.getConfigVar("watchdog-timeout"));
        
        try {
            this.socket = new DatagramSocket(port);
            this.socket.setSoTimeout(1000 * 60 * timeout);
            
        } catch (SocketException e) {
            e.printStackTrace();
        }
        
        System.out.println("[" + DateAndTime.get() +"] watchdog started (timeout: " 
                         + timeout + " min)");
        
        while (true) {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            
            try {
                /* wait for watchdog-reset */
                this.socket.receive(packet);
                
                int    length = packet.getLength();
                byte[] data   = packet.getData();
                System.out.println("[" + DateAndTime.get() +"] watchdog says: " 
                                 + new String(data, 0, length));
                
            } catch (SocketTimeoutException e) {
                System.out.println("[" + DateAndTime.get() +"] watchdog says: "
                                 + "oh nooo!");
                this.sendMail();
                
            } catch (IOException e) {
                e.printStackTrace();
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
        System.out.println("** send mail to: " + recipients);
    }
    
    
    
    public static void main(String[] args) {
        new Watchdog().run();
    }
}