package com.queryio.core.bean;

public class DiskMonitoredData {
	private int hostId;
	private String diskName;
	private float diskByteReadsPerSec;
	private float diskByteWritesPerSec;
	private String diskHealthStatus;

	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}

	public String getDiskName() {
		return diskName;
	}

	public void setDiskName(String diskName) {
		this.diskName = diskName;
	}

	public float getDiskByteReadsPerSec() {
		return diskByteReadsPerSec;
	}

	public void setDiskByteReadsPerSec(float diskByteReadsPerSec) {
		this.diskByteReadsPerSec = diskByteReadsPerSec;
	}

	public float getDiskByteWritesPerSec() {
		return diskByteWritesPerSec;
	}

	public void setDiskByteWritesPerSec(float diskByteWritesPerSec) {
		this.diskByteWritesPerSec = diskByteWritesPerSec;
	}

	public String getDiskHealthStatus() {
		return diskHealthStatus;
	}

	public void setDiskHealthStatus(String diskHealthStatus) {
		this.diskHealthStatus = diskHealthStatus;
	}
}
