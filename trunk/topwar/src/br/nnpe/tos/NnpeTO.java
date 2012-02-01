package br.nnpe.tos;

import java.io.Serializable;
import java.util.Arrays;

public class NnpeTO implements Serializable {

	private String comando;

	private SessaoCliente sessaoCliente;

	private Object Data;

	private long millisSrv;

	private byte[] dataBytes;

	public byte[] getDataBytes() {
		return dataBytes;
	}

	public long getMillisSrv() {
		return millisSrv;
	}

	public void setMillisSrv(long millisSrv) {
		this.millisSrv = millisSrv;
	}

	public void setDataBytes(byte[] dataBytes) {
		this.dataBytes = dataBytes;
	}

	public String getComando() {
		return comando;
	}

	public void setComando(String comando) {
		this.comando = comando;
	}

	public Object getData() {
		return Data;
	}

	public void setData(Object data) {
		Data = data;
	}

	public SessaoCliente getSessaoCliente() {
		return sessaoCliente;
	}

	public void setSessaoCliente(SessaoCliente sessaoCliente) {
		this.sessaoCliente = sessaoCliente;
	}

	@Override
	public String toString() {
		return "NnpeTO [comando=" + comando + ", Data=" + Data
				+ ", sessaoCliente=" + sessaoCliente + ", millisSrv="
				+ millisSrv + ", dataBytes=" + Arrays.toString(dataBytes) + "]";
	}

}
