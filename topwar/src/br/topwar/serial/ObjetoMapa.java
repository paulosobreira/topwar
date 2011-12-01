package br.topwar.serial;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;

public abstract class ObjetoMapa implements Serializable {

	private boolean pintaEmcima;
	private Color corPimaria;
	private Color corSecundaria;
	private int transparencia;
	private int altura;
	private int largura;
	private double angulo;
	private Point posicaoQuina;
	private String nome;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String toString() {
		return getNome() + " " + getClass().getSimpleName();
	}

	public int getAltura() {
		return altura;
	}

	public void setAltura(int altura) {
		this.altura = altura;
	}

	public int getLargura() {
		return largura;
	}

	public void setLargura(int largura) {
		this.largura = largura;
	}

	public Point getPosicaoQuina() {
		return posicaoQuina;
	}

	public void setPosicaoQuina(Point posicaoQuina) {
		this.posicaoQuina = posicaoQuina;
	}

	public abstract void desenha(Graphics2D g2d, double zoom);

	public abstract Rectangle obterArea();

	public double getAngulo() {
		return angulo;
	}

	public void setAngulo(double angulo) {
		this.angulo = angulo;
	}

	public boolean isPintaEmcima() {
		return pintaEmcima;
	}

	public void setPintaEmcima(boolean pintaEmcima) {
		this.pintaEmcima = pintaEmcima;
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
