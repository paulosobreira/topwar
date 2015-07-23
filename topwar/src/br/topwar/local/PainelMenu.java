package br.topwar.local;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import br.nnpe.ImageUtil;
import br.nnpe.Logger;
import br.nnpe.OcilaCor;
import br.nnpe.Util;
import br.topwar.ConstantesTopWar;
import br.topwar.cliente.PainelTopWar;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.recursos.idiomas.Lang;

public class PainelMenu {

	public BufferedImage bg;

	private static final int FADE_MINIS = 100;

	public static String MENU_SOBRE = "MENU_SOBRE";

	public static String MENU_PRINCIPAL = "MENU_PRINCIPAL";

	public static String MENU_DEMO = "MENU_DEMO";

	public static String MENU_JOGAR = "MENU_JOGAR";

	public static String MAPA_DESERTO = "mapa9";

	public static String MAPA_CIDADE = "mapa16";

	public static String MAPA_PRAIA = "mapa9";

	private String MENU = MENU_PRINCIPAL;

	private MainFrame mainFrame;

	public final static Color lightWhite = new Color(200, 200, 200, 100);

	public final static Color lightWhite2 = new Color(255, 255, 255, 160);

	public final static Color yel = new Color(255, 255, 0, 150);

	public final static Color oran = new Color(255, 188, 40, 180);

	public final static DecimalFormat df2 = new DecimalFormat("00");
	public final static DecimalFormat df3 = new DecimalFormat("000");

	public final static Color blu = new Color(105, 105, 105, 40);
	public final static Color bluQualy = new Color(105, 105, 205);

	private static final String CARREAGANDO = "CARREAGANDO";

	private RoundRectangle2D sobreRect = new RoundRectangle2D.Double(0, 0, 1,
			1, 10, 10);

	private RoundRectangle2D menuMatarRect = new RoundRectangle2D.Double(0, 0,
			1, 1, 10, 10);

	private RoundRectangle2D menuDemoRect = new RoundRectangle2D.Double(0, 0,
			1, 1, 10, 10);

	private RoundRectangle2D proximoMenuRect = new RoundRectangle2D.Double(0,
			0, 1, 1, 10, 10);

	private RoundRectangle2D anteriroMenuRct = new RoundRectangle2D.Double(0,
			0, 1, 1, 10, 10);

	private RoundRectangle2D desertoRct = new RoundRectangle2D.Double(0, 0, 1,
			1, 10, 10);

	private RoundRectangle2D cidadeRct = new RoundRectangle2D.Double(0, 0, 1,
			1, 10, 10);

	private RoundRectangle2D praiaRct = new RoundRectangle2D.Double(0, 0, 1, 1,
			10, 10);

	private RoundRectangle2D menosBotsRect = new RoundRectangle2D.Double(0, 0,
			1, 1, 10, 10);

	private RoundRectangle2D maisBotsRect = new RoundRectangle2D.Double(0, 0,
			1, 1, 10, 10);

	private RoundRectangle2D menosTempoRect = new RoundRectangle2D.Double(0, 0,
			1, 1, 10, 10);

	private RoundRectangle2D maisTempoRect = new RoundRectangle2D.Double(0, 0,
			1, 1, 10, 10);

	private RoundRectangle2D assautRect;
	private RoundRectangle2D shotgunRect;
	private RoundRectangle2D machineRect;
	private RoundRectangle2D shieldRect;
	private RoundRectangle2D rocketRect;
	private RoundRectangle2D sniperRect;

	private BufferedImage lifeBarShield;
	private BufferedImage lifeBarAssalt;
	private BufferedImage lifeBarMachineGun;
	private BufferedImage lifeBarShotgun;
	private BufferedImage lifeBarSniper;
	private BufferedImage lifeBarRocket;

	public BufferedImage assault;
	public BufferedImage shotgun;
	public BufferedImage machinegun;
	public BufferedImage sniper;
	public BufferedImage headShot;
	public BufferedImage rocket;
	public BufferedImage rocket_launcher;
	private BufferedImage riot_shield;
	private Thread renderThread = null;

	{
		assault = CarregadorRecursos.carregaBufferedImageTransparecia(
				"assault.png", null);
		machinegun = CarregadorRecursos.carregaBufferedImageTransparecia(
				"machinegun.png", null);
		shotgun = CarregadorRecursos.carregaBufferedImageTransparecia(
				"shotgun.png", null);
		sniper = CarregadorRecursos.carregaBufferedImageTransparecia(
				"sniper.png", null);
		rocket_launcher = CarregadorRecursos.carregaBufferedImageTransparecia(
				"rocket_launcher.png", null);
		riot_shield = CarregadorRecursos.carregaBufferedImageTransparecia(
				"riot-shield.png", null);

		lifeBarAssalt = ImageUtil.gerarFade(
				ImageUtil.geraResize(assault, 0.55, 0.4), FADE_MINIS);

		lifeBarMachineGun = ImageUtil.gerarFade(
				ImageUtil.geraResize(machinegun, 0.45, 0.27), FADE_MINIS);

		lifeBarShotgun = ImageUtil.gerarFade(
				ImageUtil.geraResize(shotgun, 0.6, 0.5), FADE_MINIS);

		lifeBarSniper = ImageUtil.gerarFade(
				ImageUtil.geraResize(sniper, 0.50, 0.4), FADE_MINIS);

		lifeBarRocket = ImageUtil.gerarFade(
				ImageUtil.geraResize(rocket_launcher, 0.42, 0.30), FADE_MINIS);

		lifeBarShield = ImageUtil.gerarFade(
				ImageUtil.geraResize(riot_shield, 0.75, 0.25), 255);

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

	private boolean desenhaCarregando = false;

	private List<String> creditos;

	public boolean renderThreadAlive = true;

	private Object MENU_ANTERIOR;

	private String mapaSelecionado = MAPA_DESERTO;

	private int numBotsSelecionado = 0;

	private int tempoJogoSelecionado = 10;

	private String classeSelecionada = ConstantesTopWar.ASSAULT;

	private int yCreditos;
	private int contMostraFPS;
	private int fps = 0;
	protected double fpsLimite = 60D;

	private boolean demo;

	public String getMapaSelecionado() {
		return mapaSelecionado;
	}

	public int getNumBotsSelecionado() {
		return numBotsSelecionado;
	}

	public int getTempoJogoSelecionado() {
		return tempoJogoSelecionado;
	}

	public String getClasseSelecionada() {
		return classeSelecionada;
	}

	public PainelMenu(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		inicializar();
	}

	public void inicializar() {
		MENU = MENU_PRINCIPAL;
		matarThreadRender();
		Logger.logar("matarThreadRender();");
		mainFrame.getFrameTopWar().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				processaClick(e);
				super.mouseClicked(e);
			}
		});
		Logger.logar("mainFrame.getFrameTopWar().addMouseListener");
		renderThread = new Thread(new Runnable() {
			@Override
			public void run() {
				int frames = 0;
				long startTime = System.currentTimeMillis();
				long lastTime = System.nanoTime();
				double nsPerTick = 1000000000D / 60D;
				double delta = 0;
				while (renderThreadAlive) {
					long now = System.nanoTime();

					delta += (now - lastTime) / nsPerTick;
					lastTime = now;
					boolean render = false;
					while (delta >= 1) {
						render = true;
						delta -= 1;
					}
					if (render) {
						render();
						PainelMenu.this.mainFrame.mostrarGraficos();
						++frames;
					}
					if ((System.currentTimeMillis() - startTime) > 1000) {
						startTime = System.currentTimeMillis();
						fps = frames;
						frames = 0;
						delta = 0;
					}

				}
			}
		});
		Logger.logar("renderThread = new Thread(new Runnable() {");
		iniciaRecursos();
		Logger.logar("iniciaRecursos()");
		renderThreadAlive = true;
		renderThread.start();
		Logger.logar("renderThread.start()");
		desenhaCarregando = false;
	}

	private void matarThreadRender() {
		renderThreadAlive = false;
		if (renderThread != null) {
			renderThread.interrupt();
		}
	}

	protected void processaClick(MouseEvent e) {
		if (MENU.equals(MENU_PRINCIPAL) && menuMatarRect.contains(e.getPoint())) {
			numBotsSelecionado = 29;
			MENU = MENU_JOGAR;
			return;
		}
		if (MENU.equals(MENU_PRINCIPAL) && menuDemoRect.contains(e.getPoint())) {
			numBotsSelecionado = 30;
			MENU = MENU_DEMO;
			return;
		}
		if (sobreRect.contains(e.getPoint())) {
			try {
				MENU = MENU_SOBRE;
				yCreditos = 0;
			} catch (Exception e1) {
				e1.printStackTrace();
				Logger.logarExept(e1);
			}
			return;
		}
		if (desertoRct.contains(e.getPoint())) {
			mapaSelecionado = MAPA_DESERTO;
			return;
		}
		if (cidadeRct.contains(e.getPoint())) {
			mapaSelecionado = MAPA_CIDADE;
			return;
		}
		if (praiaRct.contains(e.getPoint())) {
			mapaSelecionado = MAPA_PRAIA;
			return;
		}
		if (maisBotsRect.contains(e.getPoint())) {
			numBotsSelecionado++;
			Logger.logar("numBotsSelecionado++;");
			if (numBotsSelecionado > 50) {
				numBotsSelecionado = 50;
			}
		}

		if (menosBotsRect.contains(e.getPoint())) {
			numBotsSelecionado--;
			Logger.logar("numBotsSelecionado--;");
			if (numBotsSelecionado < 0) {
				numBotsSelecionado = 0;
			}
		}

		if (maisTempoRect.contains(e.getPoint())) {
			tempoJogoSelecionado += 10;
			if (tempoJogoSelecionado > 100) {
				tempoJogoSelecionado = 100;
			}
		}

		if (menosTempoRect.contains(e.getPoint())) {
			tempoJogoSelecionado -= 10;
			if (tempoJogoSelecionado < 10) {
				tempoJogoSelecionado = 10;
			}
		}

		if (assautRect.contains(e.getPoint())) {
			classeSelecionada = ConstantesTopWar.ASSAULT;
		}

		if (shieldRect.contains(e.getPoint())) {
			classeSelecionada = ConstantesTopWar.SHIELD;
		}

		if (shotgunRect.contains(e.getPoint())) {
			classeSelecionada = ConstantesTopWar.SHOTGUN;
		}

		if (machineRect.contains(e.getPoint())) {
			classeSelecionada = ConstantesTopWar.MACHINEGUN;
		}

		if (sniperRect.contains(e.getPoint())) {
			classeSelecionada = ConstantesTopWar.SNIPER;
		}

		if (rocketRect.contains(e.getPoint())) {
			classeSelecionada = ConstantesTopWar.ROCKET;
		}

		if (proximoMenuRect.contains(e.getPoint())) {
			proximoMenu();
			return;
		}
		if (anteriroMenuRct.contains(e.getPoint())) {
			anteriorMenu();
			return;
		}
	}

	private void iniciaRecursos() {
		bg = ImageUtil.gerarFade(
				CarregadorRecursos.carregaBackGround("mercs-chat.png"), 50);
		Logger.logar("bg = ImageUtil.gerarFade(	CarregadorRecursos.carregaBackGround(mercs-chat.png), 50);");

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				CarregadorRecursos.recursoComoStream("creditos.txt")));
		Logger.logar("BufferedReader reader = new BufferedReader(new InputStreamReader(CarregadorRecursos.recursoComoStream(creditos.txt))");
		
		creditos = new ArrayList<String>();
		try {
			String linha = reader.readLine();
			while (linha != null) {
				creditos.add(linha + "\n");
				linha = reader.readLine();
			}
		} catch (IOException e1) {
			Logger.logarExept(e1);
		}
	}

	protected void render() {
		try {
			if (!MENU.equals(MENU_ANTERIOR)) {
				MENU_ANTERIOR = MENU;
				resetaRects();
			}
			if (mainFrame == null) {
				return;
			}
			if (!mainFrame.getFrameTopWar().isVisible()) {
				return;
			}
			Graphics2D g2d = mainFrame.obterGraficos();
			setarHints(g2d);
			g2d.setColor(g2d.getBackground());
			g2d.fillRect(0, 0, getWidth(), getHeight());
			if (bg != null && PainelTopWar.desenhaImagens) {
				int centerX = mainFrame.getFrameTopWar().getWidth() / 2;
				int centerY = mainFrame.getFrameTopWar().getHeight() / 2;
				int bgX = bg.getWidth() / 2;
				int bgY = bg.getHeight() / 2;
				g2d.drawImage(bg, centerX - bgX, centerY - bgY, null);
			}
			if (desenhaCarregando) {
				desenhaCarregando(g2d);
				return;
			}
			desenhaMenuPrincipalSelecao(g2d);
			desenhaMenuJogar(g2d);
			desenhaMenuDemo(g2d);
			desenhaMenuSobre(g2d);
			desenhaFPS(g2d, getWidth() - 70, getHeight() - 50);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.logarExept(e);
		}

	}

	private void desenhaMenuDemo(Graphics2D g2d) {
		if (!MENU.equals(MENU_DEMO)) {
			return;
		}
		int x = (int) (getWidth() / 2);
		int y = (int) (getHeight() / 2);

		desenhaMapas(x - 150, y - 100, g2d);

		desenhaSeletorNumeroBots(x - 150, y - 40, g2d);

		desenhaSeletorTempoJogo(x - 150, y + 20, g2d);

		desenhaAnteriroProximo(g2d, x - 140, y + 215);
	}

	private void desenhaMenuJogar(Graphics2D g2d) {
		if (!MENU.equals(MENU_JOGAR)) {
			return;
		}
		int x = (int) (getWidth() / 2);
		int y = (int) (getHeight() / 2);

		desenhaMapas(x - 150, y - 100, g2d);

		desenhaSeletorNumeroBots(x - 150, y - 40, g2d);

		desenhaSeletorTempoJogo(x - 150, y + 20, g2d);

		desenhaSeletorClasse(x - 150, y + 100, g2d);

		desenhaAnteriroProximo(g2d, x - 140, y + 215);

	}

	private void desenhaMapas(int x, int y, Graphics2D g2d) {

		Font fontOri = g2d.getFont();
		g2d.setFont(new Font(fontOri.getName(), Font.BOLD, 28));

		int xOri = x;

		String desertoTxt = Lang.msg("deserto").toUpperCase();
		int tam = Util.calculaLarguraText(desertoTxt, g2d);
		desertoRct.setFrame(x - 15, y - 12, tam + 10, 32);
		g2d.setColor(lightWhite);
		g2d.fill(desertoRct);
		if (MAPA_DESERTO.equals(mapaSelecionado)) {
			g2d.setColor(yel);
			g2d.draw(desertoRct);
		}
		g2d.setColor(Color.BLACK);
		g2d.drawString(desertoTxt, x - 10, y + 15);

		x += (tam + 30);

		String cidadeTxt = Lang.msg("cidade").toUpperCase();
		tam = Util.calculaLarguraText(cidadeTxt, g2d);
		cidadeRct.setFrame(x - 15, y - 12, tam + 10, 32);
		g2d.setColor(lightWhite);
		g2d.fill(cidadeRct);
		if (MAPA_CIDADE.equals(mapaSelecionado)) {
			g2d.setColor(yel);
			g2d.draw(cidadeRct);
		}
		g2d.setColor(Color.BLACK);
		g2d.drawString(cidadeTxt, x - 10, y + 15);

		x += (tam + 30);

		// String praiaTxt = Lang.msg("praia").toUpperCase();
		// tam = Util.calculaLarguraText(praiaTxt, g2d);
		// praiaRct.setFrame(x - 15, y - 12, tam + 10, 32);
		// g2d.setColor(lightWhite);
		// g2d.fill(praiaRct);
		// if (MAPA_PRAIA.equals(mapaSelecionado)) {
		// g2d.setColor(yel);
		// g2d.draw(praiaRct);
		// }
		// g2d.setColor(Color.BLACK);
		// g2d.drawString(praiaTxt, x - 10, y + 15);
		g2d.setFont(fontOri);

	}

	private void desenhaMenuSobre(Graphics2D g2d) {
		if (!MENU.equals(MENU_SOBRE)) {
			return;
		}
		int x = (int) (getWidth() / 2);
		int y = (int) (getHeight() / 2);

		x -= 490;
		y -= 285;

		desenhaTextosCreditos(g2d, x + 150, 30);

		desenhaAnteriroProximo(g2d, x + 350, y + 500);

	}

	private void desenhaTextosCreditos(Graphics2D g2d, int x, int y) {
		Font fontOri = g2d.getFont();
		Font fontNegrito = new Font(fontOri.getName(), Font.BOLD, 10);
		Font fontMaior = new Font(fontOri.getName(), fontOri.getStyle(), 18);
		if (yCreditos == 0) {
			yCreditos = getHeight();
		}
		if (yCreditos > y) {
			yCreditos--;
		}

		int yDesenha = yCreditos;

		for (int i = 0; i < creditos.size(); i++) {
			String txt = creditos.get(i).toUpperCase();
			if (txt.startsWith("-")) {
				g2d.setFont(fontNegrito);
			} else {
				g2d.setFont(fontMaior);
			}
			g2d.setColor(Color.BLACK);
			g2d.drawString(txt, x + 5, yDesenha + 16);
			if (txt.startsWith("-")) {
				yDesenha += 25;
			} else {
				yDesenha += 20;
			}
			g2d.setFont(fontOri);
		}
		g2d.setFont(fontOri);
	}

	private int getHeight() {
		return mainFrame.getFrameTopWar().getHeight();
	}

	private int getWidth() {
		return mainFrame.getFrameTopWar().getWidth();
	}

	private void desenhaCarregando(Graphics2D g2d) {
		int x = (int) (getWidth() / 2);
		int y = (int) (getHeight() / 2);
		Font fontOri = g2d.getFont();
		g2d.setFont(new Font(fontOri.getName(), Font.BOLD, 28));
		g2d.setColor(lightWhite);
		String txt = Lang.msg("carregando").toUpperCase();
		int larguraTexto = Util.larguraTexto(txt, g2d);
		int desl = larguraTexto / 2;
		g2d.fillRoundRect(x - desl, y - 25, larguraTexto + 10, 30, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(txt, x - desl + 5, y);
		g2d.setFont(fontOri);

	}

	private void proximoMenu() {
		if (MENU.equals(MENU_JOGAR)) {
			demo = false;
			mainFrame.criarJogoLocal(this);
			MENU = CARREAGANDO;
			matarThreadRender();
			return;
		}
		if (MENU.equals(MENU_DEMO)) {
			demo = true;
			mainFrame.criarJogoLocal(this);
			MENU = CARREAGANDO;
			matarThreadRender();
			return;
		}
		if (MENU.equals(MENU_SOBRE)) {
			MENU = MENU_PRINCIPAL;
			return;
		}
	}

	private void anteriorMenu() {
		if (MENU.equals(MENU_JOGAR)) {
			MENU = MENU_PRINCIPAL;
			return;
		}
		if (MENU.equals(MENU_SOBRE)) {
			MENU = MENU_PRINCIPAL;
			return;
		}

	}

	public static void main(String[] args) throws IOException, Exception {
		int porcetNumVolta = Util.inte((55 - 12) * 1.66);
		System.out.println(porcetNumVolta);
	}

	private void resetaRects() {
		try {
			Map mapVo = BeanUtils.describe(this);
			for (Iterator iter = mapVo.keySet().iterator(); iter.hasNext();) {
				String propriedade = (String) iter.next();

				if (mapVo.keySet().contains(propriedade)) {
					Class propriedadeTipo = PropertyUtils.getPropertyType(this,
							propriedade);
					Object property = PropertyUtils.getProperty(this,
							propriedade);
					if (RoundRectangle2D.class.equals(propriedadeTipo)) {
						RoundRectangle2D rectangle2d = (RoundRectangle2D) property;
						rectangle2d.setFrame(0, 0, 1, 1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void desenhaAnteriroProximo(Graphics2D g2d, int x, int y) {
		Font fontOri = g2d.getFont();
		g2d.setFont(new Font(fontOri.getName(), Font.BOLD, 24));
		String anteriorTxt = Lang.msg("anterior").toUpperCase();
		int larguraTexto = Util.larguraTexto(anteriorTxt, g2d);
		anteriroMenuRct.setFrame(x, y - 25, larguraTexto + 10, 30);
		g2d.setColor(lightWhite);
		g2d.fill(anteriroMenuRct);
		g2d.setColor(Color.BLACK);
		g2d.drawString(anteriorTxt, x, y);
		x += (larguraTexto + 40);
		String proximoTxt = Lang.msg("proximo").toUpperCase();
		larguraTexto = Util.larguraTexto(proximoTxt, g2d);
		proximoMenuRect.setFrame(x, y - 25, larguraTexto + 10, 30);
		g2d.setColor(lightWhite);
		g2d.fill(proximoMenuRect);
		g2d.setColor(Color.BLACK);
		g2d.drawString(proximoTxt, x, y);
		g2d.setFont(fontOri);
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
		g2d.setStroke(new BasicStroke(2.0f));
	}

	private void desenhaMenuPrincipalSelecao(Graphics2D g2d) {
		if (!MENU.equals(MENU_PRINCIPAL)) {
			return;
		}
		int centerX = (int) (getWidth() / 2.3);
		int centerY = (int) (getHeight() / 2.5);

		centerX -= 300;

		Font fontOri = g2d.getFont();

		g2d.setFont(new Font(fontOri.getName(), Font.BOLD, 144));

		String txt = "TOP-WAR";
		int larguraTexto = Util.larguraTexto(txt, g2d);
		g2d.setColor(lightWhite);
		g2d.fillRoundRect(centerX, centerY - 120, larguraTexto + 10, 130, 15,
				15);
		g2d.setColor(Color.BLACK);
		g2d.drawString(txt, centerX + 5, centerY);

		centerX += 280;
		centerY += 70;

		g2d.setFont(new Font(fontOri.getName(), Font.BOLD, 28));

		g2d.setColor(lightWhite);
		txt = Lang.msg("buscarMatar").toUpperCase();
		larguraTexto = Util.larguraTexto(txt, g2d);
		menuMatarRect.setFrame(centerX, centerY - 25, larguraTexto + 10, 30);
		g2d.fill(menuMatarRect);
		g2d.setColor(Color.BLACK);
		g2d.drawString(txt, centerX + 5, centerY);

		centerY += 40;

		g2d.setColor(lightWhite);
		txt = Lang.msg("demo").toUpperCase();
		larguraTexto = Util.larguraTexto(txt, g2d);
		menuDemoRect.setFrame(centerX, centerY - 25, larguraTexto + 10, 30);
		g2d.fill(menuDemoRect);
		g2d.setColor(Color.BLACK);
		g2d.drawString(txt, centerX + 5, centerY);

		centerY += 40;

		g2d.setColor(lightWhite);
		txt = Lang.msg("sobre").toUpperCase();
		larguraTexto = Util.larguraTexto(txt, g2d);
		sobreRect.setFrame(centerX, centerY - 25, larguraTexto + 10, 30);
		g2d.fill(sobreRect);
		g2d.setColor(Color.BLACK);
		g2d.drawString(txt, centerX + 5, centerY);

		g2d.setFont(fontOri);
	}

	public RoundRectangle2D getSobreRect() {
		return sobreRect;
	}

	public void setSobreRect(RoundRectangle2D sobreRect) {
		this.sobreRect = sobreRect;
	}

	public RoundRectangle2D getProximoMenuRect() {
		return proximoMenuRect;
	}

	public void setProximoMenuRect(RoundRectangle2D proximoMenuRect) {
		this.proximoMenuRect = proximoMenuRect;
	}

	public RoundRectangle2D getAnteriroMenuRct() {
		return anteriroMenuRct;
	}

	public void setAnteriroMenuRct(RoundRectangle2D anteriroMenuRct) {
		this.anteriroMenuRct = anteriroMenuRct;
	}

	private void desenhaFPS(Graphics2D g2d, int x, int y) {
		String msg = "FPS";
		if (contMostraFPS >= 0 && contMostraFPS < 200) {

			msg = "  " + fps;
		} else if (contMostraFPS > 200) {
			contMostraFPS = -20;
		}
		contMostraFPS++;
		g2d.setColor(new Color(255, 255, 255, 100));
		g2d.fillRoundRect(x, y, 60, 35, 10, 10);
		Font fontOri = g2d.getFont();
		g2d.setFont(new Font(fontOri.getName(), Font.BOLD, 28));
		g2d.setColor(OcilaCor.porcentVerde100Vermelho0(Util.inte(fps * 1.6)));
		g2d.drawString(msg, x + 2, y + 26);
		g2d.setFont(fontOri);
	}

	private void desenhaSeletorNumeroBots(int x, int y, Graphics2D g2d) {
		Font fontOri = g2d.getFont();
		g2d.setFont(new Font(fontOri.getName(), Font.BOLD, 28));

		int xOri = x;

		String menos = "-";
		int tamMenos = Util.calculaLarguraText(menos, g2d);
		menosBotsRect.setFrame(x - 16, y - 6, tamMenos + 6, 22);
		g2d.setColor(lightWhite);
		g2d.fill(menosBotsRect);
		g2d.setColor(Color.BLACK);
		g2d.drawString(menos, x - 14, y + 15);

		x += 20;

		String numBotsStr = (df2.format(numBotsSelecionado) + " " + Lang
				.msg("bots")).toUpperCase();
		int tamBots = Util.calculaLarguraText(numBotsStr, g2d);
		g2d.setColor(lightWhite);
		g2d.fillRoundRect(x - 15, y - 12, tamBots + 10, 32, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(numBotsStr, x - 10, y + 15);

		x += (tamBots + 15);

		String mais = "+";
		int tamMais = Util.calculaLarguraText(mais, g2d);
		maisBotsRect.setFrame(x - 17, y - 6, tamMais + 5, 22);
		g2d.setColor(lightWhite);
		g2d.fill(maisBotsRect);
		g2d.setColor(Color.BLACK);
		g2d.drawString(mais, x - 13, y + 16);

		g2d.setFont(fontOri);

		int porcetNumBots = Util.inte((numBotsSelecionado) * 2);

		int tamNumBotsSelecionado = porcetNumBots * (tamBots + 10) / 100;
		x = xOri + 20;

		g2d.setColor(yel);
		g2d.drawRoundRect(x - 15, y - 12, tamNumBotsSelecionado, 32, 10, 10);
		g2d.setColor(blu);
		g2d.fillRoundRect(x - 15, y - 12, tamNumBotsSelecionado, 32, 10, 10);

	}

	private void desenhaSeletorTempoJogo(int x, int y, Graphics2D g2d) {
		Font fontOri = g2d.getFont();
		g2d.setFont(new Font(fontOri.getName(), Font.BOLD, 28));

		int xOri = x;

		String menos = "-";
		int tamMenos = Util.calculaLarguraText(menos, g2d);
		menosTempoRect.setFrame(x - 16, y - 6, tamMenos + 6, 22);
		g2d.setColor(lightWhite);
		g2d.fill(menosTempoRect);
		g2d.setColor(Color.BLACK);
		g2d.drawString(menos, x - 14, y + 15);

		x += 20;

		String tempoStr = (df3.format(tempoJogoSelecionado) + " " + Lang
				.msg("tempojogo")).toUpperCase();
		int tamTempo = Util.calculaLarguraText(tempoStr, g2d);
		g2d.setColor(lightWhite);
		g2d.fillRoundRect(x - 15, y - 12, tamTempo + 10, 32, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawString(tempoStr, x - 10, y + 15);

		x += (tamTempo + 15);

		String mais = "+";
		int tamMais = Util.calculaLarguraText(mais, g2d);
		maisTempoRect.setFrame(x - 17, y - 6, tamMais + 5, 22);
		g2d.setColor(lightWhite);
		g2d.fill(maisTempoRect);
		g2d.setColor(Color.BLACK);
		g2d.drawString(mais, x - 13, y + 16);

		g2d.setFont(fontOri);

		int porcetTempo = tempoJogoSelecionado;

		int tamTempoSelecionado = porcetTempo * (tamTempo + 10) / 100;
		x = xOri + 20;

		g2d.setColor(yel);
		g2d.drawRoundRect(x - 15, y - 12, tamTempoSelecionado, 32, 10, 10);
		g2d.setColor(blu);
		g2d.fillRoundRect(x - 15, y - 12, tamTempoSelecionado, 32, 10, 10);

	}

	private void desenhaSeletorClasse(int ox, int oy, Graphics2D graphics2d) {

		int x = ox;
		int y = oy;

		assautRect.setFrame(x, y, lifeBarAssalt.getWidth(),
				lifeBarAssalt.getHeight());
		graphics2d.setColor(PainelTopWar.transpBranco);
		graphics2d.fill(assautRect);
		if (ConstantesTopWar.ASSAULT.equals(classeSelecionada)) {
			graphics2d.setColor(Color.YELLOW);
		} else {
			graphics2d.setColor(Color.BLACK);
		}
		graphics2d.draw(assautRect);
		graphics2d.drawImage(lifeBarAssalt, null, x, y);

		x += 110;

		sniperRect.setFrame(x, y, lifeBarSniper.getWidth(),
				lifeBarSniper.getHeight());
		graphics2d.setColor(PainelTopWar.transpBranco);
		graphics2d.fill(sniperRect);
		if (ConstantesTopWar.SNIPER.equals(classeSelecionada)) {
			graphics2d.setColor(Color.YELLOW);
		} else {
			graphics2d.setColor(Color.BLACK);
		}
		graphics2d.draw(sniperRect);
		graphics2d.drawImage(lifeBarSniper, null, x, y);

		x += 110;

		machineRect.setFrame(x, y, lifeBarMachineGun.getWidth(),
				lifeBarMachineGun.getHeight());
		graphics2d.setColor(PainelTopWar.transpBranco);
		graphics2d.fill(machineRect);
		if (ConstantesTopWar.MACHINEGUN.equals(classeSelecionada)) {
			graphics2d.setColor(Color.YELLOW);
		} else {
			graphics2d.setColor(Color.BLACK);
		}
		graphics2d.draw(machineRect);
		graphics2d.drawImage(lifeBarMachineGun, null, x, y);

		x = ox;
		y += 30;
		shotgunRect.setFrame(x, y, lifeBarShotgun.getWidth(),
				lifeBarShotgun.getHeight());
		graphics2d.setColor(PainelTopWar.transpBranco);
		graphics2d.fill(shotgunRect);
		if (ConstantesTopWar.SHOTGUN.equals(classeSelecionada)) {
			graphics2d.setColor(Color.YELLOW);
		} else {
			graphics2d.setColor(Color.BLACK);
		}
		graphics2d.draw(shotgunRect);
		graphics2d.drawImage(lifeBarShotgun, null, x, y);

		x += 110;
		rocketRect.setFrame(x, y, lifeBarRocket.getWidth(),
				lifeBarRocket.getHeight());
		graphics2d.setColor(PainelTopWar.transpBranco);
		graphics2d.fill(rocketRect);
		if (ConstantesTopWar.ROCKET.equals(classeSelecionada)) {
			graphics2d.setColor(Color.YELLOW);
		} else {
			graphics2d.setColor(Color.BLACK);
		}
		graphics2d.draw(rocketRect);
		graphics2d.drawImage(lifeBarRocket, null, x, y);

		x += 110;
		shieldRect.setFrame(x, y, lifeBarShield.getWidth(),
				lifeBarShield.getHeight());
		if (ConstantesTopWar.SHIELD.equals(classeSelecionada)) {
			graphics2d.setColor(Color.YELLOW);
		} else {
			graphics2d.setColor(Color.BLACK);
		}
		graphics2d.draw(shieldRect);
		graphics2d.drawImage(lifeBarShield, null, x, y);

		int centerXAssaut = (int) assautRect.getCenterX();

		int centerXMachine = (int) machineRect.getCenterX();

		y = oy;

		graphics2d.setColor(Color.BLACK);
		graphics2d.drawLine(centerXAssaut, y, centerXAssaut, y - 20);
		graphics2d.drawLine(centerXMachine, y, centerXMachine, y - 20);

		graphics2d.drawLine(centerXAssaut, y - 20, centerXAssaut + 20, y - 20);
		graphics2d
				.drawLine(centerXMachine, y - 20, centerXMachine - 20, y - 20);
		graphics2d.setColor(ConstantesTopWar.lightWhite);

		graphics2d.fillRoundRect(centerXAssaut + 20, y - 27, 180, 15, 10, 10);
		graphics2d.setColor(Color.BLACK);
		graphics2d.drawString(Lang.msg("escolherclasse"), centerXAssaut + 30,
				y - 15);

	}

	public boolean isDemo() {
		return demo;
	}

	public void setDemo(boolean demo) {
		this.demo = demo;
	}

}
