package br.topwar.servidor;

import br.nnpe.Logger;
import br.topwar.ConstantesTopWar;
import br.topwar.tos.BotInfoAssault;
import br.topwar.tos.BotInfoAbstract;
import br.topwar.tos.BotInfoMachine;
import br.topwar.tos.BotInfoShield;
import br.topwar.tos.BotInfoShotGun;
import br.topwar.tos.ObjTopWar;

public class BotFactory {

	public static BotInfoAbstract criaBotInfo(ObjTopWar avatarTopWar,
			JogoServidor jogoServidor) {

		if (ConstantesTopWar.ASSAULT.equals(avatarTopWar.getClasse())) {
			return new BotInfoAssault(avatarTopWar, jogoServidor);
		} else if (ConstantesTopWar.SHOTGUN.equals(avatarTopWar.getClasse())) {
			return new BotInfoShotGun(avatarTopWar, jogoServidor);
		} else if (ConstantesTopWar.SNIPER.equals(avatarTopWar.getClasse())) {
			return new BotInfoAssault(avatarTopWar, jogoServidor);
		} else if (ConstantesTopWar.MACHINEGUN.equals(avatarTopWar.getClasse())) {
			return new BotInfoMachine(avatarTopWar, jogoServidor);
		} else if (ConstantesTopWar.ROCKET.equals(avatarTopWar.getClasse())) {

		} else if (ConstantesTopWar.SHIELD.equals(avatarTopWar.getClasse())) {
			return new BotInfoShield(avatarTopWar, jogoServidor);
		}
		Logger.logar("criaBotInfo null Classe " + avatarTopWar.getClasse());
		return null;
	}

}
