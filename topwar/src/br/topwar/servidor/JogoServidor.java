package br.topwar.servidor;

import java.io.ObjectInputStream;

import br.nnpe.Logger;
import br.topwar.ProxyComandos;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.serial.MapaTopWar;
import br.topwar.tos.DadosJogoTopWar;

public class JogoServidor {

	private DadosJogoTopWar dadosJogoTopWar;
	private static MapaTopWar mapaTopWar;
	private ProxyComandos proxyComandos;

	public JogoServidor(DadosJogoTopWar dadosJogoTopWar,
			ProxyComandos proxyComandos) {
		this.dadosJogoTopWar = dadosJogoTopWar;
		this.proxyComandos = proxyComandos;
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(CarregadorRecursos
					.recursoComoStream("mapa9.topwar"));
			mapaTopWar = (MapaTopWar) ois.readObject();
		} catch (Exception e) {
			Logger.logarExept(e);
		}
	}

}
