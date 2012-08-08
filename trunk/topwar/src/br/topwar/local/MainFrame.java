package br.topwar.local;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import br.topwar.ProxyComandos;
import br.topwar.cliente.JogoCliente;
import br.topwar.recursos.idiomas.Lang;

public class MainFrame {

	private ServidorLocal servidorLocal;

	private ClienteLocal clienteLocal;
	private ProxyComandos proxyComandos;

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
