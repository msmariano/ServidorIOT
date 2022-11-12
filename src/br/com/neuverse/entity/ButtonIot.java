package br.com.neuverse.entity;

import com.google.gson.annotations.Expose;

import br.com.neuverse.enumerador.Status;

public class ButtonIot {

	@Expose(serialize = true)
	private Status tecla;
	@Expose(serialize = true)
	private Integer buttonID;
	@Expose(serialize = true)
	private Integer gpioNumControle;
	@Expose(serialize = true)
	private String nomeGpio;
	@Expose(serialize = true)
	private Integer gpioNum;
	@Expose(serialize = true)
	private Status status;
	@Expose(serialize = true)
	private String jSon;
	@Expose(serialize = true)
	private Status funcao;
	@Expose(serialize = true)
	private String ip;
	@Expose(serialize = true)
	private String nick;
	@Expose(serialize = true)
	private Boolean selecionado;
	private String sParam;
	private String iParam;
	
	public String getiParam() {
		return iParam;
	}

	public void setiParam(String iParam) {
		this.iParam = iParam;
	}

	public String getsParam() {
		return sParam;
	}

	public void setsParam(String sParam) {
		this.sParam = sParam;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public Status getTecla() {
		return tecla;
	}

	public Integer getGpioNumControle() {
		return gpioNumControle;
	}

	public void setGpioNumControle(Integer gpioNumControle) {
		this.gpioNumControle = gpioNumControle;
	}

	public Integer getGpioNum() {
		return gpioNum;
	}

	public void setGpioNum(Integer gpioNum) {
		this.gpioNum = gpioNum;
	}

	public String getNomeGpio() {
		return nomeGpio;
	}

	public void setNomeGpio(String nomeGpio) {
		this.nomeGpio = nomeGpio;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setTecla(Status tecla) {
		this.tecla = tecla;
	}

	public Integer getButtonID() {
		return this.buttonID;
	}

	public void setButtonID(Integer buttonID) {
		this.buttonID = buttonID;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getjSon() {
		return jSon;
	}

	public void setjSon(String jSon) {
		this.jSon = jSon;
	}

	public Status getFuncao() {
		return funcao;
	}

	public void setFuncao(Status funcao) {
		this.funcao = funcao;
	}

}
