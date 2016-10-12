package com.queryio.core.datasources;

import java.io.Serializable;

public class EmailDataSource implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4264457489860268027L;
	private String id;
	private String emailAddress;
	private String password;
	private String mailServerAddress;
	private String accountName;
	private String protocol;
	private String socketType;
	private int port;
	private long connectionTimeOut;
	private long readTimeOut;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMailServerAddress() {
		return mailServerAddress;
	}
	public void setMailServerAddress(String mailServerAddress) {
		this.mailServerAddress = mailServerAddress;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getSocketType() {
		return socketType;
	}
	public void setSocketType(String socketType) {
		this.socketType = socketType;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public long getConnectionTimeOut() {
		return connectionTimeOut;
	}
	public void setConnectionTimeOut(long connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
	}
	public long getReadTimeOut() {
		return readTimeOut;
	}
	public void setReadTimeOut(long readTimeOut) {
		this.readTimeOut = readTimeOut;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
