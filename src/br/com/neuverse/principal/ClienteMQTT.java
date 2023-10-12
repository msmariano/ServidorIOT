package br.com.neuverse.principal;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import br.com.neuverse.entity.Dispositivo;
import br.com.neuverse.entity.Pool;

public class ClienteMQTT implements MqttCallbackExtended ,IMqttMessageListener{

    private final String serverURI;
    private MqttClient client;
    private final MqttConnectOptions mqttOptions;
    private String poolId;
    private ServidorIOT serverIOT;
    Type TypeListPool = new TypeToken<ArrayList<Pool>>() {}.getType();

    public ServidorIOT getServerIOT() {
        return serverIOT;
    }

    public void setServerIOT(ServidorIOT serverIOT) {
        this.serverIOT = serverIOT;
    }

    public String getPoolId() {
        return poolId;
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }

    public ClienteMQTT(String serverURI, String usuario, String senha) {
        this.serverURI = serverURI;

        mqttOptions = new MqttConnectOptions();
        mqttOptions.setMaxInflight(200);
        mqttOptions.setConnectionTimeout(0);
        mqttOptions.setKeepAliveInterval(10);
        mqttOptions.setAutomaticReconnect(true);
        mqttOptions.setCleanSession(false);
        //mqttOptions.setSSLHostnameVerifier(null);
        
        

        if (usuario != null && senha != null) {
            mqttOptions.setUserName(usuario);
            mqttOptions.setPassword(senha.toCharArray());
        }
    }

    public IMqttToken subscribe(int qos, IMqttMessageListener gestorMensagemMQTT, String... topicos) {
        if (client == null || topicos.length == 0) {
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
            return client.subscribeWithResponse(topicos, qoss, listners);
        } catch (MqttException ex) {
            System.out.println(String.format("Erro ao se inscrever nos tópicos %s - %s", Arrays.asList(topicos), ex));
            return null;
        }
    }

    public void unsubscribe(String... topicos) {
        if (client == null || !client.isConnected() || topicos.length == 0) {
            return;
        }
        try {
            client.unsubscribe(topicos);
        } catch (MqttException ex) {
            System.out.println(String.format("Erro ao se desinscrever no tópico %s - %s", Arrays.asList(topicos), ex));
        }
    }

    public void iniciar() {
        try {
            Log.log(this,"Conectando no broker MQTT em " + serverURI,"DEBUG");
            client = new MqttClient(serverURI, "73cd8514e7c447ff91d697b4b02f88c5", new MqttDefaultFilePersistence(System.getProperty("java.io.tmpdir")));
            client.setCallback(this);
            client.connect(mqttOptions);
        } catch (MqttException ex) {
            System.out.println("Erro ao se conectar ao broker mqtt " + serverURI + " - " + ex);
        }
    }

    public void finalizar() {
        if (client == null || !client.isConnected()) {
            return;
        }
        try {
            client.disconnect();
            client.close();
        } catch (MqttException ex) {
            System.out.println("Erro ao desconectar do broker mqtt - " + ex);
        }
    }

    public void publicar(String topic, byte[] payload, int qos) {
        publicar(topic, payload, qos, false);
    }

    public void publicar(String topic, byte[] payload, int qos, boolean retained) {
        try {
            if (client.isConnected()) {
                client.publish(topic, payload, qos, retained);
                System.out.println(String.format("Tópico %s publicado. %dB", topic, payload.length));
            } else {
                System.out.println("Cliente desconectado, não foi possível publicar o tópico " + topic);
            }
        } catch (MqttException ex) {
            System.out.println("Erro ao publicar " + topic + " - " + ex);
        }
    }

    @Override
    public void connectionLost(Throwable thrwbl) {
        System.out.println("Conexão com o broker perdida -" + thrwbl);
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        System.out.println("Cliente MQTT " + (reconnect ? "reconectado" : "conectado") + " com o broker " + serverURI);
        subscribe(0, this,"br/com/neuverse/servidores/" +poolId+ "/#","br/com/neuverse/geral/info");
       
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.log(this, "messageArrivedMQTT["+topic+"]:"+new String(message.getPayload()), "DEBUG");

        /*if (topic.equals("br/com/neuverse/servidores/" + serverIOT.pool.getId() + "/atualizar")) {
            Gson gson = new GsonBuilder().create();
            List<Pool> poolsAtualizar = gson.fromJson(new String(message.getPayload()), TypeListPool);
            for (Pool poolAtualizar : poolsAtualizar) {
                for (Pool pool : serverIOT.conectores) {
                    if (pool.getId().equals(poolAtualizar.getId())) {
                        for (Dispositivo dispositivoAtualizar : poolAtualizar.getDispositivos()) {
                            Dispositivo dispositivo = pool.buscar(dispositivoAtualizar.getId());
                            if (dispositivo != null)
                                dispositivo.updateStatus(dispositivoAtualizar.getStatus());
                        }
                        break;
                    }
                }
            }
        } else if (topic.equals("br/com/neuverse/geral/info")) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            ClienteMQTT clienteMQTTSend = new ClienteMQTT("ssl://73cd8514e7c447ff91d697b4b02f88c5.s1.eu.hivemq.cloud:8883","neuverse","M@r040370");
            clienteMQTTSend.iniciar();
            clienteMQTTSend.publicar("br/com/neuverse/servidores/lista", gson.toJson(serverIOT.conectores).getBytes(), 0);
            Thread.sleep(1000);
            clienteMQTTSend.finalizar();
        }*/
    }

  

}