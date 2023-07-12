package br.com.neuverse.principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;


public class Sensor {
    private String gpio;
    private String nome;
    public String getNome() {
        return nome;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }

    final static private String on = "1";
    final static private String off = "0";

    public Sensor(String gp,String direcao,String n){
        gpio = gp;
        nome = n;
        ativarPin(gpio,direcao);
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
                gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/export", false));
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

     public void ligar(Integer acao) {
        switch (acao) {
            case 0:
                escreverPin(gpio, off);              
                break;
            case 1:
                escreverPin(gpio, on);                
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

    public String getGpio() {
        return gpio;
    }

    public void setGpio(String gpio) {
        this.gpio = gpio;
    }

}
