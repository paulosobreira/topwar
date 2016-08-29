package br.topwar.local;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.WindowConstants;

import br.nnpe.Logger;
import br.topwar.ProxyComandos;
import br.topwar.cliente.JogoCliente;
import br.topwar.cliente.TopWarAppletLocal;
import br.topwar.recursos.idiomas.Lang;

public class MainFrame {

	private ServidorLocal servidorLocal;
	private ClienteLocal clienteLocal;
	private ProxyComandos proxyComandos;
	private JFrame frameTopWar;
	private TopWarAppletLocal topWarApplet;
	protected boolean jogoIniciado;
	private PainelMenu painelMenu;
	private String codeBase;

	public MainFrame(TopWarAppletLocal topWarApplet) {
		this.topWarApplet = topWarApplet;
	}

	public MainFrame(String codeBase) {
		this.codeBase = codeBase;
	}

	public static void main(String[] args) {
		String codeBase = File.separator + "WebContent" + File.separator;
		if (args != null && args.length > 0) {
			codeBase = args[0];
		}
		if (args != null && args.length > 1) {
			Lang.mudarIdioma(args[1]);
		}

		MainFrame mainFrame = new MainFrame(codeBase);
		mainFrame.iniciar(true);
	}

	public ClienteLocal getClienteLocal() {
		return clienteLocal;
	}

	public void iniciar(boolean visivel) {
		proxyComandos = new ProxyComandos();
		servidorLocal = new ServidorLocal(proxyComandos);
		proxyComandos.setControleJogosServidor(servidorLocal);
		gerarJFrameApplet();
		String versao = topWarApplet.getVersao();
		frameTopWar.setTitle(Lang.msg("topawrsolo") + " Ver. " + versao);
		Logger.logar("frameTopWar.setTitle(Lang.msg(versao);");
		frameTopWar.setVisible(visivel);
		Logger.logar("frameTopWar.setVisible(visivel);");
		painelMenu = new PainelMenu(this);
		Logger.logar("painelMenu = new PainelMenu(this);");
		frameTopWar.setSize(800, 600);
		Logger.logar("frameTopWar.setSize(800, 600);");
		frameTopWar
				.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frameTopWar.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int ret = JOptionPane.showConfirmDialog(frameTopWar,
						Lang.msg("confirmaSair"), Lang.msg("sair"),
						JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.NO_OPTION) {
					return;
				}
				if (clienteLocal != null) {
					clienteLocal.sairJogo();
					clienteLocal.getJogoCliente().matarTodasThreads();
				}
				if (servidorLocal != null) {
					servidorLocal.finalizaJogosServidor();
				}
				if (clienteLocal!=null && clienteLocal.isLocal()) {
					System.exit(0);
				}
				
				if (clienteLocal==null) {
					System.exit(0);
				}
				super.windowClosing(e);
			}
		});
	}

	public JFrame getFrameTopWar() {
		return frameTopWar;
	}

	public void mostrarGraficos() {
		BufferStrategy strategy = getFrameTopWar().getBufferStrategy();
		strategy.getDrawGraphics().dispose();
		strategy.show();
	}

	public Graphics2D obterGraficos() {
		BufferStrategy strategy = getFrameTopWar().getBufferStrategy();
		if (strategy == null) {
			getFrameTopWar().createBufferStrategy(2);
			strategy = getFrameTopWar().getBufferStrategy();
		}
		return (Graphics2D) strategy.getDrawGraphics();
	}

	public void gerarJFrameApplet() {
		if (topWarApplet == null) {
			frameTopWar = new JFrame();
			topWarApplet = new TopWarAppletLocal(codeBase);
			return;
		}
		topWarApplet.init();
		frameTopWar = new JFrame() {
			@Override
			public Container getContentPane() {
				return topWarApplet.getContentPane();
			}

			@Override
			public JRootPane getRootPane() {
				return topWarApplet.getRootPane();
			}

			@Override
			public void remove(Component comp) {
				topWarApplet.remove(comp);
			}

			@Override
			public void setTitle(String title) {
				Component parent = topWarApplet;
				while (parent.getParent() != null)
					parent = parent.getParent();
				if (parent instanceof Frame) {
					((Frame) parent).setTitle(title);
				}
			}

			@Override
			public synchronized void addWindowListener(WindowListener l) {
				Component parent = topWarApplet;
				while (parent.getParent() != null)
					parent = parent.getParent();
				if (parent instanceof Frame) {
					((Frame) parent).addWindowListener(l);
				}
			}

			@Override
			public synchronized void addKeyListener(KeyListener l) {
				topWarApplet.addKeyListener(l);
			}

			@Override
			public Container getParent() {
				return topWarApplet.getParent();
			}

			@Override
			public synchronized void addMouseWheelListener(
					MouseWheelListener l) {
				topWarApplet.addMouseWheelListener(l);
			}

			@Override
			public synchronized void removeKeyListener(KeyListener l) {
				topWarApplet.removeKeyListener(l);
			}

			@Override
			public synchronized KeyListener[] getKeyListeners() {
				return topWarApplet.getKeyListeners();
			}

			@Override
			public void repaint() {
				topWarApplet.repaint();
			}

			@Override
			public void requestFocus() {
				topWarApplet.requestFocus();
			}

			@Override
			public void update(Graphics g) {
				topWarApplet.update(g);
			}

			@Override
			public void doLayout() {
				topWarApplet.doLayout();
			}

			@Override
			public void pack() {
				topWarApplet.update(topWarApplet.getGraphics());
			}
		};
	}

	public void iniciar() {
		iniciar(false);
	}

	public void criarJogoLocal(PainelMenu painelMenu) {
		servidorLocal.finalizaJogosServidor();
		clienteLocal = new ClienteLocal(proxyComandos, topWarApplet);
		if (clienteLocal.getJogoCliente() != null
				&& clienteLocal.getJogoCliente().isJogoEmAndamento()) {
			JOptionPane.showMessageDialog(frameTopWar,
					Lang.msg("jaEstaEmUmJogo"), "TopWar",
					JOptionPane.INFORMATION_MESSAGE);
			Logger.logar("criarJogoLocal jaEstaEmUmJogo ");
			return;
		}
		JogoCliente jogoCliente = new JogoCliente(null, clienteLocal) {
			@Override
			public void setarFrameTopWar() {
				setFrameTopWar(frameTopWar);
			}
		};
		Logger.logar("criarJogoLocal");
		clienteLocal.setJogoCliente(jogoCliente);
		clienteLocal.criarJogoLocal(true, painelMenu);
		jogoIniciado = true;
		painelMenu.renderThreadAlive = false;

	}
}
