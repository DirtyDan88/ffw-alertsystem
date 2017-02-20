/*
  Copyright (c) 2015-2017, Max Stark <max.stark88@web.de>
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

package net.dirtydan.ffw.alertsystem.common.util;

import java.io.File;
import java.net.URL;

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
 * Provides access to the content of XML-files; also is able to validate
 * XML-files against a XSD-schema-file.
 */
public class XMLFile {
  
  private final Logger log = Logger.getApplicationLogger();
  
  private final String xsdFileName;
  
  private final String xmlFileName;
  
  
  
  public XMLFile(String xsdFileName, String xmlFileName) {
    this.xsdFileName = xsdFileName;
    this.xmlFileName = xmlFileName;
  }
  
  public long getLastModifiedTime() {
    return new File(xmlFileName).lastModified();
  }
  
  public Document open() {
    try {
      Schema xsdSchema = loadSchema();
      if (xsdSchema == null || !isValid(xsdSchema)) return null;
      
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      dbFactory.setSchema(xsdSchema);
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      
      Document xmlFile = dBuilder.parse(new File(xmlFileName));
      xmlFile.getDocumentElement().normalize();
      
      return xmlFile;
      
    } catch (Exception e) {
      log.error("could not open xml-file: " + xmlFileName, e);
      return null;
    }
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
      log.error("could not write xml-file: " + xmlFileName, e, true);
    }
  }
  
  
  
  private Schema loadSchema() {
    try {
      SchemaFactory schemaFactory = SchemaFactory.newInstance(
                                      XMLConstants.W3C_XML_SCHEMA_NS_URI
                                    );
      URL schemaURL = getClass().getResource("/" + xsdFileName);
      return schemaFactory.newSchema(schemaURL);
      
    } catch (Exception e) {
      log.error("could not process schema-file: " + xsdFileName, e, true);
      return null;
    }
  }
  
  private boolean isValid(Schema schema) {
    try {
      schema.newValidator().validate(new StreamSource(xmlFileName));
      return true;
      
    } catch (Exception e) {
      log.error("xml-file not valid: " + xmlFileName, e, true);
      return false;
    }
  }
  
}
