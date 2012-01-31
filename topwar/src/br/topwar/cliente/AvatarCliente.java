package br.topwar.cliente;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import br.nnpe.GeoUtil;
import br.topwar.ConstantesTopWar;
import br.topwar.tos.AvatarTopWar;

public class AvatarCliente {
	private boolean local;
	private int quadroAnimacao;
	private long lastAnim;
	private Point pontoAvatarOld;
	private AvatarTopWar avatarTopWar;

	public AvatarTopWar getAvatarTopWar() {
		return avatarTopWar;
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

	public void setAvatarTopWar(AvatarTopWar avatarTopWar) {
		this.avatarTopWar = avatarTopWar;
	}

	public Shape gerarCabeca() {
		AffineTransform afRotate = new AffineTransform();
		double angulo = getAngulo();
		double rad = Math.toRadians((double) angulo);
		Shape cabeca = desenhaCabeca(GeoUtil.calculaPonto(angulo, 6,
				getPontoAvatar()));
		GeneralPath gpCabeca = new GeneralPath(cabeca);
		afRotate.setToRotation(rad, gpCabeca.getBounds().getCenterX(), gpCabeca
				.getBounds().getCenterY());
		return gpCabeca.createTransformedShape(afRotate);
	}

	public Shape desenhaCorpo(Point p) {
		return new Rectangle2D.Double(p.x - 8, p.y - 3, 16, 6);
	}

	public Shape obeterAreaAvatar() {
		Point pontoAvatar = getPontoAvatar();
		Point desenha = new Point(pontoAvatar.x
				- (ConstantesTopWar.LARGURA_AREA_AVATAR), pontoAvatar.y
				- (ConstantesTopWar.ALTURA_AREA_AVATAR));
		Rectangle areaAvatar = new Rectangle(desenha.x, desenha.y,
				ConstantesTopWar.LARGURA_AVATAR, ConstantesTopWar.ALTURA_AVATAR);
		return areaAvatar;
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

	public Shape desenhaCabeca(Point p) {
		return new Rectangle2D.Double(p.x - 3, p.y - 2, 6, 4);
	}

	public void animar() {
		int intMin = (200 + (10 * getVelocidade()));
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

	public AvatarCliente(AvatarTopWar avatarTopWar) {
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

}
