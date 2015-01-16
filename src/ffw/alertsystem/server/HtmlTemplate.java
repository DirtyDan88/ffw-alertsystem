package ffw.alertsystem.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HtmlTemplate {
    
    private String html = "";
    private Document doc;
    
    
    public void loadTemplate(String templateFile) {
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
    
    public void writeTemplate(String fileName) {
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
    }
    
    public void setElement(String cssSelector, String content) {
        Element tag = this.doc.select(cssSelector).first();
        tag.html(content);
    }
    
}
