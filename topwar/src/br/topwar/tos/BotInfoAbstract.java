package br.topwar.tos;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import br.nnpe.GeoUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.topwar.ConstantesTopWar;
import br.topwar.serial.ObjetoMapa;
import br.topwar.servidor.JogoServidor;

public abstract class BotInfoAbstract {

	public static String PATRULHANDO = "PATRULHANDO";
	public static String ATACANDO = "ATACANDO";
	public static String SEGUINDO = "SEGUINDO";
	protected ObjTopWar avatarTopWar;
	protected ObjTopWar seguindo;
	protected JogoServidor jogoServidor;
	protected Point pontoDestino;
	protected int contPatrulha;
	private int contGuia;
	private Point ultimaGuia;
	protected Point ptAtual;
	protected int contPtAtual;
	protected int vidaUltAlvo;
	protected String estado;
	private long tempoProcessaAcaoBot = 0;

	public long getTempoProcessaAcaoBot() {
		return tempoProcessaAcaoBot;
	}

	public void setTempoProcessaAcaoBot(long tempoProcessaAcaoBot) {
		if (this.tempoProcessaAcaoBot > tempoProcessaAcaoBot) {
			return;
		}
		this.tempoProcessaAcaoBot = tempoProcessaAcaoBot;
	}

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

	public ObjTopWar getSeguindo() {
		return seguindo;
	}

	public void setSeguindo(ObjTopWar seguindo) {
		this.seguindo = seguindo;
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

	public List<ObjTopWar> ordenaDistanciaAvatarCampoVisaoTiro(
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
			if (GeoUtil.distaciaEntrePontos(avatarTopWar.getPontoAvatar(),
					avatarTopWarCopia.getPontoAvatar()) > ConstantesTopWar.LIMITE_VISAO) {
				continue;
			}
			List<Point> line = GeoUtil.drawBresenhamLine(
					avatarTopWar.getPontoAvatar(),
					avatarTopWarCopia.getPontoAvatar());
			if (line.size() < ConstantesTopWar.LIMITE_VISAO
					&& jogoServidor.campoVisaoTiro(line, avatarTopWar)) {
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

	public List<ObjTopWar> avataresVisiveisMesmoTime(
			List<ObjTopWar> avatarTopWarsCopia, ObjTopWar avatarTopWar,
			JogoServidor jogoServidor) {
		List<ObjTopWar> avataresDistancia = new ArrayList<ObjTopWar>();
		for (Iterator iterator2 = avatarTopWarsCopia.iterator(); iterator2
				.hasNext();) {
			ObjTopWar avatarTopWarCopia = (ObjTopWar) iterator2.next();
			if (avatarTopWar.equals(avatarTopWarCopia)) {
				continue;
			}
			if (!avatarTopWar.getTime().equals(avatarTopWarCopia.getTime())
					|| avatarTopWarCopia.getVida() <= 0) {
				continue;
			}
			if (ConstantesTopWar.OBJ_ROCKET == avatarTopWarCopia.getArma()) {
				continue;
			}
			if (GeoUtil.distaciaEntrePontos(avatarTopWar.getPontoAvatar(),
					avatarTopWarCopia.getPontoAvatar()) > ConstantesTopWar.LIMITE_VISAO) {
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
				avataresDistancia.add(avatarTopWarCopia);
			}
		}
		return avataresDistancia;
	}

	public ObjTopWar avatarInfiltranteProximo(
			List<ObjTopWar> avatarTopWarsCopia, ObjTopWar avatarTopWar,
			JogoServidor jogoServidor) {
		List<ObjTopWar> avataresDistancia = avataresVisiveisMesmoTime(
				avatarTopWarsCopia, avatarTopWar, jogoServidor);
		if (avataresDistancia.size() > 5) {
			return null;
		}
		Collections.sort(avataresDistancia, new Comparator<ObjTopWar>() {
			@Override
			public int compare(ObjTopWar o1, ObjTopWar o2) {
				return new Double(o1.getDistanciaDeUmAvatar())
						.compareTo(new Double(o2.getDistanciaDeUmAvatar()));
			}
		});

		for (Iterator iterator = avataresDistancia.iterator(); iterator
				.hasNext();) {
			ObjTopWar objTopWar = (ObjTopWar) iterator.next();
			if (ConstantesTopWar.ASSAULT.equals(objTopWar.getClasse())
					|| (ConstantesTopWar.SHOTGUN.equals(objTopWar.getClasse()) || ConstantesTopWar.SHIELD
							.equals(objTopWar.getClasse()))) {
				return objTopWar;
			}
		}
		return null;
	}

	private void segueAvatarInfiltrante() {
		if (Math.random() < (tempoProcessaAcaoBot / 100.0)) {
			return;
		}
		List<ObjTopWar> avatarTopWarsCopia = jogoServidor
				.getAvatarTopWarsCopia();
		ObjTopWar avatarInfiltranteProximo = avatarInfiltranteProximo(
				avatarTopWarsCopia, avatarTopWar, jogoServidor);
		if (avatarInfiltranteProximo != null) {
			setPontoDestino(avatarInfiltranteProximo.getPontoAvatar());
			setSeguindo(avatarInfiltranteProximo);
			setEstado(SEGUINDO);
		}
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

	/**
	 * Patrulhando
	 */
	protected void patrulhar() {
		if (getPontoDestino() != null) {
			return;
		}
		if (vaiSeguirInfiltrar()) {
			segueAvatarInfiltrante();
		}
		if (getPontoDestino() != null) {
			return;
		}
		if (vaiGuia()) {
			List<ObjetoMapa> objetoMapaList = jogoServidor.getMapaTopWar()
					.getObjetoMapaList();
			ArrayList<Point> canidatos = new ArrayList<Point>();
			for (Iterator iterator2 = objetoMapaList.iterator(); iterator2
					.hasNext();) {
				ObjetoMapa objetoMapa = (ObjetoMapa) iterator2.next();
				if (!ConstantesTopWar.BOT_GUIA.equals(objetoMapa.getEfeito())) {
					continue;
				}
				Point analizar = objetoMapa.getForma().getBounds()
						.getLocation();
				if (analizar.equals(getUltimaGuia())) {
					continue;
				}
				if (GeoUtil.distaciaEntrePontos(avatarTopWar.getPontoAvatar(),
						analizar) < ConstantesTopWar.LIMITE_VISAO) {
					List<Point> drawBresenhamLine = GeoUtil.drawBresenhamLine(
							avatarTopWar.getPontoAvatar(), analizar);
					if (ultimaGuia == null
							|| jogoServidor.campoVisao(drawBresenhamLine,
									avatarTopWar, true)
							&& drawBresenhamLine.size() > avatarTopWar
									.getVelocidade()) {
						canidatos.add(analizar);
					}
				}
			}
			if (!canidatos.isEmpty()) {

				Collections.shuffle(canidatos);
				Point point = canidatos.get(Util.intervalo(0,
						canidatos.size() - 1));
				setPontoDestino(point);
				setUltimaGuia(point);
			} else {
				botVaiPontoAleatorio();
			}

		} else if (vaiBaseInimiga()) {
			if (avatarTopWar.getTime() == ConstantesTopWar.TIME_VERMELHO) {
				setPontoDestino(jogoServidor.getMapaTopWar().getPontoTimeAzul());
			} else {
				setPontoDestino(jogoServidor.getMapaTopWar()
						.getPontoTimeVermelho());
			}
		} else {
			botVaiPontoAleatorio();
		}
		setEstado(BotInfoAssault.PATRULHANDO);
	}

	protected boolean vaiSeguirInfiltrar() {
		if (ultimaGuia == null) {
			return false;
		}
		if (seguindo == null || seguindo.getVida() < 0) {
			return true;
		}
		if (GeoUtil.distaciaEntrePontos(avatarTopWar.getPontoAvatar(),
				seguindo.getPontoAvatar()) > ConstantesTopWar.LIMITE_VISAO) {
			return true;
		}
		return false;
	}

	protected boolean atacaComFaca(ObjTopWar avatarTopWarCopia) {
		if (avatarTopWar.getArma() != ConstantesTopWar.ARMA_FACA)
			jogoServidor.alternarFaca(avatarTopWar);
		boolean executouAcaoAtaque;
		avatarTopWar.setAngulo(GeoUtil.calculaAngulo(
				avatarTopWar.getPontoAvatar(),
				avatarTopWarCopia.getPontoAvatar(), 90));
		jogoServidor.atacar(avatarTopWar, avatarTopWar.getAngulo(), 0);
		executouAcaoAtaque = true;
		jogoServidor.alternarFaca(avatarTopWar);
		return executouAcaoAtaque;
	}

	protected void moverBot() {
		patrulhar();
		List<Point> lineMove = GeoUtil.drawBresenhamLine(
				avatarTopWar.getPontoAvatar(), getPontoDestino());
		if (lineMove.size() < avatarTopWar.getVelocidade()) {
			setPontoDestino(null);
			setEstado(null);
		} else {
			Point dstMover = lineMove.get(avatarTopWar.getVelocidade() - 1);
			DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
			acaoClienteTopWar.setPonto(dstMover);
			acaoClienteTopWar.setAngulo(GeoUtil.calculaAngulo(
					avatarTopWar.getPontoAvatar(), dstMover, 90));
			if (jogoServidor.verificaAndavel(avatarTopWar.getPontoAvatar(),
					dstMover)) {
				String mover = (String) jogoServidor.moverPontoAvatar(
						avatarTopWar, acaoClienteTopWar);
			} else {
				setPontoDestino(null);
			}
		}
	}

}