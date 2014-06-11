package br.topwar.tos;

import java.io.Serializable;

public class DadosJogoTopWar implements Serializable {

	private String nomeJogo;

	private String nomeJogador;

	private String nomeMapa;

	private String classe;

	private Integer numBots;

	private Integer tempoJogo;

	private boolean botsVsHumans;

	public Integer getTempoJogo() {
		return tempoJogo;
	}

	public void setTempoJogo(Integer tempoJogo) {
		this.tempoJogo = tempoJogo;
	}

	public Integer getNumBots() {
		return numBots;
	}

	public void setNumBots(Integer numBots) {
		this.numBots = numBots;
	}

	public boolean isBotsVsHumans() {
		return botsVsHumans;
	}

	public void setBotsVsHumans(boolean botsVsHumans) {
		this.botsVsHumans = botsVsHumans;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

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
