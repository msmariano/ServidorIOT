package br.com.neuverse.entity;

import java.util.List;

import com.google.gson.annotations.Expose;

import br.com.neuverse.enumerador.TipoIOT;

public class Iot {
	@Expose(serialize = true)
	private String id;
	@Expose(serialize = true)
	private String name;
	@Expose(serialize = true)
	private String jSon;
	@Expose(serialize = true)
	private String objeto;
	@Expose(serialize = true)
	private String protocolo;
	@Expose(serialize = true)
	private TipoIOT tipoIOT;
	@Expose(serialize = true)
	private String ip;
	@Expose(serialize = true)
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
