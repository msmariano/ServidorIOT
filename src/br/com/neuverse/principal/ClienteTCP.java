package br.com.neuverse.principal;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.neuverse.entity.ConfigIOT;

public class ClienteTCP {

    public void iniciar(){//String endereco, int porta) {
        BufferedWriter saida;
        Socket conexao;
        try {

            ConfigIOT configIOT = new ConfigIOT();
            configIOT.setAcao("configurar");
            configIOT.getSsidSessao().setPassword("80818283");
            configIOT.getSsidSessao().setSsid("Escritorio");
            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
            String json = gson.toJson(configIOT);


            conexao = new Socket("192.168.4.1", 8080);
            saida = new BufferedWriter(new OutputStreamWriter(conexao.getOutputStream()));
            
            saida.write("{\"acao\":\"configurar\",\"ssid\":\"Escritorio\",\"passwd\":\"80818283\"}\r\n");
            saida.flush();
            saida.close();            
            conexao.close();

        } catch (Exception e) {
            System.err.println("erro: " + e.toString());
        }

    }

    public static void main(String[] args) {
       
        ClienteTCP c = new ClienteTCP();
        c.iniciar();//args[0], Integer.parseInt(args[1]));
    }
    
}
