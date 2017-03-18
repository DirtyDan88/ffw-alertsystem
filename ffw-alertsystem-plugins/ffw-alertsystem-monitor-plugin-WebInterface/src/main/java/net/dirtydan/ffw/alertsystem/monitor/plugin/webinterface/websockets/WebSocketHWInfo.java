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

package net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.websockets;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.dirtydan.ffw.alertsystem.common.plugin.Plugin.PluginState;
import net.dirtydan.ffw.alertsystem.monitor.plugin.WebInterface;
import net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface.jetty.HtmlTemplate;


public class WebSocketHWInfo extends WebSocketBase {
  
  private static final List<WebSocketBase> connections = new LinkedList<>();
  
  static {
    Timer t = new Timer();
    t.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        if (WebInterface.instance.state() == PluginState.STOPPED ||
            WebInterface.instance.state() == PluginState.ERROR) {
          t.cancel();
        }
        updateAll();
      }
    }, 0, 1000);
  }
  
  
  @Override
  protected String getName() {
    return "HWInfoWebSocket";
  }
  
  @Override
  protected void onConnect() {
    connections.add(this);
    sendMessage(buildHtml());
  }
  
  @Override
  protected void onClose() {
    connections.remove(this);
  }
  
  @Override
  protected void onError(Throwable t) {
    connections.remove(this);
  }
  
  
  public static void updateAll() {
    connections.forEach(socket -> socket.sendMessage(buildHtml()));
  }
  
  private static HtmlTemplate template = new HtmlTemplate(
                                           "/web/templates/template-hwInfo.html"
                                         );
  
  private static String buildHtml() {
    template.setElement("#cpus",        getCPUInfo());
    template.setElement("#memory",      getRuntimeMemory());
    template.setElement("#disk-space",  getDiskSpace());
    template.setElement("#temperature", "-");
    template.setElement("#os-name",     System.getProperty("os.name"));
    template.setElement("#os-version",  System.getProperty("os.version") + " " +
                                        System.getProperty("os.arch"));
    
//    InetAddress ip = InetAddress.getLocalHost();
//    ip.getHostAddress();
    
    return template.toString();
  }
  
  private static String getCPUInfo() {
    Runtime rt = Runtime.getRuntime();
    String cpus = rt.availableProcessors() + " cores (threads: " +
                  Thread.activeCount() + ")";
    return cpus;
  }
  
  private static String getRuntimeMemory() {
    Runtime rt = Runtime.getRuntime();
    
    long totalMem = rt.totalMemory();
    long freeMem  = rt.freeMemory();
    
    double usedMem = (((totalMem - freeMem) * 1.0) / totalMem) * 100.0;
           usedMem = Math.round(usedMem * 100) / 100.0;
           
    String memory = String.valueOf(usedMem) + "% used " +
                    "(total " + totalMem / 1024 / 1024 + "MB)";
    return memory;
  }
  
  private static String getDiskSpace() {
    File[] roots = File.listRoots();
    // only the first filesystem -> should be '/'
    for (File root : roots) {
      long totalSpace   = root.getTotalSpace();
      long useableSpace = root.getUsableSpace();
      
      double m = (((totalSpace - useableSpace) * 1.0) / totalSpace) * 100.0;
      m = Math.round(m * 100) / 100.0;
      String diskSpace = String.valueOf(m) + "% used";
      
      return diskSpace;
    }
    
    return "-";
  }
  
}
