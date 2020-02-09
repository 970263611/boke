package entity;

import java.io.Serializable;

public class LeaveMes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private int fromuser;
	private int touser;
	private String message;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFromuser() {
		return fromuser;
	}
	public void setFromuser(int fromuser) {
		this.fromuser = fromuser;
	}
	public int getTouser() {
		return touser;
	}
	public void setTouser(int touser) {
		this.touser = touser;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
