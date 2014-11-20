package br.topwar.local;

import javax.swing.JOptionPane;

import br.nnpe.Logger;
import br.nnpe.cliente.NnpeApplet;
import br.nnpe.tos.ErroServ;
import br.nnpe.tos.MsgSrv;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.topwar.ProxyComandos;
import br.topwar.cliente.ControleCliente;
import br.topwar.cliente.TopWarAppletLocal;
import br.topwar.recursos.idiomas.Lang;

public class ClienteLocal extends ControleCliente {

	private ProxyComandos comandos;

	public ClienteLocal(NnpeApplet topWarApplet) {
		super(null);
		this.nnpeApplet = topWarApplet;
	}

	public ClienteLocal(ProxyComandos comandos, TopWarAppletLocal topWarApplet) {
		super(null);
		this.nnpeApplet = topWarApplet;
		this.comandos = comandos;
		this.sessaoCliente = new SessaoCliente();
		sessaoCliente.setNomeJogador("TopWar");
		sessaoCliente.setUlimaAtividade(System.currentTimeMillis());
		definirImplementacaoChatWindow();
	}

	@Override
	public Object enviarObjeto(NnpeTO nnpeTO) {
		if (sessaoCliente == null) {
			return null;
		}
		sessaoCliente.setUlimaAtividade(System.currentTimeMillis());
		nnpeTO.setSessaoCliente(sessaoCliente);
		Object retorno = comandos.processarObjeto(nnpeTO);
		if (retorno instanceof ErroServ) {
			ErroServ erroServ = (ErroServ) retorno;
			Logger.logar(erroServ.obterErroFormatado());
			JOptionPane.showMessageDialog(null,
					Lang.decodeTexto(erroServ.obterErroFormatado()),
					Lang.msg("erroRecebendo"), JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (retorno instanceof MsgSrv) {
			MsgSrv msgSrv = (MsgSrv) retorno;
			JOptionPane.showMessageDialog(null,
					Lang.msg(Lang.decodeTexto(msgSrv.getMessageString())),
					Lang.msg("msgServidor"), JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
		return retorno;
	}
}
