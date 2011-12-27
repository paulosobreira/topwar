package br.topwar.serial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapaTopWar implements Serializable {

	private String nome;
	private String backGround;
	private List<ObjetoMapa> objetoMapaList = new ArrayList<ObjetoMapa>();

	public MapaTopWar() {
	}
	
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

	public List<ObjetoMapa> getObjetoMapaList() {
		return objetoMapaList;
	}

	public void setObjetoMapaList(List<ObjetoMapa> objetoMapaList) {
		this.objetoMapaList = objetoMapaList;
	}

}
