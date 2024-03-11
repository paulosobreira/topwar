package br.nnpe.cliente;

import java.awt.BorderLayout;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

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
        int result = JOptionPane.showConfirmDialog(
                nnpeChatWindow.getMainPanel(), nnpeFormLogin,
                Lang.msg("formularioLogin"), JOptionPane.OK_CANCEL_OPTION);

        if (JOptionPane.OK_OPTION == result) {
            logarRecuperarLembrar();
            atualizaVisao();
        }else{
            logarGuest();
        }
    }

    public void logarGuest() {
        NnpeTO nnpeTO = new NnpeTO();
        NnpeCliente nnpeCliente = new NnpeCliente();
        nnpeTO.setData(nnpeCliente);
        nnpeTO.setComando(Constantes.LOGAR_GUEST);
        Object ret = nnpeApplet.enviarObjeto(nnpeTO);
        if (ret instanceof NnpeTO) {
            nnpeTO = (NnpeTO) ret;
            SessaoCliente cliente = (SessaoCliente) nnpeTO.getData();
            this.sessaoCliente = cliente;
        }
        atualizaVisao();

    }

    private boolean logarRecuperarLembrar() {
        NnpeTO nnpeTO = new NnpeTO();
        NnpeCliente nnpeCliente = new NnpeCliente();
        nnpeTO.setData(nnpeCliente);

        if (!Util.isNullOrEmpty(nnpeFormLogin.getNomeRegistrar().getText())
                && !Util.isNullOrEmpty(nnpeFormLogin.getEmail().getText())) {
            int resultado = 0;
            try {
                resultado = Integer
                        .parseInt(nnpeFormLogin.getResultadorConta().getText());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(nnpeChatWindow.getMainPanel(),
                        Lang.msg("resultadoContaErrado"), Lang.msg("erro"),
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if ((nnpeFormLogin.getConta1()
                    + nnpeFormLogin.getConta2()) != resultado) {
                JOptionPane.showMessageDialog(nnpeChatWindow.getMainPanel(),
                        Lang.msg("resultadoContaErrado"), Lang.msg("erro"),
                        JOptionPane.ERROR_MESSAGE);
                return false;

            }
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
            this.sessaoCliente = cliente;
            if (sessaoCliente.getSenhaCriada() != null) {
                nnpeChatWindow.getTextAreaChat().append(Lang.msg("guardeSenhaGerada") + " - " + Lang.msg("senhaGerada", new String[]{sessaoCliente.getNomeJogador(), sessaoCliente.getSenhaCriada()}));
            }
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
