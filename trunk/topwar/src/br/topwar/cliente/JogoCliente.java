package br.topwar.cliente;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import br.nnpe.GeoUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.tos.NnpeTO;
import br.topwar.ConstantesTopWar;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.serial.MapaTopWar;
import br.topwar.tos.ObjTopWar;
import br.topwar.tos.DadosAvatar;
import br.topwar.tos.DadosJogoTopWar;
import br.topwar.tos.EventoJogo;
import br.topwar.tos.PlacarTopWar;

public class JogoCliente {
	private MapaTopWar mapaTopWar;
	private Point pontoMouseMovendo;
	private Point pontoMouseClicado;
	private Point pontoAvatar;
	private Point pontoAvatarDesenha;
	private double angulo;
	private boolean jogoEmAndamento = true;
	private boolean recarregando;
	private int ptsVermelho;
	private int ptsAzul;
	private PainelTopWar painelTopWar;
	private AvatarCliente avatarLocal;
	private Collection<AvatarCliente> avatarClientes = new HashSet<AvatarCliente>();
	private JFrame frameTopWar;
	private ControleCliente controleCliente;
	private DadosJogoTopWar dadosJogoTopWar;
	private long millisSrv;
	private int velocidade;
	protected long atulaizaAvatarSleep = 30;
	private int balas;
	private int cartuchos;
	private int arma;
	private int vida;
	private String time;
	private double anguloServidor;
	private Thread threadAtualizaPosAvatar;
	private Thread threadRepaint;
	private Thread threadDadosSrv;
	private Thread threadMoverMouse;

	private Thread threadAtacar;
	private Thread threadRecarregar;
	private Thread threadMudarClasse;
	private Thread threadAlternaFaca;
	private Thread threadMouseCliqueUnico;
	private String utlEvento = "0";
	private Long tempoRestanteJogo;
	private boolean seguirMouse;
	private Thread threadSeguirMouse;
	private String killCam;
	private List<PlacarTopWar> placar;
	private Set<EventoJogo> eventos = new HashSet<EventoJogo>();
	protected long clickTime;
	private String proxClasse;

	public String getProxClasse() {
		return proxClasse;
	}

	public void setProxClasse(String proxClasse) {
		this.proxClasse = proxClasse;
	}

	public boolean isJogoEmAndamento() {
		return jogoEmAndamento;
	}

	public Point getPontoMouseMovendo() {
		return pontoMouseMovendo;
	}

	public int getPtsVermelho() {
		return ptsVermelho;
	}

	public int getPtsAzul() {
		return ptsAzul;
	}

	public JogoCliente(DadosJogoTopWar dadosJogoTopWar,
			ControleCliente controleCliente) {
		this.dadosJogoTopWar = dadosJogoTopWar;
		this.controleCliente = controleCliente;
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(CarregadorRecursos
					.recursoComoStream(dadosJogoTopWar.getNomeMapa()
							+ ".topwar"));
			mapaTopWar = (MapaTopWar) ois.readObject();
		} catch (Exception e1) {
			Logger.logarExept(e1);
		}
		painelTopWar = new PainelTopWar(this);
	}

	public void inciaJogo() {
		iniciaJFrame();
		iniciaMouseListener();
		iniciaThreadAtualizaTela();
		iniciaThreadAtualizaDadosServidor();
		iniciaListenerTeclado();
		iniciaThreadAtualizaPosAvatar();
	}

	private void iniciaThreadAtualizaPosAvatar() {
		if (threadAtualizaPosAvatar != null) {
			threadAtualizaPosAvatar.interrupt();
		}
		threadAtualizaPosAvatar = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean interrupt = false;
				while (jogoEmAndamento && !interrupt) {
					try {
						int media = 0;
						synchronized (avatarClientes) {
							for (Iterator iterator = avatarClientes.iterator(); iterator
									.hasNext();) {
								AvatarCliente avatarCliente = (AvatarCliente) iterator
										.next();
								if (avatarCliente.isLocal()) {
									avatarLocal = avatarCliente;
								}
								if (avatarCliente.getPontoAvatarSuave() == null) {
									avatarCliente
											.setPontoAvatarSuave(avatarCliente
													.getPontoAvatar());
									continue;
								}
								List<Point> linha = GeoUtil.drawBresenhamLine(
										avatarCliente.getPontoAvatar(),
										avatarCliente.getPontoAvatarSuave());
								int noventaPorcento = (int) ((linha.size() * 0.9));
								if (linha.size() > avatarCliente
										.getVelocidade()) {
									avatarCliente.setPontoAvatarSuave(linha
											.get(noventaPorcento));
								} else if (linha.size() > 1) {
									Point point = linha.get(linha.size() - 2);
									avatarCliente.setPontoAvatarSuave(point);
								} else {
									avatarCliente
											.setPontoAvatarSuave(avatarCliente
													.getPontoAvatar());
								}
								media += linha.size();
							}
						}
						if (media > velocidade) {
							atulaizaAvatarSleep -= (media - velocidade);
							if (atulaizaAvatarSleep < 15) {
								atulaizaAvatarSleep = 15;
							}
						} else {
							atulaizaAvatarSleep = 30;
						}
						Thread.sleep(atulaizaAvatarSleep);
					} catch (InterruptedException e) {
						interrupt = true;
						Logger.logarExept(e);
					}
				}
			}
		});
		threadAtualizaPosAvatar.start();

	}

	public double getAnguloServidor() {
		return anguloServidor;
	}

	public long getMillisSrv() {
		return millisSrv;
	}

	private void iniciaThreadAtualizaDadosServidor() {

		if (threadDadosSrv != null) {
			threadDadosSrv.interrupt();
		}
		threadDadosSrv = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean interrupt = false;
				while (controleCliente.isComunicacaoServer() && jogoEmAndamento
						&& !interrupt) {
					try {
						synchronized (avatarClientes) {
							atualizaListaAvatares();
						}
						Thread.sleep(ConstantesTopWar.ATRASO_REDE_PADRAO);
						if (tempoRestanteJogo <= 0) {
							jogoEmAndamento = false;
						}
					} catch (InterruptedException e) {
						interrupt = true;
						Logger.logarExept(e);
					}
				}
			}
		});
		threadDadosSrv.start();

	}

	private void iniciaMouseListener() {
		painelTopWar.getPanel().addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (pontoAvatar == null) {
					return;
				}
				setarPontoMouseMover(e);
				super.mouseMoved(e);
			}

		});

		painelTopWar.getPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (painelTopWar.verificaComandoMudarClasse(e.getPoint())) {
					return;
				}

				if (MouseEvent.BUTTON3 == e.getButton()) {
					alternaFaca();
					return;
				}

				setarPontoMouseClicado(e);
				seguirMouse = false;
				if (ConstantesTopWar.ARMA_FACA != arma
						&& clicouAvatarAdversario(e.getPoint())) {
					atacar();
				} else {
					if (e.getClickCount() > 1) {
						doisCliques();
					} else {
						umClique();
					}
				}
				super.mouseClicked(e);
			}

			private void umClique() {
				if ((threadMouseCliqueUnico != null && threadMouseCliqueUnico
						.isAlive())) {
					Logger.logar("Retorno Clique");
					return;
				}
				threadMouseCliqueUnico = new Thread(new Runnable() {
					@Override
					public void run() {
						while (controleCliente.verificaDelay()) {
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
								Logger.logarExept(e);
							}
						}
						moverAvatarPeloMouse(pontoMouseClicado);
					}
				});
				threadMouseCliqueUnico.start();
			}

			private void doisCliques() {
				seguirMouse = true;
				if (threadSeguirMouse != null && threadSeguirMouse.isAlive()) {
					threadSeguirMouse.interrupt();
				}
				threadSeguirMouse = new Thread(new Runnable() {
					@Override
					public void run() {
						while (seguirMouse && pontoMouseMovendo != null) {
							moverAvatarPeloMouse(pontoMouseMovendo);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								return;
							}
						}
					}
				});
				threadSeguirMouse.start();
			}
		});
	}

	public boolean isSeguirMouse() {
		return seguirMouse;
	}

	protected boolean clicouAvatarAdversario(Point p) {
		Collection<AvatarCliente> avatarClientesCopia = getAvatarClientesCopia();
		for (Iterator iterator = avatarClientesCopia.iterator(); iterator
				.hasNext();) {
			AvatarCliente avatarCliente = (AvatarCliente) iterator.next();
			if (avatarCliente.getTime().equals(time)) {
				continue;
			}
			if (avatarCliente.getVida() <= 0) {
				continue;
			}
			double distaciaEntrePontos = GeoUtil.distaciaEntrePontos(p,
					avatarCliente.getPontoAvatar());
			if (distaciaEntrePontos < 50) {
				return true;
			}
		}
		return false;
	}

	protected void atacar() {
		if (avatarClientes == null) {
			return;
		}
		if (ConstantesTopWar.ARMA_FACA != arma && balas <= 0) {
			recarregar();
			return;
		}
		if (threadAtacar != null && threadAtacar.isAlive()) {
			return;
		}
		threadAtacar = new Thread(new Runnable() {
			@Override
			public void run() {
				while (controleCliente.verificaDelay()) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						Logger.logarExept(e);
					}
				}
				controleCliente.atacar();
			}
		});
		threadAtacar.start();
	}

	private void setarPontoMouseMover(MouseEvent e) {
		if (pontoMouseMovendo == null) {
			pontoMouseMovendo = new Point(e.getX(), e.getY());
		}
		pontoMouseMovendo.x = e.getX();
		pontoMouseMovendo.y = e.getY();
		if (pontoAvatar != null)
			angulo = GeoUtil.calculaAngulo(pontoAvatar, pontoMouseMovendo, 90);
		if (!seguirMouse) {
			if (!(threadMoverMouse != null && threadMoverMouse.isAlive())) {
				controleCliente.atualizaAngulo();
			}
		}

	}

	public void moverAvatarPeloMouse(final Point pontoMouseSegir) {
		pararMovimentoMouse();
		if (pontoAvatar != null && pontoMouseSegir != null) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					Object ret = ConstantesTopWar.OK;
					while (vida > 0
							&& (ConstantesTopWar.OK.equals(ret) || ConstantesTopWar.ESPERE
									.equals(ret))
							&& GeoUtil.distaciaEntrePontos(pontoAvatar,
									pontoMouseSegir) > velocidade) {
						List<Point> line = GeoUtil.drawBresenhamLine(
								pontoAvatar, pontoMouseSegir);
						if (line.size() > velocidade) {
							Point p = line.get(velocidade);
							if (GeoUtil.distaciaEntrePontos(pontoAvatar, p) > velocidade) {
								p = line.get(velocidade - 1);
							}
							ret = controleCliente.moverPonto(p);
						}
						try {
							Thread
									.sleep(ConstantesTopWar.MEIO_ATRASO_REDE_PADRAO);
						} catch (InterruptedException e) {
							return;
						}
					}
				}
			};
			threadMoverMouse = new Thread(runnable);
			threadMoverMouse.start();
		}
	}

	public Point getPontoAvatar() {
		return pontoAvatar;
	}

	public Point getPontoMouseClicado() {
		return pontoMouseClicado;
	}

	public Point getPontoAvatarDesenha() {
		return pontoAvatarDesenha;
	}

	private boolean pararMovimentoMouse() {
		if (threadMoverMouse != null && threadMoverMouse.isAlive()) {
			threadMoverMouse.interrupt();
			return true;
		}
		return false;
	}

	private void iniciaThreadAtualizaTela() {
		if (threadRepaint != null) {
			threadRepaint.interrupt();
		}
		threadRepaint = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean interrupt = false;
				while (jogoEmAndamento && !interrupt) {
					try {
						painelTopWar.atualiza();
						Thread.sleep(40);
					} catch (InterruptedException e) {
						interrupt = true;
						Logger.logarExept(e);
					}
				}
				NnpeTO nnpeTO = (NnpeTO) controleCliente.obterPlacar();
				placar = (List<PlacarTopWar>) nnpeTO.getData();
				painelTopWar.setTabCont(100);
				painelTopWar.atualiza();
			}
		});
		threadRepaint.start();
	}

	public MapaTopWar getMapaTopWar() {
		return mapaTopWar;
	}

	public void setMapaTopWar(MapaTopWar mapaTopWar) {
		this.mapaTopWar = mapaTopWar;
	}

	public Collection<AvatarCliente> getAvatarClientes() {
		return avatarClientes;
	}

	public void iniciaJFrame() {
		if (frameTopWar != null && frameTopWar.isVisible()) {
			frameTopWar.setTitle(mapaTopWar.getNome());
			frameTopWar.getContentPane().add(painelTopWar.getScrollPane());
			return;
		}
		frameTopWar = new JFrame();
		frameTopWar.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				jogoEmAndamento = false;
				super.windowClosing(e);
			}
		});
		frameTopWar.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Cursor crossHair = new Cursor(Cursor.CROSSHAIR_CURSOR);
		frameTopWar.setCursor(crossHair);
		if (mapaTopWar != null) {
			frameTopWar.setTitle(mapaTopWar.getNome());
			frameTopWar.getContentPane().add(painelTopWar.getScrollPane());
		}
		frameTopWar.setSize(1024, 768);
		frameTopWar.setVisible(true);
		frameTopWar.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				matarTodasThreads();
				super.windowClosed(e);
			}
		});
	}

	protected void matarTodasThreads() {
		jogoEmAndamento = false;
		try {
			if (threadAtualizaPosAvatar != null) {
				threadAtualizaPosAvatar.interrupt();
			}
			if (threadRepaint != null) {
				threadRepaint.interrupt();
			}
			if (threadDadosSrv != null) {
				threadDadosSrv.interrupt();
			}
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		controleCliente.sairJogo();
	}

	private void iniciaListenerTeclado() {
		KeyAdapter keyAdapter = new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				int keyCode = e.getKeyCode();
				processaComandosTeclado(keyCode);
				if (keyCode == KeyEvent.VK_P || keyCode == KeyEvent.VK_TAB) {
					if (painelTopWar.getTabCont() > 0) {
						painelTopWar.setTabCont(-1);
						return;
					}
					NnpeTO nnpeTO = (NnpeTO) controleCliente.obterPlacar();
					placar = (List<PlacarTopWar>) nnpeTO.getData();
					painelTopWar.setTabCont(100);
				}

				if (keyCode == KeyEvent.VK_F11) {
					painelTopWar.setDesenhaImagens(!painelTopWar
							.isDesenhaImagens());
				}
				if (keyCode == KeyEvent.VK_F12) {
					painelTopWar.setDesenhaObjetos(!painelTopWar
							.isDesenhaObjetos());
				}
				super.keyPressed(e);
			}

		};
		frameTopWar.addKeyListener(keyAdapter);
	}

	public String getKillCam() {
		return killCam;
	}

	public double getAngulo() {
		return angulo;
	}

	private void atualizaListaAvatares() {
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.ATUALIZAR_LISTA_AVS);
		nnpeTO.setSessaoCliente(controleCliente.getSessaoCliente());
		nnpeTO.setData(dadosJogoTopWar.getNomeJogo() + "&"
				+ obterUltimoEvento());
		nnpeTO.setMillisSrv(millisSrv);
		Object ret = controleCliente.enviarObjeto(nnpeTO);
		if (!(ret instanceof NnpeTO)) {
			return;
		}
		nnpeTO = (NnpeTO) ret;
		millisSrv = nnpeTO.getMillisSrv();
		Map retorno = (Map) nnpeTO.getData();
		if (retorno == null) {
			return;
		}
		balas = (Integer) retorno.get(ConstantesTopWar.BALAS);
		cartuchos = (Integer) retorno.get(ConstantesTopWar.CARTUCHO);
		recarregando = (Boolean) retorno.get(ConstantesTopWar.RECARREGAR);
		proxClasse = (String) retorno.get(ConstantesTopWar.MUDAR_CLASSE);
		ptsAzul = (Integer) retorno.get(ConstantesTopWar.PTS_AZUL);
		ptsVermelho = (Integer) retorno.get(ConstantesTopWar.PTS_VERMELHO);
		tempoRestanteJogo = (Long) retorno
				.get(ConstantesTopWar.TEMPO_JOGO_RESTANTE);
		killCam = (String) retorno.get(ConstantesTopWar.KILL_CAM);
		EventoJogo eventoJogo = (EventoJogo) retorno
				.get(ConstantesTopWar.EVENTO_JOGO);
		if (eventoJogo != null) {
			if (!eventos.contains(eventoJogo)) {
				Logger.logar("Evento Recebido Cliente " + eventoJogo);
			}
			eventos.add(eventoJogo);
			utlEvento = new Long(eventoJogo.getTempo()).toString();
		}
		Set<ObjTopWar> avatarTopWars = (HashSet<ObjTopWar>) DadosAvatar
				.desEmpacotarLista(retorno.get(ConstantesTopWar.LISTA_AVATARES));
		for (Iterator iterator = avatarTopWars.iterator(); iterator.hasNext();) {
			ObjTopWar avatarTopWar = (ObjTopWar) iterator.next();
			for (Iterator iterator2 = avatarClientes.iterator(); iterator2
					.hasNext();) {
				AvatarCliente avatarCliente = (AvatarCliente) iterator2.next();
				if (avatarTopWar.getNomeJogador().equals(
						avatarCliente.getNomeJogador())) {
					avatarCliente.setPontoAvatarOld(avatarCliente
							.getPontoAvatar());
					avatarCliente.setAvatarTopWar(avatarTopWar);
					if (controleCliente.getNomeJogador().equals(
							avatarTopWar.getNomeJogador())) {
						avatarCliente.setLocal(true);
						anguloServidor = avatarCliente.getAngulo();
						time = avatarCliente.getTime();
						arma = avatarCliente.getArma();
						vida = avatarCliente.getVida();
						velocidade = avatarCliente.getVelocidade();
						pontoAvatar = avatarCliente.getPontoAvatar();
						pontoAvatarDesenha = avatarCliente.getPontoDesenha();
						if (vida <= 0) {
							seguirMouse = false;
						}
					}
					break;
				}
			}
			AvatarCliente avatarCliente = new AvatarCliente(avatarTopWar);
			if (!avatarClientes.contains(avatarCliente)) {
				if (avatarCliente.getVida() <= 0) {
					avatarCliente.setQuadroAnimacaoMorte(3);
				} else {
					avatarCliente.setQuadroAnimacaoMorte(0);
				}
				avatarClientes.add(avatarCliente);
			}
		}
		for (Iterator iterator = avatarClientes.iterator(); iterator.hasNext();) {
			AvatarCliente avatarCliente = (AvatarCliente) iterator.next();
			if (!avatarTopWars.contains(avatarCliente.getAvatarTopWar())) {
				if (ConstantesTopWar.OBJ_ROCKET == avatarCliente.getArma()) {
					painelTopWar.explosao(avatarCliente.getPontoAvatar());
				}
				iterator.remove();
			}
		}
	}

	private String obterUltimoEvento() {
		if (Util.isNullOrEmpty(utlEvento)) {
			return "0";
		}
		return utlEvento;
	}

	public Long getTempoRestanteJogo() {
		return tempoRestanteJogo;
	}

	public int getArma() {
		return arma;
	}

	public int getBalas() {
		return balas;
	}

	public int getCartuchos() {
		return cartuchos;
	}

	private void setarPontoMouseClicado(MouseEvent e) {
		if (pontoMouseClicado == null) {
			pontoMouseClicado = new Point(e.getX(), e.getY());
		}
		pontoMouseClicado.x = e.getX();
		pontoMouseClicado.y = e.getY();
		clickTime = System.currentTimeMillis();
	}

	public boolean verificaRecarregando() {
		return (recarregando);
	}

	public long getClickTime() {
		return clickTime;
	}

	public List<PlacarTopWar> geraListaPlacarOrdenada(String time) {
		List<PlacarTopWar> list = new ArrayList<PlacarTopWar>();
		if (placar != null) {
			for (Iterator iterator = placar.iterator(); iterator.hasNext();) {
				PlacarTopWar placarTopWar = (PlacarTopWar) iterator.next();
				if (time.equals(placarTopWar.getTime())) {
					list.add(placarTopWar);
				}
			}
			Collections.sort(list, new Comparator<PlacarTopWar>() {
				@Override
				public int compare(PlacarTopWar o1, PlacarTopWar o2) {
					return o2.ordenacao().compareTo(o1.ordenacao());
				}
			});
		}
		return list;
	}

	public Collection<AvatarCliente> getAvatarClientesCopia() {
		Set<AvatarCliente> avataresCopy = new HashSet<AvatarCliente>();
		while (avataresCopy.isEmpty()) {
			try {
				avataresCopy.addAll(avatarClientes);
			} catch (Exception e) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
				}
			}
		}
		return avataresCopy;
	}

	public List<EventoJogo> getEventosCopia() {
		List<EventoJogo> eventosCopy = new ArrayList<EventoJogo>();
		try {
			eventosCopy.addAll(eventos);
		} catch (Exception e) {
			Logger.logarExept(e);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		return eventosCopy;
	}

	private void processaComandosTeclado(int keyCode) {
		seguirMouse = false;
		pararMovimentoMouse();
		if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
			if (avatarLocal != null) {
				Point p = avatarLocal.getPontoAvatarSuave();
				p.x -= 1;
				avatarLocal.setPontoAvatarSuave(p);
			}
			controleCliente.moverEsquerda();
		}
		if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
			if (avatarLocal != null) {
				Point p = avatarLocal.getPontoAvatarSuave();
				p.y += 1;
				avatarLocal.setPontoAvatarSuave(p);
			}
			controleCliente.moverBaixo();
		}
		if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
			if (avatarLocal != null) {
				Point p = avatarLocal.getPontoAvatarSuave();
				p.x += 1;
				avatarLocal.setPontoAvatarSuave(p);
			}
			controleCliente.moverDireita();
		}
		if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
			if (avatarLocal != null) {
				Point p = avatarLocal.getPontoAvatarSuave();
				p.y -= 1;
				avatarLocal.setPontoAvatarSuave(p);
			}
			controleCliente.moverCima();
		}
		if (keyCode == KeyEvent.VK_SPACE) {
			atacar();
		}
		if (keyCode == KeyEvent.VK_R) {
			recarregar();
		}
		if (keyCode == KeyEvent.VK_CONTROL) {
			alternaFaca();
		}
	}

	private void alternaFaca() {
		if (threadAlternaFaca != null && threadAlternaFaca.isAlive()) {
			return;
		}
		threadAlternaFaca = new Thread(new Runnable() {
			@Override
			public void run() {
				while (controleCliente.verificaDelay()) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						Logger.logarExept(e);
					}
				}
				controleCliente.alternaFaca();
			}
		});
		threadAlternaFaca.start();

	}

	private void recarregar() {
		if (threadRecarregar != null && threadRecarregar.isAlive()) {
			return;
		}
		threadRecarregar = new Thread(new Runnable() {
			@Override
			public void run() {
				while (controleCliente.verificaDelay()) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						Logger.logarExept(e);
					}
				}
				controleCliente.recarregar();
			}
		});
		threadRecarregar.start();
	}

	public void mudarClasse(final String classe) {
		if (threadMudarClasse != null && threadMudarClasse.isAlive()) {
			return;
		}
		threadMudarClasse = new Thread(new Runnable() {
			@Override
			public void run() {
				while (controleCliente.verificaDelay()) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						Logger.logarExept(e);
					}
				}
				controleCliente.mudarClasse(classe);
			}
		});
		threadMudarClasse.start();

	}
}
