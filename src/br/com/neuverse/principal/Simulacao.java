package br.com.neuverse.principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.neuverse.entity.InfoServidor;

public class Simulacao {
	
	public static void main(String[] args) throws IOException
	{
		InetAddress serverEnd = InetAddress.getByName("rasp4msmariano.dynv6.net");
        //InetAddress serverEnd = InetAddress.getByName("192.168.0.103");
        Socket socket = new Socket(serverEnd,27015);
        PrintWriter out = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream())), true);
        out.println("{\"status\":\"INFO_SERVIDOR\"}");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));


        String ret = in.readLine();
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        InfoServidor infoServidor = gson.fromJson(ret, InfoServidor.class);
        
        socket.close();
	}

}
