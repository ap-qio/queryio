package com.queryio.core.datasources;
import java.io.Serializable;
import java.util.ArrayList;
public class DataBaseDataSource implements Serializable{
	private static final long serialVersionUID = -4264457489860268027L;
	
	public String id=null;
	public String driver=null;
	public String connectionURL=null;
	public String userName=null;
	public String password=null;
	public String jarFile=null;
	public int maxConnections = 20;
	public int maxIdleConnections = 10;
	public int waitTimeMilliSeconds = 30000;
	public ArrayList<String> tableNames = null;
	
	public void setId(String id) {
		this.id = id;
	}
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
	public void setJarFileName(String jarFile) {
		this.jarFile = jarFile;
	}
	
	public void setTableNames(ArrayList<String> tableNames){
		this.tableNames = tableNames;
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
	
	
	public String getId() {
		return (this.id);
	}
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
	public String getJarFileName() {
		return (this.jarFile);
	}
	public ArrayList<String> getTableNames() {
		return (this.tableNames);
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
