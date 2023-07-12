package br.com.neuverse.entity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.neuverse.enumerador.ComEnum;
import br.com.neuverse.enumerador.Status;
import br.com.neuverse.principal.Log;

public class GpioRemoto extends Device implements Runnable {

    private Socket socket;
    public BufferedReader entrada;
    private Conector con;
    public Conector getCon() {
        return con;
    }

    public void setCon(Conector con) {
        this.con = con;
    }

    private Integer id;
    private Object toDoObject;
    private String nick;
    private Status funcao = Status.KEY;
    private Status tecla = Status.HIGH;
    private List<ButtonIot> buttonsIots;
    private List<Device> devices;
    private boolean conectado = true;

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public List<ButtonIot> getButtonsIots() {
        return buttonsIots;
    }

    public void setButtonsIots(List<ButtonIot> buttonsIots) {
        this.buttonsIots = buttonsIots;
    }

    public Status getFuncao() {
        return funcao;
    }

    public Status getTecla() {
        return tecla;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String arg) {
        nick = arg;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public boolean on() {
        try {
            Log.log(this,"GpioRemoto send ON "+id,"DEBUG");
           // if (conectado) {
                Comando com = new Comando();
                com.setComando(ComEnum.LIGAR);
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                println(gson.toJson(com));
                return true;
            //}
        } catch (Exception e) {
                Log.log(this,"GpioRemoto send ON "+e.getMessage(),"DEBUG");
        }
        return false;
    }
    //synchronized
    public  void println(String mens) {
        
       // BufferedWriter saida;
        try {
             Log.log(this,"println mens "+nick+" "+mens,"DEBUG");
            PrintWriter out = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream())));

                                out.println(mens);
                                out.flush();


           
            /*saida = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            saida.write(mens + "\r\n");
            saida.flush();*/
        } catch (IOException e) {
            Log.log(this,"println "+e.getMessage(),"DEBUG");
        }
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean off() {
        try {
            Log.log(this,"GpioRemoto send OFF "+id,"DEBUG");
            //if (conectado) {
                Comando com = new Comando();
                com.setComando(ComEnum.DESLIGAR);
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                println(gson.toJson(com));
                return true;
            //}
        } catch (Exception e) {
            Log.log(this,"GpioRemoto send OFF "+e.getMessage(),"DEBUG");
        }
        return false;

    }

    @Override
    public Status getStatus() {
        try {
            if (conectado) {
                Comando com = new Comando();
                com.setComando(ComEnum.LERSENSORES);
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                println(gson.toJson(com));
                return com.getDevice();
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public void toDo(Object obj) {
        toDoObject = obj;
    }

    @Override
    public void run() {
       
        while (conectado) {
            try {

                entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8.name()));
                Log.log(this,"Iniciando gpioremoto "+nick,"DEBUG");
                while (true) {
                    String mens = entrada.readLine();
                    Log.log(this,"mens gpio remoto:"+mens,"DEBUG");
                    if (mens == null) {
                        conectado = false;
                        socket.close();
                        break;
                    }
                    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                    Comando com = gson.fromJson(mens, Comando.class);
                    ButtonIot bIot = (ButtonIot) toDoObject;
                    bIot.setStatus(com.getDevice());                    
                    try {
                        new Thread() {
                            @Override
                            public void run() {
                                atualizar();
                            }
                        }.start();
                    }
                    catch(Exception e){
                        
                    }
                }

            } catch (Exception e) {
                conectado = false;
                try {
                    socket.close();
                } catch (IOException e1) {

                }
            }
        }
        Log.log(this,"Removendo GpioRemoto listas","DEBUG");
        devices.remove(this);
        con.getDevices().remove(this);
        buttonsIots.remove((ButtonIot) toDoObject);
    }

}
