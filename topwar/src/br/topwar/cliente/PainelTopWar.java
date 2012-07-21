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
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.nnpe.GeoUtil;
import br.nnpe.ImageUtil;
import br.nnpe.Logger;
import br.nnpe.OcilaCor;
import br.nnpe.Util;
import br.topwar.ConstantesTopWar;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.recursos.idiomas.Lang;
import br.topwar.serial.MapaTopWar;
import br.topwar.serial.ObjetoMapa;
import br.topwar.tos.EventoJogo;
import br.topwar.tos.PlacarTopWar;

public class PainelTopWar {
	private static final int FADE_MINIS = 100;
	private JogoCliente jogoCliente;
	private JPanel panel;
	private JScrollPane scrollPane;
	private MapaTopWar mapaTopWar;
	private boolean desenhaObjetos = false;
	private boolean desenhaImagens = true;
	private Hashtable<Point, Integer> mapaExplosoes = new Hashtable<Point, Integer>();
	private int tabCont = 0;
	private boolean gerouImagens;
	private AvatarCliente avatarLocal;

	private BufferedImage miniAssalt;
	private BufferedImage miniKnife;
	private BufferedImage miniHeadShot;
	private BufferedImage miniMachineGun;
	private BufferedImage miniSniper;
	private BufferedImage miniShotgun;
	private BufferedImage miniRocket;
	private BufferedImage miniShield;
	private BufferedImage lifeBarShield;
	private BufferedImage lifeBarAssalt;
	private BufferedImage lifeBarMachineGun;
	private BufferedImage lifeBarKnife;
	private BufferedImage lifeBarShotgun;
	private BufferedImage lifeBarSniper;
	private BufferedImage lifeBarRocket;

	public Map<String, BufferedImage> mapImgs = new HashMap<String, BufferedImage>();

	public BufferedImage shield;
	public BufferedImage crosshair;
	public BufferedImage vaiAqui;
	public BufferedImage blueFlag;
	public BufferedImage redFlag;
	public BufferedImage assault;
	public BufferedImage shotgun;
	public BufferedImage machinegun;
	public BufferedImage sniper;
	public BufferedImage headShot;
	public BufferedImage rocket;
	public BufferedImage rocket_launcher;
	public BufferedImage knife;
	public BufferedImage azul;
	public BufferedImage azul_sniper;
	public BufferedImage azul_shotgun;
	public BufferedImage azul_machine;
	public BufferedImage azul_rocket;
	public BufferedImage azul_faca;
	public BufferedImage vermelho;
	public BufferedImage vermelho_faca;
	public BufferedImage vermelho_shotgun;
	public BufferedImage vermelho_machine;
	public BufferedImage vermelho_rocket;
	public BufferedImage vermelho_sniper;
	public BufferedImage knifeAtttack;
	public BufferedImage azulMortes;
	public BufferedImage vermelhoMortes;
	public BufferedImage explosao;
	private BufferedImage riot_shield;

	private RoundRectangle2D assautRect;
	private RoundRectangle2D shotgunRect;
	private RoundRectangle2D machineRect;
	private RoundRectangle2D shieldRect;
	private RoundRectangle2D rocketRect;
	private RoundRectangle2D sniperRect;

	public PainelTopWar(JogoCliente jogoCliente) {
		this.jogoCliente = jogoCliente;
		mapaTopWar = jogoCliente.getMapaTopWar();
		carregaImagens();
		gerarMapaImagensExplosao();
		gerarMapaImagens(azul, "azul");
		gerarMapaImagens(vermelho, "vermelho");
		gerarMapaImagens(azul_faca, "azul_faca");
		gerarMapaImagens(azul_faca, "azul_shield");
		gerarMapaImagens(azul_sniper, "azul_sniper");
		gerarMapaImagens(azul_shotgun, "azul_shotgun");
		gerarMapaImagens(azul_machine, "azul_machine");
		gerarMapaImagens(azul_rocket, "azul_rocket");
		gerarMapaImagens(vermelho_faca, "vermelho_faca");
		gerarMapaImagens(vermelho_faca, "vermelho_shield");
		gerarMapaImagens(vermelho_sniper, "vermelho_sniper");
		gerarMapaImagens(vermelho_shotgun, "vermelho_shotgun");
		gerarMapaImagens(vermelho_machine, "vermelho_machine");
		gerarMapaImagens(vermelho_rocket, "vermelho_rocket");
		gerarMapaImagensMortes(azulMortes, "azul");
		gerarMapaImagensMortes(vermelhoMortes, "vermelho");
		gerouImagens = true;
		geraPainel();
		gerarMinis();
		gerarMudaClasseBtns();
	}

	private void gerarMudaClasseBtns() {
		assautRect = new RoundRectangle2D.Double(0, 0,
				lifeBarAssalt.getWidth(), lifeBarAssalt.getHeight(), 10, 10);
		shotgunRect = new RoundRectangle2D.Double(0, 0,
				lifeBarShotgun.getWidth(), lifeBarShotgun.getHeight(), 10, 10);
		machineRect = new RoundRectangle2D.Double(0, 0,
				lifeBarMachineGun.getWidth(), lifeBarMachineGun.getHeight(),
				10, 10);
		shieldRect = new RoundRectangle2D.Double(0, 0,
				lifeBarMachineGun.getWidth(), lifeBarMachineGun.getHeight(),
				10, 10);
		rocketRect = new RoundRectangle2D.Double(0, 0,
				lifeBarRocket.getWidth(), lifeBarRocket.getHeight(), 10, 10);
		sniperRect = new RoundRectangle2D.Double(0, 0,
				lifeBarSniper.getWidth(), lifeBarSniper.getHeight(), 10, 10);
		shieldRect = new RoundRectangle2D.Double(0, 0,
				lifeBarShield.getWidth(), lifeBarShield.getHeight(), 10, 10);

	}

	public boolean verificaComandoMudarClasse(Point p) {
		if (assautRect.contains(p)) {
			jogoCliente.mudarClasse(ConstantesTopWar.ASSAULT);
			return true;
		}
		if (rocketRect.contains(p)) {
			jogoCliente.mudarClasse(ConstantesTopWar.ROCKET);
			return true;
		}
		if (machineRect.contains(p)) {
			jogoCliente.mudarClasse(ConstantesTopWar.MACHINEGUN);
			return true;
		}
		if (sniperRect.contains(p)) {
			jogoCliente.mudarClasse(ConstantesTopWar.SNIPER);
			return true;
		}
		if (shotgunRect.contains(p)) {
			jogoCliente.mudarClasse(ConstantesTopWar.SHOTGUN);
			return true;
		}
		if (shieldRect.contains(p)) {
			jogoCliente.mudarClasse(ConstantesTopWar.SHIELD);
			return true;
		}
		return false;
	}

	private void desenhaControleMudarClasse(Graphics2D graphics2d) {
		Rectangle limitesViewPort = (Rectangle) limitesViewPort();
		Point o = new Point(limitesViewPort.x + 10, limitesViewPort.y
				+ limitesViewPort.height - 60);
		int x = o.x;
		int y = o.y;

		assautRect.setFrame(x, y, lifeBarAssalt.getWidth(),
				lifeBarAssalt.getHeight());
		if (ConstantesTopWar.ASSAULT.equals(jogoCliente.getProxClasse())) {
			graphics2d.setColor(Color.YELLOW);
		} else {
			graphics2d.setColor(Color.BLACK);
		}
		graphics2d.draw(assautRect);
		graphics2d.drawImage(lifeBarAssalt, null, x, y);

		x += 110;

		sniperRect.setFrame(x, y, lifeBarSniper.getWidth(),
				lifeBarSniper.getHeight());
		if (ConstantesTopWar.SNIPER.equals(jogoCliente.getProxClasse())) {
			graphics2d.setColor(Color.YELLOW);
		} else {
			graphics2d.setColor(Color.BLACK);
		}
		graphics2d.draw(sniperRect);
		graphics2d.drawImage(lifeBarSniper, null, x, y);

		x += 110;

		machineRect.setFrame(x, y, lifeBarMachineGun.getWidth(),
				lifeBarMachineGun.getHeight());
		if (ConstantesTopWar.MACHINEGUN.equals(jogoCliente.getProxClasse())) {
			graphics2d.setColor(Color.YELLOW);
		} else {
			graphics2d.setColor(Color.BLACK);
		}
		graphics2d.draw(machineRect);
		graphics2d.drawImage(lifeBarMachineGun, null, x, y);

		x = o.x;
		y += 30;
		shotgunRect.setFrame(x, y, lifeBarShotgun.getWidth(),
				lifeBarShotgun.getHeight());
		if (ConstantesTopWar.SHOTGUN.equals(jogoCliente.getProxClasse())) {
			graphics2d.setColor(Color.YELLOW);
		} else {
			graphics2d.setColor(Color.BLACK);
		}
		graphics2d.draw(shotgunRect);
		graphics2d.drawImage(lifeBarShotgun, null, x, y);

		x += 110;
		rocketRect.setFrame(x, y, lifeBarRocket.getWidth(),
				lifeBarRocket.getHeight());
		if (ConstantesTopWar.ROCKET.equals(jogoCliente.getProxClasse())) {
			graphics2d.setColor(Color.YELLOW);
		} else {
			graphics2d.setColor(Color.BLACK);
		}
		graphics2d.draw(rocketRect);
		graphics2d.drawImage(lifeBarRocket, null, x, y);

		x += 110;
		shieldRect.setFrame(x, y, lifeBarShield.getWidth(),
				lifeBarShield.getHeight());
		if (ConstantesTopWar.SHIELD.equals(jogoCliente.getProxClasse())) {
			graphics2d.setColor(Color.YELLOW);
		} else {
			graphics2d.setColor(Color.BLACK);
		}
		graphics2d.draw(shieldRect);
		graphics2d.drawImage(lifeBarShield, null, x, y);

		int centerXAssaut = (int) assautRect.getCenterX();

		int centerXMachine = (int) machineRect.getCenterX();

		y = o.y;

		graphics2d.setColor(Color.BLACK);
		graphics2d.drawLine(centerXAssaut, y, centerXAssaut, y - 20);
		graphics2d.drawLine(centerXMachine, y, centerXMachine, y - 20);

		graphics2d.drawLine(centerXAssaut, y - 20, centerXAssaut + 20, y - 20);
		graphics2d
				.drawLine(centerXMachine, y - 20, centerXMachine - 20, y - 20);
		graphics2d.setColor(ConstantesTopWar.lightWhite);

		graphics2d.fillRoundRect(centerXAssaut + 20, y - 27, 180, 15, 10, 10);
		graphics2d.setColor(Color.BLACK);
		graphics2d.drawString(Lang.msg("escolherProxClasse"),
				centerXAssaut + 30, y - 15);
	}

	private void gerarMinis() {
		miniAssalt = ImageUtil.geraResize(assault, 0.5);
		lifeBarAssalt = ImageUtil.gerarFade(
				ImageUtil.geraResize(assault, 0.55, 0.4), FADE_MINIS);
		miniKnife = ImageUtil.geraResize(knife, 0.5);

		lifeBarKnife = ImageUtil.gerarFade(
				ImageUtil.geraResize(knife, 0.85, 0.75), FADE_MINIS);

		miniHeadShot = ImageUtil.geraResize(headShot, 0.5);
		miniMachineGun = ImageUtil.geraResize(machinegun, 0.5);
		lifeBarMachineGun = ImageUtil.gerarFade(
				ImageUtil.geraResize(machinegun, 0.45, 0.27), FADE_MINIS);

		miniShotgun = ImageUtil.geraResize(shotgun, 0.5);
		lifeBarShotgun = ImageUtil.gerarFade(
				ImageUtil.geraResize(shotgun, 0.6, 0.5), FADE_MINIS);

		miniSniper = ImageUtil.geraResize(sniper, 0.5);
		lifeBarSniper = ImageUtil.gerarFade(
				ImageUtil.geraResize(sniper, 0.50, 0.4), FADE_MINIS);

		miniRocket = ImageUtil.geraResize(rocket_launcher, 0.5);
		lifeBarRocket = ImageUtil.gerarFade(
				ImageUtil.geraResize(rocket_launcher, 0.42, 0.30), FADE_MINIS);

		miniShield = ImageUtil.geraResize(riot_shield, 0.5);
		lifeBarShield = ImageUtil.gerarFade(
				ImageUtil.geraResize(riot_shield, 0.75, 0.25), 255);

	}

	private void carregaImagens() {
		shield = CarregadorRecursos.carregaBufferedImageTransparecia(
				"shield.png", null);
		crosshair = CarregadorRecursos.carregaBufferedImageTransparecia(
				"crosshair.png", null);
		vaiAqui = CarregadorRecursos.carregaBufferedImageTransparecia(
				"vaiaqui.png", null);
		blueFlag = CarregadorRecursos.carregaBufferedImageTransparecia(
				"blue-flag.png", null);
		redFlag = CarregadorRecursos.carregaBufferedImageTransparecia(
				"red-flag.png", null);
		assault = CarregadorRecursos.carregaBufferedImageTransparecia(
				"assault.png", null);
		shotgun = CarregadorRecursos.carregaBufferedImageTransparecia(
				"shotgun.png", null);
		riot_shield = CarregadorRecursos.carregaBufferedImageTransparecia(
				"riot-shield.png", null);

		machinegun = CarregadorRecursos.carregaBufferedImageTransparecia(
				"machinegun.png", null);
		sniper = CarregadorRecursos.carregaBufferedImageTransparecia(
				"sniper.png", null);
		headShot = CarregadorRecursos.carregaBufferedImageTransparecia(
				"headshot.png", null);
		rocket = CarregadorRecursos.carregaBufferedImageTransparecia(
				"rocket.png", null);
		rocket_launcher = CarregadorRecursos.carregaBufferedImageTransparecia(
				"rocket_launcher.png", null);
		knife = CarregadorRecursos.carregaBufferedImageTransparecia(
				"knife.png", null);
		azul = CarregadorRecursos.carregaBufferedImageTransparecia("azul.png",
				Color.MAGENTA);
		azul_sniper = CarregadorRecursos.carregaBufferedImageTransparecia(
				"azul_sniper.png", Color.MAGENTA);
		azul_shotgun = CarregadorRecursos.carregaBufferedImageTransparecia(
				"azul_shotgun.png", Color.MAGENTA);
		azul_machine = CarregadorRecursos.carregaBufferedImageTransparecia(
				"azul_machine.png", Color.MAGENTA);
		azul_rocket = CarregadorRecursos.carregaBufferedImageTransparecia(
				"azul_rocket.png", Color.MAGENTA);
		azul_faca = CarregadorRecursos.carregaBufferedImageTransparecia(
				"azul_faca.png", Color.MAGENTA);
		vermelho = CarregadorRecursos.carregaBufferedImageTransparecia(
				"vermelho.png", Color.MAGENTA);
		vermelho_faca = CarregadorRecursos.carregaBufferedImageTransparecia(
				"vermelho_faca.png", Color.MAGENTA);
		vermelho_shotgun = CarregadorRecursos.carregaBufferedImageTransparecia(
				"vermelho_shotgun.png", Color.MAGENTA);
		vermelho_machine = CarregadorRecursos.carregaBufferedImageTransparecia(
				"vermelho_machine.png", Color.MAGENTA);
		vermelho_rocket = CarregadorRecursos.carregaBufferedImageTransparecia(
				"vermelho_rocket.png", Color.MAGENTA);
		vermelho_sniper = CarregadorRecursos.carregaBufferedImageTransparecia(
				"vermelho_sniper.png", Color.MAGENTA);
		knifeAtttack = CarregadorRecursos.carregaBufferedImageTransparecia(
				"knifeAtttack.png", null);
		azulMortes = CarregadorRecursos.carregaBufferedImageTransparecia(
				"blue-dead.png", null);
		vermelhoMortes = CarregadorRecursos.carregaBufferedImageTransparecia(
				"red-dead.png", null);
		explosao = CarregadorRecursos.carregaBufferedImageTransparecia(
				"explosao.png", null);

	}

	private void gerarMapaImagensExplosao() {
		int altura = explosao.getHeight() / 4;
		int largura = explosao.getWidth() / 4;
		int contExplo = 16;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				Rectangle rect = new Rectangle(i * largura, j * altura,
						largura, altura);
				BufferedImage bufferedImage = ImageUtil.gerarSubImagem(
						explosao, rect);
				String key = "explo-" + contExplo--;
				mapImgs.put(key, ImageUtil.geraResize(bufferedImage, 3));
			}
		}

	}

	private void gerarMapaImagensMortes(BufferedImage src, String time) {
		int altura = ConstantesTopWar.ALTURA_AVATAR;
		int largura = ConstantesTopWar.LARGURA_AVATAR;
		BufferedImage bf = new BufferedImage(src.getWidth(), src.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) bf.getGraphics();
		graphics.drawImage(src, 0, 0, null);
		graphics.setColor(Color.MAGENTA);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				Rectangle rect = new Rectangle(i * largura, j * altura,
						largura, altura);
				graphics.draw(rect);
				graphics.drawString("i=" + i + " j=" + j, rect.x, rect.y
						+ altura - 10);
				BufferedImage bufferedImage = ImageUtil.gerarSubImagem(src,
						rect);
				String key = "morte-" + time + "-" + i + "-" + j;
				mapImgs.put(key, bufferedImage);
			}
		}

	}

	public boolean isDesenhaObjetos() {
		return desenhaObjetos;
	}

	public void setDesenhaObjetos(boolean desenhaObjetos) {
		this.desenhaObjetos = desenhaObjetos;
	}

	public boolean isDesenhaImagens() {
		return desenhaImagens;
	}

	public void setDesenhaImagens(boolean desenhaImagens) {
		this.desenhaImagens = desenhaImagens;
	}

	public void gerarMapaImagens(BufferedImage src, String time) {
		int altura = ConstantesTopWar.ALTURA_AVATAR;
		int largura = ConstantesTopWar.LARGURA_AVATAR;
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
			}
		}
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
				if (desenhaImagens) {
					graphics2d.drawImage(img, null, 0, 0);
				} else {
					graphics2d.setColor(Color.DARK_GRAY);
					graphics2d.fillRect(0, 0, mapaTopWar.getLargura(),
							mapaTopWar.getAltura());
				}
				loopDesenhaAvatares(graphics2d);
				desenhaInfoJogo(graphics2d);
				desenhaMira(graphics2d);
				desenhaExplosao(graphics2d);
				desenhaObjetosDebug(graphics2d);
				desenhaClicou(graphics2d);
				desenhaVaiPara(graphics2d);
				desenhaControleMudarClasse(graphics2d);

			}

			private void desenhaClicou(Graphics2D graphics2d) {
				if (System.currentTimeMillis() - jogoCliente.getClickTime() < 200) {
					Point p = jogoCliente.getPontoMouseClicado();
					graphics2d.drawImage(ImageUtil.geraResize(
							OcilaCor.geraOcila("vaiaqui", vaiAqui), 1.5),
							p.x - 12, p.y - 12, null);
				}

			}

			private void desenhaVaiPara(Graphics2D graphics2d) {
				if (jogoCliente.getPontoMouseMovendo() != null
						&& jogoCliente.isSeguirMouse()) {
					Point p = jogoCliente.getPontoMouseMovendo();
					graphics2d.drawImage(ImageUtil.geraResize(
							OcilaCor.geraOcila("vaiaqui", vaiAqui), 1.5),
							p.x - 12, p.y - 12, null);
				}
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

	protected void desenhaExplosao(Graphics2D graphics2d) {
		if (mapaExplosoes.isEmpty()) {
			return;
		}
		for (Iterator iterator = mapaExplosoes.keySet().iterator(); iterator
				.hasNext();) {
			Point p = (Point) iterator.next();
			int cont = mapaExplosoes.get(p);
			if (cont > 0) {
				BufferedImage bufferedImage = mapImgs.get("explo-" + cont);
				graphics2d.drawImage(bufferedImage, p.x - 90, p.y - 90, null);
				cont--;
				mapaExplosoes.put(p, cont);
			}
		}
	}

	private void desenhaObjetosDebug(Graphics2D graphics2d) {
		if (desenhaObjetos) {
			if (avatarLocal != null) {
				double angulo = avatarLocal.getAngulo();
				double velocidade = avatarLocal.getVelocidade();
				graphics2d.setColor(Color.GREEN);
				Rectangle limitesViewPort = (Rectangle) limitesViewPort();
				graphics2d.drawString("Angulo " + angulo,
						limitesViewPort.x + 10, limitesViewPort.y + 10);
				graphics2d.drawString("Velocidade " + velocidade,
						limitesViewPort.x + 10, limitesViewPort.y + 40);
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
				Point pontoAvatar = avatarLocal.getPontoAvatar();

				Point ptMin = GeoUtil.calculaPonto(angMinJogador, 500,
						pontoAvatar);
				graphics2d.setColor(Color.ORANGE);
				graphics2d.drawLine(pontoAvatar.x, pontoAvatar.y, ptMin.x,
						ptMin.y);
				Point ptMax = GeoUtil.calculaPonto(angMaxJogador, 500,
						pontoAvatar);
				graphics2d.setColor(Color.ORANGE);
				graphics2d.drawLine(pontoAvatar.x, pontoAvatar.y, ptMax.x,
						ptMax.y);
			}
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

	public int getTabCont() {
		return tabCont;
	}

	public void setTabCont(int tabCont) {
		this.tabCont = tabCont;
	}

	private void desenhaMira(Graphics2D graphics2d) {
		Point pontoMouse = jogoCliente.getPontoMouseMovendo();
		if (pontoMouse != null
				&& jogoCliente.clicouAvatarAdversario(pontoMouse)) {
			Point desenha = new Point(
					pontoMouse.x - (crosshair.getWidth() / 2), pontoMouse.y
							- (crosshair.getHeight() / 2));
			graphics2d.drawImage(crosshair, desenha.x, desenha.y, null);
		}
	}

	private void loopDesenhaAvatares(Graphics2D graphics2d) {
		Collection<AvatarCliente> avatarClientes = jogoCliente
				.getAvatarClientesCopia();
		if (avatarClientes == null) {
			return;
		}
		for (Iterator iterator = avatarClientes.iterator(); iterator.hasNext();) {
			AvatarCliente avatarCliente = (AvatarCliente) iterator.next();
			if (ConstantesTopWar.OBJ_ROCKET == avatarCliente.getArma()) {
				desenhaRocket(graphics2d, avatarCliente);
				continue;
			}
			double angulo = avatarCliente.getAngulo();
			/**
			 * angulo > 90 && angulo < 300 - cima resto baixo
			 */
			if (angulo > 90 && angulo < 300) {
				desenhaAvatares(graphics2d, avatarCliente);
				desenhaAvataresCombateCorpoACorpo(graphics2d, avatarCliente,
						angulo);
			} else {
				desenhaAvataresCombateCorpoACorpo(graphics2d, avatarCliente,
						angulo);
				desenhaAvatares(graphics2d, avatarCliente);
			}
			long millisSrv = jogoCliente.getMillisSrv();
			long tempoUtlDisparo = avatarCliente.getTempoUtlAtaque();
			if ((ConstantesTopWar.ARMA_ASSAULT == avatarCliente.getArma() || ConstantesTopWar.ARMA_MACHINEGUN == avatarCliente
					.getArma()) && (millisSrv - tempoUtlDisparo) < 300) {
				desenhaDisparoAvatarAssautMachine(graphics2d, avatarCliente,
						avatarClientes);
			}
			if ((ConstantesTopWar.ARMA_SNIPER == avatarCliente.getArma())
					&& (millisSrv - tempoUtlDisparo) < 300) {
				desenhaDisparoAvatarSniper(graphics2d, avatarCliente,
						avatarClientes);
			}
			if (ConstantesTopWar.ARMA_SHOTGUN == avatarCliente.getArma()
					&& (millisSrv - tempoUtlDisparo) < 300) {
				desenhaDisparoAvatarShotgun(graphics2d, avatarCliente,
						avatarClientes);
			}
		}
	}

	private void desenhaDisparoAvatarSniper(Graphics2D graphics2d,
			AvatarCliente avatarCliente,
			Collection<AvatarCliente> avatarClientes) {
		Point pontoAvatar = avatarCliente.getPontoAvatar();
		Point pontoTiro = GeoUtil.calculaPonto(avatarCliente.getAngulo(),
				avatarCliente.getRangeUtlDisparo(), pontoAvatar);

		List<Point> linhaDisparo = GeoUtil.drawBresenhamLine(pontoAvatar,
				pontoTiro);
		Point nOri = linhaDisparo.get(Util.inte(linhaDisparo.size() * .05));

		for (int i = 0; i < 2; i++) {
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

		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		AvatarCliente avatarClienteBateu = null;
		boolean bateu = false;
		for (int i = 0; i < linhaDisparo.size(); i++) {
			if (i > linhaDisparo.size() - 1) {
				break;
			}
			if (i < Util.inte(linhaDisparo.size() * .05)) {
				continue;
			}
			Point tiro = linhaDisparo.get(i);
			int indexPtFaiscaFim = (i + Util.intervalo(5, 25));
			for (Iterator iterator = avatarClientes.iterator(); iterator
					.hasNext();) {
				AvatarCliente avatarClienteAnalizar = (AvatarCliente) iterator
						.next();

				if (ConstantesTopWar.ARMA_SHIELD == avatarClienteAnalizar
						.getArma()
						&& !avatarCliente.getTime().equals(
								avatarClienteAnalizar.getTime())) {
					if (avatarClienteAnalizar.gerarEscudo().contains(tiro)) {
						bateu = true;
						avatarClienteBateu = null;
						break;
					}
				}
				if (!avatarCliente.equals(avatarClienteAnalizar)
						&& !avatarCliente.getTime().equals(
								avatarClienteAnalizar.getTime())
						&& tiro != null
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
					if (objetoMapa.getTransparencia() > 70 && tiro != null
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
				while (noAnt > (linhaDisparo.size() - 1)) {
					noAnt--;
				}
				Point ptAcertoAnt = linhaDisparo.get(noAnt);
				graphics2d.setColor(OcilaCor.geraOcila("Sniper", Color.WHITE));
				graphics2d.drawLine(pontoAvatar.x, pontoAvatar.y,
						ptAcertoAnt.x, ptAcertoAnt.y);
				nOri = tiro;
				for (int j = 0; j < 2; j++) {
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
						int intIni = Util.intervalo(5, 14);
						Point pIni = linha.get(intIni);
						Point pFim = linha.get(intIni + Util.intervalo(1, 24));
						graphics2d.drawLine(pIni.x, pIni.y, pFim.x, pFim.y);
					}
				}
			}
			/**
			 * Sangue Jogador
			 */
			if (avatarClienteBateu != null) {
				int noPost = i + 50;
				while (noPost > (linhaDisparo.size() - 1)) {
					noPost--;
				}
				Point nDst = linhaDisparo.get(noPost);
				for (int j = 0; j < 10; j++) {
					nOri = new Point(tiro.x, tiro.y);
					nDst = new Point(nDst.x + Util.intervalo(-10, 10), nDst.y
							+ Util.intervalo(-10, 10));
					graphics2d.setColor(Color.RED);
					List<Point> linha = GeoUtil.drawBresenhamLine(nOri, nDst);
					if (linha.size() > 40) {
						int intIni = Util.intervalo(10, 19);
						Point pIni = linha.get(intIni + Util.intervalo(1, 19));
						Point pFim = linha.get(intIni);
						graphics2d.drawLine(pIni.x, pIni.y, pFim.x, pFim.y);
					}
				}
			}
			if (bateu) {
				break;
			}
		}

		if (!bateu) {
			int noAnt = linhaDisparo.size() - 41;
			while (noAnt < 0) {
				noAnt++;
			}
			while (noAnt > (linhaDisparo.size() - 1)) {
				noAnt--;
			}
			Point ptAcertoAnt = linhaDisparo.get(noAnt);
			graphics2d.setColor(OcilaCor.geraOcila("Sniper", Color.WHITE));
			graphics2d.drawLine(pontoAvatar.x, pontoAvatar.y, ptAcertoAnt.x,
					ptAcertoAnt.y);
			nOri = linhaDisparo.get(linhaDisparo.size() - 1);
			for (int j = 0; j < 2; j++) {
				Point nDst = new Point(ptAcertoAnt.x + Util.intervalo(-10, 10),
						ptAcertoAnt.y + Util.intervalo(-10, 10));
				if (Math.random() > 0.5) {
					graphics2d.setColor(Color.YELLOW);
				} else {
					graphics2d.setColor(Color.WHITE);
				}
				List<Point> linha = GeoUtil.drawBresenhamLine(nOri, nDst);
				if (linha.size() > 40) {
					int intIni = Util.intervalo(5, 14);
					Point pIni = linha.get(intIni);
					Point pFim = linha.get(intIni + Util.intervalo(1, 24));
					graphics2d.drawLine(pIni.x, pIni.y, pFim.x, pFim.y);
				}
			}
		}

	}

	private void desenhaRocket(Graphics2D graphics2d,
			AvatarCliente avatarCliente) {
		Point p = null;
		if (avatarCliente.getPontoAvatarSuave() != null) {
			p = avatarCliente.getPontoAvatarSuave();
		}
		if (p == null) {
			p = avatarCliente.getPontoAvatar();
		}

		AffineTransform afRotate = new AffineTransform();
		double rad = Math.toRadians((double) avatarCliente.getAngulo());
		int larg = rocket.getWidth();
		int midLarg = larg / 2;
		afRotate.setToRotation(rad, midLarg, midLarg);
		AffineTransformOp opRotate = new AffineTransformOp(afRotate,
				AffineTransformOp.TYPE_BILINEAR);
		BufferedImage rotBuffer = new BufferedImage(larg, larg,
				BufferedImage.TYPE_INT_ARGB);
		opRotate.filter(rocket, rotBuffer);
		Rectangle area = new Rectangle(p.x, p.y, larg, larg);
		rotBuffer = processaSobreposicoesAvatar(rotBuffer, p, area, mapaTopWar);
		rotBuffer = processaGrade(rotBuffer, p, area, mapaTopWar);
		graphics2d.drawImage(rotBuffer, p.x - midLarg, p.y - midLarg, null);

		if (desenhaObjetos) {
			graphics2d.setColor(Color.WHITE);
			graphics2d.draw(avatarCliente.gerarCabeca());
			if (ConstantesTopWar.TIME_AZUL.equals(avatarCliente.getTime())) {
				graphics2d.setColor(Color.CYAN);
				graphics2d.draw(avatarCliente.gerarCorpo());
			} else {
				graphics2d.setColor(Color.MAGENTA);
				graphics2d.draw(avatarCliente.gerarCorpo());
			}
		}

		// Ellipse2D circ = new Ellipse2D.Double((double) p.x - 50.0,
		// (double) p.y - 50.0, 100.0, 100.0);
		// graphics2d.setColor(Color.YELLOW);
		// graphics2d.draw(circ);
	}

	private void desenhaDisparoAvatarShotgun(Graphics2D graphics2d,
			AvatarCliente avatarCliente,
			Collection<AvatarCliente> avatarClientes) {
		Point p = avatarCliente.getPontoAvatar();
		Point m = GeoUtil.calculaPonto(avatarCliente.getAngulo(), 200, p);
		for (int i = 0; i < 3; i++) {
			Point nOri = new Point(p.x, p.y);
			Point nDst = new Point(m.x + Util.intervalo(-30, 30), m.y
					+ Util.intervalo(-30, 30));

			List<Point> linha = GeoUtil.drawBresenhamLine(nOri, nDst);
			int cont = 0;
			for (Iterator iterator = linha.iterator(); iterator.hasNext();) {
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
					graphics2d.drawOval(point.x, point.y, Util.intervalo(1, 2),
							Util.intervalo(1, 2));
				}
			}
			if (linha.size() > 100) {
				int intIni = Util.intervalo(1, 20);
				Point pIni = linha.get(intIni);
				Point pFim = linha.get(intIni + Util.intervalo(1, 30));
				if (Math.random() > .7) {
					graphics2d.setColor(Color.WHITE);
				} else {
					graphics2d.setColor(Color.LIGHT_GRAY);
				}
				graphics2d.drawLine(pIni.x, pIni.y, pFim.x, pFim.y);
			}
		}

	}

	private void desenhaAvataresCombateCorpoACorpo(Graphics2D graphics2d,
			AvatarCliente avatarCliente, double angulo) {
		if (ConstantesTopWar.ARMA_FACA != avatarCliente.getArma()) {
			return;
		}
		long millisSrv = jogoCliente.getMillisSrv();
		long tempoUtlDisparo = avatarCliente.getTempoUtlAtaque();
		if ((millisSrv - tempoUtlDisparo) > 150) {
			return;
		}

		Point desenha = avatarCliente.getPontoDesenha();
		/**
		 * Desenha Faca
		 */
		Point pFaca = GeoUtil.calculaPonto(angulo, 10, desenha);
		AffineTransform afRotate = new AffineTransform();
		double rad = Math.toRadians((double) angulo - 60);
		afRotate.setToRotation(rad, knifeAtttack.getWidth() / 2,
				knifeAtttack.getHeight() / 2);
		AffineTransformOp opRotate = new AffineTransformOp(afRotate,
				AffineTransformOp.TYPE_BILINEAR);
		BufferedImage rotBuffer = new BufferedImage(knifeAtttack.getWidth(),
				knifeAtttack.getHeight(), BufferedImage.TYPE_INT_ARGB);
		opRotate.filter(knifeAtttack, rotBuffer);
		graphics2d.drawImage(rotBuffer, pFaca.x, pFaca.y, null);

	}

	protected void desenhaInfoJogo(Graphics2D g2d) {
		desenhaInfoCima(g2d);
		desenhaInfoControles(g2d);
		desenhaInfoBaixo(g2d);
		desenhaPlacar(g2d);
		desenhaEventos(g2d);

	}

	private void desenhaEventos(Graphics2D g2d) {
		if (miniAssalt == null) {
			return;
		}
		Shape limitesViewPort = limitesViewPort();
		int x = limitesViewPort.getBounds().x + 10;
		int y = limitesViewPort.getBounds().y + 50;

		int xJogador = x;
		int yTemp = y;
		BufferedImage arma = null;
		List<EventoJogo> eventos = jogoCliente.getEventosCopia();
		for (Iterator iterator = eventos.iterator(); iterator.hasNext();) {
			EventoJogo eventoJogo = (EventoJogo) iterator.next();
			if (jogoCliente.getMillisSrv() - eventoJogo.getTempo() > 5000) {
				continue;
			}
			if (eventoJogo.getArma() == ConstantesTopWar.ARMA_FACA) {
				arma = miniKnife;
			}
			if (eventoJogo.getArma() == ConstantesTopWar.ARMA_ASSAULT) {
				arma = miniAssalt;
			}
			if (eventoJogo.getArma() == ConstantesTopWar.ARMA_MACHINEGUN) {
				arma = miniMachineGun;
			}
			if (eventoJogo.getArma() == ConstantesTopWar.ARMA_SHOTGUN) {
				arma = miniShotgun;
			}

			if (eventoJogo.getArma() == ConstantesTopWar.ARMA_ROCKET) {
				arma = miniRocket;
			}

			if (eventoJogo.getArma() == ConstantesTopWar.ARMA_SHIELD) {
				arma = miniShield;
			}

			if (eventoJogo.getArma() == ConstantesTopWar.ARMA_SNIPER) {
				arma = miniSniper;
			}
			if (eventoJogo.getArma() == ConstantesTopWar.HEADSHOT) {
				arma = miniHeadShot;
			}

			yTemp += arma.getHeight() + 20;
			xJogador = x;

			int larguraNmJogador = Util.calculaLarguraText(
					eventoJogo.getAtacante(), g2d) + 10;

			if (ConstantesTopWar.PTS_VERMELHO.equals(eventoJogo
					.getTimeAtacante())) {
				g2d.setColor(ConstantesTopWar.lightRed);
			}
			if (ConstantesTopWar.PTS_AZUL.equals(eventoJogo.getTimeAtacante())) {
				g2d.setColor(ConstantesTopWar.lightBlu);
			}
			g2d.fillRoundRect(xJogador - 5, yTemp - 20, larguraNmJogador, 20,
					10, 10);
			g2d.setColor(Color.DARK_GRAY);
			g2d.drawString("" + eventoJogo.getAtacante(), xJogador, yTemp - 5);

			int xArma = xJogador + larguraNmJogador + 10;

			g2d.setColor(ConstantesTopWar.lightWhite);
			g2d.fillRoundRect(xArma - 10, yTemp - (arma.getHeight() + 5),
					arma.getWidth() + 20, arma.getHeight() + 5, 10, 10);
			g2d.drawImage(arma, xArma - 10, yTemp - (arma.getHeight()), null);

			xJogador = xArma + arma.getWidth() + 20;

			larguraNmJogador = Util.calculaLarguraText(eventoJogo.getMorto(),
					g2d) + 10;

			if (ConstantesTopWar.PTS_VERMELHO.equals(eventoJogo.getTimeMorto())) {
				g2d.setColor(ConstantesTopWar.lightRed);
			}
			if (ConstantesTopWar.PTS_AZUL.equals(eventoJogo.getTimeMorto())) {
				g2d.setColor(ConstantesTopWar.lightBlu);
			}
			g2d.fillRoundRect(xJogador - 5, yTemp - 20, larguraNmJogador, 20,
					10, 10);
			g2d.setColor(Color.DARK_GRAY);
			g2d.drawString("" + eventoJogo.getMorto(), xJogador, yTemp - 5);

			if (eventoJogo.getArma() == 6
					&& !desenhaImprime.contains(eventoJogo.toString())) {
				Logger.logar("Evento Desenhado " + eventoJogo);
				desenhaImprime.add(eventoJogo.toString());
			}
		}
	}

	Set desenhaImprime = new HashSet();

	private void desenhaPlacar(Graphics2D g2d) {
		if (tabCont <= 0) {
			return;
		}
		if (jogoCliente.isJogoEmAndamento())
			tabCont--;
		Shape limitesViewPort = limitesViewPort();
		int meio = limitesViewPort.getBounds().x
				+ limitesViewPort.getBounds().width / 2;
		int y = limitesViewPort.getBounds().y + 100;

		/**
		 * Blues
		 */

		List<PlacarTopWar> list = jogoCliente
				.geraListaPlacarOrdenada(ConstantesTopWar.TIME_AZUL);

		int xJogador = meio - 300;
		int yTemp = y;
		int xKills = xJogador + 205;
		int xDeaths = xKills + 45;

		g2d.setColor(ConstantesTopWar.lightBlu);
		g2d.fillRoundRect(xJogador - 5, yTemp - 15, 190, 20, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Lang.msg("jogador"), xJogador, yTemp);

		g2d.setColor(ConstantesTopWar.lightBlu);
		g2d.fillRoundRect(xKills - 5, yTemp - 15,
				Util.calculaLarguraText(Lang.msg("kills"), g2d) + 10, 20, 10,
				10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Lang.msg("kills"), xKills, yTemp);

		g2d.setColor(ConstantesTopWar.lightBlu);
		g2d.fillRoundRect(xDeaths - 5, yTemp - 15,
				Util.calculaLarguraText(Lang.msg("deaths"), g2d) + 10, 20, 10,
				10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Lang.msg("deaths"), xDeaths, yTemp);

		for (int i = 0; i < list.size(); i++) {
			PlacarTopWar placarTopWar = list.get(i);
			yTemp += 22;
			g2d.setColor(ConstantesTopWar.lightBlu);
			g2d.fillRoundRect(xJogador - 5, yTemp - 15, 190, 20, 10, 10);
			g2d.setColor(Color.BLACK);
			g2d.drawString("" + placarTopWar.getJogador(), xJogador, yTemp);

			g2d.drawString("" + placarTopWar.getClasse(), xJogador + 110, yTemp);

			g2d.setColor(ConstantesTopWar.lightBlu);
			g2d.fillRoundRect(xKills - 5, yTemp - 15,
					Util.calculaLarguraText(Lang.msg("00"), g2d) + 10, 20, 10,
					10);
			g2d.setColor(Color.BLACK);
			g2d.drawString("" + placarTopWar.getKills(), xKills, yTemp);

			g2d.setColor(ConstantesTopWar.lightBlu);
			g2d.fillRoundRect(xDeaths - 5, yTemp - 15,
					Util.calculaLarguraText(Lang.msg("00"), g2d) + 10, 20, 10,
					10);
			g2d.setColor(Color.BLACK);
			g2d.drawString("" + placarTopWar.getDeaths(), xDeaths, yTemp);

		}
		/**
		 * Reds
		 */
		list = jogoCliente
				.geraListaPlacarOrdenada(ConstantesTopWar.TIME_VERMELHO);

		xJogador = meio + 5;
		yTemp = y;
		xKills = xJogador + 205;
		xDeaths = xKills + 45;

		g2d.setColor(ConstantesTopWar.lightRed);
		g2d.fillRoundRect(xJogador - 5, yTemp - 15, 190, 20, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Lang.msg("jogador"), xJogador, yTemp);

		g2d.setColor(ConstantesTopWar.lightRed);
		g2d.fillRoundRect(xKills - 5, yTemp - 15,
				Util.calculaLarguraText(Lang.msg("kills"), g2d) + 10, 20, 10,
				10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Lang.msg("kills"), xKills, yTemp);

		g2d.setColor(ConstantesTopWar.lightRed);
		g2d.fillRoundRect(xDeaths - 5, yTemp - 15,
				Util.calculaLarguraText(Lang.msg("deaths"), g2d) + 10, 20, 10,
				10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Lang.msg("deaths"), xDeaths, yTemp);

		for (int i = 0; i < list.size(); i++) {
			PlacarTopWar placarTopWar = list.get(i);
			yTemp += 22;
			g2d.setColor(ConstantesTopWar.lightRed);
			g2d.fillRoundRect(xJogador - 5, yTemp - 15, 190, 20, 10, 10);
			g2d.setColor(Color.BLACK);
			g2d.drawString("" + placarTopWar.getJogador(), xJogador, yTemp);

			g2d.drawString("" + placarTopWar.getClasse(), xJogador + 110, yTemp);

			g2d.setColor(ConstantesTopWar.lightRed);
			g2d.fillRoundRect(xKills - 5, yTemp - 15,
					Util.calculaLarguraText(Lang.msg("00"), g2d) + 10, 20, 10,
					10);
			g2d.setColor(Color.BLACK);
			g2d.drawString("" + placarTopWar.getKills(), xKills, yTemp);

			g2d.setColor(ConstantesTopWar.lightRed);
			g2d.fillRoundRect(xDeaths - 5, yTemp - 15,
					Util.calculaLarguraText(Lang.msg("00"), g2d) + 10, 20, 10,
					10);
			g2d.setColor(Color.BLACK);
			g2d.drawString("" + placarTopWar.getDeaths(), xDeaths, yTemp);

		}

	}

	private void desenhaInfoControles(Graphics2D g2d) {
		Shape limitesViewPort = limitesViewPort();
		int x = limitesViewPort.getBounds().x
				+ (limitesViewPort.getBounds().width - 10);
		int y = limitesViewPort.getBounds().y + 30;
		Font fontOri = g2d.getFont();
		g2d.setFont(new Font(fontOri.getName(), fontOri.getStyle(), 14));
		g2d.setColor(ConstantesTopWar.lightWhite);

		int largura = Util.calculaLarguraText(Lang.msg("mouseBtnEsq"), g2d) + 20;
		g2d.fillRoundRect(x - largura, y - 20, largura, 35, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Lang.msg("mouseBtnEsq"), x - largura + 10, y);

		y += 40;

		g2d.setColor(ConstantesTopWar.lightWhite);

		largura = Util.calculaLarguraText(Lang.msg("mouseBtnDir"), g2d) + 20;
		g2d.fillRoundRect(x - largura, y - 20, largura, 35, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Lang.msg("mouseBtnDir"), x - largura + 10, y);

		y += 40;

		g2d.setColor(ConstantesTopWar.lightWhite);

		largura = Util.calculaLarguraText(Lang.msg("asdOuSetas"), g2d) + 20;
		g2d.fillRoundRect(x - largura, y - 20, largura, 35, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Lang.msg("asdOuSetas"), x - largura + 10, y);

		y += 40;

		g2d.setColor(ConstantesTopWar.lightWhite);
		largura = Util.calculaLarguraText(Lang.msg("teclaControl"), g2d) + 20;
		g2d.fillRoundRect(x - largura, y - 20, largura, 35, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Lang.msg("teclaControl"), x - largura + 10, y);

		y += 40;

		g2d.setColor(ConstantesTopWar.lightWhite);
		largura = Util.calculaLarguraText(Lang.msg("espaco"), g2d) + 20;
		g2d.fillRoundRect(x - largura, y - 20, largura, 35, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Lang.msg("espaco"), x - largura + 10, y);

		y += 40;

		g2d.setColor(ConstantesTopWar.lightWhite);

		largura = Util.calculaLarguraText(Lang.msg("r"), g2d) + 20;

		g2d.fillRoundRect(x - largura, y - 20, largura, 35, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Lang.msg("r"), x - largura + 10, y);

		y += 40;

		g2d.setColor(ConstantesTopWar.lightWhite);
		largura = Util.calculaLarguraText(Lang.msg("p"), g2d) + 20;
		g2d.fillRoundRect(x - largura, y - 20, largura, 35, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Lang.msg("p"), x - largura + 10, y);

		g2d.setFont(fontOri);
	}

	private void desenhaInfoBaixo(Graphics2D g2d) {
		Shape limitesViewPort = limitesViewPort();
		int x = limitesViewPort.getBounds().x
				+ (limitesViewPort.getBounds().width - 140);
		int y = limitesViewPort.getBounds().y
				+ (limitesViewPort.getBounds().height - 10);
		Font fontOri = g2d.getFont();
		g2d.setFont(new Font(fontOri.getName(), fontOri.getStyle(), 32));

		BufferedImage arma = null;
		if (jogoCliente.getArma() == ConstantesTopWar.ARMA_FACA) {
			arma = knife;
		} else if (jogoCliente.getArma() == ConstantesTopWar.ARMA_ASSAULT) {
			arma = assault;
		} else if (jogoCliente.getArma() == ConstantesTopWar.ARMA_SHOTGUN) {
			arma = shotgun;
		} else if (jogoCliente.getArma() == ConstantesTopWar.ARMA_MACHINEGUN) {
			arma = machinegun;
		} else if (jogoCliente.getArma() == ConstantesTopWar.ARMA_SNIPER) {
			arma = sniper;
		} else if (jogoCliente.getArma() == ConstantesTopWar.ARMA_ROCKET) {
			arma = rocket_launcher;
		} else if (jogoCliente.getArma() == ConstantesTopWar.ARMA_SHIELD) {
			arma = riot_shield;
		}
		if (arma == null) {
			g2d.setFont(fontOri);
			return;
		}

		int xArma = x - (arma.getWidth() + 30);

		if (jogoCliente.verificaRecarregando()) {
			g2d.setColor(OcilaCor.geraOcila("RECARREGANDO", Color.WHITE));
			g2d.fillRoundRect(
					x - 160,
					y - 30,
					Util.calculaLarguraText(Lang.msg("RECARREGANDO"), g2d) + 20,
					35, 10, 10);
			g2d.setColor(Color.BLACK);
			g2d.drawString(Lang.msg("RECARREGANDO"), x - 150, y);
		} else {
			g2d.setColor(ConstantesTopWar.lightWhite);
			g2d.fillRoundRect(xArma - 10, y - (arma.getHeight() + 5),
					arma.getWidth() + 20, arma.getHeight() + 5, 10, 10);
			if (desenhaImagens)
				g2d.drawImage(arma, xArma - 10, y - (arma.getHeight()), null);

			y -= 5;
			g2d.setColor(ConstantesTopWar.lightWhite);
			g2d.fillRoundRect(x - 10, y - 30,
					Util.calculaLarguraText("888", g2d) + 20, 35, 10, 10);
			g2d.setColor(Color.BLACK);
			g2d.drawString("" + jogoCliente.getBalas(), x, y);
			x += 80;
			g2d.setColor(ConstantesTopWar.lightWhite);
			g2d.fillRoundRect(x - 10, y - 30, 60, 35, 10, 10);
			g2d.setColor(Color.BLACK);
			g2d.drawString("" + jogoCliente.getCartuchos(), x, y);
		}
		g2d.setFont(fontOri);
	}

	private void desenhaInfoCima(Graphics2D g2d) {
		Shape limitesViewPort = limitesViewPort();
		Font fontOri = g2d.getFont();
		g2d.setFont(new Font(fontOri.getName(), fontOri.getStyle(), 32));
		String formatarTempo = Util.formatarTempo(jogoCliente
				.getTempoRestanteJogo());
		int larguraTimer = Util.calculaLarguraText(formatarTempo, g2d) + 20;

		int x = limitesViewPort.getBounds().x
				+ limitesViewPort.getBounds().width / 2 - (larguraTimer / 2);
		int y = limitesViewPort.getBounds().y + 40;

		int larguraPlacarAzul = Util.calculaLarguraText("00", g2d) + 20;
		int xleft = x - (larguraPlacarAzul + 10);

		g2d.setColor(ConstantesTopWar.lightBlu);
		g2d.fillRoundRect(xleft - 10, y - 30, larguraPlacarAzul, 35, 10, 10);
		g2d.setColor(Color.DARK_GRAY);
		g2d.drawString("" + jogoCliente.getPtsAzul(), xleft, y);

		xleft -= blueFlag.getWidth() + 30;

		g2d.setColor(ConstantesTopWar.lightBlu);
		g2d.fillRoundRect(xleft - 10, y - 30, blueFlag.getWidth() + 20,
				blueFlag.getHeight() + 5, 10, 10);
		g2d.drawImage(blueFlag, xleft - 5, y - 25, null);

		g2d.setColor(ConstantesTopWar.lightWhite);
		g2d.fillRoundRect(x - 10, y - 30, larguraTimer, 35, 10, 10);

		g2d.setColor(Color.BLACK);
		g2d.drawString(formatarTempo, x, y);

		x += Util.calculaLarguraText(formatarTempo, g2d) + 30;

		g2d.setColor(ConstantesTopWar.lightRed);
		g2d.fillRoundRect(x - 10, y - 30,
				Util.calculaLarguraText("00", g2d) + 20, 35, 10, 10);
		g2d.setColor(Color.DARK_GRAY);
		g2d.drawString("" + jogoCliente.getPtsVermelho(), x, y);

		x += Util.calculaLarguraText("00", g2d) + 30;

		g2d.setColor(ConstantesTopWar.lightRed);
		g2d.fillRoundRect(x - 10, y - 30, redFlag.getWidth() + 20,
				redFlag.getHeight() + 5, 10, 10);
		g2d.drawImage(redFlag, x - 5, y - 25, null);
		g2d.setFont(fontOri);
	}

	protected void desenhaDisparoAvatarAssautMachine(Graphics2D graphics2d,
			AvatarCliente avatarCliente,
			Collection<AvatarCliente> avatarClientes) {
		Point pontoAvatar = avatarCliente.getPontoAvatar();
		Point pontoTiro = GeoUtil.calculaPonto(avatarCliente.getAngulo(),
				avatarCliente.getRangeUtlDisparo(), pontoAvatar);

		List<Point> linhaDisparo = GeoUtil.drawBresenhamLine(pontoAvatar,
				pontoTiro);
		Point nOri = linhaDisparo.get(Util.inte(linhaDisparo.size() * .05));

		for (int i = 0; i < 5; i++) {
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

		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		AvatarCliente avatarClienteBateu = null;
		boolean bateu = false;
		for (int i = 0; i < linhaDisparo.size(); i += 2) {
			if (i > linhaDisparo.size() - 1) {
				break;
			}
			if (i < Util.inte(linhaDisparo.size() * .05)) {
				continue;
			}
			Point tiro = linhaDisparo.get(i);
			int indexPtFaiscaFim = (i + Util.intervalo(5, 25));
			if (indexPtFaiscaFim < linhaDisparo.size() && Math.random() > .989) {
				Point ptFaiscaFim = linhaDisparo.get(indexPtFaiscaFim);
				graphics2d.setColor(Color.YELLOW);
				graphics2d.drawLine(tiro.x, tiro.y, ptFaiscaFim.x,
						ptFaiscaFim.y);
			}
			for (Iterator iterator = avatarClientes.iterator(); iterator
					.hasNext();) {
				AvatarCliente avatarClienteAnalizar = (AvatarCliente) iterator
						.next();

				if (ConstantesTopWar.ARMA_SHIELD == avatarClienteAnalizar
						.getArma()
						&& !avatarCliente.getTime().equals(
								avatarClienteAnalizar.getTime())) {
					if (avatarClienteAnalizar.gerarEscudo().contains(tiro)) {
						bateu = true;
						avatarClienteBateu = null;
						break;
					}
				}
				if (!avatarCliente.equals(avatarClienteAnalizar)
						&& !avatarCliente.getTime().equals(
								avatarClienteAnalizar.getTime())
						&& avatarClienteAnalizar.getVida() > 0 && tiro != null
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
					if (objetoMapa.getTransparencia() > 50 && tiro != null
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
				while (noAnt > (linhaDisparo.size() - 1)) {
					noAnt--;
				}
				Point ptAcertoAnt = linhaDisparo.get(noAnt);
				nOri = tiro;
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
						int intIni = Util.intervalo(5, 14);
						Point pIni = linha.get(intIni);
						Point pFim = linha.get(intIni + Util.intervalo(1, 24));
						graphics2d.drawLine(pIni.x, pIni.y, pFim.x, pFim.y);
					}
				}
			}
			/**
			 * Sangue Jogador
			 */
			if (avatarClienteBateu != null) {
				int noPost = i + 50;
				while (noPost > (linhaDisparo.size() - 1)) {
					noPost--;
				}
				Point nDst = linhaDisparo.get(noPost);
				for (int j = 0; j < 5; j++) {
					nOri = new Point(tiro.x + Util.intervalo(-10, 10), tiro.y
							+ Util.intervalo(-10, 10));
					graphics2d.setColor(Color.RED);
					List<Point> linha = GeoUtil.drawBresenhamLine(nOri, nDst);
					if (linha.size() > 40) {
						int intIni = Util.intervalo(10, 19);
						Point pIni = linha.get(intIni + Util.intervalo(1, 19));
						Point pFim = linha.get(intIni);
						graphics2d.drawLine(pIni.x, pIni.y, pFim.x, pFim.y);
					}
				}
			}
			if (bateu) {
				break;
			}
		}

		if (!bateu) {
			int noAnt = linhaDisparo.size() - 41;
			while (noAnt < 0) {
				noAnt++;
			}
			while (noAnt > (linhaDisparo.size() - 1)) {
				noAnt--;
			}
			Point ptAcertoAnt = linhaDisparo.get(noAnt);
			nOri = linhaDisparo.get(linhaDisparo.size() - 1);
			for (int j = 0; j < 5; j++) {
				Point nDst = new Point(ptAcertoAnt.x + Util.intervalo(-10, 10),
						ptAcertoAnt.y + Util.intervalo(-10, 10));
				if (Math.random() > 0.5) {
					graphics2d.setColor(Color.YELLOW);
				} else {
					graphics2d.setColor(Color.WHITE);
				}
				List<Point> linha = GeoUtil.drawBresenhamLine(nOri, nDst);
				if (linha.size() > 40) {
					int intIni = Util.intervalo(5, 14);
					Point pIni = linha.get(intIni);
					Point pFim = linha.get(intIni + Util.intervalo(1, 24));
					graphics2d.drawLine(pIni.x, pIni.y, pFim.x, pFim.y);
				}
			}
		}

	}

	private void desenhaEscudo(Graphics2D graphics2d, double angulo,
			BufferedImage imgJog, Point desenha) {
		Point p = GeoUtil.calculaPonto(angulo, 10, desenha);
		AffineTransform afRotate = new AffineTransform();
		double rad = Math.toRadians((double) angulo);
		int larg = shield.getWidth();
		int midLarg = larg / 2;
		afRotate.setToRotation(rad, midLarg, midLarg);
		AffineTransformOp opRotate = new AffineTransformOp(afRotate,
				AffineTransformOp.TYPE_BILINEAR);
		BufferedImage rotBuffer = new BufferedImage(larg, larg,
				BufferedImage.TYPE_INT_ARGB);
		opRotate.filter(shield, rotBuffer);
		Rectangle area = new Rectangle(p.x, p.y, larg, larg);
		rotBuffer = processaSobreposicoesAvatar(rotBuffer, p, area, mapaTopWar);
		rotBuffer = processaGrade(rotBuffer, desenha, area, mapaTopWar);
		graphics2d.drawImage(rotBuffer, p.x, p.y, null);
	}

	protected void desenhaAvatares(Graphics2D graphics2d,
			AvatarCliente avatarCliente) {
		if (!gerouImagens) {
			return;
		}
		Point pontoAvatar = avatarCliente.getPontoAvatarSuave();
		if (pontoAvatar == null) {
			pontoAvatar = avatarCliente.getPontoAvatar();
		}
		int anim = avatarCliente.getQuadroAnimacao();
		int aniMorte = avatarCliente.getQuadroAnimacaoMorte();
		String time = avatarCliente.getTime();
		double angulo = avatarCliente.getAngulo();
		if (angulo < 0) {
			angulo = 360 + angulo;
		}
		BufferedImage imgJog = null;
		if (avatarCliente.getVida() > 0) {
			String timeClasse = time;
			if (ConstantesTopWar.ARMA_FACA == avatarCliente.getArma()) {
				timeClasse += "_faca";
			}
			if (ConstantesTopWar.ARMA_SHOTGUN == avatarCliente.getArma()) {
				timeClasse += "_shotgun";
			}
			if (ConstantesTopWar.ARMA_MACHINEGUN == avatarCliente.getArma()) {
				timeClasse += "_machine";
			}
			if (ConstantesTopWar.ARMA_SHIELD == avatarCliente.getArma()) {
				timeClasse += "_shield";
			}
			if (ConstantesTopWar.ARMA_SNIPER == avatarCliente.getArma()) {
				timeClasse += "_sniper";
			}
			if (ConstantesTopWar.ARMA_ROCKET == avatarCliente.getArma()) {
				timeClasse += "_rocket";
			}
			if (angulo >= 0 && angulo <= 22.5 || angulo > 337.5) {
				imgJog = mapImgs.get(timeClasse + "-" + anim + "-0");
			} else if (angulo > 292.5 && angulo <= 337.5) {
				imgJog = mapImgs.get(timeClasse + "-" + anim + "-1");
			} else if (angulo > 247.5 && angulo <= 292.5) {
				imgJog = mapImgs.get(timeClasse + "-" + anim + "-2");
			} else if (angulo > 202.5 && angulo <= 247.5) {
				imgJog = mapImgs.get(timeClasse + "-" + anim + "-3");
			} else if (angulo > 157.5 && angulo <= 202.5) {
				imgJog = mapImgs.get(timeClasse + "-" + anim + "-4");
			} else if (angulo > 112.5 && angulo <= 157.5) {
				imgJog = mapImgs.get(timeClasse + "-" + anim + "-5");
			} else if (angulo > 67.5 && angulo <= 112.5) {
				imgJog = mapImgs.get(timeClasse + "-" + anim + "-6");
			} else if (angulo > 22.5 && angulo <= 67.5) {
				imgJog = mapImgs.get(timeClasse + "-" + anim + "-7");
			}
			if (avatarCliente.getPontoAvatar() != null
					&& avatarCliente.getPontoAvatarOld() != null
					&& !avatarCliente.getPontoAvatar().equals(
							avatarCliente.getPontoAvatarOld())) {
				avatarCliente.animar();
			}
		} else {
			if (angulo > 90 && angulo < 300) {
				imgJog = mapImgs.get("morte-" + time + "-" + aniMorte + "-1");
			} else {
				imgJog = mapImgs.get("morte-" + time + "-" + aniMorte + "-0");
			}
			avatarCliente.animarDesenhoMorte();
		}
		if (imgJog == null) {
			Logger.logar("Angulo nulo " + angulo);
		} else {
			Point desenha = avatarCliente.getPontoDesenhaSuave();
			if (desenha == null) {
				desenha = avatarCliente.getPontoDesenha();
			}
			if (desenha == null) {
				return;
			}
			Rectangle areaAvatar = new Rectangle(desenha.x, desenha.y,
					imgJog.getWidth(), imgJog.getHeight());
			imgJog = processaSobreposicoesAvatar(imgJog, desenha, areaAvatar,
					mapaTopWar);
			imgJog = processaGrade(imgJog, desenha, areaAvatar, mapaTopWar);
			/**
			 * Avatar Fade
			 */
			int transp = 255;
			if (jogoCliente.getPontoAvatar() != null
					&& !avatarCliente.isLocal()) {
				int distacia = (int) GeoUtil.distaciaEntrePontos(
						jogoCliente.getPontoAvatar(), pontoAvatar);
				if (distacia > 250) {
					transp = (510 - (distacia - 200)) / 2;
					if (transp > 255) {
						transp = 255;
					}
					if (transp < 0) {
						transp = 0;
					}
					imgJog = ImageUtil.gerarFade(imgJog, transp);
				}

			}
			if (desenhaImagens) {
				if (transp == 255) {
					graphics2d.drawImage(ImageUtil.processaSombra(imgJog),
							desenha.x - 10, desenha.y, null);
				}
				graphics2d.drawImage(imgJog, desenha.x, desenha.y, null);
				if (ConstantesTopWar.ARMA_SHIELD == avatarCliente.getArma()) {
					desenhaEscudo(graphics2d, angulo, imgJog, desenha);
				}
			}

			if (desenhaObjetos) {
				graphics2d.setColor(Color.WHITE);
				graphics2d.draw(avatarCliente.gerarCabeca());
				if (ConstantesTopWar.TIME_AZUL.equals(avatarCliente.getTime())) {
					graphics2d.setColor(Color.CYAN);
					graphics2d.draw(avatarCliente.gerarCorpo());
				} else {
					graphics2d.setColor(Color.MAGENTA);
					graphics2d.draw(avatarCliente.gerarCorpo());
				}
			}
			/**
			 * Barra de Vida e Nome
			 */
			if (avatarCliente.isLocal()
					|| avatarCliente.getNomeJogador().equals(
							jogoCliente.getKillCam())
					|| (jogoCliente.getPontoMouseMovendo() != null && avatarCliente
							.obeterAreaAvatar().contains(
									jogoCliente.getPontoMouseMovendo()))
					|| (jogoCliente.getPontoMouseMovendo() != null && avatarCliente
							.obeterAreaAvatarSuave().contains(
									jogoCliente.getPontoMouseMovendo()))) {
				graphics2d.setColor(new Color(128, 128, 128, 100));
				graphics2d.fillRoundRect(desenha.x - 20, desenha.y - 20, 100,
						20, 5, 5);
				if (ConstantesTopWar.TIME_AZUL.equals(avatarCliente.getTime())) {
					graphics2d.setColor(ConstantesTopWar.lightBlu);
				} else {
					graphics2d.setColor(ConstantesTopWar.lightRed);
				}
				graphics2d.fillRoundRect(desenha.x - 20, desenha.y - 20,
						avatarCliente.getVida(), 20, 5, 5);

				if (ConstantesTopWar.ARMA_ASSAULT == avatarCliente.getArma()) {
					graphics2d.drawImage(lifeBarAssalt, desenha.x - 20,
							desenha.y - 20, null);
				} else if (ConstantesTopWar.ARMA_MACHINEGUN == avatarCliente
						.getArma()) {
					graphics2d.drawImage(lifeBarMachineGun, desenha.x - 20,
							desenha.y - 20, null);
				} else if (ConstantesTopWar.ARMA_FACA == avatarCliente
						.getArma()) {
					graphics2d.drawImage(lifeBarKnife, desenha.x - 20,
							desenha.y - 20, null);
				} else if (ConstantesTopWar.ARMA_SHOTGUN == avatarCliente
						.getArma()) {
					graphics2d.drawImage(lifeBarShotgun, desenha.x - 20,
							desenha.y - 20, null);
				} else if (ConstantesTopWar.ARMA_SNIPER == avatarCliente
						.getArma()) {
					graphics2d.drawImage(lifeBarSniper, desenha.x - 20,
							desenha.y - 20, null);
				} else if (ConstantesTopWar.ARMA_ROCKET == avatarCliente
						.getArma()) {
					graphics2d.drawImage(lifeBarRocket, desenha.x - 20,
							desenha.y - 20, null);
				} else if (ConstantesTopWar.ARMA_SHIELD == avatarCliente
						.getArma()) {
					graphics2d.drawImage(lifeBarShield, desenha.x - 20,
							desenha.y - 20, null);
				}
				if (ConstantesTopWar.TIME_AZUL.equals(avatarCliente.getTime())) {
					graphics2d.setColor(Color.WHITE);
				} else {
					graphics2d.setColor(Color.BLACK);
				}
				graphics2d.drawString("" + avatarCliente.getNomeJogador() + " "
						+ avatarCliente.getVida(), desenha.x,
						pontoAvatar.y - 20);
			}
		}

		/**
		 * Aurea Invunerabilidade
		 */

		if (avatarCliente.isInvencivel()) {
			Rectangle ar = avatarCliente.obeterAreaAvatarSuave().getBounds();
			if (ConstantesTopWar.TIME_AZUL.equals(avatarCliente.getTime())) {
				graphics2d.setColor(OcilaCor.geraOcila("INVENC_AZUL",
						new Color(150, 150, 255)));
			}
			if (ConstantesTopWar.TIME_VERMELHO.equals(avatarCliente.getTime())) {
				graphics2d.setColor(OcilaCor.geraOcila("INVENC_VERMELHO",
						new Color(255, 150, 150)));
			}
			graphics2d.fillOval(ar.x, ar.y, ar.width, ar.height);
		}

	}

	public void atualiza() {
		Collection<AvatarCliente> avatarClientes = jogoCliente
				.getAvatarClientesCopia();
		if (avatarClientes == null) {
			return;
		}
		for (Iterator iterator = avatarClientes.iterator(); iterator.hasNext();) {
			AvatarCliente avatarCliente = (AvatarCliente) iterator.next();
			if (avatarCliente.isLocal() && avatarCliente.getVida() > 0) {
				avatarLocal = avatarCliente;
				contralizaPontoNoAvatar(avatarCliente);
				break;
			}
			if (!Util.isNullOrEmpty(jogoCliente.getKillCam())
					&& avatarCliente.getNomeJogador().equals(
							jogoCliente.getKillCam())) {
				contralizaPontoNoAvatar(avatarCliente);
				break;
			}
		}
		if (panel != null) {
			panel.repaint();
		}
	}

	private void contralizaPontoNoAvatar(AvatarCliente avatarCliente) {
		Point pontoAv = avatarCliente.getPontoAvatarSuave();
		if (pontoAv == null) {
			pontoAv = avatarCliente.getPontoAvatar();
		}
		centralizarPontoDireto(pontoAv);
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

	public void explosao(Point pontoAvatar) {
		mapaExplosoes.put(pontoAvatar, 16);
	}

}
