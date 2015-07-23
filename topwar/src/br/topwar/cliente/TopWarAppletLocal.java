package br.topwar.cliente;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.net.MalformedURLException;
import java.net.URL;

import br.nnpe.cliente.NnpeApplet;
import br.nnpe.cliente.NnpeChatCliente;
import br.topwar.local.MainFrame;

public class TopWarAppletLocal extends NnpeApplet {

	private static final long serialVersionUID = 7350448978168350244L;
	MainFrame frameTopWar;
	private String codeBase;

	public TopWarAppletLocal(String codeBase) {
		this.codeBase = codeBase;
	}

	public TopWarAppletLocal() {
	}

	public URL getCodeBase() {
		try {
			return new URL(codeBase);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void init() {
		Component parent = this;
		while (parent.getParent() != null)
			parent = parent.getParent();
		if (parent instanceof Frame) {
			if (!((Frame) parent).isResizable()) {
				((Frame) parent).setResizable(true);
				((Frame) parent).setLayout(new GridLayout());
			}
		}
	}

	@Override
	public NnpeChatCliente getNnpeChatCliente() {
		return null;
	}

	@Override
	public void destroy() {
	}
}
