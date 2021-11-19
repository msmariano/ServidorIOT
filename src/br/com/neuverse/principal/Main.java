package br.com.neuverse.principal;

import java.awt.Button;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.neuverse.database.Conexao;
import br.com.neuverse.database.Configuracao;
import br.com.neuverse.database.Usuario;
import br.com.neuverse.entity.ButtonGpioRaspPi;
import br.com.neuverse.entity.ButtonIot;
import br.com.neuverse.entity.Conector;
import br.com.neuverse.entity.InfoServidor;
import br.com.neuverse.entity.Iot;
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
			Conector conector = new Conector();
			conector.setId("");
			conector.setNome("ServidorMatinhos");
			conector.setTipo(TipoIOT.SERVIDOR);
			Iot iot = new Iot();
			iot.setTipoIOT(TipoIOT.SERVIDOR);
			iot.setName("ServidorMatinhosRaspPiZero");
			conector.setIot(iot);
			mainServidor.listaConectores.add(conector);
			mainServidor.clientes = new ArrayList<>();
			mainServidor.listaGpioButtons  = new ArrayList<>();
			Integer keys[][] = {{0,1},{8,9},{15,16},{4,5}};

			List<ButtonIot> buttons = new ArrayList<>();
			for(int i=0;i < 4;i++){
				ButtonGpioRaspPi bgrpi = new ButtonGpioRaspPi(keys[i][0],keys[i][1]);
				bgrpi.setId(i);
				mainServidor.listaGpioButtons.add(bgrpi);
				ButtonIot bIot = new ButtonIot();
				bIot.setButtonID(i);
				bIot.setFuncao(Status.OUT);
				buttons.add(bIot);
			}
			Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
			String jSon = gson.toJson(buttons);
			conector.getIot().setjSon(jSon);
			
			System.out.println("Versao 1.0.5 03/09/2021 19:37");
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
}
