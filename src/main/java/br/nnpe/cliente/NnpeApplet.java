package br.nnpe.cliente;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.sound.midi.Sequencer;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import br.nnpe.Constantes;
import br.nnpe.Logger;
import br.nnpe.ZipUtil;
import br.nnpe.tos.ErroServ;
import br.nnpe.tos.MsgSrv;
import br.topwar.ConstantesTopWar;
import br.topwar.recursos.idiomas.Lang;

/**
 * @author Sobreira
 */
public abstract class NnpeApplet extends JFrame {
    private static final long serialVersionUID = 1L;

    protected URL url;
    protected Properties properties;
    protected String urlSufix;

    public static final int LATENCIA_MAX = ConstantesTopWar.LATENCIA_MAX;
    public static final int LATENCIA_MIN = ConstantesTopWar.LATENCIA_MIN;
    protected int latenciaMinima = LATENCIA_MIN;
    protected int latenciaReal;

    public abstract NnpeChatCliente getNnpeChatCliente();

    protected List pacotes = new LinkedList();

    protected boolean comunicacaoServer = true;

    protected Sequencer sequencer;

    protected LookAndFeelInfo[] looks;
    DecimalFormat decimalFormat = new DecimalFormat("#,##");
    protected String versao;

    private boolean init;

    public void init() {
        try {
            url = getCodeBase();
            properties = new Properties();
            properties.load(this.getClass().getResourceAsStream(
                    "/application.properties"));
            this.urlSufix = properties.getProperty("servidor");
            this.versao = properties.getProperty("versao");
            setSize(820, 380);
            setVisible(true);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getNnpeChatCliente().logar();
                }
            });
            thread.start();
        } catch (Exception e) {
            StackTraceElement[] trace = e.getStackTrace();
            StringBuffer retorno = new StringBuffer();
            int size = ((trace.length > 10) ? 10 : trace.length);

            for (int i = 0; i < size; i++)
                retorno.append(trace[i] + "\n");
            JOptionPane.showMessageDialog(this, retorno.toString(),
                    Lang.msg("erroEnviando"), JOptionPane.ERROR_MESSAGE);
            Logger.logarExept(e);
        }
        init = true;
    }

    URL codeBase;

    public URL getCodeBase() {
        return codeBase;
    }

    public void setCodeBase(URL codeBase) {
        this.codeBase = codeBase;
    }

    public boolean isInit() {
        return init;
    }

    public void destroy() {
        Logger.logar("Applet destroy()");
        getNnpeChatCliente().sair();
        comunicacaoServer = false;
    }

    public Object enviarObjeto(Object enviar) {
        return enviarObjeto(enviar, false);
    }

    public Object enviarObjeto(Object enviar, boolean timeout) {
        try {
            String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();
            URL dataUrl;
            long envioT = System.currentTimeMillis();
            Object retorno = null;
            dataUrl = new URL(protocol, host, port, urlSufix);
            URLConnection connection = dataUrl.openConnection();
            try {
                connection.setUseCaches(false);
                connection.setDoOutput(true);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream stream = new ObjectOutputStream(
                        byteArrayOutputStream);
                if (latenciaReal > 0 && timeout
                        && latenciaReal > latenciaMinima)
                    connection.setReadTimeout(latenciaReal);
                stream.writeObject(enviar);
                stream.flush();
                connection.setRequestProperty("Content-Length",
                        String.valueOf(byteArrayOutputStream.size()));
                connection.setRequestProperty("Content-Length",
                        "application/x-www-form-urlencoded");
                connection.getOutputStream().write(
                        byteArrayOutputStream.toByteArray());
                if (Constantes.modoZip) {
                    retorno = ZipUtil.descompactarObjeto(connection
                            .getInputStream());
                } else {
                    ObjectInputStream ois = new ObjectInputStream(
                            connection.getInputStream());
                    retorno = ois.readObject();
                }
            } catch (Exception e) {
                Logger.logarExept(e);
                return null;
            }
            long retornoT = System.currentTimeMillis();
            if (!timeout) {
                atualizarLantenciaMinima(envioT, retornoT);
            }
            if (retorno instanceof ErroServ) {
                ErroServ erroServ = (ErroServ) retorno;
                Logger.logar(erroServ.obterErroFormatado());
                JOptionPane.showMessageDialog(this,
                        Lang.decodeTexto(erroServ.obterErroFormatado()),
                        Lang.msg("erroRecebendo"), JOptionPane.ERROR_MESSAGE);
                return null;
            }
            if (retorno instanceof MsgSrv) {
                MsgSrv msgSrv = (MsgSrv) retorno;
                JOptionPane.showMessageDialog(this,
                        Lang.msg(Lang.decodeTexto(msgSrv.getMessageString())),
                        Lang.msg("msgServidor"),
                        JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
            return retorno;
        } catch (Exception e) {
            setComunicacaoServer(false);
            StackTraceElement[] trace = e.getStackTrace();
            StringBuffer retorno = new StringBuffer();
            int size = ((trace.length > 10) ? 10 : trace.length);

            for (int i = 0; i < size; i++)
                retorno.append(trace[i] + "\n");
            Logger.logarExept(e);
            JOptionPane.showMessageDialog(this, retorno.toString(),
                    Lang.msg("erroEnviando"), JOptionPane.ERROR_MESSAGE);
        }

        return null;
    }

    protected void atualizarLantenciaMinima(long envioT, long retornoT) {
        synchronized (pacotes) {
            if (pacotes.size() > 10) {
                pacotes.remove(0);
            }
            pacotes.add(new Long(retornoT - envioT));
            if (pacotes.size() >= 10) {
                long somatorio = 0;
                for (Iterator iter = pacotes.iterator(); iter.hasNext(); ) {
                    Long longElement = (Long) iter.next();
                    somatorio += longElement.longValue();
                }
                int media = (int) (somatorio / 10);
                if (media > LATENCIA_MAX) {
                    setLatenciaMinima(LATENCIA_MAX);
                } else {
                    setLatenciaMinima(media);
                }
                if (media < LATENCIA_MIN)
                    setLatenciaMinima(LATENCIA_MIN);
                else if (media < LATENCIA_MAX) {
                    setLatenciaMinima(media);
                }
                setLatenciaReal(media);
                if (getNnpeChatCliente() != null) {
                    getNnpeChatCliente().atualizaInfo();
                }
            }
        }
    }

    public int getLatenciaMinima() {
        return latenciaMinima;
    }

    public void setLatenciaMinima(int latenciaMinima) {
        this.latenciaMinima = latenciaMinima;
    }

    public int getLatenciaReal() {
        return latenciaReal;
    }

    public void setLatenciaReal(int latenciaReal) {
        this.latenciaReal = latenciaReal;
    }

    public boolean isComunicacaoServer() {
        return comunicacaoServer;
    }

    public void setComunicacaoServer(boolean comunicacaoServer) {
        this.comunicacaoServer = comunicacaoServer;
    }

    public String getVersao() {
        if (versao == null) {
            properties = new Properties();

            try {
                properties.load(this.getClass().getResourceAsStream(
                        "/application.properties"));
            } catch (IOException e) {
                Logger.logarExept(e);
            }
            this.urlSufix = properties.getProperty("servidor");
            this.versao = properties.getProperty("versao");
        }
        return " " + decimalFormat.format(new Integer(versao));
    }

}
