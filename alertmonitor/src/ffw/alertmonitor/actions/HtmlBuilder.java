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

import ffw.util.ConfigReader;
import ffw.util.ShellScript;

public class HtmlBuilder extends AlertAction {
    private String templateName;
    private String html = "";
    private Document doc;
    
    @Override
    public String getDescription() {
        return "shows the alert information as HTML-page in browser";
    }
    
    @Override
    public void run() {
        this.setTemplate(ConfigReader.getConfigVar("html-template"));
        if (!this.message.hasCoordinates()) {
            this.setTemplate("withoutGPS");
        }
        
        this.build();
        
        String fileName = this.writeHTML("html/alerts/");
        ShellScript.execute("open-browser", fileName);
    }
    
    public void setTemplate(String templateName) {
        this.templateName = templateName;
    }
    
    public void build() {
        this.loadTemplate("html/templates/" + this.templateName + ".html");
        
        String timestamp = String.valueOf(new java.util.Date().getTime() / 1000);
        this.setElement("#timestamp",    timestamp);
        if (!this.message.hasCoordinates()) {
            this.setElement("#latitude",     this.message.getLatitude());
            this.setElement("#longitude",    this.message.getLongitude());
        }
        this.setElement("#shortKeyword", this.message.getShortKeyword());
        this.setElement("#alertLevel",   this.message.getAlertLevel());
        
        for (int i=0; i<this.message.getKeywords().size(); i++) {
            Element tag = this.doc.select("#furtherKeywords ul").first();
            tag.append("<li>" + this.message.getKeywords().get(i) + "</li>");
        }
    }
    
    public String writeHTML(String path) {
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
                this.html = this.html + '\n' + line;
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
        
        this.doc = Jsoup.parse(this.html);
    }
    
    private void setElement(String cssSelector, String content) {
        if (content != null) {
            Element tag = this.doc.select(cssSelector).first();
            tag.html(content);
        }
    }
}
