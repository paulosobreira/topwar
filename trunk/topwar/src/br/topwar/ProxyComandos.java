package br.topwar;

import br.nnpe.Logger;
import br.nnpe.servidor.NnpeProxyComandos;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
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
		if (ConstantesTopWar.ATUALIZAR_LISTA_AVS.equals(nnpeTO.getComando())) {
			return atualizarListaAvatares(nnpeTO);
		} else if (ConstantesTopWar.MOVER.equals(nnpeTO.getComando())) {
			return mover(nnpeTO);
		} else if (ConstantesTopWar.ATUALIZA_ANGULO.equals(nnpeTO.getComando())) {
			return atualizaAngulo(nnpeTO);
		} else if (ConstantesTopWar.ATIRAR.equals(nnpeTO.getComando())) {
			return atirar(nnpeTO);
		} else if (ConstantesTopWar.ENTRAR_JOGO.equals(nnpeTO.getComando())) {
			return entrarJogo(nnpeTO);
		} else if (ConstantesTopWar.CRIAR_JOGO.equals(nnpeTO.getComando())) {
			return criarJogo(nnpeTO);
		} else {
			return super.processarObjeto(object);
		}
	}

	private Object atualizaAngulo(NnpeTO nnpeTO) {
		return controleJogosServidor.atualizaAngulo(nnpeTO);
	}

	private Object atirar(NnpeTO nnpeTO) {
		return controleJogosServidor.atirar(nnpeTO);
	}

	private Object entrarJogo(NnpeTO nnpeTO) {
		return controleJogosServidor.entrarJogo(nnpeTO);
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

	@Override
	public void removerClienteInativo(SessaoCliente sessaoClienteRemover) {
		super.removerClienteInativo(sessaoClienteRemover);
		controleJogosServidor.removerClienteInativo(sessaoClienteRemover);
	}

	@Override
	public void ganchoMonitorAtividade() {
		controleJogosServidor.removerJogosVaziosFinalizados();

	}

}
