package br.topwar.bots;

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
import br.topwar.tos.ObjTopWar;

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
		setExecutouAcaoAtaque(false);
		processaAvataresVisiveis(avatarTopWar, jogoServidor);
		int contaInimigosVisiveis = contaInimigosVisiveis();
		int contaAmigosVisiveis = contaAmigosVisiveis();

		if (contaAmigosVisiveis + 3 < contaInimigosVisiveis
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
			if ((avatarTopWar.getBalas() != 0 || avatarTopWar.getCartuchos() != 0)
					&& avatarTopWar.getArma() == ConstantesTopWar.ARMA_FACA) {
				jogoServidor.alternarFaca(avatarTopWar);
				executouAcaoAtaque = true;
			} else if (line.size() < Util.intervalo(15, 25)) {
				executouAcaoAtaque = atacaComFaca(avatarTopWarCopia);
			} else if (avatarTopWar.getArma() != ConstantesTopWar.ARMA_FACA
					&& line.size() <= 100) {
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
