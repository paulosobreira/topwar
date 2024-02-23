package br.topwar.tos;

import java.io.Serializable;

public class RadioMsg implements Serializable {

	private long id;

	private String avatar;

	private String msg;

	private boolean somenteTime;

	private transient String time;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "RadioMsg [avatar=" + avatar + ", id=" + id + ", msg=" + msg
				+ ", somenteTime=" + somenteTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((avatar == null) ? 0 : avatar.hashCode());
		result = prime * result + ((msg == null) ? 0 : msg.hashCode());
		result = prime * result + (somenteTime ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RadioMsg other = (RadioMsg) obj;
		if (avatar == null) {
			if (other.avatar != null)
				return false;
		} else if (!avatar.equals(other.avatar))
			return false;
		if (msg == null) {
			if (other.msg != null)
				return false;
		} else if (!msg.equals(other.msg))
			return false;
		if (somenteTime != other.somenteTime)
			return false;
		return true;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isSomenteTime() {
		return somenteTime;
	}

	public void setSomenteTime(boolean somenteTime) {
		this.somenteTime = somenteTime;
	}

}
