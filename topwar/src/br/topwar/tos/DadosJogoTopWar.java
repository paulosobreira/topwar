package br.topwar.tos;

import java.io.Serializable;

public class DadosJogoTopWar implements Serializable {

	private String nomeJogo;

	private String nomeJogador;

	private String nomeMapa;

	public String getNomeJogo() {
		return nomeJogo;
	}

	public String getNomeJogador() {
		return nomeJogador;
	}

	public void setNomeJogador(String nomeJogador) {
		this.nomeJogador = nomeJogador;
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
