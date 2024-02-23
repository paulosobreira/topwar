package br.topwar.cliente;

import br.nnpe.cliente.NnpeApplet;
import br.nnpe.cliente.NnpeChatCliente;

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
        topWarApplet.setCodeBase(new URL("http://localhost"));
        topWarApplet.init();
    }

}
