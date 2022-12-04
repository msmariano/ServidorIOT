package br.com.neuverse.principal;

import com.pi4j.io.gpio.BananaPiPin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformManager;

class  Banapiteste {
    
    private GpioController gpio;
    private GpioPinDigitalOutput out;
  
    public void testeBananaPiH(Integer ad){
        try{
            PlatformManager.setPlatform(Platform.BANANAPI);
            gpio = GpioFactory.getInstance();
            Pin pin = BananaPiPin.getPinByAddress(ad);  
            out = gpio.provisionDigitalOutputPin(pin);
            out.high();
        }
        catch(Exception e){
           System.out.println(e.getMessage());
        }
    }
    public void testeBananaPiL(Integer ad){
        try{
            PlatformManager.setPlatform(Platform.BANANAPI);
            gpio = GpioFactory.getInstance();
            Pin pin = BananaPiPin.getPinByAddress(ad);  
            out = gpio.provisionDigitalOutputPin(pin);
            out.low();
            
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
