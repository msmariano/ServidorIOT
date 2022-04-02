package br.com.neuverse.principal;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import br.com.neuverse.database.Configuracao;
import br.com.neuverse.database.Usuario;
import br.com.neuverse.entity.ButtonGpioRaspPi;
import br.com.neuverse.entity.Conector;
import br.com.neuverse.entity.InfoServidor;
import br.com.neuverse.entity.Iot;
import br.com.neuverse.entity.ServidorRest;
import br.com.neuverse.entity.Versao;
import br.com.neuverse.enumerador.TipoIOT;

public class Main {

	private static ServerSocket servidor;
	private List<Conector> listaConectores = new ArrayList<>();
	private List<ButtonGpioRaspPi> listaGpioButtons = new ArrayList<>(); 
	private List<Cliente> clientes = new ArrayList<>();
	private Integer serverPortDefault = 27015;
	private String nomeServidorDefault = "ServidorNeuverse";
	private String nomeDeviceDefault = "ServidorNeuverseIOT";
	private InfoServidor infoServidor = new InfoServidor();

	public static void main(String[] args) throws IOException, SQLException {

		Main mainServidor = new Main();		
		Log.log(mainServidor,"Bem vindo ao Servidor IOT!","INFO");	
		Log.log(mainServidor,Versao.ver(),"INFO");
		mainServidor.carregarConfiguracoes();
		mainServidor.inicializar();		
		mainServidor.criarConectorServidor();
		mainServidor.processar();	
		
	}

	public void carregaGpioButtons(){

	}

	public void criarConectorServidor(){
		Conector conector = new Conector();
		conector.setId("");
		conector.setNome(nomeServidorDefault);
		conector.setTipo(TipoIOT.SERVIDOR);
		Iot iot = new Iot();
		iot.setTipoIOT(TipoIOT.SERVIDOR);
		iot.setName(nomeDeviceDefault);
		conector.setIot(iot);
		listaConectores.add(conector);
	}
	public void inicializar() throws IOException {
		ServidorRest servidorRest = new ServidorRest();
		infoServidor = new InfoServidor();
		infoServidor.setVersao(Versao.ver());
		infoServidor.setNomeServidor(nomeServidorDefault);
		infoServidor.setNomeComputador(InetAddress.getLocalHost().getHostName());
		infoServidor.setIp(InetAddress.getLocalHost().getHostAddress());
		infoServidor.setDataAtual(new Date());
		servidorRest.setInfoServidor(infoServidor);
		listaConectores = new ArrayList<>();
		servidorRest.setListaConectores(listaConectores);
		servidor = new ServerSocket(serverPortDefault);
		servidorRest.monitoraConectores(servidorRest);
	}

	@SuppressWarnings("unused") 
	public void carregarConfiguracoes() throws SQLException {		
		Usuario usuario = new Usuario();	
		Configuracao cfg = new Configuracao();
		Integer portaServer = cfg.retornaPortaServidor();
		Log.log(this, "Cfg porta servidor:"+portaServer, "INFO");
		if(portaServer!=null){
			serverPortDefault = portaServer;
		}
	}

	public void processar() throws IOException{
		
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket socketCliente;
						socketCliente = servidor.accept();
						Log.log(this,"Cliente conectado: " + socketCliente.getInetAddress().getHostAddress()
								+ " porta " + socketCliente.getPort(),"INFO");
						new Thread() {
							@Override
							public void run() {
								Log.log(this,"Entrando Thread ip:" + socketCliente.getInetAddress().getHostAddress()
										+ " porta " + socketCliente.getPort(),"INFO");
								Cliente cliente = new Cliente();
								cliente.setIpCliente(socketCliente.getInetAddress().getHostAddress() + " porta "
										+ socketCliente.getPort());
								clientes.add(cliente);
								cliente.setSocketCliente(socketCliente);
								cliente.setListaConectores(listaConectores);
								cliente.setListaGpioButtons(listaGpioButtons);
								cliente.setClientes(clientes);
								cliente.run();
								Log.log(this,"Saindo Thread ip:" + cliente.getIpCliente(),"INFO");
							}
						}.start();

					} catch (Exception e) {
					}
				}
			}
		}.start();
	}	
}
