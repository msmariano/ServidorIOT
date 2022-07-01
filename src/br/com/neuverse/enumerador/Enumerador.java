package br.com.neuverse.enumerador;

public class Enumerador {

	private Integer id;
	private Integer casasDecimais;
	private String sigla;
	private String descricao;
	private String unidadeMedida;
	private String icone;
	
	private Boolean status;

	public Enumerador(Integer id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}
	
	public Enumerador(String sigla, String descricao) {
		this.id = 0;
		this.sigla = sigla;
		this.descricao = descricao;
	}
		
	public Enumerador(Integer id, String descricao, Boolean status) {
		this.id = id;
		this.descricao = descricao;
		this.status = status;
	}
	
	public Enumerador(Integer id, String descricao, String unidadeMedida) {
		this.id = id;
		this.descricao = descricao;
		this.unidadeMedida = unidadeMedida;
	}

	public Enumerador(Integer id, String descricao, Integer casasDecimais) {
		this.id = id;
		this.descricao = descricao;
		this.casasDecimais = casasDecimais;
	}
	
	public Enumerador(Integer id, String descricao, String icone, Boolean status) {
		this.id = id;
		this.descricao = descricao;
		this.status = status;
		this.icone = icone;
		
	}
		
	
	
	
	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(String unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public Integer getCasasDecimais() {
		return casasDecimais;
	}

	public void setCasasDecimais(Integer casasDecimais) {
		this.casasDecimais = casasDecimais;
	}

	public String getIcone() {
		return icone;
	}

	public void setIcone(String icone) {
		this.icone = icone;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sigla == null) ? 0 : sigla.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Enumerador other = (Enumerador) obj;
		if (sigla == null) {
			if (other.sigla != null) {
				return false;
			}
		} else if (!sigla.equals(other.sigla)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
