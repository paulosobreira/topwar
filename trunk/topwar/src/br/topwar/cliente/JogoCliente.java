package br.topwar.cliente;

import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import br.nnpe.Logger;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.serial.MapaTopWar;

public class JogoCliente {
	private MapaTopWar mapaTopWar;
	protected boolean rodando = true;
	private Thread threadRepaint;
	private PainelTopWar painelTopWar;
	private List<AvatarCliente> avatarClientes = new ArrayList<AvatarCliente>();

	public JogoCliente() {
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				rodando = false;
				super.windowClosing(e);
			}
		});
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Cursor crossHair = new Cursor(Cursor.CROSSHAIR_CURSOR);
		frame.setCursor(crossHair);
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(CarregadorRecursos
					.recursoComoStream("mapa9.topwar"));
			mapaTopWar = (MapaTopWar) ois.readObject();
			frame.setTitle(mapaTopWar.getNome());
		} catch (Exception e1) {
			Logger.logarExept(e1);
		}
		BufferedImage img = CarregadorRecursos.carregaBackGround(mapaTopWar
				.getBackGround());

		painelTopWar = new PainelTopWar(this);

		Thread threadRepaint = new Thread(new Runnable() {
			@Override
			public void run() {
				while (rodando) {
					try {
						painelTopWar.atualiza();
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public MapaTopWar getMapaTopWar() {
		return mapaTopWar;
	}

	public void setMapaTopWar(MapaTopWar mapaTopWar) {
		this.mapaTopWar = mapaTopWar;
	}

	public List<AvatarCliente> getAvatarClientes() {
		return avatarClientes;
	}

	public void setAvatarClientes(List<AvatarCliente> avatarClientes) {
		this.avatarClientes = avatarClientes;
	}

}
