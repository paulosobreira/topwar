package br.servlet;

import javax.servlet.ServletException;

import br.nnpe.Logger;
import br.nnpe.servidor.NnpeServlet;
import br.topwar.ProxyComandos;

/**
 * @author paulo.sobreira
 * 
 */
public class ServletTopWar extends NnpeServlet {

	public void init() throws ServletException {
		super.init();
		proxyComandos = new ProxyComandos(webDir, webInfDir);
		try {
			atualizarJnlp("topwarOnline.jnlp");
			atualizarJnlp("topwar.jnlp");
		} catch (Exception e) {
			Logger.logarExept(e);
		}
	}
	
	@Override
	public void destroy() {
		proxyComandos.pararMonitor();
		super.destroy();
	}
}
