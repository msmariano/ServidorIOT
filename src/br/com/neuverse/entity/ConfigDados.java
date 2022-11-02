package br.com.neuverse.entity;

import br.com.neuverse.enumerador.Status;

public class ConfigDados {
    private Integer id;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    private Integer pIn;
    public Integer getpIn() {
        return pIn;
    }
    public void setpIn(Integer pIn) {
        this.pIn = pIn;
    }
    private Integer pOut;
    public Integer getpOut() {
        return pOut;
    }
    public void setpOut(Integer pOut) {
        this.pOut = pOut;
    }
    private String nick;
    public String getNick() {
        return nick;
    }
    public void setNick(String nick) {
        this.nick = nick;
    }
    private Status funcao;
    public Status getFuncao() {
        return funcao;
    }
    public void setFuncao(Status funcao) {
        this.funcao = funcao;
    }
    private Status status;
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    private Status tecla;
    public Status getTecla() {
        return tecla;
    }
    public void setTecla(Status tecla) {
        this.tecla = tecla;
    }

    
}
