package br.com.neuverse.entity;

import br.com.neuverse.enumerador.Status;

public abstract class Device {
   
    abstract boolean on();
    abstract public Integer getId();
    abstract public void setId(Integer id);
    abstract boolean off();
    abstract Status getStatus(); 
    abstract public void toDo(Object obj);
}
