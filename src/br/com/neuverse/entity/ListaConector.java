package br.com.neuverse.entity;

import java.io.IOException;
import java.util.ArrayList;

import br.com.neuverse.enumerador.TipoIOT;

public class ListaConector extends ArrayList<Conector> {


    @Override
    public boolean add(Conector ctr){
        if (!ctr.getTipo().equals(TipoIOT.NETWORK)) {
            for(Conector conector : this){
                if(conector.getMac().equals(ctr.getMac())){
                    super.remove(conector);
                    break;
                }
            }
            super.add(ctr);
            //verifica se existe conectores de rede para atualizar servidores
            atualizaRede();
        }
        else {
            //Remover conectores trazidos pela rede
            for (Conector conNet : ctr.getConectores()) {
                for (Conector con : this) {
                    if (conNet.getMac().equals(con.getMac())) {
                        super.remove(con);
                        break;
                    }
                }
            }				
            addAll(ctr.getConectores());
        }      
        return true;
    }
    public void remover(Conector ctr){
        if (!ctr.getTipo().equals(TipoIOT.NETWORK))
            atualizaRede();    
        remove(ctr);
    }   
    public void atualizaRede(){
        for(Conector conector : this){
            if (conector.getTipo().equals(TipoIOT.NETWORK)){
                try {
                    conector.getCliente().getSocketCliente().close();
                } catch (IOException e) {
                }
            }
        }
    }
}
