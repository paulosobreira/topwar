package br.topwar.editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import br.topwar.recursos.CarregadorRecursos;
import br.topwar.recursos.idiomas.Lang;

public class EditorMapa {
	private JFrame frame;
	private JMenuBar bar;
	private JMenu menuEditor;
	private boolean appletStand;
	private JPanel jPanel;
	private JScrollPane scrollPane;
	private BufferedImage backGround;
	private String backGroundName;

	public EditorMapa() {
		frame = new JFrame();
		jPanel = new JPanel() {
			protected void paintComponent(java.awt.Graphics g) {
				super.paintComponent(g);
				EditorMapa.this.paintComponent((Graphics2D) g);
			};

			public Dimension getPreferredSize() {
				if (backGround == null) {
					return super.getPreferredSize();
				}
				return new Dimension(backGround.getWidth(),
						backGround.getHeight());
			}
		};
		scrollPane = new JScrollPane(jPanel,
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
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
				jPanel.repaint();
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
				jPanel.repaint();
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
				jPanel.repaint();
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
				jPanel.repaint();
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
		JMenuItem carregarImg = new JMenuItem() {
			public String getText() {
				return Lang.msg("carregarImg");
			}

		};
		menuEditor.add(carregarImg);
		carregarImg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(
						CarregadorRecursos.class.getResource(
								"CarregadorRecursos.class").getFile());
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int result = fileChooser.showOpenDialog(null);

				if (result == JFileChooser.CANCEL_OPTION) {
					return;
				}

				File file = fileChooser.getSelectedFile();
				backGroundName = file.getName();
				backGround = CarregadorRecursos.carregaBackGround(
						backGroundName, jPanel);
			}
		});

	}

	public static void main(String[] args) {
		new EditorMapa();
	}

}
