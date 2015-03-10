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
    
    
    
    private static Queue<Message> messageStack;
    private static AlertMonitor   alertMonitor;
    private static AlertListener  alertListener;
    private static Thread         alertMonitorThread;
    private static Thread         alertListenerThread;
    
    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("-logInFile")) {
                ApplicationLogger.inFile = true;
            }
        }
        
        startApplication();
        
        /* application is either terminated via signal or user */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                ApplicationLogger.log("received SIGTERM", Application.ALERTMONITOR);
                stopApplication();
            }
        });
        
        BufferedReader console = new BufferedReader(
                                 new InputStreamReader(System.in));
        try {
            boolean quit = false;
            while (!quit) {
                if (console.read() == 'q') quit = true;
                Thread.sleep(100);
            }
            ApplicationLogger.log("stopped by user", Application.ALERTMONITOR);
            stopApplication();
            
        } catch (IOException | InterruptedException e) {
            ApplicationLogger.log("ERROR: " + e.getMessage(), 
                                  Application.ALERTMONITOR);
        }
    }
    
    private static void startApplication() {
        messageStack  = new ConcurrentLinkedQueue<Message>();
        alertMonitor  = new AlertMonitor(messageStack);
        alertListener = new AlertListener(messageStack);
        
        alertMonitorThread  = new Thread(alertMonitor);
        alertListenerThread = new Thread(alertListener);
        alertMonitorThread.start();
        alertListenerThread.start();
        
        ApplicationLogger.log("ffw-alertsystem started", Application.ALERTMONITOR);
    }
    
    private static void stopApplication() {
        alertListener.stop();
        alertMonitor.stop();
        
        try {
            alertMonitorThread.join();
            alertListenerThread.join();
        } catch (InterruptedException e) {
            ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                  Application.ALERTMONITOR);
        }
        
        ApplicationLogger.log("ffw-alertsystem stopped", Application.ALERTMONITOR);
    }
}