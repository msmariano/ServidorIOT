package br.com.neuverse.entity;

public class Especie {
	private Long idEspecie;
	private String nomeCientifico;
	private String nomeComum;
	private String descricaoTipoEspecie;
	private Integer origemEspecieId;
	private boolean indEspecieAmeacadaExtincao;
	public String getNomeCientifico() {
		return nomeCientifico;
	}
	public void setNomeCientifico(String nomeCientifico) {
		this.nomeCientifico = nomeCientifico;
	}
	public Long getIdEspecie() {
		return idEspecie;
	}
	public void setIdEspecie(Long idEspecie) {
		this.idEspecie = idEspecie;
	}
	public String getNomeComum() {
		return nomeComum;
	}
	public void setNomeComum(String nomeComum) {
		this.nomeComum = nomeComum;
	}
	public Integer getOrigemEspecieId() {
		return origemEspecieId;
	}
	public void setOrigemEspecieId(Integer origemEspecieId) {
		this.origemEspecieId = origemEspecieId;
	}
	public String getDescricaoTipoEspecie() {
		return descricaoTipoEspecie;
	}
	public void setDescricaoTipoEspecie(String descricaoTipoEspecie) {
		this.descricaoTipoEspecie = descricaoTipoEspecie;
	}
	public boolean isIndEspecieAmeacadaExtincao() {
		return indEspecieAmeacadaExtincao;
	}
	public void setIndEspecieAmeacadaExtincao(boolean indEspecieAmeacadaExtincao) {
		this.indEspecieAmeacadaExtincao = indEspecieAmeacadaExtincao;
	}
}
