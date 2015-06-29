package br.topwar.servidor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.nnpe.Logger;
import br.topwar.ConstantesTopWar;
import br.topwar.bots.BotInfoAbstract;
import br.topwar.bots.BotInfoAssault;
import br.topwar.tos.ObjTopWar;

public class ThreadBot implements Runnable {
	private JogoServidor jogoServidor;
	private List<BotInfoAbstract> myBots = new ArrayList<BotInfoAbstract>();
	boolean interrupt = false;

	public ThreadBot(JogoServidor jogoServidor) {
		super();
		this.jogoServidor = jogoServidor;
	}

	public List<BotInfoAbstract> getMyBots() {
		return myBots;
	}

	@Override
	public void run() {
		while (!jogoServidor.verificaFinalizado() && !interrupt) {
			long ini = System.currentTimeMillis();
			synchronized (myBots) {
				for (Iterator iterator = myBots.iterator(); iterator.hasNext();) {
					BotInfoAbstract botInfo = (BotInfoAbstract) iterator.next();
					long iniIN = System.currentTimeMillis();
					botInfo.processaAcaoBot();
					long fimIN = (System.currentTimeMillis() - iniIN);
					botInfo.setTempoProcessaAcaoBot(fimIN);
				}
			}
			long fim = (System.currentTimeMillis() - ini);
			try {
				long sleep = (ConstantesTopWar.ATRASO_REDE_PADRAO) - fim;
				if (sleep < 15) {
					sleep = 15;
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
