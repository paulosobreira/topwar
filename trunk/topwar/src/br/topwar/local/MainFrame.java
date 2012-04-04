package br.topwar.local;

import br.topwar.ProxyComandos;

public class MainFrame {

	private ServidorLocal servidorLocal;

	private ClienteLocal clienteLocal;
	private ProxyComandos proxyComandos;

	public MainFrame() {
		proxyComandos = new ProxyComandos(null, null);
		servidorLocal = new ServidorLocal(proxyComandos);
		clienteLocal = new ClienteLocal(proxyComandos);
		clienteLocal.criarJogoSemLogar();
	}

	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame();
	}

}
