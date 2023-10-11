package br.com.neuverse.principal;

//sudo java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 /home/pi/Desktop/ServidorIOT.jar

//scp  .\ServidorIOT.jar pi@192.168.0.254:/home/pi/Desktop/
//openssl s_client -connect localhost:27015 -showcerts
//openssl s_client -connect 192.168.0.254:27017
//ps -e -o pid,cmd  | grep ServidorIOT.jar | awk '{print $1}'
//ps -e -o pid,cmd  | grep ServidorIOT.jar | awk '{print $1}'  | head -n -1 | tail -n 1
// keytool -genkeypair -keyalg RSA -alias selfsigned -keystore servidoriothttps.jks -storepass password -validity 360 -keysize 2048
// route add 192.168.10.0 mask 255.255.255.0 192.168.0.254
//keytool.exe -import -file "C:\Users\msmar\OneDrive\Documentos\ipca.bcb.gov.br.crt" -keystore "ipca.bcb.gov.br.crt" -storepass "changeit"
//ssh-keygen(sem senha) cat ~/.ssh/id_rsa.pub | ssh pi@192.168.18.254 "mkdir -p ~/.ssh && touch ~/.ssh/authorized_keys && chmod -R go= ~/.ssh && cat >> ~/.ssh/authorized_keys"
// scp .\ServidorIOT-1.jar pi@192.168.10.76:/home/pi
//ssh -L 5901:127.0.0.1:5900 -C -N -l pi 192.168.0.125

/*curl -k --location --request POST 'https://192.168.18.58:27016/ServidorIOT/listar' --data ''*/

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
import java.io.UnsupportedEncodingException;
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
import java.text.SimpleDateFormat;
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
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformManager;
import com.pi4j.wiringpi.I2C;

import br.com.neuverse.database.Configuracao;
import br.com.neuverse.database.Usuario;
import br.com.neuverse.entity.ButtonGpioBananaPi;
import br.com.neuverse.entity.ButtonGpioRaspPi;
import br.com.neuverse.entity.ButtonIot;
import br.com.neuverse.entity.Comando;
import br.com.neuverse.entity.Conector;
import br.com.neuverse.entity.ConfigIOT;
import br.com.neuverse.entity.InfoServidor;
import br.com.neuverse.entity.Iot;
import br.com.neuverse.entity.ListaConector;
import br.com.neuverse.entity.Parametro;
import br.com.neuverse.entity.ServidorCfg;
import br.com.neuverse.entity.ServidorRest;
import br.com.neuverse.entity.Versao;
import br.com.neuverse.entity.Device;
import br.com.neuverse.entity.GpioRemoto;
import br.com.neuverse.enumerador.Status;
import br.com.neuverse.enumerador.TipoIOT;
import de.pi3g.pi.oled.Font;
import de.pi3g.pi.oled.OLEDDisplay;

public class Main {

	private static ServerSocket servidor;
	private static ServerSocket servidorWithSSL;
	private static ServerSocket servidorGpioRemoto;
	private static ServerSocket servidorLog;
	private List<Conector> listaConectores = new ArrayList<>();
	private ListaConector ctrGlobal = new ListaConector();
	private List<ButtonGpioRaspPi> listaGpioButtons = new ArrayList<>();
	private List<ButtonGpioBananaPi> listaGpioButtonsBanana = new ArrayList<>();
	private List<GpioRemoto> listaGpioRemotos = new ArrayList<>();
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
	private ServidorRest servidorRest = new ServidorRest(true, portaSSLRest);
	private ServidorRest servidorRestNoSSL = new ServidorRest(true, portaRest);
	private ControlePiscina controlePiscina = null;
	private OLEDDisplay display;
	private String linhasDisplay [] = new String[5];
	

	public Conector getConector(){
		return this.conector;
	}


	public String[] getLinhasDisplay() {
		return linhasDisplay;
	}

	public void setLinhasDisplay(String[] linhasDisplay) {
		this.linhasDisplay = linhasDisplay;
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException, SQLException {

		// System.out.println("Total parametros:"+args.length);
		if (args.length > 0) {

			if (args[0].equals("oled")) {
				/*try {
					System.out.println("Testando OLED");
					Versao v = new Versao();
					OLEDDisplay display = new OLEDDisplay(0, 60);
					
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					while (true) {
						Thread.sleep(1000);
						display.clear();
						display.drawString("ServidorIOT "+v.getVersao(),  Font.FONT_5X8, 0, 0, true);
						display.drawString(sdf.format(new Date()),  Font.FONT_5X8, 0, 10, true);
						display.update();
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}*/
				return;

			}

			if (args[0].equals("ver")) {
				Versao v = new Versao();
				System.out.print(v.ver());
				return;

			}

			if (args[0].equals("reset")) {
				try {
					System.out.println("Executando reset");
					Runtime run = Runtime.getRuntime();
					// run.exec("ps -e -o pid,cmd | grep ServidorIOT.jar | awk '{print $1}' >
					// /home/pi/Desktop/pid.txt");
					run.exec(args[1]);
					return;
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}

			if (args[0].equals("confiot")) {
				try {
					ConfiguraIOT.main(null);
				} catch (Exception e) {
				}
			} else if (args[0].equals("testepi")) {
				try {
					ButtonGpioBananaPi btn = new ButtonGpioBananaPi(Integer.parseInt(args[2]), -1, -1, 1, "", 2);
					if (args[1].equals("on"))
						btn.on();
					else if (args[1].equals("off"))
						btn.off();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

			} else if (args[0].equals("testepile")) {
				try {
					try {
						System.out.println("Lendo Gpio" + args[1]);
						String gpioNameOut = "gpio" + args[1];
						BufferedReader le = new BufferedReader(
								new FileReader("/sys/class/gpio/" + gpioNameOut + "/value"));
						char b[] = new char[10];
						le.read(b);
						String valor = String.valueOf(b);
						System.out.println(valor);
						le.close();
						if (valor.trim().equals("0")) {
							System.out.println("OFF");
						} else if (valor.trim().equals("1")) {
							System.out.println("ON");
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}

				} catch (Exception e) {

				}
			}
			return;
		}
		try {
			String servidorNeuverse = InetAddress.getByName("rasp4msmariano.dynv6.net").toString();
			System.out.println("Endereco servidor:" + servidorNeuverse);
		} catch (Exception e) {
			// TODO: handle exception
		}

		/*
		 * try {
		 * String s = Cliente.convertPasswordToMD5("pradopi");
		 * System.out.println();
		 * } catch (NoSuchAlgorithmException e1) {
		 * }
		 */
		Configuracao cfg = new Configuracao();
		Main mainServidor = new Main();
		mainServidor.getServidorRest().setMain(mainServidor);
		Log.setMain(mainServidor);
		Log.log(mainServidor, "Bem vindo ao Servidor IOT!", "DEBUG");
		Log.log(mainServidor, mainServidor.ver.ver(), "DEBUG");
		mainServidor.carregarConfiguracoes();
		mainServidor.inicializar();
		mainServidor.criarConectorServidor();

		if (cfg.getTipoServidor().equals(1)) {
			mainServidor.carregaGpioButtons();
		} else if (cfg.getTipoServidor().equals(2)) {
			mainServidor.carregaGpioButtonsBanana();
		}
		if (servidor != null) {
			mainServidor.getServidorRestNoSSL().monitoraConectores(mainServidor.getServidorRestNoSSL());
			mainServidor.processar();
		}
		if (servidorLog != null)
			mainServidor.processarLogs();
		if (servidorWithSSL != null) {
			mainServidor.getServidorRest().monitoraConectores(mainServidor.getServidorRest());
			mainServidor.processarWithSSL();
		}
		if (servidorGpioRemoto != null) {
			mainServidor.processarGpioRemoto();
		}
		for (ServidorCfg servidor : cfg.retornaServidores()) {
			//mainServidor.linkServidorRedicionamento(servidor);
		}

		// BigInteger n = new BigInteger("20");
	}

	public List<Socket> getLogSocket() {
		return logSocket;
	}

	public void setLogSocket(List<Socket> logSocket) {
		this.logSocket = logSocket;
	}

	public void carregaGpioButtons() {
		try {
			Configuracao cfg = new Configuracao();
			List<Parametro> listaBtnGpio = cfg.retornaBtnGpio();
			List<ButtonIot> buttons = new ArrayList<>();
			for (Parametro btnGpio : listaBtnGpio) {
				try {
					ButtonGpioRaspPi bgrpi = new ButtonGpioRaspPi(btnGpio.getC1(), btnGpio.getC2(), btnGpio.getC3(),
							btnGpio.getC4());
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
				} catch (Exception e) {
					Log.log(this, "Inicializa Gpio Servidor :" + e.getMessage(), "DEBUG");
				}
			}
			Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
			String jSon = gson.toJson(buttons);
			conector.getIot().setjSon(jSon);
			conector.setDevices(devices);

		} catch (Exception e) {
			Log.log(this, "carregaGpioButtons() :" + e.getMessage(), "DEBUG");
		}
	}

	public void carregaGpioButtonsBanana() {
		try {
			Configuracao cfg = new Configuracao();
			List<Parametro> listaBtnGpio = cfg.retornaBtnGpio();
			List<ButtonIot> buttons = new ArrayList<>();
			for (Parametro btnGpio : listaBtnGpio) {
				try {
					ButtonGpioBananaPi bgrpi = new ButtonGpioBananaPi(btnGpio.getC1(), btnGpio.getC2(), btnGpio.getC3(),
							btnGpio.getC4(), btnGpio.getC9(), btnGpio.getC10());
					if (btnGpio.getC1() > -1) {
						listaGpioButtonsBanana.add(bgrpi);
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
				} catch (Exception e) {
					Log.log(this, "Inicializa Gpio Servidor :" + e.getMessage(), "DEBUG");
				}
			}
			Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
			String jSon = gson.toJson(buttons);
			conector.getIot().setjSon(jSon);
			conector.setDevices(devices);

		} catch (Exception e) {
			Log.log(this, "carregaGpioButtons() :" + e.getMessage(), "DEBUG");
		}
	}

	public static SSLContext buildSslContext(InputStream... inputStreams) throws Exception {
		X509Certificate cert;
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(null);

		for (InputStream inputStream : inputStreams) {
			try {
				CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
				cert = (X509Certificate) certificateFactory.generateCertificate(inputStream);
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

	@SuppressWarnings("all")
	public void linkServidorRedicionamento(ServidorCfg servidorCfg) {
		new Thread() {
			Conector networkConector = new Conector();
			Cliente cliente = new Cliente();
			boolean conectado = false;

			@Override
			public synchronized void start() {
				super.start();
			}

			public void alive() {
				new Thread() {
					@Override
					public void run() {
						this.setName("LinkServidorAlive");
						while (conectado) {
							try {
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
								Log.log(this, "Enviando alive!", "DEBUG");
								synchronized (cliente.getEventObj()) {
									cliente.getEventObj().wait(50000);
									if (cliente.getIsAlive()) {
										Log.log(this, "Retorno alive!", "DEBUG");
									} else {
										Log.log(this, "Alive timeout", "DEBUG");
										cliente.getSocketCliente().close();
										break;
									}
								}
							} catch (Exception e) {
							}
						}
					}
				}.start();
			}

			@Override
			public void run() {
				this.setName("LinkServidor");
				SSLContext sslContext = null;
				SSLSocket socket = null;
				SSLSocketFactory factory = null;
				try{
					sslContext = buildSslContext(
								new FileInputStream("/home/pi/Desktop/servidoriotsslpradovelho.pem"));
					factory = sslContext.getSocketFactory();
						
				}
				catch(Exception e){
					Log.log(this, "Erro ao criar factory com 192.168.18.254: "+e.getMessage(), "DEBUG");
				}
				while (true) {
					try {

						Thread.sleep(20*60*1000);
						Log.log(this,"Tentando Conexao Servidor","DEBUG'");
						socket = (SSLSocket) factory.createSocket("192.168.18.254", 27015);
						conectado = false;					
						socket.startHandshake();

						clientes.remove(cliente);
						networkConector = null;
						cliente = null;
						networkConector = new Conector();
						cliente = new Cliente();
						networkConector.setTipo(TipoIOT.NETWORK);
						networkConector.setStatus(Status.LOGIN);
						networkConector.setConectores(listaConectores);
						networkConector.setUsuario(servidorCfg.getUsuario());
						networkConector.setSenha(servidorCfg.getSenha());
						networkConector.setNome(servidorCfg.getNome());
						networkConector.setCliente(cliente);
						clientes.add(cliente);
						cliente.setListaConectores(listaConectores);
						cliente.setListaGpioButtons(listaGpioButtons);
						cliente.setListaGpioButtonsBanana(listaGpioButtonsBanana);
						cliente.setConectorCliente(networkConector);
						cliente.setClientes(clientes);
						// Socket socket = new Socket("192.168.18.254", 27016);
						socket.setSoTimeout(60000);
						socket.setKeepAlive(true);
						cliente.setSocketCliente(socket);
						Gson gson = new GsonBuilder()
								.setDateFormat("dd/MM/yyyy HH:mm:ss")
								.excludeFieldsWithoutExposeAnnotation()
								.create();
						String textJson = gson.toJson(networkConector);
						PrintWriter out = new PrintWriter(
								new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
						out.println(textJson);
						cliente.run();
						Thread.sleep(10000);
						conectado = true;
						alive();
					} catch (Exception e) {
						Log.log(this, "Erro de conexão com 192.168.18.254: "+e.getMessage(), "DEBUG");
					}
				}
			}
		}.start();
	}

	public void criarConectorServidor() throws SQLException {
		Configuracao cfg = new Configuracao();
		conector = new Conector();
		String nomeServ = cfg.getNomeServidor();
		if (nomeServ.equals(""))
			conector.setNome(nomeServidorDefault);
		else
			conector.setNome(nomeServ);
		UUID uniqueKey = UUID.randomUUID();
		String id = uniqueKey.toString();
		conector.setIdConector(id);
		conector.setTipo(TipoIOT.SERVIDOR);
		conector.setMac(Util.pegarMac());
		try {
			conector.setIp(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {

		}
		Iot iot = new Iot();
		if (cfg.getTipoServidor().equals(1)) {
			iot.setTipoIOT(TipoIOT.RASPBERRYGPIO);
		} else if (cfg.getTipoServidor().equals(2)) {
			iot.setTipoIOT(TipoIOT.BANANAGPIO);
		}
		iot.setName(nomeDeviceDefault);
		conector.setIot(iot);
		listaConectores.add(conector);
	}

	public void inicializar() throws IOException {
		infoServidor = new InfoServidor();
		infoServidor.setVersao(ver.ver());
		infoServidor.setUpTime(ver.getUpDate());
		infoServidor.setNomeServidor(nomeServidorDefault);
		infoServidor.setNomeComputador(InetAddress.getLocalHost().getHostName());
		infoServidor.setIp(InetAddress.getLocalHost().getHostAddress());
		infoServidor.setDataAtual(new Date());
		servidorRest.setInfoServidor(infoServidor);
		servidorRestNoSSL.setInfoServidor(infoServidor);
		//listaConectores = new ArrayList<>();
		servidorRest.setListaConectores(listaConectores);
		servidorRestNoSSL.setListaConectores(listaConectores);

		try {
			servidorWithSSL = getServerSocket(serverPortDefault);
			servidor = getServerSocket(serverPortDefault + 1);
			servidorLog = getServerSocket(serverPortDefault + 2);
			servidorGpioRemoto = getServerSocket(serverPortDefault + 3);

			new Thread() {
				@Override
				public void run() {
					try {
						for(int i = 0 ; i < linhasDisplay.length; i++ ){
							linhasDisplay[i]="";
						}
						Versao v = new Versao();
						display = new OLEDDisplay(0, 60);
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						boolean pisca = false;
						while (true) {
							Thread.sleep(1000);
							display.clear();
							display.drawString("Neuverse Tecnologia. " + v.getVersao() , Font.FONT_5X8, 0, 0, true);
							display.drawString("ServidorIOT " + v.getVersao() , Font.FONT_5X8, 0, 10, true);
							display.drawString(sdf.format(new Date()), Font.FONT_5X8, 0, 20, true);
							display.drawString(linhasDisplay[0], Font.FONT_5X8, 0, 30, true);

							display.drawString(linhasDisplay[1], Font.FONT_5X8, 0, 40, true);

							

							display.drawString(linhasDisplay[2], Font.FONT_5X8, 0, 50, true);
						

							//display.drawString("Ip:"+ipDaMaquina, Font.FONT_5X8, 0, 60, true);
							//display.drawImage(null, 0, 20);
							display.update();
							
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}.start();

			
			
		} catch (Exception e) {
			Log.log(this, e.getMessage(), "DEBUG");
		}
	}

	@SuppressWarnings("unused")
	public void carregarConfiguracoes() throws SQLException {
		Usuario usuario = new Usuario();
		Configuracao cfg = new Configuracao();
		Integer portaServer = cfg.retornaPortaServidor();
		portaRest = cfg.retornaPortaRest();
		if (cfg.getControlePiscina()) {
			Log.log(this, "Iniciando Controle Piscina", "DEBUG");
			controlePiscina = new ControlePiscina();
			controlePiscina.setLinhasDisplay(linhasDisplay);
			controlePiscina.inicializar();
		}
		portaSSLRest = cfg.retornaPortaSSLRest();
		Log.log(this, "Cfg porta servidor:" + portaServer, "INFO");
		if (portaServer != null) {
			serverPortDefault = portaServer;
		}
		if (portaRest == null)
			portaRest = 8081;
		if (portaSSLRest == null)
			portaSSLRest = 8080;
	}

	public void processar() throws IOException {

		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket socketCliente;
						socketCliente = servidor.accept();
						socketCliente.setKeepAlive(true);
						Log.log(this, "Cliente conectado: " + socketCliente.getInetAddress().getHostAddress()
								+ " porta " + socketCliente.getPort(), "INFO");
						new Thread() {
							@Override
							public void run() {
								Log.log(this, "Entrando Thread ip:" + socketCliente.getInetAddress().getHostAddress()
										+ " porta " + socketCliente.getPort(), "INFO");
								Cliente cliente = new Cliente();
								cliente.setIpCliente(socketCliente.getInetAddress().getHostAddress() + " porta "
										+ socketCliente.getPort());
								clientes.add(cliente);
								cliente.setSocketCliente(socketCliente);
								cliente.setListaConectores(listaConectores);
								cliente.setListaGpioButtons(listaGpioButtons);
								cliente.setListaGpioButtonsBanana(listaGpioButtonsBanana);
								cliente.setClientes(clientes);
								cliente.run();
								Log.log(this, "Saindo Thread ip:" + cliente.getIpCliente(), "INFO");
							}
						}.start();

					} catch (Exception e) {
					}
				}
			}
		}.start();
	}

	public void processarGpioRemoto() throws IOException {
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket gpioCliente;
						gpioCliente = servidorGpioRemoto.accept();
						gpioCliente.setKeepAlive(true);
						Log.log(this, "GpioCliente SSL conectado: " + gpioCliente.getInetAddress().getHostAddress()
								+ " porta " + gpioCliente.getPort(), "INFO");
						new Thread() {
							@Override
							public void run() {
								Log.log(this,
										"Entrando GpioCliente SSL Thread ip:"
												+ gpioCliente.getInetAddress().getHostAddress()
												+ " porta " + gpioCliente.getPort(),
										"INFO");

								GpioRemoto remoto = new GpioRemoto();
								Comando com = null;

								try {
									BufferedReader entrada = new BufferedReader(
											new InputStreamReader(gpioCliente.getInputStream(),
													StandardCharsets.UTF_8.name()));
									String mens = entrada.readLine();
									if (mens == null)
										return;
									Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
									com = gson.fromJson(mens, Comando.class);
									remoto.setNick(com.getNick());
								} catch (IOException e) {	
									return;								
								}
								remoto.setSocket(gpioCliente);
								Integer biotId = 1;
								ButtonIot uBiot = null;
								for (ButtonIot b : conector.getButtons()) {
									uBiot = b;
								}
								if (uBiot != null)
									biotId = uBiot.getButtonID() + 1;
								devices.add(remoto);
								remoto.setConectores(listaConectores);
								remoto.setClientes(clientes);
								remoto.setId(biotId);
								remoto.setDevices(devices);
								conector.getDevices().add(remoto);
								ButtonIot bIot = new ButtonIot();
								bIot.setButtonID(biotId);
								bIot.setFuncao(remoto.getFuncao());
								bIot.setTecla(remoto.getTecla());
								bIot.setStatus(com.getDevice());
								bIot.setNomeGpio("CtrlRemotoGpio");
								bIot.setNick(com.getNick());
								remoto.toDo(bIot);
								listaGpioRemotos.add(remoto);
								conector.getButtons().add(bIot);
								remoto.setButtonsIots(conector.getButtons());
								Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
								String jSon = gson.toJson(conector.getButtons());
								conector.getIot().setjSon(jSon);
								remoto.setCon(conector);
								remoto.run();
							}
						}.start();

					} catch (Exception e) {
						Log.log(this, e.getMessage(), "DEBUG");
					}
				}
			}
		}.start();
	}

	public void processarWithSSL() throws IOException {

		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket socketCliente;
						socketCliente = servidorWithSSL.accept();
						socketCliente.setKeepAlive(true);
						Log.log(this, "Cliente SSL conectado: " + socketCliente.getInetAddress().getHostAddress()
								+ " porta " + socketCliente.getPort(), "INFO");
						new Thread() {
							@Override
							public void run() {
								Log.log(this,
										"Entrando SSL Thread ip:" + socketCliente.getInetAddress().getHostAddress()
												+ " porta " + socketCliente.getPort(),
										"INFO");
								Cliente cliente = new Cliente();
								cliente.setIpCliente(socketCliente.getInetAddress().getHostAddress() + " porta "
										+ socketCliente.getPort());
								clientes.add(cliente);
								cliente.setSocketCliente(socketCliente);
								cliente.setListaConectores(listaConectores);
								cliente.setListaGpioButtons(listaGpioButtons);
								cliente.setListaGpioButtonsBanana(listaGpioButtonsBanana);
								cliente.setListaGpioRemotos(listaGpioRemotos);
								cliente.setClientes(clientes);
								cliente.run();
								Log.log(this, "Saindo SSL Thread ip:" + cliente.getIpCliente(), "INFO");
							}
						}.start();

					} catch (Exception e) {
						Log.log(this, e.getMessage(), "DEBUG");
					}
				}
			}
		}.start();
	}

	private ServerSocket getServerSocket(Integer porta) throws Exception {

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

	private SSLContext getSslContext(Path keyStorePath, char[] keyStorePass)
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

	public void processarLogs() throws IOException {

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
							public void run() {
								try {
									Socket s = socketLog;
									Terminal terminal = new Terminal();
									terminal.setSocket(socketLog);
									terminais.add(terminal);
									BufferedWriter saida;
									try {
										saida = new BufferedWriter(new OutputStreamWriter(socketLog.getOutputStream()));
										saida.write("Bem vindo ao ServidorIOT!" + "\r\n");
										saida.write(System.getProperty("os.name") + "\r\n");
										Versao ver = new Versao();
										saida.write(ver.ver() + "\r\n");
										saida.write("IP Host:" + InetAddress.getLocalHost().getHostAddress() + "\r\n");
										saida.write("Porta Servidor IOT:"
												+ String.valueOf(Log.getMain().getServerPortDefault()) + "\r\n");
										saida.write("MAC:" + terminal.pegarMac() + "\r\n");
										String processName = java.lang.management.ManagementFactory.getRuntimeMXBean()
												.getName();
										Long pid = Long.parseLong(processName.split("@")[0]);
										Long upt = java.lang.management.ManagementFactory.getRuntimeMXBean()
												.getUptime();
										terminal.setPid(pid);
										saida.write(processName + "\r\n");
										saida.write("PID:" + String.valueOf(pid) + "\r\n");
										saida.write(String.valueOf(upt) + "\r\n");
										saida.flush();
									} catch (IOException e) {
									}
									terminal.executar();
									logSocket.remove(s);
									terminais.remove(terminal);
								} catch (Exception e) {
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

	public ServidorRest getServidorRestNoSSL() {
		return servidorRestNoSSL;
	}

	public void setServidorRestNoSSL(ServidorRest servidorRestNoSSL) {
		this.servidorRestNoSSL = servidorRestNoSSL;
	}

	public ServidorRest getServidorRest() {
		return servidorRest;
	}

	public void setServidorRest(ServidorRest servidorRest) {
		this.servidorRest = servidorRest;
	}

	public List<Terminal> getTerminais() {
		return terminais;
	}

	public void setTerminais(List<Terminal> terminais) {
		this.terminais = terminais;
	}

	public ControlePiscina getControlePiscina() {

		return controlePiscina;
	}


	public OLEDDisplay getDisplay() {
		return display;
	}

	public void setDisplay(OLEDDisplay display) {
		this.display = display;
	}

}