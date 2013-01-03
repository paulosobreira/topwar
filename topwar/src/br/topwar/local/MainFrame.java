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
import java.awt.event.WindowListener;
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
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.recursos.idiomas.Lang;

public class MainFrame {

	private ServidorLocal servidorLocal;
	private ClienteLocal clienteLocal;
	private ProxyComandos proxyComandos;
	private JFrame frameTopWar;
	private TopWarApplet topWarApplet;
	protected boolean jogoIniciado;

	public MainFrame(TopWarApplet topWarApplet) {
		this.topWarApplet = topWarApplet;
	}

	public void gerarJframeApplet() {
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
		};
	}

	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame(null);
	}

	public ClienteLocal getClienteLocal() {
		return clienteLocal;
	}

	public void iniciar() {
		proxyComandos = new ProxyComandos();
		servidorLocal = new ServidorLocal(proxyComandos);
		clienteLocal = new ClienteLocal(proxyComandos, topWarApplet);
		gerarJframeApplet();
		JMenuBar bar = new JMenuBar();
		JogoCliente jogoCliente = new JogoCliente(null, clienteLocal) {
			@Override
			public void setarFrameTopWar() {
				setFrameTopWar(frameTopWar);
			}
		};
		clienteLocal.setJogoCliente(jogoCliente);
		frameTopWar.setJMenuBar(bar);
		JMenu menuJogo = new JMenu() {
			public String getText() {
				return Lang.msg("principal");
			}

		};
		bar.add(menuJogo);
		JMenuItem iniciar = new JMenuItem("Iniciar Jogo") {
			public String getText() {
				return Lang.msg("iniciar");
			}

		};
		iniciar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clienteLocal.criarJogoDepoisDeLogar(true);
				jogoIniciado = true;
			}
		});
		menuJogo.add(iniciar);
		JMenuItem sobre = new JMenuItem("Sobre o autor do jogo") {
			public String getText() {
				return Lang.msg("sobre");
			}

		};
		sobre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = Lang.msg("feitoPor")
						+ " Paulo Sobreira \n sowbreira@gmail.com \n"
						+ "http://sowbreira.appspot.com \n" + "2007-2012";
				JOptionPane.showMessageDialog(frameTopWar, msg,
						Lang.msg("093"), JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menuJogo.add(sobre);
		frameTopWar.setTitle(Lang.msg("topawrsolo"));
		final BufferedImage img = ImageUtil.gerarFade(
				CarregadorRecursos.carregaBackGround("mercs-chat.png"), 50);
		frameTopWar.getContentPane().add(new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				if (!jogoIniciado) {
					super.paintComponent(g);
					Graphics2D graphics2d = (Graphics2D) g;
					if (img != null)
						graphics2d.drawImage(img, null, 0, 0);
				}
			}
		});
	}
}
