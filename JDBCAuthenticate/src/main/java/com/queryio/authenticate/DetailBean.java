package com.queryio.authenticate;

public class DetailBean {

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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	
	String userName;
	String password;
	String url;
	String driverName;
	String driverJar;
	public String getDriverJar() {
		return driverJar;
	}
	public void setDriverJar(String driverJar) {
		this.driverJar = driverJar;
	}
	
}
