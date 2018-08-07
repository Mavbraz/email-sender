package sender;

public class Email {
	
	private String host;
	private String port;
	private boolean ssl;
	private String user;
	private String password;
	private String[] to;
	private String[] cc;
	private String[] bcc;
	private String subject;
	private String message;
	private String[] attachments;
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getPort() {
		return port;
	}
	
	public void setPort(String port) {
		this.port = port;
	}
	
	public boolean isSsl() {
		return ssl;
	}
	
	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String[] getTo() {
		return to;
	}
	
	public void setTo(String[] to) {
		this.to = to;
	}
	
	public String[] getCc() {
		return cc;
	}
	
	public void setCc(String[] cc) {
		this.cc = cc;
	}
	
	public String[] getBcc() {
		return bcc;
	}
	
	public void setBcc(String[] bcc) {
		this.bcc = bcc;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String[] getAttachments() {
		return attachments;
	}
	
	public void setAttachments(String[] attachments) {
		this.attachments = attachments;
	}
	
}
