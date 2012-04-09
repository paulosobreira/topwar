package br.topwar.servidor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.nnpe.Logger;
import br.nnpe.NameGenerator;
import br.nnpe.Util;
import br.topwar.tos.AvatarTopWar;
import br.topwar.tos.BotInfo;

public class ControleBots {

	private JogoServidor jogoServidor;

	private ThreadBot thBot1;
	private ThreadBot thBot2;

	public static void main(String[] args) throws IOException {
		NameGenerator nameGenerator = new NameGenerator("silabas");
		System.out.println(" ->  "
				+ nameGenerator.compose(Util.intervalo(2, 4)));

	}

	public ControleBots(JogoServidor jogoServidor) {
		this.jogoServidor = jogoServidor;
		thBot1 = new ThreadBot(jogoServidor);
		Thread thread1 = new Thread(thBot1);
		thread1.start();
		thBot2 = new ThreadBot(jogoServidor);
		Thread thread2 = new Thread(thBot2);
		thread2.start();
	}

	public void adicionarBot() {
		try {
			NameGenerator nameGenerator = new NameGenerator("silabas");
			for (int i = 0; i < 127; i++) {
				String nome = nameGenerator.compose(Util.intervalo(2, 3));
				AvatarTopWar bot = jogoServidor.entrarNoJogo(nome);
				bot.setBotInfo(new BotInfo(bot, jogoServidor));
				bot.getBotInfo();
				if (i % 2 == 0) {
					thBot1.addBot(bot);
				} else {
					thBot2.addBot(bot);
				}
				Logger.logar("Adicionou " + bot.getNomeJogador());
			}
		} catch (IOException e) {
			Logger.logarExept(e);
		}
	}

}
