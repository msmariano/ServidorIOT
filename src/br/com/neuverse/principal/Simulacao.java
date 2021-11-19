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
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.neuverse.entity.InfoServidor;

public class Simulacao {
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		//InetAddress serverEnd = InetAddress.getByName("rasp4msmariano.dynv6.net");
        InetAddress serverEnd = InetAddress.getByName("192.168.0.254");
		String comando = "";
		if (args.length >0) {
			System.out.println(args[0]);
			comando = args[0];
		}
		
        if(comando.equals("teste")) {
	        while(true) {
	        	
	        	Socket socket = new Socket(serverEnd,27020);
	            PrintWriter out = new PrintWriter(
	                 new BufferedWriter(new OutputStreamWriter(
	                             socket.getOutputStream())), true);
	        	out.println("{\"status\":\"INFO_SERVIDOR\"}");
		        BufferedReader in = new BufferedReader(
		                new InputStreamReader(socket.getInputStream()));
		
		
		        String ret = in.readLine();
		        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		        InfoServidor infoServidor = gson.fromJson(ret, InfoServidor.class);
		        System.out.println(infoServidor.getDataAtual());
	       
	        	Thread.sleep(5000);
	        	socket.close();
	        	
	        }
        }
        else if (comando.equals("listarIots")) {
        	Socket socket = new Socket("192.168.0.254",27020);
            PrintWriter out = new PrintWriter(
                 new BufferedWriter(new OutputStreamWriter(
                             socket.getOutputStream())), true);
        	out.println("{\"status\":\"LISTA_IOT\"}");
	        BufferedReader in = new BufferedReader(
	                new InputStreamReader(socket.getInputStream()));
	
	
	        String ret = in.readLine();
	        socket.close();
	        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
	        @SuppressWarnings("unchecked")
			List<String> lista = gson.fromJson(ret, List.class);
	        System.out.println();
            ControleBotao ct = new ControleBotao();
            ct.setNome("Teste");
            ct.setNomeIot("Casa_prado_velho");//lista.get(0));
            ct.setSenha("M@r0403");
            ct.setUsuario("Matinhos");
            
            ct.testaBotoes();
            
           
        	
        }
        
	}
	

}
