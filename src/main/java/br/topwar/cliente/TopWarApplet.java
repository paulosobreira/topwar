package br.topwar.cliente;

import br.nnpe.Util;
import br.nnpe.cliente.NnpeApplet;
import br.nnpe.cliente.NnpeChatCliente;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;

public class TopWarApplet extends NnpeApplet {

    private ControleCliente controleChatCliente = new ControleCliente(this);

    @Override
    public NnpeChatCliente getNnpeChatCliente() {
        return controleChatCliente;
    }

    public static void main(String[] args) throws MalformedURLException {
        TopWarApplet topWarApplet = new TopWarApplet();
        String host = JOptionPane.showInputDialog("Host");
        if(Util.isNullOrEmpty(host)){
            host = "http://localhost";
        }
        topWarApplet.setCodeBase(new URL(host));
        topWarApplet.init();
    }

}
