package br.nnpe.servidor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import br.nnpe.Constantes;
import br.nnpe.Logger;
import br.nnpe.PassGenerator;
import br.nnpe.Util;
import br.nnpe.persistencia.HibernateUtil;
import br.nnpe.persistencia.NnpeUsuario;
import br.nnpe.tos.ErroServ;
import br.nnpe.tos.MsgSrv;
import br.nnpe.tos.NnpeCliente;
import br.nnpe.tos.NnpeDados;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.topwar.recursos.idiomas.Lang;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

public abstract class NnpeProxyComandos {
	protected NnpeDados nnpeDados;
	protected NnpeChatServidor nnpeChatServidor;
	private NnpeMonitorAtividadeChat nnpeMonitorAtividadeChat;
	protected DefaultManageableImageCaptchaService capcha = new DefaultManageableImageCaptchaService();

	public NnpeProxyComandos(String webDir, String webInfDir) {
		nnpeDados = new NnpeDados();
		nnpeChatServidor = new NnpeChatServidor(nnpeDados);
		nnpeMonitorAtividadeChat = new NnpeMonitorAtividadeChat(this);
		nnpeMonitorAtividadeChat.start();
	}

	public Object processarObjeto(Object object) {
		NnpeTO nnpeTO = (NnpeTO) object;
		if (Constantes.ATUALIZAR_VISAO.equals(nnpeTO.getComando())) {
			return atualizarDadosVisao(nnpeTO);
		} else if (Constantes.ENVIAR_TEXTO.equals(nnpeTO.getComando())) {
			return receberTexto((NnpeCliente) nnpeTO.getData());
		} else if (Constantes.NOVO_CAPCHA.equals(nnpeTO.getComando())) {
			return novoCapcha();
		} else if (Constantes.LOGAR.equals(nnpeTO.getComando())) {
			return logar((NnpeCliente) nnpeTO.getData());
		} else if (Constantes.NOVO_USUARIO.equals(nnpeTO.getComando())) {
			return cadastrarUsuario((NnpeCliente) nnpeTO.getData());
		} else if (Constantes.RECUPERA_SENHA.equals(nnpeTO.getComando())) {
			return recuperaSenha((NnpeCliente) nnpeTO.getData());
		} else if (Constantes.ENCERRAR_SESSAO.equals(nnpeTO.getComando())) {
			return encerrarSessao((NnpeCliente) nnpeTO.getData());
		}

		return null;
	}

	private Object encerrarSessao(NnpeCliente nnpeCliente) {
		SessaoCliente sessaoClienteRemover = null;
		Collection clientes = nnpeDados.getClientes();
		for (Iterator iterator = clientes.iterator(); iterator.hasNext();) {
			SessaoCliente object = (SessaoCliente) iterator.next();
			if (object.equals(nnpeCliente.getSessaoCliente())) {
				sessaoClienteRemover = object;
				break;
			}
		}
		nnpeDados.getClientes().remove(sessaoClienteRemover);
		Logger.logar("Sessao Removida para "
				+ sessaoClienteRemover.getNomeJogador());
		NnpeTO nnpeTO = new NnpeTO();
		nnpeDados.setLinhaChat(Lang.msg("desconectou",
				new String[] { sessaoClienteRemover.getNomeJogador() }));
		nnpeDados.setDataTime(System.currentTimeMillis());
		nnpeTO.setData(nnpeDados);
		return nnpeTO;
	}

	public Object receberTexto(NnpeCliente cliente) {
		if (cliente.getSessaoCliente() == null) {
			return (new MsgSrv(Lang.msg("usuarioSemSessao")));
		}
		nnpeDados
				.atualizaAtividade(cliente.getSessaoCliente().getNomeJogador());
		nnpeDados.setLinhaChat(cliente.getSessaoCliente().getNomeJogador()
				+ " : " + cliente.getTextoChat());
		nnpeDados.setDataTime(System.currentTimeMillis());
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setData(nnpeDados);
		return nnpeTO;
	}

	private Object recuperaSenha(NnpeCliente nnpeCliente) {
		Boolean validateResponseForID = capcha.validateResponseForID(
				nnpeCliente.getChaveCapcha(), nnpeCliente.getTextoCapcha());
		if (!validateResponseForID) {
			return new MsgSrv(Lang.msg("capchaInvalido"));
		}
		NnpeUsuario usuario = new NnpeUsuario();
		Session session = NnpePersistencia.getSession();
		List usuarios = session.createCriteria(NnpeUsuario.class)
				.add(Restrictions.eq("login", nnpeCliente.getNomeJogador()))
				.list();
		usuario = (NnpeUsuario) (usuarios.isEmpty() ? null : usuarios.get(0));
		if (usuario == null) {
			usuarios = session
					.createCriteria(NnpeUsuario.class)
					.add(Restrictions.eq("email", nnpeCliente.getEmailJogador()))
					.list();
			usuario = (NnpeUsuario) (usuarios.isEmpty() ? null : usuarios
					.get(0));
		}
		if (usuario == null) {
			return new MsgSrv(Lang.msg("usuarioNaoEncontrado"));
		}
		if ((System.currentTimeMillis() - usuario.getUltimaRecuperacao()) < 300000) {
			return new MsgSrv(Lang.msg("limiteTempo"));
		}
		String senha = null;
		try {
			senha = geraSenha(usuario);
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		boolean erroMail = false;
		try {
			mandaMailSenha(usuario.getLogin(), usuario.getEmail(), senha);
		} catch (Exception e1) {
			Logger.logarExept(e1);
			if (NnpeServlet.email != null)
				Logger.logarExept(new Exception("srvEmailFora"));
			erroMail = true;
		}
		String email = usuario.getEmail();
		Transaction transaction = session.beginTransaction();
		try {
			session.saveOrUpdate(usuario);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			return new ErroServ(e);
		}
		if (erroMail) {
			return new MsgSrv(Lang.msg("senhaGerada", new String[] { senha }));
		}
		return new MsgSrv(Lang.msg("senhaEnviada", new String[] { email }));
	}

	private Object cadastrarUsuario(NnpeCliente nnpeCliente) {
		NnpeUsuario usuario = null;
		try {
			Boolean validateResponseForID = capcha.validateResponseForID(
					nnpeCliente.getChaveCapcha(), nnpeCliente.getTextoCapcha());
			if (!validateResponseForID) {
				return new MsgSrv(Lang.msg("capchaInvalido"));
			}
		} catch (Exception e) {
			Logger.logarExept(e);
			return new MsgSrv(Lang.msg("capchaInvalido"));
		}

		Session session = NnpePersistencia.getSession();
		List usuarios = session.createCriteria(NnpeUsuario.class)
				.add(Restrictions.eq("login", nnpeCliente.getNomeJogador()))
				.list();
		usuario = (NnpeUsuario) (usuarios.isEmpty() ? null : usuarios.get(0));
		if (usuario != null) {
			return new MsgSrv(Lang.msg("loginNaoDisponivel"));
		}
		usuarios = session.createCriteria(NnpeUsuario.class)
				.add(Restrictions.eq("email", nnpeCliente.getEmailJogador()))
				.list();
		usuario = (NnpeUsuario) (usuarios.isEmpty() ? null : usuarios.get(0));
		if (usuario != null) {
			return new MsgSrv(Lang.msg("emailJaCadastrado"));
		}
		usuario = new NnpeUsuario();
		usuario.setLogin(nnpeCliente.getNomeJogador());
		usuario.setLoginCriador(nnpeCliente.getNomeJogador());
		usuario.setEmail(nnpeCliente.getEmailJogador());
		String senha = null;
		try {
			geraSenha(usuario);
		} catch (Exception e) {
			return new ErroServ(e);
		}
		boolean erroMail = false;
		try {
			mandaMailSenha(usuario.getLogin(), usuario.getEmail(), senha);
		} catch (Exception e1) {
			Logger.logarExept(e1);
			if (NnpeServlet.email != null)
				Logger.logarExept(new Exception("srvEmailFora"));
			erroMail = true;
		}
		Transaction transaction = session.beginTransaction();
		try {
			if (Util.isNullOrEmpty(usuario.getLoginCriador())) {
				usuario.setLoginCriador(usuario.getLogin());
			}
			session.saveOrUpdate(usuario);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			return new ErroServ(e.getMessage());
		}
		Logger.logar("cadastrarUsuario " + usuario);
		if (erroMail) {
			System.out.println("Senha para Usuario " + usuario + ":" + senha);
		}
		return criarSessao(usuario);
	}

	private String geraSenha(NnpeUsuario usuario) throws Exception {
		PassGenerator generator = new PassGenerator();
		String senha = generator.generateIt();
		Logger.logar("geraSenhaMandaMail " + usuario + " senha " + senha);
		usuario.setSenha(Util.md5(senha));
		return senha;
	}

	private void mandaMailSenha(String nome, String email, String senha)
			throws AddressException, MessagingException {
		Logger.logar("Senha para  :" + nome + " : " + senha);
		NnpeServlet.email.sendSimpleMail("Nnpe Game Password",
				new String[] { email }, "admin@nnpe.com",
				"Your game user:password is " + nome + ":" + senha, false);
	}

	public NnpeDados getNnpeDadosChat() {
		return nnpeDados;
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
		Collection clientes = nnpeDados.getClientes();
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
			nnpeDados.getClientes().add(sessaoCliente);
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
		nnpeTO.setData(nnpeDados);
		return nnpeTO;
	}

	public void removerClienteInativo(SessaoCliente sessaoClienteRemover) {
		Logger.logar("removerClienteInativo " + sessaoClienteRemover);
		nnpeDados.getClientes().remove(sessaoClienteRemover);
	}

	public boolean verificaSemSessao(String nomeCriador) {
		if (Util.isNullOrEmpty(nomeCriador)) {
			return true;
		}
		Collection<SessaoCliente> clientes = nnpeDados.getClientes();
		for (Iterator iter = clientes.iterator(); iter.hasNext();) {
			SessaoCliente sessaoCliente = (SessaoCliente) iter.next();
			if (nomeCriador.equals(sessaoCliente.getNomeJogador())) {
				return false;
			}
		}
		return true;

	}

	public abstract void ganchoMonitorAtividade();

	public void pararMonitor() {
		if (nnpeMonitorAtividadeChat != null) {
			nnpeMonitorAtividadeChat.interrupt();
		}

	}

}
