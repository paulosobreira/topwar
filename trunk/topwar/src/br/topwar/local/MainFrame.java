package br.topwar.local;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import br.nnpe.ImageUtil;
import br.topwar.ProxyComandos;
import br.topwar.cliente.JogoCliente;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.recursos.idiomas.Lang;

public class MainFrame {

	private ServidorLocal servidorLocal;

	private ClienteLocal clienteLocal;
	private ProxyComandos proxyComandos;

	protected boolean jogoIniciado;

	public MainFrame() {
		proxyComandos = new ProxyComandos();
		servidorLocal = new ServidorLocal(proxyComandos);
		clienteLocal = new ClienteLocal(proxyComandos);
		JMenuBar bar = new JMenuBar();
		clienteLocal.setJogoCliente(new JogoCliente(null, clienteLocal));
		clienteLocal.getJogoCliente().iniciaJFrame();
		final JFrame frameTopWar = clienteLocal.getJogoCliente()
				.getFrameTopWar();
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
		frameTopWar.setVisible(true);

		// clienteLocal.criarJogoDepoisDeLogar(true);
		// clienteLocal.getJogoCliente().getFrameTopWar().setVisible(false);

		clienteLocal.getJogoCliente().getFrameTopWar()
				.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame();
	}

}
