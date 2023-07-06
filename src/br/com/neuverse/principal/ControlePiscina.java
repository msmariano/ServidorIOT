package br.com.neuverse.principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.neuverse.enumerador.Status;

public class ControlePiscina {

    private String horarios = "00:00;04:00;08:00;12:00;16:00;20:00";
    private Integer minutos = 30;
    final static private String bomba = "10";
    final static private String filtro = "17";
    final static private String chave = "9";
    final static private String nivelAlto = "20";
    final static private String nivelBaixo = "21";
    final static private String on = "1";
    final static private String off = "0";
    static private boolean onByKey = false;

    public Status retornoStatusFiltro(){
        if(lerPin(filtro).equals("1")){
            return Status.ON;
        }
        return Status.OFF;
    }


    public void inicializar(){
        monitoraAgua();
        ligarBombaFiltro(0);
        monitoraChave();
        timer(null,null);
    }

    public void timer(String hor, Integer min) {

        if (hor != null) {
            horarios = hor;
        }
        if (min != null) {
            minutos = min;
        }
        Log.log(this,"Timer Ativado para os horários:" + horarios + " por " + minutos + " minuto(s)","DEBUG");

        new Thread() {
            @Override
            public void run() {
                try {
                    BufferedWriter gpio = null;
                    try {
                        gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/export", false));
                        gpio.write(String.valueOf(17));
                        gpio.close();
                    } catch (Exception e) {
                        
                    }
                    gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio17/direction", false));
                    gpio.write("out");
                    gpio.close();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Log.log(this,"Timer iniciado as "+sdf.format(new Date()),"DEBUG");
                    while (true) {
                        String t = sdf.format(new Date());
                        Thread.sleep(1000);

                        if (horarios.contains(t)) {
                            Log.log(this,"Ativando Filtro Bomba pelo timer.","DEBUG");
                            ligarBombaFiltro(1);
                            Thread.sleep(1000 * 60 * minutos);

                            if (!onByKey) {
                                ligarBombaFiltro(0);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.log(this,"Timer "+e.getMessage(),"ERROR");
                }
            }
        }.start();
    }

    public void monitoraChave() {
        new Thread() {
            @Override
            public void run() {
                boolean acionada = false;
                boolean push = false;
                Log.log(this, "Iniciando monitoraChave", "DEBUG");
                while (true) {
                    if (lerPin(chave).equals("0")) {
                        if (!acionada) {
                            acionada = true;
                            if (!push) {
                                ligarBombaFiltro(1);
                                onByKey = true;
                                push = true;
                            } else if (push) {
                                ligarBombaFiltro(0);
                                onByKey = false;
                                push = false;
                            }
                        }
                    } else
                        acionada = false;

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {

                    }
                }
            }
        }.start();
    }

    public void ligarDreno(Integer acao) {
        switch (acao) {
            case 0:
                escreverPin(bomba, off);
                break;
            case 1:
                escreverPin(bomba, on);
                break;
        }
    }

    public void monitoraAgua() {
        new Thread() {
            @Override
            public void run() {
                Log.log(this, "Iniciando monitoraAgua", "DEBUG");
                boolean alerta = false;
                boolean maximo = false;

                while (true) {
                    if (lerPin(nivelBaixo).equals("1")) {
                        if (!alerta) {
                            alerta = true;

                        }

                    } else {
                        if (alerta) {

                            ligarDreno(1);

                        }
                        alerta = false;
                    }
                    if (lerPin(nivelAlto).equals("1")) {
                        if (!maximo) {
                            maximo = true;
                            ligarDreno(0);
                        }

                    } else {
                        maximo = false;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {

                    }
                }
            }
        }.start();
    }

    public void ligarBombaFiltro(Integer acao) {
        switch (acao) {
            case 0:
                escreverPin(filtro, off);
                break;
            case 1:
                escreverPin(filtro, on);
                break;
        }
    }

    public static void escreverPin(String pin, String valor) {

        try {
            BufferedWriter gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio" + pin + "/value", false));
            gpio.write(valor);
            gpio.close();
        } catch (Exception e) {

        }
    }

    public  String lerPin(String pin) {
        String ret = null;

        try {
            BufferedReader gpio = new BufferedReader(new FileReader("/sys/class/gpio/gpio" + pin + "/value"));
            ret = String.valueOf(String.format("%c", gpio.read()));
            gpio.close();
        } catch (Exception e) {

        }
        return ret;
    }

}
