package br.topwar.tos;

import java.awt.Point;

public class BotInfo {

	public static String PATRULHANDO = "PATRULHANDO";
	public static String ATACANDO = "ATACANDO";
	private int contPatrulha;
	private int contGuia;
	private Point ultimaGuia;

	public Point getUltimaGuia() {
		return ultimaGuia;
	}

	public void setUltimaGuia(Point ultimaGuia) {
		this.ultimaGuia = ultimaGuia;
	}

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

	public boolean vaiGuia() {
		if (ultimaGuia == null) {
			return true;
		}
		if (contGuia < 1) {
			contGuia++;
			return false;
		}
		contGuia = 0;
		return true;
	}

	public boolean vaiBaseInimiga() {
		if (contPatrulha < 5) {
			contPatrulha++;
			return false;
		}
		contPatrulha = 0;
		return true;
	}

}
