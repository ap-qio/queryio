package com.queryio.core.bean;

public class CheckpointNode {
	String id;
	int hostId;
	String status = "Stopped";
	String dirPath;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDirPath() {
		return dirPath;
	}

	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}

	public CheckpointNode(String id, int hostId) {
		this.hostId = hostId;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String nodeId) {
		this.id = nodeId;
	}

	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
}
