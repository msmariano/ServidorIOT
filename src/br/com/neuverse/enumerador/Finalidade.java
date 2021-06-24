package br.com.neuverse.enumerador;

import java.util.ArrayList;
import java.util.List;

public enum Finalidade{
	NENHUM(0,"Nenhum", false, ""),
	RESTAURACAO_MATA_CILIAR(1, "Recomposi��o de �reas de Preserva��o Permanente - APP", true, "�rea protegida, com a fun��o ambiental de preservar os recursos h�dricos, a paisagem, a estabilidade geol�gica e a biodiversidade, facilitar o fluxo g�nico de fauna e flora, proteger o solo e assegurar o bem-estar das popula��es humanas(rios, nascentes, topo de morro, declividade acima de 45� e etc)."),
	RESTAURACAO_RESERVA_LEGAL(2, "Recomposi��o de Reserva Legal", true, "�rea localizada no interior de uma propriedade ou posse rural, delimitada nos termos do art. 12 da Lei Federal n.� 12.651/2012, com a fun��o de assegurar o uso econ�mico de modo sustent�vel dos recursos naturais do im�vel rural, auxiliar a conserva��o e a reabilita��o dos processos ecol�gicos e promover a conserva��o da biodiversidade, bem como o abrigo e prote��o de fauna silvestre e da flora nativa."),
	RESTAURACAO_UNIDADE_CONSERVACAO(3, "Recomposi��o de Unidade de Conserva��o", false, "A��o de restaura��o dentro de alguma Unidade de Conserva��o do Estado do Paran�, podendo ser Municipal, Estadual, Federal e RPPN."),
	EVENTO(4,"Eventos com Plantio", false, "Eventos com plantio voltados somente para pessoa jur�dica, ou seja Prefeituras, Institui��es de Ensino, ONG's ou demais �rg�os P�blicos e Privados."),
	FINS_ECONOMICOS(5, "Reflorestamento com Esp�cie Nativa", true, "Plantio com fins econ�micos, tendo espa�amento e alinhamento definidos."),
	OUTROS_FINS(6, "Outros Fins (cortina vegetal, plantio isolado, etc)", true,"Plantio que n�o caracteriza restaura��o e ou reflorestamento, ou seja quebra vento, cortina verde, plantio isolado e etc."),
	PROJETOS_RECOMPOSICAO_RECURSOS_PUBLICOS(7, "Projetos de Recomposi��o financiados com recursos p�blicos", false, "Projetos voltados somente para pessoa jur�dica, onde o projeto de restaura��o � financiado por recurso p�blico como exemplo BNDES, Petrobr�s e etc. Coloque na descri��o a institui��o financiadora e use a op��o \"Anexos\" para enviar o(s) projeto(s). O envio de ao menos um arquivo � obrigat�rio."),
	REPOSICAO_FLORESTAL_OBRIGATORIA_SERFLOR(8, "Reposi��o Florestal Obrigat�ria - SERFLOR", true,"Projetos t�cnicos de reflorestamento para atendimento de reposi��o florestal obrigat�ria (Decreto 1940/96 - SERFLOR)."),
	CONDICIONANTE_AUTORIZACOES_FLORESTAIS_E_LICENCIAMENTOS_AMBIENTAIS(9, "Condicionante de Autoriza��es Florestais e Licenciamentos Ambientais", true,"� para atendimento de uma exig�ncia contidas em autoriza��es florestais e licenciamentos ambientais pelo IAP."),
	ENRIQUECIMENTO_FRAGMENTOS_FLORESTAIS(10, "Enriquecimento de Fragmentos Florestais", true,"Plantio de esp�cies especificas para enriquecer o fragmentos florestais existentes no im�vel, exceto Reserva Legal e �reas de Preserva��o Permanentes - APP's."),
	CUMPRIMENTO_TERMO_AIA(11, "Cumprimento de Termos de Compromisso de Restaura��o referente a Autos de Infra��o Ambiental", true,"� o instrumento pelo qual s�o estabelecidas as obriga��es a serem cumpridas pelo infrator, visando o ajuste de sua conduta. Informar no campo descri��o o n�mero do auto de infra��o ambiental e a institui��o autuante, por exemplo IAP, IBAMA, Munic�pios e etc."),
	PLANTIO_URBANO(12, "Plantio Urbano", false,"Para pessoas f�sicas ou jur�dicas, que queiram plantar uma pequena quantidade de mudas nativas em seu im�vel urbano, ou seja pedidos de at� 10 mudas nativas."),
	PROJETOS_DE_PESQUISA(13,"Projetos de Pesquisa", false, "Projetos de pesquisa voltados somente para pessoa jur�dica, ou seja Prefeituras, Institui��es de Ensino, ONG's ou demais �rg�os P�blicos e Privados.");

	private final Integer id;
	private final String descricao;
	private final Boolean indImovelObrigatorio;
	private final String descricaoDetalhada;

	private Finalidade(Integer id, String descricao, Boolean indImovelObrigatorio, String textoFinalidade) {
		this.id = id;
		this.descricao = descricao;
		this.descricaoDetalhada = textoFinalidade;
		this.indImovelObrigatorio = indImovelObrigatorio;
	}

	public static Finalidade getFinalidade(Integer id) {
		for (Finalidade s : Finalidade.values()) {
			if (s.id.equals(id)) {
				return s;
			}
		}
		return null;
	}
	
	public static List<Finalidade> listarParaAbaLocalRestauracao() {
		List<Finalidade> lst = new ArrayList<Finalidade>();
		for (Finalidade s : Finalidade.values()) {
			if (!s.id.equals(0) && !s.id.equals(0)) {
				lst.add(s);	
			}
		}
		return lst;
	}

	public Integer getId() {
		return id;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getDescricaoDetalhada() {
		return descricaoDetalhada;
	}

	public Boolean getIndImovelObrigatorio() {
		return indImovelObrigatorio;
	}

	
}