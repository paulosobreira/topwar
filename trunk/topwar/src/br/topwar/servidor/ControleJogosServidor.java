package br.topwar.servidor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.nnpe.tos.NnpeDados;
import br.nnpe.tos.NnpeTO;
import br.topwar.ProxyComandos;
import br.topwar.recursos.idiomas.Lang;
import br.topwar.tos.AvatarTopWar;
import br.topwar.tos.DadosAcaoClienteTopWar;
import br.topwar.tos.DadosJogoTopWar;

public class ControleJogosServidor {

	private ControlePersistencia controlePersistencia;

	private ProxyComandos proxyComandos;

	private Map<String, JogoServidor> mapaJogos = new HashMap<String, JogoServidor>();

	protected NnpeDados nnpeDados;

	private int contadorJogos = 0;

	public ControleJogosServidor(NnpeDados nnpeDados,
			ControlePersistencia controlePersistencia,
			ProxyComandos proxyComandos) {
		super();
		this.controlePersistencia = controlePersistencia;
		this.proxyComandos = proxyComandos;
		this.nnpeDados = nnpeDados;
	}

	public Object criarJogo(NnpeTO nnpeTO) {
		DadosJogoTopWar dadosJogoTopWar = (DadosJogoTopWar) nnpeTO.getData();
		dadosJogoTopWar.setNomeJogo(Lang.msg("jogo") + contadorJogos++);
		nnpeDados.getJogosAndamento().add(dadosJogoTopWar.getNomeJogo());
		JogoServidor jogoServidor = new JogoServidor(dadosJogoTopWar,
				proxyComandos);
		mapaJogos.put(dadosJogoTopWar.getNomeJogo(), jogoServidor);
		nnpeTO = new NnpeTO();
		nnpeTO.setData(dadosJogoTopWar);
		return nnpeTO;
	}

	public Object mover(NnpeTO nnpeTO) {
		DadosAcaoClienteTopWar acaoClienteTopWar = (DadosAcaoClienteTopWar) nnpeTO
				.getData();
		JogoServidor jogoServidor = obterJogoCliente(acaoClienteTopWar
				.getNomeCliente());
		if (jogoServidor == null) {
			return null;
		}
		AvatarTopWar avatarTopWar = obterAvatarTopWarCliente(acaoClienteTopWar
				.getNomeCliente());
		if (avatarTopWar == null) {
			return null;
		}
		jogoServidor.moverAvatar(avatarTopWar, acaoClienteTopWar);
		return null;

	}

	public Object atualizarListaAvatares(NnpeTO nnpeTO) {
		String nomeJogo = (String) nnpeTO.getData();
		JogoServidor jogoServidor = obterJogo(nomeJogo);
		if (jogoServidor != null) {
			nnpeTO.setData(jogoServidor.getAvatarTopWars());
			return nnpeTO;
		}
		return null;
	}

	private JogoServidor obterJogo(String nomeJogo) {
		return mapaJogos.get(nomeJogo);
	}

	private AvatarTopWar obterAvatarTopWarCliente(String nomeCliente) {
		Collection jogosAndamento = nnpeDados.getJogosAndamento();
		for (Iterator iterator = jogosAndamento.iterator(); iterator.hasNext();) {
			String nmJogog = (String) iterator.next();
			JogoServidor jogoServidor = mapaJogos.get(nmJogog);
			if (jogoServidor != null) {

				List<AvatarTopWar> avatarTopWars = jogoServidor
						.getAvatarTopWars();
				for (Iterator iterator2 = avatarTopWars.iterator(); iterator2
						.hasNext();) {
					AvatarTopWar avatarTopWar = (AvatarTopWar) iterator2.next();
					if (nomeCliente.equals(avatarTopWar.getNomeJogador())) {
						return avatarTopWar;
					}
				}

			}
		}
		return null;
	}

	private JogoServidor obterJogoCliente(String nomeCliente) {
		Collection jogosAndamento = nnpeDados.getJogosAndamento();
		for (Iterator iterator = jogosAndamento.iterator(); iterator.hasNext();) {
			String nmJogog = (String) iterator.next();
			JogoServidor jogoServidor = mapaJogos.get(nmJogog);
			if (jogoServidor != null) {
				List<AvatarTopWar> avatarTopWars = jogoServidor
						.getAvatarTopWars();
				for (Iterator iterator2 = avatarTopWars.iterator(); iterator2
						.hasNext();) {
					AvatarTopWar avatarTopWar = (AvatarTopWar) iterator2.next();
					if (nomeCliente.equals(avatarTopWar.getNomeJogador())) {
						return jogoServidor;
					}
				}
			}
		}
		return null;
	}
}
