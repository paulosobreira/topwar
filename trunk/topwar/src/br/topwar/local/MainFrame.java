package br.topwar.local;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import br.nnpe.ImageUtil;
import br.topwar.ProxyComandos;
import br.topwar.cliente.JogoCliente;
import br.topwar.cliente.PainelMenu;
import br.topwar.cliente.TopWarAppletLocal;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.recursos.idiomas.Lang;

public class MainFrame {

	private ServidorLocal servidorLocal;
	private ClienteLocal clienteLocal;
	private ProxyComandos proxyComandos;
	private JFrame frameTopWar;
	private TopWarAppletLocal topWarApplet;
	protected boolean jogoIniciado;
	private PainelMenu painelMenu;

	public MainFrame(TopWarAppletLocal topWarApplet) {
		this.topWarApplet = topWarApplet;
	}

	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame(null);
		mainFrame.iniciar(true);

	}

	public ClienteLocal getClienteLocal() {
		return clienteLocal;
	}

	public void iniciar(boolean visivel) {
		proxyComandos = new ProxyComandos();
		servidorLocal = new ServidorLocal(proxyComandos);
		proxyComandos.setControleJogosServidor(servidorLocal);
		clienteLocal = new ClienteLocal(proxyComandos, topWarApplet);
		gerarJframeApplet();
		String versao = topWarApplet.getVersao();
		frameTopWar.setTitle(Lang.msg("topawrsolo") + " Ver. " + versao);
		painelMenu = new PainelMenu(this);

		frameTopWar.setVisible(visivel);
		frameTopWar.setSize(800, 520);
		frameTopWar.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				clienteLocal.sairJogo();
				clienteLocal.getJogoCliente().matarTodasThreads();
				servidorLocal.finalizaJogosServidor();
				super.windowClosed(e);
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

	public void gerarJframeApplet() {
		if (topWarApplet == null) {
			frameTopWar = new JFrame();
			topWarApplet = new TopWarAppletLocal();
			return;
		}
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
			public synchronized void addMouseWheelListener(MouseWheelListener l) {
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
		if (clienteLocal.getJogoCliente() != null
				&& clienteLocal.getJogoCliente().isJogoEmAndamento()) {
			JOptionPane.showMessageDialog(frameTopWar,
					Lang.msg("jaEstaEmUmJogo"), "TopWar",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JogoCliente jogoCliente = new JogoCliente(null, clienteLocal) {
			@Override
			public void setarFrameTopWar() {
				setFrameTopWar(frameTopWar);
			}
		};
		clienteLocal.setJogoCliente(jogoCliente);
		clienteLocal.criarJogoLocal(true, painelMenu);
		jogoIniciado = true;
		painelMenu.renderThreadAlive = false;

	}
}
