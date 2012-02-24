package br.topwar.cliente;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import br.nnpe.GeoUtil;
import br.nnpe.Logger;
import br.nnpe.tos.NnpeTO;
import br.topwar.ConstantesTopWar;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.serial.MapaTopWar;
import br.topwar.servidor.JogoServidor;
import br.topwar.tos.AvatarTopWar;
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
	protected boolean jogoEmAndamento = true;
	protected boolean recarregando;
	private int ptsVermelho;
	private int ptsAzul;
	private PainelTopWar painelTopWar;
	private List<AvatarCliente> avatarClientes = new ArrayList<AvatarCliente>();
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
	private Long tempoRestanteJogo;
	protected boolean seguirMouse;
	protected Thread threadSeguirMouse;
	private String killCam;
	private List<PlacarTopWar> placar;

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
			ois = new ObjectInputStream(
					CarregadorRecursos.recursoComoStream(dadosJogoTopWar
							.getNomeMapa() + ".topwar"));
			mapaTopWar = (MapaTopWar) ois.readObject();
		} catch (Exception e1) {
			Logger.logarExept(e1);
		}
		painelTopWar = new PainelTopWar(this);
		// AvatarCliente avatarCliente = new AvatarCliente("azul",
		// new AvatarTopWar());
		// avatarCliente.setPontoAvatar(new Point(30, 30));
		// double angulo = GeoUtil.calculaAngulo(avatarCliente.getPontoAvatar(),
		// new Point(40, 40), 90);
		// avatarCliente.setAngulo(angulo);
		// avatarClientes.add(avatarCliente);
		// avatarCliente = new AvatarCliente("vermelho", new AvatarTopWar());
		// avatarCliente.setPontoAvatar(new Point(50, 50));
		// angulo = GeoUtil.calculaAngulo(avatarCliente.getPontoAvatar(),
		// new Point(40, 40), 90);
		// avatarCliente.setAngulo(angulo);
		// avatarClientes.add(avatarCliente);
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
							if (atulaizaAvatarSleep < 5) {
								atulaizaAvatarSleep = 5;
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
				setarPontoMouseClicado(e);
				seguirMouse = false;
				if (e.getClickCount() > 1) {
					seguirMouse = true;
					if (threadSeguirMouse != null
							&& threadSeguirMouse.isAlive()) {
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
				} else {
					if (ConstantesTopWar.ARMA_FACA != arma
							&& clicouAvatarAdversario(e.getPoint())) {
						atacar();
					} else {
						moverAvatarPeloMouse(pontoMouseClicado);
					}
				}
				super.mouseClicked(e);
			}
		});
	}

	protected boolean clicouAvatarAdversario(Point p) {
		synchronized (avatarClientes) {
			for (Iterator iterator = avatarClientes.iterator(); iterator
					.hasNext();) {
				AvatarCliente avatarCliente = (AvatarCliente) iterator.next();
				if (avatarCliente.getTime().equals(time)) {
					continue;
				}
				if ((avatarCliente.obeterAreaAvatar().contains(p) || avatarCliente
						.obeterAreaAvatarSuave().contains(p))) {
					return true;
				}
			}
		}
		return false;
	}

	protected void atacar() {
		if (avatarClientes == null) {
			return;
		}
		if (ConstantesTopWar.ARMA_ASSALT == arma && balas <= 0) {
			controleCliente.recarregar();
			return;
		}
		seguirMouse = false;
		pararMovimentoMouse();
		controleCliente.atacar();
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
							Thread.sleep(ConstantesTopWar.ATRASO_REDE_PADRAO / 2);
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

	public List<AvatarCliente> getAvatarClientes() {
		return avatarClientes;
	}

	public void setAvatarClientes(List<AvatarCliente> avatarClientes) {
		this.avatarClientes = avatarClientes;
	}

	private void iniciaJFrame() {
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
		frameTopWar.setTitle(mapaTopWar.getNome());
		frameTopWar.getContentPane().add(painelTopWar.getScrollPane());
		frameTopWar.setSize(800, 600);
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

	}

	private void iniciaListenerTeclado() {
		KeyAdapter keyAdapter = new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				pararMovimentoMouse();
				Thread keys = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(ConstantesTopWar.ATRASO_REDE_PADRAO);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						int keyCode = e.getKeyCode();
						if (keyCode == KeyEvent.VK_A
								|| keyCode == KeyEvent.VK_LEFT) {
							controleCliente.moverEsquerda();
						}
						if (keyCode == KeyEvent.VK_S
								|| keyCode == KeyEvent.VK_DOWN) {
							controleCliente.moverBaixo();
						}
						if (keyCode == KeyEvent.VK_D
								|| keyCode == KeyEvent.VK_RIGHT) {
							controleCliente.moverDireita();
						}
						if (keyCode == KeyEvent.VK_W
								|| keyCode == KeyEvent.VK_UP) {
							controleCliente.moverCima();
						}
						if (keyCode == KeyEvent.VK_SPACE) {
							atacar();
						}
						if (keyCode == KeyEvent.VK_R) {
							controleCliente.recarregar();
						}
						if (keyCode == KeyEvent.VK_CONTROL) {
							controleCliente.alternaFaca();
						}
						if (keyCode == KeyEvent.VK_P
								|| keyCode == KeyEvent.VK_TAB) {
							if (painelTopWar.getTabCont() > 0) {
								painelTopWar.setTabCont(-1);
								return;
							}
							painelTopWar.setTabCont(100);
							NnpeTO nnpeTO = (NnpeTO) controleCliente
									.obterPlacar();
							placar = (List<PlacarTopWar>) nnpeTO.getData();
						}

						if (keyCode == KeyEvent.VK_F11) {
							painelTopWar.setDesenhaImagens(!painelTopWar
									.isDesenhaImagens());
						}
						if (keyCode == KeyEvent.VK_F12) {
							painelTopWar.setDesenhaObjetos(!painelTopWar
									.isDesenhaObjetos());
						}
					}
				});
				keys.start();
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
		nnpeTO.setData(dadosJogoTopWar.getNomeJogo());
		nnpeTO.setMillisSrv(millisSrv);
		Object ret = controleCliente.enviarObjeto(nnpeTO);
		if (!(ret instanceof NnpeTO)) {
			return;
		}
		nnpeTO = (NnpeTO) ret;
		millisSrv = nnpeTO.getMillisSrv();
		Map retorno = (Map) nnpeTO.getData();
		balas = (Integer) retorno.get(ConstantesTopWar.BALAS);
		cartuchos = (Integer) retorno.get(ConstantesTopWar.CARTUCHO);
		recarregando = (Boolean) retorno.get(ConstantesTopWar.RECARREGAR);
		ptsAzul = (Integer) retorno.get(ConstantesTopWar.PTS_AZUL);
		ptsVermelho = (Integer) retorno.get(ConstantesTopWar.PTS_VERMELHO);
		tempoRestanteJogo = (Long) retorno
				.get(ConstantesTopWar.TEMPO_JOGO_RESTANTE);
		killCam = (String) retorno.get(ConstantesTopWar.KILL_CAM);
		EventoJogo eventoJogo = (EventoJogo) retorno
				.get(ConstantesTopWar.EVENTO_JOGO);
		if(eventoJogo!=null){
			Logger.logar(eventoJogo.toString());
		}
		
		Set<AvatarTopWar> avatarTopWars = (HashSet<AvatarTopWar>) retorno
				.get(ConstantesTopWar.LISTA_AVATARES);
		for (Iterator iterator = avatarTopWars.iterator(); iterator.hasNext();) {
			AvatarTopWar avatarTopWar = (AvatarTopWar) iterator.next();
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
				iterator.remove();
			}
		}
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
	}

	public boolean verificaRecarregando() {
		return (recarregando);
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

}
