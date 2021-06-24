package br.com.neuverse.entity;

import java.math.BigDecimal;

public class Imovel {
	private Integer idImovel;
	private BigDecimal areaTotal;
	private String nomeImovel;
	private Integer codTipoImovel;
	private Integer codTipoDocumento;
	private Integer idEnderecao;
	private Integer codSituacao;
	private Retorno retorno;
	public Integer getIdImovel() {
		return idImovel;
	}
	public void setIdImovel(Integer idImovel) {
		this.idImovel = idImovel;
	}
	public BigDecimal getAreaTotal() {
		return areaTotal;
	}
	public void setAreaTotal(BigDecimal areaTotal) {
		this.areaTotal = areaTotal;
	}
	public String getNomeImovel() {
		return nomeImovel;
	}
	public void setNomeImovel(String nomeImovel) {
		this.nomeImovel = nomeImovel;
	}
	public Integer getCodTipoImovel() {
		return codTipoImovel;
	}
	public void setCodTipoImovel(Integer codTipoImovel) {
		this.codTipoImovel = codTipoImovel;
	}
	public Integer getCodTipoDocumento() {
		return codTipoDocumento;
	}
	public void setCodTipoDocumento(Integer codTipoDocumento) {
		this.codTipoDocumento = codTipoDocumento;
	}
	public Integer getIdEnderecao() {
		return idEnderecao;
	}
	public void setIdEnderecao(Integer idEnderecao) {
		this.idEnderecao = idEnderecao;
	}
	public Integer getCodSituacao() {
		return codSituacao;
	}
	public void setCodSituacao(Integer codSituacao) {
		this.codSituacao = codSituacao;
	}
	public Retorno getRetorno() {
		return retorno;
	}
	public void setRetorno(Retorno retorno) {
		this.retorno = retorno;
	}

}
