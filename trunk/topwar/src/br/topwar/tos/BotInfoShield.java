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
			} else if (line.size() < ConstantesTopWar.LIMITE_VISAO) {
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

}