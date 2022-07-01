package br.com.neuverse.entity;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import br.com.neuverse.enumerador.Status;
import br.com.neuverse.principal.Log;

public class ButtonGpioRaspPi {
    private Integer id;
    private Integer tipo;
    private final GpioController gpio;
    private final GpioPinDigitalOutput interruptor;
    private final GpioPinDigitalInput comando;

    public ButtonGpioRaspPi(Integer gpioInterruptor,Integer gpioComando,Integer p){
        Log.log(this,"I:"+gpioInterruptor+" C:"+gpioComando,"INFO");
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
                               
            }
        });

    }

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void ligar(){
        interruptor.high();
    }
    public void desligar(){
        interruptor.low();
    }
}