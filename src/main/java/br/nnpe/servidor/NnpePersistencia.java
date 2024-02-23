package br.nnpe.servidor;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import br.nnpe.Dia;
import br.nnpe.Logger;
import br.nnpe.persistencia.HibernateUtil;
import br.nnpe.persistencia.NnpeDados;
import br.nnpe.persistencia.NnpeUsuario;
import br.nnpe.tos.NnpeTO;

/**
 * @author Paulo Sobreira Criado em 23/02/2010
 */
public class NnpePersistencia {
    private String webInfDir;

    private String webDir;

    public Session getSession() {
        return HibernateUtil.getSession();
    }

    public NnpePersistencia(String webDir, String webInfDir) {
        super();
        this.webInfDir = webInfDir;
        this.webDir = webDir;
    }


    public void gravarDados(NnpeDados... nnpeDados) throws Exception {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            for (int i = 0; i < nnpeDados.length; i++) {
                session.saveOrUpdate(nnpeDados[i]);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }

    }

    public List obterNnpeUsuarios() {
        Session session = getSession();
        try {
            return session.createCriteria(NnpeUsuario.class).list();
        } finally {
            session.close();
        }
    }

    public NnpeTO obterTodosNnpeUsuarios() {
        Dia dia = new Dia();
        dia.advance(-240);
        Session session = getSession();
        try {
            String hql = "select obj.login from Usuario obj where obj.ultimoLogon > "
                    + dia.toTimestamp().getTime() + " order by obj.login ";
            Query qry = session.createQuery(hql);
            List jogadores = qry.list();
            String[] retorno = new String[jogadores.size()];
            int i = 0;
            for (Iterator iterator = jogadores.iterator(); iterator.hasNext(); ) {
                String nome = (String) iterator.next();
                retorno[i] = nome;
                i++;
            }
            NnpeTO mesa11to = new NnpeTO();
            mesa11to.setData(retorno);
            return mesa11to;
        } finally {
            session.close();
        }

    }

    public NnpeUsuario obterNnpeUsuarioPorLogin(String login) {
        Session session = getSession();
        try {

            NnpeUsuario usuario = (NnpeUsuario) session.createCriteria(
                            NnpeUsuario.class).add(Restrictions.eq("login", login))
                    .uniqueResult();
            return usuario;
        } finally {
            session.close();
        }
    }

}
