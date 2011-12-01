package br.topwar.editor;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
import br.nnpe.Util;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.recursos.idiomas.Lang;
import br.topwar.serial.MapaTopWar;

public class EditorMapa {
	private JFrame frame;
	private JMenuBar bar;
	private JMenu menuEditor;
	private boolean appletStand;
	private JPanel painelEditor;
	private JScrollPane scrollPane;
	private BufferedImage backGround;
	private String backGroundName;
	private MapaTopWar mapaTopWar;

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
				}
			}
		});
		criarMenu();
		frame.pack();
		frame.setVisible(true);
		frame.setSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	protected void baixo() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Point p = scrollPane.getViewport().getViewPosition();
				p.y += 40;
				System.out.println("p.y " + p.y);
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
	}

	private void criarMenu() {
		bar = new JMenuBar();
		if (appletStand) {
			// applet.getRootPane().setMenuBar(bar);
		} else {
			frame.setJMenuBar(bar);
		}
		menuEditor = new JMenu() {
			public String getText() {
				return Lang.msg("editor");
			}

		};
		bar.add(menuEditor);
		JMenuItem novoMapa = new JMenuItem() {
			public String getText() {
				return Lang.msg("novoMapa");
			}

		};
		menuEditor.add(novoMapa);
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
		menuEditor.add(abrirMapa);
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
		menuEditor.add(salvarMapa);
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
