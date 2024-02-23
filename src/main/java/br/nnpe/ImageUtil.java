package br.nnpe;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JApplet;

/**
 * @author Paulo Sobreira Criado Em 21/08/2005
 */
public class ImageUtil {

	public static BufferedImage geraTransparencia(BufferedImage src,
			Color color) {
		ImageIcon img = new ImageIcon(src);
		BufferedImage srcBufferedImage = new BufferedImage(img.getIconWidth(),
				img.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		srcBufferedImage.getGraphics().drawImage(img.getImage(), 0, 0, null);

		BufferedImage bufferedImageRetorno = new BufferedImage(
				img.getIconWidth(), img.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Raster srcRaster = srcBufferedImage.getData();
		WritableRaster destRaster = bufferedImageRetorno.getRaster();
		int[] argbArray = new int[4];

		for (int i = 0; i < img.getIconWidth(); i++) {
			for (int j = 0; j < img.getIconHeight(); j++) {
				argbArray = new int[4];
				argbArray = srcRaster.getPixel(i, j, argbArray);

				Color c = new Color(argbArray[0], argbArray[1], argbArray[2],
						argbArray[3]);

				if (color.equals(c)) {
					argbArray[3] = 0;
				}

				destRaster.setPixel(i, j, argbArray);
			}
		}

		return bufferedImageRetorno;
	}

	/**
	 * Serve pra nada essa porra!!!
	 * 
	 * @param image
	 * @return
	 */
	public static boolean hasAlpha(Image image) {
		// If buffered image, the color model is readily available
		if (image instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage) image;

			return bimage.getColorModel().hasAlpha();
		}

		// Use a pixel grabber to retrieve the image's color model;
		// grabbing a single pixel is usually sufficient
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);

		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		}

		// Get the image's color model
		ColorModel cm = pg.getColorModel();

		return cm.hasAlpha();
	}

	public static BufferedImage geraTransparencia(BufferedImage src) {
		return geraTransparencia(src, 250);
	}

	public static BufferedImage geraTransparencia(BufferedImage src,
			int ingVal) {
		return geraTransparencia(src, ingVal, 255);
	}

	public static BufferedImage geraTransparencia(BufferedImage src, int ingVal,
			int translucidez) {
		ImageIcon img = new ImageIcon(src);
		BufferedImage srcBufferedImage = new BufferedImage(img.getIconWidth(),
				img.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		srcBufferedImage.getGraphics().drawImage(img.getImage(), 0, 0, null);

		BufferedImage bufferedImageRetorno = new BufferedImage(
				img.getIconWidth(), img.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Raster srcRaster = srcBufferedImage.getData();
		WritableRaster destRaster = bufferedImageRetorno.getRaster();
		int[] argbArray = new int[4];

		for (int i = 0; i < img.getIconWidth(); i++) {
			for (int j = 0; j < img.getIconHeight(); j++) {
				argbArray = new int[4];
				argbArray = srcRaster.getPixel(i, j, argbArray);

				Color c = new Color(argbArray[0], argbArray[1], argbArray[2],
						argbArray[3]);
				if (c.getRed() > ingVal && c.getGreen() > ingVal
						&& c.getBlue() > ingVal) {
					argbArray[3] = 0;
				} else {
					argbArray[3] = translucidez;
				}

				destRaster.setPixel(i, j, argbArray);
			}
		}

		return bufferedImageRetorno;
	}

	public static BufferedImage processaSombra(BufferedImage imgJog) {

		BufferedImage novaImg = new BufferedImage(imgJog.getWidth(),
				imgJog.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = novaImg.createGraphics();
		AlphaComposite composite = AlphaComposite
				.getInstance(AlphaComposite.SRC_OUT, 1);
		g2d.drawImage(imgJog, 0, 0, null);
		g2d.setComposite(composite);
		g2d.fill(new Rectangle(0, 0, imgJog.getWidth(), imgJog.getHeight()));

		g2d.dispose();

		BufferedImage imgSombraProcessada = new BufferedImage(
				novaImg.getWidth(), novaImg.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Raster srcRaster = novaImg.getData();
		WritableRaster destRaster = imgSombraProcessada.getRaster();
		int[] argbArray = new int[4];

		for (int i = 0; i < imgJog.getWidth(); i++) {
			for (int j = 0; j < imgJog.getHeight(); j++) {
				argbArray = new int[4];
				argbArray = srcRaster.getPixel(i, j, argbArray);
				Color c = new Color(argbArray[0], argbArray[1], argbArray[2],
						argbArray[3]);
				if (argbArray[3] == 255) {
					argbArray[3] = 0;
				} else {
					argbArray[0] = 45;
					argbArray[1] = 45;
					argbArray[2] = 45;
					argbArray[3] = 100;
				}

				destRaster.setPixel(i, j, argbArray);
			}
		}
		novaImg = new BufferedImage(imgJog.getWidth(), imgJog.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		g2d = novaImg.createGraphics();
		AffineTransform sat = AffineTransform.getTranslateInstance(0, 0);
		sat.shear(.5, 0);
		g2d.transform(sat);
		g2d.drawImage(imgSombraProcessada, -10, 0, null);
		g2d.dispose();
		return novaImg;
	}

	public static BufferedImage gerarSubImagem(BufferedImage azul,
			Rectangle rect) {
		int largura = (int) rect.getWidth();
		int altura = (int) rect.getHeight();

		BufferedImage bufferedImageRetorno = new BufferedImage(largura, altura,
				BufferedImage.TYPE_INT_ARGB);
		Raster srcRaster = azul.getData();
		WritableRaster destRaster = bufferedImageRetorno.getRaster();
		int[] argbArray = new int[4];

		for (int i = 0; i < largura; i++) {
			for (int j = 0; j < altura; j++) {
				argbArray = new int[4];
				argbArray = srcRaster.getPixel(i + rect.x, j + rect.y,
						argbArray);
				destRaster.setPixel(i, j, argbArray);
			}
		}
		return bufferedImageRetorno;
	}

	public static BufferedImage gerarFade(BufferedImage src, int translucidez) {
		ImageIcon img = new ImageIcon(src);
		BufferedImage srcBufferedImage = new BufferedImage(img.getIconWidth(),
				img.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		srcBufferedImage.getGraphics().drawImage(img.getImage(), 0, 0, null);
		BufferedImage bufferedImageRetorno = new BufferedImage(
				img.getIconWidth(), img.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Raster srcRaster = srcBufferedImage.getData();
		WritableRaster destRaster = bufferedImageRetorno.getRaster();
		int[] argbArray = new int[4];

		for (int i = 0; i < img.getIconWidth(); i++) {
			for (int j = 0; j < img.getIconHeight(); j++) {
				argbArray = new int[4];
				argbArray = srcRaster.getPixel(i, j, argbArray);
				Color c = new Color(argbArray[0], argbArray[1], argbArray[2],
						argbArray[3]);
				if (argbArray[3] != 0)
					argbArray[3] = translucidez;
				destRaster.setPixel(i, j, argbArray);
			}
		}
		return bufferedImageRetorno;
	}

	public static BufferedImage geraResize(BufferedImage src, double fator) {
		return geraResize(src, fator, fator);
	}

	public static BufferedImage geraResize(BufferedImage src, double fatorx,
			double fatory) {
		AffineTransform afZoom = new AffineTransform();
		afZoom.setToScale(fatorx, fatory);
		BufferedImage dst = new BufferedImage(
				(int) Math.round(src.getWidth() * fatorx),
				(int) Math.round(src.getHeight() * fatory),
				BufferedImage.TYPE_INT_ARGB);
		AffineTransformOp op = new AffineTransformOp(afZoom,
				AffineTransformOp.TYPE_BILINEAR);
		op.filter(src, dst);
		return dst;
	}

	public static BufferedImage carregaImagemWebContent(String backGround,
			JApplet applet, String pathResouces) {
		if (applet == null) {
			Logger.logar("applet null ");
			return null;
		}
		URL url = null;
		try {
			String caminho = applet.getCodeBase() + pathResouces + backGround;
			Logger.logar("Caminho Carregar Bkg " + caminho);
			url = new URL(caminho);
			BufferedImage buff = ImageIO.read(url.openStream());
			return buff;
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		return null;
	}

	public static BufferedImage toCompatibleImage(BufferedImage image) {
		// obtain the current system graphical settings
		GraphicsConfiguration gfx_config = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();

		/*
		 * if image is already compatible and optimized for current system
		 * settings, simply return it
		 */
		if (image.getColorModel().equals(gfx_config.getColorModel()))
			return image;

		// image is not optimized, so create a new image that is
		BufferedImage new_image = gfx_config.createCompatibleImage(
				image.getWidth(), image.getHeight(), image.getTransparency());

		// get the graphics context of the new image to draw the old image on
		Graphics2D g2d = (Graphics2D) new_image.getGraphics();

		// actually draw the image and dispose of context no longer needed
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();

		// return the new optimized image
		return new_image;
	}

}
