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

import ffw.alertmonitor.Message;

public class HtmlBuilder {
    private Message message;
    private String templateName;
    private String html = "";
    private Document doc;
    
    

    public void setMessage(Message message) {
        this.message = message;
    }
    
    public void setTemplate(String templateName) {
        this.templateName = templateName;
    }
    
    public void build() {
        this.loadTemplate("html/templates/" + this.templateName + ".html");
        
        String timestamp = String.valueOf(new java.util.Date().getTime() / 1000);
        this.setElement("#timestamp",    timestamp);
        this.setElement("#latitude",     this.message.getLatitude());
        this.setElement("#longitude",    this.message.getLongitude());
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
