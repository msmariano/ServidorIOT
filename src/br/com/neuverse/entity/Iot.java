package br.com.neuverse.entity;

import java.util.List;

import br.com.neuverse.enumerador.TipoIOT;

public class Iot {
	private String id;
	private String name;
	private String jSon;
	private String objeto;
	private String protocolo;
	private TipoIOT tipoIOT;
	private String ip;
	private List<IotServidor> servidores;
	
	public String getId() {
		return id;
	}
	public String getProtocolo() {
		return protocolo;
	}
	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}
	public String getObjeto() {
		return objeto;
	}
	public void setObjeto(String objeto) {
		this.objeto = objeto;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public List<IotServidor> getServidor() {
		return servidores;
	}
	public void setServidor(List<IotServidor> servidores) {
		this.servidores = servidores;
	}
	public TipoIOT getTipoIOT() {
		return tipoIOT;
	}
	public void setTipoIOT(TipoIOT tipoIOT) {
		this.tipoIOT = tipoIOT;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getjSon() {
		return jSon;
	}
	public void setjSon(String jSon) {
		this.jSon = jSon;
	}
	

}
