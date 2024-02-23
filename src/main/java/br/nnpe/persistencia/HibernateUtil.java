package br.nnpe.persistencia;

import com.fasterxml.classmate.AnnotationConfiguration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import br.nnpe.Logger;

/**
 * @author Paulo Sobreira [sowbreira@gmail.com]
 * @author Rafael Carneiro [rafaelcarneirob@gmail.com]
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;


    public static SessionFactory getSessionFactory() {
        if (sessionFactory != null && Logger.novaSession) {
            sessionFactory.close();
        }
        if (Logger.novaSession) {
            try {

                Logger.novaSession = false;
            } catch (Throwable e) {
                Logger.logarExept(e);
            }
        }
        return sessionFactory;
    }

    public static final ThreadLocal sessionThreadLocal = new ThreadLocal();

    public static Session currentSession() {
        Session s = (Session) sessionThreadLocal.get();
        if (s == null) {
            s = getSessionFactory().openSession();
            sessionThreadLocal.set(s);
        }
        return s;
    }

    public static void closeSession() {
        Session s = (Session) sessionThreadLocal.get();
        if (s != null && s.isOpen())
            s.close();
        sessionThreadLocal.set(null);
    }
}
