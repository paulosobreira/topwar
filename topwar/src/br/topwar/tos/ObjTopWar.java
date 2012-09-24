package br.topwar.tos;

import java.awt.Point;

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
	private transient double distanciaDeUmAvatar;
	private transient BotInfoAbstract botInfo;
	private transient String proxClasse;

	public double getDistanciaDeUmAvatar() {
		return distanciaDeUmAvatar;
	}

	public String getProxClasse() {
		return proxClasse;
	}

	public void setProxClasse(String proxClasse) {
		this.proxClasse = proxClasse;
	}

	public void setDistanciaDeUmAvatar(double distanciaDeUmAvatar) {
		this.distanciaDeUmAvatar = distanciaDeUmAvatar;
	}

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
	public BotInfoAbstract getBotInfo() {
		return botInfo;
	}

	public void setBotInfo(BotInfoAbstract botInfo) {
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
			setVelocidade(ConstantesTopWar.VELOCIDADE_ASSAUT);
			break;
		case ConstantesTopWar.ARMA_FACA:
			setVelocidade(ConstantesTopWar.VELOCIDADE_FACA);
			break;
		case ConstantesTopWar.ARMA_MACHINEGUN:
			setVelocidade(ConstantesTopWar.VELOCIDADE_MACHINEGUN);
			break;
		case ConstantesTopWar.ARMA_ROCKET:
			setVelocidade(ConstantesTopWar.VELOCIDADE_ROCKET);
			break;
		case ConstantesTopWar.ARMA_SHIELD:
			setVelocidade(ConstantesTopWar.VELOCIDADE_SHIELD);
			break;
		case ConstantesTopWar.ARMA_SHOTGUN:
			setVelocidade(ConstantesTopWar.VELOCIDADE_SHOTGUN);
			break;
		case ConstantesTopWar.ARMA_SNIPER:
			setVelocidade(ConstantesTopWar.VELOCIDADE_SNIPER);
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

	@Transient
	public void setupCalsseJogador() {
		if (proxClasse != null) {
			setClasse(proxClasse);
			setProxClasse(null);
		}
		if (ConstantesTopWar.ASSAULT.equals(getClasse())) {
			setArma(ConstantesTopWar.ARMA_ASSAULT);
			setBalas(ConstantesTopWar.BALAS_ASSALT);
			setCartuchos(ConstantesTopWar.CARTUCHOS_ASSALT);
			setVida(ConstantesTopWar.VIDA_COMPLETA_ASSALT);
		} else if (ConstantesTopWar.SHOTGUN.equals(getClasse())) {
			setArma(ConstantesTopWar.ARMA_SHOTGUN);
			setBalas(ConstantesTopWar.BALAS_SHOTGUN);
			setCartuchos(ConstantesTopWar.CARTUCHOS_SHOTGUN);
			setVida(ConstantesTopWar.VIDA_COMPLETA_SHOTGUN);
		} else if (ConstantesTopWar.SNIPER.equals(getClasse())) {
			setArma(ConstantesTopWar.ARMA_SNIPER);
			setBalas(ConstantesTopWar.BALAS_SNIPER);
			setCartuchos(ConstantesTopWar.CARTUCHOS_SNIPER);
			setVida(ConstantesTopWar.VIDA_COMPLETA_SNIPER);
		} else if (ConstantesTopWar.MACHINEGUN.equals(getClasse())) {
			setArma(ConstantesTopWar.ARMA_MACHINEGUN);
			setBalas(ConstantesTopWar.BALAS_MACHINEGUN);
			setCartuchos(ConstantesTopWar.CARTUCHOS_MACHINEGUN);
			setVida(ConstantesTopWar.VIDA_COMPLETA_MACHINEGUN);
		} else if (ConstantesTopWar.ROCKET.equals(getClasse())) {
			setArma(ConstantesTopWar.ARMA_ROCKET);
			setBalas(ConstantesTopWar.BALAS_ROCKET);
			setCartuchos(ConstantesTopWar.CARTUCHOS_ROCKET);
			setVida(ConstantesTopWar.VIDA_COMPLETA_ROCKET);
		} else if (ConstantesTopWar.SHIELD.equals(getClasse())) {
			setArma(ConstantesTopWar.ARMA_SHIELD);
			setVida(ConstantesTopWar.VIDA_COMPLETA_SHIELD);
			setBalas(0);
			setCartuchos(0);
		}
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

	@Transient
	public boolean verificaObj() {
		return ConstantesTopWar.OBJ_ROCKET == getArma();
	}

}
