package br.com.neuverse.entity;

import java.util.Date;

public class Versao {
    static final String versao = "1.0.13";
    static final String data = "22/10/2022 21:00";
    public Versao(){
        upDate = new Date();
    }
    public String ver(){
        return "Versao "+versao+" "+data;
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
