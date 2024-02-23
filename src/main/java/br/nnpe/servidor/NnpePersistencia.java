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
        Session session = HibernateUtil.currentSession();
        try {
            List jogador = session.createCriteria(NnpeUsuario.class).add(
                    Restrictions.eq("id", new Long(0))).list();
        } catch (Exception e) {
            Logger.logarExept(e);
            HibernateUtil.closeSession();
            Logger.novaSession = true;
            session = HibernateUtil.currentSession();
        }
        return session;
    }

    public NnpePersistencia(String webDir, String webInfDir) {
        super();
        this.webInfDir = webInfDir;
        this.webDir = webDir;
    }

    public static void main(String[] args) throws Exception {
        // ControlePersistencia controlePersistencia = new ControlePersistencia(
        // "d:" + File.separator);
        // controlePersistencia.paddockDadosSrv.getJogadoresMap().put("teste3",
        // "Paulo sobreira");
        // controlePersistencia.gravarDados();
        Dia dia = new Dia("01/06/2009");
        Dia hj = new Dia();
        Logger.logar(hj.daysBetween(dia));
    }


    public void zipDir(String dir2zip, ZipOutputStream zos) {
        try {
            // create a new File object based on the directory we
            // have to zip File
            File zipDir = new File(dir2zip);
            // get a listing of the directory content
            String[] dirList = zipDir.list();
            byte[] readBuffer = new byte[2156];
            int bytesIn = 0;
            // loop through dirList, and zip the files
            for (int i = 0; i < dirList.length; i++) {
                File f = new File(zipDir, dirList[i]);
                if (f.isDirectory()) {
                    // if the File object is a directory, call this
                    // function again to add its content recursively
                    String filePath = f.getPath();
                    zipDir(filePath, zos);
                    // loop again
                    continue;
                }
                // if we reached here, the File object f was not
                // a directory
                // create a FileInputStream on top of f
                FileInputStream fis = new FileInputStream(f);
                // create a new zip entry
                ZipEntry anEntry = new ZipEntry(f.getAbsolutePath().split(
                        "algol-rpg" + File.separator + File.separator)[1]);
                // place the zip entry in the ZipOutputStream object
                zos.putNextEntry(anEntry);
                // now write the content of the file to the ZipOutputStream
                while ((bytesIn = fis.read(readBuffer)) != -1) {
                    zos.write(readBuffer, 0, bytesIn);
                }
                // close the Stream
                fis.close();
            }
        } catch (Exception e) {
            Logger.logarExept(e);
        }
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
        }

    }

    public List obterNnpeUsuarios() {
        Session session = getSession();
        return session.createCriteria(NnpeUsuario.class).list();
    }

    public NnpeTO obterTodosNnpeUsuarios() {
        Dia dia = new Dia();
        dia.advance(-240);
        Session session = getSession();
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
    }

    public NnpeUsuario obterNnpeUsuarioPorLogin(String login) {
        Session session = getSession();
        NnpeUsuario usuario = (NnpeUsuario) session.createCriteria(
                        NnpeUsuario.class).add(Restrictions.eq("login", login))
                .uniqueResult();
        return usuario;
    }

}
