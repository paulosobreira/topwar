package br.nnpe.persistencia;

import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author Paulo Sobreira [sowbreira@gmail.com]
 * @author Rafael Carneiro [rafaelcarneirob@gmail.com]
 */
public class HibernateUtil {

    private static EntityManagerFactory factory;

    public static Session getSession() {
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory("topwar-jpa");
        }
        EntityManager entityManager = factory.createEntityManager();
        System.out.println(factory.getProperties());
        return entityManager.unwrap(org.hibernate.Session.class);
    }

}
