package br.topwar.serial;

import java.io.Serializable;

public class MapaTopWar implements Serializable {

	private String nome;
	private String backGround;

	public String getBackGround() {
		return backGround;
	}

	public void setBackGround(String backGround) {
		this.backGround = backGround;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
