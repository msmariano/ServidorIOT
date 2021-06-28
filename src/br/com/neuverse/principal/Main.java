package br.com.neuverse.principal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.neuverse.entity.Conector;
import br.com.neuverse.enumerador.Status;

public class Main {
	
	private static ServerSocket servidor;
	private List<Conector> listaConectores;
	private List<Cliente> clientes;
	final Integer serverPort = 27015;
	
	public static void main(String[] args) throws IOException {
		Main mainServidor = new Main();
		Log.grava("Bem vindo ao Servidor IOT!");
		servidor  = new ServerSocket(mainServidor.serverPort);
		Log.grava(String.valueOf(mainServidor.serverPort));
		mainServidor.listaConectores = new ArrayList<>();
		mainServidor.clientes = new ArrayList<>();
		System.out.println("Versao 1.0.4 24/06/2021 06:35");
		
		//Conector con = new Conector();
		//con.setStatus(Status.INFO_SERVIDOR);
		//Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		//String rt  = gson.toJson(con);
		
		//System.out.println(rt);
		
		
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket socketCliente;
						socketCliente = servidor.accept();
						Log.grava("Cliente conectado: " + socketCliente.getInetAddress().getHostAddress() + " porta "
								+ socketCliente.getPort());
						new Thread() {
							@Override
							public void run() {
								Log.grava("Entrando Thread ip:"+socketCliente.getInetAddress().getHostAddress() + " porta "
								+ socketCliente.getPort());
								Cliente cliente = new Cliente();
								cliente.setIpCliente(socketCliente.getInetAddress().getHostAddress() + " porta "
								+ socketCliente.getPort());
								mainServidor.clientes.add(cliente);
								cliente.setSocketCliente(socketCliente);
								cliente.setListaConectores(mainServidor.listaConectores);
								cliente.setClientes(mainServidor.clientes);
								cliente.run();
								Log.grava("Saindo Thread ip:"+cliente.getIpCliente());
							}
						}.start();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
}

