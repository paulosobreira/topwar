package br.topwar.cliente;

import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.JApplet;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import br.nnpe.GeoUtil;
import br.nnpe.Util;
import br.nnpe.cliente.NnpeApplet;
import br.nnpe.cliente.NnpeChatCliente;
import br.nnpe.tos.NnpeTO;
import br.topwar.ConstantesTopWar;
import br.topwar.local.PainelMenu;
import br.topwar.recursos.idiomas.Lang;
import br.topwar.tos.DadosAcaoClienteTopWar;
import br.topwar.tos.DadosJogoTopWar;

public class ControleCliente extends NnpeChatCliente {

	private JogoCliente jogoCliente;
	private PainelMenu painelMenu;
	protected long ultAcao;
	private boolean local;

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
		criarJogoLogadoSwing(false);
	}

	public boolean criarJogoLogadoSwing(boolean local) {
		this.local = local;
		JPanel mapaPanel = new JPanel();
		JComboBox mapaCombo = new JComboBox();
		mapaCombo.addItem("mapa9");
		mapaCombo.addItem("mapa16");
		mapaPanel.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("mapa");
			}

		});
		mapaPanel.add(mapaCombo);

		JPanel classePanel = new JPanel();

		JComboBox classesCombo = new JComboBox();
		classesCombo.addItem(Lang.msg(ConstantesTopWar.ROCKET));
		classesCombo.addItem(Lang.msg(ConstantesTopWar.SNIPER));
		classesCombo.addItem(Lang.msg(ConstantesTopWar.SHIELD));
		classesCombo.addItem(Lang.msg(ConstantesTopWar.ASSAULT));
		classesCombo.addItem(Lang.msg(ConstantesTopWar.SHOTGUN));
		classesCombo.addItem(Lang.msg(ConstantesTopWar.MACHINEGUN));
		classePanel.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("classe");
			}

		});
		classePanel.add(classesCombo);

		JPanel botPanel = new JPanel();
		JComboBox botCombo = new JComboBox();
		botCombo.addItem(0);
		botCombo.addItem(1);
		botCombo.addItem(5);
		botCombo.addItem(10);
		botCombo.addItem(20);
		botCombo.addItem(30);
		botCombo.addItem(40);
		botCombo.addItem(50);
		botCombo.addItem(60);
		botPanel.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("bots");
			}
		});
		botPanel.add(botCombo);

		JPanel botsVsHumansPanel = new JPanel();
		JCheckBox botsVsHumansCheckBox = new JCheckBox();
		botsVsHumansPanel.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("botsVsHumanos");
			}
		});
		botsVsHumansPanel.add(botsVsHumansCheckBox);

		JPanel painelentrada = new JPanel(new GridLayout(2, 2));
		painelentrada.add(mapaPanel);
		painelentrada.add(classePanel);
		painelentrada.add(botPanel);
		painelentrada.add(botsVsHumansPanel);

		int result = JOptionPane.showConfirmDialog(
				this.nnpeChatWindow.getMainPanel(), painelentrada,
				Lang.msg("criarJogo"), JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			NnpeTO nnpeTO = new NnpeTO();
			nnpeTO.setComando(ConstantesTopWar.CRIAR_JOGO);
			nnpeTO.setSessaoCliente(sessaoCliente);
			DadosJogoTopWar dadosJogoTopWar = new DadosJogoTopWar();
			dadosJogoTopWar.setNomeJogador(getNomeJogador());
			dadosJogoTopWar.setNomeMapa((String) mapaCombo.getSelectedItem());
			String classe = (String) classesCombo.getSelectedItem();
			dadosJogoTopWar.setClasse(Lang.key(classe));
			Integer numBots = (Integer) botCombo.getSelectedItem();
			dadosJogoTopWar.setNumBots(numBots);
			dadosJogoTopWar.setBotsVsHumans(botsVsHumansCheckBox.isSelected());
			nnpeTO.setData(dadosJogoTopWar);
			Object ret = enviarObjeto(nnpeTO);
			if (ret instanceof NnpeTO) {
				nnpeTO = (NnpeTO) ret;
				dadosJogoTopWar = (DadosJogoTopWar) nnpeTO.getData();
				if (local) {
					jogoCliente.setDadosJogoTopWar(dadosJogoTopWar);
				} else {
					jogoCliente = new JogoCliente(dadosJogoTopWar, this);
				}
				jogoCliente.inciaJogo();
				// if (!local) {
				// jogoCliente.gerarRadio();
				// }
			}
		} else {
			return false;
		}
		return true;
	}

	public boolean criarJogoLocal(boolean local, PainelMenu painelMenu) {
		this.local = local;
		this.painelMenu = painelMenu;
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.CRIAR_JOGO);
		nnpeTO.setSessaoCliente(sessaoCliente);
		DadosJogoTopWar dadosJogoTopWar = new DadosJogoTopWar();
		dadosJogoTopWar.setNomeJogador(getNomeJogador());
		dadosJogoTopWar.setNomeMapa(painelMenu.getMapaSelecionado());
		String classe = painelMenu.getClasseSelecionada();
		dadosJogoTopWar.setClasse(classe);
		Integer numBots = painelMenu.getNumBotsSelecionado();
		dadosJogoTopWar.setNumBots(numBots);
		dadosJogoTopWar.setBotsVsHumans(false);
		//dadosJogoTopWar.setTempoJogo(painelMenu.getTempoJogoSelecionado());
		dadosJogoTopWar.setTempoJogo(1);
		nnpeTO.setData(dadosJogoTopWar);
		Object ret = enviarObjeto(nnpeTO);
		if (ret instanceof NnpeTO) {
			nnpeTO = (NnpeTO) ret;
			dadosJogoTopWar = (DadosJogoTopWar) nnpeTO.getData();
			if (local) {
				jogoCliente.setDadosJogoTopWar(dadosJogoTopWar);
			} else {
				jogoCliente = new JogoCliente(dadosJogoTopWar, this);
			}
			jogoCliente.inciaJogo();
			// if (!local) {
			// jogoCliente.gerarRadio();
			// }
		} else {
			return false;
		}
		return true;
	}

	public boolean isLocal() {
		return local;
	}

	public void entrarJogo() {
		if (sessaoCliente == null) {
			logar();
			return;
		}
		JPanel classesPanel = new JPanel();
		JComboBox classesCombo = new JComboBox();
		classesCombo.addItem(Lang.msg(ConstantesTopWar.ASSAULT));
		classesCombo.addItem(Lang.msg(ConstantesTopWar.SHOTGUN));
		classesCombo.addItem(Lang.msg(ConstantesTopWar.SNIPER));
		classesCombo.addItem(Lang.msg(ConstantesTopWar.MACHINEGUN));
		classesCombo.addItem(Lang.msg(ConstantesTopWar.ROCKET));
		classesCombo.addItem(Lang.msg(ConstantesTopWar.SHIELD));
		classesPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("classe");
			}
		});
		classesPanel.add(classesCombo);
		ChatWindow chatWindow = (ChatWindow) this.nnpeChatWindow;
		String nomeJogoSelecionado = chatWindow.obterJogoSelecionado();

		int result = JOptionPane.showConfirmDialog(
				this.nnpeChatWindow.getMainPanel(),
				classesPanel,
				Lang.msg("entrarJogo") + " "
						+ Lang.decodeTexto(nomeJogoSelecionado),
				JOptionPane.YES_NO_OPTION);
		if (result != JOptionPane.YES_OPTION) {
			return;
		}

		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.ENTRAR_JOGO);
		nnpeTO.setSessaoCliente(sessaoCliente);
		DadosJogoTopWar dadosJogoTopWar = new DadosJogoTopWar();
		dadosJogoTopWar.setNomeJogo(nomeJogoSelecionado);
		dadosJogoTopWar.setNomeJogador(sessaoCliente.getNomeJogador());
		dadosJogoTopWar.setClasse(Lang.key((String) classesCombo
				.getSelectedItem()));
		nnpeTO.setData(dadosJogoTopWar);
		Object ret = enviarObjeto(nnpeTO);
		if (ret instanceof NnpeTO) {
			nnpeTO = (NnpeTO) ret;
			dadosJogoTopWar = (DadosJogoTopWar) nnpeTO.getData();
			jogoCliente = new JogoCliente(dadosJogoTopWar, this);
			jogoCliente.inciaJogo();
			jogoCliente.gerarRadio();
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
		if (jogoCliente.verificaLag()) {
			return true;
		}
		return ((System.currentTimeMillis() - ultAcao) < getLatenciaMinima());
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
		DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
		acaoClienteTopWar.setNomeCliente(sessaoCliente.getNomeJogador());
		acaoClienteTopWar.setAngulo(jogoCliente.getAngulo());
		int range = GeoUtil.distaciaEntrePontos(jogoCliente.getPontoAvatar(),
				jogoCliente.getPontoMouseMovendo());
		if (jogoCliente.getArma() != ConstantesTopWar.ARMA_ROCKET)
			range += 50;
		acaoClienteTopWar.setRange(range);
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.ATACAR);
		nnpeTO.setData(acaoClienteTopWar);
		Object ret = enviarObjeto(nnpeTO);
		ultAcao = System.currentTimeMillis();
		return ret;
	}

	public void atualizaAngulo() {
		if (!Util.isNullOrEmpty(jogoCliente.getKillCam())) {
			return;
		}
		DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
		acaoClienteTopWar.setNomeCliente(sessaoCliente.getNomeJogador());
		acaoClienteTopWar.setAngulo(jogoCliente.getAngulo());
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.ATUALIZA_ANGULO);
		nnpeTO.setData(acaoClienteTopWar);
		Object ret = enviarObjeto(nnpeTO);
		ultAcao = System.currentTimeMillis();
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
		nnpeTO.setSessaoCliente(sessaoCliente);
		nnpeTO.setData(acaoClienteTopWar);
		return enviarObjeto(nnpeTO);
	}

	public void sairJogo() {
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.SAIR_JOGO);
		nnpeTO.setSessaoCliente(sessaoCliente);
		Object ret = enviarObjeto(nnpeTO);
	}

	public void mudarClasse(String classe) {
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.MUDAR_CLASSE);
		nnpeTO.setSessaoCliente(sessaoCliente);
		nnpeTO.setData(classe);
		Object ret = enviarObjeto(nnpeTO);

	}

	public void enviaTextoRadio(String text, boolean somenteTime) {
		if (verificaDelay()) {
			return;
		}
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setSessaoCliente(sessaoCliente);
		if (somenteTime) {
			nnpeTO.setComando(ConstantesTopWar.RADIO_TIME);
		} else {
			nnpeTO.setComando(ConstantesTopWar.RADIO_TODOS);
		}
		DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
		nnpeTO.setData(text);
		Object ret = enviarObjeto(nnpeTO);
		ultAcao = System.currentTimeMillis();
	}

	public JApplet getApplet() {
		return nnpeApplet;
	}

	@Override
	public void sair() {
		super.sair();
		if (jogoCliente != null) {
			jogoCliente.matarTodasThreads();
		}
	}

	public void voltaMenuPrincipal() {
		sair();
		painelMenu.inicializar();
	}

}
