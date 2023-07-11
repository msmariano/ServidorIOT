package br.com.neuverse.entity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.neuverse.enumerador.ComEnum;
import br.com.neuverse.enumerador.Status;
import br.com.neuverse.principal.Log;

public class GpioRemoto extends Device implements Runnable {

    private Socket socket;
    public BufferedReader entrada;
    private Integer id;
    private Object toDoObject;
    private String nick;
    private Status funcao = Status.KEY ;
    private Status tecla = Status.HIGH;

    public Status getFuncao(){
        return funcao;
    }

    public Status getTecla(){
        return tecla;
    }

    public String getNick(){
        return nick;
    }

    public void setNick(String arg){
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
            if (socket.isConnected()) {
                Comando com = new Comando();
                com.setComando(ComEnum.LIGAR);
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                println(gson.toJson(com));
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public synchronized void println(String mens) {
        BufferedWriter saida;
        try {
            saida = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            saida.write(mens + "\r\n");
            saida.flush();
        } catch (IOException e) {

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
            if (socket.isConnected()) {
                Comando com = new Comando();
                com.setComando(ComEnum.DESLIGAR);
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                println(gson.toJson(com));
                return true;
            }
        } catch (Exception e) {
        }
        return false;

    }

    @Override
    public Status getStatus() {
       try {
            if (socket.isConnected()) {
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
        while (socket.isConnected()) {
            try {
                entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8.name()));
                while (true) {
                    String mens = entrada.readLine();
                    if (mens == null)
                        break;
                    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                    Comando com = gson.fromJson(mens, Comando.class);
                    ButtonIot bIot = (ButtonIot) toDoObject;
                    bIot.setStatus(com.getDevice());
                    atualizar();

                }

            } catch (Exception e) {
                try {
                    socket.close();
                } catch (IOException e1) {

                }
            }
        }
    }

}
