package br.topwar.cliente;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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
	private boolean desenhaObjetos = true;
	public Map<String, BufferedImage> mapImgs = new HashMap<String, BufferedImage>();
	public final static BufferedImage azul = CarregadorRecursos
			.carregaBufferedImageTransparecia("azul.png", Color.MAGENTA);
	public final static BufferedImage vermelho = CarregadorRecursos
			.carregaBufferedImageTransparecia("vermelho.png", Color.MAGENTA);

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
				graphics2d.drawImage(img, null, 0, 0);
				synchronized (jogoCliente.getAvatarClientes()) {
					List<AvatarCliente> avatarClientes = jogoCliente
							.getAvatarClientes();
					for (Iterator iterator = avatarClientes.iterator(); iterator
							.hasNext();) {
						AvatarCliente avatarCliente = (AvatarCliente) iterator
								.next();
						desenhaAvatares(graphics2d, avatarCliente);
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
			};
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

	protected void desenhaAvatares(Graphics2D graphics2d,
			AvatarCliente avatarCliente) {
		Point pontoAvatar = avatarCliente.getPontoAvatar();
		int anim = avatarCliente.getQuadroAnimacao();
		int velocidade = avatarCliente.getVelocidade();
		String time = avatarCliente.getTime();
		double angulo = avatarCliente.getAngulo();
		if (angulo < 0) {
			angulo = 360 + angulo;
		}
		graphics2d.setColor(Color.MAGENTA);
		Rectangle limitesViewPort = (Rectangle) limitesViewPort();
		graphics2d.drawString("Angulo " + angulo, limitesViewPort.x + 10,
				limitesViewPort.y + 10);
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
				Point desenha = new Point(pontoAvatar.x
						- (imgJog.getWidth() / 2), pontoAvatar.y
						- (imgJog.getHeight() / 3));
				Rectangle areaAvatar = new Rectangle(desenha.x, desenha.y,
						imgJog.getWidth(), imgJog.getHeight());
				imgJog = processaTransparencia(imgJog, desenha, areaAvatar,
						mapaTopWar);
				imgJog = processaGrade(imgJog, desenha, areaAvatar, mapaTopWar);
				graphics2d.drawImage(imgJog, desenha.x, desenha.y, null);
				if (desenhaObjetos) {
					graphics2d.setColor(Color.MAGENTA);
					graphics2d.draw(avatarCliente.obeterAreaAvatar());
				}
			}
		}
		if (!avatarCliente.getPontoAvatar().equals(
				avatarCliente.getPontoAvatarOld())){
			avatarCliente.animar();
		}
		if (desenhaObjetos) {
			graphics2d.setColor(Color.CYAN);
			graphics2d.draw(avatarCliente.gerarCorpo());
			graphics2d.setColor(Color.RED);
			graphics2d.draw(avatarCliente.gerarCabeca());
		}

	}

	public void atualiza() {
		List<AvatarCliente> avatarClientes = jogoCliente.getAvatarClientes();
		for (Iterator iterator = avatarClientes.iterator(); iterator.hasNext();) {
			AvatarCliente avatarCliente = (AvatarCliente) iterator.next();
			if (avatarCliente.isLocal())
				centralizarPontoDireto(avatarCliente.getPontoAvatar());
		}
		if (panel != null) {
			panel.repaint();
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

	protected BufferedImage processaTransparencia(BufferedImage imgJog,
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
