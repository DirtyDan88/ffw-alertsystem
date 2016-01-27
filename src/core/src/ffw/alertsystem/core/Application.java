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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;



/**
 * Abstract base-class for all runnable ffw-alertsystem-applications. Provides
 * basic funcionalities, like logging, adding a shutdownhook, processing and
 * validating the command-line arguments and creates the application's config-
 * file. Also, the @ApplicationErrorHandler is able to handle uncaught
 * exceptions and will report it via eMail.
 */
public abstract class Application {
  
  /**
   * Determines the type of an application.
   * This is mainly used for two things: <br>
   * - The log-file which is written contains the application-name in his
   *   file-name <br>
   * - The xsd-schema-file for the application config-file is determined 
   *   by the application type
   */
  public enum ApplicationType {
    ALERTRECEIVER,
    ALERTMONITOR,
    WATCHDOG;
    
    @Override
    public String toString() {
      switch(this) {
        case ALERTRECEIVER: return "alertreceiver";
        case ALERTMONITOR:  return "alertmonitor";
        case WATCHDOG:      return "watchdog";
        default: throw new IllegalArgumentException();
      }
    }
    
    public String configSchemaFile() {
      switch(this) {
        case ALERTRECEIVER: return "src/core/schema-receiver-config.xsd";
        case ALERTMONITOR:  return "src/core/schema-monitor-config.xsd";
        case WATCHDOG:      return "src/watchdog/schema-watchdog-config.xsd";
        default: throw new IllegalArgumentException();
      }
    }
    
    public String configRootNode() {
      switch(this) {
        case ALERTRECEIVER: return "ffw-alertreceiver-config";
        case ALERTMONITOR:  return "ffw-alertmonitor-config";
        case WATCHDOG:      return "ffw-watchdog-config";
        default: throw new IllegalArgumentException();
      }
    }
    
    public String error() {
      switch(this) {
        case ALERTRECEIVER: return "ReceiverError";
        case ALERTMONITOR:  return "MonitorError";
        case WATCHDOG:      return "WatchdogError";
        default: throw new IllegalArgumentException();
      }
    }
    
  }
  
  
  
  public final ApplicationType         appType;
  
  public final ApplicationLogger       log;
  
  public final ApplicationConfig       config;
  
  public final ApplicationErrorHandler errHandler;
  
  private CommandLineParams params;
  
  private Thread loggerThread;
  
  public final static String execDir = Paths.get("").toAbsolutePath().toString();
  
  
  
  /**
   * Initiates the application.
   * @param appType The type of the ffw-application.
   * @param args    String array with command line arguments.
   */
  public Application(ApplicationType appType, String[] args) {
    addShutdownHook();
    readParams(args);
    
    this.appType    = appType;
    this.log        = new ApplicationLogger(params.logLevel, appType,
                                            params.logInFile);
    this.config     = new ApplicationConfig(appType, params.configFile, log);
    this.errHandler = new ApplicationErrorHandler(this);
  }
  
  
  
  protected final void start() {
    loggerThread = new Thread(log);
    loggerThread.start();
    onApplicationStarted();
    idle();
  }
  
  protected final void stop() {
    onApplicationStopped();
    
    if (log != null) {
      log.stop();
      
      try {
        loggerThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    
    //System.out.println("system shutdown");
  }
  
  
  
  protected void onApplicationStarted() {}
  
  protected void onApplicationStopped() {}
  
  protected void onApplicationErrorOccured(Throwable t) {}
  
  
  
  private final void readParams(String[] args) {
    params = new CommandLineParams();
    JCommander jCmd = new JCommander(params);
    
    try {
      jCmd.parse(args);
      
      if (params.logLevel < 1 || params.logLevel > 5) {
        throw new ParameterException("logLevel not in interval 1-5");
      }
    } catch (ParameterException e) {
      jCmd.usage();
      System.exit(1);
    }
  }
  
  private final void addShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        //System.out.println("received SIGTERM");
        Application.this.stop();
      }
    });
  }
  
  private final void idle() {
    BufferedReader console = new BufferedReader(
      new InputStreamReader(System.in)
    );
    
    try {
      boolean quit = false;
            
      while (!quit) {
        int c = console.read();
      
        if (c == 'q') {
          quit = true;
        }
        
        Thread.sleep(100);
      }
      
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
    
    System.exit(0);
  }
  
}
