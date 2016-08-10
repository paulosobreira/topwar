package br.nnpe.tos;

import java.io.Serializable;

import br.nnpe.tos.SessaoCliente;

/**
 * @author Paulo Sobreira Criado em 28/07/2007 as 15:51:36
 */
public class NnpeCliente implements Serializable {

	private static final long serialVersionUID = 6938384085272885074L;
	private String nomeJogador;
	private String senhaJogador;
	private String emailJogador;
	private SessaoCliente sessaoCliente;
	private String textoChat;
	private String nomeJogo;
	private boolean recuperar = false;

	public NnpeCliente(SessaoCliente sessaoCliente) {
		super();
		this.sessaoCliente = sessaoCliente;
	}

	public String getTextoChat() {
		return textoChat;
	}

	public void setTextoChat(String textoChat) {
		this.textoChat = textoChat;
	}

	public String getNomeJogo() {
		return nomeJogo;
	}

	public void setNomeJogo(String nomeJogo) {
		this.nomeJogo = nomeJogo;
	}

	public SessaoCliente getSessaoCliente() {
		return sessaoCliente;
	}

	public void setSessaoCliente(SessaoCliente sessaoCliente) {
		this.sessaoCliente = sessaoCliente;
	}

	public NnpeCliente() {

	}

	public String getNomeJogador() {
		return nomeJogador;
	}

	public void setNomeJogador(String apelido) {
		this.nomeJogador = apelido;
	}

	public String getSenhaJogador() {
		return senhaJogador;
	}

	public void setSenhaJogador(String senhaJogador) {
		this.senhaJogador = senhaJogador;
	}

	public String getEmailJogador() {
		return emailJogador;
	}

	public void setEmailJogador(String emailJogador) {
		this.emailJogador = emailJogador;
	}

	public boolean isRecuperar() {
		return recuperar;
	}

	public void setRecuperar(boolean recuperar) {
		this.recuperar = recuperar;
	}

}
