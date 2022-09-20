package br.com.neuverse.entity;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import br.com.neuverse.enumerador.Status;
import br.com.neuverse.principal.Log;

public class ButtonGpioRaspPi extends Device{
    private Integer idR;
    private Integer tipo;
    private final GpioController gpio;
    private final GpioPinDigitalOutput interruptor;
    private final GpioPinDigitalInput comando;
    private Object toDoObject;

    public ButtonGpioRaspPi(Integer gpioInterruptor,Integer gpioComando,Integer p,Integer idVariavel) {
        Log.log(this,"ID:"+idVariavel+" I:"+gpioInterruptor+" C:"+gpioComando,"INFO");
        setId(idVariavel);
        idR = idVariavel;
        gpio = GpioFactory.getInstance();
        tipo = p;
        interruptor = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(gpioInterruptor));
        if(p.equals(1))
            comando = gpio.provisionDigitalInputPin(RaspiPin.getPinByAddress(gpioComando), PinPullResistance.PULL_UP);
        else if(p.equals(0)){
            comando = gpio.provisionDigitalInputPin(RaspiPin.getPinByAddress(gpioComando), PinPullResistance.PULL_DOWN);
        }
        else
            comando = gpio.provisionDigitalInputPin(RaspiPin.getPinByAddress(gpioComando), PinPullResistance.OFF);
        comando.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if(tipo.equals(1)){
                    if (interruptor.isHigh())
                        desligar();
                    else
                        ligar();
                }
                else{
                    if (interruptor.isHigh())
                        ligar();
                    else
                        desligar();
                }
                
            }
        });
        interruptor.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                ButtonIot bIot = (ButtonIot) toDoObject;  
                if(event.getState().equals(PinState.HIGH)){
                    bIot.setStatus(Status.ON);
                }
                else
                    bIot.setStatus(Status.OFF);  
            }
        });

    }

    @Override
    public Status getStatus(){
        if(tipo.equals(1)){
            if (interruptor.isHigh())
                return Status.ON;
            else
                return Status.OFF; 
        }  
        else {
            if (interruptor.isHigh())
                return Status.OFF;
            else
                return Status.ON; 

        }     
    }

    public Integer getIdR() {
        return idR;
    }

    public void setIdR(Integer idR) {
       this.idR = idR;
    }
   
    public void ligar(){
        interruptor.high();
    }
   
    public void desligar(){
        interruptor.low();
    }

    @Override
    boolean on() {
        interruptor.high();
        return false;
    }

    @Override
    boolean off() {
        interruptor.low();
        return false;
    }

    @Override
    public Integer getId() {
        return idR;
    }

    @Override
    public void setId(Integer id) {
        idR = id;
        
    }

    @Override
    public void toDo(Object obj) {
        toDoObject = obj;
    }
}