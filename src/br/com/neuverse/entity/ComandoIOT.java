package br.com.neuverse.entity;

public class ComandoIOT {
    private String acao;
    private String retorno;
    private String estadoAtual;
    private String resultado;
    private Integer retardo;
    private Integer tipo;

    public String getAcao() {
        return acao;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Integer getRetardo() {
        return retardo;
    }

    public void setRetardo(Integer retardo) {
        this.retardo = retardo;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getEstadoAtual() {
        return estadoAtual;
    }

    public void setEstadoAtual(String estadoAtual) {
        this.estadoAtual = estadoAtual;
    }

    public String getRetorno() {
        return retorno;
    }

    public void setRetorno(String retorno) {
        this.retorno = retorno;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

}
