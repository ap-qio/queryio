package com.queryio.core.bean;

import java.sql.Timestamp;

public class MigrationInfo {
	private int id;
	private String namenodeId;
	private boolean importType;
	private String title;
	private Timestamp startTime;
	private Timestamp endTime;	
	private String dataStore;
	private String destinationPath;
	private String sourcePath;
	private double progress = 0;
	private String status;
	private boolean secure;
	private boolean unzip;
	private String encryptionType;
	private String compressionType;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDataStore() {
		return dataStore;
	}
	public void setDataStore(String dataStore) {
		this.dataStore = dataStore;
	}
	public String getDestinationPath() {
		return destinationPath;
	}
	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getProgress() {
		return progress;
	}
	public void setProgress(double progress) {
		this.progress = progress;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public boolean isImportType() {
		return importType;
	}
	public void setImportType(boolean importType) {
		this.importType = importType;
	}
	public boolean isSecure() {
		return secure;
	}
	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	public String getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	public String getNamenodeId() {
		return namenodeId;
	}
	public void setNamenodeId(String namenodeId) {
		this.namenodeId = namenodeId;
	}
	public boolean isUnzip() {
		return unzip;
	}
	public void setUnzip(boolean unzip) {
		this.unzip = unzip;
	}
	public String getEncryptionType() {
		return encryptionType;
	}
	public void setEncryptionType(String encryptionType) {
		this.encryptionType = encryptionType;
	}
	public String getCompressionType() {
		return compressionType;
	}
	public void setCompressionType(String compressionType) {
		this.compressionType = compressionType;
	}
}
