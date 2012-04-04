package br.topwar;

import br.nnpe.servidor.NnpeProxyComandos;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.topwar.servidor.ControleJogosServidor;
import br.topwar.servidor.ControlePersistencia;

public class ProxyComandos extends NnpeProxyComandos {

	private ControlePersistencia controlePersistencia;
	private ControleJogosServidor controleJogosServidor;
	private boolean removeInativos = true;

	public ProxyComandos() {
		this(null, null);
		removeInativos = false;
	}

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
		} else if (ConstantesTopWar.MOVER_PONTO.equals(nnpeTO.getComando())) {
			return moverPonto(nnpeTO);
		} else if (ConstantesTopWar.MOVER.equals(nnpeTO.getComando())) {
			return mover(nnpeTO);
		} else if (ConstantesTopWar.ATUALIZA_ANGULO.equals(nnpeTO.getComando())) {
			return atualizaAngulo(nnpeTO);
		} else if (ConstantesTopWar.ATACAR.equals(nnpeTO.getComando())) {
			return atacar(nnpeTO);
		} else if (ConstantesTopWar.ALTERNA_FACA.equals(nnpeTO.getComando())) {
			return alternarFaca(nnpeTO);
		} else if (ConstantesTopWar.RECARREGAR.equals(nnpeTO.getComando())) {
			return recarregar(nnpeTO);
		} else if (ConstantesTopWar.ENTRAR_JOGO.equals(nnpeTO.getComando())) {
			return entrarJogo(nnpeTO);
		} else if (ConstantesTopWar.SAIR_JOGO.equals(nnpeTO.getComando())) {
			return sairJogo(nnpeTO);
		} else if (ConstantesTopWar.CRIAR_JOGO.equals(nnpeTO.getComando())) {
			return criarJogo(nnpeTO);
		} else if (ConstantesTopWar.OBTER_PLCAR.equals(nnpeTO.getComando())) {
			return obterPlacarJogo(nnpeTO);
		} else {
			return super.processarObjeto(object);
		}
	}

	private Object sairJogo(NnpeTO nnpeTO) {
		return controleJogosServidor.sairJogo(nnpeTO);
	}

	private Object obterPlacarJogo(NnpeTO nnpeTO) {
		return controleJogosServidor.obterPlacarJogo(nnpeTO);
	}

	private Object alternarFaca(NnpeTO nnpeTO) {
		return controleJogosServidor.alternarFaca(nnpeTO);
	}

	private Object moverPonto(NnpeTO nnpeTO) {
		return controleJogosServidor.moverPonto(nnpeTO);
	}

	private Object recarregar(NnpeTO nnpeTO) {
		return controleJogosServidor.recarregar(nnpeTO);
	}

	private Object atualizaAngulo(NnpeTO nnpeTO) {
		return controleJogosServidor.atualizaAngulo(nnpeTO);
	}

	private Object atacar(NnpeTO nnpeTO) {
		return controleJogosServidor.atacar(nnpeTO);
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
		if (removeInativos) {
			super.removerClienteInativo(sessaoClienteRemover);
			controleJogosServidor.removerClienteInativo(sessaoClienteRemover);
		}
	}

	@Override
	public void ganchoMonitorAtividade() {
		controleJogosServidor.removerJogosVaziosFinalizados();

	}

}
