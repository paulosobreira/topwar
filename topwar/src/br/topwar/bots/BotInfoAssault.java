package br.topwar.bots;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;

import br.nnpe.GeoUtil;
import br.nnpe.Util;
import br.topwar.ConstantesTopWar;
import br.topwar.servidor.JogoServidor;
import br.topwar.tos.ObjTopWar;

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
		setExecutouAcaoAtaque(false);
		processaAvataresVisiveis(avatarTopWar, jogoServidor);
		int contaInimigosVisiveis = contaInimigosVisiveis();
		int contaAmigosVisiveis = contaAmigosVisiveis();

		if (contaAmigosVisiveis + 1 < contaInimigosVisiveis
				&& !verificaDestinoSeguroDosInimigos()) {
			procurarAbrigo();
		} else {
			tentarAtacar();
		}

		if (!isExecutouAcaoAtaque()) {
			segueAvatar();
			if (getPontoDestino() == null) {
				patrulhar();
			}
			moverDestino();
		}

	}


	/**
	 * Atacar avatar inimigo
	 */
	protected void atacarInimigo() {
		setSeguindo(null);
		setExecutouAcaoAtaque(false);
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
			if ((avatarTopWar.getBalas() != 0 || avatarTopWar.getCartuchos() != 0)
					&& avatarTopWar.getArma() == ConstantesTopWar.ARMA_FACA) {
				jogoServidor.alternarFaca(avatarTopWar);
				executouAcaoAtaque = true;
			} else if (line.size() < Util.intervalo(15, 25)) {
				executouAcaoAtaque = atacaComFaca(avatarTopWarCopia);
			} else if (avatarTopWar.getArma() != ConstantesTopWar.ARMA_FACA) {
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
							Util.inte(line.size() * 1.5));
					if (vidaUltAlvo != avatarTopWar.getVida()) {
						executouAcaoAtaque = true;
					}
				}
			}
			break;
		}
		setExecutouAcaoAtaque(true);
	}

	protected void seguirInimigo() {
		setSeguindo(null);
		for (Iterator iterator2 = avataresTimeOposto.iterator(); iterator2
				.hasNext();) {
			ObjTopWar avatarTopWarCopia = (ObjTopWar) iterator2.next();
			if (ConstantesTopWar.OBJ_ROCKET == avatarTopWarCopia.getArma()) {
				continue;
			}
			setPontoDestino(avatarTopWarCopia.getPontoAvatar());
			setSeguindo(avatarTopWarCopia);
			break;
		}
	}

	@Override
	public void gerarDesvioBot() {
		setDesvio(Util.intervalo(-3, 3));
	}

}
