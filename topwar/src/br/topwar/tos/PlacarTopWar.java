package br.topwar.tos;

import java.io.Serializable;

public class PlacarTopWar implements Serializable {

	private String jogador;
	private String time;
	private int kills;
	private int deaths;

	public Integer ordenacao() {
		return kills - deaths;
	}

	public String getJogador() {
		return jogador;
	}

	public void setJogador(String jogador) {
		this.jogador = jogador;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

}
