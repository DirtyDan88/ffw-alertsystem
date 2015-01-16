package ffw.alertsystem.trigger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FFWAlertTrigger {
    
	/*
	 * 
	 * 49.33534/08.85469/4711/H1 Sturmschaden///Lobbachstr. 11/Meckesheim-Mönchzell// //Baum droht umzustürzen/
	 */
	
    public static void main(String[] args) {
        String message = createMessage(args);
        
        if (!message.isEmpty()) {
            byte[] rawData = message.getBytes();
            
            String ipAddr = getConfigVar("ipaddr");
            int port = Integer.parseInt(getConfigVar("port"));
            
            DatagramSocket dSocket = null;
            try {
                InetAddress inetaddr = InetAddress.getByName(ipAddr);
                DatagramPacket packet  = new DatagramPacket(rawData,
                                                            rawData.length,
                                                            inetaddr,
                                                            port);
                dSocket = new DatagramSocket();
                dSocket.send(packet);
                System.out.println("data was sent to " + ipAddr + ":" + port);
                
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (dSocket != null) 
                    dSocket.close();
            }
            
        } else {
            System.out.println("nothing to send");
        }
    }
    
    public static String createMessage(String[] args) {
        String recv = "";
        String message = "";
        
        for (int i = 0; i < args.length; i++) {
            recv = recv.concat(args[i] + " ");
        }
        
        String[] params = recv.split("/");
        for (int i = 0; i < params.length; i++) {
            String tmp = params[i].trim();
            
            if (!(tmp.startsWith("<") && tmp.endsWith(">")) && 
                    tmp.length() != 0) {
                message = message.concat(params[i] + "#");
            }
        }
        
        message = message.replace("�", "ae");
        message = message.replace("�", "oe");
        message = message.replace("�", "ue");
        message = message.replace("�", "ss");
        
        System.out.println(message);
        return message;
    }
    
    public static String getConfigVar(String varName) {
        BufferedReader bufReader = null;
        String varValue = "";
        
        try {
            System.out.println();
            
            File configFile = new File("config.txt");
            bufReader = new BufferedReader(new FileReader(
                    //new File("C:\\Users\\max\\Desktop\\FeuerwehrAlarmSystem\\config.txt")));
                    configFile));
            String line = null;
            
            while((line = bufReader.readLine()) != null) {
                if (!line.startsWith("#") && line.startsWith(varName)) {
                    varValue = line.split("=")[1];
                    break;
                }
            }
            
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(bufReader != null) {
                try {
                    bufReader.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return varValue;
    }
}
