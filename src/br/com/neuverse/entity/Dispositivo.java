package br.com.neuverse.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import br.com.neuverse.enumerador.Status;
import br.com.neuverse.principal.ClienteMQTT;

public class Dispositivo {

    @Expose(serialize = true)
    private Status status;

    @Expose(serialize = true)
    private Integer id;

    @Expose(serialize = true)
    private String nick;

    private String idPool;

    public String getIdPool() {
        return idPool;
    }

    public void setIdPool(String idPool) {
        this.idPool = idPool;
    }

    public boolean on() {
        return false;
    }

    public boolean off() {
        return false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status st) {
        status = st;
    }

    public void updateStatus(Status st) {

    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void sendEvents() {
        try{
        Pool pool = new Pool();
        List<Pool> conectores = new ArrayList<>();
        conectores.add(pool);
        pool.setId(idPool);
        pool.getDispositivos().add(this);

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        ClienteMQTT clienteMQTTSend = new ClienteMQTT("tcp://broker.mqttdashboard.com:1883", "neuverse",
                "M@r040370");
        clienteMQTTSend.iniciar();
        String msg = gson.toJson(conectores);
        System.out.println(msg);
        clienteMQTTSend.publicar("br/com/neuverse/servidores/events", msg.getBytes(), 0);
        clienteMQTTSend.finalizar();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

}
