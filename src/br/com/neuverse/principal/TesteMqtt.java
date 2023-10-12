package br.com.neuverse.principal;

import java.text.SimpleDateFormat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class TesteMqtt implements  MqttCallbackExtended ,IMqttMessageListener {

  
        private MqttClient mqttClient;
        private MqttConnectOptions mqttOptions;
   
        public MqttClient getMqttClient(){
            return mqttClient;
        }

        public static void main(String[] args) throws InterruptedException {

            TesteMqtt t = new TesteMqtt();
            t.iniciar();

            try {
                Thread.sleep(5000);
                t.getMqttClient().publish("br/com/neuverse/geral/info", ".".getBytes(), 0, false);
            } catch (MqttException e) {
                
            }
            
        }

        public void iniciar(){

            mqttOptions = new MqttConnectOptions();
            mqttOptions.setMaxInflight(200);
            mqttOptions.setKeepAliveInterval(10);
            mqttOptions.setAutomaticReconnect(true);
            mqttOptions.setCleanSession(false);
            mqttOptions.setSSLHostnameVerifier(null);
            mqttOptions.setUserName("neuverse");
            mqttOptions.setPassword("M@r040370".toCharArray());
            try {
                mqttClient = new MqttClient("ssl://73cd8514e7c447ff91d697b4b02f88c5.s1.eu.hivemq.cloud:8883", "1234", new MqttDefaultFilePersistence(System.getProperty("java.io.tmpdir")));
                mqttClient.setCallback(this);
                mqttClient.connect(mqttOptions);
            } catch (MqttException ex) {
                Log.log(this,"Erro ao se conectar ao broker mqtt "+ " - " + ex,"ERROR");
            }
        }

        @Override
        public void connectionLost(Throwable cause) {
            
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.log(this, "messageArrivedMQTT["+topic+"]:"+new String(message.getPayload()), "DEBUG");
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            subscribe(0, this,"br/com/neuverse/geral/lista");
        }

        public IMqttToken subscribe(int qos, IMqttMessageListener gestorMensagemMQTT, String... topicos) {
        if (mqttClient == null || topicos.length == 0) {
            return null;
        }
        for(String s : topicos){
             Log.log(this,"Subscribe:"+s,"DEBUG");
        }       

        int tamanho = topicos.length;
        int[] qoss = new int[tamanho];
        IMqttMessageListener[] listners = new IMqttMessageListener[tamanho];

        for (int i = 0; i < tamanho; i++) {
            qoss[i] = qos;
            listners[i] = gestorMensagemMQTT;
        }
        try {
            return mqttClient.subscribeWithResponse(topicos, qoss, listners);
        } catch (MqttException ex) {
            return null;
        }
    }

    
}
