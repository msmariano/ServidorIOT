package br.com.neuverse.entity;

import java.util.Date;

public class Versao {
    static final String versao = "1.0.30";
    static final String data = "19/07/2023 00:00";
    static final String backlog = "Inserido controle de selecao de botaoIOT.";
    static final StringBuilder sb  = new StringBuilder();
  
    public Versao(){
        upDate = new Date();
        sb.append("Inserido controle de selecao de botaoIOT.\r\n");
        sb.append("Criado keepalive cliente network.\r\n");
        sb.append("Implementado gpio bananapi.\r\n");
    }
    public String ver(){
        return "Versao "+versao+" "+data;
    }  
    public String getVersao(){
        return versao;
    }  
    private Date upDate;
   

    /**
     * @return Date return the upDate
     */
    public Date getUpDate() {
        return upDate;
    }

    /**
     * @param upDate the upDate to set
     */
    public void setUpDate(Date upDate) {
        this.upDate = upDate;
    }

}
