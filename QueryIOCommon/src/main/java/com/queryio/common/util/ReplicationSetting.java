package com.queryio.common.util;

public class ReplicationSetting {
	private String primaryNameNodeHost;
	private String secondaryNameNodeHost;
	private int primaryNameNodePort;
	private int secondaryNameNodePort;
	private int delay;
	private int batchSize;

	private boolean isInitial;

	public boolean isInitial() {
		return isInitial;
	}

	public void setInitial(boolean isInitial) {
		this.isInitial = isInitial;
	}

	public String getPrimaryNameNodeHost() {
		return primaryNameNodeHost;
	}

	public void setPrimaryNameNodeHost(String primaryNameNodeHost) {
		this.primaryNameNodeHost = primaryNameNodeHost;
	}

	public String getSecondaryNameNodeHost() {
		return secondaryNameNodeHost;
	}

	public void setSecondaryNameNodeHost(String secondaryNameNodeHost) {
		this.secondaryNameNodeHost = secondaryNameNodeHost;
	}

	public int getPrimaryNameNodePort() {
		return primaryNameNodePort;
	}

	public void setPrimaryNameNodePort(int primaryNameNodePort) {
		this.primaryNameNodePort = primaryNameNodePort;
	}

	public int getSecondaryNameNodePort() {
		return secondaryNameNodePort;
	}

	public void setSecondaryNameNodePort(int secondaryNameNodePort) {
		this.secondaryNameNodePort = secondaryNameNodePort;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
}
