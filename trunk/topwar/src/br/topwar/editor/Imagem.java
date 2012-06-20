package br.topwar.editor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import br.topwar.recursos.CarregadorRecursos;

public class Imagem {
	public static void main(String[] args) throws IOException {
		BufferedImage src = CarregadorRecursos
				.carregaBufferedImageTransparecia("azul_faca.png",
						Color.MAGENTA);
		double altura = src.getHeight() / 8.0;
		double largura = src.getWidth() / 4.0;
		BufferedImage bf = new BufferedImage(src.getWidth(), src.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) bf.getGraphics();
		graphics.drawImage(src, 0, 0, null);
		graphics.setColor(Color.MAGENTA);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 8; j++) {
				Rectangle2D rect = new Rectangle2D.Double(i * largura, j
						* altura, largura, altura);
				graphics.draw(rect);
				// graphics.drawString("i=" + i + " j=" + j, rect.x, rect.y
				// + altura - 10);
			}
		}
		File file = new File("c:\\temp\\azul_novo.png");
		ImageIO.write(bf, "png", file);
		JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(bf)),
				"bf", JOptionPane.INFORMATION_MESSAGE);
	}
}
