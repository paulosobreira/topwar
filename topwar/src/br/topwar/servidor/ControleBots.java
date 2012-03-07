package br.topwar.servidor;

import java.awt.Point;
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
						Thread.sleep(ConstantesTopWar.ATRASO_REDE_PADRAO);
					} catch (InterruptedException e) {
						interrupt = true;
					}
				}

			}
		});
		threadBots.start();
	}

	protected void processarAcoesBots() {
		for (Iterator iterator = bots.iterator(); iterator.hasNext();) {
			AvatarTopWar avatarTopWar = (AvatarTopWar) iterator.next();
			BotInfo botInfo = avatarTopWar.getBotInfo();
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
		for (int i = 0; i < 15; i++) {
			AvatarTopWar bot = jogoServidor.entrarNoJogo("boTeste " + i);
			bot.setBotInfo(new BotInfo());
			bots.add(bot);
			Logger.logar("Adicionou " + bot);
		}
	}

}
