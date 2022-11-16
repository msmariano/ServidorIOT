package br.com.neuverse.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import br.com.neuverse.principal.Log;

public class Conexao {

	 static protected Connection connection;

	Conexao() {
		try {
			
			//connection = DriverManager.getConnection("jdbc:sqlite:cfgServidor.db");
			if(connection  == null) {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection("jdbc:sqlite:/home/pi/Desktop/cfgServidor.db");
				//connection = DriverManager.getConnection("jdbc:postgresql://192.168.10.254:5432/ServidorIOT","msmarino","mar0403");
				Log.log(this,"Iniciado conexao sqlite","DEBUG");
			}

		} catch (Exception e) {
			Log.log(this,"erro:" + e.getMessage(),"DEBUG");
			/*
			try{
				
				if(connection  == null) {
					connection = DriverManager.getConnection("jdbc:sqlite:/home/pi/Desktop/cfgServidor.db");
					Log.log(this,"Iniciando conexao sqlite","DEBUG");
				}

			}
			catch(Exception e1){
				Log.log(this,"erro1:" + e.getMessage(),"DEBUG");
			}*/
		}
	}

}
