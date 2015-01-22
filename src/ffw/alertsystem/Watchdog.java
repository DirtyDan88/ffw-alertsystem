package ffw.alertsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Watchdog {
    
    private DatagramSocket socket;
    
    public Watchdog() {
        System.out.println("** start watchdog");
        
        int port    = Integer.parseInt(ConfigReader.getConfigVar("watchdog-port"));
        int timeout = Integer.parseInt(ConfigReader.getConfigVar("watchdog-timeout"));
        
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
                
                int         length  = packet.getLength();
                byte[]      data    = packet.getData();
                System.out.println("** watchdog says: " + new String(data, 0, length));
                
            } catch (SocketTimeoutException e) {
                /* TODO call the police or something */
                System.out.println("** watchdog says: oh nooo!");
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        new Watchdog();
    }
}
