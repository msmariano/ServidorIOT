package br.com.neuverse.principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.neuverse.entity.Comando;
import br.com.neuverse.enumerador.Status;

public class ControlePiscina {

    private String horarios = "00:00;04:00;08:00;12:00;16:00;20:00";
    private Integer minutos = 30;
    final static private String bomba = "10";
    final static private String filtro = "17";
    final static private String chave = "9";
    final static private String nivelAlto = "20";
    final static private String nivelBaixo = "21";
    final static private String indTimer = "8";
    final static private String on = "1";
    final static private String off = "0";
    static private boolean onByKey = false;
    private boolean desligarTimer = false;
    private String linhasDisplay [];
   
    public String[] getLinhasDisplay() {
        return linhasDisplay;
    }

    public void setLinhasDisplay(String linhasDisplayArg []){
        linhasDisplay = linhasDisplayArg;
    }
  

    private Comando comando = new Comando();

    public Comando getComando() {
        return comando;
    }

    public void setComando(Comando arg) {
        comando = arg;
    }

    public Status retornoStatusFiltro() {
        if (lerPin(filtro).equals("1")) {
            return Status.ON;
        }
        return Status.OFF;
    }

    public void inicializar() {

        ativarPin(nivelAlto, "in");
        ativarPin(indTimer, "out");
        ativarPin(nivelBaixo, "in");
        ativarPin(chave, "in");
        ativarPin(bomba, "out");
        ativarPin(filtro, "out");
        escreverPin(bomba, on);
        escreverPin(filtro, off);
        escreverPin(indTimer, off);
        monitoraAgua();
        ligarBombaFiltro(0);
        monitoraChave();
        timer(null, null);
        //new ScktCom("Filtro", false, "17", "out").inicializar();
        //new ScktCom("Dreno", true, "10", "out").inicializar();

    }

    public void timer(String hor, Integer min) {

        if (hor != null) {
            horarios = hor;
        }
        if (min != null) {
            minutos = min;
        }
        Log.log(this, "Timer Ativado para os horários:" + horarios + " por " + minutos + " minuto(s)", "DEBUG");

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
                    Log.log(this, "Timer iniciado as " + sdf.format(new Date()), "DEBUG");
                    comando.setTimer(Status.OFF);
                    escreverPin(indTimer, on);
                    getLinhasDisplay()[2] = "Timer UP!";
                    while (true) {
                        String t = sdf.format(new Date());
                        Thread.sleep(1000);
                        if (!desligarTimer) {
                            if (horarios.contains(t)) {
                                Log.log(this, "Ativando Filtro Bomba pelo timer.", "DEBUG");
                                getLinhasDisplay()[0] = "Filtro Timer ON!";
                                comando.setTimer(Status.ON);
                                ligarBombaFiltro(1);
                                Thread.sleep(1000 * 60 * minutos);
                                comando.setTimer(Status.OFF);
                                if (!onByKey) {
                                    ligarBombaFiltro(0);
                                    getLinhasDisplay()[0] = "Filtro Timer OFF!";
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.log(this, "Timer " + e.getMessage(), "ERROR");
                }
            }
        }.start();
    }

    public void atualizaInfo() {

        comando.setStFiltro(Status.OFF);
        comando.setMens("Filtro desligado");
        if (lerPin(filtro).equals("1")) {
            comando.setStFiltro(Status.ON);
            comando.setMens("Filtro ligado");
        }

        comando.setStDreno(Status.OFF);
        if (lerPin(bomba).equals("0")) {
            comando.setStDreno(Status.ON);
            comando.setMens(comando.getMens() + " Dreno ligado");
        }

    }

    public void monitoraChave() {
        new Thread() {
            @Override
            public void run() {
                boolean acionada = false;
                boolean bEspera = false;
                boolean push = false;
                Log.log(this, "Iniciando monitoraChave", "DEBUG");
                while (true) {
                    int count = 0;
                    if (lerPin(chave).equals("0")) {
                        while (lerPin(chave).equals("0")) {
                            count++;
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                            }
                            if (count > 49) {
                                desligarTimer = !desligarTimer;
                                if (desligarTimer) {
                                    escreverPin(indTimer, off);
                                    getLinhasDisplay()[2] = "Timer DOWN!";
                                } else{
                                    escreverPin(indTimer, on);
                                    getLinhasDisplay()[2] = "Timer UP!";
                                }

                                 bEspera =  true;
                                break;
                            }
                        }
                        if(bEspera){
                            bEspera = false;
                            try {
                                Thread.sleep(2000);
                            } catch (Exception e) {
                            }
                            continue;
                        }

                        if (!acionada) {
                            acionada = true;
                            if (!push) {
                                ligarBombaFiltro(1);
                                getLinhasDisplay()[0] = "Filtro Manual ON!";
                                onByKey = true;
                                push = true;
                            } else if (push) {
                                ligarBombaFiltro(0);
                                getLinhasDisplay()[0] = "Filtro Manual OFF!";
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
                getLinhasDisplay()[2] = "Dreno ligado";
                break;
            case 1:
                escreverPin(bomba, on);
                getLinhasDisplay()[2] = "";
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
                            getLinhasDisplay()[1] = "Presença de agua!";
                            comando.setNivel(Status.PRESENCA_AGUA);
                            alerta = true;
                        }
                    } else {
                        comando.setNivel(Status.NORMAL_AGUA);
                        getLinhasDisplay()[1] = "Agua normal!";
                        if (alerta) {
                            ligarDreno(1);
                        }
                        alerta = false;
                    }
                    if (lerPin(nivelAlto).equals("1")) {

                        if (!maximo) {
                            comando.setNivel(Status.ALERTA_AGUA);
                            maximo = true;
                            ligarDreno(0);
                            
                        }

                    } else {
                        if (comando.getNivel().equals(Status.ALERTA_AGUA))
                            comando.setNivel(Status.PRESENCA_AGUA);
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

    public String lerPin(String pin) {
        String ret = null;

        try {
            BufferedReader gpio = new BufferedReader(new FileReader("/sys/class/gpio/gpio" + pin + "/value"));
            ret = String.valueOf(String.format("%c", gpio.read()));
            gpio.close();
        } catch (Exception e) {

        }
        return ret;
    }

    public void ativarPin(String pin, String direcao) {

        try {
            BufferedWriter gpio = null;
            try {
                gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/export", true));
                gpio.write(pin);
                gpio.close();
            } catch (Exception e) {
            }

            System.out.println("Ativando pin " + pin + " para " + direcao);
            gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio" + pin + "/direction", false));
            gpio.write(direcao);
            gpio.close();
        } catch (Exception e) {

        }

    }

}
