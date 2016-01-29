/*
  Copyright (c) 2015-2016, Max Stark <max.stark88@web.de>
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

package ffw.alertsystem.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import gnu.io.*;



public class TVController {
  
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
        case SWITCH_ON:  return "#AN";
        case TURN_OFF:   return "#AU";
        default: throw new IllegalArgumentException();
      }
    }
    
  }
  
  
  
  public static void sendCommand(TVAction action,
                                 String strSerialPort,
                                 Logger log) {
    SerialPort serialPort = null;
    SerialWriter writer   = null;
    SerialReader reader   = null;
    
    try {
      serialPort = connect(strSerialPort);
      serialPort.notifyOnDataAvailable(true);
      
      writer = new SerialWriter(serialPort.getOutputStream(), log);
      reader = SerialReader.createInstance(serialPort.getInputStream(), log);
      serialPort.addEventListener(reader);
        
    } catch (IOException | NoSuchPortException | 
             UnsupportedCommOperationException | 
             PortInUseException | TooManyListenersException e) {
      log.error("could not connect to serial port '" + strSerialPort + "'", e);
    }
    
    if (serialPort != null) {
      log.info("serial connection is good, try to send command");
      
      boolean received = false;
      // try it several times, in case of some unexpected error 
      for (int i = 0; (i < 10 && !received); i++) {
        writer.write(action.getCommandString());
        
        // TODO: BUG: thats what we get here is not the response but 
        //            the echo of the own message, hence its always true
        String response = reader.getResponse();
        
        response = response.substring(0, 3);
        
        log.info("sended   '" + action.getCommandString() + "'");
        log.info("received '" + response + "'");
        log.info("expected '" + action.getStatusString());
          
        if (response.equals(action.getStatusString())) {
          received = true;
        } else {
          try {
            Thread.sleep(3000);
          } catch (InterruptedException e) {
            log.error("TVController was interrupted while it was trying to " +
                      "send the command several times", e);
          }
        }
      }
        
      reader.stop();
      serialPort.close();
      
      log.info("serial connection closed");
    }
  }
  
  private static SerialPort connect(String portName) throws NoSuchPortException,
                                                            UnsupportedCommOperationException,
                                                            PortInUseException {
    CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
    
    if ( portId.getPortType() == CommPortIdentifier.PORT_SERIAL &&
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
  
  
  
  private static class SerialWriter {
    private OutputStream outStream;
    private Logger log;
    
    public SerialWriter(OutputStream outStream, Logger log) {
      this.outStream = outStream;
      this.log = log;
    }
    
    public void write(String message) {
      try {
        byte[] msg  = message.getBytes();
        byte[] cr   = new byte[]{0x0D}; //CR
        byte[] data = new byte[msg.length + cr.length];
        
        for (int i=0; i<data.length; i++) {
            data[i] = i < msg.length ? msg[i] : cr[i-msg.length];
        }
        
        outStream.write(data);
        outStream.flush();
        
      } catch (IOException e) {
        log.error("SerialWriter could not write to output-stream of " +
                  "serial-port", e);
      }
    }
    
  }
  
  private static class SerialReader implements Runnable,
                                               SerialPortEventListener {
    private InputStream inStream;
    private volatile String message = null;
    private Logger log;
    private boolean stopped = false;
    
    public static SerialReader createInstance(InputStream inStream, Logger log) {
      SerialReader instance = new SerialReader(inStream, log);
      new Thread(instance).start();
      
      return instance;
    }
    
    private SerialReader(InputStream inStream, Logger log) {
      this.inStream = inStream;
      this.log = log;
    }
    
    public void run() {
      while (!stopped) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          log.error("SerialReader-thread was interrupted", e);
        }
      }
    }
    
    @Override
    public void serialEvent(SerialPortEvent event) {
      if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
        byte[] buf = new byte[1024];
        
        //TODO: why checked twice?
        if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
          try {
            while (inStream.available() > 0) {
              inStream.read(buf);
            }
            setMessage(new String(buf));
              
          } catch (IOException e) {
            log.error("SerialReader could not read from input-stream of " +
                      "serial-port", e);
          }
        }
      }
    }
    
    private synchronized void setMessage(String message) {
      this.message = message;
    }
    
    private synchronized String getMessage() {
      return message;
    }
    
    public String getResponse() {
      String response = null;
      response = getMessage();
      
      while (response == null) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          log.error("while try to get response from SerialReader", e);
        }
        
        response = getMessage();
      }
      
      setMessage(null);
      return response;
    }
    
    public synchronized void stop() {
      stopped = true;
    }
    
  }
  
}
