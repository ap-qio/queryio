package com.queryio.core.datasources;

import java.io.Serializable;

public class HTTPDataSource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4460639773878315799L;
	private String id;
	private String baseURL;
	private String userName;
	private String password;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
