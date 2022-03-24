package br.com.neuverse.entity;

import br.com.neuverse.enumerador.Status;

public class ButtonIot {
	private Status tecla;
	private Integer buttonID;
	private Integer gpioNumControle;
	private String nomeGpio;
	private Integer gpioNum;
	private Status status;
	private String jSon;
	private Status funcao;
	private String ip;

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
