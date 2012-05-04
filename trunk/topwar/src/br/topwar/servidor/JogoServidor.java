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
import br.topwar.tos.AvatarTopWar;
import br.topwar.tos.DadosAcaoClienteTopWar;
import br.topwar.tos.DadosJogoTopWar;
import br.topwar.tos.EventoJogo;
import br.topwar.tos.PlacarTopWar;

public class JogoServidor {
	private DadosJogoTopWar dadosJogoTopWar;
	private MapaTopWar mapaTopWar;
	private ProxyComandos proxyComandos;
	private List<AvatarTopWar> avatarTopWars = new ArrayList<AvatarTopWar>();
	private Thread monitorJogo;
	private int ptsVermelho;
	private int ptsAzul;
	private boolean finalizado = false;
	private int tempoJogoMilis;
	private long inicioJogoMilis;
	private long fimJogoMilis;
	private List<EventoJogo> eventos = new LinkedList<EventoJogo>();
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
		controleBots.adicionarBot();
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
		AvatarTopWar avatarTopWar = new AvatarTopWar();
		avatarTopWar.setClasse(dadosJogoTopWar.getClasse());
		setupCalsseJogador(avatarTopWar);
		if (Math.random() > .5) {
			avatarTopWar.setTime(ConstantesTopWar.TIME_VERMELHO);
			avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeVermelho());
		} else {
			avatarTopWar.setTime(ConstantesTopWar.TIME_AZUL);
			avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeAzul());
		}
		avatarTopWar.setNomeJogador(dadosJogoTopWar.getNomeJogador());
		avatarTopWars.add(avatarTopWar);
	}

	private void carregarMapa(DadosJogoTopWar dadosJogoTopWar) {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(CarregadorRecursos
					.recursoComoStream(dadosJogoTopWar.getNomeMapa()
							+ ".topwar"));
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
				AvatarTopWar avatarTopWar = (AvatarTopWar) iterator.next();
				long tempDesdeUltMorte = System.currentTimeMillis()
						- avatarTopWar.getUltimaMorte();
				if (avatarTopWar.getVida() <= 0 && tempDesdeUltMorte > 5000) {
					avatarTopWar.setMortoPor(null);
					avatarTopWar.setVida(ConstantesTopWar.VIDA_COMPLETA);
					avatarTopWar.setBalas(ConstantesTopWar.BALAS_ASSALT);
					avatarTopWar
							.setCartuchos(ConstantesTopWar.CARTUCHOS_ASSALT);
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

	public List<AvatarTopWar> getAvatarTopWars() {
		return avatarTopWars;
	}

	public List<AvatarTopWar> getAvatarTopWarsCopia() {
		List<AvatarTopWar> avataresCopy = new ArrayList<AvatarTopWar>();
		while (avataresCopy.isEmpty()) {
			try {
				avataresCopy.addAll(avatarTopWars);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		return avataresCopy;
	}

	public Object atualizaListaAvatares(NnpeTO nnpeTO) {
		AvatarTopWar avatarTopWarJog = obterAvatarTopWar(nnpeTO
				.getSessaoCliente().getNomeJogador());
		if (avatarTopWarJog == null)
			return null;
		Set<AvatarTopWar> ret = new HashSet<AvatarTopWar>();
		synchronized (avatarTopWars) {
			for (Iterator iterator = avatarTopWars.iterator(); iterator
					.hasNext();) {
				AvatarTopWar avatarTopWar = (AvatarTopWar) iterator.next();
				if (avatarTopWar.equals(avatarTopWarJog)) {
					avatarTopWar
							.setUltimaRequisicao(System.currentTimeMillis());
					ret.add(avatarTopWar);
					continue;
				}

				double distacia = GeoUtil.distaciaEntrePontos(avatarTopWarJog
						.getPontoAvatar(), avatarTopWar.getPontoAvatar());
				if (distacia > ConstantesTopWar.LIMITE_VISAO) {
					continue;
				}

				List<Point> line = GeoUtil.drawBresenhamLine(avatarTopWarJog
						.getPontoAvatar(), avatarTopWar.getPontoAvatar());
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
					line = GeoUtil.drawBresenhamLine(avatarTopWarJog
							.getPontoAvatar(), pontoTiro);
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
		retorno.put(ConstantesTopWar.LISTA_AVATARES, ret);
		retorno.put(ConstantesTopWar.BALAS, avatarTopWarJog.getBalas());
		retorno.put(ConstantesTopWar.CARTUCHO, avatarTopWarJog.getCartuchos());
		retorno.put(ConstantesTopWar.RECARREGAR,
				verificaRecarregando(avatarTopWarJog));
		retorno.put(ConstantesTopWar.PTS_VERMELHO, getPtsVermelho());
		retorno.put(ConstantesTopWar.PTS_AZUL, getPtsAzul());
		retorno.put(ConstantesTopWar.TEMPO_JOGO_RESTANTE, tempoRestanteJogo());
		if (avatarTopWarJog.getMortoPor() != null)
			retorno.put(ConstantesTopWar.KILL_CAM, avatarTopWarJog
					.getMortoPor().getNomeJogador());
		for (Iterator iterator = eventos.iterator(); iterator.hasNext();) {
			EventoJogo eventoJogo = (EventoJogo) iterator.next();
			if (eventoJogo.getTempo() > nnpeTO.getMillisSrv()
					&& (System.currentTimeMillis() - eventoJogo.getTempo() < 5000)) {
				retorno.put(ConstantesTopWar.EVENTO_JOGO, eventoJogo);
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

	public boolean campoVisao(List<Point> line, AvatarTopWar avatarTopWar,
			boolean campoVisaoTiro) {
		if (line == null) {
			return false;
		}

		if (line.size() > ConstantesTopWar.LIMITE_VISAO) {
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

	public String moverAvatar(AvatarTopWar avatarTopWar,
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

	public AvatarTopWar entrarNoJogo(String nomeJogador) {
		AvatarTopWar avatarTopWar = new AvatarTopWar();
		setupCalsseJogador(avatarTopWar);
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
				avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeVermelho());
				avatarTopWar.setTime(ConstantesTopWar.TIME_VERMELHO);
			} else {
				avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeAzul());
				avatarTopWar.setTime(ConstantesTopWar.TIME_AZUL);
			}
		}

		avatarTopWar.setNomeJogador(nomeJogador);
		avatarTopWars.add(avatarTopWar);
		return avatarTopWar;
	}

	private void setupCalsseJogador(AvatarTopWar avatarTopWar) {
		avatarTopWar.setVida(ConstantesTopWar.VIDA_COMPLETA);
		if (ConstantesTopWar.ASSAULT.equals(avatarTopWar.getClasse())) {
			avatarTopWar.setArma(ConstantesTopWar.ARMA_ASSAULT);
			avatarTopWar.setBalas(ConstantesTopWar.BALAS_ASSALT);
			avatarTopWar.setCartuchos(ConstantesTopWar.CARTUCHOS_ASSALT);
		} else if (ConstantesTopWar.SHOTGUN.equals(avatarTopWar.getClasse())) {
			avatarTopWar.setArma(ConstantesTopWar.ARMA_SHOTGUN);
			avatarTopWar.setBalas(ConstantesTopWar.BALAS_SHOTGUN);
			avatarTopWar.setCartuchos(ConstantesTopWar.CARTUCHOS_SHOTGUN);
		} else if (ConstantesTopWar.SNIPER.equals(avatarTopWar.getClasse())) {
			avatarTopWar.setArma(ConstantesTopWar.ARMA_SNIPER);
			avatarTopWar.setBalas(ConstantesTopWar.BALAS_SNIPER);
			avatarTopWar.setCartuchos(ConstantesTopWar.CARTUCHOS_SNIPER);
		} else if (ConstantesTopWar.MACHINEGUN.equals(avatarTopWar.getClasse())) {
			avatarTopWar.setArma(ConstantesTopWar.ARMA_MACHINEGUN);
			avatarTopWar.setBalas(ConstantesTopWar.BALAS_MACHINEGUN);
			avatarTopWar.setCartuchos(ConstantesTopWar.CARTUCHOS_MACHINEGUN);
		} else if (ConstantesTopWar.ROCKET.equals(avatarTopWar.getClasse())) {
			avatarTopWar.setArma(ConstantesTopWar.ARMA_ROCKET);
			avatarTopWar.setBalas(ConstantesTopWar.BALAS_ROCKET);
			avatarTopWar.setCartuchos(ConstantesTopWar.CARTUCHOS_ROCKET);
		} else if (ConstantesTopWar.SHIELD.equals(avatarTopWar.getClasse())) {
			avatarTopWar.setArma(ConstantesTopWar.ARMA_SHIELD);
			avatarTopWar.setBalas(0);
			avatarTopWar.setCartuchos(0);
		}
	}

	private int contarJogadores(String time) {
		int cont = 0;
		for (int i = 0; i < avatarTopWars.size(); i++) {
			if (time.equals(avatarTopWars.get(i).getTime())) {
				cont++;
			}
		}
		return cont;
	}

	public AvatarTopWar removerJogador(String nomeJogador) {
		synchronized (avatarTopWars) {
			for (Iterator iterator = avatarTopWars.iterator(); iterator
					.hasNext();) {
				AvatarTopWar avatarTopWar = (AvatarTopWar) iterator.next();
				if (avatarTopWar.getNomeJogador().equals(nomeJogador)) {
					iterator.remove();
					return avatarTopWar;
				}
			}
		}
		return null;
	}

	public AvatarTopWar obterAvatarTopWar(String nomeCliente) {
		List<AvatarTopWar> avatarTopWars = getAvatarTopWars();
		synchronized (avatarTopWars) {
			for (Iterator iterator2 = avatarTopWars.iterator(); iterator2
					.hasNext();) {
				AvatarTopWar avatarTopWar = (AvatarTopWar) iterator2.next();
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
				AvatarTopWar avatarTopWar = (AvatarTopWar) iterator.next();
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

	public Object atacar(AvatarTopWar avatarAtacando, double angulo, int range) {
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
				.getArma())
				&& (atirar(avatarAtacando, angulo, range))) {
			return ConstantesTopWar.OK;
		}

		if (ConstantesTopWar.ARMA_SHOTGUN == avatarAtacando.getArma()
				&& (atirarShotGun(avatarAtacando, angulo, range))) {
			return ConstantesTopWar.OK;
		}

		return null;
	}

	private boolean atirarShotGun(AvatarTopWar avatarAtirador, double angulo,
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
		synchronized (avatarTopWars) {
			for (Iterator iteratorAvatar = avatarTopWars.iterator(); iteratorAvatar
					.hasNext();) {
				AvatarTopWar avatarAlvo = (AvatarTopWar) iteratorAvatar.next();
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

				if (ellipse2d1.contains(avatarAlvo.getPontoAvatar())
						|| ellipse2d2.contains(avatarAlvo.getPontoAvatar())
						|| ellipse2d3.contains(avatarAlvo.getPontoAvatar())) {
					avatarAlvo.setVida(-1);
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
					return true;
				}
			}
		}
		return false;
	}

	private boolean combateCoprpoACorpo(AvatarTopWar avatarAtacando) {
		synchronized (avatarTopWars) {
			for (Iterator iteratorAvatar = avatarTopWars.iterator(); iteratorAvatar
					.hasNext();) {
				AvatarTopWar avatarAlvo = (AvatarTopWar) iteratorAvatar.next();
				if (avatarAlvo.equals(avatarAtacando)) {
					continue;
				}
				if (avatarAlvo.isInvencivel()) {
					continue;
				}
				if (avatarAlvo.getVida() < 1) {
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

	private boolean atirar(AvatarTopWar avatarAtacando, double angulo, int range) {

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

		Point pontoTiro = GeoUtil.calculaPonto(angulo
				+ Util.intervalo(-desvio, desvio), range, avatarAtacando
				.getPontoAvatar());
		List<Point> linhaTiro = GeoUtil.drawBresenhamLine(avatarAtacando
				.getPontoAvatar(), pontoTiro);
		Point pointAnt = null;
		for (int i = 0; i < linhaTiro.size(); i++) {
			Point point = (Point) linhaTiro.get(i);
			for (Iterator iterator = objetoMapaList.iterator(); iterator
					.hasNext();) {
				ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
				if (objetoMapa.getTransparencia() > 11
						&& objetoMapa.getForma().contains(point)) {
					if (pointAnt != null) {
						int balas = consomeBalasArma(avatarAtacando);
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
					AvatarTopWar avatarAlvo = (AvatarTopWar) iteratorAvatar
							.next();
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

					if (processaAcertoDanoTiro(avatarAtacando, point,
							avatarAlvo, i, linhaTiro)) {
						return true;
					}
				}
			}
			pointAnt = point;
		}
		avatarAtacando.setPontoUtlDisparo(pontoTiro);
		return false;
	}

	private boolean processaDanoCombateCorpoCorpo(AvatarTopWar avatarAtacando,
			AvatarTopWar avatarAlvo) {
		if (avatarAtacando.getTime().equals(avatarAlvo.getTime())) {
			return false;
		}
		Shape desenhaAreaFaca = AvatarCliente.desenhaAreaFaca(avatarAtacando
				.getPontoAvatar(), avatarAtacando.getAngulo());
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

	private boolean processaAcertoDanoTiro(AvatarTopWar avatarAtirador,
			Point point, AvatarTopWar avatarAlvo, int indice, List linha) {
		AvatarCliente avatarCliente = new AvatarCliente(avatarAlvo);
		Shape gerarCorpo = avatarCliente.gerarCorpo();
		Shape gerarCabeca = avatarCliente.gerarCabeca();
		if (gerarCorpo.contains(point) || gerarCabeca.contains(point)) {
			int balas = consomeBalasArma(avatarAtirador);
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
						avatarAlvo.setVida(avatarAlvo.getVida()
								- (Util.intervalo(70, 99)));
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

	private int consomeBalasArma(AvatarTopWar avatarAtirador) {
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

	private boolean verificaRecarregando(AvatarTopWar avatarAtirador) {
		return (System.currentTimeMillis() - avatarAtirador.getRecarregar()) < ConstantesTopWar.TEMPO_RECARGA;
	}

	public Object atualizaAngulo(AvatarTopWar avatarTopWar, double angulo) {
		if (avatarTopWar.getVida() <= 0) {
			return null;
		}
		avatarTopWar.setAngulo(angulo);
		return ConstantesTopWar.OK;
	}

	public Object recarregar(AvatarTopWar avatarTopWar) {
		if (avatarTopWar.getVida() <= 0) {
			return null;
		}
		if (avatarTopWar.getCartuchos() <= 0) {
			return null;
		}
		avatarTopWar.setBalas(ConstantesTopWar.BALAS_ASSALT);
		avatarTopWar.setCartuchos(avatarTopWar.getCartuchos() - 1);
		avatarTopWar.setRecarregar(System.currentTimeMillis());
		return ConstantesTopWar.OK;
	}

	public Object moverPontoAvatar(AvatarTopWar avatarTopWar,
			DadosAcaoClienteTopWar acaoClienteTopWar) {
		if (avatarTopWar.getVida() <= 0) {
			return null;
		}
		double distaciaEntrePontos = GeoUtil.distaciaEntrePontos(avatarTopWar
				.getPontoAvatar(), acaoClienteTopWar.getPonto());
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

	public Object alternarFaca(AvatarTopWar avatarTopWar) {
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
				AvatarTopWar avatarTopWar = (AvatarTopWar) iterator.next();
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
}
