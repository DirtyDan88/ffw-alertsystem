package ffw.alertmonitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Queue;

import ffw.util.ConfigReader;



public class AlertListener implements Runnable {
    
    private Queue<Message> messageQueue = null;
    
    private boolean stopped = false;
    
    private int port;
    private DatagramSocket socket;
    
    private StringBuilder buffer;
    
    
    
    public AlertListener(Queue<Message> messageQueue) {
        this.messageQueue = messageQueue;
        this.buffer = new StringBuilder();
        
        this.port = Integer.parseInt(ConfigReader.getConfigVar("pocsag-port"));
        
        try {
            this.socket = new DatagramSocket(this.port);
            this.socket.setSoTimeout(10);
            
        } catch (SocketException e) {
            System.err.println("error during socket creation: " + e.getMessage());
        }
    }
    
    @Override
    public void run() {
        System.out.println(">> start listening on port " + port);
        
        while (!this.stopped) {
            /* listen for alerts */
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            try {
                this.socket.receive(packet);
            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            /* create new message from recieved string */
            int         length  = packet.getLength();
            byte[]      data    = packet.getData();
            String      recvStr = new String(data, 0, length);
            recvStr = recvStr.replaceAll("[\n\r]", "#");
            
            
            
            this.buffer.append(recvStr);
            System.out.println(">> received data, buffer is:       '" + buffer + "'");
            
            this.checkMessageComplete();
        }
        
        this.socket.close();
        System.out.println(">> stop listening on port " + port);
    }
    
    private void checkMessageComplete() {
        while (buffer.indexOf("#") != -1) {
            int start = buffer.indexOf("POCSAG1200:");
            int end = buffer.indexOf("#");
            
            
            if (start == -1) {
                buffer.setLength(0);
                
            } else if (start > end) {
                buffer.delete(0, end+1);
                
            } else if (start < end) {
                String pocsag1200Str = buffer.substring(start, end);
                this.messageQueue.offer(new Message(pocsag1200Str));
                
                buffer.delete(0, end+1);
                System.out.println(">> message complete, buffer is:    '" + buffer + "'");
            }
        }
    }
    
    public synchronized void stop() {
        this.stopped = true;
    }
}
