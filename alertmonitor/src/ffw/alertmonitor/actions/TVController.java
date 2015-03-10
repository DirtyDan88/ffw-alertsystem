/*
    Copyright (c) 2015, Max Stark <max.stark88@web.de> 
        All rights reserved.
    
    This file is part of ffw-alertsystem, which is free software: you 
    can redistribute it and/or modify it under the terms of the GNU 
    General Public License as published by the Free Software Foundation, 
    either version 2 of the License, or (at your option) any later 
    version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    General Public License for more details. 
    
    You should have received a copy of the GNU General Public License 
    along with this program; if not, see <http://www.gnu.org/licenses/>.
*/

package ffw.alertmonitor.actions;

import java.io.IOException;
import java.io.OutputStream;

import gnu.io.*;
import ffw.util.ApplicationLogger;
import ffw.util.ConfigReader;
import ffw.util.ApplicationLogger.Application;

public class TVController implements Runnable {
    public enum TVAction {
        SWITCH_ON, 
        TURN_OFF, 
        GET_STATUS;
        
        public String getCommandString() {
            switch(this) {
                case SWITCH_ON:  return "#AN";
                case TURN_OFF:   return "#AU";
                case GET_STATUS: return "#ST";
                default: throw new IllegalArgumentException();
            }
        }
        public String getStatusString() {
            switch(this) {
                case SWITCH_ON:  return "AN";
                case TURN_OFF:   return "AU";
                default: throw new IllegalArgumentException();
            }
        }
    }
    TVAction action;
    
    public TVController(TVAction action) {
        this.action = action;
    }
    
    public static void send(TVAction action) {
        new Thread(new TVController(action)).start();
    }
    
    @Override
    public void run() {
        SerialPort serialPort = null;
        SerialWriter writer   = null;
        try {
            serialPort = connect(ConfigReader.getConfigVar("serial-port"));
            writer = new SerialWriter(serialPort.getOutputStream());
            serialPort.notifyOnDataAvailable(true);
            
        } catch (IOException | NoSuchPortException | 
                 UnsupportedCommOperationException | 
                 PortInUseException e) {
            ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                  Application.ALERTMONITOR);
        }
        
        if (serialPort != null) {
            ApplicationLogger.log("$$ serial connection is good, try to send command", 
                                  Application.ALERTMONITOR);
            
            /* try it several times, in case of some unexpected error */
            for (int i=0; i<5; i++) {
                writer.write(this.action.getCommandString());
                
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                          Application.ALERTMONITOR);
                }
            }
            
            serialPort.close();
            ApplicationLogger.log("$$ serial connection closed", 
                                  Application.ALERTMONITOR);
        }
    }
    
    private static SerialPort connect(String portName) throws NoSuchPortException, 
                                                              UnsupportedCommOperationException, 
                                                              PortInUseException {
        
        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
        
        if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL &&
                !portId.isCurrentlyOwned()) {
            SerialPort serialPort = (SerialPort) portId.open("ffw-alertmonitor", 2000);
            serialPort.setSerialPortParams(
                    9600, 
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE
                );
            return serialPort;
        }
        
        return null;
    }
    
    private class SerialWriter {
        private OutputStream outStream;
        
        public SerialWriter(OutputStream outStream) {
            this.outStream = outStream;
        }
        
        public void write(String message) {
            try {
                byte[] msg  = message.getBytes();
                byte[] cr   = new byte[]{0x0D}; //CR
                byte[] data = new byte[msg.length + cr.length];
                
                for (int i=0; i<data.length; i++) {
                    data[i] = i < msg.length ? msg[i] : cr[i-msg.length];
                }
                
                this.outStream.write(data);
                this.outStream.flush();
                
            } catch (IOException e) {
                ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                                      Application.ALERTMONITOR);
            }
        }
    }
    
    /*
    private class SerialReader implements Runnable, SerialPortEventListener {
        private boolean stopped = false;
        private InputStream inStream;
        private volatile String message;
        
        public SerialReader(InputStream inStream) {
            this.inStream = inStream;
            
        }
        
        public void run() {
            while (!this.stopped) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                byte[] buf = new byte[1024];
                
                try {
                    while (this.inStream.available() > 0) {
                        this.inStream.read(buf);
                    }
                    this.setMessage(new String(buf));
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private synchronized void setMessage(String message) {
            this.message = message;
        }
        
        public synchronized String getMessage() {
            return this.message;
        }
        
        public synchronized void stop() {
            this.stopped = true;
        }
    }
    */
}
