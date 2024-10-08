package br.servlet;

import br.nnpe.Logger;
import br.nnpe.servidor.NnpeServlet;
import br.topwar.ProxyComandos;
import br.topwar.recursos.CarregadorRecursos;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.EnumSet;
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
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(20000);
                        createSchema(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            Logger.logarExept(e);
        }
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
                createSchema(printWriter);
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

    private void createSchema(PrintWriter printWriter)
            throws Exception {
        SchemaExport export = new SchemaExport();
        export.create(EnumSet.of(TargetType.DATABASE), getMetaData().buildMetadata());
    }

    private MetadataSources getMetaData() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(CarregadorRecursos.recursoComoStream("META-INF/persistence.xml"));
        NodeList list = doc.getElementsByTagName("property");
        String url = null, pass = null, user = null, driver = null;
        for (int temp = 0; temp < list.getLength(); temp++) {
            Node node = list.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String attr = element.getAttribute("name");
                if ("javax.persistence.jdbc.url".equals(attr)) {
                    url = element.getAttribute("value");
                } else if ("javax.persistence.jdbc.user".equals(attr)) {
                    user = element.getAttribute("value");
                } else if ("javax.persistence.jdbc.password".equals(attr)) {
                    pass = element.getAttribute("value");
                } else if ("javax.persistence.jdbc.driver".equals(attr)) {
                    driver = element.getAttribute("value");
                }
            }
        }
        Class.forName(driver);
        Connection connection =
                DriverManager.getConnection(url, user, pass);
        MetadataSources metadata = new MetadataSources(
                new StandardServiceRegistryBuilder()
                        .applySetting("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect")
                        .applySetting(AvailableSettings.CONNECTION_PROVIDER, new MyConnectionProvider(connection))
                        .build());

        metadata.addAnnotatedClass(br.nnpe.persistencia.NnpeUsuario.class);
        return metadata;
    }

    private static class MyConnectionProvider implements ConnectionProvider {
        private final Connection connection;

        public MyConnectionProvider(Connection connection) {
            this.connection = connection;
        }

        @Override
        public boolean isUnwrappableAs(Class unwrapType) {
            return false;
        }

        @Override
        public <T> T unwrap(Class<T> unwrapType) {
            return null;
        }

        @Override
        public Connection getConnection() {
            return connection; // Interesting part here
        }

        @Override
        public void closeConnection(Connection conn) throws SQLException {
        }

        @Override
        public boolean supportsAggressiveRelease() {
            return true;
        }
    }
}
