package br.com.neuverse.entity;

import java.util.List;

public class ConfigIOT {

    public ConfigIOT(){
        ssidSessao = new SsidSessao();
        servidorSessao = new ServidorSessao();
    }

    public ConectorSessao getConectorSessao() {
        return conectorSessao;
    }

    public void setConectorSessao(ConectorSessao conectorSessao) {
        this.conectorSessao = conectorSessao;
    }

    public ServidorRestSessao getServidorRestSessao() {
        return servidorRestSessao;
    }

    public void setServidorRestSessao(ServidorRestSessao servidorRestSessao) {
        this.servidorRestSessao = servidorRestSessao;
    }

    public List<ButtonIot> getButtonIOTSessao() {
        return buttonIOTSessao;
    }

    public void setButtonIOTSessao(List<ButtonIot> buttonIOTSessao) {
        this.buttonIOTSessao = buttonIOTSessao;
    }

    public ServidorSessao getServidorSessao() {
        return servidorSessao;
    }

    public void setServidorSessao(ServidorSessao servidorSessao) {
        this.servidorSessao = servidorSessao;
    }

    private SsidSessao ssidSessao;
    private ServidorSessao servidorSessao;
    private ServidorRestSessao servidorRestSessao;
    private ConectorSessao conectorSessao;

    public class ServidorSessao{
        private String endereco;
        private Integer porta;

        public String getEndereco() {
            return endereco;
        }
        public Integer getPorta() {
            return porta;
        }
        public void setPorta(Integer porta) {
            this.porta = porta;
        }
        public void setEndereco(String endereco) {
            this.endereco = endereco;
        }
    }

    public class SsidSessao{
        private String ssid;
        private String password;
        public String getSsid() {
            return ssid;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
        public void setSsid(String ssid) {
            this.ssid = ssid;
        }
    }

    public SsidSessao getSsidSessao() {
        return ssidSessao;
    }

    public void setSsidSessao(SsidSessao ssidSessao) {
        this.ssidSessao = ssidSessao;
    }

    public class ServidorRestSessao {
        private String ip;
        private Integer porta;

        public String getIp() {
            return ip;
        }
        public Integer getPorta() {
            return porta;
        }
        public void setPorta(Integer porta) {
            this.porta = porta;
        }
        public void setIp(String ip) {
            this.ip = ip;
        }
    }

    public class ConectorSessao{
        private String usuario;
        private String senha;
        public String getUsuario() {
            return usuario;
        }
        public String getSenha() {
            return senha;
        }
        public void setSenha(String senha) {
            this.senha = senha;
        }
        public void setUsuario(String usuario) {
            this.usuario = usuario;
        }
    }
    

    private  List<ButtonIot> buttonIOTSessao; 
    
}
