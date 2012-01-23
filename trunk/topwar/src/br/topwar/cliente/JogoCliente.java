package br.topwar.cliente;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
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
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.serial.MapaTopWar;
import br.topwar.tos.DadosJogoTopWar;

public class JogoCliente {
	private MapaTopWar mapaTopWar;
	private Point pontoMouse;
	protected boolean rodando = true;
	private Thread threadRepaint;
	private PainelTopWar painelTopWar;
	protected Set pressed = new HashSet();
	private List<AvatarCliente> avatarClientes = new ArrayList<AvatarCliente>();
	private JFrame frameTopWar;
	private ControleCliente controleCliente;
	private Thread threadTeclado;
	public final static BufferedImage azul = CarregadorRecursos
			.carregaBufferedImageTransparecia("azul.png", Color.MAGENTA);
	public final static BufferedImage vermelho = CarregadorRecursos
			.carregaBufferedImageTransparecia("vermelho.png", Color.MAGENTA);

	public JogoCliente(DadosJogoTopWar dadosJogoTopWar,
			ControleCliente controleCliente) {
		this.controleCliente = controleCliente;
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(
					CarregadorRecursos.recursoComoStream("mapa9.topwar"));
			mapaTopWar = (MapaTopWar) ois.readObject();
		} catch (Exception e1) {
			Logger.logarExept(e1);
		}

		painelTopWar = new PainelTopWar(this);

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
						e.printStackTrace();
					}
				}
			}
		});
		threadRepaint.start();

		AvatarCliente avatarCliente = new AvatarCliente("azul", azul);
		avatarCliente.setPontoAvatar(new Point(30, 30));
		double angulo = GeoUtil.calculaAngulo(avatarCliente.getPontoAvatar(),
				new Point(40, 40), 90);
		avatarCliente.setAngulo(angulo);
		avatarClientes.add(avatarCliente);
		avatarCliente = new AvatarCliente("vermelho", vermelho);
		avatarCliente.setPontoAvatar(new Point(50, 50));
		angulo = GeoUtil.calculaAngulo(avatarCliente.getPontoAvatar(),
				new Point(40, 40), 90);
		avatarCliente.setAngulo(angulo);
		avatarClientes.add(avatarCliente);
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

	public void inciaJogo() {
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

		KeyAdapter keyAdapter = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				synchronized (pressed) {
					pressed.add(keyCode);
				}
				super.keyPressed(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();
				synchronized (pressed) {
					pressed.remove(keyCode);
				}
				super.keyReleased(e);
			}

		};
		frameTopWar.addKeyListener(keyAdapter);
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
							if (keyCode == KeyEvent.VK_A) {
								controleCliente.moverEsquerda();
							}
							if (keyCode == KeyEvent.VK_S) {
								controleCliente.moverBaixo();
							}
							if (keyCode == KeyEvent.VK_D) {
								controleCliente.moverDireita();
							}
							if (keyCode == KeyEvent.VK_W) {
								controleCliente.moverCima();
							}
						}
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		threadTeclado.start();

	}

}
