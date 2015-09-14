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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.UnsupportedAudioFileException;

import marytts.client.MaryClient;
import marytts.util.data.audio.AudioPlayer;
import marytts.util.http.Address;
import ffw.alertmonitor.AlertAction;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;
import ffw.util.ShellScript;



public class AlertSpeaker extends AlertAction {
  
  @Override
  public String getInfo() {
    return "speech output on audio-device";
  }
  
  @Override
  public void run() {
    ShellScript.execute("start-marytts-server");
    
    /* wait till the server is started */
    int time = Integer.parseInt(paramList.get("marytts-start-delay"));
    try {
      Thread.sleep(time * 1000);
    } catch (InterruptedException e) {
      ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                            Application.ALERTMONITOR);
    }
    
    String server = paramList.get("marytts-server");
    String voice  = paramList.get("marytts-voice");
    String effect = paramList.get("marytts-effect");
    int port      = Integer.parseInt(paramList.get("marytts-port"));
    int repeat    = Integer.parseInt(paramList.get("marytts-repeat"));
    int pause     = Integer.parseInt(paramList.get("marytts-pause"));
    String locale     = "de";
    String inputType  = "TEXT";
    String outputType = "AUDIO";
    String audioType  = "WAVE";
    String text       = this.buildText();
    
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      MaryClient.getMaryClient(new Address(server, port))
                .process(text, inputType, outputType, locale, audioType, 
                         voice, "", effect, "", outputStream);
      
      for (int i=0; i<repeat; i++) {
        Thread.sleep(1000 * pause);
        
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                new ByteArrayInputStream(outputStream.toByteArray()));
        AudioPlayer player = new AudioPlayer(inputStream, 
                new LineListener() {
                    @Override
                    public void update(LineEvent event) {}
                }
        );
        
        player.start();
        player.join();
      }
        
    } catch (IOException | InterruptedException |
             UnsupportedAudioFileException e) {
      ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                            Application.ALERTMONITOR);
    }
  }
  
  private String buildText() {
    StringBuilder text = new StringBuilder();
    String shortKeyword = message.getAlertSymbol();
    
    if (shortKeyword.equals("F") || shortKeyword.equals("B")) {
      text.append("Brandalarm");
    } else if (shortKeyword.equals("H") || shortKeyword.equals("T")) {
      text.append("Technische Hilfeleistung");
    } else if (shortKeyword.equals("G")) {
      text.append("Gefahrgutunfall");
    } else if (shortKeyword.equals("W")) {
      text.append("Einsatz auf Gewässer");
    } else if (shortKeyword.equals("BMA")) {
      text.append("Brandmeldeanlage");
    } else {
      text.append("Unbekannt");
    }
    text.append(". ");
    
    for (int i = 0; i < message.getKeywords().size(); i++) {
      String keyword = cleanKeyword(message.getKeywords().get(i));
      text.append(keyword);
      text.append(", ");
    }
    
    return text.toString();
  }
  
  // TODO: cleanKeyword-method
  private String cleanKeyword(String keyword) {
        if (keyword.contains("str. ")) {
            keyword = keyword.replace("str.", "straße");
        }
        
        /* workaround: known issues of marytts 5.1.2 */
        if (keyword.contains("brennende ")) {
            keyword = keyword.replace("brennende ", "brennender ");
        }
        
        return keyword;
    }
}
