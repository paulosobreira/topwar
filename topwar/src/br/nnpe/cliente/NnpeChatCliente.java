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
import br.nnpe.tos.NnpeDados;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.topwar.recursos.idiomas.Lang;

public abstract class NnpeChatCliente {
	protected NnpeApplet nnpeApplet;
	protected Thread threadAtualizadora;
	protected SessaoCliente sessaoCliente;
	protected NnpeFormLogin nnpeFormLogin;
	protected NnpeChatWindow nnpeChatWindow;
	private int problemasRede;

	public NnpeChatCliente(NnpeApplet nnpeApplet) {
		this.nnpeApplet = nnpeApplet;
		if (nnpeApplet == null) {
			return;
		}
		threadAtualizadora = new Thread(new Runnable() {

			public void run() {
				while (NnpeChatCliente.this.nnpeApplet.isComunicacaoServer()) {
					try {
						while (!NnpeChatCliente.this.nnpeApplet.isInit()) {
							Thread.sleep(20);
						}
						atualizaVisao();
						Thread.sleep(5000);
					} catch (Exception e) {
						Logger.logarExept(e);
						sair();
					}
				}
			}
		});
		definirImplementacaoChatWindow();
		nnpeApplet.setLayout(new BorderLayout());
		nnpeApplet.add(nnpeChatWindow.getMainPanel(), BorderLayout.CENTER);
		threadAtualizadora.setPriority(Thread.MIN_PRIORITY);
		threadAtualizadora.start();
	}

	public boolean isComunicacaoServer() {
		if (nnpeApplet != null)
			return nnpeApplet.isComunicacaoServer();
		return true;
	}

	public void definirImplementacaoChatWindow() {
		this.nnpeChatWindow = new NnpeChatWindow(this);
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
		NnpeDados nnpeDadosChat = (NnpeDados) nnpeTO.getData();
		nnpeChatWindow.atualizar(nnpeDadosChat);
	}

	public void logar() {
		if (nnpeApplet == null) {
			return;
		}
		nnpeFormLogin = getNnpeFormLogin();
		nnpeFormLogin.setToolTipText(Lang.msg("formularioLogin"));
		try {
			PersistenceService persistenceService = (PersistenceService) ServiceManager
					.lookup("javax.jnlp.PersistenceService");
			FileContents fileContents = persistenceService
					.get(nnpeApplet.getCodeBase());
			if (fileContents == null) {
				Logger.logar(" fileContents == null  ");
			}
			ObjectInputStream ois = new ObjectInputStream(
					fileContents.getInputStream());
			Map map = (Map) ois.readObject();
			String login = (String) map.get("login");
			String pass = (String) map.get("pass");
			if (!Util.isNullOrEmpty(pass) && !Util.isNullOrEmpty(login)) {
				nnpeFormLogin.getNomeLogar().setText(login);
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
			logarRecuperarLembrar();
			atualizaVisao();
			if (nnpeFormLogin.getLembrar().isSelected()) {
				String login = nnpeFormLogin.getNomeLogar().getText();
				String pass = String
						.valueOf((nnpeFormLogin.getSenha().getPassword()));
				lembrarSenha(login, pass);
			}
		}

	}

	private void lembrarSenha(String login, String pass) {
		try {
			PersistenceService persistenceService = (PersistenceService) ServiceManager
					.lookup("javax.jnlp.PersistenceService");
			FileContents fileContents = null;
			try {
				fileContents = persistenceService.get(nnpeApplet.getCodeBase());
			} catch (Exception e) {
				persistenceService.create(nnpeApplet.getCodeBase(), 1024);
				fileContents = persistenceService.get(nnpeApplet.getCodeBase());
			}

			if (fileContents == null) {
				Logger.logar(" fileContents == null  ");

			}

			Map map = new HashMap();
			map.put("login", login);
			map.put("pass", pass);
			ObjectOutputStream stream = new ObjectOutputStream(
					fileContents.getOutputStream(true));
			stream.writeObject(map);
			stream.flush();

		} catch (Exception e) {
			Logger.logarExept(e);
		}
	}

	private boolean logarRecuperarLembrar() {
		NnpeTO nnpeTO = new NnpeTO();
		NnpeCliente nnpeCliente = new NnpeCliente();
		nnpeTO.setData(nnpeCliente);

		if (!Util.isNullOrEmpty(nnpeFormLogin.getNomeRegistrar().getText())
				&& !Util.isNullOrEmpty(nnpeFormLogin.getEmail().getText())) {
			nnpeCliente
					.setNomeJogador(nnpeFormLogin.getNomeRegistrar().getText());
			nnpeCliente.setEmailJogador(nnpeFormLogin.getEmail().getText());
			nnpeTO.setComando(Constantes.NOVO_USUARIO);
			nnpeFormLogin.getNomeLogar().setText("");
		}

		if (!Util.isNullOrEmpty(nnpeFormLogin.getNomeLogar().getText())) {
			nnpeCliente.setNomeJogador(nnpeFormLogin.getNomeLogar().getText());
			try {
				if (!Util.isNullOrEmpty(
						new String(nnpeFormLogin.getSenha().getPassword()))) {
					nnpeCliente.setSenhaJogador(Util.md5(new String(
							nnpeFormLogin.getSenha().getPassword())));
				}
			} catch (Exception e) {
				Logger.logarExept(e);
				JOptionPane.showMessageDialog(nnpeChatWindow.getMainPanel(),
						e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
			}
			nnpeTO.setComando(Constantes.LOGAR);
		}
		if (Util.isNullOrEmpty(nnpeTO.getComando())) {
			JOptionPane.showMessageDialog(nnpeChatWindow.getMainPanel(),
					Lang.msg("opercaoLogarInvalida"), "Erro",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		Logger.logar(
				"registrarUsuario nnpeTO.getComando() " + nnpeTO.getComando());
		Object ret = nnpeApplet.enviarObjeto(nnpeTO);
		if (ret == null) {
			return false;
		}
		if (ret instanceof NnpeTO) {
			nnpeTO = (NnpeTO) ret;
			SessaoCliente cliente = (SessaoCliente) nnpeTO.getData();
			if (cliente.getSenhaCriada() != null) {
				lembrarSenha(cliente.getNomeJogador(),
						cliente.getSenhaCriada());
				JOptionPane.showMessageDialog(nnpeApplet,
						Lang.msg("senhaGerada",
								new String[]{cliente.getNomeJogador(),
										cliente.getSenhaCriada()}),
						Lang.msg("guardeSenhaGerada"),
						JOptionPane.INFORMATION_MESSAGE);

			}
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
		nnpeCliente.setTextoChat(text);
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setData(nnpeCliente);
		nnpeTO.setComando(Constantes.ENVIAR_TEXTO);
		Object ret = nnpeApplet.enviarObjeto(nnpeTO);
		if (retornoNaoValido(ret)) {
			return;
		}
		if (ret == null) {
			if (problemasRede > 10) {
				nnpeApplet.comunicacaoServer = false;
			}
			if (problemasRede > 0) {
				return;
			}
			problemasRede++;
			JOptionPane.showMessageDialog(nnpeChatWindow.getMainPanel(),
					Lang.msg("problemasRede"), "Erro",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		nnpeTO = (NnpeTO) ret;
		nnpeChatWindow.atualizar((NnpeDados) nnpeTO.getData());
		problemasRede = 0;

	}

	public Object enviarObjeto(NnpeTO nnpeTO) {
		return nnpeApplet.enviarObjeto(nnpeTO);
	}

	private boolean retornoNaoValido(Object ret) {
		if (ret instanceof ErroServ || ret instanceof MsgSrv) {
			return true;
		}
		return false;
	}

	public int getLatenciaMinima() {
		if (nnpeApplet != null)
			return nnpeApplet.getLatenciaMinima();
		return NnpeApplet.LATENCIA_MIN;
	}

	public int getLatenciaReal() {
		if (nnpeApplet != null)
			return nnpeApplet.getLatenciaReal();
		return 0;
	}

	public void atualizaInfo() {
		if (nnpeChatWindow != null)
			nnpeChatWindow.atualizaInfo();

	}

	public String getVersao() {
		if (nnpeApplet != null)
			return nnpeApplet.getVersao();
		return "impl ver local";
	}

	public void sair() {
		if (sessaoCliente != null) {
			NnpeCliente nnpeCliente = new NnpeCliente(sessaoCliente);
			NnpeTO nnpeTO = new NnpeTO();
			nnpeTO.setData(nnpeCliente);
			nnpeTO.setComando(Constantes.ENCERRAR_SESSAO);
			Object ret = enviarObjeto(nnpeTO);
		}
		sessaoCliente = null;
		if (threadAtualizadora != null) {
			threadAtualizadora.interrupt();
		}
	}

	public SessaoCliente getSessaoCliente() {
		return sessaoCliente;
	}

}
