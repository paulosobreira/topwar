package br.topwar.entidades;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class TopWarDados implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	@Column(nullable = false)
	private Date dataCriacao = new Date();

	@Column(nullable = false)
	private String loginCriador = "sistema";

	public String getLoginCriador() {
		return loginCriador;
	}

	public void setLoginCriador(String loginCriador) {
		this.loginCriador = loginCriador;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(int id) {
		this.id = new Long(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TopWarDados)) {
			return false;
		}
		if (id != null) {
			TopWarDados mesa11Dados = (TopWarDados) obj;
			return id.equals(mesa11Dados.getId());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
