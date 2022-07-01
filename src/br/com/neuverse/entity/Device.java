package br.com.neuverse.entity;

public abstract class Device {
   
    abstract boolean on();
    abstract boolean off();
    private Integer id;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
}
