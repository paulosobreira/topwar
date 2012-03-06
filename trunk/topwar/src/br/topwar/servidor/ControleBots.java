package br.topwar.servidor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.nnpe.GeoUtil;
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
				while (!jogoServidor.isFinalizado() && !interrupt) {
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
						.intervalo(0, 360), 100, avatarTopWar.getPontoAvatar());
				while (jogoServidor.verificaColisao(calculaPonto)) {
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
				Point dstMover = lineMove.get(avatarTopWar.getVelocidade());
				avatarTopWar.setAngulo(GeoUtil.calculaAngulo(avatarTopWar
						.getPontoAvatar(), dstMover, 90));
				DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
				acaoClienteTopWar.setPonto(dstMover);
				jogoServidor.moverAvatar(avatarTopWar, acaoClienteTopWar);
			}

		}
	}

	public void adicionarBot() {
		AvatarTopWar bot = jogoServidor.entrarNoJogo("boTeste");
		bot.setBotInfo(new BotInfo());
		bots.add(bot);
	}

}
