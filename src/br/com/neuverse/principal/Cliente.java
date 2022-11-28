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
import java.time.LocalDateTime;
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
import br.com.neuverse.entity.ButtonIotDevice;
import br.com.neuverse.entity.Conector;
import br.com.neuverse.entity.Device;
import br.com.neuverse.entity.Mensagem;
import br.com.neuverse.enumerador.Status;
import br.com.neuverse.enumerador.TipoIOT;

public class Cliente implements Runnable {

	private Socket socketCliente;
	private List<Conector> listaConectores;
	private List<ButtonGpioRaspPi> listaGpioButtons;
	private List<Cliente> clientes;
	private String id;
	public BufferedReader entrada;
	private String nomeIotCliente;
	private String ipCliente;
	private Boolean isLogado = false;
	private Conector conectorCliente;
	private LocalDateTime timeAlive;
	private Boolean isAlive = false;
	final Object eventObj = new Object();

	public void processar(Conector con) throws IOException, SQLException {
		switch (con.getStatus()) {
			case ALIVE:
				processarAlive(con);
				break;
			case CONECTADO:
				isAlive = true;
				synchronized(eventObj) {
					eventObj.notifyAll();
				}
				break;
			case COMANDO:
				break;
			case PROCESSARBTN:
				processarBTN(con);
				break;
			case CONTROLLERCOMMAND:
				processarControllerCommand(con);
				break;
			case NOTIFICACAO:
				processarNotificar(con);
				break;
			case NOTIFICACAO_NETWORK:
				processarNotificarNetwork(con);
				break;
			case LOGIN:
				processarLogin(con);
				if(isLogado){
					isAlive = true;
					synchronized(eventObj) {
						eventObj.notifyAll();
					}
				}
				break;
			case LOGIN_OK:
				isLogado = true;
				break;
			case LOGINWITHCOMMAND:
				processarLoginWithCommand(con);
				break;
			default:
				break;
		}
	}

	public void processarNotificar(Conector con) {
		Log.log(this, "NOTIFICACAO ", "DEBUG");
		conectorCliente.setStatus(Status.NOTIFICACAO);
		for (ButtonIot b : con.getButtons()) {
			for (ButtonIot bOwn : conectorCliente.getButtons()) {
				if (bOwn.getButtonID().equals(b.getButtonID())) {
					bOwn.setStatus(b.getStatus());
				}
			}
		}
		Log.log(this, "atualizando status device", "DEBUG");
		if (clientes != null) {
			try {
				for (Cliente cliente : clientes) {
					if (cliente.getIsLogado()) {
						Log.log(cliente, "[" + cliente.getNickName() + "]enviando atualizacao de status", "DEBUG");
						cliente.enviarAtualizar(con);
					}
				}
			} catch (Exception e) {
				Log.log(this, "atualizar()" + e.getMessage(), "DEBUG");
			}
		}
	}

	public void processarNotificarNetwork(Conector con) {
		Log.log(this, "NOTIFICACAO NETWORK", "DEBUG");
		
		for(Conector ctr : listaConectores){
			if(ctr.getIdConector().equals(con.getIdConector())){
				for (ButtonIot b : con.getButtons()) {
					for (ButtonIot bOwn : ctr.getButtons()) {
						if (bOwn.getButtonID().equals(b.getButtonID())) {
							bOwn.setStatus(b.getStatus());
							break;
						}
					}
				}
				break;
			}
		}

		
		Log.log(this, "atualizando status device", "DEBUG");
		if (clientes != null) {
			try {
				for (Cliente cliente : clientes) {
					if (cliente.getIsLogado()) {
						Log.log(cliente, "[" + cliente.getNickName() + "]enviando atualizacao de status", "DEBUG");
						cliente.enviarAtualizarNetwork(con);
					}
				}
			} catch (Exception e) {
				Log.log(this, "atualizar()" + e.getMessage(), "DEBUG");
			}
		}
	}

	public void processarControllerCommand(Conector conector) {
		if (!isLogado) {
			try {
				if (!socketCliente.isClosed())
					socketCliente.close();
				return;
			} catch (IOException e) {
			}
		}
		for (Conector con : listaConectores) {
			if (conector.getIdConector().equals(con.getIdConector())) {
				List<ButtonIot> listaBiot = gerarBtns(con.getIot().getjSon());
				for (ButtonIot biot : listaBiot) {
					switch (con.getIot().getTipoIOT()) {
						case RASPBERRYGPIO:
							for (ButtonGpioRaspPi bgrp : listaGpioButtons) {
								if (biot.getButtonID() == bgrp.getId()) {
									if (biot.getStatus() == Status.ON) {
										bgrp.ligar();
									}
								} else {
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

	public boolean validarLogin(Conector con) {
		Usuario usuario;
		try {
			usuario = new Usuario();
			usuario.setSenha(convertPasswordToMD5(con.getSenha()));
			return usuario.retornaUsuario(con.getUsuario(), usuario.getSenha());
		} catch (Exception e) {

		}
		return false;
	}

	public void processarLoginWithCommand(Conector conector) {
		try {
			processarLogin(conector);
		} catch (SQLException e) {
		}
		if (isLogado) {
			processarControllerCommand(conector);
		}
	}

	public void processarAlive(Conector conector) {
		if (validarLogin(conector)) {
			new Thread() {
				@Override
				public void run() {
					try{
						sleep(30000);
						conector.setStatus(Status.CONECTADO);
						conectorCliente.setStatus(Status.CONECTADO);
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
						Mensagem mensagem = new Mensagem();
						mensagem.setMens(sdf.format(new Date()));
						conector.setMens(mensagem);
						Gson gson = new GsonBuilder()
							.setDateFormat("dd/MM/yyyy HH:mm:ss")
							.excludeFieldsWithoutExposeAnnotation()
							.create();
						String jSon = gson.toJson(conector);
						println(jSon);
						Log.log(this, "Alive:" + conector.getNome() + " " + conector.getMens().getMens(), "INFO");
						timeAlive = LocalDateTime.now();
					}
					catch(Exception e){
					}
				}
			}.start();
		} else {
			Log.log(this, "Desconectado login invalido Alive", "DEBUG");
			try {
				if (!socketCliente.isClosed())
					socketCliente.close();
			} catch (IOException e) {
			}
		}
	}

	public static String convertPasswordToMD5(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		BigInteger hash = new BigInteger(1, md.digest(password.getBytes()));
		return String.format("%32x", hash);
	}

	public Boolean processarLogin(Conector conector) throws SQLException {
		Log.log(this, "Processando login " + conector.getNome(), "DEBUG");
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
		if (isLogado) {
			Log.log(this, "login com sucesso! " + conector.getNome(), "DEBUG");
			if (!conector.getTipo().equals(TipoIOT.NETWORK)) {
				Log.log(this, "Removendo conector anterior se existir ", "DEBUG");
				for (Conector con : listaConectores) {
					if (con.getMac().equals(conector.getMac())) {
						listaConectores.remove(con);
						break;
					}
				}
			} else {
				//Remover conectores trazidos pela rede
				for (Conector conNet : conector.getConectores()) {
					for (Conector con : listaConectores) {
						if (conNet.getMac().equals(con.getMac())) {
							listaConectores.remove(con);
							break;
						}
					}
				}				
				listaConectores.addAll(conector.getConectores());
			}
			if (!conector.getTipo().equals(TipoIOT.NETWORK)) 
				listaConectores.add(conector);
			else{
				for (Conector con : listaConectores) {
					if (con.getTipo().equals(TipoIOT.NETWORK)) {
						try {
							con.getCliente().getSocketCliente().close();
						} catch (IOException e) {
						}
					}
				}	
			}

			conectorCliente = conector;
			conector.setStatus(Status.LOGIN_OK);
			this.nickName = conector.getNome();
			List<String> iots = new ArrayList<String>();
			for (Conector con : listaConectores) {
				if (con.getIot() != null)
					iots.add(con.getIot().getName());
			}
			conector.setIots(iots);
			UUID uniqueKey = UUID.randomUUID();
			conector.setIdConector(uniqueKey.toString());
			id = uniqueKey.toString();
			Log.log(this, "Cliente:" + nickName + " logado", "DEBUG");
			if (conector.getTipo() != null && !conector.getTipo().equals(TipoIOT.HUMAN)
					&& !conector.getTipo().equals(TipoIOT.NETWORK)) {
				conector.setDevices(new ArrayList<>());
				for (ButtonIot bIot : conector.getButtons()) {
					if (bIot.getNick() == null) {
						if (bIot.getNomeGpio() != null) {
							bIot.setNick(bIot.getNomeGpio());
						}
					}
					ButtonIotDevice bIotDevice = new ButtonIotDevice();
					bIotDevice.setSocket(socketCliente);
					bIotDevice.setbIot(bIot);
					bIotDevice.setClientes(clientes);
					bIotDevice.toDo(conector);
					conector.getDevices().add(bIotDevice);
				}
			} else if (conector.getTipo() != null && !conector.getTipo().equals(TipoIOT.HUMAN)) {
				for (Conector con : conector.getConectores()) {
					con.setDevices(new ArrayList<>());
					for (ButtonIot bIot : con.getButtons()) {
						if (bIot.getNick() == null) {
							if (bIot.getNomeGpio() != null) {
								bIot.setNick(bIot.getNomeGpio());
							}
						}
						ButtonIotDevice bIotDevice = new ButtonIotDevice();
						bIotDevice.setSocket(socketCliente);
						bIotDevice.setbIot(bIot);
						bIotDevice.setClientes(clientes);
						bIotDevice.toDo(con);
						con.getDevices().add(bIotDevice);
					}
				}
			}
		} else {
			conector.setStatus(Status.LOGIN_FAIL);
		}
		try {
			String jSon = gson.toJson(conector);
			println(jSon);
		} catch (Exception e) {
			Log.log(this, e.getMessage(), "DEBUG");
		}
		if (!isLogado) {
			try {
				if (!socketCliente.isClosed())
					socketCliente.close();
			} catch (IOException e) {
			}
		} else {
			timeAlive = LocalDateTime.now();
			if (conector.getTipo() != null && !conector.getTipo().equals(TipoIOT.HUMAN)
				&& !conector.getTipo().equals(TipoIOT.NETWORK)) {
				new Thread() {
					@Override
					public void run() {
						try {
							while (true) {
								Thread.sleep(1000 * 40);
								if (timeAlive.isBefore(LocalDateTime.now().minusSeconds(30))) {
									try {
										Log.log(this, "timeout alive", "DEBUG");
										if (!socketCliente.isClosed())
											socketCliente.close();
										break;
									} catch (IOException e) {
									}
								}
							}
						} catch (InterruptedException e) {
						}
					}
				}.start();
			}
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
				if (mens == null) {
					Log.log(this, "Mensagem vazia de " + nickName, "DEBUG");
					break;
				}
				Log.log(this, "Mens recv: " + mens, "DEBUG");
				Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss")
						.excludeFieldsWithoutExposeAnnotation()
						.create();
				try {
					if (!mens.equals("")) {
						Conector conector = gson.fromJson(mens, Conector.class);
						processar(conector);
						if (!isLogado)
							break;
					}
				} catch (Exception e) {
					Log.log(this, "mensagem invalida:" + e.getMessage(), "DEBUG");
					if (!socketCliente.isClosed())
						socketCliente.close();
					break;
				}
			}
		} catch (Exception e) {
			try {
				Log.log(this, "Exception_1:" + e.getMessage(), "ERROR");
				if (!socketCliente.isClosed())
					socketCliente.close();
			} catch (IOException e1) {
			}
		}
		if (this.getId() != null)
			Log.log(this, "Cliente " + nickName + " desconectando:" + this.getId(), "DEBUG");
		else
			Log.log(this, "Cliente desconectando!", "DEBUG");

		if(conectorCliente.getTipo().equals(TipoIOT.NETWORK)){
			//Remover conectores trazidos pela rede
			for (Conector conNet : conectorCliente.getConectores()) {
				for (Conector con : listaConectores) {
					if (conNet.getMac().equals(con.getMac())) {
						listaConectores.remove(con);
						break;
					}
				}
			}
		}

		if (conectorCliente != null) {
			Log.log(this, "Removendo conector:" + conectorCliente.getNome(), "INFO");
			listaConectores.remove(conectorCliente);
		}
		if(!conectorCliente.getTipo().equals(TipoIOT.NETWORK)){
			for (Conector con : listaConectores) {
				if (con.getTipo().equals(TipoIOT.NETWORK)) {
					try {
						con.getCliente().getSocketCliente().close();
					} catch (IOException e) {
					}
				}
			}	
		}		


		Log.log(this, "Cliente:" + nickName + " deslogado", "DEBUG");
		clientes.remove(this);
	}

	public synchronized void println(String mens) {
		BufferedWriter saida;
		try {
			saida = new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream()));
			saida.write(mens + "\r\n");
			saida.flush();
		} catch (IOException e) {
			Log.log(this, "enviar " + nickName + " " + e.getMessage(), "DEBUG");
		}
	}

	public synchronized void enviar(String mens) {
		BufferedWriter saida;
		try {
			saida = new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream()));
			saida.write(mens);
			saida.flush();
		} catch (IOException e) {
			Log.log(this, "enviar " + nickName + " " + e.getMessage(), "DEBUG");
		}
	}

	public void processarBTN(Conector plug) {
		try {
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.setDateFormat("dd/MM/yyyy HH:mm:ss")
				.create();
			for (Conector con : listaConectores) {
				if (con.getNome().equals(plug.getNome())) {
					int tDevice = 0;
					if (con.getDevices() == null)
						tDevice = 0;
					else
						tDevice = con.getDevices().size();
					Log.log(this, "comando encontrou " + plug.getNome() + " com " + tDevice + " devices", "INFO");
					for (ButtonIot buttonIot : plug.getButtons()) {
						if (buttonIot.getSelecionado() != null && buttonIot.getSelecionado()) {
							for (Device device : con.getDevices()) {
								if (device.getId().equals(buttonIot.getButtonID())) {
									Log.log(this, "Comando encontrou device: " + buttonIot.getNick(), "DEBUG");
									if (buttonIot.getStatus().equals(Status.ON)) {
										device.on();
									} else if (buttonIot.getStatus().equals(Status.OFF)) {
										device.off();
									} else if (buttonIot.getStatus().equals(Status.PUSH)) {
										if (device.getStatus().equals(Status.ON)) {
											device.off();
										} else
											device.on();
									}
									break;
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
		}
	}

	public synchronized void enviarAtualizar(Conector conEnviar) {
		if(conectorCliente!=null){
			if (conectorCliente.getTipo().equals(TipoIOT.HUMAN) || 
				conectorCliente.getTipo().equals(TipoIOT.NETWORK)){
				Log.log(this, "atualizando:" + nickName, "DEBUG");
				Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
						.setDateFormat("dd/MM/yyyy HH:mm:ss")
						.create();
				if(conectorCliente.getTipo().equals(TipoIOT.HUMAN))
					conEnviar.setStatus(Status.NOTIFICACAO);
				else if (conectorCliente.getTipo().equals(TipoIOT.NETWORK))
					conEnviar.setStatus(Status.NOTIFICACAO_NETWORK);
				String jSon = gson.toJson(conEnviar);
				BufferedWriter saida;
				try {
					saida = new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream()));
					saida.write(jSon + "\r\n");
					saida.flush();
				} catch (IOException e) {
					Log.log(this, "enviar " + nickName + " " + e.getMessage(), "DEBUG");
				}
			}
		}
	}

	public synchronized void enviarAtualizarNetwork(Conector conEnviar) {
		if(conectorCliente!=null){
			if (conectorCliente.getTipo().equals(TipoIOT.HUMAN)){
				Log.log(this, "atualizando:" + nickName, "DEBUG");
				Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
						.setDateFormat("dd/MM/yyyy HH:mm:ss")
						.create();
				conEnviar.setStatus(Status.NOTIFICACAO);
				String jSon = gson.toJson(conEnviar);
				BufferedWriter saida;
				try {
					saida = new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream()));
					saida.write(jSon + "\r\n");
					saida.flush();
				} catch (IOException e) {
					Log.log(this, "enviar " + nickName + " " + e.getMessage(), "DEBUG");
				}
			}
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
		Type listType = new TypeToken<ArrayList<ButtonIot>>() {
		}.getType();
		List<ButtonIot> listaBiot = gson.fromJson(jSon, listType);
		return listaBiot;
	}

	public Conector getConectorCliente() {
		return conectorCliente;
	}

	public void setConectorCliente(Conector conectorCliente) {
		this.conectorCliente = conectorCliente;
	}

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

	public void comando(List<Conector> lista) {

	}

	public Boolean getIsAlive() {
		return isAlive;
	}

	public void setIsAlive(Boolean isAlive) {
		this.isAlive = isAlive;
	}

	public Object getEventObj() {
		return eventObj;
	}

}
