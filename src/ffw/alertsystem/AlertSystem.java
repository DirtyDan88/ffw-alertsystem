package ffw.alertsystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ffw.alertsystem.listener.AlertListener;
import ffw.alertsystem.message.Message;
import ffw.alertsystem.message.MessageLogger;
import ffw.alertsystem.message.MessageLogger.LogEvent;

public class AlertSystem implements Runnable {
    
    private Queue<Message> messageQueue = null;
    
    private boolean stopped = false;
    
    
    
    
    public AlertSystem(Queue<Message> messageQueue) {
        this.messageQueue = messageQueue;
    }
    
    @Override
    public void run() {
        System.out.println("## start alertsystem");
        
        while (!this.stopped) {
            /* check if there are new messages */
            Message msg = this.messageQueue.poll();
            if (msg != null) {
                System.out.println("## got the message");
                this.handleMessage(msg);
                
            } else {
                /* wait a little bit */
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        
        System.out.println("## stop alertsystem");
    }
    
    private void handleMessage(Message msg) {
        String[] watchdogRICs = ConfigReader.getConfigVar("watchdog-rics").split(",");
        String[] alertRICs    = ConfigReader.getConfigVar("alert-rics").split(",");
        
        msg.evaluateMessageHead();
        String msgRIC = msg.getAddress();
        
        if (msgRIC != null) {
            for (String curRIC : watchdogRICs) {
                if (msgRIC.equals(curRIC) || curRIC.equals("*")) {
                    MessageLogger.log(msg.getPocsag1200Str(), LogEvent.WATCHDOG);
                    this.resetWatchdog();
                    break;
                }
            }
            
            for (String curRIC : alertRICs) {
                if (msgRIC.equals(curRIC) || curRIC.equals("*")) {
                    MessageLogger.log(msg.getPocsag1200Str(), LogEvent.ALERT);
                    this.triggerAlert(msg);
                    break;
                }
            }
        }
    }
    
    private void triggerAlert(Message msg) {
        msg.evaluateAlphaString();
        
        if (msg.hasCoordinates()) {
            System.out.println("## alert was triggered");
            
            HtmlBuilder htmlBuilder = new HtmlBuilder(msg);
            String fileName = htmlBuilder.writeTemplate("html/alerts/");
            
            try {
                String osName = System.getProperty("os.name");
                if (osName.endsWith("Windows")) {
                    Runtime.getRuntime().exec("script/alert.bat " + fileName);
                } else {
                    Runtime.getRuntime().exec("sh script/alert.sh " + fileName);
                } 
                
                
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            
        } else {
            System.out.println("## alert was triggered, but no geo coordinates are given");
        }
    }
    
    private void resetWatchdog() {
        int port          = Integer.parseInt(ConfigReader.getConfigVar("watchdog-port"));
        String addressStr = ConfigReader.getConfigVar("watchdog-addr");
        byte[] buf        = "I am alive!".getBytes();
        
        System.out.println("## reset watchdog on: " + addressStr + ":" + port);
        
        try {
            InetAddress address   = InetAddress.getByName(addressStr);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            DatagramSocket socket = new DatagramSocket();
            
            socket.send(packet);
            socket.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized void stop() {
        this.stopped = true;
    }
    
    
    
    public static void main(String[] args) {
        Queue<Message> messageStack  = new ConcurrentLinkedQueue<Message>();
        AlertSystem    alertSystem   = new AlertSystem(messageStack);
        AlertListener  alertListener = new AlertListener(messageStack);
        
        Thread alertSystemThread   = new Thread(alertSystem);
        Thread alertListenerThread = new Thread(alertListener);
        
        alertSystemThread.start();
        alertListenerThread.start();
        
        BufferedReader console = new BufferedReader(
                                     new InputStreamReader(System.in));
        try {
            boolean quit = false;
            while (!quit) {
                if (console.read() == 'q') quit = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        alertListener.stop();
        alertSystem.stop();
    }
}