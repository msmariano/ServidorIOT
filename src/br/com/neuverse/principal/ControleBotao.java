package br.com.neuverse.principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.neuverse.entity.ButtonIot;
import br.com.neuverse.entity.Conector;
import br.com.neuverse.entity.Iot;
import br.com.neuverse.enumerador.Status;
import br.com.neuverse.enumerador.TipoIOT;



public class ControleBotao {

	private Status statusRetornado;
	private String nome;
	private String usuario;
	private String senha;
	private String nomeIot;
	private List<Integer> idsBt = new ArrayList<Integer>();

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void testaBotoes() {

		for (int j = 0; j < 8; j++) {
			String ret = envia(gerarConectorJson(Status.LOGINWITHCOMMAND, Status.READ, nomeIot, j));
			ret = ret.trim();
			int contador = 0, inicio = 0;
			for (int i = 0; i < ret.length(); i++) {
				if (ret.charAt(i) == '{') {
					contador++;
				} else if (ret.charAt(i) == '}') {
					contador--;
				}
				if (contador == 0 && i != 0) {
					pegarIds(ret.substring(inicio, i + 1));
					inicio = i + 1;
				}
			}
		}

	}

	public String gerarConectorJson(Status sEnvio, Status st, String iotDst, Integer buttonId) {
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		Conector conector = new Conector();
		conector.setIot(gerarIot(st, iotDst, buttonId));
		conector.setId("0");
		conector.setTipo(TipoIOT.HUMAN);
		conector.setNome(nome);
		conector.setSenha(senha);
		conector.setUsuario(usuario);
		conector.setStatus(sEnvio);
		return gson.toJson(conector);
	}

	public Iot gerarIot(Status st, String iotDst, Integer buttonId) {
		Iot iot = new Iot();
		iot.setId("0");
		iot.setName(iotDst);
		iot.setjSon(gerarBotoesJson(st, buttonId, iotDst));
		return iot;
	}

	public String gerarBotoesJson(Status st, Integer botaoId, String iotDst) {
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		ButtonIot buttonIot = new ButtonIot();
		buttonIot.setButtonID(botaoId);
		buttonIot.setStatus(st);
		buttonIot.setTecla(Status.NA);
		List<ButtonIot> botoes = new ArrayList<>();
		botoes.add(buttonIot);
		return gson.toJson(botoes);
	}

	public void pegarIds(String jSonRetorno) {
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		Conector conector = gson.fromJson(jSonRetorno, Conector.class);
		if (conector != null && conector.getStatus() != null && conector.getStatus().equals(Status.RETORNO)) {
			if (conector.getIot().getjSon() != null) {
				ButtonIot buttonIot = null;
				try {
					buttonIot = gson.fromJson(conector.getIot().getjSon(), ButtonIot.class);

					if (buttonIot != null && buttonIot.getFuncao() != null) {
						if (buttonIot.getFuncao().equals(Status.READ)) {
							if (buttonIot.getStatus() != null) {
								System.out.println("ButtonID:" + buttonIot.getButtonID());
								idsBt.add(buttonIot.getButtonID());
							}
						}
					}
				} catch (Exception e) {
				}
			}
		}
	}

	public String envia(String textJson) {
		String ret = null;
		try {

			InetAddress serverEnd = InetAddress.getByName("rasp4msmariano.dynv6.net");
			Socket socket = new Socket(serverEnd, 27016);
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
					true);

			out.println(textJson);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			ret = in.readLine();
			socket.close();

		} catch (Exception e) {

		}
		return ret;
	}

	public String getNomeIot() {
		return nomeIot;
	}

	public void setNomeIot(String nomeIot) {
		this.nomeIot = nomeIot;
	}

}
