package br.topwar.servidor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.nnpe.Logger;
import br.nnpe.tos.MsgSrv;
import br.nnpe.tos.NnpeDados;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.topwar.ConstantesTopWar;
import br.topwar.ProxyComandos;
import br.topwar.recursos.idiomas.Lang;
import br.topwar.tos.DadosAcaoClienteTopWar;
import br.topwar.tos.DadosJogoTopWar;
import br.topwar.tos.ObjTopWar;

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
		if (nnpeTO.getSessaoCliente() == null) {
			return new MsgSrv(Lang.msg("usuarioSemSessao"));
		}
		if (verificaJaEmUmJogo(nnpeTO.getSessaoCliente())) {
			Logger.logar("criarJogo jaEstaEmUmJogo ");
			return new MsgSrv(Lang.msg("jaEstaEmUmjogo"));
		}
		if (mapaJogos.size() >= ConstantesTopWar.NUMERO_JOGOS) {
			return new MsgSrv(Lang.msg("numeroMaximoJogos", new String[] { ""
					+ ConstantesTopWar.NUMERO_JOGOS }));
		}
		DadosJogoTopWar dadosJogoTopWar = (DadosJogoTopWar) nnpeTO.getData();
		dadosJogoTopWar.setNomeJogo(Lang.msg("jogo") + contadorJogos++);
		nnpeDados.getJogosAndamento().add(dadosJogoTopWar.getNomeJogo());
		JogoServidor jogoServidor = new JogoServidor(dadosJogoTopWar,
				proxyComandos, this);
		mapaJogos.put(dadosJogoTopWar.getNomeJogo(), jogoServidor);
		nnpeTO = new NnpeTO();
		nnpeTO.setData(dadosJogoTopWar);
		return nnpeTO;
	}

	private boolean verificaJaEmUmJogo(SessaoCliente sessaoCliente) {
		return obterJogoCliente(sessaoCliente.getNomeJogador()) != null;
	}

	public Object entrarJogo(NnpeTO nnpeTO) {
		if (nnpeTO.getSessaoCliente() == null) {
			return new MsgSrv(Lang.msg("usuarioSemSessao"));
		}
		if (verificaJaEmUmJogo(nnpeTO.getSessaoCliente())) {
			Logger.logar("criarJogo jaEstaEmUmJogo ");
			return new MsgSrv(Lang.msg("jaEstaEmUmjogo"));
		}
		DadosJogoTopWar dadosJogoTopWar = (DadosJogoTopWar) nnpeTO.getData();
		JogoServidor jogoServidor = obterJogo(dadosJogoTopWar.getNomeJogo());
		jogoServidor.entrarNoJogo(dadosJogoTopWar);
		nnpeTO.setData(jogoServidor.getDadosJogoTopWar());
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
		ObjTopWar avatarTopWar = obterAvatarTopWarClienteCopia(acaoClienteTopWar
				.getNomeCliente());
		if (avatarTopWar == null) {
			return null;
		}
		return jogoServidor.moverAvatar(avatarTopWar, acaoClienteTopWar);
	}

	public Object atualizarListaAvatares(NnpeTO nnpeTO) {

		String data = (String) nnpeTO.getData();
		String[] split = data.split("&");
		String nomeJogo = split[0];
		JogoServidor jogoServidor = obterJogo(nomeJogo);
		if (jogoServidor != null) {
			nnpeTO.setData(jogoServidor.atualizaListaAvatares(nnpeTO));
			nnpeTO.setMillisSrv(System.currentTimeMillis());
			return nnpeTO;
		}
		return null;
	}

	protected JogoServidor obterJogo(String nomeJogo) {
		return mapaJogos.get(nomeJogo);
	}

	private ObjTopWar obterAvatarTopWarClienteCopia(String nomeCliente) {
		Collection jogosAndamento = nnpeDados.getJogosAndamento();
		for (Iterator iterator = jogosAndamento.iterator(); iterator.hasNext();) {
			String nmJogog = (String) iterator.next();
			JogoServidor jogoServidor = mapaJogos.get(nmJogog);
			if (jogoServidor != null) {
				ObjTopWar obterAvatarTopWar = jogoServidor
						.obterAvatarTopWarCopia(nomeCliente);
				if (obterAvatarTopWar != null) {
					return obterAvatarTopWar;
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
				List<ObjTopWar> avatarTopWars = jogoServidor
						.getAvatarTopWarsCopia();
				for (Iterator iterator2 = avatarTopWars.iterator(); iterator2
						.hasNext();) {
					ObjTopWar avatarTopWar = (ObjTopWar) iterator2.next();
					if (nomeCliente.equals(avatarTopWar.getNomeJogador())) {
						return jogoServidor;
					}
				}
			}
		}
		return null;
	}

	public void removerJogosVaziosFinalizados() {
		synchronized (mapaJogos) {
			Set<String> keySet = mapaJogos.keySet();
			for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				JogoServidor jogoServidor = mapaJogos.get(key);
				jogoServidor.removerClientesInativos();
				if (jogoServidor.verificaFinalizado()) {
					mapaJogos.remove(key);
					Logger.logar("Removendo Jogo " + key);
					Collection jogosAndamento = nnpeDados.getJogosAndamento();
					synchronized (jogosAndamento) {
						for (Iterator iterator2 = jogosAndamento.iterator(); iterator2
								.hasNext();) {
							String jogo = (String) iterator2.next();
							if (key.equals(jogo)) {
								iterator2.remove();
							}
						}
					}
				}
			}
		}
	}

	public void removerClienteInativo(SessaoCliente sessaoClienteRemover) {
		JogoServidor jogoServidor = obterJogoCliente(sessaoClienteRemover
				.getNomeJogador());
		if (jogoServidor != null) {
			jogoServidor.removerJogador(sessaoClienteRemover.getNomeJogador());
		}

	}

	public Object atacar(NnpeTO nnpeTO) {
		DadosAcaoClienteTopWar acaoClienteTopWar = (DadosAcaoClienteTopWar) nnpeTO
				.getData();
		JogoServidor jogoServidor = obterJogoCliente(acaoClienteTopWar
				.getNomeCliente());
		if (jogoServidor == null) {
			return null;
		}
		ObjTopWar avatarTopWar = obterAvatarTopWarClienteCopia(acaoClienteTopWar
				.getNomeCliente());
		if (avatarTopWar == null) {
			return null;
		}
		jogoServidor
				.atualizaAngulo(avatarTopWar, acaoClienteTopWar.getAngulo());
		Object atacar = jogoServidor.atacar(avatarTopWar,
				acaoClienteTopWar.getAngulo(), acaoClienteTopWar.getRange());
		if (ConstantesTopWar.OK.equals(atacar)) {
			avatarTopWar.setPontoDestinoMover(null);
		}
		return atacar;
	}

	public Object atualizaAngulo(NnpeTO nnpeTO) {
		DadosAcaoClienteTopWar acaoClienteTopWar = (DadosAcaoClienteTopWar) nnpeTO
				.getData();
		JogoServidor jogoServidor = obterJogoCliente(acaoClienteTopWar
				.getNomeCliente());
		if (jogoServidor == null) {
			return null;
		}
		ObjTopWar avatarTopWar = obterAvatarTopWarClienteCopia(acaoClienteTopWar
				.getNomeCliente());
		if (avatarTopWar == null) {
			return null;
		}
		return jogoServidor.atualizaAngulo(avatarTopWar,
				acaoClienteTopWar.getAngulo());
	}

	public Object recarregar(NnpeTO nnpeTO) {
		DadosAcaoClienteTopWar acaoClienteTopWar = (DadosAcaoClienteTopWar) nnpeTO
				.getData();
		JogoServidor jogoServidor = obterJogoCliente(acaoClienteTopWar
				.getNomeCliente());
		if (jogoServidor == null) {
			return null;
		}
		ObjTopWar avatarTopWar = obterAvatarTopWarClienteCopia(acaoClienteTopWar
				.getNomeCliente());
		if (avatarTopWar == null) {
			return null;
		}
		return jogoServidor.recarregar(avatarTopWar);
	}

	public Object moverPonto(NnpeTO nnpeTO) {
		DadosAcaoClienteTopWar acaoClienteTopWar = (DadosAcaoClienteTopWar) nnpeTO
				.getData();
		JogoServidor jogoServidor = obterJogoCliente(acaoClienteTopWar
				.getNomeCliente());
		if (jogoServidor == null) {
			return null;
		}
		ObjTopWar avatarTopWar = obterAvatarTopWarClienteCopia(acaoClienteTopWar
				.getNomeCliente());
		if (avatarTopWar == null) {
			return null;
		}
		return jogoServidor.moverPontoAvatar(avatarTopWar, acaoClienteTopWar);
	}

	public Object alternarFaca(NnpeTO nnpeTO) {
		DadosAcaoClienteTopWar acaoClienteTopWar = (DadosAcaoClienteTopWar) nnpeTO
				.getData();
		JogoServidor jogoServidor = obterJogoCliente(acaoClienteTopWar
				.getNomeCliente());
		if (jogoServidor == null) {
			return null;
		}
		ObjTopWar avatarTopWar = obterAvatarTopWarClienteCopia(acaoClienteTopWar
				.getNomeCliente());
		if (avatarTopWar == null) {
			return null;
		}
		return jogoServidor.alternarFaca(avatarTopWar);
	}

	public Object obterPlacarJogo(NnpeTO nnpeTO) {
		DadosAcaoClienteTopWar acaoClienteTopWar = (DadosAcaoClienteTopWar) nnpeTO
				.getData();
		JogoServidor jogoServidor = obterJogoCliente(acaoClienteTopWar
				.getNomeCliente());
		if (jogoServidor == null) {
			return null;
		}
		return jogoServidor.obterPlacarJogo();
	}

	public Object sairJogo(NnpeTO nnpeTO) {
		JogoServidor jogoServidor = obterJogoCliente(nnpeTO.getSessaoCliente()
				.getNomeJogador());
		if (jogoServidor != null)
			jogoServidor.sairJogo(nnpeTO.getSessaoCliente().getNomeJogador());
		return ConstantesTopWar.OK;
	}

	public Object mudarClasse(NnpeTO nnpeTO) {
		ObjTopWar avatarTopWar = obterAvatarTopWarClienteCopia(nnpeTO
				.getSessaoCliente().getNomeJogador());
		if (avatarTopWar == null) {
			return null;
		}
		avatarTopWar.setProxClasse((String) nnpeTO.getData());
		return ConstantesTopWar.OK;
	}

	public Object radio(NnpeTO nnpeTO, boolean somenteTime) {
		JogoServidor jogoServidor = obterJogoCliente(nnpeTO.getSessaoCliente()
				.getNomeJogador());
		if (jogoServidor == null) {
			return null;
		}
		ObjTopWar avatarTopWar = obterAvatarTopWarClienteCopia(nnpeTO
				.getSessaoCliente().getNomeJogador());
		if (avatarTopWar == null) {
			return null;
		}
		return jogoServidor.radio(avatarTopWar, (String) nnpeTO.getData(),
				somenteTime);
	}

	public void finalizaJogosServidor() {
		synchronized (mapaJogos) {
			Set<String> keySet = mapaJogos.keySet();
			for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				JogoServidor jogoServidor = mapaJogos.get(key);
				List<ObjTopWar> avatarTopWarsCopia = jogoServidor
						.getAvatarTopWarsCopia();
				for (Iterator iterator2 = avatarTopWarsCopia.iterator(); iterator2
						.hasNext();) {
					ObjTopWar objTopWar = (ObjTopWar) iterator2.next();
					jogoServidor.removerJogador(objTopWar.getNomeJogador());
				}
				jogoServidor.setFinalizado(true);
			}
			mapaJogos.clear();
			Logger.logar("mapaJogos.size() " + mapaJogos.size());
		}
		if (nnpeDados != null) {
			Collection jogosAndamento = nnpeDados.getJogosAndamento();
			if (jogosAndamento != null) {
				jogosAndamento.clear();
				Logger.logar("jogosAndamento.size() " + jogosAndamento.size());
			}
		}
	}

	public Object obsCam(NnpeTO nnpeTO) {
		JogoServidor jogoServidor = obterJogoCliente(nnpeTO.getSessaoCliente()
				.getNomeJogador());
		if (jogoServidor == null) {
			return null;
		}
		ObjTopWar avatarTopWar = obterAvatarTopWarClienteCopia(nnpeTO
				.getSessaoCliente().getNomeJogador());
		if (avatarTopWar != null)
			avatarTopWar.setNomeAvatarAssistindo(nnpeTO.getData().toString());
		return ConstantesTopWar.OK;
	}

}
