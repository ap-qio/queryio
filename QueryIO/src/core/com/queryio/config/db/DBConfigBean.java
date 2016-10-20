package com.queryio.config.db;

public class DBConfigBean {

	String connectionName;
	String connectionType;
	String primaryUsername;
	String primaryPassword;
	String primaryConnectionURL;
	String primaryDriverName;
	String primaryJdbcJar;
	long maxConnection;
	long maxIdleConnection;
	long waitTimeinMillis;
	boolean isCustomTagDB;
	boolean isPrimary;
	String nameNodeId;
	
	public String getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	public String getNameNodeId() {
		return nameNodeId;
	}
	public void setNameNodeId(String nameNodeId) {
		this.nameNodeId = nameNodeId;
	}
	boolean isMigrated;
	
	public long getMaxConnection() {
		return maxConnection;
	}
	public void setMaxConnection(long maxConnection) {
		this.maxConnection = maxConnection;
	}
	public long getMaxIdleConnection() {
		return maxIdleConnection;
	}
	public void setMaxIdleConnection(long maxIdleConnection) {
		this.maxIdleConnection = maxIdleConnection;
	}
	public long getWaitTimeinMillis() {
		return waitTimeinMillis;
	}
	public void setWaitTimeinMillis(long waitTimeinMillis) {
		this.waitTimeinMillis = waitTimeinMillis;
	}

	
	public boolean isMigrated() {
		return isMigrated;
	}
	public void setMigrated(boolean isMigrated) {
		this.isMigrated = isMigrated;
	}
	public boolean isIsPrimary() {
		return isPrimary;
	}
	public void setIsPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}
	public boolean isCustomTagDB() {
		return isCustomTagDB;
	}
	public void setCustomTagDB(boolean isCustomTagDB) {
		this.isCustomTagDB = isCustomTagDB;
	}
	public String getConnectionName() {
		return connectionName;
	}
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
	public String getPrimaryUsername() {
		return primaryUsername;
	}
	public void setPrimaryUsername(String primaryUsername) {
		this.primaryUsername = primaryUsername;
	}
	public String getPrimaryPassword() {
		return primaryPassword;
	}
	public void setPrimaryPassword(String primaryPassword) {
		this.primaryPassword = primaryPassword;
	}
	public String getPrimaryConnectionURL() {
		return primaryConnectionURL;
	}
	public void setPrimaryConnectionURL(String primaryConnectionURL) {
		this.primaryConnectionURL = primaryConnectionURL;
	}
	public String getPrimaryDriverName() {
		return primaryDriverName;
	}
	public void setPrimaryDriverName(String primaryDriverName) {
		this.primaryDriverName = primaryDriverName;
	}
	public String getPrimaryJdbcJar() {
		return primaryJdbcJar;
	}
	public void setPrimaryJdbcJar(String primaryJdbcJar) {
		this.primaryJdbcJar = primaryJdbcJar;
	}
}
