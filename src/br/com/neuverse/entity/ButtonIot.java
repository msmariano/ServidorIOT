package br.com.neuverse.entity;

import br.com.neuverse.enumerador.Status;

public class ButtonIot {
	private Status tecla;
	private Integer buttonID;
	private Status status;
	private String jSon;
	private Status funcao;

	public Status getTecla() {
		return tecla;
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
