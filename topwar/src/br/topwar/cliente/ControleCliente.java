package br.topwar.cliente;

import br.nnpe.cliente.NnpeApplet;
import br.nnpe.cliente.NnpeChatCliente;
import br.nnpe.tos.NnpeTO;
import br.topwar.ConstantesTopWar;
import br.topwar.tos.DadosAcaoClienteTopWar;
import br.topwar.tos.DadosJogoTopWar;

public class ControleCliente extends NnpeChatCliente {

	private DadosJogoTopWar dadosJogoTopWar;
	private JogoCliente jogoCliente;

	public ControleCliente(NnpeApplet topWarApplet) {
		super(topWarApplet);
	}

	@Override
	public void definirImplementacaoChatWindow() {
		this.nnpeChatWindow = new ChatWindow(this);
	}

	public void criarJogo() {
		if (sessaoCliente == null) {
			logar();
			return;
		}
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.CRIAR_JOGO);
		DadosJogoTopWar dadosJogoTopWar = new DadosJogoTopWar();
		dadosJogoTopWar.setNomeCriadorJogo(getNomeJogador());
		dadosJogoTopWar.setNomeMapa("mapa9");
		nnpeTO.setData(dadosJogoTopWar);
		Object ret = enviarObjeto(nnpeTO);
		if (nnpeTO instanceof NnpeTO) {
			nnpeTO = (NnpeTO) ret;
			dadosJogoTopWar = (DadosJogoTopWar) nnpeTO.getData();
			jogoCliente = new JogoCliente(dadosJogoTopWar, this);
			jogoCliente.inciaJogo();
		}
	}

	public String getNomeJogador() {
		if (sessaoCliente == null) {
			logar();
			return "Sem Sessao";
		}
		return sessaoCliente.getNomeJogador();
	}

	private void mover(String mover) {
		DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
		acaoClienteTopWar.setNomeCliente(sessaoCliente.getNomeJogador());
		acaoClienteTopWar.setAngulo(jogoCliente.getAngulo());
		acaoClienteTopWar.setMoverPara(mover);
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.MOVER);
		nnpeTO.setData(acaoClienteTopWar);
		enviarObjeto(nnpeTO);
	}

	public void moverEsquerda() {
		mover(ConstantesTopWar.ESQUERDA);
	}

	public void moverBaixo() {
		mover(ConstantesTopWar.BAIXO);
	}

	public void moverDireita() {
		mover(ConstantesTopWar.DIREITA);
	}

	public void moverCima() {
		mover(ConstantesTopWar.CIMA);
	}
}
