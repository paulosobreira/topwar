package br.nnpe.servidor;

import br.nnpe.Constantes;
import br.nnpe.Logger;
import br.nnpe.ZipUtil;
import br.topwar.recursos.idiomas.Lang;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Set;

/**
 * @author paulo.sobreira
 */
public class NnpeServlet extends HttpServlet {

    public static String webInfDir;
    protected static String replaceHost = "{host}";
    public static String webDir;
    protected NnpeProxyComandos proxyComandos;

    protected static SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy");

    public void init() throws ServletException {
        super.init();
        webDir = getServletContext().getRealPath("") + File.separator;
        webInfDir = webDir + "WEB-INF" + File.separator;
        Lang.setSrvgame(true);
    }

    public void destroy() {
        super.destroy();
    }

    public void doPost(HttpServletRequest arg0, HttpServletResponse arg1)
            throws ServletException, IOException {
        doGet(arg0, arg1);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        try {
            ObjectInputStream inputStream = null;
            try {
                inputStream = new ObjectInputStream(req.getInputStream());
            } catch (Exception e) {
                Logger.logar("inputStream null - > doGetHtml");
            }

            if (inputStream != null) {
                Object object = null;

                object = inputStream.readObject();

                Object escrever = proxyComandos.processarObjeto(object);

                if (Constantes.modoZip) {
                    dumparDadosZip(ZipUtil.compactarObjeto(Logger.ativo,
                            escrever, res.getOutputStream()));
                } else {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    dumparDados(escrever);
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(escrever);
                    oos.flush();
                    res.getOutputStream().write(bos.toByteArray());
                }

                return;
            } else {
                doGetHtml(req, res);
                return;
            }
        } catch (Exception e) {
            Logger.topExecpts(e);
        }
    }

    private void dumparDadosZip(ByteArrayOutputStream byteArrayOutputStream)
            throws IOException {
        String basePath = getServletContext().getRealPath("")
                + File.separator + "WEB-INF" + File.separator + "dump"
                + File.separator;
        FileOutputStream fileOutputStream = new FileOutputStream(basePath
                + "Pack-" + System.currentTimeMillis() + ".zip");
        fileOutputStream.write(byteArrayOutputStream.toByteArray());
        fileOutputStream.close();

    }

    private void dumparDados(Object escrever) throws IOException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                arrayOutputStream);
        objectOutputStream.writeObject(escrever);
        String basePath = getServletContext().getRealPath("")
                + File.separator + "WEB-INF" + File.separator + "dump"
                + File.separator;
        FileOutputStream fileOutputStream = new FileOutputStream(
                basePath + escrever.getClass().getSimpleName() + "-"
                        + System.currentTimeMillis() + ".txt");
        fileOutputStream.write(arrayOutputStream.toByteArray());
        fileOutputStream.close();
    }

    public void doGetHtml(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

    }

    private void topExceptions(HttpServletResponse res, PrintWriter printWriter)
            throws IOException {

        printWriter.write("<h2>Nnpe Game Erros</h2><br><hr>");
        synchronized (Logger.topExceptions) {
            Set top = Logger.topExceptions.keySet();
            for (Iterator iterator = top.iterator(); iterator.hasNext(); ) {
                String exept = (String) iterator.next();
                printWriter.write(
                        "Quantidade : " + Logger.topExceptions.get(exept));
                printWriter.write("<br>");
                printWriter.write(exept);
                printWriter.write("<br><hr>");
            }
        }
        res.flushBuffer();
    }


}
