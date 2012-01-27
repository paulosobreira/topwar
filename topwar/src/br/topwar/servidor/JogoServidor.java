package br.topwar.servidor;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.nnpe.GeoUtil;
import br.nnpe.Logger;
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
		avatarTopWar.setNomeJogador(dadosJogoTopWar.getNomeJogador());
		avatarTopWars.add(avatarTopWar);
	}

	public List<AvatarTopWar> getAvatarTopWars() {
		return avatarTopWars;
	}

	public List<AvatarTopWar> getAvatarTopWars(SessaoCliente sessaoCliente) {
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
					ret.add(avatarTopWar);
					continue;
				}
				List<Point> line = GeoUtil.drawBresenhamLine(avatarTopWar
						.getPontoAvatar(), avatarTopWarJog.getPontoAvatar());
				if (campoVisao(line)) {
					ret.add(avatarTopWar);
				}

			}
		}
		return ret;
	}

	private boolean campoVisao(List<Point> line) {
		List<ObjetoMapa> objetoMapaList = mapaTopWar.getObjetoMapaList();
		for (Iterator iterator = line.iterator(); iterator.hasNext();) {
			Point point = (Point) iterator.next();
			for (Iterator iterator2 = objetoMapaList.iterator(); iterator2
					.hasNext();) {
				ObjetoMapa objetoMapa = (ObjetoMapa) iterator2.next();
				if (objetoMapa.getForma().contains(point)) {
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

	protected static boolean verificaColisao(Point novoPonto,
			MapaTopWar mapaTopWar) {
		Rectangle areaAvatar = new Rectangle(novoPonto.x, novoPonto.y,
				ConstantesTopWar.LARGURA_AVATAR, ConstantesTopWar.ALTURA_AVATAR);
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
		// TODO Auto-generated method stub
		return false;
	}
}
