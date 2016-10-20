package com.queryio.core.bean;

public class CusomTagDatabase {

	String userName;
	String password;
	String url;	
	String driverName; 
	
	
	
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
	
	public void setDriverName(String className) {
		this.driverName = className;
	}
}
