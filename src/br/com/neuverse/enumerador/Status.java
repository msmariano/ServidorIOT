package br.com.neuverse.enumerador;

import java.util.ArrayList;
import java.util.List;

public enum Status {
	CONECTADO(1, ""),
	LOGIN(2, ""),
	VOID(3, ""),
	FAIL(4, ""),
	ALIVE(5, ""),
	JSON(6, ""),
	ON(7, ""),
	OFF(8, "OFF"),
	IN(9, "IN"),
	OUT(10, ""),
	CONTROLLERCOMMAND(11, ""),
	LOGINWITHCOMMAND(12, ""),
	RETORNO(13, ""),
	READ(14, ""),
	NA(15, ""),
	CONFIG(16, ""),
	ALEATORIOON(17, ""),
	ACIONARBOTAO(18, ""),
	ALEATORIOOFF(19, ""),
	ALEATORIOINFO(20, ""),
	GETVALUE(21, ""),
	RETORNOTRANSITORIO(22, ""),
	SUCESSO(23, ""),
	ERRO(24, ""),
	INFO_SERVIDOR(25, ""),
	LISTA_IOT(26, ""),
	LOGIN_OK(27, ""),
	LOGIN_FAIL(28, ""),
	INTERRUPTOR(29, "INTERRUPTOR"),
	HIGH(30, "HIGH"),
	LOW(31, "LOW"),
	PUSH(32, "PUSH"),
	HOLD(33, "HOLD"),
	KEY(34, "KEY"),
	NOTIFICACAO(35, "NOTIFICACAO"),
	PROCESSARBTN(36, "PROCESSARBTN"),
	COMANDO(36, "COMANDO"),
	S_REDIR(37,"REDICIONAR SERVIDOR");

	private final int valor;
	private final String descricao;

	Status(int i, String descricao) {
		this.valor = i;
		this.descricao = descricao;
	}

	public static Status getEnum(Integer id) {

		for (Status item : values()) {
			if (item.getValor() == id) {
				return item;
			}
		}
		return null;
	}

	public static Status getEnumByDesc(String desc) {

		for (Status item : values()) {
			if (item.getDescricao().equals(desc)) {
				return item;
			}
		}
		return null;
	}

	public static List<String> listDescricao() {
		List<String> lista = new ArrayList<>();
		for (Status item : values()) {
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
