package br.topwar.servidor;

import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

import br.nnpe.GeoUtil;
import br.nnpe.Logger;
import br.nnpe.NameGenerator;
import br.nnpe.Util;
import br.topwar.ConstantesTopWar;
import br.topwar.serial.ObjetoMapa;
import br.topwar.tos.AvatarTopWar;
import br.topwar.tos.BotInfo;
import br.topwar.tos.DadosAcaoClienteTopWar;

public class ControleBots {

	private JogoServidor jogoServidor;
	private Thread threadBots;
	private List<AvatarTopWar> bots = new ArrayList<AvatarTopWar>();

	public static void main(String[] args) throws IOException {
		NameGenerator nameGenerator = new NameGenerator("silabas");
		System.out.println(" ->  "
				+ nameGenerator.compose(Util.intervalo(2, 4)));

	}

	public ControleBots(JogoServidor jogoServidor) {
		this.jogoServidor = jogoServidor;
	}

	public void incializar() {
		if (threadBots != null) {
			threadBots.interrupt();
		}
		threadBots = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean interrupt = false;
				while (!jogoServidor.verificaFinalizado() && !interrupt) {
					List<AvatarTopWar> avatarTopWarsCopia = jogoServidor
							.getAvatarTopWarsCopia();
					synchronized (bots) {
						processarAcoesBots(avatarTopWarsCopia);
					}
					try {
						Thread.sleep(ConstantesTopWar.ATRASO_REDE_PADRAO_BOTS);
					} catch (InterruptedException e) {
						interrupt = true;
					}
				}

			}
		});
		threadBots.start();
	}

	protected void processarAcoesBots(List<AvatarTopWar> avatarTopWarsCopia) {
		for (Iterator iterator = bots.iterator(); iterator.hasNext();) {
			AvatarTopWar avatarTopWar = (AvatarTopWar) iterator.next();
			BotInfo botInfo = avatarTopWar.getBotInfo();
			boolean executouAcaoAtaque = false;
			/**
			 * Seguir/Atacar avatar inimigo
			 */
			for (Iterator iterator2 = avatarTopWarsCopia.iterator(); iterator2
					.hasNext();) {
				AvatarTopWar avatarTopWarCopia = (AvatarTopWar) iterator2
						.next();
				if (avatarTopWar.getTime().equals(avatarTopWarCopia.getTime())
						|| avatarTopWarCopia.getVida() <= 0) {
					continue;
				}

				List<Point> line = GeoUtil.drawBresenhamLine(
						avatarTopWar.getPontoAvatar(),
						avatarTopWarCopia.getPontoAvatar());
				if (jogoServidor.campoVisao(line, avatarTopWar, true)) {
					if (!BotInfo.ATACANDO.equals(botInfo.getEstado())) {
						botInfo.setPontoDestino(avatarTopWarCopia
								.getPontoAvatar());
					} else if ((avatarTopWar.getBalas() != 0 || avatarTopWar
							.getCartuchos() != 0)
							&& avatarTopWar.getArma() != ConstantesTopWar.ARMA_ASSALT) {
						jogoServidor.alternarFaca(avatarTopWar);
						executouAcaoAtaque = true;
					} else if (line.size() < 10
							&& avatarTopWar.getArma() == ConstantesTopWar.ARMA_FACA) {
						jogoServidor.atacar(avatarTopWar,
								avatarTopWar.getAngulo(), 0);
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
							jogoServidor.atacar(avatarTopWar,
									avatarTopWar.getAngulo(), line.size());
							executouAcaoAtaque = true;
						}
					} else {
						botInfo.setPontoDestino(avatarTopWarCopia
								.getPontoAvatar());
					}
					botInfo.setEstado(BotInfo.ATACANDO);
					break;
				}

			}
			if (!executouAcaoAtaque) {
				/**
				 * Patrulhando
				 */
				if (botInfo.getPontoDestino() == null) {
					if (botInfo.vaiGuia()) {
						List<ObjetoMapa> objetoMapaList = jogoServidor
								.getMapaTopWar().getObjetoMapaList();
						ArrayList<Point> canidatos = new ArrayList<Point>();
						for (Iterator iterator2 = objetoMapaList.iterator(); iterator2
								.hasNext();) {
							ObjetoMapa objetoMapa = (ObjetoMapa) iterator2
									.next();
							if (!ConstantesTopWar.BOT_GUIA.equals(objetoMapa
									.getEfeito())) {
								continue;
							}
							Point analizar = objetoMapa.getForma().getBounds()
									.getLocation();
							if (analizar.equals(botInfo.getUltimaGuia())) {
								continue;
							}
							List<Point> drawBresenhamLine = GeoUtil
									.drawBresenhamLine(
											avatarTopWar.getPontoAvatar(),
											analizar);
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
							botInfo.setPontoDestino(point);
							botInfo.setUltimaGuia(point);
						} else {
							botVaiPontoAleatorio(avatarTopWar, botInfo);
						}

					} else if (botInfo.vaiBaseInimiga()) {
						if (avatarTopWar.getTime() == ConstantesTopWar.TIME_VERMELHO) {
							botInfo.setPontoDestino(jogoServidor
									.getMapaTopWar().getPontoTimeAzul());
						} else {
							botInfo.setPontoDestino(jogoServidor
									.getMapaTopWar().getPontoTimeVermelho());
						}
					} else {
						botVaiPontoAleatorio(avatarTopWar, botInfo);
					}
					botInfo.setEstado(BotInfo.PATRULHANDO);
				}

				List<Point> lineMove = GeoUtil.drawBresenhamLine(
						avatarTopWar.getPontoAvatar(),
						botInfo.getPontoDestino());
				if (lineMove.size() < avatarTopWar.getVelocidade()) {
					botInfo.setPontoDestino(null);
				} else {
					Point dstMover = lineMove
							.get(avatarTopWar.getVelocidade() - 1);
					DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
					acaoClienteTopWar.setPonto(dstMover);
					acaoClienteTopWar.setAngulo(GeoUtil.calculaAngulo(
							avatarTopWar.getPontoAvatar(), dstMover, 90));
					String mover = (String) jogoServidor.moverPontoAvatar(
							avatarTopWar, acaoClienteTopWar);
					if (!ConstantesTopWar.OK.equals(mover)) {
						botInfo.setPontoDestino(null);
					}
				}
			}

		}
	}

	private void botVaiPontoAleatorio(AvatarTopWar avatarTopWar, BotInfo botInfo) {
		Point calculaPonto = GeoUtil.calculaPonto(Util.intervalo(0, 360),
				Util.intervalo(100, 200), avatarTopWar.getPontoAvatar());
		while (!jogoServidor.verificaAndavel(avatarTopWar.getPontoAvatar(),
				calculaPonto)) {
			calculaPonto = GeoUtil.calculaPonto(Util.intervalo(0, 360), 100,
					avatarTopWar.getPontoAvatar());
		}
		botInfo.setPontoDestino(calculaPonto);
	}

	public void adicionarBot() {
		try {
			NameGenerator nameGenerator = new NameGenerator("silabas");
			for (int i = 0; i < 23; i++) {
				String nome = nameGenerator.compose(Util.intervalo(2, 3));
				AvatarTopWar bot = jogoServidor.entrarNoJogo(nome);
				bot.setBotInfo(new BotInfo());
				synchronized (bots) {
					bots.add(bot);
				}
				Logger.logar("Adicionou " + bot.getNomeJogador());
			}
		} catch (IOException e) {
			Logger.logarExept(e);
		}
	}

}
