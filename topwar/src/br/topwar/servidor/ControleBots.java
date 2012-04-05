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

	private List<AvatarTopWar> bots = new ArrayList<AvatarTopWar>();

	public static void main(String[] args) throws IOException {
		NameGenerator nameGenerator = new NameGenerator("silabas");
		System.out.println(" ->  "
				+ nameGenerator.compose(Util.intervalo(2, 4)));

	}

	public ControleBots(JogoServidor jogoServidor) {
		this.jogoServidor = jogoServidor;
	}

	public void adicionarBot() {
		try {
			NameGenerator nameGenerator = new NameGenerator("silabas");
			for (int i = 0; i < 3; i++) {
				String nome = nameGenerator.compose(Util.intervalo(2, 3));
				AvatarTopWar bot = jogoServidor.entrarNoJogo(nome);
				bot.setBotInfo(new BotInfo(bot, jogoServidor));
				synchronized (bots) {
					bots.add(bot);
					bot.getBotInfo().incializar();
				}
				Logger.logar("Adicionou " + bot.getNomeJogador());
			}
		} catch (IOException e) {
			Logger.logarExept(e);
		}
	}

}
