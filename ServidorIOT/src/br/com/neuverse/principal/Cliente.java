package br.com.neuverse.principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
//import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import java.lang.reflect.Type;
//import com.google.gson.reflect.TypeToken;
//import br.com.neuverse.entity.ButtonIot;
import br.com.neuverse.entity.Conector;
import br.com.neuverse.enumerador.Status;

public class Cliente implements Runnable {

	private Socket socketCliente;
	private List<Conector> listaConectores;
	private List<Cliente> clientes;
	private String id;
	public BufferedReader entrada;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			 entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));

			while (true) {
				String mens = entrada.readLine();
				if (mens == null)
					break;
				Log.grava(mens);
				Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
				try {
					Conector conector = gson.fromJson(mens, Conector.class);
					if (conector.getStatus() == Status.LOGIN) {
						if (conector.getSenha().equals("M@r0403") && conector.getUsuario().equals("Matinhos")) {

							for (Conector con : listaConectores) {
								if (con.getNome().equals(conector.getNome())) {
									listaConectores.remove(con);
								}
								break;
							}

							conector.setErro("Sucesso.");
							conector.setStatus(Status.CONECTADO);
							UUID uniqueKey = UUID.randomUUID();
							conector.getIot().setId(uniqueKey.toString());
							uniqueKey = UUID.randomUUID();
							conector.setId(uniqueKey.toString());
							id = uniqueKey.toString();
							String jSon = gson.toJson(conector);
							listaConectores.add(conector);
							enviar(jSon + "\r\n");
							continue;
						} else {
							conector.setErro("Login falhou.Verifique seu usuario/senha.");
							conector.setStatus(Status.FAIL);
							String jSon = gson.toJson(conector);
							enviar(jSon + "\r\n");
							Thread.sleep(5000);
							socketCliente.close();
							break;
						}
					}
					if (conector.getStatus() == Status.RETORNO) {
						String jSon = gson.toJson(conector);
						for (Cliente cli : clientes) {
							if (conector.getId().equals(cli.getId())) {
								System.err.println(jSon);
								cli.enviar(jSon + "\r\n");
								cli.getSocketCliente().close();
								break;
							}
						}						
						continue;						
					}
					if(conector.getStatus() == Status.CONFIG) {
						
						
						
						
					}
					if (conector.getStatus() == Status.LOGINWITHCOMMAND) {
						if (conector.getSenha().equals("M@r0403") && conector.getUsuario().equals("Matinhos")) {
							conector.setErro("Sucesso.");
							UUID uniqueKey = UUID.randomUUID();
							conector.getIot().setId(uniqueKey.toString());
							uniqueKey = UUID.randomUUID();
							conector.setId(uniqueKey.toString());
							id = uniqueKey.toString();
							for (Conector con : listaConectores) {
								if (con.getIot().getName().equals(conector.getIot().getName())) {
									conector.setStatus(Status.CONTROLLERCOMMAND);
									for (Cliente cli : clientes) {
										if (con.getId().equals(cli.getId())) {
											String jSonComando = gson.toJson(conector);
											System.err.println(jSonComando);
											cli.enviar(jSonComando + "\r\n");
											break;
										}
									}
									
									break;
								}
							}
						} else {
							conector.setErro("Login falhou.Verifique seu usuario/senha.");
							conector.setStatus(Status.FAIL);
							String jSon = gson.toJson(conector);
							enviar(jSon + "\r\n");
							Thread.sleep(5000);
							socketCliente.close();
							break;
						}
					} else if (conector.getStatus() == Status.CONTROLLERCOMMAND) {
						for (Conector con : listaConectores) {
							if (con.getIot().getName().equals(conector.getIot().getName())) {
								con.getIot().setjSon(conector.getIot().getjSon());
								con.setStatus(Status.CONTROLLERCOMMAND);
								for (Cliente cli : clientes) {
									if (con.getId().equals(cli.getId())) {
										String jSon = gson.toJson(con);
										System.err.println(jSon);
										cli.enviar(jSon + "\r\n");
										break;
									}
								}
								break;
							}
						}

					} else if (conector.getStatus() == Status.ALIVE) {
						conector.setStatus(Status.CONECTADO);
						String jSon = gson.toJson(conector);
						enviar(jSon + "\r\n");
						continue;
					} else {
						socketCliente.close();
						break;
					}

				} catch (Exception e) {
					Log.grava("mensagem inválida");
					socketCliente.close();
					break;
				}
			}
		} catch (Exception e) {
			try {
				socketCliente.close();

			} catch (IOException e1) {
			}
		}
		
		
		Log.grava("Cliente desconectado");
		for (Conector con : listaConectores) {
			if (con.getId().equals(getId())) {
				listaConectores.remove(con);
			}
			break;
		}
		clientes.remove(this);

	}

	public void enviar(String mens) {
		BufferedWriter saida;
		try {
			saida = new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream()));
			saida.write(mens);
			saida.flush();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// saida.close();
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
