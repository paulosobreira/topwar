package br.servlet;

import br.nnpe.Logger;
import br.nnpe.servidor.NnpeServlet;
import br.topwar.ProxyComandos;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

/**
 * @author paulo.sobreira
 */
public class ServletTopWar extends NnpeServlet {

    private static final long serialVersionUID = 1L;

    public void init() throws ServletException {
        super.init();
        proxyComandos = new ProxyComandos(webDir, webInfDir);
    }

    @Override
    public void destroy() {
        proxyComandos.pararMonitor();
        super.destroy();
    }

    public void doGetHtml(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        String param = request.getParameter("act");
        response.setContentType("text/html");
        PrintWriter printWriter = response.getWriter();
        try {
            printWriter.println("<html><body>");


            if ("create_schema".equals(param)) {
            } else if ("update_schema".equals(param)) {
            } else if ("x".equals(param)) {
                topExceptions(response, printWriter);
            }
            printWriter.println("<br/> ");
        } catch (Exception e) {
            printWriter.println(e.getMessage());
        }
        printWriter.println("<br/><a href='conf.jsp'>back</a>");
        printWriter.println("</body></html>");
        response.flushBuffer();
    }

    private void topExceptions(HttpServletResponse res, PrintWriter printWriter)
            throws IOException {

        printWriter.write("<h2>TopWar Erros</h2><br><hr>");
        synchronized (Logger.topExceptions) {
            Set top = Logger.topExceptions.keySet();
            for (Iterator iterator = top.iterator(); iterator.hasNext(); ) {
                String exept = (String) iterator.next();
                printWriter.write("Quantidade : "
                        + Logger.topExceptions.get(exept));
                printWriter.write("<br>");
                printWriter.write(exept);
                printWriter.write("<br><hr>");

            }
        }
        res.flushBuffer();
    }


}
