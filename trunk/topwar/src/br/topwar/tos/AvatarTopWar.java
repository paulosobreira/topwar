package br.topwar.tos;

import java.awt.Point;
import java.io.Serializable;

public class AvatarTopWar implements Serializable {
	private Point pontoAvatar;
	private double angulo;
	private int velocidade = 3;
	private String time;
	private String nomeJogador;

	public String getNomeJogador() {
		return nomeJogador;
	}

	public void setNomeJogador(String nomeJogador) {
		this.nomeJogador = nomeJogador;
	}

	public Point getPontoAvatar() {
		return pontoAvatar;
	}

	public void setPontoAvatar(Point pontoAvatar) {
		this.pontoAvatar = pontoAvatar;
	}

	public double getAngulo() {
		return angulo;
	}

	public void setAngulo(double angulo) {
		this.angulo = angulo;
	}

	public int getVelocidade() {
		return velocidade;
	}

	public void setVelocidade(int velocidade) {
		this.velocidade = velocidade;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
