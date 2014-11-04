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

public class BotInfoMachine extends BotInfoAbstract {

	public BotInfoMachine(ObjTopWar bot, JogoServidor jogoServidor) {
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
//			executouAcaoAtaque = seguirAtacarInimigo(avatarTopWarsCopia,
//					executouAcaoAtaque);
		}
		if (!executouAcaoAtaque) {
			moverDestino();
		}

	}

	/**
	 * Seguir/Atacar avatar inimigo
	 */
	protected void atacarInimigo() {

		// List<ObjTopWar> avataresOrdenadosDistancia =
		// processaAvataresVisiveis(
		// avatarTopWarsCopia, avatarTopWar, jogoServidor);
		// for (Iterator iterator2 = avataresOrdenadosDistancia.iterator();
		// iterator2
		// .hasNext();) {
		// ObjTopWar avatarTopWarCopia = (ObjTopWar) iterator2.next();
		// List<Point> line = GeoUtil.drawBresenhamLine(
		// avatarTopWar.getPontoAvatar(),
		// avatarTopWarCopia.getPontoAvatar());
		// if (!BotInfoMachine.ATACANDO.equals(getEstado())) {
		// setPontoDestino(avatarTopWarCopia.getPontoAvatar());
		// } else if ((avatarTopWar.getBalas() != 0 || avatarTopWar
		// .getCartuchos() != 0)
		// && avatarTopWar.getArma() == ConstantesTopWar.ARMA_FACA) {
		// jogoServidor.alternarFaca(avatarTopWar);
		// executouAcaoAtaque = true;
		// } else if (line.size() < Util.intervalo(10, 30)) {
		// executouAcaoAtaque = atacaComFaca(avatarTopWarCopia);
		// } else if (avatarTopWar.getArma() != ConstantesTopWar.ARMA_FACA) {
		// if (avatarTopWar.getBalas() == 0) {
		// if (avatarTopWar.getCartuchos() == 0) {
		// jogoServidor.alternarFaca(avatarTopWar);
		// executouAcaoAtaque = true;
		// } else {
		// jogoServidor.recarregar(avatarTopWar);
		// executouAcaoAtaque = true;
		// }
		// } else {
		// avatarTopWar.setAngulo(GeoUtil.calculaAngulo(
		// avatarTopWar.getPontoAvatar(),
		// avatarTopWarCopia.getPontoAvatar(), 90));
		// vidaUltAlvo = avatarTopWar.getVida();
		// jogoServidor.atacar(avatarTopWar, avatarTopWar.getAngulo()
		// + getDesvio(), Util.inte(line.size() * 1.5));
		// if (vidaUltAlvo != avatarTopWar.getVida()) {
		// executouAcaoAtaque = true;
		// } else {
		// setPontoDestino(avatarTopWarCopia.getPontoAvatar());
		// }
		// }
		// } else {
		// setPontoDestino(avatarTopWarCopia.getPontoAvatar());
		// }
		// setEstado(BotInfoMachine.ATACANDO);
		// break;
		// }
		//
		// return executouAcaoAtaque;
	}

	@Override
	public void gerarDesvioBot() {
		setDesvio(Util.intervalo(-4, 4));

	}

}
