package br.topwar.cliente;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import br.nnpe.ImageUtil;
import br.topwar.tos.AvatarTopWar;

public class AvatarCliente extends AvatarTopWar {

	public Map<String, BufferedImage> mapImgs = new HashMap<String, BufferedImage>();

	public AvatarCliente(String time, BufferedImage src) {
		setTime(time);
		gerarMapaImagens(src);
	}

	public void gerarMapaImagens(BufferedImage src) {
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
				String key = getTime() + "-" + i + "-" + j;
				mapImgs.put(key, bufferedImage);
				// JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(
				// mapImgs.get(key))), key,
				// JOptionPane.INFORMATION_MESSAGE);
			}
		}

		// JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(bf)),
		// "bf", JOptionPane.INFORMATION_MESSAGE);
	}

	public Map<String, BufferedImage> getMapImgs() {
		return mapImgs;
	}

	public void setMapImgs(Map<String, BufferedImage> mapImgs) {
		this.mapImgs = mapImgs;
	}

}
