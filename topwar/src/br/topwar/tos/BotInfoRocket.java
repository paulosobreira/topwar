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

public class BotInfoRocket extends BotInfoAbstract {

	public BotInfoRocket(ObjTopWar bot, JogoServidor jogoServidor) {
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
			if (segueAvatarInfiltrante()) {
				return;
			}
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
				String mover = (String) jogoServidor.moverPontoAvatar(
						avatarTopWar, acaoClienteTopWar);
				if (!ConstantesTopWar.OK.equals(mover)) {
					setPontoDestino(null);
				}
			}
		}

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
			if (!BotInfoRocket.ATACANDO.equals(getEstado())) {
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
			} else if (line.size() < ConstantesTopWar.LIMITE_VISAO
					&& avatarTopWar.getArma() != ConstantesTopWar.ARMA_FACA) {
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
					if (jogoServidor.verificaAndavel(
							avatarTopWar.getPontoAvatar(),
							avatarTopWarCopia.getPontoAvatar()))
						jogoServidor.atacar(avatarTopWar,
								avatarTopWar.getAngulo(),
								Util.inte(line.size() * 0.9));
					if (vidaUltAlvo != avatarTopWar.getVida()) {
						executouAcaoAtaque = true;
					} else {
						setPontoDestino(avatarTopWarCopia.getPontoAvatar());
					}
				}
			} else {
				setPontoDestino(avatarTopWarCopia.getPontoAvatar());
			}
			setEstado(BotInfoRocket.ATACANDO);
			break;
		}

		return executouAcaoAtaque;
	}

}
