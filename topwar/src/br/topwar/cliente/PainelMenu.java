package br.topwar.cliente;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import br.topwar.local.MainFrame;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.recursos.idiomas.Lang;

public class PainelMenu {

	public BufferedImage bg;

	public static String MENU_SOBRE = "MENU_SOBRE";

	public static String MENU_PRINCIPAL = "MENU_PRINCIPAL";

	public static String MENU_MATAR = "MENU_MATAR";

	private String MENU = MENU_PRINCIPAL;

	private MainFrame mainFrame;

	public final static Color lightWhite = new Color(200, 200, 200, 100);

	public final static Color lightWhite2 = new Color(255, 255, 255, 160);

	public final static Color yel = new Color(255, 255, 0, 150);

	public final static Color oran = new Color(255, 188, 40, 180);

	public final static Color blu = new Color(105, 105, 105, 40);
	public final static Color bluQualy = new Color(105, 105, 205);

	private RoundRectangle2D sobreRect = new RoundRectangle2D.Double(0, 0, 1,
			1, 10, 10);

	private RoundRectangle2D menuMatarRect = new RoundRectangle2D.Double(0, 0,
			1, 1, 10, 10);

	private RoundRectangle2D proximoMenuRect = new RoundRectangle2D.Double(0,
			0, 1, 1, 10, 10);

	private RoundRectangle2D anteriroMenuRct = new RoundRectangle2D.Double(0,
			0, 1, 1, 10, 10);

	private boolean desenhaCarregando = false;

	private List<String> creditos;

	public boolean renderThreadAlive = true;

	private Object MENU_ANTERIOR;

	private int yCreditos;
	private int contMostraFPS;
	private int fps = 0;
	protected double fpsLimite = 60D;

	public PainelMenu(MainFrame mainFrame) {
		this.mainFrame = mainFrame;

		mainFrame.getFrameTopWar().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				processaClick(e);
				super.mouseClicked(e);
			}
		});
		Thread renderThread = new Thread(new Runnable() {
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
		iniciaRecursos();
		renderThread.start();
		desenhaCarregando = false;

	}

	protected void processaClick(MouseEvent e) {
		if (MENU.equals(MENU_PRINCIPAL) && menuMatarRect.contains(e.getPoint())) {
			MENU = MENU_MATAR;
			return;
		}
		if (sobreRect.contains(e.getPoint())) {
			try {
				// mainFrame.mostraSobre();
				MENU = MENU_SOBRE;
				yCreditos = 0;
			} catch (Exception e1) {
				e1.printStackTrace();
				Logger.logarExept(e1);
			}
			return;
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
//		bg = ImageUtil.gerarFade(
//				CarregadorRecursos.carregaBackGround("mercs-chat.png"), 50);
		creditos = new ArrayList<String>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				CarregadorRecursos.recursoComoStream("creditos.txt")));
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
			if (bg != null) {
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
			desenhaMenuSobre(g2d);
			desenhaFPS(g2d, getWidth() - 70, getHeight() - 50);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.logarExept(e);
		}

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
		menuMatarRect.setFrame(x - desl, y - 25, larguraTexto + 10, 30);
		g2d.fill(menuMatarRect);
		g2d.setColor(Color.BLACK);
		g2d.drawString(txt, x - desl + 5, y);
		g2d.setFont(fontOri);

	}

	private void anteriorMenu() {
		if (MENU.equals(MENU_MATAR)) {
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

	private void proximoMenu() {
		if (MENU.equals(MENU_MATAR)) {
			return;
		}
		if (MENU.equals(MENU_SOBRE)) {
			MENU = MENU_PRINCIPAL;
			return;
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

		centerX += 300;
		centerY += 70;

		g2d.setFont(new Font(fontOri.getName(), Font.BOLD, 28));

		g2d.setColor(lightWhite);
		txt = Lang.msg("buscar e matar").toUpperCase();
		larguraTexto = Util.larguraTexto(txt, g2d);
		menuMatarRect.setFrame(centerX, centerY - 25, larguraTexto + 10, 30);
		g2d.fill(menuMatarRect);
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

}
