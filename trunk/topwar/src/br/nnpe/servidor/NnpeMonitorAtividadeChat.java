package br.nnpe.servidor;

import java.util.Collection;
import java.util.Iterator;

import br.nnpe.Logger;
import br.nnpe.tos.SessaoCliente;

/**
 * @author Paulo Sobreira Criado em 25/08/2007 as 11:22:46
 */
public class NnpeMonitorAtividadeChat extends Thread {

	private NnpeProxyComandos proxyComandos;
	private boolean viva = true;

	public NnpeMonitorAtividadeChat(NnpeProxyComandos proxyComandos) {
		this.proxyComandos = proxyComandos;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (viva) {
			try {
				sleep(5000);
				long timeNow = System.currentTimeMillis();
				Collection<SessaoCliente> clientes = proxyComandos
						.getNnpeDadosChat().getClientes();
				SessaoCliente sessaoClienteRemover = null;
				for (Iterator iter = clientes.iterator(); iter.hasNext();) {
					SessaoCliente sessaoCliente = (SessaoCliente) iter.next();
					if ((timeNow - sessaoCliente.getUlimaAtividade()) > 100000) {
						sessaoClienteRemover = sessaoCliente;
						break;
					}
				}
				if (sessaoClienteRemover != null) {
					proxyComandos.removerClienteInativo(sessaoClienteRemover);
				}
			} catch (Exception e) {
				Logger.logarExept(e);
			}
		}

	}

	private void dormir(long l) {
		try {
			Thread.sleep(l);
		} catch (InterruptedException e) {
			Logger.logarExept(e);
		}

	}

}
