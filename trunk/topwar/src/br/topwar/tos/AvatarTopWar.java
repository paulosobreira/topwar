package br.topwar.tos;

import java.awt.Point;
import java.io.Serializable;

import javax.persistence.Transient;

public class AvatarTopWar implements Serializable {
	private String time;
	private String nomeJogador;
	private Point pontoAvatar;
	private double angulo;
	private long tempoUtlDisparo;
	private int vida = 100;
	private int velocidade = 12;
	private transient Point pontoUtlDisparo;
	private transient int balas = 50;
	private transient int cartuchos = 3;
	private transient long ultimaRequisicao = System.currentTimeMillis();
	private transient long ultimaMorte;
	private transient long recarregar;

	@Transient
	public long getRecarregar() {
		return recarregar;
	}

	public void setRecarregar(long recarregar) {
		this.recarregar = recarregar;
	}

	@Transient
	public long getUltimaMorte() {
		return ultimaMorte;
	}

	public Point getPontoUtlDisparo() {
		return pontoUtlDisparo;
	}

	public void setPontoUtlDisparo(Point pontoUtlDisparo) {
		setTempoUtlDisparo(System.currentTimeMillis());
		this.pontoUtlDisparo = pontoUtlDisparo;
	}

	@Transient
	public void setUltimaMorte(long ultimaMorte) {
		this.ultimaMorte = ultimaMorte;
	}

	@Transient
	public long getUltimaRequisicao() {
		return ultimaRequisicao;
	}

	@Transient
	public void setUltimaRequisicao(long lastRequest) {
		this.ultimaRequisicao = lastRequest;
	}

	public int getBalas() {
		return balas;
	}

	public void setBalas(int balas) {
		this.balas = balas;
	}

	public int getCartuchos() {
		return cartuchos;
	}

	public void setCartuchos(int cartuchos) {
		this.cartuchos = cartuchos;
	}

	public long getTempoUtlDisparo() {
		return tempoUtlDisparo;
	}

	public void setTempoUtlDisparo(long tempoUtlDisparo) {
		this.tempoUtlDisparo = tempoUtlDisparo;
	}

	public int getVida() {
		return vida;
	}

	public void setVida(int vida) {
		this.vida = vida;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((nomeJogador == null) ? 0 : nomeJogador.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AvatarTopWar other = (AvatarTopWar) obj;
		if (nomeJogador == null) {
			if (other.nomeJogador != null)
				return false;
		} else if (!nomeJogador.equals(other.nomeJogador))
			return false;
		return true;
	}

}
