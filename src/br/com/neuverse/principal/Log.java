package br.com.neuverse.principal;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

	private static  Main main;
	
	public static void grava(String mensLog) {
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy HH:mm:ss");
		System.out.println(sdf.format(new Date())+" "+mensLog);

		


		//BufferedWriter bw;
		try {
			//bw = new BufferedWriter(new FileWriter("log.txt",true));
			//bw.write(mensLog+"\n");
			//bw.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	

	public static Main getMain() {
		return main;
	}



	public static void setMain(Main main) {
		Log.main = main;
	}



	public static void log(Object classe,String mensLog,String tipo) {
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy HH:mm:ss");
		
		//if(tipo.equals("DEBUG"))
			System.out.println("["+tipo+"] "+sdf.format(new Date())
				+"["+classe+"]:"+mensLog);

		for(Socket sckt :main.getLogSocket()){
			BufferedWriter saida;
			try {
				saida = new BufferedWriter(
					new OutputStreamWriter(sckt.getOutputStream()));
				saida.write("["+tipo+"] "+sdf.format(new Date())
					+"["+classe+"]:"+mensLog+"\r\n");
				saida.flush();
			} catch (IOException e) {				
			}
		}
		
		/*if(tipo.contains("SALVAR")){
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter("log.txt",true));
				bw.write(mensLog+"\n");
				bw.close();
				
			} catch (Exception e) {
			
			}
		}*/		
	}
}