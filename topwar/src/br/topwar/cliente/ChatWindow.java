package br.topwar.cliente;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import br.nnpe.ImageUtil;
import br.nnpe.Logger;
import br.nnpe.cliente.NnpeChatCliente;
import br.nnpe.cliente.NnpeChatWindow;
import br.nnpe.tos.NnpeDados;
import br.topwar.recursos.CarregadorRecursos;
import br.topwar.recursos.idiomas.Lang;

public class ChatWindow extends NnpeChatWindow {
	protected JList listaJogos;
	private HashMap<String, String> mapaJogosAndamento = new HashMap<String, String>();
	protected JButton criarJogo;
	protected JButton entrarJogo;

	protected ControleCliente controleChatCliente;

	public ChatWindow(NnpeChatCliente nnpeChatCliente) {
		super(nnpeChatCliente);
		this.controleChatCliente = (ControleCliente) nnpeChatCliente;
		img = ImageUtil.gerarFade(
				CarregadorRecursos.carregaBackGround("mercs-chat.png"), 50);
	}

	public void gerarLayout() {
		JPanel cPanel = new JPanel(new BorderLayout());
		compTransp(cPanel);
		JPanel ePanel = new JPanel(new BorderLayout());
		compTransp(ePanel);
		mainPanel.add(cPanel, BorderLayout.CENTER);
		JPanel chatPanel = new JPanel();
		compTransp(chatPanel);
		String versao = "Rodando Main";
		if (nnpeChatCliente != null) {
			versao = nnpeChatCliente.getVersao();
		}
		chatPanel.setBorder(new TitledBorder("TopWar Chat Room " + versao));
		JPanel usersPanel = new JPanel();
		compTransp(usersPanel);
		usersPanel.setBorder(new TitledBorder("Jogadores Online") {
			public String getTitle() {
				return Lang.msg("jogadoresOnline");
			}
		});
		JPanel jogosPanel = new JPanel();
		compTransp(jogosPanel);
		jogosPanel.setBorder(new TitledBorder("Jogos") {
			public String getTitle() {
				return Lang.msg("jogos");
			}
		});

		cPanel.add(chatPanel, BorderLayout.CENTER);
		mainPanel.add(ePanel, BorderLayout.EAST);
		JPanel inputPanel = new JPanel();
		compTransp(inputPanel);
		cPanel.add(inputPanel, BorderLayout.SOUTH);
		ePanel.add(usersPanel, BorderLayout.CENTER);
		ePanel.add(jogosPanel, BorderLayout.SOUTH);
		/**
		 * adicionar componentes.
		 */
		JScrollPane usersPane = new JScrollPane(listaClientes) {
			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();
				return new Dimension(120, 210);
			}
		};
		compTransp(listaClientes);
		compTransp(usersPane);
		listaJogos = new JList(new DefaultListModel());
		JScrollPane jogsPane = new JScrollPane(listaJogos) {
			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();
				return new Dimension(120, 100);
			}
		};
		compTransp(listaJogos);
		compTransp(jogsPane);
		usersPanel.add(usersPane);
		jogosPanel.add(jogsPane);
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(1, 4));
		criarJogo = new JButton("criarJogo") {

			public String getText() {

				return Lang.msg("criarJogo");
			}
		};
		entrarJogo = new JButton("entrarJogo") {

			public String getText() {

				return Lang.msg("entrarJogo");
			}
		};
		buttonsPanel.add(criarJogo);
		buttonsPanel.add(entrarJogo);
		buttonsPanel.add(sair);
		buttonsPanel.add(sobre);
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

		JPanel panelTextoEnviar = new JPanel();
		compTransp(panelTextoEnviar);
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
		compTransp(textAreaScrollPane);
		compTransp(textAreaChat);
		chatPanel.add(textAreaScrollPane, BorderLayout.CENTER);
		chatPanel.setOpaque(false);
	}

	private void compTransp(JComponent c) {
		c.setBackground(new Color(255, 255, 255, 0));
	}

	@Override
	protected void gerarAcoes() {
		super.gerarAcoes();
		criarJogo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controleChatCliente.criarJogo();
			}
		});
		entrarJogo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controleChatCliente.entrarJogo();
			}
		});
	}

	@Override
	public void atualizar(NnpeDados nnpeDados) {
		super.atualizar(nnpeDados);
		DefaultListModel modelJogosCriados = ((DefaultListModel) listaJogos
				.getModel());
		modelJogosCriados.clear();
		mapaJogosAndamento.clear();
		for (Iterator iter = nnpeDados.getJogosAndamento().iterator(); iter
				.hasNext();) {
			String nmJogo = (String) iter.next();
			Logger.logar("nmJogo" + nmJogo);
			String key = Lang.decodeTexto(nmJogo);
			// NnpeTO nnpeTO = new NnpeTO();
			// nnpeTO.setComando(ConstantesTopWar.OBTER_DADOS_JOGO);
			// nnpeTO.setData(nmJogo);
			// String placar = "";
			// Object ret = nnpeChatCliente.enviarObjeto(nnpeTO);
			// if (ret instanceof NnpeTO) {
			// nnpeTO = (NnpeTO) ret;
			// }
			mapaJogosAndamento.put(key, nmJogo);
			modelJogosCriados.addElement(key);
		}
		listaJogos.setModel(modelJogosCriados);
	}

	public static void main(String[] args) {
		final ChatWindow nnpeChatWindow = new ChatWindow(null);
		JFrame frame = new JFrame();
		frame.getContentPane().add(nnpeChatWindow.getMainPanel());
		JTextArea area = new JTextArea(20, 50);
		area.setBackground(new Color(255, 255, 255, 0));
		JScrollPane pane = new JScrollPane(area);
		pane.setBackground(new Color(255, 255, 255, 0));
		// frame.getContentPane().add(jPanel);
		frame.setSize(820, 380);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					nnpeChatWindow.getMainPanel().repaint();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}

	public String obterJogoSelecionado() {
		Object key = listaJogos.getSelectedValue();
		if (key == null) {
			return mapaJogosAndamento.get(mapaJogosAndamento.keySet()
					.iterator().next());
		}
		return mapaJogosAndamento.get(key);
	}
}
