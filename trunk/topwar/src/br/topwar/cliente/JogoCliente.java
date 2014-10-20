package br.topwar.cliente;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import br.nnpe.GeoUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.cliente.NnpeApplet;
import br.nnpe.tos.NnpeTO;
import br.topwar.ConstantesTopWar;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.recursos.idiomas.Lang;
import br.topwar.serial.MapaTopWar;
import br.topwar.serial.ObjetoMapa;
import br.topwar.servidor.JogoServidor;
import br.topwar.tos.DadosAcaoClienteTopWar;
import br.topwar.tos.ObjTopWar;
import br.topwar.tos.DadosAvatar;
import br.topwar.tos.DadosJogoTopWar;
import br.topwar.tos.EventoJogo;
import br.topwar.tos.PlacarTopWar;
import br.topwar.tos.RadioMsg;

public class JogoCliente {
	private MapaTopWar mapaTopWar;
	private Point pontoMouseMovendo;
	private Point pontoMouseClicado;
	private Point pontoAvatar;
	private Point pontoAvatarDesenha;
	private double angulo;
	private boolean jogoEmAndamento = true;
	private boolean renderiza = true;
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
	private Thread threadAtualizaAngulo;
	private Thread threadSeguirMouse;
	private String utlEvento = "0";
	private Long tempoRestanteJogo;
	private boolean seguirMouse;
	private String killCam;
	private List<PlacarTopWar> placar;
	private Set<EventoJogo> eventos = new HashSet<EventoJogo>();
	private List<RadioMsg> radio = new ArrayList<RadioMsg>();
	protected long clickTime;
	private String proxClasse;
	private long ultRadio = 0;
	protected boolean modoTexto;
	private StringBuffer textoEnviar = new StringBuffer();
	protected boolean modoTextoSomenteTime;
	private int fps = 0;
	protected double fpsLimite = 60D;
	private boolean atacando;

	public PainelTopWar getPainelTopWar() {
		return painelTopWar;
	}

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
	}

	public void carregaMapa() {
		if (dadosJogoTopWar == null) {
			return;
		}
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(
					CarregadorRecursos.recursoComoStream(dadosJogoTopWar
							.getNomeMapa() + ".topwar"));
			mapaTopWar = (MapaTopWar) ois.readObject();
		} catch (Exception e1) {
			Logger.logarExept(e1);
		}
	}

	public void inciaJogo() {
		carregaMapa();
		iniciaJFrame();
		painelTopWar = new PainelTopWar(this);
		iniciaMouseListener();
		iniciaListenerTeclado();
		iniciaThreadAtualizaTela();
		iniciaThreadAtualizaDadosServidor();
		iniciaThreadAtualizaPosAvatar();
	}

	public void setDadosJogoTopWar(DadosJogoTopWar dadosJogoTopWar) {
		this.dadosJogoTopWar = dadosJogoTopWar;
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
						Collection<AvatarCliente> avatarClientesCopia = getAvatarClientesCopia();
						for (Iterator iterator = avatarClientesCopia.iterator(); iterator
								.hasNext();) {
							AvatarCliente avatarCliente = (AvatarCliente) iterator
									.next();
							if (avatarCliente.getVida() <= 0) {
								continue;
							}
							if (avatarCliente.isLocal()) {
								avatarLocal = avatarCliente;
							}
							if (avatarCliente.getPontoAvatarSuave() == null) {
								avatarCliente.setPontoAvatarSuave(avatarCliente
										.getPontoAvatar());
								continue;
							}
							if (avatarCliente.isInvencivel()
									&& getMapaTopWar() != null
									&& (GeoUtil.distaciaEntrePontos(
											getMapaTopWar().getPontoTimeAzul(),
											avatarCliente.getPontoAvatar()) < avatarCliente
											.getVelocidade() || GeoUtil
											.distaciaEntrePontos(
													getMapaTopWar()
															.getPontoTimeVermelho(),
													avatarCliente
															.getPontoAvatar()) < avatarCliente
											.getVelocidade())) {
								avatarCliente.setPontoAvatarSuave(avatarCliente
										.getPontoAvatar());
								continue;
							}
							List<Point> linha = GeoUtil.drawBresenhamLine(
									avatarCliente.getPontoAvatar(),
									avatarCliente.getPontoAvatarSuave());
							int noventaPorcento = (int) ((linha.size() * 0.9));
							if (linha.size() > avatarCliente.getVelocidade()) {
								avatarCliente.setPontoAvatarSuave(linha
										.get(noventaPorcento));
							} else if (linha.size() > 1) {
								Point point = linha.get(linha.size() - 2);
								avatarCliente.setPontoAvatarSuave(point);
							} else {
								avatarCliente.setPontoAvatarSuave(avatarCliente
										.getPontoAvatar());
							}
							media += linha.size();
						}
						Thread.sleep(atulaizaAvatarSleep);
					} catch (InterruptedException e) {
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
						long sleep = ConstantesTopWar.ATRASO_REDE_PADRAO;
						if (controleCliente.getLatenciaReal() > ConstantesTopWar.ATRASO_REDE_PADRAO) {
							sleep = ConstantesTopWar.DUPLO_ATRASO_REDE_PADRAO;
						}
						// if (controleCliente.getLatenciaReal() >
						// ConstantesTopWar.DUPLO_ATRASO_REDE_PADRAO) {
						// sleep = controleCliente.getLatenciaReal();
						// }
						Thread.sleep(sleep);
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
		frameTopWar.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (pontoAvatar == null) {
					return;
				}
				setarPontoMouseMover(e);
				super.mouseMoved(e);
			}

		});

		frameTopWar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frameTopWar.requestFocus();
				if (painelTopWar.verificaVoltaMenuPrincipal(e.getPoint())) {
					controleCliente.voltaMenuPrincipal();
					return;
				}

				if (painelTopWar.verificaComandoMudarClasse(e.getPoint())) {
					return;
				}

				setarPontoMouseClicado(e);
				if (e.getClickCount() == 1
						&& MouseEvent.BUTTON3 == e.getButton()) {
					moverAvatarPeloMouse(pontoMouseClicado);
					return;
				}

				if (e.getClickCount() > 1
						&& MouseEvent.BUTTON3 == e.getButton()) {
					doisCliques();
					return;
				}

				seguirMouse = false;
				if (ConstantesTopWar.ARMA_FACA == arma) {
					moverAvatarPeloMouse(pontoMouseClicado);
					atacar();
				} else {
					atacar();
				}
				super.mouseClicked(e);
			}

			private void doisCliques() {
				System.out.println("doisCliques()");
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

	protected boolean mirouAvatarAdversario(Point p, int arma) {
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
			if (ConstantesTopWar.ARMA_FACA == arma && distaciaEntrePontos > 50) {
				return false;
			} else if (distaciaEntrePontos < 50) {
				return true;
			}
		}
		return false;
	}

	protected void atacar() {
		atacar(5);
	}

	protected void atacar(final int deleyAtacar) {
		if (atacando) {
			return;
		}
		if (avatarClientes == null) {
			return;
		}
		if (ConstantesTopWar.ARMA_FACA != arma && balas <= 0
				&& arma != ConstantesTopWar.ARMA_SHIELD) {
			recarregar();
			return;
		}
		threadAtacar = new Thread(new Runnable() {
			@Override
			public void run() {
				atacando = true;
				try {
					if (arma == ConstantesTopWar.ARMA_SHIELD) {
						while (controleCliente.verificaDelay()) {
							Thread.sleep(200);
						}
						controleCliente.alternaFaca();
						while (controleCliente.verificaDelay()) {
							Thread.sleep(200);
						}
						controleCliente.atacar();
						while (controleCliente.verificaDelay()) {
							Thread.sleep(200);
						}
						controleCliente.alternaFaca();
					} else {
						while (controleCliente.verificaDelay()) {
							Thread.sleep(deleyAtacar);
						}
						controleCliente.atacar();
					}
				} catch (Exception e) {
					Logger.logarExept(e);

				} finally {
					atacando = false;
				}
			}
		});
		threadAtacar.start();
	}

	private void setarPontoMouseMover(MouseEvent e) {
		Point descontoCentraliza = painelTopWar.getDescontoCentraliza();
		if (pontoMouseMovendo == null) {
			pontoMouseMovendo = new Point(e.getX() + descontoCentraliza.x,
					e.getY() + descontoCentraliza.y);
		}
		pontoMouseMovendo.x = e.getX() + descontoCentraliza.x;
		pontoMouseMovendo.y = e.getY() + descontoCentraliza.y;
		if (pontoAvatar != null)
			angulo = GeoUtil.calculaAngulo(pontoAvatar, pontoMouseMovendo, 90);
		if (!seguirMouse) {
			if (!(threadMoverMouse != null && threadMoverMouse.isAlive())) {
				if (threadAtualizaAngulo != null
						&& threadAtualizaAngulo.isAlive()) {
					return;
				}
				threadAtualizaAngulo = new Thread(new Runnable() {
					@Override
					public void run() {
						while (controleCliente.verificaDelay()) {
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						controleCliente.atualizaAngulo();
					}
				});
				threadAtualizaAngulo.start();
			}
			boolean mirouAvatarAdversario = mirouAvatarAdversario(
					pontoMouseMovendo, arma);
			if (mirouAvatarAdversario && arma == ConstantesTopWar.ARMA_SHOTGUN) {
				atacar(500);
			}
			if (mirouAvatarAdversario && arma == ConstantesTopWar.ARMA_ASSAULT) {
				atacar(200);
			}
			if (mirouAvatarAdversario
					&& arma == ConstantesTopWar.ARMA_MACHINEGUN) {
				atacar(100);
			}
			if (mirouAvatarAdversario && arma == ConstantesTopWar.ARMA_FACA) {
				atacar(100);
			}

		}

	}

	public void moverAvatarPeloMouse(final Point pontoMouseSeguir) {
		pararMovimentoMouse();
		if (pontoAvatar != null && pontoMouseSeguir != null) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					Object moverPonto = controleCliente
							.moverPonto(pontoMouseSeguir);
					if (ConstantesTopWar.ESPERE.equals(moverPonto)
							|| moverPonto == null) {
						setPontoMouseClicado(null);
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

	public void setPontoMouseClicado(Point pontoMouseClicado) {
		this.pontoMouseClicado = pontoMouseClicado;
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
			renderiza = false;
		}
		threadRepaint = new Thread(new Runnable() {
			@Override
			public void run() {
				int frames = 0;
				long startTime = System.currentTimeMillis();
				long lastTime = System.nanoTime();

				double delta = 0;
				while (renderiza) {
					long now = System.nanoTime();
					double nsPerTick = 1000000000D / fpsLimite;
					delta += (now - lastTime) / nsPerTick;
					lastTime = now;
					boolean render = false;
					while (delta >= 1) {
						render = true;
						delta -= 1;
					}
					if (render) {
						painelTopWar.atualiza();
						++frames;
					}
					if ((System.currentTimeMillis() - startTime) > 1000) {
						startTime = System.currentTimeMillis();
						fps = frames;
						frames = 0;
						delta = 0;
					}
				}

				NnpeTO nnpeTO = (NnpeTO) controleCliente.obterPlacar();
				if (nnpeTO != null) {
					placar = (List<PlacarTopWar>) nnpeTO.getData();
					painelTopWar.setTabCont(100);
					painelTopWar.atualiza();
				}
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

	public JFrame getFrameTopWar() {
		return frameTopWar;
	}

	public void setFrameTopWar(JFrame frameTopWar) {
		this.frameTopWar = frameTopWar;
	}

	public void iniciaJFrame() {
		if (frameTopWar != null && frameTopWar.isVisible()) {
			if (mapaTopWar != null)
				frameTopWar.setTitle(mapaTopWar.getNome());
			frameTopWar.getContentPane().removeAll();
			return;
		}
		setarFrameTopWar();
		if (controleCliente.isLocal()) {
			frameTopWar.getContentPane().removeAll();
		}
		frameTopWar.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				jogoEmAndamento = false;
				controleCliente.sairJogo();
				matarTodasThreads();
				super.windowClosing(e);
			}
		});
		Cursor crossHair = new Cursor(Cursor.CROSSHAIR_CURSOR);
		if (mapaTopWar != null) {
			frameTopWar.setCursor(crossHair);
			frameTopWar.setTitle(mapaTopWar.getNome());
		}
		if (controleCliente.isLocal()) {
			frameTopWar.getContentPane().validate();
			frameTopWar.requestFocus();
		} else {
			frameTopWar.setSize(1024, 768);
			frameTopWar.setVisible(true);
		}
	}

	public void setarFrameTopWar() {
		if (frameTopWar == null)
			frameTopWar = new JFrame();
	}

	public void matarTodasThreads() {
		jogoEmAndamento = false;
		try {
			if (threadAtualizaAngulo != null) {
				threadAtualizaAngulo.interrupt();
			}
			if (threadAtualizaPosAvatar != null) {
				threadAtualizaPosAvatar.interrupt();
			}
			if (threadRepaint != null) {
				threadRepaint.interrupt();
				renderiza = false;
			}
			if (threadDadosSrv != null) {
				threadDadosSrv.interrupt();
			}
		} catch (Exception e) {
			Logger.logarExept(e);
		}
	}

	public List<RadioMsg> getRadioMsgCopia() {
		List<RadioMsg> radiosCopy = new ArrayList<RadioMsg>();
		while (radiosCopy.isEmpty()) {
			try {
				if (radio.isEmpty()) {
					return radiosCopy;
				}
				radiosCopy.addAll(radio);
			} catch (Exception e) {
				radiosCopy.clear();
				Logger.logarExept(e);
			}
		}
		return radiosCopy;
	}

	public List<RadioMsg> getRadioMsgCopiaPainel() {
		List<RadioMsg> radiosCopy = new ArrayList<RadioMsg>();
		while (radiosCopy.isEmpty()) {
			try {
				if (radio.isEmpty()) {
					return radiosCopy;
				}
				int i = radio.size() - 1;
				int cont = 0;
				while (true) {
					if (i < 0 || cont > 5) {
						break;
					}
					radiosCopy.add(radio.get(i--));
					cont++;
				}
			} catch (Exception e) {
				radiosCopy.clear();
				Logger.logarExept(e);
			}
		}
		return radiosCopy;
	}

	public StringBuffer getTextoEnviar() {
		return textoEnviar;
	}

	public void setTextoEnviar(StringBuffer textoEnviar) {
		this.textoEnviar = textoEnviar;
	}

	public boolean isModoTexto() {
		return modoTexto;
	}

	private void iniciaListenerTeclado() {
		KeyAdapter keyAdapter = new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_ENTER) {
					modoTexto = !modoTexto;
					if (!modoTexto) {
						controleCliente.enviaTextoRadio(textoEnviar.toString(),
								modoTextoSomenteTime);
						textoEnviar = new StringBuffer();
					}
				}
				if (modoTexto) {
					if (keyCode == KeyEvent.VK_F1) {
						modoTextoSomenteTime = !modoTextoSomenteTime;
					}

					if (keyCode == KeyEvent.VK_BACK_SPACE) {
						if (textoEnviar.length() > 0)
							textoEnviar.delete(textoEnviar.length() - 1,
									textoEnviar.length());
					} else {
						String keyToText = Util.keyToText(keyCode);
						if (!e.isShiftDown()) {
							keyToText = keyToText.toLowerCase();
						}
						// Logger.logar("keyToText " + keyToText);
						textoEnviar.append(keyToText);
					}

					return;
				}
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
		KeyListener[] keyListeners = frameTopWar.getKeyListeners();
		for (int i = 0; i < keyListeners.length; i++) {
			frameTopWar.removeKeyListener(keyListeners[i]);
		}
		frameTopWar.addKeyListener(keyAdapter);
		if (frameTopWar.getParent() != null) {
			frameTopWar.getParent().addKeyListener(keyAdapter);
			frameTopWar.addKeyListener(keyAdapter);
		}
	}

	public boolean isModoTextoSomenteTime() {
		return modoTextoSomenteTime;
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
				+ obterUltimoEvento() + "&" + obterUltRadio());
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
			// if (!eventos.contains(eventoJogo)) {
			// Logger.logar("Evento Recebido Cliente " + eventoJogo);
			// }
			eventos.add(eventoJogo);
			utlEvento = new Long(eventoJogo.getTempo()).toString();
		}
		RadioMsg radioMsg = (RadioMsg) retorno.get(ConstantesTopWar.RADIO_JOGO);
		if (radioMsg != null) {
			Logger.logar("Radio Recebido Cliente " + radioMsg);
			radio.add(radioMsg);
			ultRadio = radioMsg.getId();
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

	private long obterUltRadio() {
		return ultRadio;
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
		Point descontoCentraliza = painelTopWar.getDescontoCentraliza();
		if (pontoMouseClicado == null) {
			pontoMouseClicado = new Point(e.getX() + descontoCentraliza.x,
					e.getY() + descontoCentraliza.y);
		}
		pontoMouseClicado.x = e.getX() + descontoCentraliza.x;
		pontoMouseClicado.y = e.getY() + descontoCentraliza.y;
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
				avataresCopy.clear();
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
		}
		return eventosCopy;
	}

	private void processaComandosTeclado(int keyCode) {

		if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
			controleCliente.moverEsquerda();
			pararMovimentoMouse();
			seguirMouse = false;
		}
		if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
			controleCliente.moverBaixo();
			pararMovimentoMouse();
			seguirMouse = false;
		}
		if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
			controleCliente.moverDireita();
			pararMovimentoMouse();
			seguirMouse = false;
		}
		if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
			controleCliente.moverCima();
			pararMovimentoMouse();
			seguirMouse = false;
		}
		if (keyCode == KeyEvent.VK_SPACE) {
			atacar();
			pararMovimentoMouse();
			seguirMouse = false;
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

	public void gerarRadio() {
		ButtonGroup groupRadio = new ButtonGroup();
		JRadioButton todos = new JRadioButton();
		final JRadioButton time = new JRadioButton();
		groupRadio.add(todos);
		groupRadio.add(time);

		JPanel radioPanel = new JPanel();
		radioPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("radioTodos");
			}
		});
		radioPanel.add(todos);
		radioPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("radioTime");
			}
		});
		radioPanel.add(time);
		final JTextField radioText = new JTextField(50);
		radioPanel.add(radioText);
		frameTopWar.getContentPane().add(radioPanel, BorderLayout.SOUTH);
		radioText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controleCliente.enviaTextoRadio(radioText.getText(),
						time.isSelected());
				radioText.setText("");
				frameTopWar.requestFocus();
			}
		});
		frameTopWar.requestFocus();
	}

	public JApplet getApplet() {
		return controleCliente.getApplet();
	}

	public boolean verificaLag() {
		if (controleCliente == null) {
			return false;
		}
		return controleCliente.getLatenciaReal() > NnpeApplet.LATENCIA_MAX;
	}

	public int getLag() {
		if (controleCliente == null) {
			return 5;
		}
		return controleCliente.getLatenciaReal();
	}

	public void mostrarGraficos() {
		BufferStrategy strategy = getFrameTopWar().getBufferStrategy();
		strategy.getDrawGraphics().dispose();
		strategy.show();
	}

	public Graphics2D obterGraficos() {
		BufferStrategy strategy = getFrameTopWar().getBufferStrategy();
		if (strategy == null) {
			getFrameTopWar().createBufferStrategy(2);
			strategy = getFrameTopWar().getBufferStrategy();
		}
		return (Graphics2D) strategy.getDrawGraphics();
	}

	public int getFps() {
		return fps;
	}

	public void mudaLimiteFps() {
		if (fpsLimite == 60D) {
			fpsLimite = 30D;
		} else if (fpsLimite == 30D) {
			fpsLimite = 60D;
		}

	}
}
