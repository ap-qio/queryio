package com.queryio.common.database;

public class CustomTagDBConfig {

	private String customTagUrl;
	private String customTagDriverClass;
	private String customTagUserName;
	private String customTagPassword;
	private String customTagPoolName;
	private int customTagMaxIdleConn;
	private long customTagMaxWaitTime;
	private String customTagDriverJarPath;
	private int customTagMaxConn;
	private String customTagDBType;
	
	public String getCustomTagDBType() {
		return customTagDBType;
	}

	public void setCustomTagDBType(String customTagDBType) {
		this.customTagDBType = customTagDBType;
	}

	public String getCustomTagUrl() {
		return customTagUrl;
	}

	public void setCustomTagUrl(String customTagUrl) {
		this.customTagUrl = customTagUrl;
	}

	public String getCustomTagDriverClass() {
		return customTagDriverClass;
	}

	public void setCustomTagDriverClass(String customTagDriverClass) {
		this.customTagDriverClass = customTagDriverClass;
	}

	public String getCustomTagUserName() {
		return customTagUserName;
	}

	public void setCustomTagUserName(String customTagUserName) {
		this.customTagUserName = customTagUserName;
	}

	public String getCustomTagPassword() {
		return customTagPassword;
	}

	public void setCustomTagPassword(String customTagPassword) {
		this.customTagPassword = customTagPassword;
	}

	public String getCustomTagPoolName() {
		return customTagPoolName;
	}

	public void setCustomTagPoolName(String customTagPoolName) {
		this.customTagPoolName = customTagPoolName;
	}

	public String getCustomTagDriverJarPath() {
		return customTagDriverJarPath;
	}

	public void setCustomTagDriverJarPath(String customTagDriverJarPath) {
		this.customTagDriverJarPath = customTagDriverJarPath;
	}

	
	public int getCustomTagMaxConn() {
		return customTagMaxConn;
	}

	public void setCustomTagMaxConn(int customTagMaxConn) {
		this.customTagMaxConn = customTagMaxConn;
	}

	public int getCustomTagMaxIdleConn() {
		return customTagMaxIdleConn;
	}

	public void setCustomTagMaxIdleConn(int customTagMaxIdleConn) {
		this.customTagMaxIdleConn = customTagMaxIdleConn;
	}

	public long getCustomTagMaxWaitTime() {
		return customTagMaxWaitTime;
	}

	public void setCustomTagMaxWaitTime(long customTagMaxWaitTime) {
		this.customTagMaxWaitTime = customTagMaxWaitTime;
	}
	
	@Override
	public String toString() {
		return this.customTagPoolName;
	}
}
