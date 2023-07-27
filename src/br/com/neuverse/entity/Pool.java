package br.com.neuverse.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.annotations.Expose;

public class Pool {
    @Expose(serialize = true)
    private String id;
    @Expose(serialize = true)
   	private List<Dispositivo> dispositivos = new ArrayList<>();

    public Dispositivo buscar(Integer id){
        for (Dispositivo dispositivo : dispositivos){
            if(dispositivo.getId().equals(id))
                return dispositivo;
        }
        return null;
    }
  

    public Pool(){
        UUID uniqueKey = UUID.randomUUID();
		String idGerado = uniqueKey.toString();
        id = idGerado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Dispositivo> getDispositivos() {
        return dispositivos;
    }

    public void setDispositivos(List<Dispositivo> dispositivos) {
        this.dispositivos = dispositivos;
    }	
    
}
