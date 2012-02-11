package br.topwar.servidor;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javassist.bytecode.analysis.Analyzer;

import br.nnpe.GeoUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.tos.SessaoCliente;
import br.topwar.ConstantesTopWar;
import br.topwar.ProxyComandos;
import br.topwar.cliente.AvatarCliente;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.serial.MapaTopWar;
import br.topwar.serial.ObjetoMapa;
import br.topwar.tos.AvatarTopWar;
import br.topwar.tos.DadosAcaoClienteTopWar;
import br.topwar.tos.DadosJogoTopWar;

public class JogoServidor {
	private DadosJogoTopWar dadosJogoTopWar;
	private static MapaTopWar mapaTopWar;
	private ProxyComandos proxyComandos;
	private List<AvatarTopWar> avatarTopWars = new ArrayList<AvatarTopWar>();
	private Thread monitorJogo;
	private int ptsVermelho;
	private int ptsAzul;
	private boolean finalizado = false;
	private int tempoJogoMilis;
	private long inicioJogoMilis;
	private long fimJogoMilis;

	public JogoServidor(DadosJogoTopWar dadosJogoTopWar,
			ProxyComandos proxyComandos) {
		this.dadosJogoTopWar = dadosJogoTopWar;
		this.proxyComandos = proxyComandos;
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(
					CarregadorRecursos.recursoComoStream(dadosJogoTopWar
							.getNomeMapa() + ".topwar"));
			mapaTopWar = (MapaTopWar) ois.readObject();
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		AvatarTopWar avatarTopWar = new AvatarTopWar();
		avatarTopWar.setVida(ConstantesTopWar.VIDA_COMPLETA);
		avatarTopWar.setBalas(ConstantesTopWar.BALAS_ASSALT);
		avatarTopWar.setCartuchos(ConstantesTopWar.CARTUCHOS_ASSALT);
		if (Math.random() > .5) {
			avatarTopWar.setTime(ConstantesTopWar.TIME_VERMELHO);
			avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeVermelho());
		} else {
			avatarTopWar.setTime(ConstantesTopWar.TIME_AZUL);
			avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeAzul());
		}
		avatarTopWar.setNomeJogador(dadosJogoTopWar.getNomeJogador());
		avatarTopWars.add(avatarTopWar);
		int tempoJogoMinutos = 10;
		tempoJogoMilis = tempoJogoMinutos * 60 * 1000;
		inicioJogoMilis = System.currentTimeMillis();
		fimJogoMilis = inicioJogoMilis + tempoJogoMilis;

		monitorJogo = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!finalizado) {
					processaClicoJogoServidor();
					if (verificaFinalizado()) {
						finalizado = true;
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						Logger.logarExept(e);
						finalizado = true;
					}

				}

			}
		});
		monitorJogo.start();
	}

	public long tempoRestanteJogo() {
		long tempo = fimJogoMilis - System.currentTimeMillis();
		if (tempo < 0) {
			return 0;
		}
		return tempo;
	}

	public boolean isFinalizado() {
		return finalizado;
	}

	public void setFinalizado(boolean finalizado) {
		this.finalizado = finalizado;
	}

	protected void processaClicoJogoServidor() {
		synchronized (avatarTopWars) {
			for (Iterator iterator = avatarTopWars.iterator(); iterator
					.hasNext();) {
				AvatarTopWar avatarTopWar = (AvatarTopWar) iterator.next();
				long tempDesdeUltMorte = System.currentTimeMillis()
						- avatarTopWar.getUltimaMorte();
				if (avatarTopWar.getVida() <= 0 && tempDesdeUltMorte > 3000) {
					avatarTopWar.setVida(ConstantesTopWar.VIDA_COMPLETA);
					avatarTopWar.setBalas(ConstantesTopWar.BALAS_ASSALT);
					avatarTopWar
							.setCartuchos(ConstantesTopWar.CARTUCHOS_ASSALT);
					if (ConstantesTopWar.TIME_AZUL.equals(avatarTopWar
							.getTime())) {
						avatarTopWar.setPontoAvatar(mapaTopWar
								.getPontoTimeAzul());
					}
					if (ConstantesTopWar.TIME_VERMELHO.equals(avatarTopWar
							.getTime())) {
						avatarTopWar.setPontoAvatar(mapaTopWar
								.getPontoTimeVermelho());
					}
				}
			}
		}

	}

	public List<AvatarTopWar> getAvatarTopWars() {
		return avatarTopWars;
	}

	public Object atualizaListaAvatares(SessaoCliente sessaoCliente) {
		AvatarTopWar avatarTopWarJog = obterAvatarTopWar(sessaoCliente
				.getNomeJogador());
		if (avatarTopWarJog == null)
			return null;
		List<AvatarTopWar> ret = new ArrayList<AvatarTopWar>();
		synchronized (avatarTopWars) {
			for (Iterator iterator = avatarTopWars.iterator(); iterator
					.hasNext();) {
				AvatarTopWar avatarTopWar = (AvatarTopWar) iterator.next();
				if (avatarTopWar.equals(avatarTopWarJog)) {
					avatarTopWar
							.setUltimaRequisicao(System.currentTimeMillis());
					ret.add(avatarTopWar);
					continue;
				}
				/**
				 * Campo Visao Jogador Meia Lua
				 */
				Point back = GeoUtil.calculaPonto(
						avatarTopWarJog.getAngulo() + 180, 30,
						avatarTopWarJog.getPontoAvatar());
				Ellipse2D ellipse2d = new Ellipse2D.Double(back.x - 25,
						back.y - 25, 50, 50);

				List<Point> line = GeoUtil.drawBresenhamLine(
						avatarTopWarJog.getPontoAvatar(),
						avatarTopWar.getPontoAvatar());
				if (campoVisao(line, ellipse2d)) {
					ret.add(avatarTopWar);
				}
				/**
				 * Campo Audição Tiro 360
				 */
				if ((System.currentTimeMillis() - avatarTopWar
						.getTempoUtlDisparo()) < 300) {
					Point pontoTiro = avatarTopWar.getPontoUtlDisparo();
					line = GeoUtil.drawBresenhamLine(
							avatarTopWarJog.getPontoAvatar(), pontoTiro);
					if (campoVisao(line, null)) {
						ret.add(avatarTopWar);
					}
				}

			}
		}
		Map retorno = new HashMap();
		retorno.put(ConstantesTopWar.LISTA_AVATARES, ret);
		retorno.put(ConstantesTopWar.BALAS, avatarTopWarJog.getBalas());
		retorno.put(ConstantesTopWar.CARTUCHO, avatarTopWarJog.getCartuchos());
		retorno.put(ConstantesTopWar.RECARREGAR,
				verificaRecarregando(avatarTopWarJog));
		retorno.put(ConstantesTopWar.PTS_VERMELHO, getPtsVermelho());
		retorno.put(ConstantesTopWar.PTS_AZUL, getPtsAzul());
		retorno.put(ConstantesTopWar.TEMPO_JOGO_RESTANTE, tempoRestanteJogo());
		return retorno;
	}

	public int getPtsVermelho() {
		return ptsVermelho;
	}

	public int getPtsAzul() {
		return ptsAzul;
	}

	private boolean campoVisao(List<Point> line, Ellipse2D ellipse2d) {
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		for (Iterator iterator = line.iterator(); iterator.hasNext();) {
			Point point = (Point) iterator.next();
			if (ellipse2d != null
					&& ellipse2d.intersects(new Rectangle2D.Double(point.x,
							point.y, 1, 1))) {
				return false;
			}
			for (Iterator iterator2 = objetoMapaList.iterator(); iterator2
					.hasNext();) {
				ObjetoMapa objetoMapa = (ObjetoMapa) iterator2.next();
				if (!ConstantesTopWar.GRADE.equals(objetoMapa.getEfeito())
						&& objetoMapa.getTransparencia() > 50
						&& objetoMapa.getForma().contains(point)) {
					return false;
				}
			}
		}
		return true;
	}

	public DadosJogoTopWar getDadosJogoTopWar() {
		return dadosJogoTopWar;
	}

	public void setDadosJogoTopWar(DadosJogoTopWar dadosJogoTopWar) {
		this.dadosJogoTopWar = dadosJogoTopWar;
	}

	public String getNome() {
		return dadosJogoTopWar.getNomeJogo();
	}

	public String moverAvatar(AvatarTopWar avatarTopWar,
			DadosAcaoClienteTopWar acaoClienteTopWar) {
		Point novoPonto = new Point(avatarTopWar.getPontoAvatar().x,
				avatarTopWar.getPontoAvatar().y);
		if (ConstantesTopWar.ESQUERDA.equals(acaoClienteTopWar.getMoverPara())) {
			novoPonto.x = novoPonto.x - avatarTopWar.getVelocidade();
		}
		if (ConstantesTopWar.BAIXO.equals(acaoClienteTopWar.getMoverPara())) {
			novoPonto.y = novoPonto.y + avatarTopWar.getVelocidade();
		}
		if (ConstantesTopWar.DIREITA.equals(acaoClienteTopWar.getMoverPara())) {
			novoPonto.x = novoPonto.x + avatarTopWar.getVelocidade();
		}
		if (ConstantesTopWar.CIMA.equals(acaoClienteTopWar.getMoverPara())) {
			novoPonto.y = novoPonto.y - avatarTopWar.getVelocidade();
		}
		if (verificaColisao(novoPonto, mapaTopWar)) {
			return null;
		}
		avatarTopWar.setPontoAvatar(novoPonto);
		avatarTopWar.setAngulo(acaoClienteTopWar.getAngulo());
		return ConstantesTopWar.OK;
	}

	public static boolean verificaColisao(Point novoPonto, MapaTopWar mapaTopWar) {
		Rectangle areaMapa = new Rectangle(0, 0, mapaTopWar.getLargura(),
				mapaTopWar.getAltura());
		Shape novaArea = AvatarCliente.desenhaCorpo(novoPonto);
		if (!areaMapa.contains(novaArea.getBounds())) {
			return true;
		}
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		for (Iterator iterator = objetoMapaList.iterator(); iterator.hasNext();) {
			ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
			if (objetoMapa.getTransparencia() > 10
					&& objetoMapa.getEfeito() == null
					&& objetoMapa.getForma().intersects(novaArea.getBounds())) {
				return true;
			}
		}

		return false;
	}

	public void entrarNoJogo(String nomeJogador) {
		AvatarTopWar avatarTopWar = new AvatarTopWar();
		avatarTopWar.setVida(ConstantesTopWar.VIDA_COMPLETA);
		avatarTopWar.setBalas(ConstantesTopWar.BALAS_ASSALT);
		avatarTopWar.setCartuchos(ConstantesTopWar.CARTUCHOS_ASSALT);
		int contAzul = contarJogadores(ConstantesTopWar.TIME_AZUL);
		int contVermelho = contarJogadores(ConstantesTopWar.TIME_VERMELHO);
		if (contAzul > contVermelho) {
			avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeVermelho());
			avatarTopWar.setTime(ConstantesTopWar.TIME_VERMELHO);
		} else if (contAzul < contVermelho) {
			avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeAzul());
			avatarTopWar.setTime(ConstantesTopWar.TIME_AZUL);
		} else {
			if (Math.random() > 0.5) {
				avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeVermelho());
				avatarTopWar.setTime(ConstantesTopWar.TIME_VERMELHO);
			} else {
				avatarTopWar.setPontoAvatar(mapaTopWar.getPontoTimeAzul());
				avatarTopWar.setTime(ConstantesTopWar.TIME_AZUL);
			}
		}

		avatarTopWar.setNomeJogador(nomeJogador);
		avatarTopWars.add(avatarTopWar);
	}

	private int contarJogadores(String time) {
		int cont = 0;
		for (int i = 0; i < avatarTopWars.size(); i++) {
			if (time.equals(avatarTopWars.get(i).getTime())) {
				cont++;
			}
		}
		return cont;
	}

	public AvatarTopWar removerJogador(String nomeJogador) {
		synchronized (avatarTopWars) {
			for (Iterator iterator = avatarTopWars.iterator(); iterator
					.hasNext();) {
				AvatarTopWar avatarTopWar = (AvatarTopWar) iterator.next();
				if (avatarTopWar.getNomeJogador().equals(nomeJogador)) {
					iterator.remove();
					return avatarTopWar;
				}
			}
		}
		return null;
	}

	public AvatarTopWar obterAvatarTopWar(String nomeCliente) {
		List<AvatarTopWar> avatarTopWars = getAvatarTopWars();
		synchronized (avatarTopWars) {
			for (Iterator iterator2 = avatarTopWars.iterator(); iterator2
					.hasNext();) {
				AvatarTopWar avatarTopWar = (AvatarTopWar) iterator2.next();
				if (nomeCliente.equals(avatarTopWar.getNomeJogador())) {
					return avatarTopWar;
				}
			}
		}
		return null;
	}

	public boolean verificaFinalizado() {
		finalizado = avatarTopWars.isEmpty();
		return finalizado;
	}

	public void removerClientesInativos() {
		synchronized (avatarTopWars) {
			for (Iterator iterator = avatarTopWars.iterator(); iterator
					.hasNext();) {
				AvatarTopWar avatarTopWar = (AvatarTopWar) iterator.next();
				long diff = (System.currentTimeMillis() - avatarTopWar
						.getUltimaRequisicao());
				if (diff > 5000) {
					iterator.remove();
				}
			}
		}
	}

	public Object atirar(AvatarTopWar avatarAtirador, double angulo) {
		if (verificaRecarregando(avatarAtirador)) {
			return null;
		}
		Point pontoTiro = GeoUtil.calculaPonto(angulo,
				ConstantesTopWar.ASSALT_MAX_RANGE,
				avatarAtirador.getPontoAvatar());
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		List<Point> linhaTiro = GeoUtil.drawBresenhamLine(
				avatarAtirador.getPontoAvatar(), pontoTiro);
		Point pointAnt = null;
		for (Iterator iteratorPtsBala = linhaTiro.iterator(); iteratorPtsBala
				.hasNext();) {
			Point point = (Point) iteratorPtsBala.next();

			for (Iterator iterator = objetoMapaList.iterator(); iterator
					.hasNext();) {
				ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
				if (!ConstantesTopWar.GRADE.equals(objetoMapa.getEfeito())
						&& objetoMapa.getTransparencia() > 10
						&& objetoMapa.getForma().contains(point)) {
					if (pointAnt != null) {
						int balas = consomeBalasArma(avatarAtirador);
						if (balas != 0) {
							avatarAtirador.setPontoUtlDisparo(point);
						} else {
							return null;
						}
					}
					return null;
				}
			}
			synchronized (avatarTopWars) {
				for (Iterator iteratorAvatar = avatarTopWars.iterator(); iteratorAvatar
						.hasNext();) {
					AvatarTopWar avatarAlvo = (AvatarTopWar) iteratorAvatar
							.next();
					if (avatarAlvo.equals(avatarAtirador)) {
						continue;
					}
					AvatarCliente avatarCliente = new AvatarCliente(avatarAlvo);
					if (avatarCliente.gerarCorpo().contains(point)
							|| avatarCliente.gerarCabeca().contains(point)) {
						int balas = consomeBalasArma(avatarAtirador);
						if (balas != 0) {
							avatarAtirador.setPontoUtlDisparo(point);
						} else {
							return null;
						}
						if (avatarAtirador.getTime().equals(
								avatarAlvo.getTime())) {
							return null;
						} else {
							if (avatarCliente.gerarCorpo().contains(point)) {
								avatarAlvo
										.setVida(avatarAlvo.getVida() - balas);
							} else {
								avatarAlvo.setVida(0);
							}
							if (avatarAlvo.getVida() < 1) {
								if (ConstantesTopWar.TIME_AZUL
										.equals(avatarAlvo.getTime())) {
									ptsVermelho++;
								} else {
									ptsAzul++;
								}
							}
							avatarAlvo.setDeaths(avatarAlvo.getDeaths() + 1);
							avatarAtirador
									.setKills(avatarAtirador.getKills() + 1);

						}

						return ConstantesTopWar.OK;
					}
				}
			}
			pointAnt = point;
		}
		return null;
	}

	private int consomeBalasArma(AvatarTopWar avatarAtirador) {
		if (avatarAtirador.getBalas() <= 0) {
			return 0;
		}
		int balas = Util.intervalo(3, 7);
		if (avatarAtirador.getBalas() < balas) {
			balas = avatarAtirador.getBalas();
		}
		avatarAtirador.setBalas(avatarAtirador.getBalas() - balas);
		return balas;
	}

	private boolean verificaRecarregando(AvatarTopWar avatarAtirador) {
		return (System.currentTimeMillis() - avatarAtirador.getRecarregar()) < ConstantesTopWar.TEMPO_RECARGA;
	}

	public Object atualizaAngulo(AvatarTopWar avatarTopWar, double angulo) {
		avatarTopWar.setAngulo(angulo);
		return ConstantesTopWar.OK;
	}

	public Object recarregar(AvatarTopWar avatarTopWar) {
		if (avatarTopWar.getCartuchos() <= 0) {
			return null;
		}
		avatarTopWar.setBalas(30);
		avatarTopWar.setCartuchos(avatarTopWar.getCartuchos() - 1);
		avatarTopWar.setRecarregar(System.currentTimeMillis());
		return ConstantesTopWar.OK;
	}
}
