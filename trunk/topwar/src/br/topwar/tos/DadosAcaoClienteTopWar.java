package br.topwar.tos;

import java.awt.Point;
import java.io.Serializable;

public class DadosAcaoClienteTopWar implements Serializable {

	private String nomeCliente;
	private String moverPara;
	private Point ponto;
	private int range;
	private double angulo;

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public Point getPonto() {
		return ponto;
	}

	public void setPonto(Point ponto) {
		this.ponto = ponto;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public double getAngulo() {
		return angulo;
	}

	public void setAngulo(double angulo) {
		this.angulo = angulo;
	}

	public String getMoverPara() {
		return moverPara;
	}

	public void setMoverPara(String moverPara) {
		this.moverPara = moverPara;
	}

}
