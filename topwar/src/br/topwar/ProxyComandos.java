package br.topwar;

import br.nnpe.servidor.NnpeProxyComandos;
import br.nnpe.tos.NnpeTO;
import br.topwar.servidor.ControleJogosServidor;
import br.topwar.servidor.ControlePersistencia;

public class ProxyComandos extends NnpeProxyComandos {

	private ControlePersistencia controlePersistencia;
	private ControleJogosServidor controleJogosServidor;

	public ProxyComandos(String webDir, String webInfDir) {
		super(webDir, webInfDir);
		controlePersistencia = new ControlePersistencia(webDir, webInfDir);
		controleJogosServidor = new ControleJogosServidor(nnpeDados,
				controlePersistencia, this);
	}

	public Object processarObjeto(Object object) {
		NnpeTO nnpeTO = (NnpeTO) object;
		if (ConstantesTopWar.CRIAR_JOGO.equals(nnpeTO.getComando())) {
			return criarJogo(nnpeTO);
		} else if (ConstantesTopWar.ATUALIZAR_LISTA_AVS.equals(nnpeTO
				.getComando())) {
			return atualizarListaAvatares(nnpeTO);
		} else if (ConstantesTopWar.MOVER.equals(nnpeTO.getComando())) {
			return mover(nnpeTO);
		} else {
			return super.processarObjeto(object);
		}
	}

	private Object atualizarListaAvatares(NnpeTO nnpeTO) {
		return controleJogosServidor.atualizarListaAvatares(nnpeTO);
	}

	private Object mover(NnpeTO nnpeTO) {
		return controleJogosServidor.mover(nnpeTO);
	}

	private Object criarJogo(NnpeTO nnpeTO) {
		return controleJogosServidor.criarJogo(nnpeTO);
	}

}
