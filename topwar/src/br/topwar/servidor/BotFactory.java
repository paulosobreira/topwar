package br.topwar.servidor;

import br.topwar.ConstantesTopWar;
import br.topwar.tos.BotInfoAssault;
import br.topwar.tos.BotInfoAbstract;
import br.topwar.tos.BotInfoShield;
import br.topwar.tos.BotInfoShotGun;
import br.topwar.tos.ObjTopWar;

public class BotFactory {

	public static BotInfoAbstract criaBotInfo(ObjTopWar bot,
			JogoServidor jogoServidor) {
		int arma = bot.getArma();
		switch (arma) {
		case ConstantesTopWar.ARMA_ASSAULT:
			return new BotInfoAssault(bot, jogoServidor);
		case ConstantesTopWar.ARMA_MACHINEGUN:
			return new BotInfoAssault(bot, jogoServidor);
		case ConstantesTopWar.ARMA_SHIELD:
			return new BotInfoShield(bot, jogoServidor);
		case ConstantesTopWar.ARMA_SHOTGUN:
			return new BotInfoShotGun(bot, jogoServidor);

		default:
			break;
		}
		return null;
	}

}
