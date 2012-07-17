package br.topwar.tos;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import br.nnpe.GeoUtil;
import br.nnpe.Util;
import br.topwar.ConstantesTopWar;
import br.topwar.servidor.JogoServidor;

public abstract class BotInfoAbstract {

	public static String PATRULHANDO = "PATRULHANDO";
	public static String ATACANDO = "ATACANDO";
	public static String SEGUINDO = "SEGUINDO";
	protected ObjTopWar avatarTopWar;
	protected JogoServidor jogoServidor;
	protected Point pontoDestino;
	protected int contPatrulha;
	protected int contGuia;
	protected Point ultimaGuia;
	protected Point ptAtual;
	protected int contPtAtual;
	protected int vidaUltAlvo;
	protected String estado;

	public abstract void processaAcaoBot();

	protected abstract boolean seguirAtacarInimigo(
			List<ObjTopWar> avatarTopWarsCopia, boolean executouAcaoAtaque);

	public Point getPontoDestino() {
		return pontoDestino;
	}

	public void setPontoDestino(Point pontoDestino) {
		this.pontoDestino = pontoDestino;
	}

	public ObjTopWar getAvatarTopWar() {
		return avatarTopWar;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public void setAvatarTopWar(ObjTopWar avatarTopWar) {
		this.avatarTopWar = avatarTopWar;
	}

	public JogoServidor getJogoServidor() {
		return jogoServidor;
	}

	public void setJogoServidor(JogoServidor jogoServidor) {
		this.jogoServidor = jogoServidor;
	}

	public int getContPatrulha() {
		return contPatrulha;
	}

	public void setContPatrulha(int contPatrulha) {
		this.contPatrulha = contPatrulha;
	}

	public int getContGuia() {
		return contGuia;
	}

	public void setContGuia(int contGuia) {
		this.contGuia = contGuia;
	}

	public Point getUltimaGuia() {
		return ultimaGuia;
	}

	public void setUltimaGuia(Point ultimaGuia) {
		this.ultimaGuia = ultimaGuia;
	}

	public Point getPtAtual() {
		return ptAtual;
	}

	public void setPtAtual(Point ptAtual) {
		this.ptAtual = ptAtual;
	}

	public int getContPtAtual() {
		return contPtAtual;
	}

	public void setContPtAtual(int contPtAtual) {
		this.contPtAtual = contPtAtual;
	}

	public int getVidaUltAlvo() {
		return vidaUltAlvo;
	}

	public void setVidaUltAlvo(int vidaUltAlvo) {
		this.vidaUltAlvo = vidaUltAlvo;
	}

	public List<ObjTopWar> ordenaDistanciaAvatar(
			List<ObjTopWar> avatarTopWarsCopia, ObjTopWar avatarTopWar,
			JogoServidor jogoServidor) {
		List<ObjTopWar> avataresOrdenadosDistancia = new ArrayList<ObjTopWar>();
		for (Iterator iterator2 = avatarTopWarsCopia.iterator(); iterator2
				.hasNext();) {
			ObjTopWar avatarTopWarCopia = (ObjTopWar) iterator2.next();
			if (avatarTopWar.getTime().equals(avatarTopWarCopia.getTime())
					|| avatarTopWarCopia.getVida() <= 0) {
				continue;
			}
			if (ConstantesTopWar.OBJ_ROCKET == avatarTopWarCopia.getArma()) {
				continue;
			}
			List<Point> line = GeoUtil.drawBresenhamLine(
					avatarTopWar.getPontoAvatar(),
					avatarTopWarCopia.getPontoAvatar());
			if (jogoServidor.campoVisao(line, avatarTopWar, true)) {
				avatarTopWarCopia.setDistanciaDeUmAvatar(GeoUtil
						.distaciaEntrePontos(
								avatarTopWarCopia.getPontoAvatar(),
								avatarTopWar.getPontoAvatar()));
				avataresOrdenadosDistancia.add(avatarTopWarCopia);
			}
		}
		Collections.sort(avataresOrdenadosDistancia,
				new Comparator<ObjTopWar>() {
					@Override
					public int compare(ObjTopWar o1, ObjTopWar o2) {
						return new Double(o1.getDistanciaDeUmAvatar())
								.compareTo(new Double(o2
										.getDistanciaDeUmAvatar()));
					}
				});
		return avataresOrdenadosDistancia;
	}

	public List<ObjTopWar> ordenaDistanciaAvatarMesmoTime(
			List<ObjTopWar> avatarTopWarsCopia, ObjTopWar avatarTopWar,
			JogoServidor jogoServidor) {
		List<ObjTopWar> avataresOrdenadosDistancia = new ArrayList<ObjTopWar>();
		for (Iterator iterator2 = avatarTopWarsCopia.iterator(); iterator2
				.hasNext();) {
			ObjTopWar avatarTopWarCopia = (ObjTopWar) iterator2.next();
			if (!avatarTopWar.getTime().equals(avatarTopWarCopia.getTime())
					|| avatarTopWarCopia.getVida() <= 0) {
				continue;
			}
			if (ConstantesTopWar.OBJ_ROCKET == avatarTopWarCopia.getArma()) {
				continue;
			}
			List<Point> line = GeoUtil.drawBresenhamLine(
					avatarTopWar.getPontoAvatar(),
					avatarTopWarCopia.getPontoAvatar());
			if (jogoServidor.campoVisao(line, avatarTopWar, true)) {
				avatarTopWarCopia.setDistanciaDeUmAvatar(GeoUtil
						.distaciaEntrePontos(
								avatarTopWarCopia.getPontoAvatar(),
								avatarTopWar.getPontoAvatar()));
				avataresOrdenadosDistancia.add(avatarTopWarCopia);
			}
		}
		Collections.sort(avataresOrdenadosDistancia,
				new Comparator<ObjTopWar>() {
					@Override
					public int compare(ObjTopWar o1, ObjTopWar o2) {
						return new Double(o1.getDistanciaDeUmAvatar())
								.compareTo(new Double(o2
										.getDistanciaDeUmAvatar()));
					}
				});
		return avataresOrdenadosDistancia;
	}

	public Point avatarInfiltranteProximo(List<ObjTopWar> avatarTopWarsCopia,
			ObjTopWar avatarTopWar, JogoServidor jogoServidor) {
		List<ObjTopWar> avataresOrdenadosDistancia = ordenaDistanciaAvatarMesmoTime(
				avatarTopWarsCopia, avatarTopWar, jogoServidor);
		if (avataresOrdenadosDistancia.size() > 1) {
			return null;
		}
		for (Iterator iterator = avataresOrdenadosDistancia.iterator(); iterator
				.hasNext();) {
			ObjTopWar objTopWar = (ObjTopWar) iterator.next();
			if (ConstantesTopWar.ASSAULT.equals(objTopWar.getClasse())
					|| (ConstantesTopWar.SHOTGUN.equals(objTopWar.getClasse()) || ConstantesTopWar.SHIELD
							.equals(objTopWar.getClasse()))) {
				return objTopWar.getPontoAvatar();
			}
		}
		return null;
	}

	public boolean segueAvatarInfiltrante() {
		if (SEGUINDO.equals(getEstado())) {
			return false;
		}
		List<ObjTopWar> avatarTopWarsCopia = jogoServidor
				.getAvatarTopWarsCopia();
		List<ObjTopWar> ordenaDistanciaAvatar = ordenaDistanciaAvatar(
				avatarTopWarsCopia, avatarTopWar, jogoServidor);
		if (!ordenaDistanciaAvatar.isEmpty()) {
			return false;
		}
		Point avatarInfiltranteProximo = avatarInfiltranteProximo(
				avatarTopWarsCopia, avatarTopWar, jogoServidor);
		if (avatarInfiltranteProximo != null) {
			setPontoDestino(avatarInfiltranteProximo);
			setEstado(SEGUINDO);
		}
		return false;
	}

	public void botVaiPontoAleatorio() {
		Point calculaPonto = GeoUtil.calculaPonto(Util.intervalo(0, 360),
				Util.intervalo(100, 200), avatarTopWar.getPontoAvatar());
		while (!jogoServidor.verificaAndavel(avatarTopWar.getPontoAvatar(),
				calculaPonto)) {
			calculaPonto = GeoUtil.calculaPonto(Util.intervalo(0, 360), 100,
					avatarTopWar.getPontoAvatar());
		}
		setPontoDestino(calculaPonto);
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