package br.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;

import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.persistencia.HibernateUtil;
import br.nnpe.servidor.NnpeServlet;
import br.topwar.ProxyComandos;

/**
 * @author paulo.sobreira
 * 
 */
public class ServletTopWar extends NnpeServlet {

	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
		super.init();
		proxyComandos = new ProxyComandos(webDir, webInfDir);
		try {
			atualizarJnlp("topwarOnline.jnlp");
			atualizarJnlp("topwar.jnlp");
			copiaJars();
		} catch (Exception e) {
			Logger.logarExept(e);
		}
	}

	private void copiaJars() {
		try {
			String[] files = { "commons-collections-3.1.jar",
					"commons-logging-1.1.1.jar", "jcaptcha-1.0-all.jar",
					"hibernate-core.jar" };
			for (int i = 0; i < files.length; i++) {
				String file = files[i];
				String oriPath = webDir.replace("webapps" + File.separator
						+ "topwar", "lib")
						+ file;
				String dstPath = webDir + File.separator + file;
				Util.copyFile(new File(oriPath), new File(dstPath));

			}
		} catch (IOException e) {
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

			AnnotationConfiguration cfg = new AnnotationConfiguration();
			cfg.configure("hibernate.cfg.xml");

			SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

			if ("create_schema".equals(param)) {
				createSchema(cfg, sessionFactory, printWriter);
			} else if ("update_schema".equals(param)) {
				updateSchema(cfg, sessionFactory, printWriter);
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

		printWriter.write("<h2>TopWar Exceções</h2><br><hr>");
		synchronized (Logger.topExceptions) {
			Set top = Logger.topExceptions.keySet();
			for (Iterator iterator = top.iterator(); iterator.hasNext();) {
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

	private void updateSchema(AnnotationConfiguration cfg,
			SessionFactory sessionFactory, PrintWriter printWriter)
			throws SQLException {
		Dialect dialect = Dialect.getDialect(cfg.getProperties());
		Session session = sessionFactory.openSession();
		DatabaseMetadata meta = new DatabaseMetadata(session.connection(),
				dialect);
		String[] strings = cfg.generateSchemaUpdateScript(dialect, meta);
		executeStatement(sessionFactory, strings, printWriter);

	}

	private void executeStatement(SessionFactory sessionFactory,
			String[] strings, PrintWriter printWriter) throws SQLException {

		Session session = sessionFactory.openSession();
		session.beginTransaction();

		for (int i = 0; i < strings.length; i++) {
			String string = strings[i];
			java.sql.Statement statement = session.connection()
					.createStatement();
			statement.execute(string);
			printWriter.println("<br/> " + string);
		}

		session.flush();

	}

	private void createSchema(AnnotationConfiguration cfg,
			SessionFactory sessionFactory, PrintWriter printWriter)
			throws HibernateException, SQLException {
		Dialect dialect = Dialect.getDialect(cfg.getProperties());
		String[] strings = cfg.generateSchemaCreationScript(dialect);
		executeStatement(sessionFactory, strings, printWriter);
	}

}
