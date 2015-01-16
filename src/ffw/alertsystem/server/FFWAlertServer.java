package ffw.alertsystem.server;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FFWAlertServer {
    
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(getConfigVar("port"));
        @SuppressWarnings("resource")
        DatagramSocket socket = new DatagramSocket(port);
        System.out.printf("start listening on port %d \n", port);
        
        while (true) {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            socket.receive(packet);
            
            InetAddress address = packet.getAddress();
            int         length  = packet.getLength();
            byte[]      data    = packet.getData();
            String      message = new String(data, 0, length);
            System.out.printf("Message from %s (length %d):\n%s\n",
                              address, length, message);
            String[] msg = message.split("#");
            
            
            HtmlTemplate template = new HtmlTemplate();
            template.loadTemplate("html/template.html");
            
            //String timestamp = String.valueOf(System.currentTimeMillis() );
            
            
            
            
            
            String timestamp = String.valueOf(new java.util.Date().getTime() / 1000);
            System.out.println(timestamp);
            template.setElement("#timestamp", timestamp);
            
            template.setElement("#tag1", msg[3]);
            template.setElement("#tag2", msg[4]);
            template.setElement("#tag3", msg[5]);
            template.setElement("#tag4", msg[6]);
            
            
            
            Date now = new java.util.Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss");
            String dateAndTime = sdf.format(now);
            
            String fileName = "html/" + dateAndTime + ".html";
            template.writeTemplate(fileName);
            
            Runtime.getRuntime().exec("sh script/alert.sh " + fileName);
            //Desktop.getDesktop().browse(new URI("file:///home/max/workspace/eclipse/FFWServer/html/template.html"));
            //Desktop.getDesktop().browse(new URI("file:///home/max/workspace/eclipse/FFWServer/html/template.html"));
            
        }
        
        //socket.close();
    }
    
    
    public static String getConfigVar(String varName) {
        BufferedReader bufReader = null;
        String varValue = "";
        
        try {
            System.out.println();
            
            File configFile = new File("config.txt");
            bufReader = new BufferedReader(new FileReader(configFile));
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
