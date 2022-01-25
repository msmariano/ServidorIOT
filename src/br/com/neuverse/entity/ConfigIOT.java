package br.com.neuverse.entity;

public class ConfigIOT {

    public ConfigIOT(){
        ssidSessao = new SsidSessao();
        servidorSessao = new ServidorSessao();
    }

    public ServidorSessao getServidorSessao() {
        return servidorSessao;
    }

    public void setServidorSessao(ServidorSessao servidorSessao) {
        this.servidorSessao = servidorSessao;
    }

    private SsidSessao ssidSessao;
    private ServidorSessao servidorSessao;

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
    
}
