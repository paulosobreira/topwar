package conceito;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import br.nnpe.GeoUtil;
import br.nnpe.ImageUtil;
import br.nnpe.Util;
import br.topwar.recursos.CarregadorRecursos;

public class Conceito {

	protected static boolean rodando = true;
	protected static int velocidade = 3;
	final static int CIMA = KeyEvent.VK_W;
	final static int BAIXO = KeyEvent.VK_S;
	final static int ESQUERDA = KeyEvent.VK_A;
	final static int DIREIRA = KeyEvent.VK_D;
	static private int anim;
	static private Thread atirando;
	protected static Set pressed = new HashSet();
	public static Map<String, BufferedImage> mapImgs = new HashMap<String, BufferedImage>();
	private static long lastAnim;
	private static String time = "azul";
	public final static BufferedImage azul = CarregadorRecursos
			.carregaBufferedImageTransparecia("azul.png", Color.MAGENTA);
	public final static BufferedImage vermelho = CarregadorRecursos
			.carregaBufferedImageTransparecia("vermelho.png", Color.MAGENTA);

	public static void main(String[] args) {
		if ("azul".equals(time)) {
			gerarMapaImagens(azul);
		} else {
			gerarMapaImagens(vermelho);
		}
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				rodando = false;
				super.windowClosing(e);
			}
		});
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		final BufferedImage img = CarregadorRecursos.carregaImg("mapa9.jpg");
		final Point p = new Point(150, 80);
		final Point m = new Point(0, 0);
		final AffineTransform afRotate = new AffineTransform();
		KeyAdapter keyAdapter = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				pressed.add(keyCode);
				move(keyCode);
				super.keyPressed(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();
				pressed.remove(keyCode);
				super.keyReleased(e);
			}

			private void move(int keyCode) {

				if (keyCode == KeyEvent.VK_DOWN) {
					velocidade--;
					if (velocidade < 1) {
						velocidade = 1;
					}
				}
				if (keyCode == KeyEvent.VK_UP) {
					velocidade++;
				}
			}

		};

		final JPanel panel = new JPanel() {
			protected void paintComponent(java.awt.Graphics g) {
				Graphics2D graphics2d = (Graphics2D) g;
				graphics2d.drawImage(img, null, 0, 0);
				graphics2d.setColor(Color.WHITE);
				// graphics2d.fill(new Rectangle(0, 0, 800, 600));
				Shape corpo = desenhaCorpo(p);
				double angulo = GeoUtil.calculaAngulo(p, m, 90);
				double rad = Math.toRadians((double) angulo);
				GeneralPath gpCorpo = new GeneralPath(corpo);
				if (angulo < 0) {
					angulo = 360 + angulo;
				}
				graphics2d.setColor(Color.MAGENTA);
				g.drawString("Angulo " + angulo, 10, 10);
				g.drawString("Amin " + anim, 10, 25);
				g.drawString("Velocidade " + velocidade, 10, 40);
				synchronized (mapImgs) {
					BufferedImage imgJog = null;
					if (angulo >= 0 && angulo <= 22.5 || angulo > 337.5) {
						imgJog = mapImgs.get(time + "-" + anim + "-0");
					} else if (angulo > 292.5 && angulo <= 337.5) {
						imgJog = mapImgs.get(time + "-" + anim + "-1");
					} else if (angulo > 247.5 && angulo <= 292.5) {
						imgJog = mapImgs.get(time + "-" + anim + "-2");
					} else if (angulo > 202.5 && angulo <= 247.5) {
						imgJog = mapImgs.get(time + "-" + anim + "-3");
					} else if (angulo > 157.5 && angulo <= 202.5) {
						imgJog = mapImgs.get(time + "-" + anim + "-4");
					} else if (angulo > 112.5 && angulo <= 157.5) {
						imgJog = mapImgs.get(time + "-" + anim + "-5");
					} else if (angulo > 67.5 && angulo <= 112.5) {
						imgJog = mapImgs.get(time + "-" + anim + "-6");
					} else if (angulo > 22.5 && angulo <= 67.5) {
						imgJog = mapImgs.get(time + "-" + anim + "-7");
					}
					if (imgJog == null) {
						System.out.println("Angulo nulo " + angulo);
					} else {
						g.drawImage(imgJog, p.x - (imgJog.getWidth() / 2), p.y
								- (imgJog.getHeight() / 2), null);
					}
				}
				afRotate.setToRotation(rad, gpCorpo.getBounds().getCenterX(),
						gpCorpo.getBounds().getCenterY());
//				graphics2d.draw(gpCorpo.createTransformedShape(afRotate));
				graphics2d.setColor(Color.RED);
				Shape cabeca = desenhaCabeca(GeoUtil.calculaPonto(angulo, 6, p));
				GeneralPath gpCabeca = new GeneralPath(cabeca);
				afRotate.setToRotation(rad, gpCabeca.getBounds().getCenterX(),
						gpCabeca.getBounds().getCenterY());
//				graphics2d.draw(gpCabeca.createTransformedShape(afRotate));
				graphics2d.setColor(Color.CYAN);
				// graphics2d.drawLine(p.x, p.y, m.x, m.y);
				// List linha = GeoUtil.drawBresenhamLine(p, m);
				// for (Iterator iterator = linha.iterator();
				// iterator.hasNext();) {
				// Point ptLinha = (Point) iterator.next();
				// graphics2d.fillOval(ptLinha.x, ptLinha.y, 2, 2);
				// }
				if (atirando != null && atirando.isAlive()) {
					for (int i = 0; i < 3; i++) {
						Point nOri = new Point(p.x, p.y);
						Point nDst = new Point(m.x + Util.intervalo(-15, 15),
								m.y + Util.intervalo(-15, 15));
						graphics2d.setColor(Color.YELLOW);
						List<Point> linha = GeoUtil.drawBresenhamLine(nOri,
								nDst);
						if (linha.size() > 30) {
							int intIni = Util.intervalo(10, 20);
							Point pIni = linha.get(intIni);
							Point pFim = linha.get(intIni
									+ Util.intervalo(1, 10));
							graphics2d.drawLine(pIni.x, pIni.y, pFim.x, pFim.y);
						}
					}
				}
			};
		};
		frame.getContentPane().add(panel);
		frame.setSize(800, 600);
		frame.addKeyListener(keyAdapter);
		panel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				m.x = e.getX();
				m.y = e.getY();
				super.mouseMoved(e);
			}

		});
		panel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (atirando == null || !atirando.isAlive()) {
					atirando = new Thread(new Runnable() {
						public void run() {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					atirando.start();
				}
				super.mouseClicked(e);
			}
		});
		Thread threadRepaint = new Thread(new Runnable() {
			@Override
			public void run() {
				while (rodando) {
					panel.repaint();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		threadRepaint.start();
		Thread threadTeclado = new Thread(new Runnable() {
			@Override
			public void run() {
				while (rodando) {
					for (Iterator iterator = pressed.iterator(); iterator
							.hasNext();) {
						Integer key = (Integer) iterator.next();
						int keyCode = key.intValue();
						if (keyCode == KeyEvent.VK_A) {
							animar();
							p.x = p.x - velocidade;
						}
						if (keyCode == KeyEvent.VK_S) {
							animar();
							p.y = p.y + velocidade;
						}
						if (keyCode == KeyEvent.VK_D) {
							animar();
							p.x = p.x + velocidade;
						}
						if (keyCode == KeyEvent.VK_W) {
							animar();
							p.y = p.y - velocidade;
						}
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		threadTeclado.start();

		frame.setVisible(true);

	}

	private static void animar() {
		int intMin = (100 + (10 * velocidade));
		if (intMin < 60) {
			intMin = 60;
		}
		if ((System.currentTimeMillis() - lastAnim) < intMin) {
			return;
		}
		anim++;
		if (anim > 3) {
			anim = 0;
		}
		lastAnim = System.currentTimeMillis();
	}

	private static void gerarMapaImagens(BufferedImage src) {
		int altura = src.getHeight() / 8;
		int largura = src.getWidth() / 4;
		BufferedImage bf = new BufferedImage(src.getWidth(), src.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) bf.getGraphics();
		graphics.drawImage(src, 0, 0, null);
		graphics.setColor(Color.MAGENTA);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 8; j++) {
				Rectangle rect = new Rectangle(i * largura, j * altura,
						largura, altura);
				graphics.draw(rect);
				graphics.drawString("i=" + i + " j=" + j, rect.x, rect.y
						+ altura - 10);
				BufferedImage bufferedImage = ImageUtil.gerarSubImagem(src,
						rect);
				String key = time + "-" + i + "-" + j;
				mapImgs.put(key, bufferedImage);
				// JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(
				// mapImgs.get(key))), key,
				// JOptionPane.INFORMATION_MESSAGE);
			}
		}

		JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(bf)),
				"bf", JOptionPane.INFORMATION_MESSAGE);

	}

	protected static Shape desenhaCabeca(Point p) {
		return new Rectangle2D.Double(p.x - 3, p.y - 2, 6, 4);
	}

	protected static Shape desenhaCorpo(Point p) {
		return new Rectangle2D.Double(p.x - 8, p.y - 3, 16, 6);
	}
}
