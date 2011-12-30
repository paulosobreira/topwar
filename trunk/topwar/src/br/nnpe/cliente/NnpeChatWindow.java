package br.nnpe.cliente;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import br.nnpe.Logger;
import br.nnpe.tos.DadosChatNnpe;
import br.topwar.cliente.ControleChatCliente;
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

	protected JButton entrarJogo = new JButton("Entrar Jogo") {

		public String getText() {

			return Lang.msg("entrarJogo");
		}
	};
	protected JButton criarJogo = new JButton("Criar Jogo") {

		public String getText() {

			return Lang.msg("criarJogo");
		}
	};

	protected JButton verDetalhes = new JButton("Ver Detalhes") {

		public String getText() {

			return Lang.msg("verDetalhes");
		}
	};
	protected JButton sairJogo = new JButton("sairJogo") {

		public String getText() {

			return Lang.msg("sairJogo");
		}
	};

	protected JComboBox comboIdiomas = new JComboBox(new String[] {
			Lang.msg("pt"), Lang.msg("en") });
	protected JButton sobre = new JButton("Sobre") {

		public String getText() {

			return Lang.msg("sobre");
		}
	};
	protected JLabel infoLabel1 = new JLabel();
	protected Set chatTimes = new HashSet();
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
		criarJogo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				nnpeChatCliente.criarJogo();
			}

		});

		entrarJogo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nnpeChatCliente.entarJogo();
			}

		});
		verDetalhes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nnpeChatCliente.verDetalhesJogo();
				nnpeChatCliente.verDetalhesJogador();

			}

		});

		sairJogo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nnpeChatCliente.sairJogo();

			}

		});

		sobre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = Lang.msg("feitopor") + "  Paulo Sobreira \n "
						+ "sowbreira@gmail.com \n"
						+ "sowbreira.appspot.com/ \n" + "Janeiro de 2011 \n ";

				JOptionPane.showMessageDialog(getMainPanel(), msg,
						Lang.msg("autor"), JOptionPane.INFORMATION_MESSAGE);
			}
		});

	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public void atualizar(DadosChatNnpe dadosMesa11) {
		atualizarChat(dadosMesa11);
	}

	protected void atualizarChat(DadosChatNnpe dadosMesa11) {
		if ("".equals(dadosMesa11.getLinhaChat())
				|| dadosMesa11.getLinhaChat() == null
				|| dadosMesa11.getDataTime() == null) {
			return;
		}
		if (!chatTimes.contains(dadosMesa11.getDataTime())) {
			textAreaChat.append(dadosMesa11.getLinhaChat() + "\n");
			textAreaChat.setCaretPosition(textAreaChat.getText().length());
			chatTimes.add(dadosMesa11.getDataTime());
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

	public void mostrarDetalhesJogador(Object object) {
		Logger.logar("Implementar mostrarDetalhesJogador");
	}

	public String obterJogoSelecionado() {
		Logger.logar("Implementar obterJogoSelecionado");
		return null;
	}

	public void gerarLayout() {
		Logger.logar("Implementar gerarLayout");
	}
}
