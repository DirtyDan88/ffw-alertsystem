package ffw.alertmonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ffw.alertmonitor.actions.AlertMailInformer;
import ffw.alertmonitor.actions.AlertSpeaker;
import ffw.alertmonitor.actions.HtmlBuilder;
import ffw.alertmonitor.actions.TVController;
import ffw.alertmonitor.actions.TVController.TVAction;
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
            Message message = this.messageQueue.poll();
            if (message != null) {
                this.handleMessage(message);
                
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
    
    private void handleMessage(Message message) {
        String[] watchdogRICs = ConfigReader.getConfigVar("watchdog-rics").split(",");
        String[] alertRICs    = ConfigReader.getConfigVar("alert-rics").split(",");
        
        message.evaluateMessageHead();
        String msgRIC = message.getAddress();
        
        if (msgRIC != null) {
            for (String curRIC : watchdogRICs) {
                if (msgRIC.equals(curRIC) || curRIC.equals("*")) {
                    MessageLogger.log(message.getPocsag1200Str(), LogEvent.WATCHDOG);
                    this.resetWatchdog();
                    break;
                }
            }
            
            for (String curRIC : alertRICs) {
                if (msgRIC.equals(curRIC) || curRIC.equals("*")) {
                    MessageLogger.log(message.getPocsag1200Str(), LogEvent.ALERT);
                    this.executeAlertActions(message);
                    break;
                }
            }
        }
    }
    
    private void executeAlertActions(Message message) {
        message.evaluateAlphaString();
        
        StringBuilder actions = new StringBuilder();
        String tvModule      = ConfigReader.getConfigVar("tv-module");
        String browserModule = ConfigReader.getConfigVar("browser-module");
        String speechModule  = ConfigReader.getConfigVar("speech-module");
        String emailModule   = ConfigReader.getConfigVar("email-module");
        
        /* switch on TV */
        if (tvModule.equals("enable")) {
            TVController.send(TVAction.SWITCH_ON);
            actions.append("tv-module ");
        }
        
        /* open browser and show alert infos */
        if (browserModule.equals("enable")) {
            HtmlBuilder.build(message);
            actions.append("browser-module ");
        }
        
        /* start audio ouput */
        if (speechModule.equals("enable")) {
            AlertSpeaker.play(message);
            actions.append("speech-module ");
        }
        
        /* send alert infos via email */
        if (emailModule.equals("enable")) {
            AlertMailInformer.send(message);
            actions.append("email-module ");
        }
        
        ApplicationLogger.log("## alert was triggered, following actions were "
                            + "executed: " + actions, Application.ALERTMONITOR);
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
        
        Thread alertMonitorThread  = new Thread(alertMonitor);
        Thread alertListenerThread = new Thread(alertListener);
        alertMonitorThread.start();
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
        
        try {
            alertMonitorThread.join();
            alertListenerThread.join();
        } catch (InterruptedException e) {
            ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                  Application.ALERTMONITOR);
        }
    }
}