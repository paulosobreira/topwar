package br.topwar.servidor;

import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.nnpe.GeoUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.topwar.ConstantesTopWar;
import br.topwar.tos.AvatarTopWar;
import br.topwar.tos.BotInfo;
import br.topwar.tos.DadosAcaoClienteTopWar;

public class ControleBots {

	private JogoServidor jogoServidor;
	private Thread threadBots;
	private List<AvatarTopWar> bots = new ArrayList<AvatarTopWar>();

	public ControleBots(JogoServidor jogoServidor) {
		this.jogoServidor = jogoServidor;
	}

	public void incializar() {
		if (threadBots != null) {
			threadBots.interrupt();
		}
		threadBots = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean interrupt = false;
				while (!jogoServidor.verificaFinalizado() && !interrupt) {
					synchronized (bots) {
						processarAcoesBots();
					}
					try {
						Thread.sleep(ConstantesTopWar.ATRASO_REDE_PADRAO_BOTS);
					} catch (InterruptedException e) {
						interrupt = true;
					}
				}

			}
		});
		threadBots.start();
	}

	protected void processarAcoesBots() {
		List<AvatarTopWar> avatarTopWarsCopia = jogoServidor
				.getAvatarTopWarsCopia();
		for (Iterator iterator = bots.iterator(); iterator.hasNext();) {
			AvatarTopWar avatarTopWar = (AvatarTopWar) iterator.next();
			BotInfo botInfo = avatarTopWar.getBotInfo();

			/**
			 * Seguir avatar inimigo
			 */
			for (Iterator iterator2 = avatarTopWarsCopia.iterator(); iterator2
					.hasNext();) {
				AvatarTopWar avatarTopWarCopia = (AvatarTopWar) iterator2
						.next();
				if (avatarTopWar.getTime().equals(avatarTopWarCopia.getTime())
						|| avatarTopWarCopia.getVida() <= 0) {
					continue;
				}

				List<Point> line = GeoUtil.drawBresenhamLine(avatarTopWar
						.getPontoAvatar(), avatarTopWarCopia.getPontoAvatar());
				if (jogoServidor.campoVisao(line, avatarTopWar, true)) {
					if (line.size() <= 5
							&& avatarTopWar.getArma() == ConstantesTopWar.ARMA_ASSALT) {
						jogoServidor.alternarFaca(avatarTopWar);
					} else if (line.size() > 5
							&& avatarTopWar.getArma() == ConstantesTopWar.ARMA_FACA) {
						jogoServidor.alternarFaca(avatarTopWar);
					}
					if (line.size() <= 5
							&& avatarTopWar.getArma() == ConstantesTopWar.ARMA_FACA) {
						jogoServidor.atacar(avatarTopWar, avatarTopWar
								.getAngulo(), 5);
					} else if (line.size() < ConstantesTopWar.MEIO_LIMITE_VISAO
							&& avatarTopWar.getArma() == ConstantesTopWar.ARMA_ASSALT) {
						if (avatarTopWar.getBalas() == 0
								&& avatarTopWar.getCartuchos() == 0) {
							jogoServidor.alternarFaca(avatarTopWar);
						} else {
							jogoServidor.atacar(avatarTopWar, avatarTopWar
									.getAngulo(), line.size());
						}
					} else {
						botInfo.setPontoDestino(avatarTopWarCopia
								.getPontoAvatar());
					}
					break;
				}

			}

			/**
			 * Patrulhando
			 */
			if (botInfo.getPontoDestino() == null) {
				Point calculaPonto = GeoUtil.calculaPonto(Util
						.intervalo(0, 360), Util.intervalo(100, 200),
						avatarTopWar.getPontoAvatar());
				while (!jogoServidor.verificaAndavel(avatarTopWar
						.getPontoAvatar(), calculaPonto)) {
					calculaPonto = GeoUtil.calculaPonto(Util.intervalo(0, 360),
							100, avatarTopWar.getPontoAvatar());
				}
				botInfo.setPontoDestino(calculaPonto);
			}

			List<Point> lineMove = GeoUtil.drawBresenhamLine(avatarTopWar
					.getPontoAvatar(), botInfo.getPontoDestino());
			if (lineMove.size() < avatarTopWar.getVelocidade()) {
				botInfo.setPontoDestino(null);
			} else {
				Point dstMover = lineMove.get(avatarTopWar.getVelocidade() - 1);
				DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
				acaoClienteTopWar.setPonto(dstMover);
				acaoClienteTopWar.setAngulo(GeoUtil.calculaAngulo(avatarTopWar
						.getPontoAvatar(), dstMover, 90));
				String mover = (String) jogoServidor.moverPontoAvatar(
						avatarTopWar, acaoClienteTopWar);
				if (!ConstantesTopWar.OK.equals(mover)) {
					botInfo.setPontoDestino(null);
				}
			}

		}
	}

	public void adicionarBot() {
		for (int i = 0; i < 31; i++) {
			AvatarTopWar bot = jogoServidor.entrarNoJogo("boTeste " + i);
			bot.setBotInfo(new BotInfo());
			bots.add(bot);
			Logger.logar("Adicionou " + bot);
		}
	}

}
