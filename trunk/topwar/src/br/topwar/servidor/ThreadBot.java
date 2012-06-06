package br.topwar.servidor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.topwar.ConstantesTopWar;
import br.topwar.tos.ObjTopWar;
import br.topwar.tos.BotInfo;

public class ThreadBot implements Runnable {
	private JogoServidor jogoServidor;
	private List<BotInfo> myBots = new ArrayList<BotInfo>();
	boolean interrupt = false;

	public ThreadBot(JogoServidor jogoServidor) {
		super();
		this.jogoServidor = jogoServidor;
	}

	public List<BotInfo> getMyBots() {
		return myBots;
	}

	@Override
	public void run() {
		while (!jogoServidor.verificaFinalizado() && !interrupt) {
			synchronized (myBots) {
				for (Iterator iterator = myBots.iterator(); iterator.hasNext();) {
					BotInfo botInfo = (BotInfo) iterator.next();
					botInfo.processaAcaoBot();
				}
			}
			try {
				long sleep = (ConstantesTopWar.ATRASO_REDE_PADRAO_BOTS)
						- myBots.size();
				if (sleep < 10) {
					sleep = 10;
				}
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				interrupt = true;
			}
		}

	}

	public void addBot(ObjTopWar bot) {
		synchronized (myBots) {
			myBots.add(bot.getBotInfo());
		}

	}

}
