package br.com.neuverse.entity;

import com.google.gson.annotations.Expose;

import br.com.neuverse.enumerador.Status;

public class Dispositivo {

    @Expose(serialize = true)
    private Status status;

    @Expose(serialize = true)
    private Integer id;

    @Expose(serialize = true)
    private String nick;

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

}
