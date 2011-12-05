package br.topwar.serial;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import br.nnpe.GeoUtil;

public class ObjetoMapa implements Serializable {

	private int transparencia = 125;
	private double angulo;
	private Shape forma;
	private double zoom;
	private String efeito;

	public String getEfeito() {
		return efeito;
	}

	public void setEfeito(String efeito) {
		this.efeito = efeito;
	}

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
		int value = (int) GeoUtil.distaciaEntrePontos(p, center);

		if (center.x < p.x) {
			x = value;
		}
		if (center.x > p.x) {
			x = -value;
		}
		if (center.y < p.y) {
			y = value;
		}
		if (center.y > p.y) {
			y = -value;
		}
		affineTransform.setToTranslation(x, y);
		forma = generalPath.createTransformedShape(affineTransform);
	}

	public void maisAngulo() {
		angulo = 1;
		girar();
	}

	public void menosAngulo() {
		angulo = -1;
		girar();
	}

	private void girar() {
		Point center = new Point((int) forma.getBounds().getCenterX(),
				(int) forma.getBounds().getCenterY());
		AffineTransform affineTransform = AffineTransform
				.getScaleInstance(1, 1);
		GeneralPath generalPath = new GeneralPath(forma);
		generalPath.transform(affineTransform);
		double rad = Math.toRadians((double) angulo);
		affineTransform.setToRotation(rad, center.x, center.y);
		forma = generalPath.createTransformedShape(affineTransform);
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

	public void maisTransparencia() {
		setTransparencia(getTransparencia() + 1);

	}

	public void menosTransparencia() {
		setTransparencia(getTransparencia() - 1);
	}

	public void menosZoom() {
		zoom = 0.99;
		esticar();
	}

	public void maisZoom() {
		zoom = 1.01;
		esticar();
	}

	private void esticar() {
		AffineTransform affineTransform = AffineTransform.getScaleInstance(
				zoom, zoom);
		GeneralPath generalPath = new GeneralPath(forma);
		generalPath.transform(affineTransform);
		if (zoom == 1.01) {
			affineTransform.setToTranslation(-7, -6);
		}
		if (zoom == 0.99) {
			affineTransform.setToTranslation(7, 6);
		}
		forma = generalPath.createTransformedShape(affineTransform);
	}

}
