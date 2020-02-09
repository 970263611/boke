package entity;

public class WeChat {
	
	private String token;
	private String signature;
	private String timestamp;
	private String nonce;
	private String echostr;
	private String mes;
	
	public WeChat() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public WeChat(String signature, String timestamp, String nonce, String echostr) {
		super();
		this.signature = signature;
		this.timestamp = timestamp;
		this.nonce = nonce;
		this.echostr = echostr;
	}

	public WeChat(String token, String signature, String timestamp, String nonce, String echostr, String mes) {
		super();
		this.token = token;
		this.signature = signature;
		this.timestamp = timestamp;
		this.nonce = nonce;
		this.echostr = echostr;
		this.mes = mes;
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getNonce() {
		return nonce;
	}
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
	public String getEchostr() {
		return echostr;
	}
	public void setEchostr(String echostr) {
		this.echostr = echostr;
	}
	public String getMes() {
		return mes;
	}
	public void setMes(String mes) {
		this.mes = mes;
	}
	
	

}
