package br.com.neuverse.entity;

import br.com.neuverse.enumerador.ComEnum;
import br.com.neuverse.enumerador.Status;

public class Comando {
    private ComEnum comando;
    //private Status stFiltro;
    //private Status stDreno;
    //private String comTo;
    private Status nivel;



    public Status getNivel(){
        return nivel;
    }

    public void setNivel(Status arg){
        nivel = arg;
    }

    public ComEnum getComando(){
        return comando;
    }
    public void setComando(ComEnum arg){
        comando = arg;
    }
    
}