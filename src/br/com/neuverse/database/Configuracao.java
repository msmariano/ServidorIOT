package br.com.neuverse.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Configuracao extends Conexao implements Dao<Configuracao>{
	
	
	public Configuracao() throws SQLException{
		super();
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE IF NOT EXISTS CONFIGURACAO ( ID  INTEGER PRIMARY KEY AUTOINCREMENT, PARAMETRO VARCHAR, C1 VARCHAR,C2 VARCHAR,C3 VARCHAR,C4 VARCHAR,C5 VARCHAR )");

	}
	
	public Integer retornaPortaServidor() {
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT C1 FROM CONFIGURACAO where parametro = 'endServidor'");
			if(rs.next()) {
				return Integer.parseInt(rs.getString("C1"));
			}
		} catch (SQLException e) {			
		}
		return null;
	}		

	@Override
	public List<Object> listar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object obter(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(Configuracao obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		
	}

	

}
