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

public class JogoCliente {
	private MapaTopWar mapaTopWar;
	private Point pontoMouseMovendo;
	private Point pontoMouseClicado;
	private Point pontoAvatar;
	private Point pontoAvatarDesenha;
	private double angulo;
	protected boolean rodando = true;
	private Thread threadRepaint;
	private Thread threadDadosSrv;
	private Thread threadMoverMouse;
	private PainelTopWar painelTopWar;
	private List<AvatarCliente> avatarClientes = new ArrayList<AvatarCliente>();
	private JFrame frameTopWar;
	private ControleCliente controleCliente;
	private DadosJogoTopWar dadosJogoTopWar;
	private long millisSrv;
	protected long ultAcao;
	private int velocidade;
	protected long atulaizaAvatarSleep = 30;
	private Thread threadAtualizaPosAvatar;
	private int balas;
	private int cartuchos;

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
				while (rodando && !interrupt) {
					try {
						int media = 0;
						for (Iterator iterator = avatarClientes.iterator(); iterator
								.hasNext();) {
							AvatarCliente avatarCliente = (AvatarCliente) iterator
									.next();
							if (avatarCliente.getPontoAvatarSuave() == null) {
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
				while (controleCliente.isComunicacaoServer() && rodando
						&& !interrupt) {
					try {
						synchronized (avatarClientes) {
							atualizaListaAvatares();
						}
						Thread.sleep(ConstantesTopWar.ATRASO_REDE_PADRAO);
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
				setarPontoMouse(e);
				super.mouseMoved(e);
			}

		});

		painelTopWar.getPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setarPontoMouse(e);
				setarPontoMouseMover(e);
				moverPeloMouse();
				super.mouseClicked(e);
			}
		});
	}

	protected void atirar() {
		if (avatarClientes == null) {
			return;
		}
		if (balas <= 0) {
			controleCliente.recarregar();
		}
		controleCliente.atirar();
	}

	private void setarPontoMouse(MouseEvent e) {
		if (pontoMouseMovendo == null) {
			pontoMouseMovendo = new Point(e.getX(), e.getY());
		}
		pontoMouseMovendo.x = e.getX();
		pontoMouseMovendo.y = e.getY();
		if (pontoAvatar != null)
			angulo = GeoUtil.calculaAngulo(pontoAvatar, pontoMouseMovendo, 90);
		if ((System.currentTimeMillis() - ultAcao) > ConstantesTopWar.ATRASO_REDE_PADRAO) {
			controleCliente.atualizaAngulo();
		}
	}

	protected void moverPeloMouse() {
		if ((System.currentTimeMillis() - ultAcao) < ConstantesTopWar.ATRASO_REDE_PADRAO) {
			return;
		}
		ultAcao = System.currentTimeMillis();
		pararMovimentoMouse();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (pontoAvatarDesenha != null && pontoMouseClicado != null) {
					if (GeoUtil.distaciaEntrePontos(pontoAvatar,
							pontoMouseClicado) < velocidade) {
						return;
					}
					List<Point> line = GeoUtil.drawBresenhamLine(pontoAvatar,
							pontoMouseClicado);
					int nvelo = velocidade;
					boolean pathX = false;
					boolean pathY = false;
					for (int i = 0; i < line.size(); i += nvelo) {
						Point p = line.get(i);
						String ret = null;
						if (p.x == pontoAvatar.x) {
							pathX = false;
						} else if (p.x > pontoAvatar.x + nvelo) {
							Point novoPt = new Point(pontoAvatar.x + nvelo,
									pontoAvatar.y);
							if (!JogoServidor.verificaColisao(novoPt,
									mapaTopWar)) {
								ret = (String) controleCliente.moverDireita();
							}
						} else if (p.x < pontoAvatar.x - nvelo) {
							Point novoPt = new Point(pontoAvatar.x - nvelo,
									pontoAvatar.y);
							if (!JogoServidor.verificaColisao(novoPt,
									mapaTopWar)) {
								ret = (String) controleCliente.moverEsquerda();
							}
						} else {
							pathX = false;
						}
						if (ConstantesTopWar.OK.equals(ret)) {
							pathX = true;
						} else {
							pathX = false;
						}
						if (pathX) {
							try {
								Thread
										.sleep(ConstantesTopWar.ATRASO_REDE_PADRAO);
							} catch (InterruptedException e) {
								Logger.logarExept(e);
								return;
							}
						}
						ret = null;
						if (p.y == pontoAvatar.y) {
							pathY = false;
						} else if (p.y > pontoAvatar.y + nvelo) {
							Point novoPt = new Point(pontoAvatar.x,
									pontoAvatar.y + nvelo);
							if (!JogoServidor.verificaColisao(novoPt,
									mapaTopWar)) {
								ret = (String) controleCliente.moverBaixo();
							}
						} else if (p.y < pontoAvatar.y - nvelo) {
							Point novoPt = new Point(pontoAvatar.x,
									pontoAvatar.y - nvelo);
							if (!JogoServidor.verificaColisao(novoPt,
									mapaTopWar)) {
								ret = (String) controleCliente.moverCima();
							}
						} else {
							pathY = false;
						}
						if (ConstantesTopWar.OK.equals(ret)) {
							pathY = true;
						} else {
							pathY = false;
						}
						if (pathY) {
							try {
								Thread
										.sleep(ConstantesTopWar.ATRASO_REDE_PADRAO);
							} catch (InterruptedException e) {
								Logger.logarExept(e);
								return;
							}
						}

					}
				}
			}
		};
		threadMoverMouse = new Thread(runnable);
		threadMoverMouse.start();
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
				while (rodando && !interrupt) {
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
				rodando = false;
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
		rodando = false;
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
			public void keyPressed(KeyEvent e) {
				pararMovimentoMouse();
				if ((System.currentTimeMillis() - ultAcao) < ConstantesTopWar.ATRASO_REDE_PADRAO) {
					return;
				}
				ultAcao = System.currentTimeMillis();
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
					controleCliente.moverEsquerda();
				}
				if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
					controleCliente.moverBaixo();
				}
				if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
					controleCliente.moverDireita();
				}
				if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
					controleCliente.moverCima();
				}
				if (keyCode == KeyEvent.VK_SPACE) {
					atirar();
				}
				super.keyPressed(e);
			}

		};
		frameTopWar.addKeyListener(keyAdapter);
	}

	public double getAngulo() {
		return angulo;
	}

	private void atualizaListaAvatares() {
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(ConstantesTopWar.ATUALIZAR_LISTA_AVS);
		nnpeTO.setSessaoCliente(controleCliente.getSessaoCliente());
		nnpeTO.setData(dadosJogoTopWar.getNomeJogo());
		Object ret = controleCliente.enviarObjeto(nnpeTO);
		if (!(ret instanceof NnpeTO)) {
			return;
		}
		nnpeTO = (NnpeTO) ret;
		millisSrv = nnpeTO.getMillisSrv();
		Map retorno = (Map) nnpeTO.getData();
		balas = (Integer) retorno.get(ConstantesTopWar.BALAS);
		cartuchos = (Integer) retorno.get(ConstantesTopWar.CARTUCHO);
		List<AvatarTopWar> avatarTopWars = (List<AvatarTopWar>) retorno
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
						angulo = avatarCliente.getAngulo();
						velocidade = avatarCliente.getVelocidade();
						pontoAvatar = avatarCliente.getPontoAvatar();
						pontoAvatarDesenha = avatarCliente.getPontoDesenha();
					}
					break;
				}
			}
			AvatarCliente avatarCliente = new AvatarCliente(avatarTopWar);
			if (!avatarClientes.contains(avatarCliente)) {
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

	private void setarPontoMouseMover(MouseEvent e) {
		if (pontoMouseClicado == null) {
			pontoMouseClicado = new Point(e.getX(), e.getY());
		}
		pontoMouseClicado.x = e.getX();
		pontoMouseClicado.y = e.getY();
	}

}
