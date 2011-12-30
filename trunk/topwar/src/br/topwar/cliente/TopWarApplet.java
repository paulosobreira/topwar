package br.topwar.cliente;

import br.nnpe.cliente.NnpeApplet;
import br.nnpe.cliente.NnpeChatCliente;

public class TopWarApplet extends NnpeApplet {

	private ControleChatCliente controleChatCliente = new ControleChatCliente(
			this);

	@Override
	public NnpeChatCliente getControleChatCliente() {
		return controleChatCliente;
	}

}
