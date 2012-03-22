package br.topwar.tos;

import java.awt.Point;

public class BotInfo {

	public static String PATRULHANDO = "PATRULHANDO";
	public static String ATACANDO = "ATACANDO";
	private int contPatrulha;

	private String estado;

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	private Point pontoDestino;

	public Point getPontoDestino() {
		return pontoDestino;
	}

	public void setPontoDestino(Point pontoDestino) {
		this.pontoDestino = pontoDestino;
	}

	public boolean vaiBaseInimiga() {
		if (contPatrulha < 10) {
			contPatrulha++;
			return false;
		}
		contPatrulha = 0;
		return true;
	}

}
