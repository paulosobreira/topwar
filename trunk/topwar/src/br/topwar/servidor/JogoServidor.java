package br.topwar.servidor;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.nnpe.GeoUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.topwar.ConstantesTopWar;
import br.topwar.ProxyComandos;
import br.topwar.cliente.AvatarCliente;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.serial.MapaTopWar;
import br.topwar.serial.ObjetoMapa;
import br.topwar.tos.ObjTopWar;
import br.topwar.tos.DadosAcaoClienteTopWar;
import br.topwar.tos.DadosAvatar;
import br.topwar.tos.DadosJogoTopWar;
import br.topwar.tos.EventoJogo;
import br.topwar.tos.PlacarTopWar;

public class JogoServidor {
	private DadosJogoTopWar dadosJogoTopWar;
	private MapaTopWar mapaTopWar;
	private ProxyComandos proxyComandos;
	private List<ObjTopWar> avatarTopWars = new ArrayList<ObjTopWar>();
	private Thread monitorJogo;
	private int ptsVermelho;
	private int ptsAzul;
	private boolean finalizado = false;
	private int tempoJogoMilis;
	private long inicioJogoMilis;
	private long fimJogoMilis;
	private Set<EventoJogo> eventos = new HashSet<EventoJogo>();
	private ControleBots controleBots;

	public JogoServidor(DadosJogoTopWar dadosJogoTopWar,
			ProxyComandos proxyComandos) {
		this.dadosJogoTopWar = dadosJogoTopWar;
		this.proxyComandos = proxyComandos;
		controleBots = new ControleBots(this);
		carregarMapa(dadosJogoTopWar);
		incluirAvatarCriadorJogo(dadosJogoTopWar);
		iniciarContadorTempoJogo();
		iniciaMonitorDeJogo();
		controleBots.adicionarBots();
	}

	public MapaTopWar getMapaTopWar() {
		return mapaTopWar;
	}

	private void iniciaMonitorDeJogo() {
		monitorJogo = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!finalizado) {
					processaClicoJogoServidor();
					if (verificaFinalizado()) {
						finalizado = true;
					}
					try {
						Thread.sleep(ConstantesTopWar.ATRASO_REDE_PADRAO * 2);
					} catch (InterruptedException e) {
						Logger.logarExept(e);
						finalizado = true;
					}

				}

			}
		});
		monitorJogo.start();
	}

	private void iniciarContadorTempoJogo() {
		int tempoJogoMinutos = 10;
		tempoJogoMilis = tempoJogoMinutos * 60 * 1000;
		inicioJogoMilis = System.currentTimeMillis();
		fimJogoMilis = inicioJogoMilis + tempoJogoMilis;
	}

	private void incluirAvatarCriadorJogo(DadosJogoTopWar dadosJogoTopWar) {
		ObjTopWar avatarTopWar = new ObjTopWar();
		avatarTopWar.setClasse(dadosJogoTopWar.getClasse());
		avatarTopWar.setupCalsseJogador();
		boolean botsVsHumans = getDadosJogoTopWar().isBotsVsHumans();
		int numBots = getDadosJogoTopWar().getNumBots();
		String time = ConstantesTopWar.TIME_AZUL;
		if (!botsVsHumans || numBots == 0) {
			time = Math.random() < .5 ? ConstantesTopWar.TIME_VERMELHO
					: ConstantesTopWar.TIME_AZUL;
		}

		avatarTopWar.setTime(time);
		if (ConstantesTopWar.TIME_AZUL.equals(time))
			avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeAzul());
		if (ConstantesTopWar.TIME_VERMELHO.equals(time))
			avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeVermelho());

		avatarTopWar.setNomeJogador(dadosJogoTopWar.getNomeJogador());
		avatarTopWars.add(avatarTopWar);
	}

	private void carregarMapa(DadosJogoTopWar dadosJogoTopWar) {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(
					CarregadorRecursos.recursoComoStream(dadosJogoTopWar
							.getNomeMapa() + ".topwar"));
			mapaTopWar = (MapaTopWar) ois.readObject();
		} catch (Exception e) {
			Logger.logarExept(e);
		}
	}

	public long tempoRestanteJogo() {
		long tempo = fimJogoMilis - System.currentTimeMillis();
		if (tempo < 0) {
			return 0;
		}
		return tempo;
	}

	public void setFinalizado(boolean finalizado) {
		this.finalizado = finalizado;
	}

	protected void processaClicoJogoServidor() {
		synchronized (avatarTopWars) {
			for (Iterator iterator = avatarTopWars.iterator(); iterator
					.hasNext();) {
				ObjTopWar avatarTopWar = (ObjTopWar) iterator.next();
				long tempDesdeUltMorte = System.currentTimeMillis()
						- avatarTopWar.getUltimaMorte();
				if (avatarTopWar.getVida() <= 0 && tempDesdeUltMorte > 5000) {
					avatarTopWar.setMortoPor(null);
					avatarTopWar.setupCalsseJogador();
					if (ConstantesTopWar.TIME_AZUL.equals(avatarTopWar
							.getTime())) {
						avatarTopWar.setPontoAvatar(mapaTopWar
								.getPontoTimeAzul());
					}
					if (ConstantesTopWar.TIME_VERMELHO.equals(avatarTopWar
							.getTime())) {
						avatarTopWar.setPontoAvatar(mapaTopWar
								.getPontoTimeVermelho());
					}
				}
				if (avatarTopWar.getVida() > 0 && tempDesdeUltMorte < 12000) {
					avatarTopWar.setInvencivel(true);
				} else {
					avatarTopWar.setInvencivel(false);
				}

			}
		}
	}

	public List<ObjTopWar> getAvatarTopWars() {
		return avatarTopWars;
	}

	public List<EventoJogo> getEventosCopia() {
		List<EventoJogo> eventosCopy = new ArrayList<EventoJogo>();
		boolean concur = false;
		try {
			eventosCopy.addAll(eventos);
		} catch (Exception e) {
			concur = true;
			Logger.logarExept(e);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		if (concur) {
			while (eventosCopy.isEmpty()) {
				try {
					eventosCopy.addAll(eventos);
				} catch (Exception e) {
					Logger.logarExept(e);
					try {
						Thread.sleep(5);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		return eventosCopy;
	}

	public List<ObjTopWar> getAvatarTopWarsCopia() {
		List<ObjTopWar> avataresCopy = new ArrayList<ObjTopWar>();
		while (avataresCopy.isEmpty()) {
			try {
				avataresCopy.addAll(avatarTopWars);
			} catch (Exception e) {
				Logger.logarExept(e);
				try {
					Thread.sleep(5);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		return avataresCopy;
	}

	public Object atualizaListaAvatares(NnpeTO nnpeTO) {
		ObjTopWar avatarTopWarJog = obterAvatarTopWar(nnpeTO.getSessaoCliente()
				.getNomeJogador());
		if (avatarTopWarJog == null)
			return null;
		Set<ObjTopWar> ret = new HashSet<ObjTopWar>();
		synchronized (avatarTopWars) {
			for (Iterator iterator = avatarTopWars.iterator(); iterator
					.hasNext();) {
				ObjTopWar avatarTopWar = (ObjTopWar) iterator.next();
				if (avatarTopWar.equals(avatarTopWarJog)) {
					avatarTopWar
							.setUltimaRequisicao(System.currentTimeMillis());
					ret.add(avatarTopWar);
					continue;
				}

				double distacia = GeoUtil.distaciaEntrePontos(
						avatarTopWarJog.getPontoAvatar(),
						avatarTopWar.getPontoAvatar());
				if (distacia > ConstantesTopWar.LIMITE_VISAO
						&& !avatarTopWar.verificaObj()) {
					continue;
				}

				List<Point> line = GeoUtil.drawBresenhamLine(
						avatarTopWarJog.getPontoAvatar(),
						avatarTopWar.getPontoAvatar());
				if (campoVisao(line, avatarTopWarJog, false)) {
					ret.add(avatarTopWar);
				}
				/**
				 * Campo Audição Tiro 360
				 */
				Point pontoTiro = avatarTopWar.getPontoUtlDisparo();
				if (pontoTiro != null
						&& (System.currentTimeMillis() - avatarTopWar
								.getTempoUtlAtaque()) < 300) {
					line = GeoUtil.drawBresenhamLine(
							avatarTopWarJog.getPontoAvatar(), pontoTiro);
					if (campoVisao(line, null, true)) {
						ret.add(avatarTopWar);
					}
				}
				if (avatarTopWarJog.getVida() <= 0
						&& avatarTopWarJog.getMortoPor() != null
						&& avatarTopWarJog.getMortoPor().equals(avatarTopWar)) {
					ret.add(avatarTopWar);
				}

			}
		}
		Map retorno = new HashMap();
		retorno.put(ConstantesTopWar.LISTA_AVATARES,
				DadosAvatar.empacotaLista(ret));
		retorno.put(ConstantesTopWar.BALAS, avatarTopWarJog.getBalas());
		retorno.put(ConstantesTopWar.CARTUCHO, avatarTopWarJog.getCartuchos());
		retorno.put(ConstantesTopWar.RECARREGAR,
				verificaRecarregando(avatarTopWarJog));
		retorno.put(ConstantesTopWar.PTS_VERMELHO, getPtsVermelho());
		retorno.put(ConstantesTopWar.PTS_AZUL, getPtsAzul());
		retorno.put(ConstantesTopWar.TEMPO_JOGO_RESTANTE, tempoRestanteJogo());
		retorno.put(ConstantesTopWar.MUDAR_CLASSE,
				avatarTopWarJog.getProxClasse());
		if (avatarTopWarJog.getMortoPor() != null)
			retorno.put(ConstantesTopWar.KILL_CAM, avatarTopWarJog
					.getMortoPor().getNomeJogador());

		String data = (String) nnpeTO.getData();
		String[] split = data.split("&");
		Long utlEvento = new Long(0);
		if (!Util.isNullOrEmpty(split[1])) {
			utlEvento = new Long(split[1]);
		}
		List<EventoJogo> eventosCopia = getEventosCopia();
		for (Iterator iterator = eventosCopia.iterator(); iterator.hasNext();) {
			EventoJogo eventoJogo = (EventoJogo) iterator.next();
			if ((System.currentTimeMillis() - eventoJogo.getTempo()) > 5000) {
				continue;
			}
			if (eventoJogo.getTempo() > utlEvento) {
				retorno.put(ConstantesTopWar.EVENTO_JOGO, eventoJogo);
				break;
			}
		}
		return retorno;
	}

	public int getPtsVermelho() {
		return ptsVermelho;
	}

	public int getPtsAzul() {
		return ptsAzul;
	}

	public boolean campoVisao(List<Point> line, ObjTopWar avatarTopWar,
			boolean campoVisaoTiro) {
		if (avatarTopWar == null) {
			return false;
		}

		if (line == null) {
			return false;
		}

		if (line.size() > ConstantesTopWar.LIMITE_VISAO
				&& !avatarTopWar.verificaObj()) {
			return false;
		}
		/**
		 * Campo Visao Jogador Meia Lua
		 */
		Ellipse2D ellipse2dCostas = null;
		if (avatarTopWar != null) {
			Point back = GeoUtil.calculaPonto(avatarTopWar.getAngulo() + 180,
					30, avatarTopWar.getPontoAvatar());
			ellipse2dCostas = new Ellipse2D.Double(back.x - 25, back.y - 25,
					50, 50);
		}
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		for (int i = 0; i < line.size(); i += 2) {
			Point point = line.get(i);
			if (ellipse2dCostas != null
					&& ellipse2dCostas.intersects(new Rectangle2D.Double(
							point.x, point.y, 1, 1))) {
				return false;
			}
			for (Iterator iterator2 = objetoMapaList.iterator(); iterator2
					.hasNext();) {
				ObjetoMapa objetoMapa = (ObjetoMapa) iterator2.next();

				/**
				 * Checa objeto nao visivel para tiro
				 */

				if (campoVisaoTiro && (objetoMapa.getTransparencia() > 50)
						&& objetoMapa.getForma().contains(point)) {
					return false;
				}
				/**
				 * Checa objeto nao visivel
				 */
				if (!ConstantesTopWar.GRADE.equals(objetoMapa.getEfeito())
						&& objetoMapa.getTransparencia() > 50
						&& objetoMapa.getForma().contains(point)) {
					return false;
				}
			}
		}
		return true;
	}

	public DadosJogoTopWar getDadosJogoTopWar() {
		return dadosJogoTopWar;
	}

	public void setDadosJogoTopWar(DadosJogoTopWar dadosJogoTopWar) {
		this.dadosJogoTopWar = dadosJogoTopWar;
	}

	public String getNome() {
		return dadosJogoTopWar.getNomeJogo();
	}

	public String moverAvatar(ObjTopWar avatarTopWar,
			DadosAcaoClienteTopWar acaoClienteTopWar) {
		if (avatarTopWar.getVida() <= 0) {
			return null;
		}
		Point novoPonto = new Point(avatarTopWar.getPontoAvatar().x,
				avatarTopWar.getPontoAvatar().y);
		if (ConstantesTopWar.ESQUERDA.equals(acaoClienteTopWar.getMoverPara())) {
			novoPonto.x = novoPonto.x - avatarTopWar.getVelocidade();
		}
		if (ConstantesTopWar.BAIXO.equals(acaoClienteTopWar.getMoverPara())) {
			novoPonto.y = novoPonto.y + avatarTopWar.getVelocidade();
		}
		if (ConstantesTopWar.DIREITA.equals(acaoClienteTopWar.getMoverPara())) {
			novoPonto.x = novoPonto.x + avatarTopWar.getVelocidade();
		}
		if (ConstantesTopWar.CIMA.equals(acaoClienteTopWar.getMoverPara())) {
			novoPonto.y = novoPonto.y - avatarTopWar.getVelocidade();
		}
		if (verificaColisao(novoPonto, mapaTopWar)) {
			return null;
		}
		avatarTopWar.setPontoAvatar(novoPonto);
		avatarTopWar.setAngulo(acaoClienteTopWar.getAngulo());
		return ConstantesTopWar.OK;
	}

	public boolean verificaColisao(Point novoPonto) {
		return verificaColisao(novoPonto, mapaTopWar);
	}

	public static boolean verificaColisao(Point novoPonto, MapaTopWar mapaTopWar) {
		Rectangle areaMapa = new Rectangle(0, 0, mapaTopWar.getLargura(),
				mapaTopWar.getAltura());
		Shape novaArea = AvatarCliente.desenhaCorpo(novoPonto);
		if (!areaMapa.contains(novaArea.getBounds())) {
			return true;
		}
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		for (Iterator iterator = objetoMapaList.iterator(); iterator.hasNext();) {
			ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
			if (objetoMapa.getTransparencia() > 10
					&& objetoMapa.getEfeito() == null
					&& objetoMapa.getForma().intersects(novaArea.getBounds())) {
				return true;
			}
		}

		return false;
	}

	public ObjTopWar entrarNoJogo(DadosJogoTopWar dadosJogoTopWar) {
		boolean botsVsHumans = getDadosJogoTopWar().isBotsVsHumans();
		if (botsVsHumans) {
			return entrarNoJogo(dadosJogoTopWar, ConstantesTopWar.TIME_AZUL);
		}
		return entrarNoJogo(dadosJogoTopWar, null);
	}

	public ObjTopWar entrarNoJogo(DadosJogoTopWar dadosJogoTopWar, String time) {
		ObjTopWar avatarTopWar = new ObjTopWar();
		avatarTopWar.setClasse(dadosJogoTopWar.getClasse());
		avatarTopWar.setupCalsseJogador();
		if (Util.isNullOrEmpty(time)) {
			int contAzul = contarJogadores(ConstantesTopWar.TIME_AZUL);
			int contVermelho = contarJogadores(ConstantesTopWar.TIME_VERMELHO);
			if (contAzul > contVermelho) {
				avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeVermelho());
				avatarTopWar.setTime(ConstantesTopWar.TIME_VERMELHO);
			} else if (contAzul < contVermelho) {
				avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeAzul());
				avatarTopWar.setTime(ConstantesTopWar.TIME_AZUL);
			} else {
				if (Math.random() > 0.5) {
					avatarTopWar.setPontoAvatar(mapaTopWar
							.getPontoTimeVermelho());
					avatarTopWar.setTime(ConstantesTopWar.TIME_VERMELHO);
				} else {
					avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeAzul());
					avatarTopWar.setTime(ConstantesTopWar.TIME_AZUL);
				}
			}
		} else {
			if (ConstantesTopWar.TIME_VERMELHO.equals(time)) {
				avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeVermelho());
				avatarTopWar.setTime(ConstantesTopWar.TIME_VERMELHO);
			} else {
				avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeAzul());
				avatarTopWar.setTime(ConstantesTopWar.TIME_AZUL);
			}
		}

		avatarTopWar.setNomeJogador(dadosJogoTopWar.getNomeJogador());
		avatarTopWars.add(avatarTopWar);
		return avatarTopWar;
	}

	public int contarJogadores(String time) {
		int cont = 0;
		for (int i = 0; i < avatarTopWars.size(); i++) {
			if (time.equals(avatarTopWars.get(i).getTime())) {
				cont++;
			}
		}
		return cont;
	}

	public ObjTopWar removerJogador(String nomeJogador) {
		synchronized (avatarTopWars) {
			for (Iterator iterator = avatarTopWars.iterator(); iterator
					.hasNext();) {
				ObjTopWar avatarTopWar = (ObjTopWar) iterator.next();
				if (avatarTopWar.verificaObj()) {
					continue;
				}
				if (avatarTopWar.getNomeJogador().equals(nomeJogador)) {
					iterator.remove();
					return avatarTopWar;
				}
			}
		}
		return null;
	}

	public ObjTopWar obterAvatarTopWar(String nomeCliente) {
		List<ObjTopWar> avatarTopWars = getAvatarTopWars();
		synchronized (avatarTopWars) {
			for (Iterator iterator2 = avatarTopWars.iterator(); iterator2
					.hasNext();) {
				ObjTopWar avatarTopWar = (ObjTopWar) iterator2.next();
				if (nomeCliente.equals(avatarTopWar.getNomeJogador())) {
					return avatarTopWar;
				}
			}
		}
		return null;
	}

	public boolean verificaFinalizado() {
		finalizado = avatarTopWars.isEmpty();
		return finalizado;
	}

	public void removerClientesInativos() {
		synchronized (avatarTopWars) {
			for (Iterator iterator = avatarTopWars.iterator(); iterator
					.hasNext();) {
				ObjTopWar avatarTopWar = (ObjTopWar) iterator.next();
				if (avatarTopWar.getBotInfo() != null) {
					continue;
				}
				long diff = (System.currentTimeMillis() - avatarTopWar
						.getUltimaRequisicao());
				if (diff > 5000) {
					iterator.remove();
				}
			}
		}
	}

	public Object atacar(ObjTopWar avatarAtacando, double angulo, int range) {
		if (avatarAtacando.getVida() <= 0) {
			return null;
		}
		if (ConstantesTopWar.ARMA_FACA != avatarAtacando.getArma()
				&& verificaRecarregando(avatarAtacando)) {
			return null;
		}
		if (ConstantesTopWar.ARMA_FACA == avatarAtacando.getArma()
				&& (combateCoprpoACorpo(avatarAtacando))) {
			return ConstantesTopWar.OK;
		}

		if ((ConstantesTopWar.ARMA_ASSAULT == avatarAtacando.getArma()
				|| ConstantesTopWar.ARMA_MACHINEGUN == avatarAtacando.getArma() || ConstantesTopWar.ARMA_SNIPER == avatarAtacando
				.getArma()) && (atirar(avatarAtacando, angulo, range))) {
			return ConstantesTopWar.OK;
		}

		if (ConstantesTopWar.ARMA_SHOTGUN == avatarAtacando.getArma()
				&& (atirarShotGun(avatarAtacando, angulo, range))) {
			return ConstantesTopWar.OK;
		}

		if (ConstantesTopWar.ARMA_ROCKET == avatarAtacando.getArma()
				&& (atirarRocket(avatarAtacando, angulo, range))) {
			return ConstantesTopWar.OK;
		}

		return null;
	}

	private boolean atirarRocket(final ObjTopWar avatarAtacando,
			final double angulo, final int range) {
		int balas = consomeBalasArma(avatarAtacando);
		if (balas != 0) {
			Thread rocket = new Thread(new Runnable() {
				@Override
				public void run() {
					avatarAtacando.setTempoUtlAtaque(System.currentTimeMillis());
					int desvio = ConstantesTopWar.DESVIO_ROCKET;
					Point pontoTiro = GeoUtil.calculaPonto(
							angulo + Util.intervalo(-desvio, desvio), range,
							avatarAtacando.getPontoAvatar());
					List<Point> linhaTiro = GeoUtil.drawBresenhamLine(
							avatarAtacando.getPontoAvatar(), pontoTiro);
					ObjTopWar objTopWar = new ObjTopWar();
					objTopWar.setArma(ConstantesTopWar.OBJ_ROCKET);
					objTopWar.setNomeJogador(avatarAtacando.getNomeJogador()
							+ "-" + ConstantesTopWar.OBJ_ROCKET);
					objTopWar.setTime(avatarAtacando.getTime());
					objTopWar.setAngulo(angulo);
					synchronized (avatarTopWars) {
						avatarTopWars.add(objTopWar);
					}
					for (Iterator iterator = linhaTiro.iterator(); iterator
							.hasNext();) {
						Point point = (Point) iterator.next();
						if (verificaColisao(point)) {
							break;
						}
						objTopWar.setPontoAvatar(point);
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
						}
					}
					synchronized (avatarTopWars) {
						avatarTopWars.remove(objTopWar);
					}
					Point explo = objTopWar.getPontoAvatar();
					Ellipse2D circ = new Ellipse2D.Double(
							(double) explo.x - 50.0, (double) explo.y - 50.0,
							100.0, 100.0);
					List<EventoJogo> eventosTemp = new ArrayList<EventoJogo>();
					List<ObjTopWar> avatarTopWarsCopia = getAvatarTopWarsCopia();
					List<ObjTopWar> atingidosList = new ArrayList<ObjTopWar>();
					for (Iterator iteratorAvatar = avatarTopWarsCopia
							.iterator(); iteratorAvatar.hasNext();) {
						ObjTopWar avatarAlvo = (ObjTopWar) iteratorAvatar
								.next();
						if (avatarAlvo.equals(avatarAtacando)) {
							continue;
						}
						if (avatarAlvo.getTime().equals(
								avatarAtacando.getTime())) {
							continue;
						}
						if (avatarAlvo.isInvencivel()) {
							continue;
						}
						if (avatarAlvo.getVida() <= 0) {
							continue;
						}
						AvatarCliente avatarCliente = new AvatarCliente(
								avatarAlvo);
						if (circ.intersects(avatarCliente.gerarCorpo()
								.getBounds2D())) {

							if (!verificaAndavel(
									avatarAtacando.getPontoAvatar(),
									avatarCliente.getPontoAvatar())) {
								continue;
							}
							atingidosList.add(avatarAlvo);
							EventoJogo eventoJogo = new EventoJogo();
							eventoJogo.setArma(avatarAtacando.getArma());
							eventoJogo.setAtacante(avatarAtacando
									.getNomeJogador());
							if (ConstantesTopWar.TIME_AZUL
									.equals(avatarAtacando.getTime())) {
								eventoJogo
										.setTimeAtacante(ConstantesTopWar.PTS_AZUL);
							}
							if (ConstantesTopWar.TIME_VERMELHO
									.equals(avatarAtacando.getTime())) {
								eventoJogo
										.setTimeAtacante(ConstantesTopWar.PTS_VERMELHO);
							}
							eventoJogo.setMorto(avatarAlvo.getNomeJogador());
							if (ConstantesTopWar.TIME_AZUL.equals(avatarAlvo
									.getTime())) {
								eventoJogo
										.setTimeMorto(ConstantesTopWar.PTS_AZUL);
							}
							if (ConstantesTopWar.TIME_VERMELHO
									.equals(avatarAlvo.getTime())) {
								eventoJogo
										.setTimeMorto(ConstantesTopWar.PTS_VERMELHO);
							}
							eventosTemp.add(eventoJogo);
						}
					}
					for (Iterator iterator = atingidosList.iterator(); iterator
							.hasNext();) {
						ObjTopWar atingidos = (ObjTopWar) iterator.next();

						ObjTopWar avatarAlvo = obterAvatarTopWar(atingidos
								.getNomeJogador());
						if (avatarAlvo == null) {
							continue;
						}
						avatarAlvo.setVida(-1);
						if (ConstantesTopWar.TIME_AZUL.equals(avatarAlvo
								.getTime())) {
							ptsVermelho++;
						} else {
							ptsAzul++;
						}
						avatarAlvo.setMortoPor(avatarAtacando);
						avatarAlvo.setDeaths(avatarAlvo.getDeaths() + 1);
						avatarAtacando.setKills(avatarAtacando.getKills() + 1);
					}

					for (Iterator iterator = eventosTemp.iterator(); iterator
							.hasNext();) {
						EventoJogo eventoJogoTemp = (EventoJogo) iterator
								.next();
						eventoJogoTemp.setTempo(System.currentTimeMillis());
						eventos.add(eventoJogoTemp);
						Logger.logar("Evento Rocket Serv " + eventoJogoTemp);
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							Logger.logarExept(e);
						}
					}

				}
			});
			rocket.start();

		}
		return false;
	}

	private boolean atirarShotGun(ObjTopWar avatarAtirador, double angulo,
			int range) {
		Point pontoAvatar = avatarAtirador.getPontoAvatar();
		Point front = GeoUtil.calculaPonto(angulo, 20, pontoAvatar);
		Ellipse2D ellipse2d1 = new Ellipse2D.Double(front.x - 10, front.y - 10,
				20, 20);

		front = GeoUtil.calculaPonto(angulo, 45, pontoAvatar);
		Ellipse2D ellipse2d2 = new Ellipse2D.Double(front.x - 15, front.y - 15,
				30, 30);
		front = GeoUtil.calculaPonto(angulo, 80, pontoAvatar);
		Ellipse2D ellipse2d3 = new Ellipse2D.Double(front.x - 20, front.y - 20,
				40, 40);
		avatarAtirador.setBalas(avatarAtirador.getBalas() - 1);
		avatarAtirador.setTempoUtlAtaque(System.currentTimeMillis());

		List<ObjTopWar> atingidos = new ArrayList<ObjTopWar>();

		final List<EventoJogo> eventosShotgun = new ArrayList<EventoJogo>();

		List<ObjTopWar> avatarTopWarsCopia = getAvatarTopWarsCopia();

		for (Iterator iteratorAvatar = avatarTopWarsCopia.iterator(); iteratorAvatar
				.hasNext();) {
			ObjTopWar avatarAlvo = (ObjTopWar) iteratorAvatar.next();
			if (avatarAlvo.equals(avatarAtirador)) {
				continue;
			}
			if (avatarAlvo.getTime().equals(avatarAtirador.getTime())) {
				continue;
			}
			if (avatarAlvo.isInvencivel()) {
				continue;
			}
			if (avatarAlvo.getVida() <= 0) {
				continue;
			}
			if (ConstantesTopWar.OBJ_ROCKET == avatarAlvo.getArma()) {
				continue;
			}

			if (ellipse2d1.contains(avatarAlvo.getPontoAvatar())
					|| ellipse2d2.contains(avatarAlvo.getPontoAvatar())
					|| ellipse2d3.contains(avatarAlvo.getPontoAvatar())) {

				if (!verificaAndavel(avatarAtirador.getPontoAvatar(),
						avatarAlvo.getPontoAvatar())) {
					continue;
				}
				EventoJogo eventoJogo = new EventoJogo();
				eventoJogo.setArma(avatarAtirador.getArma());
				eventoJogo.setAtacante(avatarAtirador.getNomeJogador());
				if (ConstantesTopWar.TIME_AZUL.equals(avatarAtirador.getTime())) {
					eventoJogo.setTimeAtacante(ConstantesTopWar.PTS_AZUL);
				}
				if (ConstantesTopWar.TIME_VERMELHO.equals(avatarAtirador
						.getTime())) {
					eventoJogo.setTimeAtacante(ConstantesTopWar.PTS_VERMELHO);
				}
				eventoJogo.setMorto(avatarAlvo.getNomeJogador());
				if (ConstantesTopWar.TIME_AZUL.equals(avatarAlvo.getTime())) {
					eventoJogo.setTimeMorto(ConstantesTopWar.PTS_AZUL);
				}
				if (ConstantesTopWar.TIME_VERMELHO.equals(avatarAlvo.getTime())) {
					eventoJogo.setTimeMorto(ConstantesTopWar.PTS_VERMELHO);
				}

				eventosShotgun.add(eventoJogo);
				atingidos.add(avatarAlvo);
			}
		}

		for (Iterator iterator = atingidos.iterator(); iterator.hasNext();) {
			ObjTopWar objTopWar = (ObjTopWar) iterator.next();
			ObjTopWar avatarAlvo = obterAvatarTopWar(objTopWar.getNomeJogador());
			if (avatarAlvo == null) {
				continue;
			}
			avatarAlvo.setVida(-1);
			if (ConstantesTopWar.TIME_AZUL.equals(avatarAlvo.getTime())) {
				ptsVermelho++;
			} else {
				ptsAzul++;
			}
			avatarAlvo.setMortoPor(avatarAtirador);
			avatarAlvo.setDeaths(avatarAlvo.getDeaths() + 1);
			avatarAtirador.setKills(avatarAtirador.getKills() + 1);
		}
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				for (Iterator iterator = eventosShotgun.iterator(); iterator
						.hasNext();) {
					EventoJogo eventoJogo = (EventoJogo) iterator.next();
					eventoJogo.setTempo(System.currentTimeMillis());
					synchronized (eventos) {
						eventos.add(eventoJogo);
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}
				}

			}
		});
		thread.start();

		return !atingidos.isEmpty();
	}

	private boolean combateCoprpoACorpo(ObjTopWar avatarAtacando) {
		synchronized (avatarTopWars) {
			for (Iterator iteratorAvatar = avatarTopWars.iterator(); iteratorAvatar
					.hasNext();) {
				ObjTopWar avatarAlvo = (ObjTopWar) iteratorAvatar.next();
				if (avatarAlvo.equals(avatarAtacando)) {
					continue;
				}
				if (avatarAlvo.isInvencivel()) {
					continue;
				}
				if (avatarAlvo.getVida() < 1) {
					continue;
				}
				if (ConstantesTopWar.OBJ_ROCKET == avatarAlvo.getArma()) {
					continue;
				}
				AvatarCliente avatarClienteAlvo = new AvatarCliente(avatarAlvo);
				if (ConstantesTopWar.ARMA_FACA == avatarAtacando.getArma()) {
					if (processaDanoCombateCorpoCorpo(avatarAtacando,
							avatarAlvo)) {
						return true;
					}
				}
			}
		}
		avatarAtacando.setTempoUtlAtaque(System.currentTimeMillis());
		return false;
	}

	private boolean atirar(ObjTopWar avatarAtacando, double angulo, int range) {
		int balas = consomeBalasArma(avatarAtacando);
		if (ConstantesTopWar.ARMA_ASSAULT == avatarAtacando.getArma()
				&& range > ConstantesTopWar.ASSALT_MAX_RANGE) {
			range = ConstantesTopWar.ASSALT_MAX_RANGE;
		}
		if (ConstantesTopWar.ARMA_SNIPER == avatarAtacando.getArma()
				&& range > ConstantesTopWar.SNIPER_MAX_RANGE) {
			range = ConstantesTopWar.SNIPER_MAX_RANGE;
		}
		if (ConstantesTopWar.ARMA_MACHINEGUN == avatarAtacando.getArma()
				&& range > ConstantesTopWar.MACHINEGUN_MAX_RANGE) {
			range = ConstantesTopWar.MACHINEGUN_MAX_RANGE;
		}

		avatarAtacando.setRangeUtlDisparo(range);
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();

		int desvio = 0;

		if (ConstantesTopWar.ARMA_ASSAULT == avatarAtacando.getArma()) {
			desvio = ConstantesTopWar.DESVIO_ASSAULT;
		} else if (ConstantesTopWar.ARMA_SNIPER == avatarAtacando.getArma()) {
			desvio = ConstantesTopWar.DESVIO_SNIPER;
		} else if (ConstantesTopWar.ARMA_MACHINEGUN == avatarAtacando.getArma()) {
			desvio = ConstantesTopWar.DESVIO_MACHINEGUN;
		}
		Point pontoTiro = GeoUtil.calculaPonto(
				angulo + Util.intervalo(-desvio, desvio), range,
				avatarAtacando.getPontoAvatar());
		List<Point> linhaTiro = GeoUtil.drawBresenhamLine(
				avatarAtacando.getPontoAvatar(), pontoTiro);
		Point pointAnt = null;
		for (int i = 0; i < linhaTiro.size(); i++) {
			Point point = (Point) linhaTiro.get(i);
			for (Iterator iterator = objetoMapaList.iterator(); iterator
					.hasNext();) {
				ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
				if (objetoMapa.getTransparencia() > 11
						&& objetoMapa.getForma().contains(point)) {
					if (pointAnt != null) {
						if (balas > 0) {
							avatarAtacando.setPontoUtlDisparo(point);
							return true;
						} else {
							return false;
						}
					}
					return false;
				}
			}
			synchronized (avatarTopWars) {
				for (Iterator iteratorAvatar = avatarTopWars.iterator(); iteratorAvatar
						.hasNext();) {
					ObjTopWar avatarAlvo = (ObjTopWar) iteratorAvatar.next();
					if (avatarAlvo.equals(avatarAtacando)) {
						continue;
					}
					if (avatarAlvo.getTime().equals(avatarAtacando.getTime())) {
						continue;
					}
					if (avatarAlvo.isInvencivel()) {
						continue;
					}
					if (avatarAlvo.getVida() <= 0) {
						continue;
					}
					if (ConstantesTopWar.OBJ_ROCKET == avatarAlvo.getArma()) {
						continue;
					}
					AvatarCliente avatarCliente = new AvatarCliente(avatarAlvo);
					if (ConstantesTopWar.ARMA_SHIELD == avatarCliente.getArma()
							&& avatarCliente.gerarEscudo().contains(point)) {
						avatarAtacando.setPontoUtlDisparo(point);
						consomeBalasArma(avatarAtacando);
						return false;
					}

					if (processaAcertoDanoTiro(avatarAtacando, point,
							avatarAlvo, i, linhaTiro, balas)) {
						return true;
					}
				}
			}
			pointAnt = point;
		}
		avatarAtacando.setPontoUtlDisparo(pontoTiro);
		return false;
	}

	private boolean processaDanoCombateCorpoCorpo(ObjTopWar avatarAtacando,
			ObjTopWar avatarAlvo) {
		if (avatarAtacando.getTime().equals(avatarAlvo.getTime())) {
			return false;
		}
		Shape desenhaAreaFaca = AvatarCliente.desenhaAreaFaca(
				avatarAtacando.getPontoAvatar(), avatarAtacando.getAngulo());
		Shape desenhaCorpoAlvo = AvatarCliente.desenhaCorpo(avatarAlvo
				.getPontoAvatar());
		Shape desenhaCabecaAlvo = AvatarCliente.desenhaCabeca(avatarAlvo
				.getPontoAvatar());
		if (desenhaAreaFaca.intersects(desenhaCorpoAlvo.getBounds2D())
				|| desenhaAreaFaca.intersects(desenhaCabecaAlvo.getBounds2D())) {
			avatarAlvo.setVida(0);
			avatarAlvo.setMortoPor(avatarAtacando);
			if (ConstantesTopWar.TIME_AZUL.equals(avatarAlvo.getTime())) {
				ptsVermelho++;
			} else {
				ptsAzul++;
			}
			avatarAlvo.setDeaths(avatarAlvo.getDeaths() + 1);
			avatarAtacando.setKills(avatarAtacando.getKills() + 1);
			EventoJogo eventoJogo = new EventoJogo();
			eventoJogo.setArma(avatarAtacando.getArma());
			eventoJogo.setAtacante(avatarAtacando.getNomeJogador());
			eventoJogo.setMorto(avatarAlvo.getNomeJogador());
			eventoJogo.setTempo(System.currentTimeMillis());
			eventoJogo.setAtacante(avatarAtacando.getNomeJogador());
			if (ConstantesTopWar.TIME_AZUL.equals(avatarAtacando.getTime())) {
				eventoJogo.setTimeAtacante(ConstantesTopWar.PTS_AZUL);
			}
			if (ConstantesTopWar.TIME_VERMELHO.equals(avatarAtacando.getTime())) {
				eventoJogo.setTimeAtacante(ConstantesTopWar.PTS_VERMELHO);
			}
			eventoJogo.setMorto(avatarAlvo.getNomeJogador());
			if (ConstantesTopWar.TIME_AZUL.equals(avatarAlvo.getTime())) {
				eventoJogo.setTimeMorto(ConstantesTopWar.PTS_AZUL);
			}
			if (ConstantesTopWar.TIME_VERMELHO.equals(avatarAlvo.getTime())) {
				eventoJogo.setTimeMorto(ConstantesTopWar.PTS_VERMELHO);
			}
			eventos.add(eventoJogo);
			return true;
		}
		return false;
	}

	private boolean processaAcertoDanoTiro(ObjTopWar avatarAtirador,
			Point point, ObjTopWar avatarAlvo, int indice, List linha, int balas) {
		AvatarCliente avatarCliente = new AvatarCliente(avatarAlvo);
		Shape gerarCorpo = avatarCliente.gerarCorpo();
		Shape gerarCabeca = avatarCliente.gerarCabeca();
		if (gerarCorpo.contains(point) || gerarCabeca.contains(point)) {
			if (balas > 0) {
				avatarAtirador.setPontoUtlDisparo(point);
			} else {
				return false;
			}
			boolean headShot = false;
			if (avatarAtirador.getTime().equals(avatarAlvo.getTime())) {
				return true;
			} else {
				if (Math.random() > 0.8 && gerarCabeca.contains(point)) {
					avatarAlvo.setVida(0);
					headShot = true;
				} else if (gerarCorpo.contains(point)
						&& (indice + ConstantesTopWar.LARGURA_AVATAR < linha
								.size())) {
					for (int i = indice; i < indice
							+ ConstantesTopWar.LARGURA_AVATAR; i++) {
						Point p = (Point) linha.get(i);
						if (Math.random() > 0.8 && gerarCabeca.contains(p)) {
							avatarAlvo.setVida(0);
							headShot = true;
							break;
						}
					}
				}
				if (!headShot) {
					if (ConstantesTopWar.ARMA_SNIPER == avatarAtirador
							.getArma()) {
						avatarAlvo.setVida(0);
					} else {
						avatarAlvo.setVida(avatarAlvo.getVida()
								- (balas * Util.intervalo(1, 2)));
					}
				}
				if (avatarAlvo.getVida() < 1) {
					if (ConstantesTopWar.TIME_AZUL.equals(avatarAlvo.getTime())) {
						ptsVermelho++;
					} else {
						ptsAzul++;
					}
					avatarAlvo.setMortoPor(avatarAtirador);
					avatarAlvo.setDeaths(avatarAlvo.getDeaths() + 1);
					avatarAtirador.setKills(avatarAtirador.getKills() + 1);
					EventoJogo eventoJogo = new EventoJogo();
					eventoJogo.setArma(avatarAtirador.getArma());
					if (headShot) {
						eventoJogo.setArma(ConstantesTopWar.HEADSHOT);
					}
					eventoJogo.setAtacante(avatarAtirador.getNomeJogador());
					if (ConstantesTopWar.TIME_AZUL.equals(avatarAtirador
							.getTime())) {
						eventoJogo.setTimeAtacante(ConstantesTopWar.PTS_AZUL);
					}
					if (ConstantesTopWar.TIME_VERMELHO.equals(avatarAtirador
							.getTime())) {
						eventoJogo
								.setTimeAtacante(ConstantesTopWar.PTS_VERMELHO);
					}
					eventoJogo.setMorto(avatarAlvo.getNomeJogador());
					if (ConstantesTopWar.TIME_AZUL.equals(avatarAlvo.getTime())) {
						eventoJogo.setTimeMorto(ConstantesTopWar.PTS_AZUL);
					}
					if (ConstantesTopWar.TIME_VERMELHO.equals(avatarAlvo
							.getTime())) {
						eventoJogo.setTimeMorto(ConstantesTopWar.PTS_VERMELHO);
					}
					eventoJogo.setTempo(System.currentTimeMillis());
					eventos.add(eventoJogo);
				}
				return true;
			}
		}
		return false;
	}

	private int consomeBalasArma(ObjTopWar avatarAtirador) {
		if (avatarAtirador.getBalas() <= 0) {
			return 0;
		}
		int balas = Util.intervalo(3, 7);
		if (avatarAtirador.getBalas() < balas) {
			balas = avatarAtirador.getBalas();
		}
		avatarAtirador.setBalas(avatarAtirador.getBalas() - balas);
		return balas;
	}

	private boolean verificaRecarregando(ObjTopWar avatarAtirador) {
		long tempoRecarga = ConstantesTopWar.TEMPO_RECARGA;
		if (ConstantesTopWar.ARMA_ASSAULT == avatarAtirador.getArma()) {
			tempoRecarga = ConstantesTopWar.TEMPO_RECARGA_ASSAUT;
		}
		if (ConstantesTopWar.ARMA_MACHINEGUN == avatarAtirador.getArma()) {
			tempoRecarga = ConstantesTopWar.TEMPO_RECARGA_MACHINEGUN;
		}
		if (ConstantesTopWar.ARMA_SNIPER == avatarAtirador.getArma()) {
			tempoRecarga = ConstantesTopWar.TEMPO_RECARGA_SNIPER;
		}
		if (ConstantesTopWar.ARMA_ROCKET == avatarAtirador.getArma()) {
			tempoRecarga = ConstantesTopWar.TEMPO_RECARGA_ROCKET;
		}
		return (System.currentTimeMillis() - avatarAtirador.getRecarregar()) < tempoRecarga;
	}

	public Object atualizaAngulo(ObjTopWar avatarTopWar, double angulo) {
		if (avatarTopWar.getVida() <= 0) {
			return null;
		}
		avatarTopWar.setAngulo(angulo);
		return ConstantesTopWar.OK;
	}

	public Object recarregar(ObjTopWar avatarTopWar) {
		if (avatarTopWar.getVida() <= 0) {
			return null;
		}
		if (avatarTopWar.getCartuchos() <= 0) {
			return null;
		}
		if (ConstantesTopWar.ASSAULT.equals(avatarTopWar.getClasse())) {
			avatarTopWar.setBalas(ConstantesTopWar.BALAS_ASSALT);
		} else if (ConstantesTopWar.SHOTGUN.equals(avatarTopWar.getClasse())) {
			avatarTopWar.setBalas(ConstantesTopWar.BALAS_SHOTGUN);
		} else if (ConstantesTopWar.MACHINEGUN.equals(avatarTopWar.getClasse())) {
			avatarTopWar.setBalas(ConstantesTopWar.BALAS_MACHINEGUN);
		} else if (ConstantesTopWar.SNIPER.equals(avatarTopWar.getClasse())) {
			avatarTopWar.setBalas(ConstantesTopWar.BALAS_SNIPER);
		} else if (ConstantesTopWar.ROCKET.equals(avatarTopWar.getClasse())) {
			avatarTopWar.setBalas(ConstantesTopWar.BALAS_ROCKET);
		}

		avatarTopWar.setCartuchos(avatarTopWar.getCartuchos() - 1);
		avatarTopWar.setRecarregar(System.currentTimeMillis());
		return ConstantesTopWar.OK;
	}

	public Object moverPontoAvatar(ObjTopWar avatarTopWar,
			DadosAcaoClienteTopWar acaoClienteTopWar) {
		if (avatarTopWar.getVida() <= 0) {
			return null;
		}
		double distaciaEntrePontos = GeoUtil.distaciaEntrePontos(
				avatarTopWar.getPontoAvatar(), acaoClienteTopWar.getPonto());
		// Logger.logar("distaciaEntrePontos " + distaciaEntrePontos);
		// Logger.logar("avatarTopWar.getVelocidade() * 1.3 "
		// + avatarTopWar.getVelocidade() * 1.3);
		if (distaciaEntrePontos > (avatarTopWar.getVelocidade() * 1.3)) {
			return null;
		}
		if (verificaColisao(acaoClienteTopWar.getPonto(), mapaTopWar)) {
			return null;
		}
		avatarTopWar.setPontoAvatar(acaoClienteTopWar.getPonto());
		avatarTopWar.setAngulo(acaoClienteTopWar.getAngulo());
		return ConstantesTopWar.OK;
	}

	public Object alternarFaca(ObjTopWar avatarTopWar) {
		if (avatarTopWar.getVida() <= 0) {
			return null;
		}
		if (avatarTopWar.getArma() == ConstantesTopWar.ARMA_FACA) {
			if (ConstantesTopWar.SHOTGUN.equals(avatarTopWar.getClasse())) {
				avatarTopWar.setArma(ConstantesTopWar.ARMA_SHOTGUN);
			} else if (ConstantesTopWar.ASSAULT
					.equals(avatarTopWar.getClasse())) {
				avatarTopWar.setArma(ConstantesTopWar.ARMA_ASSAULT);
			} else if (ConstantesTopWar.SNIPER.equals(avatarTopWar.getClasse())) {
				avatarTopWar.setArma(ConstantesTopWar.ARMA_SNIPER);
			} else if (ConstantesTopWar.MACHINEGUN.equals(avatarTopWar
					.getClasse())) {
				avatarTopWar.setArma(ConstantesTopWar.ARMA_MACHINEGUN);
			} else if (ConstantesTopWar.ROCKET.equals(avatarTopWar.getClasse())) {
				avatarTopWar.setArma(ConstantesTopWar.ARMA_ROCKET);
			} else if (ConstantesTopWar.SHIELD.equals(avatarTopWar.getClasse())) {
				avatarTopWar.setArma(ConstantesTopWar.ARMA_SHIELD);
			}
		} else {
			avatarTopWar.setArma(ConstantesTopWar.ARMA_FACA);
		}
		return ConstantesTopWar.OK;
	}

	public Object obterPlacarJogo() {
		List<PlacarTopWar> placar = new ArrayList<PlacarTopWar>();
		synchronized (avatarTopWars) {
			for (Iterator iterator = avatarTopWars.iterator(); iterator
					.hasNext();) {
				ObjTopWar avatarTopWar = (ObjTopWar) iterator.next();
				PlacarTopWar placarTopWar = new PlacarTopWar();
				placarTopWar.setJogador(avatarTopWar.getNomeJogador());
				placarTopWar.setTime(avatarTopWar.getTime());
				placarTopWar.setKills(avatarTopWar.getKills());
				placarTopWar.setDeaths(avatarTopWar.getDeaths());
				placar.add(placarTopWar);
			}
		}
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setData(placar);
		return nnpeTO;
	}

	public boolean verificaAndavel(Point pontoAvatar, Point calculaPonto) {
		List<Point> linha = GeoUtil
				.drawBresenhamLine(pontoAvatar, calculaPonto);
		if (calculaPonto.x <= 0) {
			return false;
		}
		if (calculaPonto.y <= 0) {
			return false;
		}
		if (calculaPonto.x >= mapaTopWar.getLargura()) {
			return false;
		}
		if (calculaPonto.y >= mapaTopWar.getAltura()) {
			return false;
		}
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		for (int i = 0; i < linha.size(); i++) {
			Point point = (Point) linha.get(i);
			for (Iterator iterator = objetoMapaList.iterator(); iterator
					.hasNext();) {
				ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
				if (objetoMapa.getEfeito() == null
						&& objetoMapa.getTransparencia() > 10
						&& objetoMapa.getForma().contains(point)) {
					return false;
				}
			}
		}
		return true;
	}

	public void sairJogo(String nomeJogador) {
		removerJogador(nomeJogador);
	}

	public static void main(String[] args) {
		Set set = new HashSet();
		set.add(1);
		set.add(2);
		set.add(3);
		set.add(4);
		set.add(5);
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			System.out.println(iterator.next());
		}

	}
}
