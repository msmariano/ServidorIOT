package br.com.neuverse.entity;

public class Municipio {
	
	private Long	idMunicipioCorreios;
	private Integer	codIbge;
	private String	nomeMunicipio;
	private String siglaUF;
	

	
	public Long getIdMunicipioCorreios() {
		return idMunicipioCorreios;
	}
	public void setIdMunicipioCorreios(Long idMunicipioCorreios) {
		this.idMunicipioCorreios = idMunicipioCorreios;
	}
	public Integer getCodIbge() {
		return codIbge;
	}
	public void setCodIbge(Integer codIbge) {
		this.codIbge = codIbge;
	}
	public String getNomeMunicipio() {
		return nomeMunicipio;
	}
	public void setNomeMunicipio(String nomeMunicipio) {
		this.nomeMunicipio = nomeMunicipio;
	}
	public String getSiglaUF() {
		return siglaUF;
	}
	public void setSiglaUF(String sigla) {
		this.siglaUF = sigla;
	}
	
}
