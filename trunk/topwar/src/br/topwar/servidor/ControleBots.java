package br.topwar.servidor;

import java.io.IOException;

import br.nnpe.Logger;
import br.nnpe.NameGenerator;
import br.nnpe.Util;
import br.topwar.ConstantesTopWar;
import br.topwar.local.ServidorLocal;
import br.topwar.tos.DadosJogoTopWar;
import br.topwar.tos.ObjTopWar;

public class ControleBots {

	private JogoServidor jogoServidor;
	private ControleJogosServidor controleJogosServidor;
	private ThreadBot thBot1;
	private ThreadBot thBot2;

	private Thread thread1;

	private Thread thread2;

	public static void main(String[] args) throws IOException {
		NameGenerator nameGenerator = new NameGenerator("silabas");
		Logger.logar(" ->  " + nameGenerator.compose(Util.intervalo(2, 4)));

	}

	public ControleBots(JogoServidor jogoServidor,
			ControleJogosServidor controleJogosServidor) {
		this.jogoServidor = jogoServidor;
		this.controleJogosServidor = controleJogosServidor;
		thBot1 = new ThreadBot(jogoServidor);
		thread1 = new Thread(thBot1);
		thBot2 = new ThreadBot(jogoServidor);
		thread2 = new Thread(thBot2);
	}

	public void adicionarBots() {
		try {
			Integer numBots = jogoServidor.getDadosJogoTopWar().getNumBots();
			Logger.logar("numBots " + numBots);
			boolean botsVsHumans = jogoServidor.getDadosJogoTopWar()
					.isBotsVsHumans();

			NameGenerator nameGenerator = new NameGenerator("silabas");
			if (!(controleJogosServidor instanceof ServidorLocal)
					&& numBots > 99) {
				numBots = 99;
			}
			//numBots = 6;
			for (int i = 0; i < numBots; i++) {
				String nome = "Bot " + i;
				DadosJogoTopWar dadosJogoTopWar = new DadosJogoTopWar();
				int botNumClass = Util.intervalo(0, 5);

				botNumClass = 5;
				switch (botNumClass) {
				case 0:
					dadosJogoTopWar.setClasse(ConstantesTopWar.MACHINEGUN);
					break;
				case 1:
					dadosJogoTopWar.setClasse(ConstantesTopWar.SHOTGUN);
					break;
				case 2:
					dadosJogoTopWar.setClasse(ConstantesTopWar.SHIELD);
					break;
				case 3:
					dadosJogoTopWar.setClasse(ConstantesTopWar.SNIPER);
					break;
				case 4:
					dadosJogoTopWar.setClasse(ConstantesTopWar.ROCKET);
					break;
				case 5:
					dadosJogoTopWar.setClasse(ConstantesTopWar.ASSAULT);
					break;

				default:
					break;
				}

				dadosJogoTopWar.setNomeJogador(nome);
				String time = ConstantesTopWar.TIME_VERMELHO;
				if (!botsVsHumans) {
					int contAzul = jogoServidor
							.contarJogadores(ConstantesTopWar.TIME_AZUL);
					int contVermelho = jogoServidor
							.contarJogadores(ConstantesTopWar.TIME_VERMELHO);
					if (contAzul > contVermelho) {
						time = ConstantesTopWar.TIME_VERMELHO;
					} else if (contAzul < contVermelho) {
						time = ConstantesTopWar.TIME_AZUL;
					} else {
						time = Math.random() < .5 ? ConstantesTopWar.TIME_VERMELHO
								: ConstantesTopWar.TIME_AZUL;

					}

				}
				ObjTopWar bot = jogoServidor
						.entrarNoJogo(dadosJogoTopWar, time);
				bot.setBotInfo(BotFactory.criaBotInfo(bot, jogoServidor));
				if (i % 2 == 0) {
					thBot1.addBot(bot);
				} else {
					thBot2.addBot(bot);
				}
				Logger.logar("Adicionou " + bot.getNomeJogador());
			}
			if (!thread1.isAlive()) {
				thread1.start();
			}
			if (!thread2.isAlive()) {
				thread2.start();
			}
		} catch (IOException e) {
			Logger.logarExept(e);
		}
	}
}
