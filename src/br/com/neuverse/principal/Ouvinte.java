package br.com.neuverse.principal;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Ouvinte implements IMqttMessageListener {

    public Ouvinte(ClienteMQTT clienteMQTT, String topico, int qos) {
        clienteMQTT.subscribe(qos, this, topico);
    }

    @Override
    public void messageArrived(String topico, MqttMessage mm) throws Exception {
        System.out.println("Mensagem recebida:");
        System.out.println("\tTópico: " + topico);
        System.out.println("\tMensagem: " + new String(mm.getPayload()));
        System.out.println(mm.getId());


        

    

        if(topico.equals("br/com/neuverse/servidores/inserir")){

        }
        else if(topico.equals("br/com/neuverse/servidores/listar")){
            ClienteMQTT clienteMQTT = new ClienteMQTT("tcp://broker.mqttdashboard.com:1883", "neuverse", "M@r040370");
            clienteMQTT.iniciar();
            clienteMQTT.publicar("br/com/neuverse/id",  "".getBytes(), 0);
        }

        
        
    }

}
