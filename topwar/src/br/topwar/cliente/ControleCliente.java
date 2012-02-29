package br.topwar.cliente;

import java.awt.Point;

import br.nnpe.Util;
import br.nnpe.cliente.NnpeApplet;
import br.nnpe.cliente.NnpeChatCliente;
import br.nnpe.tos.NnpeTO;
import br.topwar.ConstantesTopWar;
import br.topwar.tos.DadosAcaoClienteTopWar;
import br.topwar.tos.DadosJogoTopWar;

public class ControleCliente extends NnpeChatCliente {

	private JogoCliente jogoCliente;
	protected long ultAcao;

	public JogoCliente getJogoCliente() {
		return jogoCliente;
	}

	public void setJogoCliente(JogoCliente jogoCliente) {
		this.jogoCliente = jogoCliente;
	}

	public long getUltAcao() {
		return ultAcao;
	}

	public void setUltAcao(long ultAcao) {
		this.ultAcao = ultAcao;
	}

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
		nnpeTO.setSessaoCliente(sessaoCliente);
		DadosJogoTopWar dadosJogoTopWar = new DadosJogoTopWar();
		dadosJogoTopWar.setNomeJogador(getNomeJogador());
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

	public void entrarJogo() {
		if (sessaoCliente == null) {
			logar();
			return;
		}
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.ENTRAR_JOGO);
		nnpeTO.setSessaoCliente(sessaoCliente);
		ChatWindow chatWindow = (ChatWindow) this.nnpeChatWindow;
		DadosJogoTopWar dadosJogoTopWar = new DadosJogoTopWar();
		dadosJogoTopWar.setNomeJogo(chatWindow.obterJogoSelecionado());
		dadosJogoTopWar.setNomeJogador(sessaoCliente.getNomeJogador());
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

	private Object mover(String mover) {
		if (!Util.isNullOrEmpty(jogoCliente.getKillCam())) {
			return null;
		}
		if (verificaDelay()) {
			return null;
		}
		DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
		acaoClienteTopWar.setNomeCliente(sessaoCliente.getNomeJogador());
		acaoClienteTopWar.setAngulo(jogoCliente.getAngulo());
		acaoClienteTopWar.setMoverPara(mover);
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.MOVER);
		nnpeTO.setData(acaoClienteTopWar);
		Object ret = enviarObjeto(nnpeTO);
		ultAcao = System.currentTimeMillis();
		return ret;
	}

	public boolean verificaDelay() {
		return ((System.currentTimeMillis() - ultAcao) < ConstantesTopWar.ATRASO_REDE_PADRAO);
	}

	public Object moverEsquerda() {
		return mover(ConstantesTopWar.ESQUERDA);
	}

	public Object moverBaixo() {
		return mover(ConstantesTopWar.BAIXO);
	}

	public Object moverDireita() {
		return mover(ConstantesTopWar.DIREITA);
	}

	public Object moverCima() {
		return mover(ConstantesTopWar.CIMA);
	}

	public Object atacar() {
		if (!Util.isNullOrEmpty(jogoCliente.getKillCam())) {
			return null;
		}
		if (verificaDelay()) {
			return null;
		}
		DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
		acaoClienteTopWar.setNomeCliente(sessaoCliente.getNomeJogador());
		acaoClienteTopWar.setAngulo(jogoCliente.getAngulo());
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.ATACAR);
		nnpeTO.setData(acaoClienteTopWar);
		Object ret = enviarObjeto(nnpeTO);
		ultAcao = System.currentTimeMillis();
		return ret;
	}

	public Object atualizaAngulo() {
		if (!Util.isNullOrEmpty(jogoCliente.getKillCam())) {
			return null;
		}
		if (verificaDelay()) {
			return null;
		}
		DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
		acaoClienteTopWar.setNomeCliente(sessaoCliente.getNomeJogador());
		acaoClienteTopWar.setAngulo(jogoCliente.getAngulo());
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.ATUALIZA_ANGULO);
		nnpeTO.setData(acaoClienteTopWar);
		Object ret = enviarObjeto(nnpeTO);
		ultAcao = System.currentTimeMillis();
		return ret;

	}

	public Object recarregar() {
		if (!Util.isNullOrEmpty(jogoCliente.getKillCam())) {
			return null;
		}
		if (verificaDelay()) {
			return null;
		}
		DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
		acaoClienteTopWar.setNomeCliente(sessaoCliente.getNomeJogador());
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.RECARREGAR);
		nnpeTO.setData(acaoClienteTopWar);
		Object ret = enviarObjeto(nnpeTO);
		ultAcao = System.currentTimeMillis();
		return ret;

	}

	public Object moverPonto(Point p) {
		if (!Util.isNullOrEmpty(jogoCliente.getKillCam())) {
			return null;
		}
		if (verificaDelay()) {
			return ConstantesTopWar.ESPERE;
		}
		DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
		acaoClienteTopWar.setNomeCliente(sessaoCliente.getNomeJogador());
		acaoClienteTopWar.setPonto(p);
		acaoClienteTopWar.setAngulo(jogoCliente.getAngulo());
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.MOVER_PONTO);
		nnpeTO.setData(acaoClienteTopWar);
		Object ret = enviarObjeto(nnpeTO);
		ultAcao = System.currentTimeMillis();
		return ret;

	}

	public Object alternaFaca() {
		if (!Util.isNullOrEmpty(jogoCliente.getKillCam())) {
			return null;
		}
		if (verificaDelay()) {
			return null;
		}
		DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
		acaoClienteTopWar.setNomeCliente(sessaoCliente.getNomeJogador());
		acaoClienteTopWar.setAngulo(jogoCliente.getAngulo());
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.ALTERNA_FACA);
		nnpeTO.setData(acaoClienteTopWar);
		Object ret = enviarObjeto(nnpeTO);
		ultAcao = System.currentTimeMillis();
		return ret;
	}

	public Object obterPlacar() {
		DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
		acaoClienteTopWar.setNomeCliente(sessaoCliente.getNomeJogador());
		acaoClienteTopWar.setAngulo(jogoCliente.getAngulo());
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.OBTER_PLCAR);
		nnpeTO.setData(acaoClienteTopWar);
		return enviarObjeto(nnpeTO);
	}

}
