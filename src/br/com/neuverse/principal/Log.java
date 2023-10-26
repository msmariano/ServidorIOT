package br.com.neuverse.principal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Log {
	private static  Main main;
	private static final Boolean bloquearProc = Boolean.FALSE;
	public static void grava(String mensLog) {
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy HH:mm:ss");
		System.out.println(sdf.format(new Date())+" "+mensLog);
		if(bloquearProc){
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter("log.txt",true));
				bw.write(mensLog+"\n");
				bw.close();
			} catch (Exception e) {
			}
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
			System.out.println("["+tipo+"] "+sdf.format(new Date())+"["+classe+"]:"+mensLog);


		if(tipo.equals("SALVARDIRETO")){
			BufferedWriter bw;
			try {

				File file = new File("/home/pi/Desktop/log.txt");
  				if (file.exists()) {
					double bytes = file.length();
					if(bytes > 10*1024000){
						file.delete();
					}
				}			

				bw = new BufferedWriter(new FileWriter("/home/pi/Desktop/log.txt",true));
				bw.write("["+tipo+"] "+sdf.format(new Date())+"["+classe+"]:"+mensLog+"\n");
				bw.close();

				
				
			} catch (Exception e) {
			}
		}

		if(main!=null&&main.getTerminais()!=null){
			for(Terminal ter :main.getTerminais()){
				BufferedWriter saida;
				String classeFiltrada=null;
				boolean filtrar = false;
				try {				
					try{
						for(String str : ter.regras){
							if(str.contains("classe")){
								String s[] = str.split(";");
								classeFiltrada = s[1];
								break;
							}
						}	
						for(String str : ter.regras){
							if(str.contains("mens")){
								filtrar = true;
								String s[] = str.split(";");
								if(mensLog.contains(s[1])){
									filtrar = false;
									break;
								}
							}
						}											
					}
					catch(Exception e){
						System.out.println(e.getMessage());
					}
					
					if(ter.regras.contains("all") ||ter.regras.contains(tipo)){
						if(classeFiltrada!=null&&!classe.toString().contains(classeFiltrada))
							continue;
						if(filtrar)
							continue;
						saida = new BufferedWriter(
							new OutputStreamWriter(ter.getSocket().getOutputStream()));
						saida.write("["+tipo+"] "+sdf.format(new Date())
							+"["+classe+"]:"+mensLog+"\r\n");
						saida.flush();
					}
				} catch (IOException e) {				
					System.out.println(e.getMessage());
				}
			}
			
			if(bloquearProc&&tipo.contains("SALVAR")){
				BufferedWriter bw;
				try {
					bw = new BufferedWriter(new FileWriter("log.txt",true));
					bw.write(mensLog+"\n");
					bw.close();
					
				} catch (Exception e) {
				}
			}
		}		
	}
}