package br.topwar.serial;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import br.nnpe.GeoUtil;

public class ObjetoMapa implements Serializable {

	private Color corPimaria;
	private Color corSecundaria;
	private int transparencia;
	private double angulo;
	private Shape forma;

	public ObjetoMapa(List<Point> pontos) {
		Polygon polygon = new Polygon();
		for (Iterator iterator = pontos.iterator(); iterator.hasNext();) {
			Point point = (Point) iterator.next();
			polygon.addPoint(point.x, point.y);
		}
		forma = polygon;
	}

	public Shape getForma() {
		return forma;
	}

	public void setForma(Shape forma) {
		this.forma = forma;
	}

	public void mover(Point p) {
		Point center = new Point((int) forma.getBounds().getCenterX(),
				(int) forma.getBounds().getCenterY());
		AffineTransform affineTransform = AffineTransform
				.getScaleInstance(1, 1);
		GeneralPath generalPath = new GeneralPath(forma);
		generalPath.transform(affineTransform);
		int x = 0;
		int y = 0;
		if (center.x < p.x) {
			x = 2;
		}
		if (center.x > p.x) {
			x = -2;
		}
		if (center.y < p.y) {
			y = 2;
		}
		if (center.y > p.y) {
			y = -2;
		}
		affineTransform.setToTranslation(x, y);
		forma = generalPath.createTransformedShape(affineTransform);
	}

	public void desenha(Graphics2D g2d, double zoom) {

		// g2d.setColor(new Color(getCorPimaria().getRed(), getCorPimaria()
		// .getGreen(), getCorPimaria().getBlue(), getTransparencia()));
		// double rad = Math.toRadians((double) getAngulo());
		// AffineTransform affineTransform = AffineTransform
		// .getScaleInstance(1, 1);
		// affineTransform.setToRotation(rad, polygon.getBounds().getCenterX(),
		// polygon.getBounds().getCenterY());
		// GeneralPath generalPath = new GeneralPath(polygon);
		// generalPath.transform(affineTransform);
		// affineTransform.setToScale(zoom, zoom);
		// g2d.fill(generalPath.createTransformedShape(affineTransform));
	}

	public double getAngulo() {
		return angulo;
	}

	public void setAngulo(double angulo) {
		this.angulo = angulo;
	}

	public Color getCorPimaria() {
		return corPimaria;
	}

	public void setCorPimaria(Color corPimaria) {
		this.corPimaria = corPimaria;
	}

	public Color getCorSecundaria() {
		return corSecundaria;
	}

	public void setCorSecundaria(Color corSecundaria) {
		this.corSecundaria = corSecundaria;
	}

	public int getTransparencia() {
		return transparencia;
	}

	public void setTransparencia(int transparencia) {
		if (transparencia < 0) {
			transparencia = 0;
		}
		if (transparencia > 255) {
			transparencia = 255;
		}
		this.transparencia = transparencia;
	}

}
