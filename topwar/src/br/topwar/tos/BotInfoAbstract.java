package br.topwar.tos;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import sun.util.logging.resources.logging;

import br.nnpe.GeoUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.topwar.ConstantesTopWar;
import br.topwar.serial.ObjetoMapa;
import br.topwar.servidor.JogoServidor;

public abstract class BotInfoAbstract {
	boolean executouAcaoAtaque = false;
	public static String PATRULHANDO = "PATRULHANDO";
	public static String ATACANDO = "ATACANDO";
	public static String SEGUINDO = "SEGUINDO";
	protected ObjTopWar avatarTopWar;
	protected ObjTopWar seguindo;
	protected List<ObjTopWar> avataresVisiveis = new ArrayList<ObjTopWar>();
	protected List<ObjTopWar> avataresMesmoTime = new ArrayList<ObjTopWar>();
	protected List<ObjTopWar> avataresTimeOposto = new ArrayList<ObjTopWar>();
	protected JogoServidor jogoServidor;
	protected Point pontoDestino;
	protected Point pontoSeguindo;
	protected int contPatrulha;
	private int contGuia;
	private int desvio;
	private Point ultimaGuia;
	protected Point ptAtual;
	protected int contPtAtual;
	protected int vidaUltAlvo;
	private long tempoProcessaAcaoBot = 0;
	private int seguindoParado;

	public long getTempoProcessaAcaoBot() {
		return tempoProcessaAcaoBot;
	}

	public void setTempoProcessaAcaoBot(long tempoProcessaAcaoBot) {
		if (this.tempoProcessaAcaoBot > tempoProcessaAcaoBot) {
			return;
		}
		this.tempoProcessaAcaoBot = tempoProcessaAcaoBot;
	}

	public abstract void processaAcaoBot();

	public abstract void gerarDesvioBot();

	protected abstract void atacarInimigo();

	public int getDesvio() {
		return desvio;
	}

	public boolean isExecutouAcaoAtaque() {
		return executouAcaoAtaque;
	}

	public void setExecutouAcaoAtaque(boolean executouAcaoAtaque) {
		this.executouAcaoAtaque = executouAcaoAtaque;
	}

	public void setDesvio(int desvio) {
		this.desvio = desvio;
	}

	public Point getPontoDestino() {
		return pontoDestino;
	}

	public void setPontoDestino(Point pontoDestino) {
		this.pontoDestino = pontoDestino;
	}

	public ObjTopWar getAvatarTopWar() {
		return avatarTopWar;
	}

	public void setAvatarTopWar(ObjTopWar avatarTopWar) {
		this.avatarTopWar = avatarTopWar;
	}

	public JogoServidor getJogoServidor() {
		return jogoServidor;
	}

	public void setJogoServidor(JogoServidor jogoServidor) {
		this.jogoServidor = jogoServidor;
	}

	public int getContPatrulha() {
		return contPatrulha;
	}

	public void setContPatrulha(int contPatrulha) {
		this.contPatrulha = contPatrulha;
	}

	public int getContGuia() {
		return contGuia;
	}

	public ObjTopWar getSeguindo() {
		return seguindo;
	}

	public void setSeguindo(ObjTopWar seguindo) {
		this.seguindo = seguindo;
	}

	public void setContGuia(int contGuia) {
		this.contGuia = contGuia;
	}

	public Point getUltimaGuia() {
		return ultimaGuia;
	}

	public void setUltimaGuia(Point ultimaGuia) {
		this.ultimaGuia = ultimaGuia;
	}

	public Point getPtAtual() {
		return ptAtual;
	}

	public void setPtAtual(Point ptAtual) {
		this.ptAtual = ptAtual;
	}

	public int getContPtAtual() {
		return contPtAtual;
	}

	public void setContPtAtual(int contPtAtual) {
		this.contPtAtual = contPtAtual;
	}

	public int getVidaUltAlvo() {
		return vidaUltAlvo;
	}

	public void setVidaUltAlvo(int vidaUltAlvo) {
		this.vidaUltAlvo = vidaUltAlvo;
	}

	public void processaAvataresVisiveis(ObjTopWar avatarTopWar,
			JogoServidor jogoServidor) {
		avataresVisiveis.clear();
		avataresMesmoTime.clear();
		avataresTimeOposto.clear();
		List<ObjTopWar> avatarTopWarsCopia = jogoServidor
				.getAvatarTopWarsCopia();
		for (Iterator iterator2 = avatarTopWarsCopia.iterator(); iterator2
				.hasNext();) {
			ObjTopWar avatarTopWarCopia = (ObjTopWar) iterator2.next();
			if (avatarTopWar.equals(avatarTopWarCopia)) {
				continue;
			}
			if (avatarTopWarCopia.getVida() <= 0) {
				continue;
			}
			if (ConstantesTopWar.OBJ_ROCKET == avatarTopWarCopia.getArma()) {
				continue;
			}
			if (GeoUtil.distaciaEntrePontos(avatarTopWar.getPontoAvatar(),
					avatarTopWarCopia.getPontoAvatar()) > ConstantesTopWar.LIMITE_VISAO) {
				continue;
			}
			List<Point> line = GeoUtil.drawBresenhamLine(
					avatarTopWar.getPontoAvatar(),
					avatarTopWarCopia.getPontoAvatar());
			if (line.size() < ConstantesTopWar.LIMITE_VISAO
					&& jogoServidor.campoVisaoTiro(line, avatarTopWar)) {
				avatarTopWarCopia.setDistanciaDeUmAvatar(GeoUtil
						.distaciaEntrePontos(
								avatarTopWarCopia.getPontoAvatar(),
								avatarTopWar.getPontoAvatar()));
				avataresVisiveis.add(avatarTopWarCopia);
				if (avatarTopWar.getTime().equals(avatarTopWarCopia.getTime())) {
					avataresMesmoTime.add(avatarTopWarCopia);
				} else {
					avataresTimeOposto.add(avatarTopWarCopia);
				}
			}
		}
		Collections.sort(avataresVisiveis, new Comparator<ObjTopWar>() {
			@Override
			public int compare(ObjTopWar o1, ObjTopWar o2) {
				return new Double(o1.getDistanciaDeUmAvatar())
						.compareTo(new Double(o2.getDistanciaDeUmAvatar()));
			}
		});
	}

	public ObjTopWar avatarMesmoTimeSeguir(ObjTopWar avatarTopWar) {
		for (Iterator iterator = avataresMesmoTime.iterator(); iterator
				.hasNext();) {
			ObjTopWar objTopWar = (ObjTopWar) iterator.next();
			if (objTopWar.getBotInfo() == null) {
				return objTopWar;
			}
			// if (avatarTopWar.getBotInfo() == null
			// || ConstantesTopWar.ASSAULT.equals(objTopWar.getClasse())
			// || (ConstantesTopWar.SHOTGUN.equals(objTopWar.getClasse()) ||
			// ConstantesTopWar.SHIELD
			// .equals(objTopWar.getClasse()))) {
			// return objTopWar;
			// }
		}
		return null;
	}

	protected void segueAvatar() {
		ObjTopWar avatarSeguir = getSeguindo();
		if (avatarSeguir == null) {
			avatarSeguir = avatarMesmoTimeSeguir(avatarTopWar);
		}
		if (avatarSeguir != null) {
			if (getPontoSeguindo() == avatarSeguir.getPontoAvatar()) {
				seguindoParado++;
			} else {
				seguindoParado = 0;
			}
			if (seguindoParado > 30) {
				setSeguindo(null);
				return;
			}
			int distaciaEntrePontos = GeoUtil.distaciaEntrePontos(
					avatarTopWar.getPontoAvatar(),
					avatarSeguir.getPontoAvatar());
			if (distaciaEntrePontos > ConstantesTopWar.LIMITE_VISAO) {
				setSeguindo(null);
				setPontoDestino(null);
				return;
			}
			if (distaciaEntrePontos < Util.intervalo(50, 100)) {
				return;
			}
			setPontoDestino(avatarSeguir.getPontoAvatar());
			setSeguindo(avatarSeguir);
			setPontoSeguindo(avatarSeguir.getPontoAvatar());
		}
	}

	public void botVaiPontoAleatorio() {
		Point calculaPonto = GeoUtil.calculaPonto(Util.intervalo(0, 360),
				Util.intervalo(100, 300), avatarTopWar.getPontoAvatar());
		while (!jogoServidor.verificaFinalizado()
				&& !jogoServidor.verificaAndavel(avatarTopWar.getPontoAvatar(),
						calculaPonto)) {
			calculaPonto = GeoUtil.calculaPonto(Util.intervalo(0, 360),
					Util.intervalo(100, 300), avatarTopWar.getPontoAvatar());
		}
		setPontoDestino(calculaPonto);
	}

	public boolean vaiGuia() {
		if (getSeguindo() != null) {
			return false;
		}
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
		if (getSeguindo() != null) {
			return false;
		}
		if (contPatrulha < 5) {
			contPatrulha++;
			return false;
		}
		contPatrulha = 0;
		return true;
	}

	/**
	 * Patrulhando
	 */
	protected void patrulhar() {
		if (getPontoDestino() != null) {
			return;
		}
		if (vaiGuia()) {
			patrulharGuia();
		} else if (vaiBaseInimiga()) {
			patrulharAteBaseInimiga();
		} else {
			botVaiPontoAleatorio();
		}
	}

	private void patrulharAteBaseInimiga() {
		if (avatarTopWar.getTime() == ConstantesTopWar.TIME_VERMELHO) {
			setPontoDestino(jogoServidor.getMapaTopWar().getPontoTimeAzul());
		} else {
			setPontoDestino(jogoServidor.getMapaTopWar().getPontoTimeVermelho());
		}
	}

	private void patrulharGuia() {
		List<ObjetoMapa> objetoMapaList = jogoServidor.getMapaTopWar()
				.getObjetoMapaList();
		ArrayList<Point> possiveisDestinos = new ArrayList<Point>();
		for (Iterator iterator2 = objetoMapaList.iterator(); iterator2
				.hasNext();) {
			ObjetoMapa objetoMapa = (ObjetoMapa) iterator2.next();
			if (!ConstantesTopWar.BOT_GUIA.equals(objetoMapa.getEfeito())) {
				continue;
			}
			Point analizar = objetoMapa.getForma().getBounds().getLocation();
			if (analizar.equals(getUltimaGuia())) {
				continue;
			}
			if (GeoUtil.distaciaEntrePontos(avatarTopWar.getPontoAvatar(),
					analizar) < ConstantesTopWar.LIMITE_VISAO) {
				List<Point> drawBresenhamLine = GeoUtil.drawBresenhamLine(
						avatarTopWar.getPontoAvatar(), analizar);
				if (getUltimaGuia() == null
						|| jogoServidor.campoVisao(drawBresenhamLine,
								avatarTopWar, true)) {
					possiveisDestinos.add(analizar);
				}
			}
		}
		if (!possiveisDestinos.isEmpty()) {
			Collections.shuffle(possiveisDestinos);
			Point point = possiveisDestinos.get(Util.intervalo(0,
					possiveisDestinos.size() - 1));
			setPontoDestino(point);
			setUltimaGuia(point);
		} else {
			botVaiPontoAleatorio();
		}
	}

	protected boolean atacaComFaca(ObjTopWar avatarTopWarCopia) {
		if (avatarTopWar.getArma() != ConstantesTopWar.ARMA_FACA)
			jogoServidor.alternarFaca(avatarTopWar);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		boolean executouAcaoAtaque;
		avatarTopWar.setAngulo(GeoUtil.calculaAngulo(
				avatarTopWar.getPontoAvatar(),
				avatarTopWarCopia.getPontoAvatar(), 90));
		jogoServidor.atacar(avatarTopWar, avatarTopWar.getAngulo(), 0);
		executouAcaoAtaque = true;
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		jogoServidor.alternarFaca(avatarTopWar);
		return executouAcaoAtaque;
	}

	protected void moverDestino() {
		if (avatarTopWar.getPontoAvatar() == null) {
			return;
		}
		if (getPontoDestino() == null) {
			return;
		}
		List<Point> lineMove = GeoUtil.drawBresenhamLine(
				avatarTopWar.getPontoAvatar(), getPontoDestino());
		if (lineMove.size() < avatarTopWar.getVelocidade()) {
			setPontoDestino(null);
		} else {
			Point dstMover = lineMove.get(avatarTopWar.getVelocidade() - 1);
			DadosAcaoClienteTopWar acaoClienteTopWar = new DadosAcaoClienteTopWar();
			acaoClienteTopWar.setPonto(dstMover);
			acaoClienteTopWar.setAngulo(GeoUtil.calculaAngulo(
					avatarTopWar.getPontoAvatar(), dstMover, 90));
			if (jogoServidor.verificaAndavel(avatarTopWar.getPontoAvatar(),
					dstMover)) {
				String mover = (String) jogoServidor.moverPontoAvatar(
						avatarTopWar, acaoClienteTopWar);
				if (avatarTopWar.getPontoAvatar().equals(getPontoSeguindo())) {
					setPontoSeguindo(null);
				}
			} else {
				setPontoDestino(null);
			}
		}
	}

	public Point getPontoSeguindo() {
		return pontoSeguindo;
	}

	public void setPontoSeguindo(Point pontoSeguindo) {
		this.pontoSeguindo = pontoSeguindo;
	}

	public int contaAmigosVisiveis() {
		return avataresMesmoTime.size();
	}

	public int contaInimigosVisiveis() {
		return avataresTimeOposto.size();
	}

	public void procurarAbrigo() {
		Logger.logar(avatarTopWar.getNomeJogador() + "  procurarAbrigo()");
		int cont = 0;
		while (cont < 20) {
			botVaiPontoAleatorio();
			if (verificaDestinoSeguroDosInimigos()) {
				break;
			}
			cont++;
		}
	}

	public boolean verificaDestinoSeguroDosInimigos() {
		for (Iterator iterator = avataresTimeOposto.iterator(); iterator
				.hasNext();) {
			ObjTopWar objTopWarInimigo = (ObjTopWar) iterator.next();
			if (!verificaPontoDestinoSeguro(objTopWarInimigo.getPontoAvatar())) {
				return false;
			}
		}
		return true;
	}

	private boolean verificaPontoDestinoSeguro(Point inimigo) {
		if(inimigo==null || pontoDestino==null){
			return false;
		}
		List<Point> linhaFuga = GeoUtil
				.drawBresenhamLine(inimigo, pontoDestino);
		for (Iterator iterator2 = linhaFuga.iterator(); iterator2.hasNext();) {
			Point point = (Point) iterator2.next();
			if (jogoServidor.verificaColisao(point)) {
				return true;
			}
		}
		return false;
	}

}