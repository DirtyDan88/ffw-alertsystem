/*
    Copyright (c) 2015, Max Stark <max.stark88@web.de> 
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

package ffw.alertmonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ffw.util.ApplicationLogger;


public class Application {
    private static Queue<Message> messageQueue;
    private static AlertMonitor   alertMonitor;
    private static AlertListener  alertListener;
    private static Thread         alertMonitorThread;
    private static Thread         alertListenerThread;
    
    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("-logInFile")) {
                ApplicationLogger.inFile = true;
            }
        }
        
        startApplication();
        
        /* application is either terminated via signal or user */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                ApplicationLogger.log("received SIGTERM", 
                        ApplicationLogger.Application.ALERTMONITOR);
                stopApplication();
            }
        });
        
        BufferedReader console = new BufferedReader(
                                 new InputStreamReader(System.in));
        try {
            boolean quit = false;
            while (!quit) {
                if (console.read() == 'q') quit = true;
                Thread.sleep(100);
            }
            ApplicationLogger.log("stopped by user", 
                    ApplicationLogger.Application.ALERTMONITOR);
            stopApplication();
            
        } catch (IOException | InterruptedException e) {
            ApplicationLogger.log("ERROR: " + e.getMessage(), 
                    ApplicationLogger.Application.ALERTMONITOR);
        }
    }
    
    private static void startApplication() {
        ApplicationLogger.log("ffw-alertsystem started", 
                ApplicationLogger.Application.ALERTMONITOR);
        
        messageQueue  = new ConcurrentLinkedQueue<Message>();
        alertMonitor  = new AlertMonitor(messageQueue);
        alertListener = new AlertListener(messageQueue);
        
        alertMonitorThread  = new Thread(alertMonitor);
        alertListenerThread = new Thread(alertListener);
        alertMonitorThread.start();
        alertListenerThread.start();
    }
    
    private static void stopApplication() {
        alertListener.stop();
        alertMonitor.stop();
        
        try {
            alertMonitorThread.join();
            alertListenerThread.join();
        } catch (InterruptedException e) {
            ApplicationLogger.log("## ERROR: " + e.getMessage(), 
                    ApplicationLogger.Application.ALERTMONITOR);
        }
        
        ApplicationLogger.log("ffw-alertsystem stopped", 
                ApplicationLogger.Application.ALERTMONITOR);
    }
}
