package br.com.neuverse.principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;
import java.math.BigInteger;

import com.google.gson.reflect.TypeToken;

import br.com.neuverse.database.Usuario;
import br.com.neuverse.entity.ButtonGpioRaspPi;
import br.com.neuverse.entity.ButtonIot;
import br.com.neuverse.entity.Conector;
import br.com.neuverse.entity.Mensagem;
import br.com.neuverse.enumerador.Status;
import br.com.neuverse.enumerador.TipoIOT;

public class Cliente implements Runnable {

	private Socket socketCliente;
	private List<Conector> listaConectores;
	private List<ButtonGpioRaspPi>	listaGpioButtons;
	private List<Cliente> clientes;
	private String id;
	public BufferedReader entrada;
	private String nomeIotCliente;
	private String ipCliente;
	private Boolean isLogado = false;
	public Boolean getIsLogado() {
		return isLogado;
	}

	public void setIsLogado(Boolean isLogado) {
		this.isLogado = isLogado;
	}

	private String nickName;

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getNomeIotCliente() {
		return nomeIotCliente;
	}

	public List<ButtonGpioRaspPi> getListaGpioButtons() {
		return listaGpioButtons;
	}

	public void setListaGpioButtons(List<ButtonGpioRaspPi> listaGpioButtons) {
		this.listaGpioButtons = listaGpioButtons;
	}

	public String getIpCliente() {
		return ipCliente;
	}

	public void setIpCliente(String ipCliente) {
		this.ipCliente = ipCliente;
	}

	public void setNomeIotCliente(String nomeIotCliente) {
		this.nomeIotCliente = nomeIotCliente;
	}

	public void processar(Conector con) throws IOException, SQLException{
		switch(con.getStatus()) {
			case ALIVE:
				processarAlive(con);
				break;
			case CONTROLLERCOMMAND:
				processarControllerCommand(con);
				break;
			case LOGIN:
				processarLogin(con);
				break;
			case LOGINWITHCOMMAND:
				processarLoginWithCommand(con);
				break;
			default:
				break;			
		}
	}
	
	public void processarControllerCommand(Conector conector) {
		if(!isLogado){
			try {
				socketCliente.close();
				return ;
			} catch (IOException e) {
			}
		}
		for (Conector con : listaConectores) {
			if (conector.getIdConector().equals(con.getIdConector())) {
				List<ButtonIot> listaBiot = gerarBtns(con.getIot().getjSon());								
				for(ButtonIot biot : listaBiot){
					switch (con.getIot().getTipoIOT()) {
						case RASPBERRYGPIO:							
								for(ButtonGpioRaspPi bgrp : listaGpioButtons){	
									if(biot.getButtonID() == bgrp.getId()){
										if(biot.getStatus() == Status.ON) {
											bgrp.ligar();									}
									}
									else {
										bgrp.desligar();
									}						
								}													
							break;
						case CONTROLEREMOTO:
							break;
						default:
							break;
					}
				}	
				break;
			}			
		}		
	}

	public boolean validarLogin(Conector con){
		Usuario usuario;
		try {
			usuario = new Usuario();
			return usuario.retornaUsuario(con.getUsuario(), con.getSenha());
		} catch (SQLException e) {
			
		}
		return false;
	}

	public void processarLoginWithCommand(Conector conector){
		try {
			processarLogin(conector);
		} catch (SQLException e) {
		}
		if(isLogado) {	
			processarControllerCommand(conector);		
		}		
	}

	public void processarAlive(Conector conector){
		if(validarLogin(conector)){
			conector.setStatus(Status.CONECTADO);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
			Mensagem mensagem = new Mensagem();
			mensagem.setMens(sdf.format(new Date()));
			conector.setMens(mensagem);
			Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
			String jSon = gson.toJson(conector);
			enviar(jSon + "\r\n");
			Log.log(this,"Alive:"+conector.getNome()+" "+conector.getMens().getMens(),"INFO");
		} else
			try {
				socketCliente.close();
			} catch (IOException e) {
			}
	}

	public static String convertPasswordToMD5(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");

		BigInteger hash = new BigInteger(1, md.digest(password.getBytes()));

		return String.format("%32x", hash);
	}

	public Boolean processarLogin(Conector conector) throws SQLException{		
		Gson gson = new GsonBuilder()
		.setDateFormat("dd/MM/yyyy HH:mm:ss")
		.excludeFieldsWithoutExposeAnnotation()
		.create();
		Usuario usuario = new Usuario();
		try {
			usuario.setSenha(convertPasswordToMD5(conector.getSenha()));
		} catch (NoSuchAlgorithmException e1) {			
		}
		usuario.setUsuario(conector.getUsuario());
		isLogado = usuario.obterPorUsuarioSenha();
		if(isLogado) {
			conector.setStatus(Status.LOGIN_OK);
			this.nickName = conector.getNome();
			List<String> iots = new ArrayList<String>();
			for (Conector con : listaConectores) {
				iots.add(con.getIot().getName());
			}
			conector.setIots(iots);
			UUID uniqueKey = UUID.randomUUID();
			conector.setIdConector(uniqueKey.toString());
			uniqueKey = UUID.randomUUID();
			conector.setIdConector(uniqueKey.toString());
			id = uniqueKey.toString();
			Log.log(this,"Inserindo "+nickName,"DEBUG");
			//if(conector.getTipo()!=null&&conector.getTipo().equals(TipoIOT.HUMAN)) {
				conector.setConectores(listaConectores);
			//}
			Log.log(this,"Cliente:"+nickName+" logado","DEBUG");
		}
		else {
			conector.setStatus(Status.LOGIN_FAIL);
		}		
		String jSon = gson.toJson(conector);
		enviar(jSon + "\r\n");
		if(!isLogado)
			try {
				socketCliente.close();
			} catch (IOException e) {
			}
		
		return isLogado;
	}

	@Override
	public void run() {
		try {
			entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream(),
				StandardCharsets.UTF_8.name()));
			while (true) {
				String mens = entrada.readLine();
				if (mens == null){
					Log.log(this,"Mensagem vazia de "+nickName,"DEBUG");
					break;
				}
				Log.grava(mens);
				Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss")
					.excludeFieldsWithoutExposeAnnotation()
					.create();
				try {
					if(!mens.equals("")) {
						Conector conector = gson.fromJson(mens, Conector.class);
						processar(conector);
						if(!isLogado)
							break;
					}
				} catch (Exception e) {
					Log.log(this,"mensagem invalida:"+e.getMessage(),"DEBUG");
					socketCliente.close();
					break;
				}
			}
		} catch (Exception e) {
			try {
				Log.log(this,"Exception_1:"+e.getMessage(),"ERROR");
				socketCliente.close();
			} catch (IOException e1) {
			}
		}		
		if(this.getId()!=null)
			Log.log(this,"Cliente "+nickName+" desconectando:"+this.getId(),"DEBUG");
		else
			Log.log(this,"Cliente desconectando!","DEBUG");
		for (Conector con : listaConectores) {
			if (con.getIdConector().equals(getId()) && !con.getTipo().equals(TipoIOT.SERVIDOR)) {
				Log.log(this,"Removendo conector:"+con.getNome(),"INFO");
				listaConectores.remove(con);
				break;
			}			
		}
		Log.log(this,"Cliente:"+nickName+" deslogado","DEBUG");
		clientes.remove(this);
	}

	public synchronized void enviar(String mens) {
		BufferedWriter saida;
		try {
			saida = new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream()));
			saida.write(mens);
			saida.flush();
		} catch (IOException e) {
			Log.log(this, "enviar "+nickName+" "+e.getMessage(), "DEBUG");
		}

	}

	public synchronized void enviarAtualizar() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        String jSon = gson.toJson(listaConectores);
		
		BufferedWriter saida;
		try {
			saida = new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream()));
			saida.write(jSon+"\r\n");
			saida.flush();
		} catch (IOException e) {
			Log.log(this, "enviar "+nickName+" "+e.getMessage(), "DEBUG");
		}

	}

	public Socket getSocketCliente() {
		return socketCliente;
	}

	public void setSocketCliente(Socket socketCliente) {
		this.socketCliente = socketCliente;
	}

	public List<Conector> getListaConectores() {
		return listaConectores;
	}

	public void setListaConectores(List<Conector> listaConectores) {
		this.listaConectores = listaConectores;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public List<ButtonIot> gerarBtns(String jSon) {
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		Type listType = new TypeToken<ArrayList<ButtonIot>>(){}.getType();
		List<ButtonIot> listaBiot = gson.fromJson(jSon, listType );
		return listaBiot;
	}

}
