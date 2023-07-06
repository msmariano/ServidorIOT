package br.com.neuverse.enumerador;

import java.util.ArrayList;
import java.util.List;

public enum ComEnum {
    LIGAR(1,"Ligar"),
    DESLIGAR(1,"Desligar");

    private final int valor;
	private final String descricao;

	ComEnum(int i, String descricao) {
		this.valor = i;
		this.descricao = descricao;
	}

	public static ComEnum getEnum(Integer id) {

		for (ComEnum item : values()) {
			if (item.getValor() == id) {
				return item;
			}
		}
		return null;
	}

	public static ComEnum getEnumByDesc(String desc) {

		for (ComEnum item : values()) {
			if (item.getDescricao().equals(desc)) {
				return item;
			}
		}
		return null;
	}

	public static List<String> listDescricao() {
		List<String> lista = new ArrayList<>();
		for (ComEnum item : values()) {
			if(!item.getDescricao().equals(""))
				lista.add(item.getDescricao());
		}
		return lista;
	}

	public String getDescricao() {
		return descricao;
	}

	public int getValor() {
		return valor;
	}
    
}
