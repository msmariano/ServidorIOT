package br.com.neuverse.entity;

import br.com.neuverse.enumerador.ComEnum;
import br.com.neuverse.enumerador.Status;

public class Comando {
    private ComEnum comando;
    private Status stFiltro;
    private Status stDreno;
    private String comTo;


    public ComEnum getComando(){
        return comando;
    }
    public void setComando(ComEnum arg){
        comando = arg;
    }
    
}
