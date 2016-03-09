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

package ffw.alertsystem.plugins.monitor;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import ffw.alertsystem.core.monitor.MonitorPlugin;
import ffw.alertsystem.util.SSLContextCreator;



public class MqttSubscriber extends MonitorPlugin {
  
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
  
  
  
  private void openMqttConnection() {
    String topic     = config().paramList().get("topic");
    String brokerURI = config().paramList().get("broker-uri");
    String clientId  = config().paramList().get("client-id");
    
    try {
      client = new MqttClient(brokerURI, clientId);
      client.setCallback(new MessageCallback());
      
      MqttConnectOptions options = new MqttConnectOptions();
      options.setSocketFactory(
                SSLContextCreator.create(
                  config().paramList().get("ca-certificate"),
                  config().paramList().get("client-certificate"),
                  config().paramList().get("client-keyfile"), ""
                ).getSocketFactory()
              );
      client.connect(options);
      client.subscribe(topic);
      
      log.info("connected to broker " + brokerURI + " on topic " + topic);
      
    } catch (MqttException e) {
      log.error("could not connect to broker " + brokerURI, e, true);
    }
  }
  
  private void closeMqttConnection() {
    String topic     = config().paramList().get("topic");
    String brokerURI = config().paramList().get("broker-uri");
    
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
      //log.error("connection lost", t, true);
      errorOccured(t);
    }
    
  }
  
}
