package br.com.neuverse.principal;

import java.awt.Button;
import java.io.BufferedReader;
import java.io.BufferedWriter;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
//import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import br.com.neuverse.database.Usuario;
import br.com.neuverse.entity.ButtonGpioRaspPi;
import br.com.neuverse.entity.ButtonIot;
import br.com.neuverse.entity.ComandoIOT;
//import java.lang.reflect.Type;
//import com.google.gson.reflect.TypeToken;
//import br.com.neuverse.entity.ButtonIot;
import br.com.neuverse.entity.Conector;
import br.com.neuverse.entity.InfoServidor;
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
			case LISTA_IOT:
			break;
			case ACIONARBOTAO:
				break;
			case ALEATORIOINFO:
				break;
			case ALEATORIOOFF:
				break;
			case ALEATORIOON:
				break;
			case ALIVE:
				if(isLogado)
					processarAlive(con);
				else
					socketCliente.close();
				break;
			case CONECTADO:
				break;
			case CONFIG:
				break;
			case CONTROLLERCOMMAND:
				break;
			case ERRO:
				break;
			case FAIL:
				break;
			case GETVALUE:
				break;
			case HIGH:
				break;
			case HOLD:
				break;
			case IN:
				break;
			case INFO_SERVIDOR:
				break;
			case INTERRUPTOR:
				break;
			case JSON:
				break;
			case LOGIN:
				processarLogin(con);
				if(!isLogado)
					socketCliente.close();
				break;
			case LOGINWITHCOMMAND:
				processarLoginWithCommand(con);
				break;
			case LOGIN_FAIL:
				break;
			case LOGIN_OK:
				break;
			case LOW:
				break;
			case NA:
				break;
			case OFF:
				break;
			case ON:
				break;
			case OUT:
				break;
			case PUSH:
				break;
			case READ:
				break;
			case RETORNO:
				break;
			case RETORNOTRANSITORIO:
				break;
			case SUCESSO:
				break;
			case VOID:
				break;
			default:
				break;			
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
		for (Conector con : listaConectores) {
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

	public Boolean processarLogin(Conector conector) throws SQLException{		
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		Usuario usuario = new Usuario();
		usuario.setSenha(conector.getSenha());
		usuario.setUsuario(conector.getUsuario());
		usuario.obterPorUsuarioSenha();
		if(usuario.getId()!=null) {
			conector.setStatus(Status.LOGIN_OK);
			List<String> iots = new ArrayList<String>();
			for (Conector con : listaConectores) {
				iots.add(con.getIot().getName());
			}
			conector.setIots(iots);
			UUID uniqueKey = UUID.randomUUID();
			conector.setId(uniqueKey.toString());
			uniqueKey = UUID.randomUUID();
			conector.setId(uniqueKey.toString());
			id = uniqueKey.toString();
			Log.grava("Inserindo["+conector.getNome()+"] id: "+conector.getId());
			isLogado = true;
		}
		else {
			conector.setStatus(Status.LOGIN_FAIL);
		}		
		String jSon = gson.toJson(conector);
		enviar(jSon + "\r\n");
		return isLogado;
	}

	@Override
	public void run() {
		try {
			entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			while (true) {
				String mens = entrada.readLine();
				if (mens == null){
					Log.log(this,"Mensagem vazia","INFO");
					break;
				}
				Log.grava(mens);
				Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
				try {
					if(!mens.equals("")) {
						Conector conector = gson.fromJson(mens, Conector.class);
						processar(conector);
					}
				} catch (Exception e) {
					Log.log(this,"mensagem inválida:"+e.getMessage(),"ERROR");
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
		Log.log(this,"Cliente desconectando:"+this.getId(),"INFO");
		for (Conector con : listaConectores) {
			if (con.getId().equals(getId()) && !con.getTipo().equals(TipoIOT.SERVIDOR)) {
				Log.log(this,"Removendo conector:"+con.getNome(),"INFO");
				listaConectores.remove(con);
				break;
			}			
		}
		clientes.remove(this);
	}

	public synchronized void enviar(String mens) {
		BufferedWriter saida;
		try {
			saida = new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream()));
			saida.write(mens);
			saida.flush();
		} catch (IOException e) {
			Log.grava(e.getMessage());
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

}
