package br.com.neuverse.principal;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.neuverse.database.Configuracao;
import br.com.neuverse.database.Usuario;
import br.com.neuverse.entity.ButtonGpioRaspPi;
import br.com.neuverse.entity.ButtonIot;
import br.com.neuverse.entity.Conector;
import br.com.neuverse.entity.InfoServidor;
import br.com.neuverse.entity.Iot;
import br.com.neuverse.entity.Parametro;
import br.com.neuverse.entity.ServidorCfg;
import br.com.neuverse.entity.ServidorRest;
import br.com.neuverse.entity.Versao;
import br.com.neuverse.entity.Device;
import br.com.neuverse.enumerador.Status;
import br.com.neuverse.enumerador.TipoIOT;

public class Main {

	private static ServerSocket servidor;
	private static ServerSocket servidorWithSSL;
	private static ServerSocket servidorLog;
	private List<Conector> listaConectores = new ArrayList<>();
	private List<ButtonGpioRaspPi> listaGpioButtons = new ArrayList<>(); 
	private List<Device> devices = new ArrayList<>();
	private List<Cliente> clientes = new ArrayList<>();
	private List<Socket> logSocket = new ArrayList<>();
	private Integer serverPortDefault = 27015;
	private Integer portaRest = 8081;
	private Integer portaSSLRest = 8080;
	private String nomeServidorDefault = "ServidorNeuverse";
	private String nomeDeviceDefault = "ServidorNeuverseIOT";
	private InfoServidor infoServidor = new InfoServidor();
	private Conector conector;
	private Versao ver = new Versao();
	private List<Terminal> terminais = new ArrayList<>();

	public List<Terminal> getTerminais() {
		return terminais;
	}

	public void setTerminais(List<Terminal> terminais) {
		this.terminais = terminais;
	}

	public static void main(String[] args) throws IOException, SQLException {

		/*try {
			String s = Cliente.convertPasswordToMD5("pradopi");
			System.out.println();
		} catch (NoSuchAlgorithmException e1) {
		}*/

		Main mainServidor = new Main();	
		Log.setMain(mainServidor);	
		Log.log(mainServidor,"Bem vindo ao Servidor IOT!","DEBUG");	
		Log.log(mainServidor,mainServidor.ver.ver(),"DEBUG");
		mainServidor.carregarConfiguracoes();
		mainServidor.inicializar();		
		mainServidor.criarConectorServidor();
		mainServidor.carregaGpioButtons();
		mainServidor.processar();	
		mainServidor.processarLogs();
		mainServidor.processarWithSSL();
		Configuracao cfg = new Configuracao();
		for(ServidorCfg servidor : cfg.retornaServidores()){
			mainServidor.linkServidorRedicionamento(servidor);
		}

		//BigInteger n = new BigInteger("20");		
	}

	public List<Socket> getLogSocket() {
		return logSocket;
	}

	public void setLogSocket(List<Socket> logSocket) {
		this.logSocket = logSocket;
	}

	public void carregaGpioButtons(){
		try{
			Configuracao cfg = new Configuracao();
			List<Parametro> listaBtnGpio = cfg.retornaBtnGpio();
			List<ButtonIot> buttons = new ArrayList<>();
			for(Parametro btnGpio : listaBtnGpio) {
				ButtonGpioRaspPi bgrpi = new ButtonGpioRaspPi(btnGpio.getC1(),btnGpio.getC2(),btnGpio.getC3(),btnGpio.getC4());
				listaGpioButtons.add(bgrpi);
				bgrpi.setConectores(listaConectores);
				bgrpi.setClientes(clientes);
				devices.add(bgrpi);
				ButtonIot bIot = new ButtonIot();
				bIot.setButtonID(btnGpio.getC4());				
				bIot.setFuncao(Status.getEnum(btnGpio.getC5()));
				bIot.setTecla(Status.getEnum(btnGpio.getC6()));
				bIot.setStatus(bgrpi.getStatus());
				bIot.setNomeGpio("CtrlGpioServidor");
				bIot.setNick(btnGpio.getC8());
				bgrpi.toDo(bIot);
				buttons.add(bIot);
				conector.getButtons().add(bIot);
			}
			Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
			String jSon = gson.toJson(buttons);
			conector.getIot().setjSon(jSon);
			conector.setDevices(devices);

		}
		catch(Exception e) {

		}
	}

	public static SSLContext buildSslContext(InputStream... inputStreams) throws Exception {
		X509Certificate cert;
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(null);

		for (InputStream inputStream : inputStreams) {
			try {
				CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
				cert = (X509Certificate)certificateFactory.generateCertificate(inputStream);
			} finally {
				inputStream.close();
			}
			String alias = cert.getSubjectX500Principal().getName();
			trustStore.setCertificateEntry(alias, cert);
		}

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
		tmf.init(trustStore);
		TrustManager[] trustManagers = tmf.getTrustManagers();
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, trustManagers, null);

		return sslContext;
	}

	public void linkServidorRedicionamento(ServidorCfg servidorCfg){
		new Thread() {
			Conector networkConector = new Conector();
			Cliente cliente = new Cliente();
			@Override
			public synchronized void start() {
				super.start();
				networkConector.setTipo(TipoIOT.NETWORK);
				networkConector.setStatus(Status.LOGIN);
				networkConector.setConectores(listaConectores);
				networkConector.setUsuario(servidorCfg.getUsuario());
				networkConector.setSenha(servidorCfg.getSenha());
				networkConector.setNome(servidorCfg.getNome());
				clientes.add(cliente);
				cliente.setListaConectores(listaConectores);
				cliente.setListaGpioButtons(listaGpioButtons);
				cliente.setClientes(clientes);
			}	
			public void alive(){
				new Thread() {
					@Override
					public void run() {
						while(cliente.getSocketCliente().isConnected()){
							cliente.setIsAlive(false);
							Conector conectorAlive = new Conector();
							conectorAlive.setStatus(Status.ALIVE);
							conectorAlive.setUsuario(servidorCfg.getUsuario());
							conectorAlive.setSenha(servidorCfg.getSenha());
							Gson gson = new GsonBuilder()
								.setDateFormat("dd/MM/yyyy HH:mm:ss")
								.excludeFieldsWithoutExposeAnnotation()
								.create();
                        	String textJson = gson.toJson(conectorAlive);
							cliente.println(textJson);
							try {
								Thread.sleep(40000);
							} catch (InterruptedException e) {
							}	
							if(!cliente.getIsAlive()){
								try {
									//cliente.getSocketCliente().close();
								} catch (Exception e) {
								}	
							}							
						}
					}
				}.start();
			}		
			@Override
			public void run() {
				while (true) {
					try {
						//SSLContext sslContext = buildSslContext(new FileInputStream("/home/pi/Desktop/servidoriotssl.pem"));
						//SSLSocketFactory factory =sslContext.getSocketFactory();
                        //SSLSocket socket = (SSLSocket)factory.createSocket("192.168.10.254", 27015);
						//socket.startHandshake();	
						Socket socket = new Socket("192.168.10.254", 27016);				
						cliente.setSocketCliente(socket);
						Gson gson = new GsonBuilder()
							.setDateFormat("dd/MM/yyyy HH:mm:ss")
							.excludeFieldsWithoutExposeAnnotation()
							.create();
                        String textJson = gson.toJson(networkConector);
						PrintWriter out = new PrintWriter(
                        	new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        out.println(textJson);
						//alive();
                       	cliente.run();		
						Thread.sleep(10000);									
					}
					catch (Exception e) {
						Log.log(this,e.getMessage(),"DEBUG");
					}
				}				
			}		
		}.start();			
	}

	public void criarConectorServidor(){
		conector = new Conector();
		conector.setNome(nomeServidorDefault);
		UUID uniqueKey = UUID.randomUUID();
        String id = uniqueKey.toString();
		conector.setIdConector(id);
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
		
		ServidorRest servidorRest = new ServidorRest(true,portaSSLRest);
		ServidorRest servidorRestNoSSL = new ServidorRest(false,portaRest);
		infoServidor = new InfoServidor();
		infoServidor.setVersao(ver.ver());
		infoServidor.setUpTime(ver.getUpDate());
		infoServidor.setNomeServidor(nomeServidorDefault);
		infoServidor.setNomeComputador(InetAddress.getLocalHost().getHostName());
		infoServidor.setIp(InetAddress.getLocalHost().getHostAddress());
		infoServidor.setDataAtual(new Date());
		servidorRest.setInfoServidor(infoServidor);
		servidorRestNoSSL.setInfoServidor(infoServidor);
		listaConectores = new ArrayList<>();
		servidorRest.setListaConectores(listaConectores);
		servidorRestNoSSL.setListaConectores(listaConectores);
		
		servidor = new ServerSocket(serverPortDefault+1);
		try {
			servidorWithSSL	= getServerSocket(serverPortDefault);
		} catch (Exception e) {
			Log.log(this,e.getMessage(),"DEBUG");
		}
		
		servidorLog = new ServerSocket(serverPortDefault+2);
		servidorRest.monitoraConectores(servidorRest);
		servidorRestNoSSL.monitoraConectores(servidorRest);
	}

	@SuppressWarnings("unused") 
	public void carregarConfiguracoes() throws SQLException {		
		Usuario usuario = new Usuario();	
		Configuracao cfg = new Configuracao();
		Integer portaServer = cfg.retornaPortaServidor();
		portaRest = cfg.retornaPortaRest();
		portaSSLRest = cfg.retornaPortaSSLRest();
		Log.log(this, "Cfg porta servidor:"+portaServer, "INFO");
		if(portaServer!=null){
			serverPortDefault = portaServer;
		}
		if(portaRest==null)
			portaRest = 8081;
		if(portaSSLRest==null)
			portaSSLRest = 8080;
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

	public void processarWithSSL() throws IOException{
		
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket socketCliente;
						socketCliente = servidorWithSSL.accept();
						Log.log(this,"Cliente SSL conectado: " + socketCliente.getInetAddress().getHostAddress()
								+ " porta " + socketCliente.getPort(),"INFO");
						new Thread() {
							@Override
							public void run() {
								Log.log(this,"Entrando SSL Thread ip:" + socketCliente.getInetAddress().getHostAddress()
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
								Log.log(this,"Saindo SSL Thread ip:" + cliente.getIpCliente(),"INFO");
							}
						}.start();

					} catch (Exception e) {
						Log.log(this, e.getMessage(), "DEBUG");
					}
				}
			}
		}.start();
	}

	private  ServerSocket getServerSocket(Integer porta)throws Exception {

		InetSocketAddress address = new InetSocketAddress("0.0.0.0", porta);
		// Backlog is the maximum number of pending connections on the socket,
		// 0 means that an implementation-specific default is used
		int backlog = 0;

		Path keyStorePath = Paths.get("/home/pi/Desktop/servidoriothttps.jks");
		char[] keyStorePassword = "password".toCharArray();

		// Bind the socket to the given port and address
		ServerSocket serverSocket = getSslContext(keyStorePath, keyStorePassword)
				.getServerSocketFactory()
				.createServerSocket(address.getPort(), backlog, address.getAddress());

		Arrays.fill(keyStorePassword, '0');

		return serverSocket;
	}

	private  SSLContext getSslContext(Path keyStorePath, char[] keyStorePass)
        throws Exception {

		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(new FileInputStream(keyStorePath.toFile()), keyStorePass);

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(keyStore, keyStorePass);

		SSLContext sslContext = SSLContext.getInstance("TLS");
		// Null means using default implementations for TrustManager and SecureRandom
		sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
		return sslContext;
	}

	public void processarLogs() throws IOException{
		
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket socketLog;
						socketLog = servidorLog.accept();
						logSocket.add(socketLog);
						new Thread() {
							@Override
							public void run(){
								try{
									Socket s = socketLog;
									Terminal terminal = new Terminal();
									terminal.setSocket(socketLog);
									terminais.add(terminal);
									BufferedWriter saida;
									try {
										saida = new BufferedWriter(new OutputStreamWriter(socketLog.getOutputStream()));
										saida.write("Bem vindo ao ServidorIOT!" + "\r\n");
										saida.write(System.getProperty("os.name")+ "\r\n");
										Versao ver = new Versao();
										saida.write(ver.ver() + "\r\n");
										saida.write("IP Host:"+InetAddress.getLocalHost().getHostAddress()+"\r\n");
										saida.write("Porta Servidor IOT:"+String.valueOf(Log.getMain().getServerPortDefault())+"\r\n");
										saida.write(terminal.pegarMac());									
										String processName =
            								java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
										Long pid = Long.parseLong(processName.split("@")[0]);
										Long upt = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();                    
										terminal.setPid(pid);
										saida.write(processName + "\r\n");
										saida.write("PID:"+String.valueOf(pid) + "\r\n");
										saida.write(String.valueOf(upt) + "\r\n");
										saida.flush();
									} catch (IOException e) {
									}
									terminal.executar();
									logSocket.remove(s);
									terminais.remove(terminal);
								}
								catch(Exception e){
								}
							}
						}.start();

					} catch (Exception e) {
					}
				}
			}
		}.start();
	}
	public Integer getServerPortDefault() {
		return serverPortDefault;
	}

	public void setServerPortDefault(Integer serverPortDefault) {
		this.serverPortDefault = serverPortDefault;
	}
}
