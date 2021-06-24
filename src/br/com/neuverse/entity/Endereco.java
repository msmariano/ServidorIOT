package br.com.neuverse.entity;

public class Endereco {
	private Integer idEndereco;
	private Integer idMunicipiosCorreio;
	private Integer codIdentificacao;
	private String logradouro;
	private String cep;
	public Integer getIdEndereco() {
		return idEndereco;
	}
	public void setIdEndereco(Integer idEndereco) {
		this.idEndereco = idEndereco;
	}
	public Integer getIdMunicipiosCorreio() {
		return idMunicipiosCorreio;
	}
	public void setIdMunicipiosCorreio(Integer idMunicipiosCorreio) {
		this.idMunicipiosCorreio = idMunicipiosCorreio;
	}
	public Integer getCodIdentificacao() {
		return codIdentificacao;
	}
	public void setCodIdentificacao(Integer codIdentificacao) {
		this.codIdentificacao = codIdentificacao;
	}
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}

}
