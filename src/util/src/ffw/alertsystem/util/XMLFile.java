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

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;



/**
 * Provides access to content of XML-files; also is able to validate XML-files
 * against a XSD-schema-file.
 */
public class XMLFile {
  
  private final String xsdFileName;
  
  private final String xmlFileName;
  
  private final Logger log;
  
  
  
  public XMLFile(String xsdFileName, String xmlFileName, Logger log) {
    this.xsdFileName = xsdFileName;
    this.xmlFileName = xmlFileName;
    this.log = log;
  }
  
  
  
  public Document open() {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      dbFactory.setSchema(loadSchema());
      
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document xmlFile = dBuilder.parse(new File(xmlFileName));
      xmlFile.getDocumentElement().normalize();
      
      return xmlFile;
      
    } catch (Exception e) {
      log.error("could not open xml-file: " + xmlFileName, e);
    }
    
    return null;
  }
  
  public void write(Document xmlFile) {
    try {
      TransformerFactory.newInstance().
        newTransformer().transform(
          new DOMSource(xmlFile),
          new StreamResult(new File(xmlFileName))
        );
      log.debug("wrote xml-file: " + xmlFileName);
      
    } catch (TransformerFactoryConfigurationError |
             TransformerException e) {
      log.error("could not write xml-file: " + xmlFileName, e);
    }
  }
  
  public boolean isValid() {
    try {
      Schema schema = loadSchema();
      schema.newValidator().validate(
        new StreamSource(xmlFileName)
      );
      
      return true;
      
    } catch (Exception e) {
      log.error("xml-file not valid: " + xmlFileName, e);
      return false;
    }
  }
  
  public long getLastModifiedTime() {
    return new File(xmlFileName).lastModified();
  }
  
  private Schema loadSchema() {
    try {
      SchemaFactory factory = SchemaFactory.newInstance(
        XMLConstants.W3C_XML_SCHEMA_NS_URI
      );
      Schema schema = factory.newSchema(
        new StreamSource(xsdFileName)
      );
      
      return schema;
      
    } catch (Exception e) {
      log.error("could not open schema-file: " + xsdFileName, e);
    }
    
    return null;
  }
  
}
