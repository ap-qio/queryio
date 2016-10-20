package com.queryio.common.beans;

import java.sql.Timestamp;

public class NNRestoreInfo {

	private String restoreId;	
	private String migrationId;
	private String namenodeId;
	private Timestamp startTime;
	private Timestamp endTime;
	private String status;
	
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
	public String getRestoreId() {
		return restoreId;
	}
	public void setRestoreId(String restoreId) {
		this.restoreId = restoreId;
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
}
