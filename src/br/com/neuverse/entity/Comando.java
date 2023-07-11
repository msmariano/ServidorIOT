package br.com.neuverse.entity;

import br.com.neuverse.enumerador.ComEnum;
import br.com.neuverse.enumerador.Status;

public class Comando {
    private ComEnum comando;
    private Status stFiltro;
    private Status stDreno;
    private Status comTo;
    private Status nivel;
    private String mens;
    private Status timer;
    private Status device;
    private String nick;

    public String getNick(){
        return nick;
    }

    public void setNick(String arg){
        nick = arg;
    }

    public Status getDevice() {
        return device;
    }

    public void setDevice(Status arg) {
        device = arg;
    }

    public Status getTimer() {
        return timer;
    }

    public void setTimer(Status arg) {
        timer = arg;
    }

    public Status getStDreno() {
        return stDreno;
    }

    public void setStDreno(Status arg) {
        stDreno = arg;
    }

    public Status getComTo() {
        return comTo;
    }

    public void setComTo(Status arg) {
        comTo = arg;
    }

    public Status getStFiltro() {
        return stFiltro;
    }

    public void setStFiltro(Status arg) {
        stFiltro = arg;
    }

    public String getMens() {
        return mens;
    }

    public void setMens(String arg) {
        mens = arg;
    }

    public Status getNivel() {
        return nivel;
    }

    public void setNivel(Status arg) {
        nivel = arg;
    }

    public ComEnum getComando() {
        return comando;
    }

    public void setComando(ComEnum arg) {
        comando = arg;
    }

}
