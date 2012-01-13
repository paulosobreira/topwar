package br.topwar.cliente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.cliente.NnpeChatCliente;
import br.nnpe.cliente.NnpeChatWindow;
import br.nnpe.tos.NnpeDadosChat;
import br.nnpe.tos.NnpeTO;
import br.topwar.ConstantesTopWar;
import br.topwar.recursos.idiomas.Lang;

public class ChatWindow extends NnpeChatWindow {
	protected JList listaJogos = new JList();
	private HashMap mapaJogosCriados = new HashMap();

	public ChatWindow(NnpeChatCliente nnpeChatCliente) {
		super(nnpeChatCliente);
	}

	public void gerarLayout() {
		JPanel cPanel = new JPanel(new BorderLayout());
		JPanel ePanel = new JPanel(new BorderLayout());
		mainPanel.add(cPanel, BorderLayout.CENTER);
		JPanel chatPanel = new JPanel();
		String versao = "Rodando Main";
		if (nnpeChatCliente != null) {
			versao = nnpeChatCliente.getVersao();
		}
		chatPanel.setBorder(new TitledBorder("TopWar Chat Room " + versao));
		JPanel usersPanel = new JPanel();
		usersPanel.setBorder(new TitledBorder("Jogadores Online") {
			public String getTitle() {
				return Lang.msg("jogadoresOnline");
			}
		});
		JPanel jogosPanel = new JPanel();
		jogosPanel.setBorder(new TitledBorder("Jogos") {
			public String getTitle() {
				return Lang.msg("jogos");
			}
		});

		cPanel.add(chatPanel, BorderLayout.CENTER);
		mainPanel.add(ePanel, BorderLayout.EAST);
		JPanel inputPanel = new JPanel();
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
		JScrollPane jogsPane = new JScrollPane(listaJogos) {
			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();
				return new Dimension(120, 100);
			}
		};

		usersPanel.add(usersPane);
		jogosPanel.add(jogsPane);
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

	@Override
	public void atualizar(NnpeDadosChat nnpeDadosChat) {
		super.atualizar(nnpeDadosChat);
		DefaultListModel modelJogosCriados = ((DefaultListModel) listaJogos
				.getModel());
		modelJogosCriados.clear();
		mapaJogosCriados.clear();
		for (Iterator iter = nnpeDadosChat.getJogosCriados().iterator(); iter
				.hasNext();) {
			String nmJogo = (String) iter.next();
			String key = Lang.decodeTexto(nmJogo);
			NnpeTO nnpeTO = new NnpeTO();
			nnpeTO.setComando(ConstantesTopWar.OBTER_DADOS_JOGO);
			nnpeTO.setData(nmJogo);
			String placar = "";
			Object ret = nnpeChatCliente.enviarObjeto(nnpeTO);
			if (ret instanceof NnpeTO) {
				nnpeTO = (NnpeTO) ret;
			}
			mapaJogosCriados.put(key, Util.isNullOrEmpty(nmJogo) ? nmJogo
					: placar);
			modelJogosCriados.addElement(key);
		}
		listaJogos.setModel(modelJogosCriados);
	}

}
