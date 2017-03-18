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

package net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.jetty;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import net.dirtydan.ffw.alertsystem.common.util.Logger;


/**
 * Reads Html-files and uses jsoup to provide css-selectors like '#id-name'
 * and '.class-name', {@link HtmlTemplate#setElement()}.
 */
public class HtmlTemplate {
  
  private final Logger log = JettyServer.jettyLogger;
  
  private Document doc = null;
  
  
  public HtmlTemplate(String template) {
    try {
      InputStream is = HtmlTemplate.class.getResourceAsStream(template);
      doc = Jsoup.parse(is, null, "");
    } catch (IOException e) {
      log.error("could not load template-file: " + template, e);
    }
  }
  
  public void setElement(String cssSelector, String content) {
    if (content != null) {
      Element tag = doc.select(cssSelector).first();
      if (tag != null) {
        tag.html(content);
      } else {
        log.warn("no match for css selector '" + cssSelector + "'");
      }
    } else {
      log.warn("value for css selector '" + cssSelector + "' is null");
    }
  }
  
  public void addClass(String cssSelector, String cssClass) {
    doc.select(cssSelector).first().addClass(cssClass);
  }
  
  public void removeClass(String cssSelector, String cssClass) {
    doc.select(cssSelector).first().removeClass(cssClass);
  }
  
  @Override
  public String toString() {
    return (doc != null) ? doc.toString() : "";
  }
  
}
