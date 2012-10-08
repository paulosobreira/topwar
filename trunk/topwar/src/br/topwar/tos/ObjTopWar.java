package br.topwar.tos;

import java.awt.Point;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

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
	private String classe;
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

	public char charAt(int index) {
		return classe.charAt(index);
	}

	public int codePointAt(int index) {
		return classe.codePointAt(index);
	}

	public int codePointBefore(int index) {
		return classe.codePointBefore(index);
	}

	public int codePointCount(int beginIndex, int endIndex) {
		return classe.codePointCount(beginIndex, endIndex);
	}

	public int compareTo(String anotherString) {
		return classe.compareTo(anotherString);
	}

	public int compareToIgnoreCase(String str) {
		return classe.compareToIgnoreCase(str);
	}

	public String concat(String str) {
		return classe.concat(str);
	}

	public boolean contains(CharSequence s) {
		return classe.contains(s);
	}

	public boolean contentEquals(CharSequence cs) {
		return classe.contentEquals(cs);
	}

	public boolean contentEquals(StringBuffer sb) {
		return classe.contentEquals(sb);
	}

	public boolean endsWith(String suffix) {
		return classe.endsWith(suffix);
	}

	public boolean equalsIgnoreCase(String anotherString) {
		return classe.equalsIgnoreCase(anotherString);
	}

	public byte[] getBytes() {
		return classe.getBytes();
	}

	public byte[] getBytes(Charset charset) {
		return classe.getBytes(charset);
	}

	public void getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin) {
		classe.getBytes(srcBegin, srcEnd, dst, dstBegin);
	}

	public byte[] getBytes(String charsetName)
			throws UnsupportedEncodingException {
		return classe.getBytes(charsetName);
	}

	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		classe.getChars(srcBegin, srcEnd, dst, dstBegin);
	}

	public int indexOf(int ch, int fromIndex) {
		return classe.indexOf(ch, fromIndex);
	}

	public int indexOf(int ch) {
		return classe.indexOf(ch);
	}

	public int indexOf(String str, int fromIndex) {
		return classe.indexOf(str, fromIndex);
	}

	public int indexOf(String str) {
		return classe.indexOf(str);
	}

	public String intern() {
		return classe.intern();
	}

	public boolean isEmpty() {
		return classe.isEmpty();
	}

	public int lastIndexOf(int ch, int fromIndex) {
		return classe.lastIndexOf(ch, fromIndex);
	}

	public int lastIndexOf(int ch) {
		return classe.lastIndexOf(ch);
	}

	public int lastIndexOf(String str, int fromIndex) {
		return classe.lastIndexOf(str, fromIndex);
	}

	public int lastIndexOf(String str) {
		return classe.lastIndexOf(str);
	}

	public int length() {
		return classe.length();
	}

	public boolean matches(String regex) {
		return classe.matches(regex);
	}

	public int offsetByCodePoints(int index, int codePointOffset) {
		return classe.offsetByCodePoints(index, codePointOffset);
	}

	public boolean regionMatches(boolean ignoreCase, int toffset, String other,
			int ooffset, int len) {
		return classe.regionMatches(ignoreCase, toffset, other, ooffset, len);
	}

	public boolean regionMatches(int toffset, String other, int ooffset, int len) {
		return classe.regionMatches(toffset, other, ooffset, len);
	}

	public String replace(char oldChar, char newChar) {
		return classe.replace(oldChar, newChar);
	}

	public String replace(CharSequence target, CharSequence replacement) {
		return classe.replace(target, replacement);
	}

	public String replaceAll(String regex, String replacement) {
		return classe.replaceAll(regex, replacement);
	}

	public String replaceFirst(String regex, String replacement) {
		return classe.replaceFirst(regex, replacement);
	}

	public String[] split(String regex, int limit) {
		return classe.split(regex, limit);
	}

	public String[] split(String regex) {
		return classe.split(regex);
	}

	public boolean startsWith(String prefix, int toffset) {
		return classe.startsWith(prefix, toffset);
	}

	public boolean startsWith(String prefix) {
		return classe.startsWith(prefix);
	}

	public CharSequence subSequence(int beginIndex, int endIndex) {
		return classe.subSequence(beginIndex, endIndex);
	}

	public String substring(int beginIndex, int endIndex) {
		return classe.substring(beginIndex, endIndex);
	}

	public String substring(int beginIndex) {
		return classe.substring(beginIndex);
	}

	public char[] toCharArray() {
		return classe.toCharArray();
	}

	public String toLowerCase() {
		return classe.toLowerCase();
	}

	public String toLowerCase(Locale locale) {
		return classe.toLowerCase(locale);
	}

	public String toString() {
		return classe.toString();
	}

	public String toUpperCase() {
		return classe.toUpperCase();
	}

	public String toUpperCase(Locale locale) {
		return classe.toUpperCase(locale);
	}

	public String trim() {
		return classe.trim();
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

	@Transient
	public int codClasse() {
		if (ConstantesTopWar.ASSAULT.equals(getClasse())) {
			return 0;
		} else if (ConstantesTopWar.SHOTGUN.equals(getClasse())) {
			return 1;
		} else if (ConstantesTopWar.SNIPER.equals(getClasse())) {
			return 2;
		} else if (ConstantesTopWar.MACHINEGUN.equals(getClasse())) {
			return 3;
		} else if (ConstantesTopWar.ROCKET.equals(getClasse())) {
			return 4;
		} else if (ConstantesTopWar.SHIELD.equals(getClasse())) {
			return 5;
		}
		return 0;
	}

	@Transient
	public void setCodClasse(int codClasse) {
		switch (codClasse) {
		case 0:
			setClasse(ConstantesTopWar.ASSAULT);
			break;
		case 1:
			setClasse(ConstantesTopWar.SHOTGUN);
			break;
		case 2:
			setClasse(ConstantesTopWar.SNIPER);
			break;
		case 3:
			setClasse(ConstantesTopWar.MACHINEGUN);
			break;
		case 4:
			setClasse(ConstantesTopWar.ROCKET);
			break;
		case 5:
			setClasse(ConstantesTopWar.SHIELD);
			break;
		default:
			break;
		}

	}

}
