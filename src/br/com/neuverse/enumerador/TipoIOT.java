package br.com.neuverse.enumerador;

public enum TipoIOT {
	CONTROLELAMPADA(1,"Controle lampadas"),CONTROLEREMOTO(2,"Controle remoto"),IOT(3,"Iot"),HUMAN(4,"Humano"),
		SERVIDOR(5,"Servidor"),SERVIDORIOT(6,"ServidorIOT"),RESTPROTOCOL(7,"RestProtocol");

	private final int valor;
	private final String descricao;
	private TipoIOT tipo;

	TipoIOT(int i, String descricao) {
		this.valor = i;
		this.descricao = descricao;
	}

	public int getValor() {
		return valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public TipoIOT getTipo() {
		return tipo;
	}

	public void setTipo(TipoIOT tipo) {
		this.tipo = tipo;
	}

}
