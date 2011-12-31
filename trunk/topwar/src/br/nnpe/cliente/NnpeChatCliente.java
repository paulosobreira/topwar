package br.nnpe.cliente;

import java.awt.BorderLayout;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.swing.JOptionPane;

import br.nnpe.Constantes;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.tos.ErroServ;
import br.nnpe.tos.MsgSrv;
import br.nnpe.tos.NnpeCliente;
import br.nnpe.tos.NnpeDadosChat;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.topwar.recursos.idiomas.Lang;

public abstract class NnpeChatCliente {
	protected NnpeApplet nnpeApplet;
	protected Thread threadAtualizadora;
	protected SessaoCliente sessaoCliente;
	protected boolean comunicacaoServer = true;
	protected NnpeFormLogin nnpeFormLogin;
	protected NnpeChatWindow nnpeChatWindow;

	public NnpeChatCliente(NnpeApplet nnpeApplet) {
		this.nnpeApplet = nnpeApplet;
		threadAtualizadora = new Thread(new Runnable() {

			public void run() {
				while (comunicacaoServer) {
					try {
						Thread.sleep(10000);
						atualizaVisao();
					} catch (Exception e) {
						Logger.logarExept(e);
					}
				}
			}
		});
		setChatWindow();
//		atualizaVisao();
		nnpeApplet.setLayout(new BorderLayout());
		nnpeApplet.add(nnpeChatWindow.getMainPanel(), BorderLayout.CENTER);
		threadAtualizadora.setPriority(Thread.MIN_PRIORITY);
		threadAtualizadora.start();
	}

	private void setChatWindow() {
		nnpeChatWindow = new NnpeChatWindow(this);
	}

	protected void atualizaVisao() {
		if (nnpeChatWindow == null || nnpeApplet == null) {
			return;
		}
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(Constantes.ATUALIZAR_VISAO);
		nnpeTO.setSessaoCliente(sessaoCliente);
		Object ret = nnpeApplet.enviarObjeto(nnpeTO);
		if (ret == null) {
			return;
		}
		nnpeTO = (NnpeTO) ret;
		NnpeDadosChat nnpeDadosChat = (NnpeDadosChat) nnpeTO.getData();
		nnpeChatWindow.atualizar(nnpeDadosChat);
	}

	public void logar() {
		nnpeFormLogin = getNnpeFormLogin();
		nnpeFormLogin.setToolTipText(Lang.msg("formularioLogin"));
		try {
			PersistenceService persistenceService = (PersistenceService) ServiceManager
					.lookup("javax.jnlp.PersistenceService");
			FileContents fileContents = persistenceService.get(nnpeApplet
					.getCodeBase());
			if (fileContents == null) {
				Logger.logar(" fileContents == null  ");
			}
			ObjectInputStream ois = new ObjectInputStream(
					fileContents.getInputStream());
			Map map = (Map) ois.readObject();
			String login = (String) map.get("login");
			String pass = (String) map.get("pass");
			if (!Util.isNullOrEmpty(pass) && !Util.isNullOrEmpty(login)) {
				nnpeFormLogin.getNome().setText(login);
				nnpeFormLogin.getSenha().setText(pass);
				nnpeFormLogin.getLembrar().setSelected(true);
			}
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		int result = JOptionPane.showConfirmDialog(
				nnpeChatWindow.getMainPanel(), nnpeFormLogin,
				Lang.msg("formularioLogin"), JOptionPane.OK_CANCEL_OPTION);

		if (JOptionPane.OK_OPTION == result) {
			registrarUsuario();
			atualizaVisao();
			if (nnpeFormLogin.getLembrar().isSelected()) {
				try {
					PersistenceService persistenceService = (PersistenceService) ServiceManager
							.lookup("javax.jnlp.PersistenceService");
					FileContents fileContents = null;
					try {
						fileContents = persistenceService.get(nnpeApplet
								.getCodeBase());
					} catch (Exception e) {
						persistenceService.create(nnpeApplet.getCodeBase(),
								1024);
						fileContents = persistenceService.get(nnpeApplet
								.getCodeBase());
					}

					if (fileContents == null) {
						Logger.logar(" fileContents == null  ");

					}

					Map map = new HashMap();
					map.put("login", nnpeFormLogin.getNome().getText());
					map.put("pass", String.valueOf((nnpeFormLogin.getSenha()
							.getPassword())));
					ObjectOutputStream stream = new ObjectOutputStream(
							fileContents.getOutputStream(true));
					stream.writeObject(map);
					stream.flush();

				} catch (Exception e) {
					Logger.logarExept(e);
				}
			}
		}

	}

	private boolean registrarUsuario() {
		NnpeTO mesa11to = new NnpeTO();
		NnpeCliente clienteMesa11 = new NnpeCliente();
		mesa11to.setData(clienteMesa11);
		clienteMesa11.setNomeJogador(nnpeFormLogin.getNome().getText());

		try {
			if (!Util.isNullOrEmpty(new String(nnpeFormLogin.getSenha()
					.getPassword()))) {
				clienteMesa11.setSenhaJogador(Util.md5(new String(nnpeFormLogin
						.getSenha().getPassword())));
			}
		} catch (Exception e) {
			Logger.logarExept(e);
			JOptionPane.showMessageDialog(nnpeChatWindow.getMainPanel(),
					e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
		}
		clienteMesa11.setEmailJogador(nnpeFormLogin.getEmail().getText());
		// clienteMesa11.setRecuperar(nnpeFormLogin.getRecuperar().isSelected());
		// clienteMesa11.setChaveCapcha(nnpeFormLogin.getCapchaChave());
		clienteMesa11.setTexto(nnpeFormLogin.getCapchaTexto());

		if (!Util.isNullOrEmpty(clienteMesa11.getNomeJogador())
				&& !Util.isNullOrEmpty(clienteMesa11.getSenhaJogador())) {
			mesa11to.setComando(Constantes.LOGAR);
		} else if (!Util.isNullOrEmpty(clienteMesa11.getNomeJogador())
				&& !Util.isNullOrEmpty(clienteMesa11.getEmailJogador())
				&& !clienteMesa11.isRecuperar()) {
			mesa11to.setComando(Constantes.NOVO_USUARIO);
		} else if (clienteMesa11.isRecuperar()) {
			mesa11to.setComando(Constantes.RECUPERA_SENHA);
		}
		Logger.logar("registrarUsuario mesa11to.getComando() "
				+ mesa11to.getComando());
		Object ret = nnpeApplet.enviarObjeto(mesa11to);
		if (ret == null) {
			return false;
		}
		if (ret instanceof NnpeTO) {
			mesa11to = (NnpeTO) ret;
			SessaoCliente cliente = (SessaoCliente) mesa11to.getData();
			this.sessaoCliente = cliente;
		}
		return true;
	}

	private NnpeFormLogin getNnpeFormLogin() {
		return new NnpeFormLogin(nnpeApplet);
	}

	public void enviarTexto(String text) {
		if (sessaoCliente == null) {
			logar();
			return;
		}
		NnpeCliente nnpeCliente = new NnpeCliente(sessaoCliente);
		nnpeCliente.setTexto(text);
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setData(nnpeCliente);
		nnpeTO.setComando(Constantes.ENVIAR_TEXTO);
		Object ret = nnpeApplet.enviarObjeto(nnpeTO);
		if (retornoNaoValido(ret)) {
			return;
		}
		if (ret == null) {
			JOptionPane.showMessageDialog(nnpeChatWindow.getMainPanel(),
					Lang.msg("problemasRede"), "Erro",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		nnpeTO = (NnpeTO) ret;
		nnpeChatWindow.atualizar((NnpeDadosChat) nnpeTO.getData());

	}

	private boolean retornoNaoValido(Object ret) {
		if (ret instanceof ErroServ || ret instanceof MsgSrv) {
			return true;
		}
		return false;
	}

	public int getLatenciaMinima() {
		return nnpeApplet.getLatenciaMinima();
	}

	public int getLatenciaReal() {
		return nnpeApplet.getLatenciaReal();
	}

	public void atualizaInfo() {
		if (nnpeChatWindow != null)
			nnpeChatWindow.atualizaInfo();

	}

	public String getVersao() {
		return nnpeApplet.getVersao();
	}
}