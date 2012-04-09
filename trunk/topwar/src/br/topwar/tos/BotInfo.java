package br.topwar.tos;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.nnpe.GeoUtil;
import br.nnpe.Util;
import br.topwar.ConstantesTopWar;
import br.topwar.serial.ObjetoMapa;
import br.topwar.servidor.JogoServidor;

public class BotInfo {

	public static String PATRULHANDO = "PATRULHANDO";
	public static String ATACANDO = "ATACANDO";
	private int contPatrulha;
	private int contGuia;
	private Point ultimaGuia;
	private AvatarTopWar avatarTopWar;
	private JogoServidor jogoServidor;

	public BotInfo(AvatarTopWar bot, JogoServidor jogoServidor) {
		this.avatarTopWar = bot;
		this.jogoServidor = jogoServidor;
	}

	public void processaAcaoBot() {
		List<AvatarTopWar> avatarTopWarsCopia = jogoServidor
				.getAvatarTopWarsCopia();
		boolean executouAcaoAtaque = false;
		/**
		 * Seguir/Atacar avatar inimigo
		 */
		for (Iterator iterator2 = avatarTopWarsCopia.iterator(); iterator2
				.hasNext();) {
			AvatarTopWar avatarTopWarCopia = (AvatarTopWar) iterator2.next();
			if (avatarTopWar.getTime().equals(avatarTopWarCopia.getTime())
					|| avatarTopWarCopia.getVida() <= 0) {
				continue;
			}

			List<Point> line = GeoUtil.drawBresenhamLine(avatarTopWar
					.getPontoAvatar(), avatarTopWarCopia.getPontoAvatar());
			if (jogoServidor.campoVisao(line, avatarTopWar, true)) {
				if (!BotInfo.ATACANDO.equals(getEstado())) {
					setPontoDestino(avatarTopWarCopia.getPontoAvatar());
				} else if ((avatarTopWar.getBalas() != 0 || avatarTopWar
						.getCartuchos() != 0)
						&& avatarTopWar.getArma() != ConstantesTopWar.ARMA_ASSALT) {
					jogoServidor.alternarFaca(avatarTopWar);
					executouAcaoAtaque = true;
				} else if (line.size() < 10
						&& avatarTopWar.getArma() == ConstantesTopWar.ARMA_FACA) {
					jogoServidor.atacar(avatarTopWar, avatarTopWar.getAngulo(),
							0);
					executouAcaoAtaque = true;
				} else if (line.size() < ConstantesTopWar.MEIO_LIMITE_VISAO
						&& avatarTopWar.getArma() == ConstantesTopWar.ARMA_ASSALT) {
					if (avatarTopWar.getBalas() == 0) {
						if (avatarTopWar.getCartuchos() == 0) {
							jogoServidor.alternarFaca(avatarTopWar);
							executouAcaoAtaque = true;
						} else {
							jogoServidor.recarregar(avatarTopWar);
							executouAcaoAtaque = true;
						}
					} else {
						avatarTopWar.setAngulo(GeoUtil.calculaAngulo(
								avatarTopWar.getPontoAvatar(),
								avatarTopWarCopia.getPontoAvatar(), 90));
						jogoServidor.atacar(avatarTopWar, avatarTopWar
								.getAngulo(), line.size());
						executouAcaoAtaque = true;
					}
				} else {
					setPontoDestino(avatarTopWarCopia.getPontoAvatar());
				}
				setEstado(BotInfo.ATACANDO);
				break;
			}

		}
		if (!executouAcaoAtaque) {
			/**
			 * Patrulhando
			 */
			if (getPontoDestino() == null) {
				if (vaiGuia()) {
					List<ObjetoMapa> objetoMapaList = jogoServidor
							.getMapaTopWar().getObjetoMapaList();
					ArrayList<Point> canidatos = new ArrayList<Point>();
					for (Iterator iterator2 = objetoMapaList.iterator(); iterator2
							.hasNext();) {
						ObjetoMapa objetoMapa = (ObjetoMapa) iterator2.next();
						if (!ConstantesTopWar.BOT_GUIA.equals(objetoMapa
								.getEfeito())) {
							continue;
						}
						Point analizar = objetoMapa.getForma().getBounds()
								.getLocation();
						if (analizar.equals(getUltimaGuia())) {
							continue;
						}
						List<Point> drawBresenhamLine = GeoUtil
								.drawBresenhamLine(avatarTopWar
										.getPontoAvatar(), analizar);
						if (jogoServidor.campoVisao(drawBresenhamLine,
								avatarTopWar, true)
								&& drawBresenhamLine.size() > avatarTopWar
										.getVelocidade()) {
							canidatos.add(analizar);
						}
					}
					if (!canidatos.isEmpty()) {
						Collections.shuffle(canidatos);
						Point point = canidatos.get(0);
						setPontoDestino(point);
						setUltimaGuia(point);
					} else {
						botVaiPontoAleatorio();
					}

				} else if (vaiBaseInimiga()) {
					if (avatarTopWar.getTime() == ConstantesTopWar.TIME_VERMELHO) {
						setPontoDestino(jogoServidor.getMapaTopWar()
								.getPontoTimeAzul());
					} else {
						setPontoDestino(jogoServidor.getMapaTopWar()
								.getPontoTimeVermelho());
					}
				} else {
					botVaiPontoAleatorio();
				}
				setEstado(BotInfo.PATRULHANDO);
			}

			List<Point> lineMove = GeoUtil.drawBresenhamLine(avatarTopWar
					.getPontoAvatar(), getPontoDestino());
			if (lineMove.size() < avatarTopWar.getVelocidade()) {
				setPontoDestino(null);
			} else {
				Point dstMover = lineMove.get(avatarTopWar.getVelocidade() - 1);
				DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
				acaoClienteTopWar.setPonto(dstMover);
				acaoClienteTopWar.setAngulo(GeoUtil.calculaAngulo(avatarTopWar
						.getPontoAvatar(), dstMover, 90));
				String mover = (String) jogoServidor.moverPontoAvatar(
						avatarTopWar, acaoClienteTopWar);
				if (!ConstantesTopWar.OK.equals(mover)) {
					setPontoDestino(null);
				}
			}
		}

	}

	private void botVaiPontoAleatorio() {
		Point calculaPonto = GeoUtil.calculaPonto(Util.intervalo(0, 360), Util
				.intervalo(100, 200), avatarTopWar.getPontoAvatar());
		while (!jogoServidor.verificaAndavel(avatarTopWar.getPontoAvatar(),
				calculaPonto)) {
			calculaPonto = GeoUtil.calculaPonto(Util.intervalo(0, 360), 100,
					avatarTopWar.getPontoAvatar());
		}
		setPontoDestino(calculaPonto);
	}

	public Point getUltimaGuia() {
		return ultimaGuia;
	}

	public void setUltimaGuia(Point ultimaGuia) {
		this.ultimaGuia = ultimaGuia;
	}

	private String estado;

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	private Point pontoDestino;

	public Point getPontoDestino() {
		return pontoDestino;
	}

	public void setPontoDestino(Point pontoDestino) {
		this.pontoDestino = pontoDestino;
	}

	public boolean vaiGuia() {
		if (ultimaGuia == null) {
			return true;
		}
		if (contGuia < 1) {
			contGuia++;
			return false;
		}
		contGuia = 0;
		return true;
	}

	public boolean vaiBaseInimiga() {
		if (contPatrulha < 5) {
			contPatrulha++;
			return false;
		}
		contPatrulha = 0;
		return true;
	}

}
