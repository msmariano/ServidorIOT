package br.com.neuverse.principal;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.neuverse.database.Configuracao;
import br.com.neuverse.database.Usuario;
import br.com.neuverse.entity.ButtonGpioRaspPi;
import br.com.neuverse.entity.ButtonIot;
import br.com.neuverse.entity.Conector;
import br.com.neuverse.entity.InfoServidor;
import br.com.neuverse.entity.Iot;
import br.com.neuverse.entity.ServidorRest;
import br.com.neuverse.entity.Versao;
import br.com.neuverse.enumerador.Status;
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
	private Conector conector;

	public static void main(String[] args) throws IOException, SQLException {

		Main mainServidor = new Main();		
		Log.log(mainServidor,"Bem vindo ao Servidor IOT!","INFO");	
		Log.log(mainServidor,Versao.ver(),"INFO");
		mainServidor.carregarConfiguracoes();
		mainServidor.inicializar();		
		mainServidor.criarConectorServidor();
		mainServidor.carregaGpioButtons();
		mainServidor.processar();	
		//BigInteger n = new BigInteger("20");
		
		
	}

	public void carregaGpioButtons(){
		try{
			Configuracao cfg = new Configuracao();
			List<Integer[][]> listaBtnGpio = cfg.retornaBtnGpio();
			List<ButtonIot> buttons = new ArrayList<>();
			for(Integer[][] btnGpio : listaBtnGpio) {
				ButtonGpioRaspPi bgrpi = new ButtonGpioRaspPi(btnGpio[0][0],btnGpio[0][1],btnGpio[0][2]);
				bgrpi.setId(btnGpio[0][3]);
				listaGpioButtons.add(bgrpi);
				ButtonIot bIot = new ButtonIot();
				bIot.setButtonID(btnGpio[0][3]);				
				bIot.setFuncao(Status.getEnum(btnGpio[0][4]));
				bIot.setTecla(Status.getEnum(btnGpio[0][5]));
				bIot.setStatus(Status.getEnum(btnGpio[0][6]));
				bIot.setNomeGpio("CtrlGpioServidor");
				buttons.add(bIot);
			}
			Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
			String jSon = gson.toJson(buttons);
			conector.getIot().setjSon(jSon);

		}
		catch(Exception e) {

		}
	}

	public void criarConectorServidor(){
		conector = new Conector();
		conector.setNome(nomeServidorDefault);
		UUID uniqueKey = UUID.randomUUID();
        String id = uniqueKey.toString();
		conector.setId(id);
		conector.setTipo(TipoIOT.SERVIDOR);
		try {			
			conector.setIp(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			
		}
		Iot iot = new Iot();
		iot.setTipoIOT(TipoIOT.RASPBERRYGPIO);
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
