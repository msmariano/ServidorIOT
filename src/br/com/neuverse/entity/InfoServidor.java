package br.com.neuverse.entity;

import java.util.Date;

import br.com.neuverse.principal.Main;

public class InfoServidor {
	private String nomeServidor;
	private Date dataAtual;
	private String ip;
	private String nomeComputador;
	private String versao;
	private Date upTime;
	
	public Date getUpTime() {
		return upTime;
	}
	public void setUpTime(Date upTime) {
		this.upTime = upTime;
	}

	private Main main;

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
	


    /**
     * @return Main return the main
     */
    public Main getMain() {
        return main;
    }

    /**
     * @param main the main to set
     */
    public void setMain(Main main) {
        this.main = main;
    }

}
