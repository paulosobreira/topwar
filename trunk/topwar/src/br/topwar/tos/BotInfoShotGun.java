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

public class BotInfoShotGun extends BotInfoAbstract {

	public BotInfoShotGun(ObjTopWar bot, JogoServidor jogoServidor) {
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
		if (avatarTopWar.getVida() <= 0) {
			return;
		}
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
		setEstado(BotInfoShotGun.PATRULHANDO);
	}

	/**
	 * Seguir/Atacar avatar inimigo
	 */
	protected boolean seguirAtacarInimigo(List<ObjTopWar> avatarTopWarsCopia,
			boolean executouAcaoAtaque) {

		List<ObjTopWar> avataresOrdenadosDistancia = ordenaDistanciaAvatar(
				avatarTopWarsCopia, avatarTopWar, jogoServidor);
		for (Iterator iterator2 = avataresOrdenadosDistancia.iterator(); iterator2
				.hasNext();) {
			ObjTopWar avatarTopWarCopia = (ObjTopWar) iterator2.next();
			List<Point> line = GeoUtil.drawBresenhamLine(
					avatarTopWar.getPontoAvatar(),
					avatarTopWarCopia.getPontoAvatar());
			if (!BotInfoShotGun.ATACANDO.equals(getEstado())) {
				setPontoDestino(avatarTopWarCopia.getPontoAvatar());
			} else if ((avatarTopWar.getBalas() != 0 || avatarTopWar
					.getCartuchos() != 0)
					&& avatarTopWar.getArma() == ConstantesTopWar.ARMA_FACA) {
				jogoServidor.alternarFaca(avatarTopWar);
				executouAcaoAtaque = true;
			} else if (line.size() < 10
					&& avatarTopWar.getArma() == ConstantesTopWar.ARMA_FACA) {
				int vida = avatarTopWar.getVida();
				jogoServidor.atacar(avatarTopWar, avatarTopWar.getAngulo(), 0);
				if (vida != avatarTopWar.getVida()) {
					executouAcaoAtaque = true;
				} else {
					setPontoDestino(avatarTopWarCopia.getPontoAvatar());
				}
			} else if (line.size() < 70
					&& avatarTopWar.getArma() != ConstantesTopWar.ARMA_FACA
					&& jogoServidor.verificaAndavel(
							avatarTopWar.getPontoAvatar(),
							avatarTopWarCopia.getPontoAvatar())) {
				if (avatarTopWar.getBalas() == 0) {
					if (avatarTopWar.getCartuchos() == 0) {
						jogoServidor.alternarFaca(avatarTopWar);
						executouAcaoAtaque = true;
					} else {
						jogoServidor.recarregar(avatarTopWar);
						executouAcaoAtaque = true;
					}
				} else {
					avatarTopWar.setAngulo(GeoUtil.calculaAngulo(
							avatarTopWar.getPontoAvatar(),
							avatarTopWarCopia.getPontoAvatar(), 90));
					vidaUltAlvo = avatarTopWar.getVida();
					jogoServidor.atacar(avatarTopWar, avatarTopWar.getAngulo(),
							line.size() + Util.intervalo(20, 40));
					if (vidaUltAlvo != avatarTopWar.getVida()) {
						executouAcaoAtaque = true;
					} else {
						setPontoDestino(avatarTopWarCopia.getPontoAvatar());
					}
				}
			} else {
				setPontoDestino(avatarTopWarCopia.getPontoAvatar());
			}
			setEstado(BotInfoShotGun.ATACANDO);
			break;
		}
		return executouAcaoAtaque;
	}

}
