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

import java.io.InputStream;
import java.io.InputStreamReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;



/**
 * Reads Html-files and uses jsoup to provide css-selectors like '#id-name'
 * and '.class-name', {@link HtmlTemplate#setElement()}.
 */
public class HtmlTemplate {
  
  private Document doc = null;
  
  private Logger log;
  
  
  
  public HtmlTemplate(Logger log, InputStream is) {
    this.log = log;
    
    String html = FileReader.getContent(new InputStreamReader(is));
    
    if (html != null) {
      doc = Jsoup.parse(html);
    } else {
      log.error("could not load html-template-file", new Exception());
    }
  }
  
  @Override
  public String toString() {
    if (doc == null) {
      return "";
    }
    
    return doc.toString();
  }
  
  public void setElement(String cssSelector, String content) {
    if (content != null) {
      Element tag = doc.select(cssSelector).first();
      if (tag != null) {
        tag.html(content);
      } else {
        log.error("no match for css selector '" + cssSelector + "'",
                  new Exception());
      }
    } else {
      log.warn("value for css selector '" + cssSelector + "' is null");
    }
  }
  
}
