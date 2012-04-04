package br.topwar.local;

import br.nnpe.tos.NnpeDados;
import br.nnpe.tos.SessaoCliente;
import br.topwar.ProxyComandos;
import br.topwar.servidor.ControleJogosServidor;
import br.topwar.servidor.ControlePersistencia;

public class ServidorLocal extends ControleJogosServidor {

	public ServidorLocal(NnpeDados nnpeDados,
			ControlePersistencia controlePersistencia,
			ProxyComandos proxyComandos) {
		super(nnpeDados, controlePersistencia, proxyComandos);
	}

	public ServidorLocal(ProxyComandos proxyComandos) {
		super(null, null, proxyComandos);
		SessaoCliente sessaoCliente = new SessaoCliente();
		sessaoCliente.setNomeJogador("Jogador");
		sessaoCliente.setUlimaAtividade(System.currentTimeMillis());
		proxyComandos.getNnpeDadosChat().getClientes().add(sessaoCliente);
	}

}
