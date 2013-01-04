package br.topwar.local;

import br.nnpe.tos.SessaoCliente;
import br.topwar.ProxyComandos;
import br.topwar.servidor.ControleJogosServidor;

public class ServidorLocal extends ControleJogosServidor {

	public ServidorLocal(ProxyComandos proxyComandos) {
		super(null, null, proxyComandos);
		SessaoCliente sessaoCliente = new SessaoCliente();
		sessaoCliente.setNomeJogador("TopWar");
		sessaoCliente.setUlimaAtividade(System.currentTimeMillis());
		proxyComandos.getNnpeDadosChat().getClientes().add(sessaoCliente);
	}

	public void removerClienteInativo(SessaoCliente sessaoClienteRemover) {
	}

}
