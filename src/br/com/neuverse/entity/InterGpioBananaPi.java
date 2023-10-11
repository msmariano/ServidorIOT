package br.com.neuverse.entity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import br.com.neuverse.enumerador.Status;
import br.com.neuverse.enumerador.TipoIOT;
import br.com.neuverse.principal.Log;

public class InterGpioBananaPi extends Dispositivo {

     private Integer out;
    private Integer in;
    private String gpioNameOut;
    private String gpioNameIn;
    
    private Object toDoObject;
    private Status st = Status.OFF;
    private String btnsCtrlControlar;
    private Integer tpControle;
    private Integer nivelAcionamento;

    public InterGpioBananaPi(Integer gpioInterruptor, Integer gpioComando, Integer p, Integer idVariavel,
            String btnsCtrl, Integer tipoControle,String nickNameString,String idPool,Integer genero) throws IOException {

        Log.log(this,"Placa BananaPi ID:"+idVariavel+" I:"+gpioInterruptor+" O:"+gpioComando+" btns:"+btnsCtrl
            +" tipo:"+tipoControle,"INFO");
           
        setId(idVariavel);
        setNick(nickNameString);
        setIdPool(idPool);
        nivelAcionamento = p;
        System.out.println(genero);

        try{
            setGenero(TipoIOT.getEnum(genero));
        }
        catch(Exception e){
        }
               


        if(nivelAcionamento == 0){
            setNivelAcionamento(Status.LOW);
        }
        else{
            setNivelAcionamento(Status.HIGH);
        }
        gpioNameOut = "gpio" + gpioInterruptor;
        gpioNameIn = "gpio" + gpioComando;
        btnsCtrlControlar = btnsCtrl;
        tpControle =  tipoControle;       
        out = gpioInterruptor;
        in = gpioComando;
        BufferedWriter gpio;
        try {
            try {
                gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/export", false));
                if (out > -1)
                    gpio.write(String.valueOf(gpioInterruptor));
                if (in > -1)    
                    gpio.write(String.valueOf(gpioComando));
                gpio.close();
                if (out > -1)
                    st = getStatus();
            } catch (Exception e) {
            }
            if (out > -1) {
                gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/" + gpioNameOut + "/direction", false));
                gpio.write("out");
                gpio.close();
                new Thread() {
                    @Override
                    public void run() {
                        Status statusLocal = getStatus();
                        while (true) {
                            try {
                                BufferedReader le = new BufferedReader(
                                        new FileReader("/sys/class/gpio/" + gpioNameOut + "/value"));
                                char b[] = new char[10];
                                le.read(b);
                                 
                                String valor = String.valueOf(b);
                                le.close();
                                if (valor.trim().equals("0")) {
                                    setStatus(Status.OFF);
                                    if(statusLocal.equals(Status.ON)){
                                        sendEvents();
                                    }
                                    statusLocal = getStatus();
                                } else if (valor.trim().equals("1")) {
                                   setStatus(Status.ON);
                                   if(statusLocal.equals(Status.OFF)){
                                        sendEvents();
                                    }
                                    statusLocal = getStatus();
                                }                                
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                Log.log(this, "Erro ao ler arquivo out " + e.getMessage(), "DEBUG");
                            }
                        }
                    }
                }.start();
            }
            if (in > -1) {
                Log.log(this, "/sys/class/gpio/" + gpioNameIn + "/direction", "INFO");
                gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/" + gpioNameIn + "/direction", false));
                gpio.write("in");
                gpio.close();
                new Thread() {
                    @Override
                    public void run() {
                        String vlrAtual = "-1";
                        while (true) {
                            try {  
                                BufferedReader le = new BufferedReader(
                                    new FileReader("/sys/class/gpio/" + gpioNameIn + "/value"));                              
                                char b[] = new char[10];
                                le.read(b);                                
                                String valor = String.valueOf(b);
                                if(!valor.equals(vlrAtual)){
                                    vlrAtual = valor;
                                    Log.log(this,"Evento recebido...","DEBUG");
                                    if (valor.trim().equals("0")) {
                                        if(tpControle.equals(1))
                                            off();
                                        else if(tpControle.equals(2))
                                            ControlarBtns("off");
                                    } else if (valor.trim().equals("1")) {
                                        if(tpControle.equals(1))
                                            on();
                                        else if(tpControle.equals(2))
                                            ControlarBtns("on");
                                    }
                                }
                                le.close();
                                Thread.sleep(500);
                            } catch (Exception e) {
                                Log.log(this, "Erro ao ler in arquivo" + e.getMessage(), "DEBUG");
                            }
                        }
                        
                    }
                }.start();
            }
        } catch (Exception e) {
        }
        setId(idVariavel);
        setStatus(getStatus());
    }

    public void ControlarBtns(String onoff) throws IOException{
        for(String b : btnsCtrlControlar.split(";")){
            BufferedWriter gpio;
            gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio" + b + "/value", false));
            if(onoff.equals("on"))
                gpio.write("1");
            if(onoff.equals("off"))
                gpio.write("0");
            gpio.close();
        }
    }

    @Override
    public boolean on() {
        try {
            BufferedWriter gpio;
            setStatus(Status.ON);
            System.out.println("/sys/class/gpio/" + gpioNameOut + "/value");
            gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/" + gpioNameOut + "/value", false));
            gpio.write("1");
            gpio.close();
        } catch (Exception e) {
        }
        return false;
    }

   

    @Override
    public boolean off() {
        try {
            BufferedWriter gpio;
            setStatus(Status.OFF);
            System.out.println("/sys/class/gpio/" + gpioNameOut + "/value");
            gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/" + gpioNameOut + "/value", false));
            gpio.write("0");
            gpio.close();
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public Status getStatus() {
        try {
            BufferedReader le = new BufferedReader(new FileReader("/sys/class/gpio/" + gpioNameOut + "/value"));
            char b[] = new char[10];
            le.read(b);
            String valor = String.valueOf(b);
            le.close();
            if (valor.trim().equals("0")) {
                return Status.OFF;
            } else if (valor.trim().equals("1")) {
                return Status.ON;
            }
        } catch (Exception e) {
        }
        return null;
    }

   
    public void toDo(Object obj) {
        toDoObject = obj;
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
