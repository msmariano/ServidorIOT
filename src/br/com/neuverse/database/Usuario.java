package br.com.neuverse.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Usuario extends Conexao implements Dao<Usuario>{
	
	public Usuario() throws SQLException{
		super();
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE IF NOT EXISTS USUARIO ( ID  INTEGER PRIMARY KEY AUTOINCREMENT, USUARIO VARCHAR, SENHA VARCHAR )");

	}
	
	private String usuario;
	private String senha;
	private Integer id;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
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
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		
	}
	
	public void obterPorUsuarioSenha() {
		
		PreparedStatement stmt;
		
		try {
			stmt = connection.prepareStatement("select ID from USUARIO WHERE USUARIO = ? AND SENHA = ?");
			stmt.setString(1,usuario);
			stmt.setString(2,senha);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				
				id = resultSet.getInt(1);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	@Override
	public void save() {
		PreparedStatement stmt;
		try {
			stmt = connection.prepareStatement("select ID from USUARIO WHERE USUARIO = ? AND SENHA = ?");
			stmt.setString(1,usuario);
			stmt.setString(2,senha);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				
				id = resultSet.getInt(1);
				
				Statement sta = connection.createStatement();
				sta.execute("UPDATE USUARIO SET SENHA = '"+senha +"', usuario = '"+usuario+"' WHERE ID =  "+id);
			}
			else
			{
				Statement sta = connection.createStatement();
				sta.execute("INSERT INTO USUARIO(USUARIO,SENHA)VALUES('"+usuario+"','"+senha+"')");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void save(Usuario obj) {
		// TODO Auto-generated method stub
		
	}

}
