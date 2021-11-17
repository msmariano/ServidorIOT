package br.com.neuverse.entity;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class ButtonGpioRaspPi {
    private Integer id;
    private final GpioController gpio;
    private final GpioPinDigitalOutput interruptor;
    private final GpioPinDigitalInput comando;

    public ButtonGpioRaspPi(Integer gpioInterruptor,Integer gpioComando){
        gpio = GpioFactory.getInstance();
        interruptor = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(gpioInterruptor));
        comando = gpio.provisionDigitalInputPin(RaspiPin.getPinByAddress(gpioComando), PinPullResistance.PULL_DOWN);
        comando.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (interruptor.isHigh())
                    desligar();
                else
                    ligar();
                
            }
        });

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