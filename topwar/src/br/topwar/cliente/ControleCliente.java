package br.topwar.cliente;

import br.nnpe.cliente.NnpeApplet;
import br.nnpe.cliente.NnpeChatCliente;
import br.nnpe.tos.NnpeTO;
import br.topwar.ConstantesTopWar;
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
		nnpeTO.setData(dadosJogoTopWar);
		Object ret = enviarObjeto(nnpeTO);
		if (nnpeTO instanceof NnpeTO) {
			nnpeTO = (NnpeTO) ret;
			dadosJogoTopWar = (DadosJogoTopWar) nnpeTO.getData();
			jogoCliente = new JogoCliente(dadosJogoTopWar, this);
			jogoCliente.inciaJogo();
		}
	}

	public void moverEsquerda() {
		// TODO Auto-generated method stub
		
	}

	public void moverBaixo() {
		// TODO Auto-generated method stub
		
	}

	public void moverDireita() {
		// TODO Auto-generated method stub
		
	}

	public void moverCima() {
		// TODO Auto-generated method stub
		
	}
}
