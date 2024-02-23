package br.topwar.tos;

import java.io.Serializable;

public class EventoJogo implements Serializable {

	private String atacante;
	private String timeAtacante;
	private String morto;
	private String timeMorto;
	private int arma;
	private long tempo;

	public String getTimeAtacante() {
		return timeAtacante;
	}

	public void setTimeAtacante(String timeAtacante) {
		this.timeAtacante = timeAtacante;
	}

	public String getTimeMorto() {
		return timeMorto;
	}

	public void setTimeMorto(String timeMorto) {
		this.timeMorto = timeMorto;
	}

	public String getAtacante() {
		return atacante;
	}

	public String getMorto() {
		return morto;
	}

	public void setMorto(String morto) {
		this.morto = morto;
	}

	public void setAtacante(String atacante) {
		this.atacante = atacante;
	}

	public int getArma() {
		return arma;
	}

	public void setArma(int arma) {
		this.arma = arma;
	}

	public long getTempo() {
		return tempo;
	}

	public void setTempo(long tempo) {
		this.tempo = tempo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (tempo ^ (tempo >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventoJogo other = (EventoJogo) obj;
		if (tempo != other.tempo)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EventoJogo [atacante=" + atacante + ", morto=" + morto
				+ ", arma=" + arma + ", tempo=" + tempo + "]";
	}

}
