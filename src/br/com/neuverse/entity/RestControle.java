package br.com.neuverse.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import br.com.neuverse.enumerador.Status;

public class RestControle extends Device {

    private Integer id;
    private Object toDoObject;
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
    ControleRest ctrlRest = new ControleRest();

    private Boolean setButton(Status st){
        try{
            Boolean bOk = false;
            Conector plug = (Conector) toDoObject;
            ComandoIOT cIot = new ComandoIOT();
            ctrlRest.setIp(plug.getIp());
            for(ButtonIot bIot : plug.getButtons()){
                if(bIot.getButtonID().equals(this.id)){
                    bIot.setStatus(st);
                    bOk = true;
                    break;
                }
            }
            if(!bOk)
                return false;
            cIot.setAcao("processaBtn");            
            cIot.setConteudo(gson.toJson(plug.getButtons())); 
            ctrlRest.setIp(plug.getIp());
            ComandoIOT cIotRet = ctrlRest.sendRest(gson.toJson(cIot));
            if(cIotRet.getRetorno().equals("Ok"))
                return true;
            else
                return false;
        }
        catch(Exception e){

        }
        return false;
    }

    @Override
    boolean on() {       
        return setButton(Status.ON);            
    }
    @Override
    boolean off() {
        return setButton(Status.OFF);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
        
    }
    

    @Override
    Status getStatus() {
        try{
            Conector plug = (Conector) toDoObject;
            ComandoIOT cIot = new ComandoIOT();
            ctrlRest.setIp(plug.getIp());
            cIot.setAcao("listarBotoes"); 
            ComandoIOT cIotRet = ctrlRest.sendRest(gson.toJson(cIot));
            if(cIotRet.getRetorno().equals("Ok")){
                Type listType = new TypeToken<ArrayList<ButtonIot>>(){}.getType();
                List<ButtonIot> listaBiot =gson.fromJson(cIotRet.getResultado(), listType);
                plug.setButtons(listaBiot);
                for(ButtonIot bIot : listaBiot){
                    if(bIot.getButtonID().equals(this.id)){
                       return bIot.getStatus();
                    }
                }                   
            }
        }
        catch(Exception e){
        }
        return null;
    }

    @Override
    public void toDo(Object obj) {
        toDoObject = obj;        
    }
    
}