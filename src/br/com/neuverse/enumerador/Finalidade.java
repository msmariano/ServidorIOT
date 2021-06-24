package br.com.neuverse.enumerador;

import java.util.ArrayList;
import java.util.List;

public enum Finalidade{
	NENHUM(0,"Nenhum", false, ""),
	RESTAURACAO_MATA_CILIAR(1, "Recomposição de Áreas de Preservação Permanente - APP", true, "Área protegida, com a função ambiental de preservar os recursos hídricos, a paisagem, a estabilidade geológica e a biodiversidade, facilitar o fluxo gênico de fauna e flora, proteger o solo e assegurar o bem-estar das populações humanas(rios, nascentes, topo de morro, declividade acima de 45º e etc)."),
	RESTAURACAO_RESERVA_LEGAL(2, "Recomposição de Reserva Legal", true, "Área localizada no interior de uma propriedade ou posse rural, delimitada nos termos do art. 12 da Lei Federal n.º 12.651/2012, com a função de assegurar o uso econômico de modo sustentável dos recursos naturais do imóvel rural, auxiliar a conservação e a reabilitação dos processos ecológicos e promover a conservação da biodiversidade, bem como o abrigo e proteção de fauna silvestre e da flora nativa."),
	RESTAURACAO_UNIDADE_CONSERVACAO(3, "Recomposição de Unidade de Conservação", false, "Ação de restauração dentro de alguma Unidade de Conservação do Estado do Paraná, podendo ser Municipal, Estadual, Federal e RPPN."),
	EVENTO(4,"Eventos com Plantio", false, "Eventos com plantio voltados somente para pessoa jurídica, ou seja Prefeituras, Instituições de Ensino, ONG's ou demais Órgãos Públicos e Privados."),
	FINS_ECONOMICOS(5, "Reflorestamento com Espécie Nativa", true, "Plantio com fins econômicos, tendo espaçamento e alinhamento definidos."),
	OUTROS_FINS(6, "Outros Fins (cortina vegetal, plantio isolado, etc)", true,"Plantio que não caracteriza restauração e ou reflorestamento, ou seja quebra vento, cortina verde, plantio isolado e etc."),
	PROJETOS_RECOMPOSICAO_RECURSOS_PUBLICOS(7, "Projetos de Recomposição financiados com recursos públicos", false, "Projetos voltados somente para pessoa jurídica, onde o projeto de restauração é financiado por recurso público como exemplo BNDES, Petrobrás e etc. Coloque na descrição a instituição financiadora e use a opção \"Anexos\" para enviar o(s) projeto(s). O envio de ao menos um arquivo é obrigatório."),
	REPOSICAO_FLORESTAL_OBRIGATORIA_SERFLOR(8, "Reposição Florestal Obrigatória - SERFLOR", true,"Projetos técnicos de reflorestamento para atendimento de reposição florestal obrigatória (Decreto 1940/96 - SERFLOR)."),
	CONDICIONANTE_AUTORIZACOES_FLORESTAIS_E_LICENCIAMENTOS_AMBIENTAIS(9, "Condicionante de Autorizações Florestais e Licenciamentos Ambientais", true,"É para atendimento de uma exigência contidas em autorizações florestais e licenciamentos ambientais pelo IAP."),
	ENRIQUECIMENTO_FRAGMENTOS_FLORESTAIS(10, "Enriquecimento de Fragmentos Florestais", true,"Plantio de espécies especificas para enriquecer o fragmentos florestais existentes no imóvel, exceto Reserva Legal e Áreas de Preservação Permanentes - APP's."),
	CUMPRIMENTO_TERMO_AIA(11, "Cumprimento de Termos de Compromisso de Restauração referente a Autos de Infração Ambiental", true,"É o instrumento pelo qual são estabelecidas as obrigações a serem cumpridas pelo infrator, visando o ajuste de sua conduta. Informar no campo descrição o número do auto de infração ambiental e a instituição autuante, por exemplo IAP, IBAMA, Municípios e etc."),
	PLANTIO_URBANO(12, "Plantio Urbano", false,"Para pessoas físicas ou jurídicas, que queiram plantar uma pequena quantidade de mudas nativas em seu imóvel urbano, ou seja pedidos de até 10 mudas nativas."),
	PROJETOS_DE_PESQUISA(13,"Projetos de Pesquisa", false, "Projetos de pesquisa voltados somente para pessoa jurídica, ou seja Prefeituras, Instituições de Ensino, ONG's ou demais Órgãos Públicos e Privados.");

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