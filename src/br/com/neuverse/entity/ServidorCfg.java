package br.com.neuverse.entity;

public class ServidorCfg {
    private Integer id;
    private String nome;
    private String ip;
    private Integer porta;
    private String macAdress;
    private String usuario;
    private String senha;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public Integer getPorta() {
        return porta;
    }
    public void setPorta(Integer porta) {
        this.porta = porta;
    }
    public String getMacAdress() {
        return macAdress;
    }
    public void setMacAdress(String macAdress) {
        this.macAdress = macAdress;
    }
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
}
