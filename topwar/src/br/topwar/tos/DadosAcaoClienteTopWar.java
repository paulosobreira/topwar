package br.topwar.tos;

import java.io.Serializable;

public class DadosAcaoClienteTopWar implements Serializable {

	private String nomeCliente;
	private String moverPara;
	private double angulo;

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
