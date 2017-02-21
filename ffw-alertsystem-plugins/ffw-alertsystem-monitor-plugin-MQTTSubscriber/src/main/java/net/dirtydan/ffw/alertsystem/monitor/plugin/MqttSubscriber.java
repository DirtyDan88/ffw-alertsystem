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

package net.dirtydan.ffw.alertsystem.monitor.plugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import net.dirtydan.ffw.alertsystem.common.plugin.PluginDependency;
import net.dirtydan.ffw.alertsystem.common.util.SSLContextCreator;
import net.dirtydan.ffw.alertsystem.monitor.MessageConsumer;



public class MqttSubscriber extends MonitorPlugin {
  
  @PluginDependency
  private MessageConsumer monitor;
  
  private MqttClient client;
  
  
  
  @Override
  protected void onMonitorPluginStart() {
    openMqttConnection();
  }
  
  @Override
  protected void onMonitorPluginReload() {
    closeMqttConnection();
    openMqttConnection();
  }
  
  @Override
  protected void onMonitorPluginStop() {
    closeMqttConnection();
  }
  
  @Override
  protected void onPluginError(Throwable t) {
    log.info("restart plugin in 3s", true);
    
    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    exec.scheduleWithFixedDelay(new Runnable() {
      @Override
      public void run() {
        restart();
        exec.shutdown();
      }
    }, 3, 1, TimeUnit.SECONDS);
  }
  
  
  
  private void openMqttConnection() {
    String topic     = config().paramList().get("topic").val();
    String brokerURI = config().paramList().get("broker-uri").val();
    String clientId  = config().paramList().get("client-id").val();
    
    try {
      client = new MqttClient(brokerURI, clientId);
      client.setCallback(new MessageCallback());
      
      MqttConnectOptions options = new MqttConnectOptions();
      options.setSocketFactory(
                SSLContextCreator.create(
                  config().paramList().get("ca-certificate").val(),
                  config().paramList().get("client-certificate").val(),
                  config().paramList().get("client-keyfile").val(), ""
                ).getSocketFactory()
              );
      client.connect(options);
      client.subscribe(topic);
      
      log.info("connected to broker " + brokerURI + " on topic " + topic);
      
    } catch (MqttException e) {
      log.error("could not connect to broker " + brokerURI, e, true);
      errorOccured(e);
    }
  }
  
  private void closeMqttConnection() {
    String topic     = config().paramList().get("topic").val();
    String brokerURI = config().paramList().get("broker-uri").val();
    
    try {
      client.disconnect();
      client.close();
      
      log.info("disconnected from broker " + brokerURI + " and topic " + topic);
      
    } catch (MqttException e) {
      log.error("could not close mqtt-connection", e, true);
    }
  }
  
  
  
  private class MessageCallback implements MqttCallback {
    
    @Override
    public void messageArrived(String topic, MqttMessage message) {
      log.info("received message on topic " + topic, true);
      monitor.insertMessage(message.toString());
    }
    
    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {}
    
    @Override
    public void connectionLost(Throwable t) {
      errorOccured(t);
    }
    
  }
  
}
