package br.topwar.cliente;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import br.nnpe.ImageUtil;
import br.topwar.tos.AvatarTopWar;

public class AvatarCliente {
	private boolean local;
	private int quadroAnimacao;
	private AvatarTopWar avatarTopWar;

	public int getQuadroAnimacao() {
		return quadroAnimacao;
	}

	public void setQuadroAnimacao(int quadroAnimacao) {
		if (quadroAnimacao > 3) {
			quadroAnimacao = 0;
		}
		this.quadroAnimacao = quadroAnimacao;
	}

	public AvatarCliente(String time, AvatarTopWar avatarTopWar) {
		this.avatarTopWar = avatarTopWar;
		setTime(time);
	}

	public boolean equals(Object obj) {
		return avatarTopWar.equals(obj);
	}

	public double getAngulo() {
		return avatarTopWar.getAngulo();
	}

	public String getNomeJogador() {
		return avatarTopWar.getNomeJogador();
	}

	public Point getPontoAvatar() {
		return avatarTopWar.getPontoAvatar();
	}

	public String getTime() {
		return avatarTopWar.getTime();
	}

	public int getVelocidade() {
		return avatarTopWar.getVelocidade();
	}

	public int hashCode() {
		return avatarTopWar.hashCode();
	}

	public void setAngulo(double angulo) {
		avatarTopWar.setAngulo(angulo);
	}

	public void setNomeJogador(String nomeJogador) {
		avatarTopWar.setNomeJogador(nomeJogador);
	}

	public void setPontoAvatar(Point pontoAvatar) {
		avatarTopWar.setPontoAvatar(pontoAvatar);
	}

	public void setTime(String time) {
		avatarTopWar.setTime(time);
	}

	public void setVelocidade(int velocidade) {
		avatarTopWar.setVelocidade(velocidade);
	}

	public String toString() {
		return avatarTopWar.toString();
	}

	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}

}
