package ffw.alertsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Watchdog {
    
    private DatagramSocket socket;
    
    public Watchdog() {
        Date now = new java.util.Date();
        SimpleDateFormat sdfDateAndTime = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String dateAndTime = sdfDateAndTime.format(now);
        
        int port    = Integer.parseInt(ConfigReader.getConfigVar("watchdog-port"));
        int timeout = Integer.parseInt(ConfigReader.getConfigVar("watchdog-timeout"));
        
        System.out.println("** " + dateAndTime +" start watchdog (timeout: " + timeout + "s)");
        
        try {
            this.socket = new DatagramSocket(port);
            this.socket.setSoTimeout(1000 * 60 * timeout);
            
        } catch (SocketException e) {
            e.printStackTrace();
        }
        
        while (true) {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            
            try {
                this.socket.receive(packet);
                
                now = new java.util.Date();
                dateAndTime = sdfDateAndTime.format(now);
                
                int         length = packet.getLength();
                byte[]      data   = packet.getData();
                System.out.println("** " + dateAndTime +" watchdog says: " + new String(data, 0, length));
                
            } catch (SocketTimeoutException e) {
                /* TODO call the police or something */
                
                now = new java.util.Date();
                dateAndTime = sdfDateAndTime.format(now);
                System.out.println("** " + dateAndTime +" watchdog says: oh nooo!");
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        new Watchdog();
    }
}
