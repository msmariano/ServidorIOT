package br.com.neuverse.entity;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Hidrometro implements GpioPinListenerDigital{

    private final GpioController gpio;

   
    public Hidrometro(Integer gpioHidrometro){
        gpio = GpioFactory.getInstance();
        gpio.provisionDigitalInputPin(RaspiPin.getPinByAddress(gpioHidrometro), PinPullResistance.OFF);
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        
        
    }

    
}