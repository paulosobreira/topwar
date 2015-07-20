package br.nnpe.cliente;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import br.nnpe.Constantes;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.tos.NnpeTO;
import br.topwar.recursos.idiomas.Lang;

public class NnpeFormLogin extends JPanel {
	protected JComboBox comboIdiomas = new JComboBox(new String[] {
			Lang.msg("pt"), Lang.msg("en") });
	protected JTextField nomeLogar = new JTextField(20);
	@Deprecated
	protected JTextField capchaTexto = new JTextField(20);
	@Deprecated
	protected JTextField capchaTextoRecuperar = new JTextField(20);
	@Deprecated
	protected Map<String, String> chapchaChave = new HashMap<String, String>();
	protected JTextField nomeRegistrar = new JTextField(20);
	protected JTextField nomeRecuperar = new JTextField(20);
	protected NnpeApplet nnpeApplet;
	protected JTextField email = new JTextField(20);
	protected JTextField emailRecuperar = new JTextField(20);

	public Map<String, String> getChapchaChave() {
		return chapchaChave;
	}

	private JLabel senhaLabel = new JLabel("Senha") {
		public String getText() {
			return Lang.msg("senha");
		}
	};
	private JPasswordField senha = new JPasswordField(20);

	public JCheckBox getLembrar() {
		return lembrar;
	}

	public void setLembrar(JCheckBox lembrar) {
		this.lembrar = lembrar;
	}

	public NnpeFormLogin(NnpeApplet nnpeApplet) {
		this.nnpeApplet = nnpeApplet;
		if (nnpeApplet == null) {
			return;
		}
		setLayout(new BorderLayout());
		JTabbedPane jTabbedPane = new JTabbedPane();
		JPanel abaEntrar = new JPanel(new BorderLayout(15, 15));
		JPanel sulaba1 = new JPanel(new BorderLayout());
		sulaba1.add(gerarLogin(), BorderLayout.CENTER);
		sulaba1.add(gerarLembrar(), BorderLayout.SOUTH);
		abaEntrar.add(sulaba1, BorderLayout.CENTER);
		abaEntrar.add(gerarIdiomas(), BorderLayout.SOUTH);
		jTabbedPane.addTab(Lang.msg("entrar"), abaEntrar);
		JPanel abaResgistrar = new JPanel(new BorderLayout());
		abaResgistrar.add(gerarRegistrar(), BorderLayout.CENTER);
		jTabbedPane.addTab(Lang.msg("registrar"), abaResgistrar);
		JPanel abaRecuperar = new JPanel(new BorderLayout());
		abaRecuperar.add(gerarRecuperar(), BorderLayout.CENTER);
		jTabbedPane.addTab(Lang.msg("recuperarSenha"), abaRecuperar);
		add(jTabbedPane, BorderLayout.CENTER);
		setSize(300, 300);
		setVisible(true);
	}

	private Component gerarRecuperar() {
		JPanel registrarPanel = new JPanel(new GridLayout(4, 2));
		registrarPanel.setBorder(new TitledBorder("Registrar") {
			public String getTitle() {
				return Lang.msg("recuperarSenha");
			}
		});
		registrarPanel.add(new JLabel("Entre com seu Nome") {
			public String getText() {
				return Lang.msg("nome");
			}
		});
		registrarPanel.add(nomeRecuperar);
		registrarPanel.add(new JLabel("Entre com seu e-mail") {
			public String getText() {
				return Lang.msg("ouEntreEmail");
			}
		});
		registrarPanel.add(emailRecuperar);

		JPanel newPanel = new JPanel(new BorderLayout());
		newPanel.add(registrarPanel, BorderLayout.NORTH);
//		newPanel.add(gerarCapchaPanel(capchaTextoRecuperar,
//				Constantes.RECUPERAR), BorderLayout.CENTER);
		return newPanel;
	}

	private JCheckBox lembrar = new JCheckBox();

	private Component gerarLembrar() {
		lembrar = new JCheckBox();
		JPanel langPanel = new JPanel();
		langPanel.add(lembrar);
		langPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("lembrar");
			}
		});
		return langPanel;
	}

	private JPanel gerarIdiomas() {
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
				NnpeFormLogin.this.repaint();
			}
		});
		JPanel langPanel = new JPanel(new BorderLayout());
		langPanel.setBorder(new TitledBorder("Idiomas") {
			public String getTitle() {
				return Lang.msg("idiomas");
			}
		});
		langPanel.add(comboIdiomas, BorderLayout.CENTER);

		return langPanel;
	}

	private JPanel gerarRegistrar() {
		if (nnpeApplet == null) {
			return null;
		}
		JPanel registrarPanel = new JPanel(new GridLayout(4, 2));
		registrarPanel.setBorder(new TitledBorder("Registrar") {
			public String getTitle() {
				return Lang.msg("registrar");
			}
		});
		registrarPanel.add(new JLabel("Entre com seu Nome") {
			public String getText() {
				return Lang.msg("nome");
			}
		});
		registrarPanel.add(nomeRegistrar);
		registrarPanel.add(new JLabel("Entre com seu e-mail") {
			public String getText() {
				return Lang.msg("entreEmail");
			}
		});
		registrarPanel.add(email);
		JPanel newPanel = new JPanel(new BorderLayout());
		newPanel.add(registrarPanel, BorderLayout.NORTH);
//		newPanel.add(gerarCapchaPanel(capchaTexto, Constantes.REGISTRAR),
//				BorderLayout.CENTER);
		return newPanel;
	}

	@Deprecated
	private Component gerarCapchaPanel(JTextField capchaTexto,
			final String cpachaChave) {
		if (nnpeApplet == null) {
			return null;
		}
		JPanel capchaPanel = new JPanel(new BorderLayout());
		final JLabel capchaImage = new JLabel();
		JPanel capchaImagePanel = new JPanel();
		capchaImagePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chapchaChave.put(cpachaChave, capchaReload(capchaImage));
				Logger.logar("Reload de Capcha " + cpachaChave);
				super.mouseClicked(e);
			}
		});
		capchaImagePanel.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("clickNovaImagem");
			}
		});
		capchaImagePanel.add(capchaImage);
		capchaPanel.add(capchaImagePanel, BorderLayout.CENTER);
		JPanel sulPanel = new JPanel();
		sulPanel.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("digiteFrase");
			}
		});
		sulPanel.add(capchaTexto);
		capchaPanel.add(sulPanel, BorderLayout.SOUTH);
		chapchaChave.put(cpachaChave, capchaReload(capchaImage));
		return capchaPanel;
	}

	@Deprecated
	protected String capchaReload(JLabel capchaImage) {
		NnpeTO nnpeTO = new NnpeTO();
		nnpeTO.setComando(Constantes.NOVO_CAPCHA);
		Object ret = nnpeApplet.enviarObjeto(nnpeTO);
		if (ret != null && ret instanceof NnpeTO) {
			nnpeTO = (NnpeTO) ret;
			capchaImage.setIcon(new ImageIcon(nnpeTO.getDataBytes()));
			return (String) nnpeTO.getData();
		}
		return null;

	}

	private JPanel gerarLogin() {
		JPanel panel = new JPanel();
		GridLayout gridLayout = new GridLayout(4, 1);
		panel.setBorder(new TitledBorder("Entrar") {
			@Override
			public String getTitle() {
				return Lang.msg("entrar");
			}
		});
		panel.setLayout(gridLayout);
		panel.add(new JLabel("Entre com seu Nome") {
			public String getText() {
				return Lang.msg("nome");
			}
		});
		panel.add(nomeLogar);
		panel.add(senhaLabel);
		panel.add(senha);
		return panel;
	}

	public void setNome(JTextField nome) {
		this.nomeLogar = nome;
	}

	public JPasswordField getSenha() {
		return senha;
	}

	public JTextField getNomeLogar() {
		return nomeLogar;
	}

	public JTextField getNomeRegistrar() {
		return nomeRegistrar;
	}

	public JTextField getNomeRecuperar() {
		return nomeRecuperar;
	}

	public static void main(String[] args) throws FileNotFoundException {
		// FileOutputStream fileOutputStream = new
		// FileOutputStream("teste.xml");
		// XMLEncoder encoder = new XMLEncoder(fileOutputStream);
		// String teste = "HandlerFactory";
		// encoder.writeObject(teste);
		// encoder.flush();
		// encoder.close();
		NnpeFormLogin formEntrada = new NnpeFormLogin(null);
		formEntrada.setToolTipText(Lang.msg("formularioLogin"));
		int result = JOptionPane.showConfirmDialog(null, formEntrada, Lang
				.msg("formularioLogin"), JOptionPane.OK_CANCEL_OPTION);

		if (JOptionPane.OK_OPTION == result) {
			Logger.logar("ok");
		}
	}

	public JTextField getEmail() {
		return email;
	}

	public String getCapchaTexto() {
		return capchaTexto.getText();
	}

	public String getCapchaTextoRecuperar() {
		return capchaTextoRecuperar.getText();
	}

	public JTextField getEmailRecuperar() {
		return emailRecuperar;
	}

}
