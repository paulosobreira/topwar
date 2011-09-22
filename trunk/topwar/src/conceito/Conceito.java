package conceito;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import br.nnpe.GeoUtil;

public class Conceito {

	protected static boolean rodando = true;
	protected static int velocidade = 3;
	final static int CIMA = KeyEvent.VK_W;
	final static int BAIXO = KeyEvent.VK_S;
	final static int ESQUERDA = KeyEvent.VK_A;
	final static int DIREIRA = KeyEvent.VK_D;
	protected static Set pressed = new HashSet();

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				rodando = false;
				super.windowClosing(e);
			}
		});
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		final BufferedImage img = CarregadorRecursos
				.carregaImg("CrackDown-Scene2-Act3.png");
		final Point p = new Point(150, 80);
		final Point m = new Point(0, 0);
		final AffineTransform afRotate = new AffineTransform();
		KeyAdapter keyAdapter = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				pressed.add(keyCode);
				move(keyCode);
				super.keyPressed(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();
				pressed.remove(keyCode);
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
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				m.x = e.getX();
				m.y = e.getY();
				super.mouseMoved(e);
			}
		};

		final JPanel panel = new JPanel() {
			protected void paintComponent(java.awt.Graphics g) {
				Graphics2D graphics2d = (Graphics2D) g;
				graphics2d.drawImage(img, null, 0, 0);
				graphics2d.setColor(Color.WHITE);
				Shape corpo = desenhaCorpo(p);
				double angulo = GeoUtil.calculaAngulo(p, m, 90);
				double rad = Math.toRadians((double) angulo);
				GeneralPath gpCorpo = new GeneralPath(corpo);
				afRotate.setToRotation(rad, gpCorpo.getBounds().getCenterX(),
						gpCorpo.getBounds().getCenterY());
				graphics2d.draw(gpCorpo.createTransformedShape(afRotate));
				graphics2d.setColor(Color.RED);
				Shape cabeca = desenhaCabeca(GeoUtil.calculaPonto(angulo, 6, p));
				GeneralPath gpCabeca = new GeneralPath(cabeca);
				afRotate.setToRotation(rad, gpCabeca.getBounds().getCenterX(),
						gpCabeca.getBounds().getCenterY());
				graphics2d.draw(gpCabeca.createTransformedShape(afRotate));
				graphics2d.setColor(Color.YELLOW);
				List linha = GeoUtil.drawBresenhamLine(p, m);
				for (Iterator iterator = linha.iterator(); iterator.hasNext();) {
					Point ptLinha = (Point) iterator.next();
					graphics2d.fillOval(ptLinha.x, ptLinha.y, 2, 2);
				}
			};
		};
		frame.getContentPane().add(panel);
		frame.setSize(img.getWidth(), img.getHeight());
		frame.addKeyListener(keyAdapter);
		panel.addMouseMotionListener(mouseAdapter);
		Thread threadRepaint = new Thread(new Runnable() {
			@Override
			public void run() {
				while (rodando) {
					panel.repaint();
					try {
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
					for (Iterator iterator = pressed.iterator(); iterator
							.hasNext();) {
						Integer key = (Integer) iterator.next();
						int keyCode = key.intValue();
						if (keyCode == KeyEvent.VK_A) {
							p.x = p.x - velocidade;
						}
						if (keyCode == KeyEvent.VK_S) {
							p.y = p.y + velocidade;
						}
						if (keyCode == KeyEvent.VK_D) {
							p.x = p.x + velocidade;
						}
						if (keyCode == KeyEvent.VK_W) {
							p.y = p.y - velocidade;
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

	protected static Shape desenhaCabeca(Point p) {
		return new Rectangle2D.Double(p.x - 3, p.y - 2, 6, 4);
	}

	protected static Shape desenhaCorpo(Point p) {
		return new Rectangle2D.Double(p.x - 8, p.y - 3, 16, 6);
	}
}
