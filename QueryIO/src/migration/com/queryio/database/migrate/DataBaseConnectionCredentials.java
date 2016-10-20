package com.queryio.database.migrate;

import java.util.ArrayList;

public class DataBaseConnectionCredentials {
	
	public String driver=null;
	public String connectionURL=null;
	public String userName=null;
	public String password=null;
	public boolean isImportAllTables = false;
	public ArrayList<String> tableNames = null;
	public int numberOfMappers=1;
	public int maxConnections = 20;
	public int maxIdleConnections = 10;
	public int waitTimeMilliSeconds = 30000;
	public String targetDirectory=null;
	public String jarFile=null;
	//setters
	public void  setDriver(String driver) {
		this.driver = driver;
	}
	public void  setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}
	public void  setUserName(String userName) {
		this.userName = userName;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setIsImportAllTables(boolean isImportAllTables) {
		this.isImportAllTables = isImportAllTables;
	}
	public void setTableNames(ArrayList<String> tableNames) {
		this.tableNames = tableNames;
	}
	public void setNumberOfMappers(int numberOfMappers) {
		this.numberOfMappers = numberOfMappers;
	}
	public void setTargetDirectory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}
	public void setJarFileName(String jarFile) {
		this.jarFile = jarFile;
	}
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}
	public void setMaxIdleConnections(int maxIdleConnections) {
		this.maxIdleConnections = maxIdleConnections;
	}
	public void setWaitTimeMilliSeconds(int waitTimeMilliSeconds) {
		this.waitTimeMilliSeconds = waitTimeMilliSeconds;
	}
	//getters
	public String getDriver() {
		return (this.driver);
	}
	public String  getConnectionURL() {
		return (this.connectionURL);
	}
	public String  getUserName() {
		return (this.userName);
	}
	public String getPassword() {
		return (this.password);
	}
	public boolean getIsImportAllTables() {
		return (this.isImportAllTables);
	}
	public ArrayList<String> getTableNames() {
		return (this.tableNames );
	}
	public int getNumberOfMappers() {
		return(this.numberOfMappers);
	}
	public String getTargetDirectory() {
		return (this.targetDirectory);
	}
	public String getJarFileName() {
		return (this.jarFile);
	}
	public int getMaxConnections() {
		return (this.maxConnections );
	}
	public int getMaxIdleConnections() {
		return (this.maxIdleConnections );
	}
	public int getWaitTimeMilliSeconds() {
		return (this.waitTimeMilliSeconds);
	}
}
