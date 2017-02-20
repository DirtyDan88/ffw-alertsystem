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

package net.dirtydan.ffw.alertsystem.common.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import net.dirtydan.ffw.alertsystem.common.util.Logger;



/**
 * Abstract base-class for all runnable ffw-alertsystem-applications. Provides
 * basic funcionalities, like logging, adding a shutdownhook, processing and
 * validating the command-line arguments and creates the application's config-
 * file. Also, the @ApplicationErrorHandler is able to handle uncaught
 * exceptions and will report it via eMail.
 */
public abstract class Application {
  
  public final String applicationName;
  
  public final ApplicationConfig config;
  
  public final ApplicationErrorHandler errHandler;
  
  private final ApplicationLogger log;
  
  private final Thread loggerThread;
  
  private CommandLineParams params;
  
  
  
  /**
   * Initiates the application.
   * @param applicationName  The name of the ffw-application.
   * @param configSchemaFile The xsd-schema-file for the config-file of the
   *                         ffw-application.
   * @param args             String array with command line arguments.
   */
  public Application(String applicationName, String configSchemaFile,
                     String[] args) {
    this.applicationName = applicationName;
    
    addShutdownHook();
    readParams(args);
    
    log = new ApplicationLogger(applicationName, params.logLevel, params.logInFile);
    Logger.setApplicationLogger(log);
    loggerThread = new Thread(log);
    loggerThread.start();
    
    config     = new ApplicationConfig(configSchemaFile, params.configFile);
    errHandler = new ApplicationErrorHandler(this);
  }
  
  
  
  protected final void start() {
    if (onApplicationStarted()) {
      idle();
    } else {
      stop();
    }
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
  }
  
  
  
  protected boolean onApplicationStarted() { return true; }
  
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
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                                           Application.this.stop();
                                         }));
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
    
    // calls the shutdown-hook
    System.exit(0);
  }
  
}
