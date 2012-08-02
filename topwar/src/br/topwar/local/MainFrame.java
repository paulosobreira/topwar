package br.topwar.local;

import javax.swing.WindowConstants;

import br.topwar.ProxyComandos;

public class MainFrame {

	private ServidorLocal servidorLocal;

	private ClienteLocal clienteLocal;
	private ProxyComandos proxyComandos;

	public MainFrame() {
		proxyComandos = new ProxyComandos();
		servidorLocal = new ServidorLocal(proxyComandos);
		clienteLocal = new ClienteLocal(proxyComandos);
		if (!clienteLocal.criarJogoDepoisDeLogar()) {
			System.exit(0);
		}
//		clienteLocal.getJogoCliente().getFrameTopWar().setVisible(false);
		clienteLocal.getJogoCliente().getFrameTopWar()
				.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame();
	}

}
