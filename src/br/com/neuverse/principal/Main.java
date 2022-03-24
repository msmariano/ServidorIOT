package br.com.neuverse.principal;

import java.awt.Button;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.neuverse.database.Conexao;
import br.com.neuverse.database.Configuracao;
import br.com.neuverse.database.Usuario;
import br.com.neuverse.entity.ButtonGpioRaspPi;
import br.com.neuverse.entity.ButtonIot;
import br.com.neuverse.entity.ComandoIOT;
import br.com.neuverse.entity.Conector;
import br.com.neuverse.entity.InfoServidor;
import br.com.neuverse.entity.Iot;
import br.com.neuverse.entity.IotServidor;
import br.com.neuverse.entity.ServidorRest;
import br.com.neuverse.entity.Versao;
import br.com.neuverse.enumerador.Status;
import br.com.neuverse.enumerador.TipoIOT;

public class Main {

	private static ServerSocket servidor;
	private List<Conector> listaConectores;
	private List<ButtonGpioRaspPi>	listaGpioButtons;
	private List<Cliente> clientes;
	final Integer serverPort = 27015;

	public static void main(String[] args) throws IOException, SQLException {

		/*if (args.length == 0) {
			Runtime.getRuntime().exec(
					"java -jar " + (new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()))
							.getAbsolutePath() + " cmd");
		} else*/ {

			
			@SuppressWarnings("unused")
			Configuracao cfg = new Configuracao();
			
			Main mainServidor = new Main();

			


			String comando = "";
			if (args.length > 0) {
				System.out.println(args[0]);
				comando = args[0];
			}

			if (comando.equals("teste")) {
				Socket socket = new Socket("localhost", mainServidor.serverPort);
				PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
						true);
				out.println("{\"status\":\"INFO_SERVIDOR\"}");
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				String ret = in.readLine();
				Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
				InfoServidor infoServidor = gson.fromJson(ret, InfoServidor.class);
				System.out.println(infoServidor.getDataAtual());
				socket.close();
				return;
			} else if (comando.equals("listaIots")) {

			} else if (comando.equals("json") && args.length > 3) {
				Socket socket = new Socket(args[2], Integer.parseInt(args[3]));
				PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
						true);
				out.println(args[1]);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String ret = in.readLine();
				System.out.println(ret);

				socket.close();

				return;
			} else if (comando.equals("ver") && args.length > 1) {

			} else if ((comando.equals("ligar") || comando.equals("desligar")) && args.length > 1) {
				Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
				Conector conector = new Conector();
				conector.setStatus(Status.LOGINWITHCOMMAND);
				conector.setSenha("M@r0403");
				conector.setUsuario("Matinhos");
				conector.setTipo(TipoIOT.HUMAN);
				Iot iot = new Iot();
				conector.setIot(iot);
				conector.setId("0");
				iot.setId("0");
				iot.setTipoIOT(TipoIOT.SERVIDOR);
				iot.setName("ServidorMatinhosRaspPiZero");
				ButtonIot button = new ButtonIot();
				// if(args[1].toLowerCase().equals("garagem"))
				// button.setButtonID(1);
				// lse if(args[1].toLowerCase().equals("cozinha"))
				button.setButtonID(Integer.parseInt(args[1]));
				button.setStatus(Status.OUT);
				if (args[0].equals("ligar"))
					button.setTecla(Status.ON);
				else if (args[0].equals("desligar"))
					button.setTecla(Status.OFF);

				List<ButtonIot> botoes = new ArrayList<>();
				botoes.add(button);

				iot.setjSon(gson.toJson(botoes));
				String jSon = gson.toJson(conector, Conector.class);
				System.out.println(jSon);
				Socket socket = new Socket(args[2],  Integer.parseInt(args[3]));
				PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
						true);
				out.println(jSon);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				System.out.println(in.readLine());
				socket.close();
				return;

			}

			Log.grava("Bem vindo ao Servidor IOT!");
			servidor = new ServerSocket(mainServidor.serverPort);
			Log.grava(String.valueOf(mainServidor.serverPort));
			mainServidor.listaConectores = new ArrayList<>();
			ServidorRest servidorRest = new ServidorRest();
			servidorRest.setListaConectores(mainServidor.listaConectores);
			Conector conector = new Conector();
			conector.setId("");
			conector.setNome("ServidorMatinhos");
			conector.setTipo(TipoIOT.SERVIDOR);
			Iot iot = new Iot();
			iot.setTipoIOT(TipoIOT.SERVIDOR);
			iot.setName("ServidorMatinhosRaspPiZero");
			conector.setIot(iot);

			/*Conector conectorPiscina = new Conector();
			conectorPiscina.setId("");
			conectorPiscina.setNome("MatinhosPiscina");
			conectorPiscina.setTipo(TipoIOT.SERVIDORIOT);
			Iot iotPiscina = new Iot();
			iotPiscina.setTipoIOT(TipoIOT.SERVIDORIOT);
			iotPiscina.setName("ServidorMatinhosPiscina");
			conectorPiscina.setIot(iotPiscina);
			List<IotServidor> servidores = new ArrayList<>();
			IotServidor servidorIot = new IotServidor();
			servidores.add(servidorIot);
			servidorIot.setEndIp("192.168.0.251");
			servidorIot.setPorta("80");
			servidorIot.setId(0);
			iotPiscina.setServidor(servidores);*/
			
			
			mainServidor.listaConectores.add(conector);
			//mainServidor.listaConectores.add(conectorPiscina);
			mainServidor.clientes = new ArrayList<>();
			mainServidor.listaGpioButtons  = new ArrayList<>();
			Integer keys[][] = {{0,1},{8,9},{15,16}};
			Integer p[] = {1,1,1,1};

			/*List<ButtonIot> buttons = new ArrayList<>();
			for(int i=0;i < 3;i++){
				ButtonGpioRaspPi bgrpi = new ButtonGpioRaspPi(keys[i][0],keys[i][1],p[i]);
				bgrpi.setId(i);
				mainServidor.listaGpioButtons.add(bgrpi);
				ButtonIot bIot = new ButtonIot();
				bIot.setButtonID(i);
				bIot.setFuncao(Status.OUT);
				buttons.add(bIot);
			}
			ButtonIot bIot = new ButtonIot();
			bIot.setButtonID(4);
			bIot.setFuncao(Status.OUT);
			buttons.add(bIot);
			Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
			String jSon = gson.toJson(buttons);
			conector.getIot().setjSon(jSon);*/
			
			Log.log(mainServidor,Versao.ver());
		   

			//temporizador filtro piscina
			new Thread() {
				@Override
				public void run() {
					
					try {
						//Main.motorPiscina("ligar");
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					try {
						Thread.sleep(15*60*1000);
					} catch (InterruptedException e1) {					
					}
					try {
						//Main.motorPiscina("desligar");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					while (true) {
						SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
						String hora = sd.format(new Date());
						Log.grava("Thread Timer Motor Piscina:"+hora);
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if(hora.equals("00:00") || hora.equals("06:00") || hora.equals("12:00")
						|| hora.equals("18:00")){
							try {
								Log.grava("Inicio Timer");
								//Main.motorPiscina("ligar");
								Thread.sleep(15*60*1000);
								//Main.motorPiscina("desligar");
								Log.grava("Fim Timer");
							} catch (Exception e) {
								
								e.printStackTrace();
							}
						}
					}
				}
			}.start();


			try {
				Usuario usuario = new Usuario();
				usuario.setUsuario("marcelo@neuverse.com.br");
				usuario.setSenha("M@r0403");
				usuario.obterPorUsuarioSenha();
				System.out.println();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Conector con = new Conector();
			// con.setStatus(Status.INFO_SERVIDOR);
			// Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
			// String rt = gson.toJson(con);

			// System.out.println(rt);

			new Thread() {
				@Override
				public void run() {
					while (true) {
						try {
							Socket socketCliente;
							socketCliente = servidor.accept();
							Log.grava("Cliente conectado: " + socketCliente.getInetAddress().getHostAddress()
									+ " porta " + socketCliente.getPort());
							new Thread() {
								@Override
								public void run() {
									Log.grava("Entrando Thread ip:" + socketCliente.getInetAddress().getHostAddress()
											+ " porta " + socketCliente.getPort());
									Cliente cliente = new Cliente();
									cliente.setIpCliente(socketCliente.getInetAddress().getHostAddress() + " porta "
											+ socketCliente.getPort());
									mainServidor.clientes.add(cliente);
									cliente.setSocketCliente(socketCliente);
									cliente.setListaConectores(mainServidor.listaConectores);
									cliente.setListaGpioButtons(mainServidor.listaGpioButtons);
									cliente.setClientes(mainServidor.clientes);
									cliente.run();
									Log.grava("Saindo Thread ip:" + cliente.getIpCliente());
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
	public static ComandoIOT motorPiscina(String acao,String uri) throws Exception {
        SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
		String hora = sd.format(new Date());
        System.out.println(hora); 
        URL url = new URL("http://"+uri);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(HttpMethod.POST);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Host", "192.168.0.241");
        con.setRequestProperty("Connection", "Keep-Alive");
        String l = "{\"acao\":\""+acao+"\"}";
        con.setRequestProperty("Content-Length", String.valueOf(l.length()));
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        con.setDoOutput(true);
        
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = l.getBytes();
            os.write(input, 0, l.length());			
        }
        int responseCode = con.getResponseCode();

        System.out.println(responseCode);
       System.out.println(con.getContentLength());  


        if(responseCode == HttpURLConnection.HTTP_OK){
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                response.append(inputLine);
            }
            in.close();

            String json = response.toString().substring(0,con.getContentLength());
            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
            ComandoIOT ciot = gson.fromJson(json, ComandoIOT.class);
			return ciot;
        }
		return null;
    }
	
}
