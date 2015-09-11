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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ffw.alertmonitor.AlertAction;
import ffw.util.ShellScript;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class HtmlBuilder extends AlertAction {
  private String templateName;
  private String html = "";
  private Document doc;
  
  @Override
  public String getInfo() {
    return "shows the alert information as HTML-page in browser";
  }
  
  @Override
  public void run() {
    templateName = paramList.get("html-template");
    
    if (message.hasCoordinates()) {
      ApplicationLogger.log("## HTMLBuilder: map loaded with GPS coordinates",
                            Application.ALERTMONITOR, false);
    } else if (!message.getStreet().equals("")) {
      ApplicationLogger.log("## HTMLBuilder: map loaded with streetname and village",
                            Application.ALERTMONITOR, false);
    } else {
      ApplicationLogger.log("## HTMLBuilder: load html-template without map",
                            Application.ALERTMONITOR, false);
      //TODO: write working template for that scenario
      // templateName = "withoutMap";
    }
    
    build();
    
    String fileName = writeHTML("html/alerts/");
    ShellScript.execute("open-browser", fileName);
    
    doc = null;
    html = "";
  }
  
  
  
  private void build() {
    loadTemplate("html/templates/" + templateName + ".html");
    
    setElement("#timestamp",    message.getTimestamp());
    setElement("#alertSymbol",  message.getAlertSymbol());
    setElement("#alertLevel",   message.getAlertLevel());
    setElement("#alertKeyword", message.getAlertKeyword());
    
    setElement("#mapProvider", paramList.get("html-map-provider"));
    if (message.hasCoordinates()) {
      setElement("#latitude",  message.getLatitude());
      setElement("#longitude", message.getLongitude());
    }
    
    setElement("#street",           message.getStreet());
    setElement("#village",          message.getVillage());
    setElement("#furtherPlaceDesc", message.getFurtherPlaceDescAsString());
    
    for (int i = 0; i < message.getKeywords().size(); i++) {
      Element tag = doc.select("#furtherKeywords ul").first();
      tag.append("<li>" + message.getKeywords().get(i) + "</li>");
    }
  }
  
  private String writeHTML(String path) {
    // TODO: use DateAndTime class
    Date now = new java.util.Date();
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss");
    String dateAndTime = sdf.format(now);
    
    String fileName = path + dateAndTime + ".html";
    FileWriter writer = null;
    
    try {
      writer = new FileWriter(new File(fileName));
      writer.write(doc.toString());
        
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch(IOException e) {
          e.printStackTrace();
        }
      }
    }
    
    return fileName;
  }
  
  
  
  private void loadTemplate(String templateFile) {
    BufferedReader bufReader = null;
    
    try {
      bufReader = new BufferedReader(new FileReader(templateFile));
      String line;
      
      while((line = bufReader.readLine()) != null) {
        html = html + '\n' + line;
      }
      
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      if(bufReader != null) {
        try {
          bufReader.close();
        } catch(IOException e) {
          e.printStackTrace();
        }
      }
    }
    
    doc = Jsoup.parse(html);
  }
  
  private void setElement(String cssSelector, String content) {
    if (content != null) {
      Element tag = doc.select(cssSelector).first();
      tag.html(content);
    }
  }
}
