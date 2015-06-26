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
		setExecutouAcaoAtaque(false);
		processaAvataresVisiveis(avatarTopWar, jogoServidor);
		int contaInimigosVisiveis = contaInimigosVisiveis();
		//int contaAmigosVisiveis = contaAmigosVisiveis();

		tentarAtacar();

		if (!isExecutouAcaoAtaque()) {
			segueAvatar();
			if (getPontoDestino() == null) {
				patrulhar();
			}
			if (contaInimigosVisiveis > 0) {
				protegeAvatarSeguindoSeguindo();
			}
			moverDestino();
		}

	}

	private void protegeAvatarSeguindoSeguindo() {
		if (getSeguindo() == null) {
			return;
		}
		ObjTopWar avatarMaisProximoInimigo = null;
		int distancia = Integer.MAX_VALUE;
		for (Iterator iterator = avataresTimeOposto.iterator(); iterator
				.hasNext();) {
			ObjTopWar objTopWar = (ObjTopWar) iterator.next();
			if (objTopWar.getArma() == ConstantesTopWar.ARMA_SHOTGUN) {
				continue;
			}
			int distaciaEntrePontos = GeoUtil.distaciaEntrePontos(getSeguindo()
					.getPontoAvatar(), objTopWar.getPontoAvatar());
			if (distaciaEntrePontos < distancia) {
				distancia = distaciaEntrePontos;
				avatarMaisProximoInimigo = objTopWar;
			}
		}
		if (avatarMaisProximoInimigo == null) {
			return;
		}
		List<Point> drawBresenhamLine = GeoUtil.drawBresenhamLine(getSeguindo()
				.getPontoAvatar(), avatarMaisProximoInimigo.getPontoAvatar());
		setPontoDestino(drawBresenhamLine.get(Util.inte(drawBresenhamLine
				.size() * 0.1)));
		avatarTopWar.setAngulo(GeoUtil.calculaAngulo(
				avatarTopWar.getPontoAvatar(),
				avatarMaisProximoInimigo.getPontoAvatar(), 90));
	}

	/**
	 * Seguir/Atacar avatar inimigo
	 */
	protected void atacarInimigo() {
		setSeguindo(null);
		setExecutouAcaoAtaque(false);
		Integer menorDistancia = null;
		ObjTopWar inimigoMaisProximo = null;
		for (Iterator iterator2 = avataresTimeOposto.iterator(); iterator2
				.hasNext();) {
			ObjTopWar avatarTopWarCopia = (ObjTopWar) iterator2.next();
			if (avatarTopWarCopia.getVida() <= 0) {
				continue;
			}
			if (ConstantesTopWar.OBJ_ROCKET == avatarTopWarCopia.getArma()) {
				continue;
			}
			List<Point> line = GeoUtil.drawBresenhamLine(
					avatarTopWar.getPontoAvatar(),
					avatarTopWarCopia.getPontoAvatar());
			if (menorDistancia == null || line.size() < menorDistancia) {
				menorDistancia = line.size();
				inimigoMaisProximo = avatarTopWarCopia;
			}
			if (line.size() > 25) {
				continue;
			}
			executouAcaoAtaque = atacaComFaca(avatarTopWarCopia);
			executouAcaoAtaque = true;
			break;
		}
		if (!executouAcaoAtaque && inimigoMaisProximo != null) {
			setPontoDestino(inimigoMaisProximo.getPontoAvatar());
			return;
		}
		setExecutouAcaoAtaque(true);
	}

	protected boolean seguindo() {
		return false;
	}

	@Override
	public void gerarDesvioBot() {
		setDesvio(0);

	}
}
