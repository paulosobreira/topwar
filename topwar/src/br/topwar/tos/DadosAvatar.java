package br.topwar.tos;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DadosAvatar {

	private String time;
	private String nomeJogador;
	private int x;
	private int y;
	private double angulo;
	private long tempoUtlAtaque;
	private int vida;
	private int arma;
	private int rangeUtlDisparo;
	private boolean invencivel;

	public String encode() {
		return time + "!" + nomeJogador + "!" + x + "!" + y + "!" + angulo
				+ "!" + tempoUtlAtaque + "!" + vida + "!" + arma + "!"
				+ rangeUtlDisparo + "!" + (invencivel ? "1" : "0");
	}

	public void decode(String val) {
		String[] sp = val.split("!");
		time = sp[0];
		nomeJogador = sp[1];
		x = parseInt(sp[2]);
		y = parseInt(sp[3]);
		angulo = parseDouble(sp[4]);
		tempoUtlAtaque = parseLong(sp[5]);
		vida = parseInt(sp[6]);
		arma = parseInt(sp[7]);
		rangeUtlDisparo = parseInt(sp[8]);
		invencivel = "1".equals(sp[9]);
	}

	private double parseDouble(String string) {
		try {
			return Double.parseDouble(string);
		} catch (Exception e) {
		}
		return 0;
	}

	private long parseLong(String string) {
		try {
			return Long.parseLong(string);
		} catch (Exception e) {
		}
		return 0;
	}

	private int parseInt(String string) {
		try {
			return Integer.parseInt(string);
		} catch (Exception e) {
		}
		return 0;
	}

	public static String empacotaLista(Set<ObjTopWar> ret) {
		StringBuffer buffer = new StringBuffer();
		for (Iterator iterator = ret.iterator(); iterator.hasNext();) {
			ObjTopWar avatarTopWar = (ObjTopWar) iterator.next();
			DadosAvatar dadosAvatar = new DadosAvatar();
			dadosAvatar.time = avatarTopWar.getTime();
			dadosAvatar.nomeJogador = avatarTopWar.getNomeJogador();
			if (avatarTopWar.getPontoAvatar() != null) {
				dadosAvatar.x = avatarTopWar.getPontoAvatar().x;
				dadosAvatar.y = avatarTopWar.getPontoAvatar().y;
			}
			dadosAvatar.angulo = avatarTopWar.getAngulo();
			dadosAvatar.tempoUtlAtaque = avatarTopWar.getTempoUtlAtaque();
			dadosAvatar.vida = avatarTopWar.getVida();
			dadosAvatar.arma = avatarTopWar.getArma();
			dadosAvatar.rangeUtlDisparo = avatarTopWar.getRangeUtlDisparo();
			dadosAvatar.invencivel = avatarTopWar.isInvencivel();
			buffer.append(dadosAvatar.encode() + "@");
		}
		return buffer.toString();
	}

	public static HashSet<ObjTopWar> desEmpacotarLista(Object object) {
		HashSet<ObjTopWar> avatarTopWars = new HashSet<ObjTopWar>();
		String[] listStrs = object.toString().split("@");
		for (int i = 0; i < listStrs.length; i++) {
			DadosAvatar dadosAvatar = new DadosAvatar();
			dadosAvatar.decode(listStrs[i]);

			ObjTopWar avatarTopWar = new ObjTopWar();
			avatarTopWar.setTime(dadosAvatar.time);
			avatarTopWar.setNomeJogador(dadosAvatar.nomeJogador);
			avatarTopWar
					.setPontoAvatar(new Point(dadosAvatar.x, dadosAvatar.y));
			avatarTopWar.setAngulo(dadosAvatar.angulo);
			avatarTopWar.setTempoUtlAtaque(dadosAvatar.tempoUtlAtaque);
			avatarTopWar.setVida(dadosAvatar.vida);
			avatarTopWar.setArma(dadosAvatar.arma);
			avatarTopWar.setRangeUtlDisparo(dadosAvatar.rangeUtlDisparo);
			avatarTopWar.setInvencivel(dadosAvatar.invencivel);
			avatarTopWars.add(avatarTopWar);
		}
		return avatarTopWars;
	}

	public static void main(String[] args) {
		String teste = "asd@ZXc@QWE@q";
		String[] split = teste.split("@");
		for (int i = 0; i < split.length; i++) {
			System.out.println(split[i].length());
		}
	}
}
