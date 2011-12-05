package br.topwar.editor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Console;
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

import br.nnpe.ExampleFileFilter;
import br.nnpe.GeoUtil;
import br.nnpe.Util;
import br.topwar.Constantes;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.recursos.idiomas.Lang;
import br.topwar.serial.MapaTopWar;
import br.topwar.serial.ObjetoMapa;

public class EditorMapa {
	private static final String NOVO_OBJETO = "NOVO_OBJETO";
	private JFrame frame;
	private JMenuBar bar;
	private JMenu menuMapa;
	private boolean appletStand;
	private JPanel painelEditor;
	private JScrollPane scrollPane;
	private BufferedImage backGround;
	private String backGroundName;
	private MapaTopWar mapaTopWar;
	private String clickState;
	private List<Point> pontosNovoObj;
	private ObjetoMapa objetoMapaSelecionado;
	private JMenu menuObjetos;

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

			@Override
			public void mouseMoved(MouseEvent e) {

			}

		});
		painelEditor.addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				arrastarDoMouse(e);
				super.mouseMoved(e);
			}
		});
	}

	protected void efeitoGrade() {
		if (objetoMapaSelecionado == null) {
			return;
		}
		objetoMapaSelecionado.setEfeito(Constantes.GRADE);
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
				if (p.y + frame.getHeight() > (backGround.getHeight())) {
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
				if (p.x + frame.getWidth() > (backGround.getWidth())) {
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
		g2d.drawImage(backGround, 0, 0, null);
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
					g2d.setColor(Color.BLACK);
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
		}
	}

	private void desenhaObjetoEfeito(Graphics2D g2d, ObjetoMapa objetoMapa) {
		if (Constantes.GRADE.equals(objetoMapa.getEfeito())) {
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
	}

	protected void salvarMapa() throws IOException {
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

}
