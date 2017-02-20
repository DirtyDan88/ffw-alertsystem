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

import java.io.PrintWriter;
import java.io.StringWriter;



/**
 * Abstract logger class which defines log-levels.
 */
public abstract class Logger {
  
  /**
   * LogLevel OFF logs nothing.
   */
  public static final int OFF   = 1;
  
  /**
   * LogLevel ERROR logs only errors.
   */
  public static final int ERROR = 2;
  
  /**
   * LogLevel WARN logs errors and warnings.
   */
  public static final int WARN  = 3;
  
  /**
   * LogLevel INFO logs info, errors and warnings.
   */
  public static final int INFO  = 4;
  
  /**
   * LogLevel DEBUG logs every message.
   */
  public static final int DEBUG = 5;
  
  /**
   * @return A string which describes a log-level.
   */
  public static String getLogLevelName(int logLevel) {
    switch (logLevel) {
      case OFF:   return "OFF";
      case ERROR: return "ERROR";
      case WARN:  return "WARN";
      case INFO:  return "INFO";
      case DEBUG: return "DEBUG";
      default:    return "UNKNOWN";
    }
  }
  
  
  
  /**
   * The choosen log-level of the logger, see granularity of log-levels above.
   */
  public int logLevel;
  
  /**
   * Only the constructor is able to set the log-level.
   * @param logLevel The desired log-level of the logger.
   */
  public Logger(int _logLevel) {
    logLevel = _logLevel;
  }
  
  
  
  /**
   * The only method a derived logger-class has to implement is the log-method,
   * where it is decided what actually to do with the log-message (write to
   * file, to standard output or whatever). <br>
   * It is called from every log-method below.
   * @param text          The String with the log-message.
   * @param printWithTime True if the printage of the time of this message is
   *                      desired.
   */
  public abstract void log(String text, boolean printWithTime);
  
  
  
  public void error(String text, Throwable e, boolean printWithTime) {
    if (logLevel >= ERROR) {
      StringWriter stackTrace = new StringWriter();
      e.printStackTrace(new PrintWriter(stackTrace));
      String trace = stackTrace.toString();
      
      log(                      "[ERRO] " + text + "\n" +
        "                               Message:    " + e.getMessage() + "\n" +
        "                               Stacktrace: " + trace + "\n",
        printWithTime
      );
    }
  }
  
  public void error(String text, Throwable e) {
    error(text, e, false);
  }
  
  public void warn(String text, boolean printWithTime) {
    if (logLevel >= WARN) {
      log("[WARN] " + text, printWithTime);
    }
  }
  
  public void warn(String text) {
    warn(text, false);
  }
  
  public void info(String text, boolean printWithTime) {
    if (logLevel >= INFO) {
      log("[INFO] " + text, printWithTime);
    }
  }
  
  public void info(String text) {
    info(text, false);
  }
  
  public void debug(String text, boolean printWithTime) {
    if (logLevel >= DEBUG) {
      log("[DEBG] " + text, printWithTime);
    }
  }
  
  public void debug(String text) {
    debug(text, false);
  }
  
  
  
  /**
   * The global @Logger object.
   */
  private static Logger applicationLogger;
  
  /**
   * Sets a global @Logger object which can be application-wide.
   * @param log The global @Logger object.
   */
  public static void setApplicationLogger(Logger log) {
    applicationLogger = log;
  }
  
  /**
   * @return The global @Logger object. If the global @Logger is not set, the
   * function returns a dummy @Logger implementation which does not log
   * anything.
   */
  public static Logger getApplicationLogger() {
    if (applicationLogger == null) {
      // mock a Logger-object if no Logger is available
      return new Logger(1) {
               @Override
               public void log(String message, boolean printWithTime) {}
             };
    }
    
    return applicationLogger;
  }
  
}
