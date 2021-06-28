package br.com.neuverse.principal;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	
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
	

}