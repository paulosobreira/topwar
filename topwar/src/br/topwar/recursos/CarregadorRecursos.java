package br.topwar.recursos;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import br.nnpe.ImageUtil;
import br.nnpe.Logger;

public class CarregadorRecursos {
	private static Map bufferImages = new HashMap();

	public static URL carregarImagem(String imagem) {
		Logger.logar("carregarImagem " + imagem);
		return CarregadorRecursos.class.getResource(imagem);
	}

	public static BufferedImage carregaBufferedImageTranspareciaBranca(
			String file) {
		BufferedImage buffer = null;
		try {
			ImageIcon icon = new ImageIcon(
					CarregadorRecursos.class.getResource(file));
			buffer = ImageUtil.toBufferedImage(icon.getImage());
			if (buffer == null) {
				Logger.logar("img=" + buffer);
				System.exit(1);
			}

		} catch (Exception e) {
			Logger.logar("Erro gerando transparencia para :" + file);
			Logger.logarExept(e);
		}

		return ImageUtil.geraTransparencia(buffer);
	}

	public static BufferedImage carregaBufferedImage(String file) {
		BufferedImage buffer = null;
		try {
			ImageIcon icon = new ImageIcon(
					CarregadorRecursos.class.getResource(file));
			buffer = ImageUtil.toBufferedImage(icon.getImage());
			if (buffer == null) {
				Logger.logar("img=" + buffer);
				System.exit(1);
			}

		} catch (Exception e) {
			Logger.logar("Erro gerando transparencia para :" + file);
			Logger.logarExept(e);
		}

		return buffer;
	}

	public static BufferedImage carregaBufferedImageTranspareciaBranca(
			String file, int ingVal) {
		return carregaBufferedImageTranspareciaBranca(file, ingVal, 255);
	}

	public static BufferedImage carregaBufferedImageTranspareciaBranca(
			String file, int ingVal, int translucidez) {
		BufferedImage buffer = null;
		try {
			ImageIcon icon = new ImageIcon(
					CarregadorRecursos.class.getResource(file));
			buffer = ImageUtil.toBufferedImage(icon.getImage());
			if (buffer == null) {
				Logger.logar("img=" + buffer);
				System.exit(1);
			}

		} catch (Exception e) {
			Logger.logar("Erro gerando transparencia para :" + file);
			Logger.logarExept(e);
		}

		return ImageUtil.geraTransparencia(buffer, ingVal, translucidez);
	}

	public static BufferedImage carregaBufferedImageTranspareciaPreta(
			String file) {
		BufferedImage buffer = null;
		try {
			ImageIcon icon = new ImageIcon(
					CarregadorRecursos.class.getResource(file));
			buffer = ImageUtil.toBufferedImage(icon.getImage());
			if (buffer == null) {
				Logger.logar("img=" + buffer);
				System.exit(1);
			}

		} catch (Exception e) {
			Logger.logar("Erro gerando transparencia para :" + file);
			Logger.logarExept(e);
		}

		return ImageUtil.geraTransparencia(buffer, Color.BLACK);
	}

	public static BufferedImage carregaBackGround(String backGroundStr,
			JPanel panel) {
		ImageIcon icon = new ImageIcon(
				CarregadorRecursos.class.getResource(backGroundStr));
		BufferedImage backGround = ImageUtil.toBufferedImage(icon.getImage());
		panel.setSize(backGround.getWidth(), backGround.getHeight());

		if (backGround == null) {
			Logger.logar("backGround=" + backGround);
			System.exit(1);
		}
		return backGround;
	}

	public static InputStream recursoComoStream(String string) {
		CarregadorRecursos rec = new CarregadorRecursos();
		return rec.getClass().getResourceAsStream(string);
	}

	public InputStream recursoComoStreamIn(String string) {
		return this.getClass().getResourceAsStream(string);
	}

	public static void main(String[] args) throws URISyntaxException,
			IOException, ClassNotFoundException {
		// String val = "tn_2008voi-mclaren.gif";
		// System.out.println(Util.intervalo(0, 0));

		// gerarListaCarrosLado();
		// gerarCarrosCima();
		// JFrame frame = new JFrame();
		// frame.setSize(200, 200);
		// frame.setVisible(true);
		// Graphics2D graphics2d = (Graphics2D) frame.getContentPane()
		// .getGraphics();
		// BufferedImage gerarCorresCarros = gerarCorresCarros(Color.BLUE, 1);
		// graphics2d.drawImage(gerarCorresCarros, 0, 0, null);
		// CarregadorRecursos carregadorRecursos = new
		// CarregadorRecursos(false);
		// Properties properties = new Properties();
		//
		// properties.load(CarregadorRecursos
		// .recursoComoStream("properties/pistas.properties"));
		//
		// Enumeration propName = properties.propertyNames();
		// while (propName.hasMoreElements()) {
		// final String name = (String) propName.nextElement();
		// // System.out.println(name);
		// ObjectInputStream ois = new ObjectInputStream(carregadorRecursos
		// .getClass().getResourceAsStream(name));
		//
		// Circuito circuito = (Circuito) ois.readObject();
		// // System.out.println(properties.getProperty(name));
		// // System.out.println(circuito.getNome());
		// circuito.setMultiplicador(circuito.getMultiplciador() + 1);
		// FileOutputStream fileOutputStream = new FileOutputStream(new File(
		// name));
		// ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
		// oos.writeObject(circuito);
		// oos.flush();
		// fileOutputStream.close();
		// }

		BufferedImage travadaRodaImg = CarregadorRecursos
				.carregaBufferedImageTranspareciaBranca("travadaRoda.png", 200,
						50);
		JOptionPane.showConfirmDialog(null, new JLabel(new ImageIcon(
				travadaRodaImg)));
	}

	private static void gerarListaCarrosLado() throws IOException {
		List carList = new LinkedList();
		File file = new File("src/sowbreira/f1mane/recursos/carros");
		File[] dir = file.listFiles();
		for (int i = 0; i < dir.length; i++) {
			if (!dir[i].getName().startsWith(".")) {
				File[] imgCar = dir[i].listFiles();
				for (int j = 0; j < imgCar.length; j++) {
					if (!imgCar[j].getName().startsWith(".")
							&& !imgCar[j].getName().equals("Thumbs.db")) {
						String str = imgCar[j].getPath().split("recursos")[1];
						str = str.substring(1, str.length());
						carList.add(str);

					}
				}
			}
		}
		FileWriter fileWriter = new FileWriter(
				"src/sowbreira/f1mane/recursos/carlist.txt");
		for (Iterator iterator = carList.iterator(); iterator.hasNext();) {
			String carro = (String) iterator.next();
			StringBuffer nCarro = new StringBuffer();
			for (int i = 0; i < carro.length(); i++) {
				if (carro.charAt(i) == '\\') {
					nCarro.append('/');
				} else {
					nCarro.append(carro.charAt(i));
				}
			}
			Logger.logar(nCarro.toString());
			fileWriter.write(nCarro.toString() + "\n");
		}
		fileWriter.close();

	}

	public static BufferedImage carregaImgSemCache(String img) {
		ImageIcon icon = new ImageIcon(
				CarregadorRecursos.class.getResource(img));
		return ImageUtil.toBufferedImage(icon.getImage());
	}

	public static BufferedImage carregaImg(String img) {
		BufferedImage bufferedImage = (BufferedImage) bufferImages.get(img);
		if (bufferedImage != null) {
			return bufferedImage;
		}
		ImageIcon icon = new ImageIcon(
				CarregadorRecursos.class.getResource(img));
		bufferedImage = ImageUtil.toBufferedImage(icon.getImage());
		bufferImages.put(img, bufferedImage);
		return bufferedImage;
	}

	public static BufferedImage carregaBufferedImageMeiaTransparenciaBraca(
			String file) {
		BufferedImage buffer = null;
		try {
			ImageIcon icon = new ImageIcon(
					CarregadorRecursos.class.getResource(file));
			buffer = ImageUtil.toBufferedImage(icon.getImage());
			if (buffer == null) {
				Logger.logar("img=" + buffer);
				System.exit(1);
			}

		} catch (Exception e) {
			Logger.logar("Erro gerando transparencia para :" + file);
			Logger.logarExept(e);
		}
		ImageIcon img = new ImageIcon(buffer);
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
				if (c.getRed() > 250 && c.getGreen() > 250 && c.getBlue() > 250) {
					argbArray[3] = 0;
				} else {
					argbArray[3] = 100;
				}
				destRaster.setPixel(i, j, argbArray);
			}
		}

		return bufferedImageRetorno;
	}

	public static BufferedImage carregaBufferedImageTransparecia(String file,
			Color cor) {
		BufferedImage buffer = null;
		try {
			ImageIcon icon = new ImageIcon(
					CarregadorRecursos.class.getResource(file));
			buffer = ImageUtil.toBufferedImage(icon.getImage());
			if (buffer == null) {
				Logger.logar("img=" + buffer);
				System.exit(1);
			}

		} catch (Exception e) {
			Logger.logar("Erro gerando transparencia para :" + file);
			Logger.logarExept(e);
		}
		ImageIcon img = new ImageIcon(buffer);
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
				if (c.equals(cor)) {
					argbArray[3] = 0;
				}
				destRaster.setPixel(i, j, argbArray);
			}
		}

		return bufferedImageRetorno;
	}

}
