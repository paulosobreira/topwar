package br.topwar.cliente;

import br.nnpe.cliente.NnpeApplet;
import br.nnpe.cliente.NnpeChatCliente;

public class TopWarApplet extends NnpeApplet {

	private ControleCliente controleChatCliente = new ControleCliente(this);

	@Override
	public NnpeChatCliente getNnpeChatCliente() {
		return controleChatCliente;
	}

}
