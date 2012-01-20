package br.topwar.tos;

import java.io.Serializable;

public class DadosJogoTopWar implements Serializable {

	private String nomeJogo;
	
	private String nomeMapa;

	public String getNomeJogo() {
		return nomeJogo;
	}

	public void setNomeJogo(String nomeJogo) {
		this.nomeJogo = nomeJogo;
	}

}
