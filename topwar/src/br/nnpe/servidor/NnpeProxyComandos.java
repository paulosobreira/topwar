package br.nnpe.servidor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.nnpe.Constantes;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.persistencia.HibernateUtil;
import br.nnpe.persistencia.NnpeUsuario;
import br.nnpe.tos.ErroServ;
import br.nnpe.tos.MsgSrv;
import br.nnpe.tos.NnpeCliente;
import br.nnpe.tos.NnpeDadosChat;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.topwar.recursos.idiomas.Lang;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

public class NnpeProxyComandos {
	protected NnpeDadosChat nnpeDadosChat;
	protected NnpeChatServidor nnpeChatServidor;
	protected NnpeMonitorAtividadeChat nnpeMonitorAtividadeChat;
	protected DefaultManageableImageCaptchaService capcha = new DefaultManageableImageCaptchaService();

	public NnpeProxyComandos(String webDir, String webInfDir) {
		nnpeDadosChat = new NnpeDadosChat();
		nnpeChatServidor = new NnpeChatServidor(nnpeDadosChat);
		nnpeMonitorAtividadeChat = new NnpeMonitorAtividadeChat(this);
	}

	public Object processarObjeto(Object object) {
		NnpeTO nnpeTO = (NnpeTO) object;
		if (Constantes.ATUALIZAR_VISAO.equals(nnpeTO.getComando())) {
			return atualizarDadosVisao(nnpeTO);
		} else if (Constantes.NOVO_CAPCHA.equals(nnpeTO.getComando())) {
			return novoCapcha();
		} else if (Constantes.LOGAR.equals(nnpeTO.getComando())) {
			return logar((NnpeCliente) nnpeTO.getData());
		}

		return null;
	}

	public NnpeDadosChat getNnpeDadosChat() {
		return nnpeDadosChat;
	}

	public Object logar(NnpeCliente nnpeCliente) {
		NnpeUsuario usuario = new NnpeUsuario();
		Session session = NnpePersistencia.getSession();
		try {
			List usuarios = session
					.createCriteria(NnpeUsuario.class)
					.add(Restrictions.eq("login", nnpeCliente.getNomeJogador()))
					.list();
			usuario = (NnpeUsuario) (usuarios.isEmpty() ? null : usuarios
					.get(0));
			if (usuario == null) {
				return new MsgSrv(Lang.msg("usuarioNaoEncontrado"));
			}
			if (!usuario.getSenha().equals(nnpeCliente.getSenhaJogador())) {
				return new MsgSrv(Lang.msg("senhaIncorreta"));
			}
			return criarSessao(usuario);
		} finally {
			HibernateUtil.closeSession();
		}

	}

	private Object criarSessao(NnpeUsuario usuario) {
		SessaoCliente sessaoCliente = null;
		Collection clientes = nnpeDadosChat.getClientes();
		for (Iterator iterator = clientes.iterator(); iterator.hasNext();) {
			SessaoCliente object = (SessaoCliente) iterator.next();
			if (object.getNomeJogador().equals(usuario.getLogin())) {
				sessaoCliente = object;
				break;
			}
		}
		if (sessaoCliente == null) {
			sessaoCliente = new SessaoCliente();
			sessaoCliente.setNomeJogador(usuario.getLogin());
			nnpeDadosChat.getClientes().add(sessaoCliente);
		}

		sessaoCliente.setUlimaAtividade(System.currentTimeMillis());

		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setData(sessaoCliente);
		Logger.logar("Sessao criada para " + sessaoCliente.getNomeJogador());
		return nnpeTO;
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
			return nnpeTO;
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		return new ErroServ(Lang.msg("erroCapcha"));
	}

	private Object atualizarDadosVisao(NnpeTO nnpeTO) {
		if (nnpeTO.getSessaoCliente() != null) {
			nnpeChatServidor.atualizaSessaoCliente(nnpeTO.getSessaoCliente());
		}
		nnpeTO.setData(nnpeDadosChat);
		return nnpeTO;
	}

	public void removerClienteInativo(SessaoCliente sessaoClienteRemover) {
		Logger.logar("removerClienteInativo " + sessaoClienteRemover);
		nnpeDadosChat.getClientes().remove(sessaoClienteRemover);
	}

	public boolean verificaSemSessao(String nomeCriador) {
		if (Util.isNullOrEmpty(nomeCriador)) {
			return true;
		}
		Collection<SessaoCliente> clientes = nnpeDadosChat.getClientes();
		for (Iterator iter = clientes.iterator(); iter.hasNext();) {
			SessaoCliente sessaoCliente = (SessaoCliente) iter.next();
			if (nomeCriador.equals(sessaoCliente.getNomeJogador())) {
				return false;
			}
		}
		return true;

	}

}
