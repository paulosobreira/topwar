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
		if (contPtAtual < 25) {
			executouAcaoAtaque = seguirAtacarInimigo(avatarTopWarsCopia,
					executouAcaoAtaque);
		}
		if (!executouAcaoAtaque) {
			moverBot();
		}

	}

	/**
	 * Seguir/Atacar avatar inimigo
	 */
	protected boolean seguirAtacarInimigo(List<ObjTopWar> avatarTopWarsCopia,
			boolean executouAcaoAtaque) {

		List<ObjTopWar> avataresOrdenadosDistancia = ordenaDistanciaAvatarCampoVisaoTiro(
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
			} else if (line.size() < Util.intervalo(10, 30)) {
				executouAcaoAtaque = atacaComFaca(avatarTopWarCopia);
			} else if (line.size() < 70
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

	protected boolean vaiSeguirInfiltrar() {
		return false;
	}

	@Override
	public void gerarDesvioBot() {
		setDesvio(0);

	}
}