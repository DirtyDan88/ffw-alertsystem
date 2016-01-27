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

package ffw.alertsystem.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ffw.alertsystem.core.Application.ApplicationType;
import ffw.alertsystem.util.DateAndTime;
import ffw.alertsystem.util.Logger;



/**
 * Implementation of the abstract base-class Logger. Works asynchronously, which
 * means that all log-messages were first stored in a queue, the logger then
 * takes the messages and writes them either to the standard-output or to an
 * file. The reason for this is to avoid a delay for the caller of the logger
 * which would occur if the log-file has to be opened for every log()-call.<br>
 *
 * @see @Logger
 */
public class ApplicationLogger extends Logger
                               implements Runnable {
  
  private boolean stopped = false;
  private final boolean inFile;
  private final ApplicationType app;
  private final Queue<Pair<String, Boolean>> messageBuffer;
  
  
  
  public ApplicationLogger(int logLevel, ApplicationType app, boolean inFile) {
    super(logLevel);
    
    this.app    = app;
    this.inFile = inFile;
    
    messageBuffer = new ConcurrentLinkedQueue<>();
    
    // print the logger settings
    log("================================================================", true);
    log("Logger setting: logLevel=" + logLevel +
                       " application="+ app +
                       " inFile=" + inFile, true);
  }
  
  
  
  @Override
  public synchronized void log(String message, boolean printWithTime) {
    if (getLogLevel() == DEBUG) {
      message = message + " [" + Thread.currentThread().getName() + "]";
    }
    
    messageBuffer.add(new Pair<String, Boolean>(message, printWithTime));
  }
    
  @Override
  public final void run() {
    Pair<String, Boolean> message;
    
    while (!stopped || messageBuffer.size() > 0) {
      PrintStream s = getPrintStream();
      
      while ((message = messageBuffer.poll()) != null) {
        String dateAndTime = "";
        if (message.printWithTime()) {
            dateAndTime = "[" + DateAndTime.get() + "] ";
        } else {
            dateAndTime = "                        ";
        }
        
        s.println(dateAndTime + message.getText());
      }
      
      if (inFile) {
        s.close();
      } else {
        s.flush();
      }
      
      try {
        // Writing the logs into a file it is also sufficient to do this only
        // every 2 seconds -> reduces file operations.
        int sleepTime;
        if (inFile) {
          sleepTime = 2000;
        } else {
          sleepTime = 100;
        }
        Thread.sleep(sleepTime);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  public final synchronized void stop() {
    stopped = true;
  }
  
  
  
  private final PrintStream getPrintStream() {
    if (inFile) {
      File logFileDir = new File("data/logs/" + DateAndTime.getYearAndMonthName());
      logFileDir.mkdirs();
      
      String fileName = "log-" + DateAndTime.getDate() + "-" + app + ".txt";
      File logFile = new File(logFileDir, fileName);
      
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(logFile, true);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      
      return new PrintStream(fos);
      
    } else {
      return System.out;
    }
  }
  
  private class Pair<L, R> {
    private final L left;
    private final R right;
    
    public Pair(L left, R right) {
      this.left = left;
      this.right = right;
    }
    
    public L getText() { return left; }
    public R printWithTime() { return right; }
    
  }
  
}