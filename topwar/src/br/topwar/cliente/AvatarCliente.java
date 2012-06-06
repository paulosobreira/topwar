package br.topwar.cliente;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import br.nnpe.GeoUtil;
import br.topwar.ConstantesTopWar;
import br.topwar.tos.ObjTopWar;

public class AvatarCliente {
	private boolean local;
	private int quadroAnimacao;
	private int quadroAnimacaoMorte;
	private long lastAnim;
	private long lastAnimMorte;
	private Point pontoAvatarSuave;
	private Point pontoAvatarOld;
	private ObjTopWar avatarTopWar;

	public int getRangeUtlDisparo() {
		return avatarTopWar.getRangeUtlDisparo();
	}

	public void setRangeUtlDisparo(int rangeUtlDisparo) {
		avatarTopWar.setRangeUtlDisparo(rangeUtlDisparo);
	}

	public boolean isInvencivel() {
		return avatarTopWar.isInvencivel();
	}

	public ObjTopWar getAvatarTopWar() {
		return avatarTopWar;
	}

	public void setQuadroAnimacaoMorte(int quadroAnimacaoMorte) {
		this.quadroAnimacaoMorte = quadroAnimacaoMorte;
	}

	public Point getPontoAvatarOld() {
		return pontoAvatarOld;
	}

	public void setPontoAvatarOld(Point pontoAvatarOld) {
		this.pontoAvatarOld = pontoAvatarOld;
	}

	public int getQuadroAnimacao() {
		return quadroAnimacao;
	}

	public void setAvatarTopWar(ObjTopWar avatarTopWar) {
		this.avatarTopWar = avatarTopWar;
	}

	public Point getPontoAvatarSuave() {
		return pontoAvatarSuave;
	}

	public void setPontoAvatarSuave(Point pontoAvatarDesenha) {
		this.pontoAvatarSuave = pontoAvatarDesenha;
	}

	public Shape gerarCabeca() {
		AffineTransform afRotate = new AffineTransform();
		double angulo = getAngulo();
		double rad = Math.toRadians((double) angulo);
		Shape cabeca = desenhaCabeca(getPontoAvatar());
		GeneralPath gpCabeca = new GeneralPath(cabeca);
		afRotate.setToRotation(rad, gpCabeca.getBounds().getCenterX(), gpCabeca
				.getBounds().getCenterY());
		return gpCabeca.createTransformedShape(afRotate);
	}

	public static Shape desenhaAreaFaca(Point pontoAvatar, double angulo) {
		Point pFacaAv = GeoUtil.calculaPonto(angulo, 10, pontoAvatar);
		return new Ellipse2D.Double(pFacaAv.x - 10, pFacaAv.y - 10, 20, 20);
	}

	public static Shape desenhaCorpo(Point p) {
		return new Rectangle2D.Double(p.x - 8, p.y, 18, 18);
	}

	public int getArma() {
		return avatarTopWar.getArma();
	}

	public Shape obeterAreaAvatar() {
		Point desenha = getPontoDesenha();
		Rectangle areaAvatar = new Rectangle(desenha.x, desenha.y,
				ConstantesTopWar.LARGURA_AVATAR, ConstantesTopWar.ALTURA_AVATAR);
		return areaAvatar;
	}

	public Point getPontoDesenha() {
		Point pontoAvatar = getPontoAvatar();
		return new Point(
				pontoAvatar.x - (ConstantesTopWar.LARGURA_AREA_AVATAR),
				pontoAvatar.y - (ConstantesTopWar.ALTURA_AREA_AVATAR));

	}

	public Shape gerarCorpo() {
		AffineTransform afRotate = new AffineTransform();
		Shape corpo = desenhaCorpo(getPontoAvatar());
		double angulo = getAngulo();
		double rad = Math.toRadians((double) angulo);
		GeneralPath gpCorpo = new GeneralPath(corpo);
		afRotate.setToRotation(rad, gpCorpo.getBounds().getCenterX(), gpCorpo
				.getBounds().getCenterY());
		return gpCorpo.createTransformedShape(afRotate);
	}

	public static Shape desenhaCabeca(Point p) {
		return new Rectangle2D.Double(p.x - 2, p.y - 8, 3, 3);
	}

	public void animar() {
		int intMin = (120 - (5 * getVelocidade()));
		if (intMin < 60) {
			intMin = 60;
		}
		if ((System.currentTimeMillis() - lastAnim) < intMin) {
			return;
		}
		quadroAnimacao++;
		if (quadroAnimacao > 3) {
			quadroAnimacao = 0;
		}
		lastAnim = System.currentTimeMillis();
	}

	public int getQuadroAnimacaoMorte() {
		return quadroAnimacaoMorte;
	}

	public void animarDesenhoMorte() {
		int intMin = (200 - (10 * getVelocidade()));
		if (intMin < 60) {
			intMin = 60;
		}
		if ((System.currentTimeMillis() - lastAnimMorte) < intMin) {
			return;
		}
		quadroAnimacaoMorte++;
		if (quadroAnimacaoMorte > 3) {
			quadroAnimacaoMorte = 3;
		}
		lastAnimMorte = System.currentTimeMillis();
	}

	public AvatarCliente(ObjTopWar avatarTopWar) {
		this.avatarTopWar = avatarTopWar;
	}

	public boolean equals(Object obj) {
		if (obj instanceof AvatarCliente) {
			AvatarCliente outro = (AvatarCliente) obj;
			return (getNomeJogador().equals(outro.getNomeJogador()));
		} else
			return avatarTopWar.equals(obj);
	}

	public double getAngulo() {
		return avatarTopWar.getAngulo();
	}

	public String getNomeJogador() {
		return avatarTopWar.getNomeJogador();
	}

	public Point getPontoAvatar() {
		return avatarTopWar.getPontoAvatar();
	}

	public String getTime() {
		return avatarTopWar.getTime();
	}

	public int getVelocidade() {
		return avatarTopWar.getVelocidade();
	}

	public int hashCode() {
		return avatarTopWar.hashCode();
	}

	public void setAngulo(double angulo) {
		avatarTopWar.setAngulo(angulo);
	}

	public void setNomeJogador(String nomeJogador) {
		avatarTopWar.setNomeJogador(nomeJogador);
	}

	public void setPontoAvatar(Point pontoAvatar) {
		avatarTopWar.setPontoAvatar(pontoAvatar);
	}

	public void setTime(String time) {
		avatarTopWar.setTime(time);
	}

	public void setVelocidade(int velocidade) {
		avatarTopWar.setVelocidade(velocidade);
	}

	public String toString() {
		return avatarTopWar.toString();
	}

	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}

	public long getTempoUtlAtaque() {
		return avatarTopWar.getTempoUtlAtaque();
	}

	public long getUltimaMorte() {
		return avatarTopWar.getUltimaMorte();
	}

	public int getVida() {
		return avatarTopWar.getVida();
	}

	public Shape obeterAreaAvatarSuave() {
		Point desenha = getPontoDesenhaSuave();
		if (desenha == null) {
			return obeterAreaAvatar();
		}
		Rectangle areaAvatar = new Rectangle(desenha.x, desenha.y,
				ConstantesTopWar.LARGURA_AVATAR, ConstantesTopWar.ALTURA_AVATAR);
		return areaAvatar;
	}

	public Point getPontoDesenhaSuave() {
		if (pontoAvatarSuave == null) {
			return null;
		}
		return new Point(pontoAvatarSuave.x
				- (ConstantesTopWar.LARGURA_AREA_AVATAR), pontoAvatarSuave.y
				- (ConstantesTopWar.ALTURA_AREA_AVATAR));
	}

}
