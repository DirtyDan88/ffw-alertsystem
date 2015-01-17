package ffw.alertsystem;

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

import ffw.alertsystem.message.Message;

public class HtmlBuilder {
    
    private String html = "";
    private Document doc;
    
    public void build(Message msg) {
        this.loadTemplate("html/template.html");
        
        String timestamp = String.valueOf(new java.util.Date().getTime() / 1000);
        this.setElement("#timestamp", timestamp);
        
        //this.setElement("#longitude1", message.get(0));
        //this.setElement("#longitude2", message.get(1));
        //this.setElement("#tag1", message.get(3));
        //template.setElement("#tag1", message.get(4));
        //template.setElement("#tag1", message.get(5));
        //template.setElement("#tag1", message.get(6));
    }
    
    public String writeTemplate(String path) {
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
        Element tag = this.doc.select(cssSelector).first();
        tag.html(content);
    }
    
}
