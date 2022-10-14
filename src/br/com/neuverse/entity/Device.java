package br.com.neuverse.entity;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.neuverse.enumerador.Status;
import br.com.neuverse.principal.Cliente;
import br.com.neuverse.principal.Log;

public abstract class Device {
   
    abstract boolean on();
    abstract public Integer getId();
    abstract public void setId(Integer id);
    abstract boolean off();
    abstract Status getStatus(); 
    abstract public void toDo(Object obj);
    private List<Conector> conectores;
    private List<Cliente> clientes;

    public List<Cliente> getClientes() {
        return clientes;
    }
    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }
    public List<Conector> getConectores() {
        return conectores;
    }
    public void setConectores(List<Conector> conectores) {
        this.conectores = conectores;
    }

    public void atualizar(){
       Log.log(this,"atualizando status device","DEBUG");
        if(clientes!=null){
            try {
                for(Cliente cliente : clientes){
                    if(cliente.getIsLogado()){
                        Log.log(cliente,"["+cliente.getNickName()+"]enviando atualizacao de status","DEBUG");
                        cliente.enviarAtualizar();
                    }                    
                }
            } catch (Exception e) {  
                Log.log(this,"atualizar()"+e.getMessage(),"DEBUG");              
            }
        }

    }
}
