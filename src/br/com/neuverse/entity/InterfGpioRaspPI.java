package br.com.neuverse.entity;

import com.google.gson.annotations.Expose;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import br.com.neuverse.enumerador.Status;
import br.com.neuverse.enumerador.TipoIOT;

public class InterfGpioRaspPI extends Dispositivo {

    private GpioController gpio;
    private Integer tipo;
    private GpioPinDigitalInput entrada;
    private GpioPinDigitalOutput saida;
    private Integer nivelAcionamento;
    
    public InterfGpioRaspPI(){

    }
    
    public InterfGpioRaspPI(Integer gpioSaida, Integer gpioEntrada, Integer p, Integer id, String nickNameString,Integer genero) {

        System.out.println("Iniciando "+gpioSaida+" "+gpioEntrada+" "+p+" "+id+" "+nickNameString);
        setId(id);
        setNick(nickNameString);
        gpio = GpioFactory.getInstance();
        Pin pinIN = null;
        Pin pinOUT = null;
        tipo = p;
        System.out.println(genero);
          try{
            setGenero(TipoIOT.getEnum(genero));
        }
        catch(Exception e){
        }
         
        nivelAcionamento = p;

        if(nivelAcionamento == 0){
            setNivelAcionamento(Status.LOW);
        }
        else{
            setNivelAcionamento(Status.HIGH);
        }
        if(gpioSaida>-1){
            pinOUT = RaspiPin.getPinByAddress(gpioSaida);
            saida = gpio.provisionDigitalOutputPin(pinOUT);
        }
        if (gpioEntrada!=null&&gpioEntrada > -1) {
            pinIN = RaspiPin.getPinByAddress(gpioEntrada);
            if (p.equals(1))
                entrada = gpio.provisionDigitalInputPin(pinIN, PinPullResistance.PULL_UP);
            else if (p.equals(0))
                entrada = gpio.provisionDigitalInputPin(pinIN, PinPullResistance.PULL_DOWN);
            else
                entrada = gpio.provisionDigitalInputPin(pinIN, PinPullResistance.OFF);

            entrada.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                    if (tipo.equals(1)) {
                        if (saida.isHigh())
                            saida.low();
                        else
                            saida.high();
                    } else {
                        if (saida.isHigh())
                            saida.high();
                        else
                            saida.low();
                    }
                }
            });

        }
        if(gpioSaida>-1){
            saida.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                    if (event.getState().equals(PinState.HIGH))
                        setStatus(Status.ON);
                    else
                    setStatus(Status.OFF);
                    sendEvents();
                }
            });
            setStatus(getStatus());
        }

    }

    @Override
    public boolean on() {
        try {
            saida.high();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public boolean off() {
        try {
            saida.low();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public Status getStatus() {
        if (tipo.equals(1)) {
            if (saida.isHigh())
                return Status.ON;
            else
                return Status.OFF;
        } else {
            if (saida.isHigh())
                return Status.OFF;
            else
                return Status.ON;
        }
    }

    @Override
    public void updateStatus(Status st) {
        if (st == Status.ON) {
            on();
        } else if (st == Status.OFF) {
            off();
        }

    }
}
