package br.nnpe.servidor;

import br.nnpe.tos.MsgSrv;
import br.nnpe.tos.NnpeCliente;
import br.nnpe.tos.NnpeDados;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.topwar.recursos.idiomas.Lang;

public class NnpeChatServidor {
	private NnpeDados nnpeDados;

	public NnpeChatServidor(NnpeDados nnpeDados) {
		this.nnpeDados = nnpeDados;
	}

	public Object receberTexto(NnpeCliente cliente) {
		if (cliente.getSessaoCliente() == null) {
			return (new MsgSrv(Lang.msg("usuarioSemSessao")));
		}
		nnpeDados
				.atualizaAtividade(cliente.getSessaoCliente().getNomeJogador());
		nnpeDados.setLinhaChat(cliente.getSessaoCliente().getNomeJogador()
				+ " : " + cliente.getTextoChat());
		nnpeDados.setDataTime(System.currentTimeMillis());
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setData(nnpeDados);
		return mesa11to;
	}

	public void atualizaSessaoCliente(SessaoCliente sessaoCliente) {
		nnpeDados.atualizaAtividade(sessaoCliente.getNomeJogador());
	}

}
