package br.topwar.editor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
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
import java.io.IOException;
import java.io.ObjectInputStream;
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
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.nnpe.GeoUtil;
import br.nnpe.ImageUtil;
import br.nnpe.Util;
import br.topwar.ConstantesTopWar;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.serial.MapaTopWar;
import br.topwar.serial.ObjetoMapa;

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
	private static MapaTopWar mapaTopWar;
	private static JPanel panel;
	private static JScrollPane scrollPane;
	private static Point pontoMouse;
	private static Point pontoAvatar;
	private static Rectangle areaAvatar;
	private static boolean desenhaObjetos;
	public final static BufferedImage azul = CarregadorRecursos
			.carregaBufferedImageTransparecia("azul.png", Color.MAGENTA);
	public final static BufferedImage vermelho = CarregadorRecursos
			.carregaBufferedImageTransparecia("vermelho.png", Color.MAGENTA);
	public final static BufferedImage crosshair = CarregadorRecursos
			.carregaBufferedImageTransparecia("crosshair.png", null);

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
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
		Cursor crossHair = new Cursor(Cursor.CROSSHAIR_CURSOR);
		frame.setCursor(crossHair);
		ObjectInputStream ois = new ObjectInputStream(
				CarregadorRecursos.recursoComoStream("mapa9.topwar"));

		mapaTopWar = (MapaTopWar) ois.readObject();
		frame.setTitle(mapaTopWar.getNome());

		final BufferedImage img = CarregadorRecursos
				.carregaBackGround(mapaTopWar.getBackGround());

		pontoAvatar = new Point(650, 600);
		pontoMouse = new Point(0, 0);
		KeyAdapter keyAdapter = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				synchronized (pressed) {
					pressed.add(keyCode);
				}
				move(keyCode);
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

		geraPainel(img, pontoAvatar, pontoMouse);
		scrollPane = new JScrollPane(panel,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getViewport().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
			}
		});
		frame.getContentPane().add(scrollPane);
		frame.setSize(800, 600);
		frame.addKeyListener(keyAdapter);
		panel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				pontoMouse.x = e.getX();
				pontoMouse.y = e.getY();
				super.mouseMoved(e);
			}

		});
		panel.setDoubleBuffered(true);
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
						centralizarPontoDireto(pontoAvatar);
						// pontoMouse =
						// MouseInfo.getPointerInfo().getLocation();
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
					synchronized (pressed) {
						for (Iterator iterator = pressed.iterator(); iterator
								.hasNext();) {
							Integer key = (Integer) iterator.next();
							int keyCode = key.intValue();
							Point novoPonto = new Point(pontoAvatar.x,
									pontoAvatar.y);
							if (keyCode == KeyEvent.VK_A) {
								novoPonto.x = novoPonto.x - velocidade;
							}
							if (keyCode == KeyEvent.VK_S) {
								novoPonto.y = novoPonto.y + velocidade;
							}
							if (keyCode == KeyEvent.VK_D) {
								novoPonto.x = novoPonto.x + velocidade;
							}
							if (keyCode == KeyEvent.VK_W) {
								novoPonto.y = novoPonto.y - velocidade;
							}
							if (keyCode == KeyEvent.VK_ESCAPE) {
								desenhaObjetos = !desenhaObjetos;
							}
							if (!verificaColisao(novoPonto)) {
								animar();
								pontoAvatar.x = novoPonto.x;
								pontoAvatar.y = novoPonto.y;
							}
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

	protected static boolean verificaColisao(Point novoPonto) {
		return verificaColisao(novoPonto, areaAvatar, mapaTopWar);
	}

	protected static boolean verificaColisao(Point novoPonto,
			Rectangle areaAvatar, MapaTopWar mapaTopWar) {
		Point desenha = new Point(novoPonto.x
				- ((int) areaAvatar.getWidth() / 2), novoPonto.y
				- ((int) areaAvatar.getHeight() / 3));
		Rectangle novaArea = new Rectangle(desenha.x, desenha.y,
				(int) areaAvatar.getWidth(), (int) areaAvatar.getHeight());
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		for (Iterator iterator = objetoMapaList.iterator(); iterator.hasNext();) {
			ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
			if (objetoMapa.getTransparencia() > 100
					&& objetoMapa.getEfeito() == null
					&& objetoMapa.getForma().intersects(novaArea)) {
				return true;
			}
		}
		return false;
	}

	private static void geraPainel(final BufferedImage img, final Point p,
			final Point m) {
		panel = new JPanel() {

			public Dimension getPreferredSize() {
				if (img == null) {
					return super.getPreferredSize();
				}
				return new Dimension(img.getWidth(), img.getHeight());
			}

			protected void paintComponent(java.awt.Graphics g) {
				super.paintComponent(g);
				Graphics2D graphics2d = (Graphics2D) g;
				graphics2d.drawImage(img, null, 0, 0);
				double angulo = GeoUtil.calculaAngulo(pontoAvatar, pontoMouse,
						90);
				if (angulo < 0) {
					angulo = 360 + angulo;
				}
				graphics2d.setColor(Color.MAGENTA);
				Rectangle limitesViewPort = (Rectangle) limitesViewPort();
				graphics2d.drawString("Angulo " + angulo,
						limitesViewPort.x + 10, limitesViewPort.y + 10);
				graphics2d.drawString("Amin " + anim, limitesViewPort.x + 10,
						limitesViewPort.y + 25);
				graphics2d.drawString("Velocidade " + velocidade,
						limitesViewPort.x + 10, limitesViewPort.y + 40);
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
						Point desenha = new Point(
								p.x - (imgJog.getWidth() / 2), p.y
										- (imgJog.getHeight() / 3));
						areaAvatar = new Rectangle(desenha.x, desenha.y,
								imgJog.getWidth(), imgJog.getHeight());
						imgJog = Conceito.processaTransparencia(imgJog,
								desenha, areaAvatar, mapaTopWar);
						imgJog = Conceito.processaGrade(imgJog, desenha,
								areaAvatar, mapaTopWar);
						graphics2d
								.drawImage(imgJog, desenha.x, desenha.y, null);
						if (desenhaObjetos) {
							graphics2d.setColor(Color.MAGENTA);
							graphics2d.draw(areaAvatar);
						}
					}
				}
				if (desenhaObjetos) {
					graphics2d.setColor(Color.CYAN);
					graphics2d.draw(gerarCorpo());
					graphics2d.drawLine(p.x, p.y, m.x, m.y);
					graphics2d.setColor(Color.RED);
					graphics2d.draw(gerarCabeca());
				}
				// List linha = GeoUtil.drawBresenhamLine(p, m);
				// for (Iterator iterator = linha.iterator();
				// iterator.hasNext();) {
				// Point ptLinha = (Point) iterator.next();
				// graphics2d.fillOval(ptLinha.x, ptLinha.y, 2, 2);
				// }
				if (atirando != null && atirando.isAlive()) {

					/**
					 * shotgun
					 */
					for (int i = 0; i < 3; i++) {
						Point nOri = new Point(p.x, p.y);
						Point nDst = new Point(m.x + Util.intervalo(-30, 30),
								m.y + Util.intervalo(-30, 30));
						
						List<Point> linha = GeoUtil.drawBresenhamLine(nOri,
								nDst);
						int cont = 0;
						for (Iterator iterator = linha.iterator(); iterator
								.hasNext();) {
							cont++;
							if (cont > 100) {
								break;
							}
							Point point = (Point) iterator.next();
							if (Math.random() > .9) {
								if(Math.random()>.7){
									graphics2d.setColor(Color.WHITE);
								}else{
									graphics2d.setColor(Color.LIGHT_GRAY);
								}
								graphics2d.drawOval(point.x, point.y,
										Util.intervalo(1, 2),
										Util.intervalo(1, 2));
							}
						}
						if (linha.size() > 100) {
							int intIni = Util.intervalo(1, 20);
							Point pIni = linha.get(intIni);
							Point pFim = linha.get(intIni
									+ Util.intervalo(1, 30));
							if(Math.random()>.7){
								graphics2d.setColor(Color.WHITE);
							}else{
								graphics2d.setColor(Color.LIGHT_GRAY);
							}
							graphics2d.drawLine(pIni.x, pIni.y, pFim.x, pFim.y);
						}
					}

					/**
					 * assaut
					 */
					// for (int i = 0; i < 5; i++) {
					// Point nOri = new Point(p.x, p.y);
					// Point nDst = new Point(m.x + Util.intervalo(-15, 15),
					// m.y + Util.intervalo(-15, 15));
					// graphics2d.setColor(Color.YELLOW);
					// List<Point> linha = GeoUtil.drawBresenhamLine(nOri,
					// nDst);
					// if (linha.size() > 40) {
					// int intIni = Util.intervalo(10, 20);
					// Point pIni = linha.get(intIni);
					// Point pFim = linha.get(intIni
					// + Util.intervalo(1, 20));
					// graphics2d.drawLine(pIni.x, pIni.y, pFim.x, pFim.y);
					// }
					// }
				}
				if (desenhaObjetos) {
					List<ObjetoMapa> objetoMapaList = mapaTopWar
							.getObjetoMapaList();
					graphics2d.setColor(Color.YELLOW);
					for (Iterator iterator = objetoMapaList.iterator(); iterator
							.hasNext();) {
						ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
						graphics2d.draw(objetoMapa.getForma());
					}
				}
				if (pontoMouse != null) {
					Point desenha = new Point(pontoMouse.x
							- (crosshair.getWidth() / 2), pontoMouse.y
							- (crosshair.getHeight() / 2));
					graphics2d.drawImage(crosshair, desenha.x, desenha.y, null);
				}
			};
		};
	}

	protected static Shape gerarCabeca() {
		AffineTransform afRotate = new AffineTransform();
		double angulo = GeoUtil.calculaAngulo(pontoAvatar, pontoMouse, 90);
		double rad = Math.toRadians((double) angulo);
		Shape cabeca = desenhaCabeca(GeoUtil.calculaPonto(angulo, 6,
				pontoAvatar));
		GeneralPath gpCabeca = new GeneralPath(cabeca);
		afRotate.setToRotation(rad, gpCabeca.getBounds().getCenterX(), gpCabeca
				.getBounds().getCenterY());
		return gpCabeca.createTransformedShape(afRotate);
	}

	protected static Shape gerarCorpo() {
		AffineTransform afRotate = new AffineTransform();
		Shape corpo = desenhaCorpo(pontoAvatar);
		double angulo = GeoUtil.calculaAngulo(pontoAvatar, pontoMouse, 90);
		double rad = Math.toRadians((double) angulo);
		GeneralPath gpCorpo = new GeneralPath(corpo);
		afRotate.setToRotation(rad, gpCorpo.getBounds().getCenterX(), gpCorpo
				.getBounds().getCenterY());
		return gpCorpo.createTransformedShape(afRotate);
	}

	protected static BufferedImage processaTransparencia(BufferedImage imgJog,
			Point desenha, Rectangle areaAvatar, MapaTopWar mapaTopWar) {
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		for (Iterator iterator = objetoMapaList.iterator(); iterator.hasNext();) {
			ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
			if ((objetoMapa.getTransparencia() == 0)
					&& objetoMapa.getForma().intersects(areaAvatar)) {
				BufferedImage novaImg = new BufferedImage(imgJog.getWidth(),
						imgJog.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = novaImg.createGraphics();
				g2d.drawImage(imgJog, 0, 0, null);
				Rectangle bounds = objetoMapa.getForma().getBounds();
				AlphaComposite composite = AlphaComposite.getInstance(
						AlphaComposite.CLEAR, 1);
				g2d.setComposite(composite);
				AffineTransform affineTransform = AffineTransform
						.getScaleInstance(1, 1);
				GeneralPath generalPath = new GeneralPath(objetoMapa.getForma());
				affineTransform.setToTranslation(
						-(bounds.x - (bounds.x - desenha.x)),
						-(bounds.y - (bounds.y - desenha.y)));
				Shape createTransformedShape = generalPath
						.createTransformedShape(affineTransform);
				g2d.fill(createTransformedShape);
				g2d.dispose();
				return novaImg;
			}
		}
		return imgJog;
	}

	protected static BufferedImage processaGrade(BufferedImage imgJog,
			Point desenha, Rectangle areaAvatar, MapaTopWar mapaTopWar) {
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		for (Iterator iterator = objetoMapaList.iterator(); iterator.hasNext();) {
			ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
			if ((ConstantesTopWar.GRADE.equals(objetoMapa.getEfeito()))
					&& objetoMapa.getForma().intersects(areaAvatar)) {
				BufferedImage novaImg = new BufferedImage(imgJog.getWidth(),
						imgJog.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = novaImg.createGraphics();
				g2d.drawImage(imgJog, 0, 0, null);
				Rectangle bounds = objetoMapa.getForma().getBounds();

				BufferedImage bufferedImagePasso1 = new BufferedImage(
						bounds.width, bounds.height,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D graphics = (Graphics2D) bufferedImagePasso1
						.getGraphics();
				int inicioLinha = 0;
				int fimLinha = 0 + bounds.width;
				int inicioCol = 0;
				graphics.setColor(Color.CYAN);
				for (int i = 0; i < bounds.getHeight(); i++) {
					if (i % 2 == 0)
						graphics.drawLine(inicioLinha, inicioCol + i, fimLinha,
								inicioCol + i);
				}
				Shape forma = objetoMapa.getForma();
				AffineTransform affineTransform = AffineTransform
						.getScaleInstance(1, 1);
				GeneralPath generalPath = new GeneralPath(forma);
				generalPath.transform(affineTransform);
				affineTransform.setToTranslation(-bounds.x, -bounds.y);
				forma = generalPath.createTransformedShape(affineTransform);
				BufferedImage bufferedImagePasso2 = new BufferedImage(
						bounds.width, bounds.height,
						BufferedImage.TYPE_INT_ARGB);
				graphics = (Graphics2D) bufferedImagePasso2.getGraphics();
				graphics.setClip(forma);
				graphics.drawImage(bufferedImagePasso1, 0, 0, null);
				g2d.drawImage(bufferedImagePasso2, bounds.x - desenha.x,
						bounds.y - desenha.y, null);
				g2d.dispose();
				return ImageUtil.geraTransparencia(novaImg, Color.CYAN);
			}
		}
		return imgJog;
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

	public static void gerarMapaImagens(BufferedImage src) {
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

	public static void centralizarPontoDireto(Point pin) {
		final Point p = new Point((int) (pin.x)
				- (scrollPane.getViewport().getWidth() / 2), (int) (pin.y)
				- (scrollPane.getViewport().getHeight() / 2));
		if (p.x < 0) {
			p.x = 1;
		}
		double maxX = ((panel.getWidth()) - scrollPane.getViewport().getWidth());
		if (p.x > maxX) {
			p.x = Util.inte(maxX) - 1;
		}
		if (p.y < 0) {
			p.y = 1;
		}
		double maxY = ((panel.getHeight()) - (scrollPane.getViewport()
				.getHeight()));
		if (p.y > maxY) {
			p.y = Util.inte(maxY) - 1;
		}
		Point oldp = scrollPane.getViewport().getViewPosition();
		if (!oldp.equals(p)) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollPane.getViewport().setViewPosition(p);
				}
			});
		}
	}

	public static Shape limitesViewPort() {
		if (scrollPane == null) {
			return null;
		}
		Rectangle rectangle = scrollPane.getViewport().getBounds();
		rectangle.x = scrollPane.getViewport().getViewPosition().x;
		rectangle.y = scrollPane.getViewport().getViewPosition().y;
		return rectangle;
	}
}
