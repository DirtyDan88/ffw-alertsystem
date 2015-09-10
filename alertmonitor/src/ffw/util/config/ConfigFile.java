package ffw.util.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ffw.alertmonitor.AlertActionManager.AlertActionDesc;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class ConfigFile {
  
  private static String xsdFileName = "config.xsd";
  private static String xmlFileName = null;
  
  private static long lastReadTime = -1;
  
  
  
  public static boolean setFileName(String xmlFileName) {
    ConfigFile.xmlFileName = xmlFileName;
    lastReadTime = -1;
    
    if (validate()) {
      return true;
    }
    
    ConfigFile.xmlFileName = null;
    return false;
  }
  
  public static boolean validate() {
    try {
      Schema schema = loadSchema();
      schema.newValidator().validate(
        new StreamSource(xmlFileName)
      );
      
      return true;
      
    } catch (Exception e) {
      return false;
    }
  }
  
  public static boolean hasChanged() {
    if (lastReadTime == getLastModifiedTime()) {
      return false;
    }
    
    return true;
  }
  
  
  
  public static String getConfigParam(String paramName) {
    Document config = openConfig();
    
    if (config != null) {
      NodeList nodes = config.getElementsByTagName(paramName);
      
      if (nodes.getLength() == 0) {
        ApplicationLogger.log("## ERROR: parameter " + paramName + 
                              " does not exist.",
                              Application.ALERTMONITOR, true);
      } else {
        return nodes.item(0).getTextContent();
      }
    }
    
    return null;
  }
  
  public static List<AlertActionDesc> getAlertActionDescs() {
    List<AlertActionDesc> alertActionDescs = new ArrayList<AlertActionDesc>();
    Document config = openConfig();
    
    if (config != null) {
      NodeList actionNodes = config.getElementsByTagName("alertaction");
      for (int i = 0; i < actionNodes.getLength(); i++) {
        Element elem = (Element) actionNodes.item(i);
        
        String packageName  = elem.getElementsByTagName("packageName").
                                   item(0).getTextContent();
        String className    = elem.getElementsByTagName("className").
                                   item(0).getTextContent();
        String instanceName = elem.getAttribute("instanceName");
        String isActive     = elem.getElementsByTagName("active").
                                   item(0).getTextContent();
        String description  = elem.getElementsByTagName("description").
                                   item(0).getTextContent();
        
        alertActionDescs.add(
          new AlertActionDesc(
            packageName,
            className,
            instanceName,
            isActive,
            description
          )
        );
      }
    }
    
    return alertActionDescs;
  }
  
  public static List<String> getAlertActionRics(String alertActionName) {
    ArrayList<String> ricList = new ArrayList<String>();
    Document config = openConfig();
    
    if (config != null) {
      NodeList actionNodes = config.getElementsByTagName("alertaction");
      for (int i = 0; i < actionNodes.getLength(); i++) {
        Element action = (Element) actionNodes.item(i);
        
        if (action.getAttribute("instanceName").equals(alertActionName)) {
          NodeList ricNodes = action.getElementsByTagName("ric"); 
          for (int j = 0; j < ricNodes.getLength(); j++) {
            Element ric = (Element) ricNodes.item(j);
            ricList.add(ric.getTextContent());
          }
          
          return ricList;
        }
      }
      
      ApplicationLogger.log("## ERROR: alert-action " + alertActionName + 
                            " does not exist.",
                            Application.ALERTMONITOR, true);
    }
    
    return ricList;
  }
  
  public static Map<String, String> getAlertActionParams(String alertActionName) {
    Map<String, String> paramList = new HashMap<String, String>();
    Document config = openConfig();
    
    if (config != null) {
      NodeList actionNodes = config.getElementsByTagName("alertaction");
      for (int i = 0; i < actionNodes.getLength(); i++) {
        Element action = (Element) actionNodes.item(i);
        
        if (action.getAttribute("instanceName").equals(alertActionName)) {
          NodeList paramNodes = action.getElementsByTagName("param"); 
          for (int j = 0; j < paramNodes.getLength(); j++) {
            Element param = (Element) paramNodes.item(j);
            paramList.put(param.getAttribute("name"), param.getTextContent());
          }
          
          return paramList;
        }
      }
      
      ApplicationLogger.log("## ERROR: alert-action " + alertActionName + 
                            " does not exist.",
                            Application.ALERTMONITOR, true);
    }
    
    return paramList;
  }
  
  /*
  public static String getAlertActionParam(String alertActionName, 
                                           String paramName) {
    Document config = openConfig();
    
    if (config != null) {
      NodeList actionNodes = config.getElementsByTagName("alertaction");
      for (int i = 0; i < actionNodes.getLength(); i++) {
        Element action = (Element) actionNodes.item(i);
        
        if (action.getAttribute("instanceName").equals(alertActionName)) {
          NodeList paramNodes = action.getElementsByTagName("param"); 
          for (int j = 0; j < paramNodes.getLength(); j++) {
            Element param = (Element) paramNodes.item(i);
            
            if (param.getAttribute("name").equals(paramName)) {
              return param.getTextContent();
            }
          }
          
          ApplicationLogger.log("## ERROR: Parameter " + paramName + " for " +
                                "alert-action " + alertActionName + " does not exist.",
                                Application.ALERTMONITOR, true);
          return null;
        }
      }
      
      ApplicationLogger.log("## ERROR: alert-action " + alertActionName + 
                            " does not exist.",
                            Application.ALERTMONITOR, true);
    }
    return null;
  }
  */
  
  
  
  private static Document openConfig() {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      dbFactory.setSchema(loadSchema());
      
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document config = dBuilder.parse(new File(xmlFileName));
      config.getDocumentElement().normalize();
      
      lastReadTime = getLastModifiedTime();
      return config;
      
    } catch (Exception e) {
      ApplicationLogger.log("## ERROR: Was not able to open config-file:\n" + 
                            e.getMessage(), Application.ALERTMONITOR, true);
    }
    
    return null;
  }
  
  private static Schema loadSchema() {
    try {
      SchemaFactory factory = SchemaFactory.newInstance(
        XMLConstants.W3C_XML_SCHEMA_NS_URI
      );
      Schema schema = factory.newSchema(
        new StreamSource(xsdFileName)
      );
      
      return schema;
      
    } catch (Exception e) {
      ApplicationLogger.log("## ERROR: Was not able to open schema-file:\n" +
                            e.getMessage(), Application.ALERTMONITOR, true);
    }
    
    return null;
  }
  
  private static long getLastModifiedTime() {
    return new File(xmlFileName).lastModified();
  }
}
