package br.com.neuverse.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import br.com.neuverse.principal.Log;

public class Usuario extends Conexao implements Dao<Usuario>{
	
	public Usuario() throws SQLException{
		super();
		//Statement statement = connection.createStatement();
		//statement.execute("CREATE TABLE IF NOT EXISTS USUARIO ( ID  INTEGER PRIMARY KEY AUTOINCREMENT, USUARIO VARCHAR, SENHA VARCHAR,NOME VARCHAR )");

	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	private String usuario;
	private String senha;
	private String nome;
	private String token;
	private Boolean isLogado;
	
	public Boolean getIsLogado() {
		return isLogado;
	}

	public void setIsLogado(Boolean logado) {
		this.isLogado = logado;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

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

	public Boolean retornaUsuario(String user,String passwd){
		try {
			isLogado = false;
			Statement statement = connection.createStatement();
			String sql = "select * from usuario where usuario = '"+user+"' and senha = '"+passwd+"'";
			ResultSet rs = statement.executeQuery(sql);
			
			if(rs.next()) {
				this.setId(rs.getInt("id"));
				this.setNome(rs.getString("nome"));
				this.setSenha(passwd);
				this.setUsuario(user);
				isLogado = true;
				return true;
			}
		} catch (SQLException e) {
			Log.log (this,"Erro retornaUsuario"+e.getMessage(),"INFO");
					
		}
		return false;
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
	
	public Boolean obterPorUsuarioSenha() {
		
		PreparedStatement stmt;
		isLogado = false;
		
		try {
			stmt = connection.prepareStatement("select ID,nome from USUARIO WHERE USUARIO = ? AND SENHA = ?");
			stmt.setString(1,usuario);
			stmt.setString(2,senha);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				
				id = resultSet.getInt(1);
				nome =  resultSet.getString(2);
				isLogado = true;
				return true;
			}
		}
		catch (Exception e) {
		}
		return false;
		
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
