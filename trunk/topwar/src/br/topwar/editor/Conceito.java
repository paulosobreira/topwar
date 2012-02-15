package br.topwar.editor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
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
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
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

	protected boolean rodando = true;
	protected int velocidade = 3;
	final int CIMA = KeyEvent.VK_W;
	final int BAIXO = KeyEvent.VK_S;
	final int ESQUERDA = KeyEvent.VK_A;
	final int DIREIRA = KeyEvent.VK_D;
	private int anim;
	private int ocilaAlphaRecarregando = 255;
	private boolean ocilaAlphaRecarregandoSobe = false;
	private Thread atirando;
	protected Set pressed = new HashSet();
	public Map<String, BufferedImage> mapImgs = new HashMap<String, BufferedImage>();
	private long lastAnim;
	private String time;
	private MapaTopWar mapaTopWar;
	private JPanel panel;
	private JScrollPane scrollPane;
	private Point pontoMouse;
	private Point pontoAvatar;
	private Rectangle areaAvatar;
	private boolean desenhaObjetos;
	private Point origem;
	private int knifeTransp;
	private boolean knifeTranspMaisTransp = true;

	public final BufferedImage azul = CarregadorRecursos
			.carregaBufferedImageTransparecia("azul.png", Color.MAGENTA);
	public final BufferedImage vermelho = CarregadorRecursos
			.carregaBufferedImageTransparecia("vermelho.png", Color.MAGENTA);
	public final BufferedImage crosshair = CarregadorRecursos
			.carregaBufferedImageTransparecia("crosshair.png", null);

	public final BufferedImage knifeAtttack = CarregadorRecursos
			.carregaBufferedImageTransparecia("knifeAtttack.png", null);
	public BufferedImage[] knifeAtttacks = new BufferedImage[255];

	public static void main(String[] args) throws Exception {
		Conceito conceito = new Conceito();
		conceito.incializa("mapa9.topwar", ConstantesTopWar.TIME_AZUL);
	}

	public void incializa(String mapa, String time) throws IOException,
			ClassNotFoundException {
		for (int i = 0; i < knifeAtttacks.length; i++) {
			knifeAtttacks[i] = ImageUtil.gerarFade(knifeAtttack, 255 - i);
		}
		this.time = time;
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
				CarregadorRecursos.recursoComoStream(mapa));

		mapaTopWar = (MapaTopWar) ois.readObject();
		frame.setTitle(mapaTopWar.getNome());

		final BufferedImage img = CarregadorRecursos
				.carregaBackGround(mapaTopWar.getBackGround());
		origem = new Point(650, 600);
		pontoAvatar = new Point(650, 600);
		pontoMouse = new Point(0, 0);
		KeyAdapter keyAdapter = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				synchronized (pressed) {
					pressed.add(keyCode);
				}
				if (keyCode == KeyEvent.VK_ESCAPE) {
					desenhaObjetos = !desenhaObjetos;
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

	protected boolean verificaColisao(Point novoPonto) {
		return verificaColisao(novoPonto, areaAvatar, mapaTopWar);
	}

	protected static boolean verificaColisao(Point novoPonto,
			Rectangle areaAvatar, MapaTopWar mapaTopWar) {
		Shape novaArea = desenhaCorpo(novoPonto);
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

	private void geraPainel(final BufferedImage img, final Point p,
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
						imgJog = processaTransparencia(imgJog, desenha,
								areaAvatar, mapaTopWar);
						imgJog = processaGrade(imgJog, desenha, areaAvatar,
								mapaTopWar);

						/**
						 * Desenha Faca
						 */
						Point pFaca = GeoUtil.calculaPonto(angulo, 10, desenha);

						BufferedImage knife = knifeAtttacks[knifeTransp];
						AffineTransform afRotate = new AffineTransform();
						double rad = Math.toRadians((double) angulo - 60);
						afRotate.setToRotation(rad, knife.getWidth() / 2,
								knife.getHeight() / 2);
						AffineTransformOp opRotate = new AffineTransformOp(
								afRotate, AffineTransformOp.TYPE_BILINEAR);
						BufferedImage rotBuffer = new BufferedImage(
								knife.getWidth(), knife.getHeight(),
								BufferedImage.TYPE_INT_ARGB);
						opRotate.filter(knife, rotBuffer);
						if (knifeTransp > 200) {
							knifeTranspMaisTransp = false;
						}
						if (knifeTransp < 50) {
							knifeTranspMaisTransp = true;
						}
						if (knifeTranspMaisTransp) {
							knifeTransp += 30;
						} else {
							knifeTransp -= 30;
						}
						if (angulo > 90 && angulo < 300) {
							graphics2d.drawImage(imgJog, desenha.x, desenha.y,
									null);
							graphics2d.drawImage(rotBuffer, pFaca.x, pFaca.y,
									null);
						} else {
							graphics2d.drawImage(rotBuffer, pFaca.x, pFaca.y,
									null);
							graphics2d.drawImage(imgJog, desenha.x, desenha.y,
									null);
						}
						System.out.println(angulo);

						if (desenhaObjetos) {
							graphics2d.setColor(Color.MAGENTA);
							graphics2d.draw(areaAvatar);

							Point pFacaAv = GeoUtil.calculaPonto(angulo, 10,
									pontoAvatar);
							graphics2d.drawOval(pFacaAv.x - 10, pFacaAv.y - 10,
									20, 20);

							Point back = GeoUtil.calculaPonto(angulo + 180, 30,
									pontoAvatar);
							Ellipse2D ellipse2d = new Ellipse2D.Double(
									back.x - 20, back.y - 20, 40, 40);
							graphics2d.draw(ellipse2d);
							graphics2d.setColor(Color.WHITE);
							graphics2d.drawOval(pontoAvatar.x, pontoAvatar.y,
									10, 10);
							List<Point> line = GeoUtil.drawBresenhamLine(
									pontoAvatar, origem);
							boolean desenhaOr = true;
							for (Iterator iterator = line.iterator(); iterator
									.hasNext();) {
								Point point = (Point) iterator.next();
								graphics2d.drawOval(point.x, point.y, 2, 2);
								if (ellipse2d
										.intersects(new Rectangle2D.Double(
												point.x, point.y, 1, 1))) {
									desenhaOr = false;
									break;
								}
							}
							if (desenhaOr)
								graphics2d.drawOval(origem.x, origem.y, 10, 10);

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

				desenhaInfoJogo(graphics2d);
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
								if (Math.random() > .7) {
									graphics2d.setColor(Color.WHITE);
								} else {
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
							if (Math.random() > .7) {
								graphics2d.setColor(Color.WHITE);
							} else {
								graphics2d.setColor(Color.LIGHT_GRAY);
							}
							graphics2d.drawLine(pIni.x, pIni.y, pFim.x, pFim.y);
						}
					}

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
				if (mapaTopWar.getPontoTimeAzul() != null) {
					graphics2d.setColor(ConstantesTopWar.lightBlu);
					graphics2d.fillOval(mapaTopWar.getPontoTimeAzul().x - 20,
							mapaTopWar.getPontoTimeAzul().y - 20, 40, 40);
				}
				if (mapaTopWar.getPontoTimeVermelho() != null) {
					graphics2d.setColor(ConstantesTopWar.lightRed);
					graphics2d.fillOval(
							mapaTopWar.getPontoTimeVermelho().x - 20,
							mapaTopWar.getPontoTimeVermelho().y - 20, 40, 40);
				}

			};
		};
	}

	protected void desenhaInfoJogo(Graphics2D g2d) {
		desenhaInfoBaixo(g2d);
		desenhaInfoCima(g2d);

	}

	private void desenhaInfoCima(Graphics2D g2d) {
		Shape limitesViewPort = limitesViewPort();

		int x = limitesViewPort.getBounds().x + 300;
		int y = limitesViewPort.getBounds().y + 40;
		Font fontOri = g2d.getFont();
		g2d.setFont(new Font(fontOri.getName(), fontOri.getStyle(), 32));
		g2d.setColor(ConstantesTopWar.lightBlu);
		g2d.fillRoundRect(x - 10, y - 30,
				Util.calculaLarguraText("AZUL", g2d) + 20, 35, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString("AZUL", x, y);

		x += Util.calculaLarguraText("AZUL", g2d) + 30;

		g2d.setColor(ConstantesTopWar.lightBlu);
		g2d.fillRoundRect(x - 10, y - 30,
				Util.calculaLarguraText("00", g2d) + 20, 35, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString("00", x, y);

		x += Util.calculaLarguraText("00", g2d) + 30;

		g2d.setColor(ConstantesTopWar.lightWhite);

		String formatarTempo = Util.formatarTempo(System.currentTimeMillis());

		g2d.fillRoundRect(x - 10, y - 30,
				Util.calculaLarguraText(formatarTempo, g2d) + 20, 35, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(formatarTempo, x, y);

		x += Util.calculaLarguraText(formatarTempo, g2d) + 30;

		g2d.setColor(ConstantesTopWar.lightRed);
		g2d.fillRoundRect(x - 10, y - 30,
				Util.calculaLarguraText("00", g2d) + 20, 35, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString("00", x, y);

		x += Util.calculaLarguraText("00", g2d) + 30;

		g2d.setColor(ConstantesTopWar.lightRed);
		g2d.fillRoundRect(x - 10, y - 30,
				Util.calculaLarguraText("VERMELHO", g2d) + 20, 35, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString("VERMELHO", x, y);
	}

	private void desenhaInfoBaixo(Graphics2D g2d) {
		Shape limitesViewPort = limitesViewPort();
		int x = limitesViewPort.getBounds().x
				+ (limitesViewPort.getBounds().width - 500);
		int y = limitesViewPort.getBounds().y
				+ (limitesViewPort.getBounds().height - 10);
		Font fontOri = g2d.getFont();
		g2d.setFont(new Font(fontOri.getName(), fontOri.getStyle(), 32));
		g2d.setColor(ConstantesTopWar.lightWhite);
		g2d.fillRoundRect(x - 10, y - 30,
				Util.calculaLarguraText("ASSAULT", g2d) + 20, 35, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString("ASSAULT", x, y);

		x += 180;

		if (false) {
			g2d.setColor(ConstantesTopWar.lightWhite);
			g2d.fillRoundRect(x - 10, y - 30,
					Util.calculaLarguraText("50", g2d) + 20, 35, 10, 10);
			g2d.setColor(Color.BLACK);
			g2d.drawString("50", x, y);
			x += 80;
			g2d.setColor(ConstantesTopWar.lightWhite);
			g2d.fillRoundRect(x - 10, y - 30,
					Util.calculaLarguraText("3", g2d) + 20, 35, 10, 10);
			g2d.setColor(Color.BLACK);
			g2d.drawString("3 ", x, y);
		} else {
			g2d.setColor(new Color(255, 255, 255, ocilaAlphaRecarregando));
			g2d.fillRoundRect(x - 10, y - 30,
					Util.calculaLarguraText("RECARREGANDO", g2d) + 20, 35, 10,
					10);
			g2d.setColor(Color.BLACK);
			g2d.drawString("RECARREGANDO", x, y);

			if (ocilaAlphaRecarregandoSobe) {
				ocilaAlphaRecarregando += 10;
			} else {
				ocilaAlphaRecarregando -= 10;
			}
			if (ocilaAlphaRecarregando < 50) {
				ocilaAlphaRecarregandoSobe = true;
			}
			if (ocilaAlphaRecarregando > 200) {
				ocilaAlphaRecarregandoSobe = false;
			}
		}
		g2d.setFont(fontOri);
	}

	protected Shape gerarCabeca() {
		AffineTransform afRotate = new AffineTransform();
		double angulo = GeoUtil.calculaAngulo(pontoAvatar, pontoMouse, 90);
		double rad = Math.toRadians((double) angulo);
		// Shape cabeca = desenhaCabeca(GeoUtil.calculaPonto(angulo, 6,
		// pontoAvatar));
		Shape cabeca = desenhaCabeca(pontoAvatar);
		GeneralPath gpCabeca = new GeneralPath(cabeca);
		afRotate.setToRotation(rad, gpCabeca.getBounds().getCenterX(), gpCabeca
				.getBounds().getCenterY());
		return gpCabeca.createTransformedShape(afRotate);
	}

	protected Shape gerarCorpo() {
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

	private void animar() {
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

	public void gerarMapaImagens(BufferedImage src) {
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
				mapImgs.put(key, ImageUtil.gerarFade(bufferedImage, 250));
				// JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(
				// mapImgs.get(key))), key,
				// JOptionPane.INFORMATION_MESSAGE);
			}
		}

		JOptionPane.showMessageDialog(null,
				new JLabel(new ImageIcon(ImageUtil.gerarFade(bf, 150))), "bf",
				JOptionPane.INFORMATION_MESSAGE);

	}

	protected Shape desenhaCabeca(Point p) {
		return new Rectangle2D.Double(p.x - 2, p.y - 8, 3, 3);
	}

	protected static Shape desenhaCorpo(Point p) {
		return new Rectangle2D.Double(p.x - 8, p.y, 18, 18);
	}

	public void centralizarPontoDireto(Point pin) {
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

	public Shape limitesViewPort() {
		if (scrollPane == null) {
			return null;
		}
		Rectangle rectangle = scrollPane.getViewport().getBounds();
		rectangle.x = scrollPane.getViewport().getViewPosition().x;
		rectangle.y = scrollPane.getViewport().getViewPosition().y;
		return rectangle;
	}
}
