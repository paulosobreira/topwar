package br.topwar.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.hibernate.id.GUIDGenerator;

import br.nnpe.ExampleFileFilter;
import br.nnpe.Util;
import br.topwar.ConstantesTopWar;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.recursos.idiomas.Lang;
import br.topwar.serial.MapaTopWar;
import br.topwar.serial.ObjetoMapa;

public class EditorMapa {
	private static final String NOVO_OBJETO = "NOVO_OBJETO";
	protected static final String PONTO_TIME_VERMELHO = "PONTO_TIME_VERMELHO";
	protected static final String PONTO_TIME_AZUL = "PONTO_TIME_AZUL";
	private JFrame frame;
	private JMenuBar bar;
	private JMenu menuMapa;
	private JMenu menuObjetos;
	private JMenu menuPontos;
	private boolean appletStand;
	private JPanel painelEditor;
	private JScrollPane scrollPane;
	private BufferedImage backGround;
	private String backGroundName;
	private MapaTopWar mapaTopWar;
	private String clickState;
	private List<Point> pontosNovoObj;
	private ObjetoMapa objetoMapaSelecionado;

	private Point pontoMouse = null;
	private boolean desenhaAvatar = false;

	public void setObjetoMapaSelecionado(ObjetoMapa objetoMapaSelecionado) {
		this.objetoMapaSelecionado = objetoMapaSelecionado;
	}

	public EditorMapa() {
		frame = new JFrame();
		painelEditor = new JPanel() {
			protected void paintComponent(java.awt.Graphics g) {
				super.paintComponent(g);
				EditorMapa.this.paintComponent((Graphics2D) g);
			};

			public Dimension getPreferredSize() {
				if (backGround == null) {
					return super.getPreferredSize();
				}
				return new Dimension(backGround.getWidth(), backGround
						.getHeight());
			}
		};
		scrollPane = new JScrollPane(painelEditor,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane);
		frame.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int keyCoode = e.getKeyCode();
				if (backGround == null) {
					return;
				}
				if (keyCoode == KeyEvent.VK_LEFT) {
					esquerda();
				} else if (keyCoode == KeyEvent.VK_RIGHT) {
					direita();
				} else if (keyCoode == KeyEvent.VK_UP) {
					cima();
				} else if (keyCoode == KeyEvent.VK_DOWN) {
					baixo();
				} else if (keyCoode == KeyEvent.VK_PAGE_UP) {
					maisAnguloObj();
				} else if (keyCoode == KeyEvent.VK_PAGE_DOWN) {
					menosAnguloObj();
				} else if (keyCoode == KeyEvent.VK_MINUS) {
					menosTransparenciaObj();
				} else if (keyCoode == KeyEvent.VK_EQUALS) {
					maisTransparenciaObj();
				} else if (keyCoode == KeyEvent.VK_INSERT) {
					novoObjeto();
				} else if (keyCoode == KeyEvent.VK_DELETE) {
					apagarObjeto();
				} else if (keyCoode == KeyEvent.VK_HOME) {
					maisZoomObj();
				} else if (keyCoode == KeyEvent.VK_END) {
					menosZoomObj();
				} else if (keyCoode == KeyEvent.VK_ESCAPE) {
					excluirUltimoNo();
				} else if (keyCoode == KeyEvent.VK_3) {
					efeitoGrade();
				} else if (keyCoode == KeyEvent.VK_W) {
					novoGuiaBot();
				} else if (keyCoode == KeyEvent.VK_SPACE) {
					desenhaAvatar = !desenhaAvatar;
				}
			}
		});
		criarMenu();
		frame.pack();
		frame.setVisible(true);
		frame.setSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		painelEditor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clickDoMouse(e);
				super.mouseClicked(e);
			}

		});
		painelEditor.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				arrastarDoMouse(e);
				super.mouseMoved(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (objetoMapaSelecionado == null
						&& !NOVO_OBJETO.equals(clickState)) {
					pontoMouse = e.getPoint();
					painelEditor.repaint();
				}
			}
		});
		frame.setTitle("TopWar - Editor de Mapas");
	}

	protected void efeitoGrade() {
		if (objetoMapaSelecionado == null) {
			return;
		}
		if (ConstantesTopWar.GRADE.equals(objetoMapaSelecionado.getEfeito())) {
			objetoMapaSelecionado.setEfeito(null);
		} else {
			objetoMapaSelecionado.setEfeito(ConstantesTopWar.GRADE);
		}
		painelEditor.repaint();

	}

	protected void excluirUltimoNo() {
		if (pontosNovoObj != null && !pontosNovoObj.isEmpty()) {
			pontosNovoObj.remove(pontosNovoObj.size() - 1);
			painelEditor.repaint();
		}

	}

	protected void menosZoomObj() {
		if (objetoMapaSelecionado == null) {
			return;
		}
		objetoMapaSelecionado.menosZoom();
		painelEditor.repaint();
	}

	protected void maisZoomObj() {
		if (objetoMapaSelecionado == null) {
			return;
		}
		objetoMapaSelecionado.maisZoom();
		painelEditor.repaint();
	}

	protected void apagarObjeto() {
		if (objetoMapaSelecionado == null) {
			return;
		}
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		for (Iterator iterator = objetoMapaList.iterator(); iterator.hasNext();) {
			ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
			if (objetoMapa.equals(objetoMapaSelecionado)) {
				iterator.remove();
				break;
			}
		}
		painelEditor.repaint();
	}

	protected void maisTransparenciaObj() {
		if (objetoMapaSelecionado == null) {
			return;
		}
		objetoMapaSelecionado.maisTransparencia();
		painelEditor.repaint();

	}

	protected void menosTransparenciaObj() {
		if (objetoMapaSelecionado == null) {
			return;
		}
		objetoMapaSelecionado.menosTransparencia();
		painelEditor.repaint();

	}

	protected void arrastarDoMouse(MouseEvent e) {
		Point point = e.getPoint();
		if (mapaTopWar == null) {
			return;
		}
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		if (objetoMapaSelecionado == null) {
			for (Iterator iterator = objetoMapaList.iterator(); iterator
					.hasNext();) {
				ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
				if (objetoMapa.getForma().contains(point)) {
					objetoMapaSelecionado = objetoMapa;
					break;
				}
			}
		}
		if (objetoMapaSelecionado != null) {
			objetoMapaSelecionado.mover(point);
			painelEditor.repaint();
		}
	}

	protected void clickDoMouse(MouseEvent e) {
		Point point = e.getPoint();
		if (NOVO_OBJETO.equals(clickState)) {
			if (MouseEvent.BUTTON1 == e.getButton()) {
				pontosNovoObj.add(e.getPoint());
			} else {
				if (pontosNovoObj != null && mapaTopWar != null) {
					ObjetoMapa objetoMapa = new ObjetoMapa(pontosNovoObj);
					mapaTopWar.getObjetoMapaList().add(objetoMapa);
					clickState = null;
					pontosNovoObj = null;
					objetoMapaSelecionado = objetoMapa;
				}
			}
		} else if (PONTO_TIME_AZUL.equals(clickState)) {
			mapaTopWar.setPontoTimeAzul(e.getPoint());
		} else if (PONTO_TIME_VERMELHO.equals(clickState)) {
			mapaTopWar.setPontoTimeVermelho(e.getPoint());
		} else if (ConstantesTopWar.BOT_GUIA.equals(clickState)) {
			ObjetoMapa objetoMapa = new ObjetoMapa();
			objetoMapa.setEfeito(ConstantesTopWar.BOT_GUIA);
			objetoMapa.setForma(new Ellipse2D.Double(point.x - 5, point.y - 5,
					10, 10));
			mapaTopWar.getObjetoMapaList().add(objetoMapa);
			clickState = null;
			objetoMapaSelecionado = objetoMapa;
		} else if (mapaTopWar != null) {
			List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
			for (Iterator iterator = objetoMapaList.iterator(); iterator
					.hasNext();) {
				ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
				if (objetoMapa.getForma().contains(point)) {
					objetoMapaSelecionado = objetoMapa;
					break;
				}
			}
		}
		if (MouseEvent.BUTTON1 != e.getButton()) {
			objetoMapaSelecionado = null;
		}
		painelEditor.repaint();

	}

	protected void baixo() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Point p = scrollPane.getViewport().getViewPosition();
				p.y += 40;
				if (p.y + scrollPane.getViewport().getHeight() > (backGround
						.getHeight())) {
					return;
				}
				painelEditor.repaint();
				scrollPane.getViewport().setViewPosition(p);

			}
		});
	}

	protected void cima() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Point p = scrollPane.getViewport().getViewPosition();
				p.y -= 40;
				if (p.y < 0) {
					return;
				}
				painelEditor.repaint();
				scrollPane.getViewport().setViewPosition(p);

			}
		});

	}

	protected void direita() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Point p = scrollPane.getViewport().getViewPosition();
				p.x += 40;
				if (p.x + scrollPane.getViewport().getWidth() > (backGround
						.getWidth())) {
					return;
				}
				painelEditor.repaint();
				scrollPane.getViewport().setViewPosition(p);

			}
		});

	}

	protected void esquerda() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Point p = scrollPane.getViewport().getViewPosition();
				p.x -= 40;
				if (p.x < 0) {
					return;
				}
				painelEditor.repaint();
				scrollPane.getViewport().setViewPosition(p);
			}
		});

	}

	protected void paintComponent(Graphics2D g2d) {
		if (backGround == null) {
			return;
		}
		Rectangle limitesViewPort = (Rectangle) limitesViewPort();
		// g2d.drawImage(backGround, 0, 0, null);
		if (pontosNovoObj != null) {
			Point ptAnt = null;
			for (Iterator iterator = pontosNovoObj.iterator(); iterator
					.hasNext();) {
				Point ptAtual = (Point) iterator.next();
				if (ptAnt != null) {
					g2d.setColor(Color.CYAN);
					g2d.drawLine(ptAnt.x, ptAnt.y, ptAtual.x, ptAtual.y);
				}
				ptAnt = ptAtual;
				g2d.setColor(Color.YELLOW);
				g2d.fillOval(ptAtual.x - 2, ptAtual.y - 2, 4, 4);
			}
		}
		g2d.setColor(Color.BLACK);
		if (mapaTopWar != null) {
			List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
			int cont = 0;
			for (int i = objetoMapaList.size() - 1; i >= 0; i--) {
				ObjetoMapa objetoMapa = (ObjetoMapa) objetoMapaList.get(i);
				if (objetoMapa.equals(objetoMapaSelecionado)) {
					g2d.setColor(Color.YELLOW);
				} else {
					g2d.setColor(Color.CYAN);
				}
				int x = objetoMapa.getForma().getBounds().x;
				int y = objetoMapa.getForma().getBounds().y;
				g2d.drawString("C:" + cont++, x, y);
				g2d.drawString("T:" + objetoMapa.getTransparencia(), x, y + 10);
				g2d.draw(objetoMapa.getForma());
				Color color = new Color(255, 255, 255, objetoMapa
						.getTransparencia());
				g2d.setColor(color);
				if (objetoMapa.getEfeito() == null) {
					g2d.fill(objetoMapa.getForma());
				} else {
					desenhaObjetoEfeito(g2d, objetoMapa);
				}

			}
			if (mapaTopWar.getPontoTimeAzul() != null) {
				g2d.setColor(ConstantesTopWar.lightBlu);
				g2d.fillOval(mapaTopWar.getPontoTimeAzul().x - 20, mapaTopWar
						.getPontoTimeAzul().y - 20, 40, 40);
			}
			if (mapaTopWar.getPontoTimeVermelho() != null) {
				g2d.setColor(ConstantesTopWar.lightRed);
				g2d.fillOval(mapaTopWar.getPontoTimeVermelho().x - 20,
						mapaTopWar.getPontoTimeVermelho().y - 20, 40, 40);
			}

		}

		if (desenhaAvatar && pontoMouse != null) {
			// BufferedImage imgJog = Conceito.mapImgs.get("azul-0-0");
			// Point desenha = new Point(pontoMouse.x - (imgJog.getWidth() / 2),
			// pontoMouse.y - (imgJog.getHeight() / 2));
			// Rectangle areaAvatar = new Rectangle(desenha.x, desenha.y,
			// imgJog.getWidth(), imgJog.getHeight());
			// imgJog = Conceito.processaTransparencia(imgJog, desenha,
			// areaAvatar, mapaTopWar);
			// imgJog = Conceito.processaGrade(imgJog, desenha, areaAvatar,
			// mapaTopWar);
			// g2d.drawImage(imgJog, desenha.x, desenha.y, null);
			// if (Conceito.verificaColisao(pontoMouse, areaAvatar, mapaTopWar))
			// {
			// g2d.setColor(Color.CYAN);
			// } else {
			// g2d.setColor(Color.MAGENTA);
			// }
			// g2d.draw(areaAvatar);
		}
		g2d.setColor(new Color(100, 100, 100, 150));
		g2d.fillRoundRect(limitesViewPort.x + 3, limitesViewPort.y + 8, 300,
				80, 10, 10);
		g2d.setColor(Color.WHITE);
		g2d.drawString("Transparencia = 0", limitesViewPort.x + 10,
				limitesViewPort.y + 20);
		g2d.drawString("Terreno Dificil(Ver, Anada e Atira) % = 1 -> 10",
				limitesViewPort.x + 10, limitesViewPort.y + 35);
		g2d.drawString("Estrutura (Ver e Atira) % = 11 -> 70",
				limitesViewPort.x + 10, limitesViewPort.y + 50);
		g2d.drawString("Estrutura Resistente (Ver Atraves) % = 71 -> 100",
				limitesViewPort.x + 10, limitesViewPort.y + 65);
		g2d.drawString("Estrutura Solida = maior que 100",
				limitesViewPort.x + 10, limitesViewPort.y + 80);
	}

	private void desenhaObjetoEfeito(Graphics2D g2d, ObjetoMapa objetoMapa) {
		if (ConstantesTopWar.GRADE.equals(objetoMapa.getEfeito())) {
			Rectangle bounds = objetoMapa.getForma().getBounds();

			BufferedImage bufferedImagePasso1 = new BufferedImage(bounds.width,
					bounds.height, BufferedImage.TYPE_INT_ARGB);

			Graphics2D graphics = (Graphics2D) bufferedImagePasso1
					.getGraphics();
			int inicioLinha = 0;
			int fimLinha = 0 + bounds.width;
			int inicioCol = 0;
			Color color = new Color(255, 255, 255, objetoMapa
					.getTransparencia());
			graphics.setColor(color);
			for (int i = 0; i < bounds.getHeight(); i++) {
				if (i % 2 == 0)
					graphics.drawLine(inicioLinha, inicioCol + i, fimLinha,
							inicioCol + i);
			}
			Shape forma = objetoMapa.getForma();
			AffineTransform affineTransform = AffineTransform.getScaleInstance(
					1, 1);
			GeneralPath generalPath = new GeneralPath(forma);
			generalPath.transform(affineTransform);
			affineTransform.setToTranslation(-bounds.x, -bounds.y);
			forma = generalPath.createTransformedShape(affineTransform);
			BufferedImage bufferedImagePasso2 = new BufferedImage(bounds.width,
					bounds.height, BufferedImage.TYPE_INT_ARGB);
			graphics = (Graphics2D) bufferedImagePasso2.getGraphics();
			graphics.setClip(forma);
			graphics.drawImage(bufferedImagePasso1, 0, 0, null);
			g2d.drawImage(bufferedImagePasso2, bounds.x, bounds.y, null);
		}

	}

	private void criarMenu() {
		bar = new JMenuBar();
		if (appletStand) {
			// applet.getRootPane().setMenuBar(bar);
		} else {
			frame.setJMenuBar(bar);
		}
		menuMapa = new JMenu() {
			public String getText() {
				return Lang.msg("menuMapa");
			}

		};
		bar.add(menuMapa);
		JMenuItem novoMapa = new JMenuItem() {
			public String getText() {
				return Lang.msg("novoMapa");
			}

		};
		menuMapa.add(novoMapa);
		novoMapa.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				novoMapa();
			}

		});
		JMenuItem abrirMapa = new JMenuItem() {
			public String getText() {
				return Lang.msg("abrirMapa");
			}

		};
		menuMapa.add(abrirMapa);
		abrirMapa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					abrirMapa();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		JMenuItem salvarMapa = new JMenuItem() {
			public String getText() {
				return Lang.msg("salvarMapa");
			}

		};
		menuMapa.add(salvarMapa);
		salvarMapa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					salvarMapa();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		JMenuItem testarMapa = new JMenuItem() {
			public String getText() {
				return Lang.msg("testarMapa");
			}

		};
		menuMapa.add(testarMapa);
		testarMapa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					testarMapa();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		menuObjetos = new JMenu() {
			public String getText() {
				return Lang.msg("menuObjetos");
			}

		};
		bar.add(menuObjetos);

		JMenuItem novoObj = new JMenuItem() {
			public String getText() {
				return Lang.msg("novoObj");
			}

		};
		novoObj.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				novoObjeto();
			}
		});
		menuObjetos.add(novoObj);

		JMenuItem apagarObj = new JMenuItem() {
			public String getText() {
				return Lang.msg("apagarObj");
			}

		};
		apagarObj.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				apagarObjeto();
			}
		});
		menuObjetos.add(apagarObj);

		JMenuItem maisAngulo = new JMenuItem() {
			public String getText() {
				return Lang.msg("maisAngulo");
			}

		};
		menuObjetos.add(maisAngulo);
		maisAngulo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				maisAnguloObj();
			}
		});
		JMenuItem menosAngulo = new JMenuItem() {
			public String getText() {
				return Lang.msg("menosAngulo");
			}

		};
		menuObjetos.add(menosAngulo);
		maisAngulo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menosAnguloObj();
			}
		});

		JMenuItem maisZoom = new JMenuItem() {
			public String getText() {
				return Lang.msg("maisZoom");
			}

		};
		menuObjetos.add(maisZoom);
		maisZoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				maisZoomObj();
			}
		});
		JMenuItem menosZoom = new JMenuItem() {
			public String getText() {
				return Lang.msg("menosZoom");
			}

		};
		menuObjetos.add(menosZoom);
		maisZoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menosZoomObj();
			}
		});
		JMenuItem maisTransparencia = new JMenuItem() {
			public String getText() {
				return Lang.msg("maisTransparencia");
			}

		};
		menuObjetos.add(maisTransparencia);
		maisTransparencia.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				maisTransparenciaObj();
			}
		});
		JMenuItem menosTransparencia = new JMenuItem() {
			public String getText() {
				return Lang.msg("menosTransparencia");
			}

		};
		menuObjetos.add(menosTransparencia);
		maisTransparencia.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menosTransparenciaObj();
			}
		});

		JMenuItem grade = new JMenuItem() {
			public String getText() {
				return Lang.msg("tpGrade");
			}

		};
		menuObjetos.add(grade);
		grade.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				efeitoGrade();
			}
		});

		JMenuItem excNo = new JMenuItem() {
			public String getText() {
				return Lang.msg("excNo");
			}

		};
		menuObjetos.add(excNo);
		excNo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				excluirUltimoNo();
			}
		});

		JMenuItem desAv = new JMenuItem() {
			public String getText() {
				return Lang.msg("desAv");
			}

		};
		menuObjetos.add(desAv);
		desAv.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				desenhaAvatar = !desenhaAvatar;
			}
		});

		JMenuItem listar = new JMenuItem() {
			public String getText() {
				return Lang.msg("listarObjs");
			}

		};
		menuObjetos.add(listar);
		listar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FormularioListaObjetos formularioListaObjetos = new FormularioListaObjetos(
						EditorMapa.this);
				formularioListaObjetos.mostrarPainel();
			}
		});

		menuPontos = new JMenu() {
			public String getText() {
				return Lang.msg("menupontos");
			}

		};
		bar.add(menuPontos);

		JMenuItem ptTimeVermelho = new JMenuItem() {
			public String getText() {
				return Lang.msg("ptTimeVermelho");
			}

		};
		menuPontos.add(ptTimeVermelho);
		ptTimeVermelho.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clickState = PONTO_TIME_VERMELHO;
			}
		});

		JMenuItem ptTimeAzul = new JMenuItem() {
			public String getText() {
				return Lang.msg("ptTimeAzul");
			}

		};
		menuPontos.add(ptTimeAzul);
		ptTimeAzul.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clickState = PONTO_TIME_AZUL;
			}
		});

		JMenuItem guiaBot = new JMenuItem() {
			public String getText() {
				return Lang.msg("guiaBot");
			}

		};
		menuPontos.add(guiaBot);
		guiaBot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				novoGuiaBot();
			}
		});

	}

	protected void novoGuiaBot() {
		clickState = ConstantesTopWar.BOT_GUIA;
		objetoMapaSelecionado = null;
		desenhaAvatar = false;
	}

	protected void testarMapa() throws IOException, ClassNotFoundException {
		Conceito conceito = new Conceito();
		conceito.incializa(mapaTopWar.getNome() + ".topwar",
				ConstantesTopWar.TIME_AZUL);

	}

	public JFrame getFrame() {
		return frame;
	}

	public MapaTopWar getMapaTopWar() {
		return mapaTopWar;
	}

	protected void maisAnguloObj() {
		if (objetoMapaSelecionado == null) {
			return;
		}
		objetoMapaSelecionado.maisAngulo();
		painelEditor.repaint();
	}

	protected void menosAnguloObj() {
		if (objetoMapaSelecionado == null) {
			return;
		}
		objetoMapaSelecionado.menosAngulo();
		painelEditor.repaint();
	}

	protected void novoObjeto() {
		clickState = NOVO_OBJETO;
		pontosNovoObj = new ArrayList<Point>();
		objetoMapaSelecionado = null;
		desenhaAvatar = false;
	}

	protected void salvarMapa() throws IOException {
		if (backGround == null) {
			return;
		}
		mapaTopWar.setLargura(backGround.getWidth());
		mapaTopWar.setAltura(backGround.getHeight());
		JFileChooser fileChooser = new JFileChooser(CarregadorRecursos.class
				.getResource("CarregadorRecursos.class").getFile());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		ExampleFileFilter exampleFileFilter = new ExampleFileFilter("topwar");
		fileChooser.setFileFilter(exampleFileFilter);
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		}
		File file = fileChooser.getSelectedFile();
		String fileName = file.getCanonicalFile().toString();
		if (!fileName.endsWith(".topwar")) {
			fileName += ".topwar";
		}
		file = new File(fileName);
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
		oos.writeObject(mapaTopWar);
		oos.flush();
		fileOutputStream.close();
	}

	protected void abrirMapa() throws IOException, ClassNotFoundException {
		JFileChooser fileChooser = new JFileChooser(CarregadorRecursos.class
				.getResource("CarregadorRecursos.class").getFile());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		ExampleFileFilter exampleFileFilter = new ExampleFileFilter("topwar");
		fileChooser.setFileFilter(exampleFileFilter);

		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		}

		FileInputStream inputStream = new FileInputStream(fileChooser
				.getSelectedFile());
		ObjectInputStream ois = new ObjectInputStream(inputStream);

		mapaTopWar = (MapaTopWar) ois.readObject();
		frame.setTitle(mapaTopWar.getNome());

		backGround = CarregadorRecursos.carregaBackGround(mapaTopWar
				.getBackGround(), painelEditor);
	}

	private void novoMapa() {
		String nomeMapa = JOptionPane.showInputDialog(frame, Lang
				.msg("nomeDoMapa"));
		if (Util.isNullOrEmpty(nomeMapa)) {
			JOptionPane.showMessageDialog(frame, Lang.msg("nomeInvalido"), Lang
					.msg("erro"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		JFileChooser fileChooser = new JFileChooser(CarregadorRecursos.class
				.getResource("CarregadorRecursos.class").getFile());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		}

		File file = fileChooser.getSelectedFile();
		backGroundName = file.getName();
		backGround = CarregadorRecursos.carregaBackGround(backGroundName,
				painelEditor);
		mapaTopWar = new MapaTopWar();
		mapaTopWar.setNome(nomeMapa);
		mapaTopWar.setBackGround(backGroundName);
		frame.setTitle(nomeMapa);
	}

	public static void main(String[] args) {
		new EditorMapa();
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

	public void excluirNo(ObjetoMapa excluir) {
		if (excluir == null) {
			return;
		}
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		for (Iterator iterator = objetoMapaList.iterator(); iterator.hasNext();) {
			ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
			if (objetoMapa.equals(excluir)) {
				iterator.remove();
				break;
			}
		}
	}

	public void atualizarObjs() {
		painelEditor.repaint();
	}

	public void centralizaObjSelecionado() {
		if (objetoMapaSelecionado == null) {
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				painelEditor.repaint();
				int x = objetoMapaSelecionado.getForma().getBounds().x
						- (scrollPane.getViewport().getWidth() / 2);
				int y = objetoMapaSelecionado.getForma().getBounds().y
						- (scrollPane.getViewport().getHeight() / 2);
				if (x < 0) {
					x = 0;
				}
				if (y < 0) {
					y = 0;
				}
				scrollPane.getViewport().setViewPosition(new Point(x, y));
			}
		});

	}
}
