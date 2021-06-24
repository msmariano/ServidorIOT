package br.com.neuverse.entity;

import java.util.List;

public class Retorno {
	private String mensagem;
	private Integer codigo;
	private String numeroRequerimentoGerado;
	private Requerimento requerimento;
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public String getNumeroRequerimentoGerado() {
		return numeroRequerimentoGerado;
	}
	public void setNumeroRequerimentoGerado(String numeroRequerimentoGerado) {
		this.numeroRequerimentoGerado = numeroRequerimentoGerado;
	}
	public Requerimento getRequerimento() {
		return requerimento;
	}
	public void setRequerimento(Requerimento requerimento) {
		this.requerimento = requerimento;
	}

	
}
