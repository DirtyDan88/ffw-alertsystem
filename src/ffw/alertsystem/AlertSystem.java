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
                System.out.println("## got a message");
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
        
        msg.evaluate();
        
        
        
        String watchdogRIC = ConfigReader.getConfigVar("watchdog-ric");
        String[] listRICs  = ConfigReader.getConfigVar("ric-list").split(",");
        
        if (msg.getAddress().equals(watchdogRIC)) {
            this.resetWatchdog();
        }
        
        for (String RIC : listRICs) {
            
            
            if (msg.getAddress().equals(RIC) || RIC.equals("*")) {
                System.out.println("## alert !!!");
                MessageLogger.log(msg.getPocsag1200Str());
                break;
            }
        }
        
        
    }
    
    public void resetWatchdog() {
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

//POCSAG1200: Address:  160942  Function: 3  Alpha:   11288/bert/Opek/Magen-Darm-Zentrum M/Bismarckplatz 1/MA-Schwetzingerstadt/WB 3 Ida Scipio Heim/Murgstr. 4/MA-Neckarstadt/12, (laut Pflege geht der Bruder zum Bürgermeister sollte es nicht klappen)/
// Alpha:   <FF><DLE>POCSAG1200: Address:  157601  Function: 3


//49.52647/08.66811/26/F1 undefiniertes Kleinfeuer//vorm Kindergarten/Kurpfalzstr. /Weinheim-Lützelsachsen// //brennende Mülleimer/
// 8624/nefso/Koch/WB EG Caritas/Schönauer Str. 2/Plankstadt// //140 DD 142, 36/
// POCSAG1200: Address:  160942  Function: 3  Alpha:   11288/bert/Opek/Magen-Darm-Zentrum M/Bismarckplatz 1/MA-Schwetzingerstadt/WB 3 Ida Scipio Heim/Murgstr. 4/MA-Neckarstadt/12, (laut Pflege geht der Bruder zum Bürgermeister sollte es nicht klappen)/

// Alpha:   <FF><DLE>POCSAG1200: Address:  157601  Function: 3


/*
System.out.println("Latitude:  " + msg.getLatitude());
System.out.println("Longitude: " + msg.getLongitude());
for (String keyword : msg.getKeywords()) {
    System.out.println(keyword);
}

HtmlBuilder htmlBuilder = new HtmlBuilder();
htmlBuilder.build(msg);

String fileName = htmlBuilder.writeTemplate("html/alerts/");




//Runtime.getRuntime().exec("sh script/alert.sh " + fileName);
try {
    URI uri = new URI("file:///home/max/workspace/eclipse-java/ffw-alertsystem/"+fileName);
    Desktop.getDesktop().browse(uri);
} catch (URISyntaxException e) {
    e.printStackTrace();
}
*/