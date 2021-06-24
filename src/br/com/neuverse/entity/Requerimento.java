package br.com.neuverse.entity;

import java.math.BigDecimal;

public class Requerimento {
	private Integer idPessoa;
	private BigDecimal areaPlantio;
	private Integer qtdeMudas;
	private String descricaoRequerimento;
	private Integer idPessoaRepresentante;
	private Integer codNaturezaJuridica;
	private Integer codEntidadeConveniada;
	private Integer codFinalidade;
	private char indCumpTermoAutoInfracao;
	private Integer idImovel;
	private Integer idMunicipio;
	public Integer getIdPessoa() {
		return idPessoa;
	}
	public void setIdPessoa(Integer idPessoa) {
		this.idPessoa = idPessoa;
	}
	public BigDecimal getAreaPlantio() {
		return areaPlantio;
	}
	public void setAreaPlantio(BigDecimal areaPlantio) {
		this.areaPlantio = areaPlantio;
	}
	public Integer getQtdeMudas() {
		return qtdeMudas;
	}
	public void setQtdeMudas(Integer qtdeMudas) {
		this.qtdeMudas = qtdeMudas;
	}
	public String getDescricaoRequerimento() {
		return descricaoRequerimento;
	}
	public void setDescricaoRequerimento(String descricaoRequerimento) {
		this.descricaoRequerimento = descricaoRequerimento;
	}
	public Integer getIdPessoaRepresentante() {
		return idPessoaRepresentante;
	}
	public void setIdPessoaRepresentante(Integer idPessoaRepresentante) {
		this.idPessoaRepresentante = idPessoaRepresentante;
	}
	public Integer getCodNaturezaJuridica() {
		return codNaturezaJuridica;
	}
	public void setCodNaturezaJuridica(Integer codNaturezaJuridica) {
		this.codNaturezaJuridica = codNaturezaJuridica;
	}
	public Integer getCodEntidadeConveniada() {
		return codEntidadeConveniada;
	}
	public void setCodEntidadeConveniada(Integer codEntidadeConveniada) {
		this.codEntidadeConveniada = codEntidadeConveniada;
	}
	public Integer getCodFinalidade() {
		return codFinalidade;
	}
	public void setCodFinalidade(Integer codFinalidade) {
		this.codFinalidade = codFinalidade;
	}
	public char getIndCumpTermoAutoInfracao() {
		return indCumpTermoAutoInfracao;
	}
	public void setIndCumpTermoAutoInfracao(char indCumpTermoAutoInfracao) {
		this.indCumpTermoAutoInfracao = indCumpTermoAutoInfracao;
	}
	public Integer getIdImovel() {
		return idImovel;
	}
	public void setIdImovel(Integer idImovel) {
		this.idImovel = idImovel;
	}
	public Integer getIdMunicipio() {
		return idMunicipio;
	}
	public void setIdMunicipio(Integer idMunicipio) {
		this.idMunicipio = idMunicipio;
	}
	public Integer getCodUnidadeConservacao() {
		return codUnidadeConservacao;
	}
	public void setCodUnidadeConservacao(Integer codUnidadeConservacao) {
		this.codUnidadeConservacao = codUnidadeConservacao;
	}
	public Integer getCodInfracaoAmbiental() {
		return codInfracaoAmbiental;
	}
	public void setCodInfracaoAmbiental(Integer codInfracaoAmbiental) {
		this.codInfracaoAmbiental = codInfracaoAmbiental;
	}
	public Integer getDescricaoPlantioUrbanoPFisica() {
		return descricaoPlantioUrbanoPFisica;
	}
	public void setDescricaoPlantioUrbanoPFisica(Integer descricaoPlantioUrbanoPFisica) {
		this.descricaoPlantioUrbanoPFisica = descricaoPlantioUrbanoPFisica;
	}
	public String getDescricaoProjetoRecomposicao() {
		return descricaoProjetoRecomposicao;
	}
	public void setDescricaoProjetoRecomposicao(String descricaoProjetoRecomposicao) {
		this.descricaoProjetoRecomposicao = descricaoProjetoRecomposicao;
	}
	public Integer getDescricaoPlantioUrbanoPJuridica() {
		return descricaoPlantioUrbanoPJuridica;
	}
	public void setDescricaoPlantioUrbanoPJuridica(Integer descricaoPlantioUrbanoPJuridica) {
		this.descricaoPlantioUrbanoPJuridica = descricaoPlantioUrbanoPJuridica;
	}
	public String getDescricaoProjetosPesquisa() {
		return descricaoProjetosPesquisa;
	}
	public void setDescricaoProjetosPesquisa(String descricaoProjetosPesquisa) {
		this.descricaoProjetosPesquisa = descricaoProjetosPesquisa;
	}
	public String getUtmEste() {
		return utmEste;
	}
	public void setUtmEste(String utmEste) {
		this.utmEste = utmEste;
	}
	public String getUtmNorte() {
		return utmNorte;
	}
	public void setUtmNorte(String utmNorte) {
		this.utmNorte = utmNorte;
	}
	public Integer getCodTipoMedida() {
		return codTipoMedida;
	}
	public void setCodTipoMedida(Integer codTipoMedida) {
		this.codTipoMedida = codTipoMedida;
	}
	private Integer codUnidadeConservacao;
	private Integer  codInfracaoAmbiental;
	private Integer descricaoPlantioUrbanoPFisica;
	private String descricaoProjetoRecomposicao;
	private Integer descricaoPlantioUrbanoPJuridica;
	private String descricaoProjetosPesquisa;
	private String utmEste;
	private String utmNorte;
	private Integer codTipoMedida;
	
	
}

//em pessoa fisica
//Recomposição de Áreas de Preservação Permanente - APP 
//Recomposição de Reserva Legal
//Reposição Florestal Obrigatória - SERFLOR
//Enriquecimento de Fragmentos Florestais
//	-Cumprimento de termo de compromisso relacionado com Auto de Infração Ambiental(não obrigatorio)

//Recomposição de Unidade de Conservação
//	-Municipio(obrigatorio)
// - codUnidadeConservacao(obrigatorio)


//Cumprimento de Termos de Compromisso de Restauração referente a Autos de Infração Ambiental
// codInfracaoAmbiental(nao obrigatorio

//Plantio Urbano
// -descricao

// o imovel e obrigatorio para todos
//-------------------------------------------------

//Pessoa juridica

//eventos com plantio
	//-municipio(obrigatorio)
	//-anexar arquivo (nao obrigatorio)
	//o imovel nao obrigatorio


//Projetos de Recomposição financiados com recursos públicos
//-municipio(obrigatorio)
	//-anexar arquivo (nao obrigatorio)
	//-descricao obrigatorio
//Plantio Urbano
//-descricao
//-municipio(obrigatorio)


//Projetos de Pesquisa

//-municipio(obrigatorio)
	//-anexar arquivo (nao obrigatorio)
	//-descricao obrigatorio