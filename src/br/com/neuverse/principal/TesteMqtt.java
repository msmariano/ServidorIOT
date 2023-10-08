package br.com.neuverse.principal;

import java.text.SimpleDateFormat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class TesteMqtt  {

  

   

        public static void main(String[] args) throws InterruptedException {
            ClienteMQTT clienteMQTT = new ClienteMQTT("tcp://broker.mqttdashboard.com:1883", "neuverse", "M@r040370");
            clienteMQTT.iniciar();
    
            //new Ouvinte(clienteMQTT, "br/com/neuverse/servidores/lista", 1);
            new Ouvinte(clienteMQTT,"br/com/neuverse/servidores/events",1);
            while(true){
    
            Thread.sleep(10000);
    
                //clienteMQTT.publicar("br/com/neuverse/geral/info",  "Cliente".getBytes(), 1);
                //clienteMQTT.publicar("br/com/neuverse/servidores/inserir",  "Matinhos".getBytes(), 1);
                //clienteMQTT.publicar("br/com/neuverse/servidores/listar",  "QuartoRamon".getBytes(), 1);
            }
            
        }
    
}
