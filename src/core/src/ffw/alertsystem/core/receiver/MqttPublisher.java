package ffw.alertsystem.core.receiver;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import ffw.alertsystem.core.ApplicationConfig;
import ffw.alertsystem.core.ApplicationLogger;
import ffw.alertsystem.util.SSLContextCreator;



/**
 * A Mqtt-based message-publisher. Connects to a mosquitto-broker and publishes
 * new messages from the @MessageReceiver.
 */
public class MqttPublisher implements MessagePublisher {
  
  private ApplicationConfig config;
  
  private ApplicationLogger log;
  
  private MqttClient client;
  
  private String brokerURI;
  
  private String topic;
  
  
  
  @Override
  public void init(ApplicationConfig config, ApplicationLogger log) {
    this.config = config;
    this.log    = log;
  }
  
  @Override
  public void start() {
    brokerURI = config.getParam("mqtt-broker-uri");
    topic     = config.getParam("mqtt-topic");
    
    try {
      client = new MqttClient(brokerURI, config.getParam("mqtt-client-id"));
      
      MqttConnectOptions options = new MqttConnectOptions();
//      options.setCleanSession(false);
//      options.setConnectionTimeout(60);
//      options.setKeepAliveInterval(60);
      
      options.setSocketFactory(
                SSLContextCreator.create(
                  config.getParam("ca-certificate"),
                  config.getParam("client-certificate"),
                  config.getParam("client-keyfile"), ""
                ).getSocketFactory()
              );
      options.setWill(
                client.getTopic(topic),"I'm gone :(".getBytes(), 0, false
              );
      client.connect(options);
      
      log.info("connected to broker " + brokerURI + " on topic " + topic);
      
    } catch (MqttException e) {
      log.error("could not connect to broker " + brokerURI, e, true);
    }
  }
  
  @Override
  public void newMessage(String message) {
    log.info("publish message: " + message);
    MqttTopic t = client.getTopic(topic);
    
    try {
      t.publish(new MqttMessage(message.getBytes()));
    } catch (MqttException e) {
      log.error("could not publish message", e, true);
    }
  }
  
  @Override
  public void stop() {
    try {
      client.disconnect();
      client.close();
    } catch (MqttException e) {
      log.error("could not close mqtt-connection", e, true);
    }
  }
  
}
