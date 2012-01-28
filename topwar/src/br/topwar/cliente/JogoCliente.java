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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import br.nnpe.GeoUtil;
import br.nnpe.Logger;
import br.nnpe.tos.NnpeTO;
import br.topwar.ConstantesTopWar;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.serial.MapaTopWar;
import br.topwar.tos.AvatarTopWar;
import br.topwar.tos.DadosJogoTopWar;

public class JogoCliente {
	private MapaTopWar mapaTopWar;
	private Point pontoMouse;
	private Point pontoMouseMover;
	private Point pontoAvatar;
	private double angulo;
	protected boolean rodando = true;
	private Thread threadRepaint;
	private Thread threadDadosSrv;
	private Thread threadTeclado;
	private Thread threadMoverMouse;
	private PainelTopWar painelTopWar;
	protected Set pressed = new HashSet();
	private List<AvatarCliente> avatarClientes = new ArrayList<AvatarCliente>();
	private JFrame frameTopWar;
	private ControleCliente controleCliente;
	private DadosJogoTopWar dadosJogoTopWar;

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
		iniciaThreadTeclado();
	}

	private void iniciaThreadAtualizaDadosServidor() {

		if (threadDadosSrv != null) {
			threadDadosSrv.interrupt();
		}
		threadDadosSrv = new Thread(new Runnable() {
			@Override
			public void run() {
				while (rodando) {
					try {
						synchronized (avatarClientes) {
							atualizaListaAvatares();
						}
						Thread.sleep(150);
					} catch (InterruptedException e) {
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
				if (pontoMouse == null) {
					pontoMouse = new Point(e.getX(), e.getY());
				}
				pontoMouse.x = e.getX();
				pontoMouse.y = e.getY();
				angulo = GeoUtil.calculaAngulo(pontoAvatar, pontoMouse, 90);

				super.mouseMoved(e);
			}

		});

		painelTopWar.getPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (pontoMouseMover == null) {
					pontoMouseMover = new Point(e.getX(), e.getY());
				}
				pontoMouseMover.x = e.getX();
				pontoMouseMover.y = e.getY();
				moverPeloMouse();
				super.mouseClicked(e);
			}
		});
	}

	protected void moverPeloMouse() {
		pararMovimentoMouse();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (pontoAvatar != null && pontoMouseMover != null) {
					boolean path = true;
					while (path
							&& GeoUtil.distaciaEntrePontos(pontoAvatar,
									pontoMouseMover) > 5) {
						try {
							Thread.sleep(150);
						} catch (InterruptedException e) {
							path = false;
							Logger.logarExept(e);
						}
						String ret = null;
						if (pontoMouseMover.x > pontoAvatar.x) {
							ret = (String) controleCliente.moverDireita();
						} else {
							ret = (String) controleCliente.moverEsquerda();
						}
						if (!ConstantesTopWar.OK.equals(ret)) {
							path = false;
						}
						ret = null;
						try {
							Thread.sleep(150);
						} catch (InterruptedException e) {
							path = false;
							Logger.logarExept(e);
						}
						if (pontoMouseMover.y > pontoAvatar.y) {
							ret = (String) controleCliente.moverBaixo();
						} else {
							ret = (String) controleCliente.moverCima();
						}
						if (!ConstantesTopWar.OK.equals(ret)) {
							path = false;
						}
					}
				}
			}
		};
		threadMoverMouse = new Thread(runnable);
		threadMoverMouse.start();
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
				while (rodando) {
					try {
						painelTopWar.atualiza();
						Thread.sleep(50);
					} catch (InterruptedException e) {
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
			if (threadRepaint != null) {
				threadRepaint.interrupt();
			}
			if (threadDadosSrv != null) {
				threadDadosSrv.interrupt();
			}
			if (threadTeclado != null) {
				threadTeclado.interrupt();
			}
			pararMovimentoMouse();
		} catch (Exception e) {
			Logger.logarExept(e);
		}

	}

	private void iniciaListenerTeclado() {
		KeyAdapter keyAdapter = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				synchronized (pressed) {
					pressed.add(keyCode);
				}
				pararMovimentoMouse();
				super.keyPressed(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();
				synchronized (pressed) {
					pressed.remove(keyCode);
				}
				pararMovimentoMouse();
				super.keyReleased(e);
			}

		};
		frameTopWar.addKeyListener(keyAdapter);
	}

	private void iniciaThreadTeclado() {
		if (threadTeclado != null) {
			threadTeclado.interrupt();
		}
		threadTeclado = new Thread(new Runnable() {
			@Override
			public void run() {
				while (rodando) {
					synchronized (pressed) {
						for (Iterator iterator = pressed.iterator(); iterator
								.hasNext();) {
							Integer key = (Integer) iterator.next();
							int keyCode = key.intValue();
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
						}
					}
					try {
						Thread.sleep(150);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		threadTeclado.start();
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
		List<AvatarTopWar> avatarTopWars = (List<AvatarTopWar>) nnpeTO
				.getData();
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
						pontoAvatar = avatarCliente.getPontoAvatar();
					}
					break;
				}
			}
			AvatarCliente avatarCliente = new AvatarCliente(
					avatarTopWar.getTime(), avatarTopWar);
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

}
