package br.com.neuverse.entity;

public class Interruptor {
    private int estadoAtual;
    private String acao;
    private String retorno;
    
    public int getEstadoAtual() {
        return estadoAtual;
    }
    public String getRetorno() {
        return retorno;
    }
    public void setRetorno(String retorno) {
        this.retorno = retorno;
    }
    public String getAcao() {
        return acao;
    }
    public void setAcao(String acao) {
        this.acao = acao;
    }
    public void setEstadoAtual(int estadoAtual) {
        this.estadoAtual = estadoAtual;
    }
    
    
}
