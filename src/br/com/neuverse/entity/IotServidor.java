package br.com.neuverse.entity;

public class IotServidor {
    private String endIp;
    private String porta;
    private String jSon;
    private Integer id;

    public String getEndIp() {
        return endIp;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getjSon() {
        return jSon;
    }
    public void setjSon(String jSon) {
        this.jSon = jSon;
    }
    public String getPorta() {
        return porta;
    }
    public void setPorta(String porta) {
        this.porta = porta;
    }
    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }
}
