package br.topwar.tos;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.nnpe.GeoUtil;
import br.nnpe.Util;
import br.topwar.ConstantesTopWar;
import br.topwar.serial.ObjetoMapa;
import br.topwar.servidor.JogoServidor;

public class BotInfoShield extends BotInfoAbstract {

	public static String PATRULHANDO = "PATRULHANDO";
	public static String ATACANDO = "ATACANDO";
	private int contPatrulha;
	private int contGuia;
	private Point ultimaGuia;
	private Point ptAtual;
	private int contPtAtual;
	private ObjTopWar avatarTopWar;
	private JogoServidor jogoServidor;
	private int vidaUltAlvo;

	public BotInfoShield(ObjTopWar bot, JogoServidor jogoServidor) {
		this.avatarTopWar = bot;
		this.jogoServidor = jogoServidor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.topwar.tos.BotInfoIface#processaAcaoBot()
	 */
	@Override
	public void processaAcaoBot() {
		Point pontoAvatar = avatarTopWar.getPontoAvatar();
		if (pontoAvatar.equals(ptAtual)) {
			contPtAtual++;
		} else {
			contPtAtual = 0;
		}
		ptAtual = pontoAvatar;
		List<ObjTopWar> avatarTopWarsCopia = jogoServidor
				.getAvatarTopWarsCopia();
		boolean executouAcaoAtaque = false;
		if (contPtAtual < 50) {
			executouAcaoAtaque = seguirAtacarInimigo(avatarTopWarsCopia,
					executouAcaoAtaque);
		}
		if (!executouAcaoAtaque) {
			patrulhar();
			List<Point> lineMove = GeoUtil.drawBresenhamLine(
					avatarTopWar.getPontoAvatar(), getPontoDestino());
			if (lineMove.size() < avatarTopWar.getVelocidade()) {
				setPontoDestino(null);
			} else {
				Point dstMover = lineMove.get(avatarTopWar.getVelocidade() - 1);
				DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
				acaoClienteTopWar.setPonto(dstMover);
				acaoClienteTopWar.setAngulo(GeoUtil.calculaAngulo(
						avatarTopWar.getPontoAvatar(), dstMover, 90));
				String mover = (String) jogoServidor.moverPontoAvatar(
						avatarTopWar, acaoClienteTopWar);
				if (!ConstantesTopWar.OK.equals(mover)) {
					setPontoDestino(null);
				}
			}
		}

	}

	/**
	 * Patrulhando
	 */
	private void patrulhar() {
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
				List<Point> drawBresenhamLine = GeoUtil.drawBresenhamLine(
						avatarTopWar.getPontoAvatar(), analizar);
				if (jogoServidor.campoVisao(drawBresenhamLine, avatarTopWar,
						true)
						&& drawBresenhamLine.size() > avatarTopWar
								.getVelocidade()) {
					canidatos.add(analizar);
				}
			}
			if (!canidatos.isEmpty()) {
				Collections.shuffle(canidatos);
				Point point = canidatos.get(0);
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
		setEstado(BotInfoShield.PATRULHANDO);
	}

	/**
	 * Seguir/Atacar avatar inimigo
	 */
	private boolean seguirAtacarInimigo(List<ObjTopWar> avatarTopWarsCopia,
			boolean executouAcaoAtaque) {
		List<ObjTopWar> avataresOrdenadosDistancia = ordenaDistanciaAvatar(
				avatarTopWarsCopia, avatarTopWar, jogoServidor);
		for (Iterator iterator2 = avataresOrdenadosDistancia.iterator(); iterator2
				.hasNext();) {
			ObjTopWar avatarTopWarCopia = (ObjTopWar) iterator2.next();
			List<Point> line = GeoUtil.drawBresenhamLine(
					avatarTopWar.getPontoAvatar(),
					avatarTopWarCopia.getPontoAvatar());
			if (!BotInfoShield.ATACANDO.equals(getEstado())) {
				setPontoDestino(avatarTopWarCopia.getPontoAvatar());
			} else if (line.size() < 10
					&& avatarTopWar.getArma() != ConstantesTopWar.ARMA_FACA) {
				jogoServidor.alternarFaca(avatarTopWar);
				avatarTopWar.setAngulo(GeoUtil.calculaAngulo(
						avatarTopWar.getPontoAvatar(),
						avatarTopWarCopia.getPontoAvatar(), 90));
				jogoServidor.atacar(avatarTopWar, avatarTopWar.getAngulo(), 0);
				executouAcaoAtaque = true;
				jogoServidor.alternarFaca(avatarTopWar);
			} else if (line.size() < ConstantesTopWar.MEIO_LIMITE_VISAO) {
				if (avatarTopWar.getArma() == ConstantesTopWar.ARMA_FACA)
					jogoServidor.alternarFaca(avatarTopWar);
				avatarTopWar.setAngulo(GeoUtil.calculaAngulo(
						avatarTopWar.getPontoAvatar(),
						avatarTopWarCopia.getPontoAvatar(), 90));
				setPontoDestino(avatarTopWarCopia.getPontoAvatar());
			} else {
				setPontoDestino(avatarTopWarCopia.getPontoAvatar());
			}
			setEstado(BotInfoShield.ATACANDO);
			break;
		}
		return executouAcaoAtaque;
	}

	private void botVaiPontoAleatorio() {
		Point calculaPonto = GeoUtil.calculaPonto(Util.intervalo(0, 360),
				Util.intervalo(100, 200), avatarTopWar.getPontoAvatar());
		while (!jogoServidor.verificaAndavel(avatarTopWar.getPontoAvatar(),
				calculaPonto)) {
			calculaPonto = GeoUtil.calculaPonto(Util.intervalo(0, 360), 100,
					avatarTopWar.getPontoAvatar());
		}
		setPontoDestino(calculaPonto);
	}

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
