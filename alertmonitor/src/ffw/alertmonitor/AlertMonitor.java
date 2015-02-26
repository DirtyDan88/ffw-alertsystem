package ffw.alertmonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ffw.alertmonitor.TVController.TVAction;
import ffw.util.ApplicationLogger;
import ffw.util.ConfigReader;
import ffw.util.MessageLogger;
import ffw.util.ApplicationLogger.Application;
import ffw.util.MessageLogger.LogEvent;

public class AlertMonitor implements Runnable {
    private boolean stopped = false;
    private Queue<Message> messageQueue = null;
    
    public AlertMonitor(Queue<Message> messageQueue) {
        this.messageQueue = messageQueue;
    }
    
    @Override
    public void run() {
        ApplicationLogger.log("## monitor is waiting for messages", 
                              Application.ALERTMONITOR);
        
        while (!this.stopped) {
            /* check if there are new messages */
            Message msg = this.messageQueue.poll();
            if (msg != null) {
                this.handleMessage(msg);
                
            } else {
                /* wait a little bit */
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                          Application.ALERTMONITOR);
                }
            }
        }
        
        ApplicationLogger.log("## monitor stopped", 
                              Application.ALERTMONITOR);
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
                    // TODO: reset watchdog for each incoming message? 
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
            /* try to switch on TV */
            TVController.send(TVAction.SWITCH_ON);
            
            HtmlBuilder htmlBuilder = new HtmlBuilder(msg);
            String fileName = htmlBuilder.writeTemplate("html/alerts/");
            
            try {
                String osName = System.getProperty("os.name");
                if (osName.contains("Windows")) {
                    Runtime.getRuntime().exec("script/alert.bat " + fileName);
                } else {
                    Runtime.getRuntime().exec("sh script/alert.sh " + fileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
                ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                      Application.ALERTMONITOR);
            }
            
            ApplicationLogger.log("## alert was triggered", 
                                  Application.ALERTMONITOR);
            
        } else {
            // TODO: show template without map?
            ApplicationLogger.log("## alert was triggered, but no geo "
                                + "coordinates are given", 
                                Application.ALERTMONITOR);
        }
    }
    
    private void resetWatchdog() {
        int port          = Integer.parseInt(ConfigReader.getConfigVar("watchdog-port"));
        String addressStr = ConfigReader.getConfigVar("watchdog-addr");
        byte[] buf        = "I am alive!".getBytes();
        
        ApplicationLogger.log("## reset watchdog on: " + addressStr + 
                              ":" + port, Application.ALERTMONITOR);
        
        try {
            InetAddress address   = InetAddress.getByName(addressStr);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            DatagramSocket socket = new DatagramSocket();
            
            if (addressStr.equals("255.255.255.255")) {
                socket.setBroadcast(true);
            }
            socket.send(packet);
            socket.close();
            
        } catch (IOException e) {
            e.printStackTrace();
            ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                  Application.ALERTMONITOR);
        }
    }
    
    public synchronized void stop() {
        this.stopped = true;
    }
    
    
    
    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("-logInFile")) {
                ApplicationLogger.inFile = true;
            }
        }
        
        ApplicationLogger.log("ffw-alertsystem started", Application.ALERTMONITOR);
        
        Queue<Message> messageStack  = new ConcurrentLinkedQueue<Message>();
        AlertMonitor   alertMonitor  = new AlertMonitor(messageStack);
        AlertListener  alertListener = new AlertListener(messageStack);
        
        Thread alertSystemThread   = new Thread(alertMonitor);
        Thread alertListenerThread = new Thread(alertListener);
        alertSystemThread.start();
        alertListenerThread.start();
        
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        try {
            boolean quit = false;
            while (!quit) {
                if (console.read() == 'q') quit = true;
            }
        } catch (IOException e) {
            ApplicationLogger.log("ERROR: " + e.getMessage(), 
                                  Application.ALERTMONITOR);
        }
        
        ApplicationLogger.log("ffw-alertsystem stopped", Application.ALERTMONITOR);
        
        alertListener.stop();
        alertMonitor.stop();
    }
}