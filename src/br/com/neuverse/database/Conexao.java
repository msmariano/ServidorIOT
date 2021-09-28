package br.com.neuverse.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

	protected Connection connection;

	Conexao() {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\msmar\\Desktop\\cfgServidor.db");

		} catch (SQLException e) {
		}
	}

}
