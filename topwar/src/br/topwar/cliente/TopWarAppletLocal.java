package br.topwar.cliente;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;

import br.nnpe.cliente.NnpeApplet;
import br.nnpe.cliente.NnpeChatCliente;
import br.topwar.local.MainFrame;

public class TopWarAppletLocal extends NnpeApplet {

	MainFrame frameTopWar;

	@Override
	public void init() {
		frameTopWar = new MainFrame(this);
		Component parent = this;
		while (parent.getParent() != null)
			parent = parent.getParent();
		if (parent instanceof Frame) {
			if (!((Frame) parent).isResizable()) {
				((Frame) parent).setResizable(true);
				((Frame) parent).setLayout(new GridLayout());
			}
		}
		frameTopWar.iniciar();
	}

	@Override
	public NnpeChatCliente getNnpeChatCliente() {
		return null;
	}

	@Override
	public void destroy() {
	}
}
