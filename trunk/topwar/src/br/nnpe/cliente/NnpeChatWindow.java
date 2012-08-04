package br.nnpe.cliente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import br.nnpe.Logger;
import br.nnpe.tos.NnpeDados;
import br.nnpe.tos.SessaoCliente;
import br.topwar.recursos.idiomas.Lang;

/**
 * @author paulo.sobreira
 * 
 */
public class NnpeChatWindow {

	protected JPanel mainPanel;
	protected NnpeChatCliente nnpeChatCliente;
	protected JList listaClientes = new JList();
	protected JTextArea textAreaChat = new JTextArea();
	protected JTextField textoEnviar = new JTextField();
	protected JComboBox comboIdiomas = new JComboBox(new String[] {
			Lang.msg("pt"), Lang.msg("en") });
	protected JButton sobre = new JButton("Sobre") {

		public String getText() {

			return Lang.msg("sobre");
		}
	};
	protected JButton sair = new JButton("sair") {

		public String getText() {

			return Lang.msg("sair");
		}
	};
	protected JLabel infoLabel1 = new JLabel();
	protected Set chatSet = new HashSet();
	protected SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public NnpeChatWindow(NnpeChatCliente nnpeChatCliente) {
		mainPanel = new JPanel(new BorderLayout());
		if (nnpeChatCliente != null) {
			this.nnpeChatCliente = nnpeChatCliente;
		}
		gerarLayout();
		gerarAcoes();
		if (nnpeChatCliente != null) {
			atualizaInfo();
		}

	}

	protected void gerarAcoes() {
		ActionListener actionListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Thread enviarTexto = new Thread(new Runnable() {

					public void run() {
						try {
							nnpeChatCliente.enviarTexto(textoEnviar.getText());
							textoEnviar.setText("");
						} catch (Exception e) {
							Logger.logarExept(e);
						}
					}
				});
				enviarTexto.start();
			}

		};
		textoEnviar.addActionListener(actionListener);
		sobre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = Lang.msg("feitopor") + "  Paulo Sobreira \n "
						+ "sowbreira@gmail.com \n"
						+ "sowbreira.appspot.com/ \n" + "Janeiro de 2011 \n ";

				JOptionPane.showMessageDialog(getMainPanel(), msg,
						Lang.msg("autor"), JOptionPane.INFORMATION_MESSAGE);
			}
		});
		sair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ret = JOptionPane.showConfirmDialog(getMainPanel(),
						Lang.msg("confirmaSair"), Lang.msg("confirmaSair"),
						JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.NO_OPTION) {
					return;
				}
				acaoBotaoSair();
			}
		});
	}

	public void acaoBotaoSair() {
		Logger.logar("Subscreva acaoBotaoSair()");
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public void atualizar(NnpeDados dadosMesa11) {
		atualizarChat(dadosMesa11);
		DefaultListModel clientesModel = new DefaultListModel();
		for (Iterator iter = dadosMesa11.getClientes().iterator(); iter
				.hasNext();) {
			SessaoCliente element = (SessaoCliente) iter.next();
			clientesModel.addElement(element);
		}
		listaClientes.setModel(clientesModel);

	}

	protected void atualizarChat(NnpeDados nnpeDadosChat) {
		if ("".equals(nnpeDadosChat.getLinhaChat())
				|| nnpeDadosChat.getLinhaChat() == null
				|| nnpeDadosChat.getDataTime() == null) {
			return;
		}
		if (!chatSet.contains(nnpeDadosChat.getDataTime())) {
			textAreaChat.append(Lang.decodeTexto(nnpeDadosChat.getLinhaChat())
					+ "\n");
			textAreaChat.setCaretPosition(textAreaChat.getText().length());
			chatSet.add(nnpeDadosChat.getDataTime());
		}
	}

	public void atualizaInfo() {
		String text = Lang.msg("latenciaJogo") + " "
				+ nnpeChatCliente.getLatenciaMinima();
		text += " " + Lang.msg("latenciaReal") + " "
				+ nnpeChatCliente.getLatenciaReal();
		text += " " + Lang.msg("maxJogos") + " " + 10;

		infoLabel1.setText(text);

	}

	public void gerarLayout() {
		JPanel cPanel = new JPanel(new BorderLayout());
		JPanel ePanel = new JPanel(new GridLayout(1, 2));
		mainPanel.add(cPanel, BorderLayout.CENTER);
		JPanel chatPanel = new JPanel();
		String versao = "Rodando Main";
		if (nnpeChatCliente != null) {
			versao = nnpeChatCliente.getVersao();
		}
		chatPanel.setBorder(new TitledBorder("Nnpe Chat Room " + versao));
		JPanel usersPanel = new JPanel();
		usersPanel.setBorder(new TitledBorder("Jogadores Online") {
			public String getTitle() {
				return Lang.msg("jogadoresOnline");
			}
		});
		cPanel.add(chatPanel, BorderLayout.CENTER);
		mainPanel.add(ePanel, BorderLayout.EAST);
		JPanel inputPanel = new JPanel();
		cPanel.add(inputPanel, BorderLayout.SOUTH);
		ePanel.add(usersPanel);
		/**
		 * adicionar componentes.
		 */
		JScrollPane jogsPane = new JScrollPane(listaClientes) {
			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();
				return new Dimension(120, 340);
			}
		};
		usersPanel.add(jogsPane);
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(3, 4));
		buttonsPanel.add(comboIdiomas);
		comboIdiomas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Logger.logar(Lang
						.key(comboIdiomas.getSelectedItem().toString()));
				String i = Lang.key(comboIdiomas.getSelectedItem().toString());
				if (i != null && !"".equals(i)) {
					Lang.mudarIdioma(i);
					comboIdiomas.removeAllItems();
					comboIdiomas.addItem(Lang.msg("pt"));
					comboIdiomas.addItem(Lang.msg("en"));
				}
			}
		});
		buttonsPanel.add(sobre);
		buttonsPanel.add(sair);
		JPanel panelTextoEnviar = new JPanel();
		panelTextoEnviar.setBorder(new TitledBorder("Texto Enviar") {
			public String getTitle() {
				return Lang.msg("textoEnviar");
			}
		});
		panelTextoEnviar.setLayout(new BorderLayout());
		panelTextoEnviar.add(textoEnviar, BorderLayout.CENTER);
		inputPanel.setLayout(new BorderLayout());
		inputPanel.add(panelTextoEnviar, BorderLayout.NORTH);
		inputPanel.add(buttonsPanel, BorderLayout.CENTER);
		inputPanel.add(infoLabel1, BorderLayout.SOUTH);
		chatPanel.setLayout(new BorderLayout());

		JScrollPane textAreaScrollPane = new JScrollPane(textAreaChat) {
			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();
				return new Dimension(600, 200);
			}
		};

		chatPanel.add(textAreaScrollPane, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		NnpeChatWindow nnpeChatWindow = new NnpeChatWindow(null);
		JFrame frame = new JFrame();
		frame.getContentPane().add(nnpeChatWindow.getMainPanel());
		// frame.setSize(820, 380);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

}
