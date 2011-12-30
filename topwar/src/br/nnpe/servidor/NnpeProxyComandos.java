package br.nnpe.servidor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import br.nnpe.Constantes;
import br.nnpe.Logger;
import br.nnpe.tos.ErroServ;
import br.nnpe.tos.NnpeDadosChat;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.topwar.recursos.idiomas.Lang;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

public class NnpeProxyComandos {
	protected NnpeDadosChat nnpeDadosChat;
	protected DefaultManageableImageCaptchaService capcha = new DefaultManageableImageCaptchaService();

	public NnpeProxyComandos(String webDir, String webInfDir) {
		nnpeDadosChat = new NnpeDadosChat();
	}

	public Object processarObjeto(Object object) {
		NnpeTO nnpeTO = (NnpeTO) object;
		if (Constantes.NOVO_CAPCHA.equals(nnpeTO.getComando())) {
			return novoCapcha();
		}

		return null;
	}

	public Object novoCapcha() {
		try {
			ByteArrayOutputStream jpegstream = new ByteArrayOutputStream();
			String chave = String.valueOf(System.currentTimeMillis());
			BufferedImage challenge = capcha.getImageChallengeForID(chave);
			ImageIO.write(challenge, "jpg", jpegstream);
			NnpeTO nnpeTO = new NnpeTO();
			nnpeTO.setComando(Constantes.NOVO_CAPCHA);
			nnpeTO.setData(chave);
			nnpeTO.setDataBytes(jpegstream.toByteArray());
//			JOptionPane.showMessageDialog(null,
//					new ImageIcon(nnpeTO.getDataBytes()));
			return nnpeTO;
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		return new ErroServ(Lang.msg("erroCapcha"));
	}

	private Object atualizarDadosVisao(NnpeTO mesa11to) {
		if (mesa11to.getSessaoCliente() != null) {
			// controleChatServidor.atualizaSessaoCliente(mesa11to
			// .getSessaoCliente());
		}
		mesa11to.setData(nnpeDadosChat);
		return mesa11to;
	}

	public void removerClienteInativo(SessaoCliente sessaoClienteRemover) {
		Logger.logar("removerClienteInativo " + sessaoClienteRemover);
		// controleJogosServidor.removerClienteInativo(sessaoClienteRemover);
		nnpeDadosChat.getClientes().remove(sessaoClienteRemover);
	}

	public boolean verificaSemSessao(String nomeCriador) {
		return false;
		// return controleLogin.verificaSemSessao(nomeCriador);
	}
}
