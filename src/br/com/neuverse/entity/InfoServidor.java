package br.com.neuverse.entity;

import java.util.Date;

public class InfoServidor {
	private String nomeServidor;
	private Date dataAtual;
	private String ip;
	private String nomeComputador;
	private String versao;

	public String getNomeServidor() {
		return nomeServidor;
	}
	public String getVersao() {
		return versao;
	}
	public void setVersao(String versao) {
		this.versao = versao;
	}
	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getNomeComputador() {
		return nomeComputador;
	}
	public void setNomeComputador(String nomeComputador) {
		this.nomeComputador = nomeComputador;
	}
	public Date getDataAtual() {
		return dataAtual;
	}
	public void setDataAtual(Date dataAtual) {
		this.dataAtual = dataAtual;
	}
	

}
