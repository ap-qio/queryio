package com.queryio.common.beans;

import java.sql.Timestamp;

public class NNDBMigrationInfo {
	private int hostId;
	private String hostIP;
	private String migrationId;
	private String namenodeId;
	private String dbName;
	private String backupFolder;
	private Timestamp startTime;
	private Timestamp endTime;
	private String status;
	private String error;

	public String getMigrationId() {
		return migrationId;
	}

	public void setMigrationId(String migrationId) {
		this.migrationId = migrationId;
	}

	public String getNamenodeId() {
		return namenodeId;
	}

	public void setNamenodeId(String namenodeId) {
		this.namenodeId = namenodeId;
	}

	public String getBackupFolder() {
		return backupFolder;
	}

	public void setBackupFolder(String backupFolder) {
		this.backupFolder = backupFolder;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}

	public String getHostIP() {
		return hostIP;
	}

	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}
}
