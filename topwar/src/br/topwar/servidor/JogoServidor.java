package br.topwar.servidor;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.nnpe.Logger;
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

	public JogoServidor(DadosJogoTopWar dadosJogoTopWar,
			ProxyComandos proxyComandos) {
		this.dadosJogoTopWar = dadosJogoTopWar;
		this.proxyComandos = proxyComandos;
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(CarregadorRecursos
					.recursoComoStream(dadosJogoTopWar.getNomeMapa()
							+ ".topwar"));
			mapaTopWar = (MapaTopWar) ois.readObject();
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		AvatarTopWar avatarTopWar = new AvatarTopWar();
		avatarTopWar.setPontoAvatar(new Point(20, 20));
		avatarTopWar.setTime("vermelho");
		avatarTopWar.setNomeJogador(dadosJogoTopWar.getNomeCriadorJogo());
		avatarTopWars.add(avatarTopWar);
	}

	public List<AvatarTopWar> getAvatarTopWars() {
		return avatarTopWars;
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

	public void moverAvatar(AvatarTopWar avatarTopWar,
			DadosAcaoClienteTopWar acaoClienteTopWar) {
		Point novoPonto = avatarTopWar.getPontoAvatar();
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
		avatarTopWar.setPontoAvatar(novoPonto);
		avatarTopWar.setAngulo(acaoClienteTopWar.getAngulo());
	}

	protected static boolean verificaColisao(Point novoPonto,
			Rectangle areaAvatar, MapaTopWar mapaTopWar) {
		Point desenha = new Point(novoPonto.x
				- ((int) areaAvatar.getWidth() / 2), novoPonto.y
				- ((int) areaAvatar.getHeight() / 3));
		Rectangle novaArea = new Rectangle(desenha.x, desenha.y,
				(int) areaAvatar.getWidth(), (int) areaAvatar.getHeight());
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		for (Iterator iterator = objetoMapaList.iterator(); iterator.hasNext();) {
			ObjetoMapa objetoMapa = (ObjetoMapa) iterator.next();
			if (objetoMapa.getTransparencia() > 100
					&& objetoMapa.getEfeito() == null
					&& objetoMapa.getForma().intersects(novaArea)) {
				return true;
			}
		}
		return false;
	}

	public void adicionarJogador(String nomeJogador) {
		AvatarTopWar avatarTopWar = new AvatarTopWar();
		avatarTopWar.setPontoAvatar(new Point(20, 20));
		avatarTopWar.setTime("azul");
		avatarTopWar.setNomeJogador(nomeJogador);
		avatarTopWars.add(avatarTopWar);
	}
}
