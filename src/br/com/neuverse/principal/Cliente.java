package br.com.neuverse.principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
//import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import java.lang.reflect.Type;
//import com.google.gson.reflect.TypeToken;
//import br.com.neuverse.entity.ButtonIot;
import br.com.neuverse.entity.Conector;
import br.com.neuverse.entity.InfoServidor;
import br.com.neuverse.entity.Mensagem;
import br.com.neuverse.enumerador.Status;

public class Cliente implements Runnable {

	private Socket socketCliente;
	private List<Conector> listaConectores;
	private List<Cliente> clientes;
	private String id;
	public BufferedReader entrada;
	private String nomeIotCliente;
	private String ipCliente;

	public String getNomeIotCliente() {
		return nomeIotCliente;
	}

	public String getIpCliente() {
		return ipCliente;
	}

	public void setIpCliente(String ipCliente) {
		this.ipCliente = ipCliente;
	}

	public void setNomeIotCliente(String nomeIotCliente) {
		this.nomeIotCliente = nomeIotCliente;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			 entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));

			while (true) {
				String mens = entrada.readLine();
				if (mens == null){
					Log.grava("Mensagem vazia");
					break;
				}
				Log.grava(mens);
				Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
				try {
					Conector conector = gson.fromJson(mens, Conector.class);
					if (conector.getStatus() == Status.INFO_SERVIDOR) {
						InfoServidor infoServidor = new InfoServidor();
						infoServidor.setNomeServidor("Servidor MsMariano");
						infoServidor.setDataAtual(new Date());
						String nomecomputador=InetAddress.getLocalHost().getHostName();
						infoServidor.setNomeComputador(nomecomputador);
						String jSon = gson.toJson(infoServidor);
						enviar(jSon + "\r\n");
					}
					
					if (conector.getStatus() == Status.LOGIN) {
						if (conector.getSenha().equals("M@r0403") && conector.getUsuario().equals("Matinhos")) {

							for (Conector con : listaConectores) {
								if (con.getNome().equals(conector.getNome())) {
									listaConectores.remove(con);
									
									for(Cliente c : clientes) {
										if(c.getNomeIotCliente().equals(conector.getNome())) {
											try {
												Log.grava("Removendo["+con.getNome()+"]  id: "+c.getId());
												clientes.remove(c);												
												c.socketCliente.close();							
												
											}catch (Exception e) {
												// TODO: handle exception
											}
											break;
										}
									}
								}
								break;
							}

							setNomeIotCliente(conector.getNome());
							conector.setErro("Sucesso.");
							conector.setStatus(Status.CONECTADO);
							UUID uniqueKey = UUID.randomUUID();
							conector.getIot().setId(uniqueKey.toString());
							uniqueKey = UUID.randomUUID();
							conector.setId(uniqueKey.toString());
							id = uniqueKey.toString();
							String jSon = gson.toJson(conector);
							Log.grava("Inserindo["+conector.getNome()+"] id: "+conector.getId());
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
								System.err.println("RETORNO: "+ jSon);
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
							
							boolean sair =true;
							for (Conector con : listaConectores) {
								if (con.getIot().getName().equals(conector.getIot().getName())) {
									conector.setStatus(Status.CONTROLLERCOMMAND);
									for (Cliente cli : clientes) {
										if (con.getId().equals(cli.getId())) {
											String jSonComando = gson.toJson(conector);
											System.err.println("LOGINWITHCOMMAND->CONTROLLERCOMMAND json: "+conector.getIot().getjSon());
											
											Conector cr = new Conector();
											cr.setId(id);
											cr.setStatus(Status.RETORNOTRANSITORIO);
											Mensagem mensRet = new Mensagem();
											mensRet.setId(id);
											mensRet.setSt(Status.SUCESSO);
											mensRet.setMens("Comando enviando,por favor aguarde.");
											cr.setMens(mensRet);
											String ret  = gson.toJson(cr);
											enviar(ret);
											
											cli.enviar(jSonComando + "\r\n");
											sair = false;	
											break;
										}
									}									
									break;
								}
							}
							if(sair) {
								System.err.println("LOGINWITHCOMMAND n�o encontrdo conector:"+conector.getIot().getName()+ " por "+conector.getNome());
								
								Conector cr = new Conector();
								cr.setId(id);
								cr.setStatus(Status.RETORNOTRANSITORIO);
								Mensagem mensRet = new Mensagem();
								mensRet.setId(id);
								mensRet.setMens("LOGINWITHCOMMAND n�o encontrdo conector:"+conector.getIot().getName()+ " por "+conector.getNome());
								cr.setMens(mensRet);
								mensRet.setSt(Status.ERRO);
								String ret  = gson.toJson(cr);
								enviar(ret);								
								socketCliente.close();
								break;
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
										//System.err.println(jSon);
										cli.enviar(jSon + "\r\n");
										break;
									}
								}
								break;
							}
						}

					} else if (conector.getStatus() == Status.ALIVE) {
						conector.setStatus(Status.CONECTADO);
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
						Mensagem mensagem = new Mensagem();
						mensagem.setMens(sdf.format(new Date()));
						conector.setMens(mensagem);
						String jSon = gson.toJson(conector);
						enviar(jSon + "\r\n");
						Log.grava("Alive:"+conector.getNome()+" "+conector.getMens().getMens());
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
				Log.grava("Exception_1:"+e.getMessage());
				socketCliente.close();

			} catch (IOException e1) {
			}
		}
		
		
		Log.grava("Cliente desconectando:"+this.getId());
		for (Conector con : listaConectores) {
			if (con.getId().equals(getId())) {
				Log.grava("Removendo conector:"+con.getNome());
				listaConectores.remove(con);
				break;
			}			
		}
		clientes.remove(this);

	}

	public synchronized void enviar(String mens) {
		BufferedWriter saida;
		try {
			saida = new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream()));
			saida.write(mens);
			saida.flush();
			/*try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
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
