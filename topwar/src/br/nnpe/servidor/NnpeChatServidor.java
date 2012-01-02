package br.nnpe.servidor;

import br.nnpe.tos.MsgSrv;
import br.nnpe.tos.NnpeCliente;
import br.nnpe.tos.NnpeDadosChat;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.topwar.recursos.idiomas.Lang;

public class NnpeChatServidor {
	private NnpeDadosChat nnpeDadosChat;

	public NnpeChatServidor(NnpeDadosChat nnpeDadosChat) {
		this.nnpeDadosChat = nnpeDadosChat;
	}

	public Object receberTexto(NnpeCliente cliente) {
		if (cliente.getSessaoCliente() == null) {
			return (new MsgSrv(Lang.msg("usuarioSemSessao")));
		}
		nnpeDadosChat.atualizaAtividade(cliente.getSessaoCliente()
				.getNomeJogador());
		nnpeDadosChat.setLinhaChat(cliente.getSessaoCliente().getNomeJogador()
				+ " : " + cliente.getTextoCapcha());
		nnpeDadosChat.setDataTime(System.currentTimeMillis());
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setData(nnpeDadosChat);
		return mesa11to;
	}

	public void atualizaSessaoCliente(SessaoCliente sessaoCliente) {
		nnpeDadosChat.atualizaAtividade(sessaoCliente.getNomeJogador());
	}

}
