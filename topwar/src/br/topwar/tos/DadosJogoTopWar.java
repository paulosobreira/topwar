package br.topwar.tos;

import java.io.Serializable;

public class DadosJogoTopWar implements Serializable {

	private String nomeJogo;

	private String nomeCriadorJogo;

	private String nomeMapa;

	public String getNomeJogo() {
		return nomeJogo;
	}

	public String getNomeCriadorJogo() {
		return nomeCriadorJogo;
	}

	public void setNomeCriadorJogo(String nomeCriadorJogo) {
		this.nomeCriadorJogo = nomeCriadorJogo;
	}

	public void setNomeJogo(String nomeJogo) {
		this.nomeJogo = nomeJogo;
	}

	public String getNomeMapa() {
		return nomeMapa;
	}

	public void setNomeMapa(String nomeMapa) {
		this.nomeMapa = nomeMapa;
	}

}
