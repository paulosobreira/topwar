package br.applet;

import br.nnpe.applet.NnpeApplet;
import br.nnpe.cliente.NnpeChatCliente;
import br.topwar.cliente.ControleChatCliente;

public class TopWarApplet extends NnpeApplet {

	private ControleChatCliente controleChatCliente = new ControleChatCliente(
			this);

	@Override
	public NnpeChatCliente getControleChatCliente() {
		return controleChatCliente;
	}

}
