package br.com.neuverse.principal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import br.com.neuverse.entity.Conector;

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
		System.out.println("Versao 1.0.1 18/03/2021 20:00");
		
		
	
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
								Cliente cliente = new Cliente();
								mainServidor.clientes.add(cliente);
								cliente.setSocketCliente(socketCliente);
								cliente.setListaConectores(mainServidor.listaConectores);
								cliente.setClientes(mainServidor.clientes);
								cliente.run();
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
