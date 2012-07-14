package br.topwar.tos;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import br.nnpe.GeoUtil;
import br.topwar.ConstantesTopWar;
import br.topwar.servidor.JogoServidor;

public abstract class BotInfoAbstract {

	public abstract void processaAcaoBot();

	public List<ObjTopWar> ordenaDistanciaAvatar(
			List<ObjTopWar> avatarTopWarsCopia, ObjTopWar avatarTopWar,
			JogoServidor jogoServidor) {
		List<ObjTopWar> avataresOrdenadosDistancia = new ArrayList<ObjTopWar>();
		for (Iterator iterator2 = avatarTopWarsCopia.iterator(); iterator2
				.hasNext();) {
			ObjTopWar avatarTopWarCopia = (ObjTopWar) iterator2.next();
			if (avatarTopWar.getTime().equals(avatarTopWarCopia.getTime())
					|| avatarTopWarCopia.getVida() <= 0) {
				continue;
			}
			if (ConstantesTopWar.OBJ_ROCKET == avatarTopWarCopia.getArma()) {
				continue;
			}
			List<Point> line = GeoUtil.drawBresenhamLine(
					avatarTopWar.getPontoAvatar(),
					avatarTopWarCopia.getPontoAvatar());
			if (jogoServidor.campoVisao(line, avatarTopWar, true)) {
				avatarTopWarCopia.setDistanciaDeUmAvatar(GeoUtil
						.distaciaEntrePontos(
								avatarTopWarCopia.getPontoAvatar(),
								avatarTopWar.getPontoAvatar()));
				avataresOrdenadosDistancia.add(avatarTopWarCopia);
			}
		}
		Collections.sort(avataresOrdenadosDistancia,
				new Comparator<ObjTopWar>() {
					@Override
					public int compare(ObjTopWar o1, ObjTopWar o2) {
						return new Double(o1.getDistanciaDeUmAvatar())
								.compareTo(new Double(o2
										.getDistanciaDeUmAvatar()));
					}
				});
		return avataresOrdenadosDistancia;
	}

}