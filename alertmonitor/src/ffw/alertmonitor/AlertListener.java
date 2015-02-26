package ffw.alertmonitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Queue;

import ffw.util.ApplicationLogger;
import ffw.util.ConfigReader;
import ffw.util.ApplicationLogger.Application;

public class AlertListener implements Runnable {
    private boolean stopped = false;
    
    private int            port;
    private DatagramSocket socket;
    private StringBuilder  buffer;
    private Queue<Message> messageQueue = null;
    
    public AlertListener(Queue<Message> messageQueue) {
        this.messageQueue = messageQueue;
        this.buffer       = new StringBuilder();
        
        this.port = Integer.parseInt(ConfigReader.getConfigVar("pocsag-port"));
        
        try {
            this.socket = new DatagramSocket(this.port);
            this.socket.setSoTimeout(10);
            
        } catch (SocketException e) {
            ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                  Application.ALERTMONITOR);
        }
    }
    
    @Override
    public void run() {
        ApplicationLogger.log(">> listener is listening (port: " + port + ")", 
                              Application.ALERTMONITOR);
        
        while (!this.stopped) {
            /* listen for alerts */
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            try {
                this.socket.receive(packet);
            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                      Application.ALERTMONITOR);
            }
            
            /* create new message from recieved string */
            int         length  = packet.getLength();
            byte[]      data    = packet.getData();
            String      recvStr = new String(data, 0, length);
            recvStr = recvStr.replaceAll("[\n\r]", "#");
            
            this.buffer.append(recvStr);
            ApplicationLogger.log(">> received data, buffer is:    '" + buffer + "'", 
                                  Application.ALERTMONITOR);
            
            this.checkMessageComplete();
        }
        
        this.socket.close();
        ApplicationLogger.log(">> listener stopped (port: " + port + ")",
                              Application.ALERTMONITOR);
    }
    
    private void checkMessageComplete() {
        while (buffer.indexOf("#") != -1) {
            int start = buffer.indexOf("POCSAG1200:");
            int end   = buffer.indexOf("#");
            
            if (start == -1) {
                buffer.setLength(0);
                
            } else if (start > end) {
                buffer.delete(0, end+1);
                
            } else if (start < end) {
                String pocsag1200Str = buffer.substring(start, end);
                this.messageQueue.offer(new Message(pocsag1200Str));
                
                buffer.delete(0, end+1);
                ApplicationLogger.log(">> message complete, "
                                    + "buffer is: '" + buffer + "'", 
                                      Application.ALERTMONITOR, false);
            }
        }
    }
    
    public synchronized void stop() {
        this.stopped = true;
    }
}
