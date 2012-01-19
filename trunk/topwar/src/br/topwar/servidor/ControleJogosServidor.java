package br.topwar.servidor;

import java.util.HashMap;
import java.util.Map;

import br.nnpe.tos.NnpeDados;
import br.nnpe.tos.NnpeTO;
import br.topwar.ProxyComandos;
import br.topwar.recursos.idiomas.Lang;
import br.topwar.tos.DadosJogoTopWar;

public class ControleJogosServidor {

	private ControlePersistencia controlePersistencia;

	private ProxyComandos proxyComandos;

	protected NnpeDados nnpeDados;

	private Map<String, JogoServidor> mapaJogos = new HashMap<String, JogoServidor>();

	private int contadorJogos = 0;

	public ControleJogosServidor(NnpeDados nnpeDados,
			ControlePersistencia controlePersistencia,
			ProxyComandos proxyComandos) {
		super();
		this.controlePersistencia = controlePersistencia;
		this.proxyComandos = proxyComandos;
		this.nnpeDados = nnpeDados;
	}

	public Object criarJogo(NnpeTO nnpeTO) {
		DadosJogoTopWar dadosJogoTopWar = (DadosJogoTopWar) nnpeTO.getData();
		dadosJogoTopWar.setNomeJogo(Lang.msg("jogo") + contadorJogos++);
		nnpeDados.getJogosAndamento().add(dadosJogoTopWar.getNomeJogo());
		JogoServidor jogoServidor = new JogoServidor(dadosJogoTopWar,
				proxyComandos);
		mapaJogos.put(dadosJogoTopWar.getNomeJogo(), jogoServidor);
		nnpeTO = new NnpeTO();
		nnpeTO.setData(dadosJogoTopWar);
		return nnpeTO;
	}
}
