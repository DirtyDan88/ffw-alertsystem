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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.jetty.util.log.Logger;

import net.dirtydan.ffw.alertsystem.common.application.ApplicationLogger;


public class JettyLogger extends net.dirtydan.ffw.alertsystem.common.util.Logger
                         implements org.eclipse.jetty.util.log.Logger {
  
  private final ApplicationLogger _log;
  
  private final Thread _loggerThread;
  
  private final boolean _inFile;
  
  
  public JettyLogger(String appName, int logLevel, boolean inFile) {
    super(logLevel);
    
    _inFile = inFile;
    _log = new ApplicationLogger("jetty-" + appName, logLevel, inFile);
    _loggerThread = new Thread(_log);
    _loggerThread.start();
  }
  
  public void stop() {
    if (_log != null) {
      _log.stop();
      try {
        _loggerThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  
  @Override
  public void log(String message, boolean printWithTime) {
    if (!_inFile) message = "jetty >> " + message;
    _log.log(message, printWithTime);
  }
  
  
  @Override
  public void debug(Throwable t) {
    StringWriter stackTrace = new StringWriter();
    t.printStackTrace(new PrintWriter(stackTrace));
    
    debug(stackTrace.toString(), true);
  }
  
  @Override
  public void debug(String arg0, Object... arg1) {
    StringBuilder txt = new StringBuilder(arg0);
    for (Object o : arg1) {
      txt.append(o.toString());
    }
    
    debug(txt.toString(), true);
  }
  
  @Override
  public void debug(String arg0, long arg1) {
    debug(arg0 + arg1, true);
  }
  
  @Override
  public void debug(String arg0, Throwable t) {
    StringWriter stackTrace = new StringWriter();
    t.printStackTrace(new PrintWriter(stackTrace));
    
    debug(arg0 + stackTrace.toString(), true);
  }
  
  
  @Override
  public Logger getLogger(String arg0) {
    return this;
  }
  
  @Override
  public String getName() {
    return "ffw-jetty-logger";
  }
  
  @Override
  public void ignore(Throwable arg0) {}
  
  
  @Override
  public void info(Throwable t) {
    StringWriter stackTrace = new StringWriter();
    t.printStackTrace(new PrintWriter(stackTrace));
    
    info(stackTrace.toString(), true);
  }
  
  @Override
  public void info(String arg0, Object... arg1) {
    StringBuilder txt = new StringBuilder(arg0);
    for (Object o : arg1) {
      txt.append(o.toString());
    }
    
    info(txt.toString(), true);
  }
  
  @Override
  public void info(String arg0, Throwable t) {
    StringWriter stackTrace = new StringWriter();
    t.printStackTrace(new PrintWriter(stackTrace));
    
    info(arg0 + stackTrace.toString(), true);
  }
  
  
  @Override
  public boolean isDebugEnabled() { return false; }
  
  @Override
  public void setDebugEnabled(boolean arg0) {}
  
  
  @Override
  public void warn(Throwable t) {
    StringWriter stackTrace = new StringWriter();
    t.printStackTrace(new PrintWriter(stackTrace));
    
    warn(stackTrace.toString(), true);
  }
  
  @Override
  public void warn(String arg0, Object... arg1) {
    StringBuilder txt = new StringBuilder(arg0);
    for (Object o : arg1) {
      txt.append(o.toString());
    }
    
    warn(txt.toString(), true);
  }
  
  @Override
  public void warn(String arg0, Throwable t) {
    StringWriter stackTrace = new StringWriter();
    t.printStackTrace(new PrintWriter(stackTrace));
    
    warn(arg0 + stackTrace.toString(), true);
  }
  
}
