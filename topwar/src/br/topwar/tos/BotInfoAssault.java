package br.topwar.tos;

import java.util.List;

import br.nnpe.Util;
import br.topwar.servidor.JogoServidor;

public class BotInfoAssault extends BotInfoAbstract {

	public BotInfoAssault(ObjTopWar bot, JogoServidor jogoServidor) {
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
		processaAvataresVisiveis(avatarTopWar, jogoServidor);
		segueAvatar();
		if (getPontoDestino() == null) {
			patrulhar();
		}
		moverDestino();

		// Point pontoAvatar = avatarTopWar.getPontoAvatar();
		// if (pontoAvatar.equals(ptAtual)) {
		// contPtAtual++;
		// } else {
		// contPtAtual = 0;
		// }
		// ptAtual = pontoAvatar;
		// List<ObjTopWar> avatarTopWarsCopia = jogoServidor
		// .getAvatarTopWarsCopia();
		// boolean executouAcaoAtaque = false;
		// if (contPtAtual < 50) {
		// executouAcaoAtaque = seguirAtacarInimigo(avatarTopWarsCopia,
		// executouAcaoAtaque);
		// }
		// if (!executouAcaoAtaque) {
		// seguir();
		// if (!SEGUINDO.equals(getEstado())) {
		// patrulhar();
		// }
		// moverBot();
		// }

	}

	/**
	 * Seguir/Atacar avatar inimigo
	 */
	protected boolean seguirAtacarInimigo(List<ObjTopWar> avatarTopWarsCopia,
			boolean executouAcaoAtaque) {
		// for (Iterator iterator2 = avataresVisiveis.iterator(); iterator2
		// .hasNext();) {
		// ObjTopWar avatarTopWarCopia = (ObjTopWar) iterator2.next();
		// if (avatarTopWar.getTime().equals(avatarTopWarCopia.getTime())
		// || avatarTopWarCopia.getVida() <= 0) {
		// continue;
		// }
		// if (ConstantesTopWar.OBJ_ROCKET == avatarTopWarCopia.getArma()) {
		// continue;
		// }
		// List<Point> line = GeoUtil.drawBresenhamLine(
		// avatarTopWar.getPontoAvatar(),
		// avatarTopWarCopia.getPontoAvatar());
		// if (!BotInfoAssault.ATACANDO.equals(getEstado())) {
		// setPontoDestino(avatarTopWarCopia.getPontoAvatar());
		// } else if ((avatarTopWar.getBalas() != 0 || avatarTopWar
		// .getCartuchos() != 0)
		// && avatarTopWar.getArma() == ConstantesTopWar.ARMA_FACA) {
		// jogoServidor.alternarFaca(avatarTopWar);
		// executouAcaoAtaque = true;
		// } else if (line.size() < Util.intervalo(15, 25)) {
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
		// setEstado(BotInfoAssault.ATACANDO);
		// break;
		// }
		return executouAcaoAtaque;
	}

	@Override
	public void gerarDesvioBot() {
		setDesvio(Util.intervalo(-3, 3));
	}
}
