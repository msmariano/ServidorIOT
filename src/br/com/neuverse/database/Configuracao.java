package br.com.neuverse.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.neuverse.entity.Parametro;
import br.com.neuverse.entity.ServidorCfg;
import br.com.neuverse.principal.Log;


public class Configuracao extends Conexao implements Dao<Configuracao>{
	
	
	public Configuracao() throws SQLException{
		super();
		//Statement statement = connection.createStatement();
		//statement.execute("CREATE TABLE IF NOT EXISTS CONFIGURACAO ( ID  INTEGER PRIMARY KEY AUTOINCREMENT, PARAMETRO VARCHAR, C1 VARCHAR,C2 VARCHAR,C3 VARCHAR,C4 VARCHAR,C5 VARCHAR,C6 VARCHAR,C7 VARCHAR,C8 VARCHAR )");

	}

	public List<ServidorCfg> retornaServidores(){
		List<ServidorCfg> servidores = new ArrayList<>();
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM SERVIDORES");
			while(rs.next()) {
				ServidorCfg servidorCfg = new ServidorCfg();
				servidorCfg.setId(rs.getInt("id"));
				servidorCfg.setNome(rs.getString("Nome"));
				servidorCfg.setIp(rs.getString("ip"));
				servidorCfg.setPorta(rs.getInt("porta"));
				servidorCfg.setMacAdress(rs.getString("macAdress"));
				servidorCfg.setUsuario(rs.getString("usuario"));
				servidorCfg.setSenha(rs.getString("senha"));
				servidores.add(servidorCfg);
			}
			
		} catch (SQLException e) {
			Log.log(this,e.getMessage(),"ERROR");		
		}		

		return servidores;
	}

	public Boolean getControlePiscina(){
		Log.log(this,"Lendo configuração controle piscina.","DEBUG");
		try{
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT C1 FROM CONFIGURACAO where parametro = 'controlePiscina'");
			if(rs.next()) {
				String c1 = rs.getString("C1");
				if(c1.equals("true"))
					return true;				
			}
		}
		catch(Exception e){
			Log.log(this, "getControlePiscina:"+e.getMessage(), "DEBUG");
		}
		return false;
	}

	public Integer getTipoServidor(){
		try{
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT C1 FROM CONFIGURACAO where parametro = 'tipoServidor'");
			if(rs.next()) {
				return rs.getInt("C1");
			}
		}
		catch(Exception e){
		}
		return 1;
	}

	public String getNomeServidor(){
		try{
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT C1 FROM CONFIGURACAO where parametro = 'nomeServidor'");
			if(rs.next()) {
				return rs.getString("C1");
			}
		}
		catch(Exception e){
		}
		return "Sem Nome";
	}

	public List<Parametro> retornaBtnGpio() {

		List<Parametro> parametros = new ArrayList<>();
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT C1,C2,C3,C4,C5,C6,C7,C8,C9,C10 FROM CONFIGURACAO where parametro = 'btnGpio'");
			while(rs.next()) {

				Parametro parametro = new Parametro();
				parametro.setC1(rs.getInt("C1"));
				parametro.setC2(rs.getInt("C2"));
				parametro.setC3(rs.getInt("C3"));
				parametro.setC4(rs.getInt("C4"));
				parametro.setC5(rs.getInt("C5"));
				parametro.setC6(rs.getInt("C6"));
				parametro.setC7(rs.getInt("C7"));
				parametro.setC8(rs.getString("C8"));
				parametro.setC9(rs.getString("C9"));
				parametro.setC10(rs.getInt("C10"));
				parametros.add(parametro);
			}
			
		} catch (SQLException e) {
			Log.log(this,e.getMessage(),"ERROR");		
		}		
		return parametros;
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
	public Integer retornaPortaSSLRest(){
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT C1 FROM CONFIGURACAO where parametro = 'portaSSLRest'");
			if(rs.next()) {
				return Integer.parseInt(rs.getString("C1"));
			}
		} catch (SQLException e) {			
		}
		return null;

	}	

	public Integer retornaPortaRest(){
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT C1 FROM CONFIGURACAO where parametro = 'portaRest'");
			if(rs.next()) {
				return Integer.parseInt(rs.getString("C1"));
			}
		} catch (SQLException e) {			
		}
		return null;
		
	}	

	@Override
	public List<Object> listar() {
		
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
