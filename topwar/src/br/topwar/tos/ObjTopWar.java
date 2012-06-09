package br.topwar.tos;

import java.awt.Point;
import java.io.Serializable;

import javax.persistence.Transient;

import br.topwar.ConstantesTopWar;

public class ObjTopWar {
	private String time;
	private String nomeJogador;
	private Point pontoAvatar;
	private double angulo;
	private long tempoUtlAtaque;
	private int vida = 0;
	private int velocidade = 10;
	private int arma = ConstantesTopWar.ARMA_FACA;
	private int rangeUtlDisparo;
	private boolean invencivel;
	private transient String classe;
	private transient Point pontoUtlDisparo;
	private transient ObjTopWar mortoPor;
	private transient int balas = 0;
	private transient int cartuchos = 0;
	private transient int kills = 0;
	private transient int deaths = 0;
	private transient long ultimaRequisicao = System.currentTimeMillis();
	private transient long ultimaMorte;
	private transient long recarregar;
	private transient BotInfo botInfo;

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public int getRangeUtlDisparo() {
		return rangeUtlDisparo;
	}

	public void setRangeUtlDisparo(int rangeUtlDisparo) {
		this.rangeUtlDisparo = rangeUtlDisparo;
	}

	@Transient
	public BotInfo getBotInfo() {
		return botInfo;
	}

	public void setBotInfo(BotInfo botInfo) {
		this.botInfo = botInfo;
	}

	public boolean isInvencivel() {
		return invencivel;
	}

	public void setInvencivel(boolean invencivel) {
		this.invencivel = invencivel;
	}

	@Transient
	public ObjTopWar getMortoPor() {
		return mortoPor;
	}

	public void setMortoPor(ObjTopWar mortoPor) {
		this.mortoPor = mortoPor;
	}

	public int getArma() {
		return arma;
	}

	public void setArma(int arma) {
		switch (arma) {
		case ConstantesTopWar.ARMA_ASSAULT:
			setVelocidade(7);
			break;
		case ConstantesTopWar.ARMA_FACA:
			setVelocidade(10);
			break;
		case ConstantesTopWar.ARMA_MACHINEGUN:
			setVelocidade(5);
			break;
		case ConstantesTopWar.ARMA_ROCKET:
			setVelocidade(5);
			break;
		case ConstantesTopWar.ARMA_SHIELD:
			setVelocidade(10);
			break;
		case ConstantesTopWar.ARMA_SHOTGUN:
			setVelocidade(10);
			break;
		case ConstantesTopWar.ARMA_SNIPER:
			setVelocidade(5);
			break;
		default:
			setVelocidade(1);
			break;
		}
		this.arma = arma;
	}

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

	@Transient
	public Point getPontoUtlDisparo() {
		return pontoUtlDisparo;
	}

	@Transient
	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	@Transient
	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public void setPontoUtlDisparo(Point pontoUtlDisparo) {
		setTempoUtlAtaque(System.currentTimeMillis());
		this.pontoUtlDisparo = pontoUtlDisparo;
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

	public long getTempoUtlAtaque() {
		return tempoUtlAtaque;
	}

	public void setTempoUtlAtaque(long tempoUtlAtaque) {
		this.tempoUtlAtaque = tempoUtlAtaque;
	}

	public int getVida() {
		return vida;
	}

	public void setVida(int vida) {
		if (vida <= 0) {
			ultimaMorte = System.currentTimeMillis();
		}
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
		ObjTopWar other = (ObjTopWar) obj;
		if (nomeJogador == null) {
			if (other.nomeJogador != null)
				return false;
		} else if (!nomeJogador.equals(other.nomeJogador))
			return false;
		return true;
	}

}