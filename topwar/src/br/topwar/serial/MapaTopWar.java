package br.topwar.serial;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapaTopWar implements Serializable {

	private static final long serialVersionUID = -6506355168057643733L;
	private String nome;
	private String backGround;
	private Point pontoTimeVermelho;
	private Point pontoTimeAzul;

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

	public Point getPontoTimeVermelho() {
		return pontoTimeVermelho;
	}

	public void setPontoTimeVermelho(Point pontoTimeVermelho) {
		this.pontoTimeVermelho = pontoTimeVermelho;
	}

	public Point getPontoTimeAzul() {
		return pontoTimeAzul;
	}

	public void setPontoTimeAzul(Point pontoTimeAzul) {
		this.pontoTimeAzul = pontoTimeAzul;
	}

}
