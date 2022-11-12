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

    public void atualizar() {
        Log.log(this, "atualizando status device", "DEBUG");

        Conector conEnviar = null;
        boolean parar = false;
        for (Conector conector : conectores) {
            for (Device device : conector.getDevices()) {
                Log.log(this, "listando device:"+device.toString(), "DEBUG");
                if (device.equals(this)) {
                    conEnviar = conector;
                    Log.log(this, "Encontrou device:"+this.toString(), "DEBUG");
                    parar = true;
                    break;
                }
            }
            if(parar)
                break;
        }
        if (clientes != null) {
            String jSon = "";
            if(conEnviar!=null){
                Gson gson = new GsonBuilder()
			        .setDateFormat("dd/MM/yyyy HH:mm:ss")
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
                jSon = gson.toJson(conEnviar,Conector.class);
            }
            Log.log(this,"achou numero de clientes "+clientes.size(), "DEBUG");
            try {
                for (Cliente cliente : clientes) {
                    Log.log(this,"Enviando notificacao para cliente["+jSon+"] "+ cliente.getNickName(),"DEBUG");
                    if (cliente.getIsLogado()) {
                        Log.log(this, "[" + cliente.getNickName() + "]enviando atualizacao de status", "DEBUG");
                        cliente.enviarAtualizar(conEnviar);
                    }
                }
            } catch (Exception e) {
                Log.log(this, "atualizar()" + e.getMessage(), "DEBUG");
            }
        }
    }
}
