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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ffw.alertmonitor.AlertActionManager.AlertActionDesc;
import ffw.util.logging.ApplicationLogger;
import ffw.util.logging.ApplicationLogger.Application;



public class ConfigFile {
  
  private static String xsdFileName = "config.xsd";
  private static String xmlFileName;
  
  public static boolean setFileName(String xmlFileName) {
    ConfigFile.xmlFileName = xmlFileName;
    return validate();
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
  
  
  
  public String getConfigParam() {
    
    
    Document doc = openConfig();
    if (doc == null) {
      
    }
    
    
    
    
    try { 
      
      
      System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
      
   } catch (Exception e) {
      e.printStackTrace();
   }
    
    
    
    return null;
  }
  
  
  
  public static List<AlertActionDesc> getAlertActionDescs() {
    List<AlertActionDesc> alertActionDescs = new ArrayList<AlertActionDesc>();
    Document config = openConfig();
    
    if (config != null) {
      NodeList alertActionNodes = config.getElementsByTagName("alertaction");
      for (int i = 0; i < alertActionNodes.getLength(); i++) {
        Node alertActionNode = alertActionNodes.item(i);
        
        if (alertActionNode.getNodeType() == Node.ELEMENT_NODE) {
          Element elem = (Element) alertActionNode;
          
          String packageName  = elem.getElementsByTagName("packageName").
                                     item(0).getTextContent();
          String className    = elem.getElementsByTagName("className").
                                     item(0).getTextContent();
          String instanceName = elem.getAttribute("instanceName");
          
          alertActionDescs.add(new AlertActionDesc(packageName,
                                                   className,
                                                   instanceName));
        }
      }
    }
    
    return alertActionDescs;
  }
  
  
  
  public static String getAlertActionParam(String alertActionName, 
                                           String paramName) {
    Document config = openConfig();
    
    if (config != null) {
      
      //NodeList actionNodes = config.getElementsByTagName("alertactions");
      //actionNodes.item(0).getAttributes()
      
      Element elem = config.getElementById("instanceName");
      NodeList paramNodes = elem.getElementsByTagName("param");
      
      
      for (int i = 0; i < paramNodes.getLength(); i++) {
        String attrName = paramNodes.item(i).getAttributes().item(0).getNodeValue();
        
        if (attrName.equals(paramName)) {
          return paramNodes.item(i).getNodeValue();
        }
      }
      
    }
    
    ApplicationLogger.log("## ERROR: Parameter " + paramName + " for " +
                          "alert-action " + alertActionName + " does not exist.",
                          Application.ALERTMONITOR, true);
    
    return null;
  }
  
  
  
  public Map<String, String> getAllAlertActionParams(String alertActionName) {
    Map<String, String> map = new HashMap<String, String>();
    
    Document doc = openConfig();
    if (doc == null) {
      
    }
    
    
    NodeList alertActions = doc.getElementsByTagName("alertaction");
    
    for (int i = 0; i < alertActions.getLength(); i++) {
      Node alertAction = alertActions.item(i);
      System.out.println("\nCurrent Element :" + alertAction.getNodeName());
      
      //if (alertAction.getNodeType() == Node.ELEMENT_NODE) {
    }
    
    /*
    for (int temp = 0; temp < nList.getLength(); temp++) {
       Node nNode = nList.item(temp);
       System.out.println("\nCurrent Element :" 
          + nNode.getNodeName());
       
       if (nNode.getNodeType() == Node.ELEMENT_NODE) {
          Element eElement = (Element) nNode;
          System.out.println("Student roll no : " 
             + eElement.getAttribute("rollno"));
          System.out.println("First Name : " 
             + eElement
             .getElementsByTagName("firstname")
             .item(0)
             .getTextContent());
          System.out.println("Last Name : " 
          + eElement
             .getElementsByTagName("lastname")
             .item(0)
             .getTextContent());
          System.out.println("Nick Name : " 
          + eElement
             .getElementsByTagName("nickname")
             .item(0)
             .getTextContent());
          System.out.println("Marks : " 
          + eElement
             .getElementsByTagName("marks")
             .item(0)
             .getTextContent());
       }
       
    }*/
    
    
    return map;
  }
  
  
  
  private static Document openConfig() {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      dbFactory.setSchema(loadSchema());
      
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document config = dBuilder.parse(new File(xmlFileName));
      config.getDocumentElement().normalize();
      
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
}
