package br.topwar.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import br.topwar.serial.ObjetoMapa;

public class FormularioListaObjetos {

	private EditorMapa editorMapa;
	private DefaultListModel defaultListModelOP;
	private JList list;
	private JFrame frame = new JFrame();

	public DefaultListModel getDefaultListModelOP() {
		return defaultListModelOP;
	}

	public void setDefaultListModelOP(DefaultListModel defaultListModelOP) {
		this.defaultListModelOP = defaultListModelOP;
	}

	public JList getList() {
		return list;
	}

	public void setList(JList list) {
		this.list = list;
	}

	public FormularioListaObjetos(EditorMapa editorMapa) {
		this.editorMapa = editorMapa;
		defaultListModelOP = new DefaultListModel();
		list = new JList(defaultListModelOP);
		list.setCellRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				return super.getListCellRendererComponent(list, "Objeto "
						+ index, index, isSelected, cellHasFocus);
				// return new JLabel("Objeto " + index);
			}
		});
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				atualizarObjs();
			}
		});
		JPanel main = new JPanel(new BorderLayout());
		JPanel botoes = new JPanel(new GridLayout(1, 2));
		main.add(new JScrollPane(list), BorderLayout.CENTER);
		main.add(botoes, BorderLayout.SOUTH);
		JButton remover = new JButton("Remover");
		remover.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int sel = list.getSelectedIndex();
				if (sel == -1)
					return;
				ObjetoMapa objetoMapa = (ObjetoMapa) defaultListModelOP
						.get(sel);

				FormularioListaObjetos.this.editorMapa
						.setObjetoMapaSelecionado(objetoMapa);
				FormularioListaObjetos.this.editorMapa
						.centralizaObjSelecionado();
				int showConfirmDialog = JOptionPane.showConfirmDialog(frame,
						"Apagar Objeto", "Confirme", JOptionPane.YES_NO_OPTION);
				if (JOptionPane.YES_OPTION != showConfirmDialog) {
					return;
				}
				defaultListModelOP.remove(sel);
				FormularioListaObjetos.this.editorMapa.excluirNo(objetoMapa);
				atualizarObjs();
			}
		});

		JButton selecionar = new JButton("Selecionar");
		selecionar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int sel = list.getSelectedIndex();
				if (sel == -1)
					return;
				ObjetoMapa objetoMapa = (ObjetoMapa) defaultListModelOP
						.get(sel);
				FormularioListaObjetos.this.editorMapa
						.setObjetoMapaSelecionado(objetoMapa);
				FormularioListaObjetos.this.editorMapa
						.centralizaObjSelecionado();
				atualizarObjs();
			}
		});

		botoes.add(selecionar);
		botoes.add(remover);
		frame.add(main);
	}

	public void mostrarPainel() {
		List<ObjetoMapa> objetoMapaList = editorMapa.getMapaTopWar()
				.getObjetoMapaList();
		if (objetoMapaList == null) {
			JOptionPane.showMessageDialog(editorMapa.getFrame(), "Sem Objetos");
			return;
		}
		defaultListModelOP.clear();
		for (ObjetoMapa op : objetoMapaList) {
			defaultListModelOP.addElement(op);
		}
		Point location = editorMapa.getFrame().getLocation();
		frame.setLocation(new Point(location.x
				+ editorMapa.getFrame().getWidth(), location.y));
		frame.setSize(250, 400);
		frame.setVisible(true);
	}

	protected void atualizarObjs() {
		FormularioListaObjetos.this.editorMapa.atualizarObjs();
	}

	public static void main(String[] args) {
		FormularioListaObjetos formularioListaObjetos = new FormularioListaObjetos(
				null);
		formularioListaObjetos.mostrarPainel();
	}
}
