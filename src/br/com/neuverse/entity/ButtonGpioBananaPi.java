package br.com.neuverse.entity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import br.com.neuverse.enumerador.Status;
import br.com.neuverse.principal.Log;

public class ButtonGpioBananaPi extends Device {
    private Integer out;
    private Integer in;
    private String gpioNameOut;
    private String gpioNameIn;
    private Integer idR;
    private Object toDoObject;
    private Status st = Status.OFF;
    private String btnsCtrlControlar;
    private Integer tpControle;

    public ButtonGpioBananaPi(Integer gpioInterruptor, Integer gpioComando, Integer p, Integer idVariavel,
            String btnsCtrl, Integer tipoControle) throws IOException {

        Log.log(this,"Placa BananaPi ID:"+idVariavel+" I:"+gpioInterruptor+" O:"+gpioComando+" btns:"+btnsCtrl
            +" tipo:"+tipoControle,"INFO");
        
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
                        while (true) {
                            try {
                                BufferedReader le = new BufferedReader(
                                        new FileReader("/sys/class/gpio/" + gpioNameOut + "/value"));
                                char b[] = new char[10];
                                le.read(b);
                                String valor = String.valueOf(b);
                                le.close();
                                ButtonIot bIot = (ButtonIot) toDoObject;
                                Boolean bAtualizar = false;
                                if (valor.trim().equals("0")) {
                                    if (st.equals(Status.ON))
                                        bAtualizar = true;
                                    st = Status.OFF;
                                    if(bIot!=null)
                                        bIot.setStatus(Status.OFF);
                                } else if (valor.trim().equals("1")) {
                                    if (st.equals(Status.OFF))
                                        bAtualizar = true;
                                    st = Status.ON;
                                    if(bIot!=null)
                                        bIot.setStatus(Status.ON);
                                }
                                if (bAtualizar) {
                                    bAtualizar = false;
                                    try {
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                atualizar();
                                            }
                                        }.start();
                                    } catch (Exception e) {
                                        Log.log(this, "atualizar() listener" + e.getMessage(), "DEBUG");
                                    }
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
            System.out.println("/sys/class/gpio/" + gpioNameOut + "/value");
            gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/" + gpioNameOut + "/value", false));
            gpio.write("1");
            gpio.close();
        } catch (Exception e) {
        }
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
    public boolean off() {
        try {
            BufferedWriter gpio;
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

    @Override
    public void toDo(Object obj) {
        toDoObject = obj;
    }

}
