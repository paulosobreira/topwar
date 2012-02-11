package br.topwar.cliente;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.nnpe.GeoUtil;
import br.nnpe.ImageUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.topwar.ConstantesTopWar;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.serial.MapaTopWar;
import br.topwar.serial.ObjetoMapa;

public class PainelTopWar {
	private JogoCliente jogoCliente;
	private JPanel panel;
	private JScrollPane scrollPane;
	private MapaTopWar mapaTopWar;
	private boolean desenhaObjetos = false;
	private int ocilaAlphaRecarregando = 255;
	private boolean ocilaAlphaRecarregandoSobe = false;
	public final BufferedImage crosshair = CarregadorRecursos
			.carregaBufferedImageTransparecia("crosshair.png", null);
	public Map<String, BufferedImage> mapImgs = new HashMap<String, BufferedImage>();
	public final static BufferedImage azul = CarregadorRecursos
			.carregaBufferedImageTransparecia("azul.png", Color.MAGENTA);
	public final static BufferedImage vermelho = CarregadorRecursos
			.carregaBufferedImageTransparecia("vermelho.png", Color.MAGENTA);
	public final static Color lightWhite = new Color(255, 255, 255, 200);

	public PainelTopWar(JogoCliente jogoCliente) {
		this.jogoCliente = jogoCliente;
		mapaTopWar = jogoCliente.getMapaTopWar();
		gerarMapaImagens(azul, "azul");
		gerarMapaImagens(vermelho, "vermelho");
		geraPainel();
	}

	public void gerarMapaImagens(BufferedImage src, String time) {
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

		// JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(bf)),
		// "bf", JOptionPane.INFORMATION_MESSAGE);
	}

	public JPanel getPanel() {
		return panel;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	private void geraPainel() {
		final BufferedImage img = CarregadorRecursos
				.carregaBackGround(mapaTopWar.getBackGround());
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
				setarHints(graphics2d);
				graphics2d.drawImage(img, null, 0, 0);
				loopDesenhaAvatares(graphics2d);
				desenhaInfoJogo(graphics2d);
				desenhaMira(graphics2d);
				desenhaObjetosDebug(graphics2d);
			}

		};
		scrollPane = new JScrollPane(panel,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getViewport().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
			}
		});

	}

	private void desenhaObjetosDebug(Graphics2D graphics2d) {
		if (desenhaObjetos) {
			List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
			for (Iterator iterator = objetoMapaList.iterator(); iterator
					.hasNext();) {
				ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
				graphics2d.setColor(new Color(0, 255, 0, objetoMapa
						.getTransparencia()));
				graphics2d.draw(objetoMapa.getForma());
			}
		}
	}

	private void desenhaMira(Graphics2D graphics2d) {
		Point pontoMouse = jogoCliente.getPontoMouseMovendo();
		if (pontoMouse != null) {
			Point desenha = new Point(
					pontoMouse.x - (crosshair.getWidth() / 2), pontoMouse.y
							- (crosshair.getHeight() / 2));
			graphics2d.drawImage(crosshair, desenha.x, desenha.y, null);
		}
	}

	private void loopDesenhaAvatares(Graphics2D graphics2d) {
		synchronized (jogoCliente.getAvatarClientes()) {
			List<AvatarCliente> avatarClientes = jogoCliente
					.getAvatarClientes();
			for (Iterator iterator = avatarClientes.iterator(); iterator
					.hasNext();) {
				AvatarCliente avatarCliente = (AvatarCliente) iterator.next();
				desenhaAvatares(graphics2d, avatarCliente);
				long millisSrv = jogoCliente.getMillisSrv();
				long tempoUtlDisparo = avatarCliente.getTempoUtlDisparo();
				if ((millisSrv - tempoUtlDisparo) < 150) {
					desenhaDisparoAvatar(graphics2d, avatarCliente);
				}

			}
			Logger.logar("avatarClientes.size " + avatarClientes.size());
		}
	}

	protected void desenhaInfoJogo(Graphics2D g2d) {
		Shape limitesViewPort = limitesViewPort();
		int x = limitesViewPort.getBounds().x
				+ (limitesViewPort.getBounds().width - 500);
		int y = limitesViewPort.getBounds().y
				+ +(limitesViewPort.getBounds().height - 10);
		Font fontOri = g2d.getFont();
		g2d.setFont(new Font(fontOri.getName(), fontOri.getStyle(), 32));
		g2d.setColor(lightWhite);
		g2d.fillRoundRect(x - 10, y - 30,
				Util.calculaLarguraText("ASSAULT", g2d) + 20, 35, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString("ASSAULT", x, y);

		x += 180;

		if (jogoCliente.verificaRecarregando()) {
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
		} else {
			g2d.setColor(lightWhite);
			g2d.fillRoundRect(x - 10, y - 30,
					Util.calculaLarguraText("88", g2d) + 20, 35, 10, 10);
			g2d.setColor(Color.BLACK);
			g2d.drawString("" + jogoCliente.getBalas(), x, y);
			x += 80;
			g2d.setColor(lightWhite);
			g2d.fillRoundRect(x - 10, y - 30,
					Util.calculaLarguraText("8", g2d) + 20, 35, 10, 10);
			g2d.setColor(Color.BLACK);
			g2d.drawString("" + jogoCliente.getCartuchos(), x, y);
			g2d.setFont(fontOri);

		}
	}

	protected void desenhaDisparoAvatar(Graphics2D graphics2d,
			AvatarCliente avatarCliente) {
		Point pontoAvatar = avatarCliente.getPontoAvatar();
		Point pontoTiro = GeoUtil.calculaPonto(avatarCliente.getAngulo(),
				ConstantesTopWar.ASSALT_MAX_RANGE, pontoAvatar);
		for (int i = 0; i < 5; i++) {
			Point nOri = new Point(pontoAvatar.x, pontoAvatar.y);
			Point nDst = new Point(pontoTiro.x + Util.intervalo(-15, 15),
					pontoTiro.y + Util.intervalo(-15, 15));
			graphics2d.setColor(Color.YELLOW);
			List<Point> linha = GeoUtil.drawBresenhamLine(nOri, nDst);
			if (linha.size() > 40) {
				int intIni = Util.intervalo(10, 20);
				Point pIni = linha.get(intIni);
				Point pFim = linha.get(intIni + Util.intervalo(1, 20));
				graphics2d.drawLine(pIni.x, pIni.y, pFim.x, pFim.y);
			}
		}
		List<Point> linhaDisparo = GeoUtil.drawBresenhamLine(pontoAvatar,
				pontoTiro);
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		List<AvatarCliente> avatarClientes = jogoCliente.getAvatarClientes();
		AvatarCliente avatarClienteBateu = null;
		for (int i = 0; i < linhaDisparo.size(); i++) {
			Point tiro = linhaDisparo.get(i);
			boolean bateu = false;
			for (Iterator iterator = avatarClientes.iterator(); iterator
					.hasNext();) {
				AvatarCliente avatarClienteAnalizar = (AvatarCliente) iterator
						.next();
				if (!avatarCliente.equals(avatarClienteAnalizar)
						&& avatarClienteAnalizar.gerarCorpo().contains(tiro)) {
					bateu = true;
					avatarClienteBateu = avatarClienteAnalizar;
					break;
				}
			}
			if (!bateu) {
				for (Iterator iterator = objetoMapaList.iterator(); iterator
						.hasNext();) {
					ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
					if (objetoMapa.getTransparencia() > 11
							&& objetoMapa.getForma().contains(tiro)) {
						bateu = true;
						break;
					}
				}
			}
			/**
			 * Bala Acerta
			 */
			if (bateu) {
				int noAnt = i - 41;
				while (noAnt < 0) {
					noAnt++;
				}
				Point ptAcertoAnt = linhaDisparo.get(noAnt);
				Point nOri = tiro;
				for (int j = 0; j < 5; j++) {
					Point nDst = new Point(ptAcertoAnt.x
							+ Util.intervalo(-10, 10), ptAcertoAnt.y
							+ Util.intervalo(-10, 10));
					if (Math.random() > 0.5) {
						graphics2d.setColor(Color.YELLOW);
					} else {
						graphics2d.setColor(Color.WHITE);
					}
					List<Point> linha = GeoUtil.drawBresenhamLine(nOri, nDst);
					if (linha.size() > 40) {
						int intIni = Util.intervalo(5, 10);
						Point pIni = linha.get(intIni);
						Point pFim = linha.get(intIni + Util.intervalo(1, 20));
						graphics2d.drawLine(pIni.x, pIni.y, pFim.x, pFim.y);
					}
				}

			}
			/**
			 * Sangue Jogador
			 */
			if (avatarClienteBateu != null) {
				int noPost = i + 50;
				while (noPost > linhaDisparo.size()) {
					noPost--;
				}
				Point nDst = linhaDisparo.get(noPost);
				for (int j = 0; j < 5; j++) {
					Point nOri = new Point(tiro.x + Util.intervalo(-10, 10),
							tiro.y + Util.intervalo(-10, 10));
					graphics2d.setColor(Color.RED);
					List<Point> linha = GeoUtil.drawBresenhamLine(nOri, nDst);
					if (linha.size() > 40) {
						int intIni = Util.intervalo(10, 20);
						Point pIni = linha.get(intIni + Util.intervalo(1, 20));
						Point pFim = linha.get(intIni);
						graphics2d.drawLine(pIni.x, pIni.y, pFim.x, pFim.y);
					}
				}

			}
			if (bateu) {
				break;
			}
		}
	}

	protected void desenhaAvatares(Graphics2D graphics2d,
			AvatarCliente avatarCliente) {
		Point pontoAvatar = avatarCliente.getPontoAvatarSuave();
		if (pontoAvatar == null) {
			pontoAvatar = avatarCliente.getPontoAvatar();
		}
		int anim = avatarCliente.getQuadroAnimacao();
		int velocidade = avatarCliente.getVelocidade();
		String time = avatarCliente.getTime();
		double angulo = avatarCliente.getAngulo();
		if (angulo < 0) {
			angulo = 360 + angulo;
		}
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
				Logger.logar("Angulo nulo " + angulo);
			} else {
				Point desenha = new Point(pontoAvatar.x
						- (imgJog.getWidth() / 2), pontoAvatar.y
						- (imgJog.getHeight() / 3));
				Rectangle areaAvatar = new Rectangle(desenha.x, desenha.y,
						imgJog.getWidth(), imgJog.getHeight());
				imgJog = processaSobreposicoesAvatar(imgJog, desenha,
						areaAvatar, mapaTopWar);
				imgJog = processaGrade(imgJog, desenha, areaAvatar, mapaTopWar);
				/**
				 * Avatar Fade
				 */
				if (jogoCliente.getPontoAvatar() != null
						&& !avatarCliente.isLocal()) {
					List<Point> line = GeoUtil.drawBresenhamLine(
							jogoCliente.getPontoAvatar(), pontoAvatar);
					if (line.size() > 200) {
						int transp = (510 - (line.size() - 200)) / 2;
						if (transp > 255) {
							transp = 255;
						}
						if (transp < 0) {
							transp = 0;
						}
						imgJog = ImageUtil.gerarFade(imgJog, transp);
					}

				}
				graphics2d.drawImage(imgJog, desenha.x, desenha.y, null);
				graphics2d.setColor(new Color(128, 128, 128, 100));
				graphics2d.fillRoundRect(desenha.x - 20, desenha.y - 20, 100,
						20, 5, 5);
				graphics2d.setColor(new Color(0, 255, 0, 100));
				graphics2d.fillRoundRect(desenha.x - 20, desenha.y - 20,
						avatarCliente.getVida(), 20, 5, 5);
				graphics2d.setColor(Color.WHITE);
				graphics2d.drawString("" + avatarCliente.getNomeJogador() + " "
						+ avatarCliente.getVida(), desenha.x,
						pontoAvatar.y - 20);
			}
		}
		if (!avatarCliente.getPontoAvatar().equals(
				avatarCliente.getPontoAvatarOld())) {
			avatarCliente.animar();
		}
		if (desenhaObjetos) {
			graphics2d.setColor(Color.GREEN);
			Rectangle limitesViewPort = (Rectangle) limitesViewPort();
			graphics2d.drawString("Angulo " + angulo, limitesViewPort.x + 10,
					limitesViewPort.y + 10);
			graphics2d.drawString("Amin " + anim, limitesViewPort.x + 10,
					limitesViewPort.y + 25);
			graphics2d.drawString("Velocidade " + velocidade,
					limitesViewPort.x + 10, limitesViewPort.y + 40);
			graphics2d.setColor(Color.CYAN);
			graphics2d.draw(avatarCliente.gerarCorpo());
			graphics2d.setColor(Color.RED);
			graphics2d.draw(avatarCliente.gerarCabeca());
			graphics2d.setColor(Color.BLUE);
			Point pontoAvatarLocal = jogoCliente.getPontoAvatar();
			Point pontoMouseClicado = jogoCliente.getPontoMouseClicado();
			if (pontoMouseClicado != null && pontoAvatarLocal != null) {
				graphics2d.drawLine(pontoAvatarLocal.x, pontoAvatarLocal.y,
						pontoMouseClicado.x, pontoMouseClicado.y);
			}

			double anguloJog = angulo;

			double angMinJogador = anguloJog - 120;
			if (angMinJogador < 0) {
				angMinJogador += 360;
			}

			double angMaxJogador = anguloJog + 120;
			if (angMinJogador > 360) {
				angMinJogador -= 360;
			}

			Point ptMin = GeoUtil.calculaPonto(angMinJogador, 500, pontoAvatar);
			graphics2d.setColor(Color.BLACK);
			graphics2d.drawLine(pontoAvatar.x, pontoAvatar.y, ptMin.x, ptMin.y);
			Point ptMax = GeoUtil.calculaPonto(angMaxJogador, 500, pontoAvatar);
			graphics2d.setColor(Color.WHITE);
			graphics2d.drawLine(pontoAvatar.x, pontoAvatar.y, ptMax.x, ptMax.y);
		}

	}

	public void atualiza() {
		List<AvatarCliente> avatarClientes = jogoCliente.getAvatarClientes();
		for (Iterator iterator = avatarClientes.iterator(); iterator.hasNext();) {
			AvatarCliente avatarCliente = (AvatarCliente) iterator.next();
			if (avatarCliente.isLocal()) {
				Point pontoAv = avatarCliente.getPontoAvatarSuave();
				if (pontoAv == null) {
					pontoAv = avatarCliente.getPontoAvatar();
				}
				centralizarPontoDireto(pontoAv);

			}
		}
		if (panel != null) {
			panel.repaint();
		}
	}

	private void setarHints(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
				RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

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

	protected BufferedImage processaSobreposicoesAvatar(BufferedImage imgJog,
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

	protected BufferedImage processaGrade(BufferedImage imgJog, Point desenha,
			Rectangle areaAvatar, MapaTopWar mapaTopWar) {
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

}
