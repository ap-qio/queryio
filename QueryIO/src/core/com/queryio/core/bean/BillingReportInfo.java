package com.queryio.core.bean;

public class BillingReportInfo {
	private long usedStorage;

	private long bytesRead;
	private long bytesWritten;

	private long putRequests;
	private long getRequests;
	private long listRequests;
	private long deleteRequests;

	public long getUsedStorage() {
		return usedStorage;
	}

	public void setUsedStorage(long usedStorage) {
		this.usedStorage = usedStorage;
	}

	public long getBytesRead() {
		return bytesRead;
	}

	public void setBytesRead(long bytesRead) {
		this.bytesRead = bytesRead;
	}

	public long getBytesWritten() {
		return bytesWritten;
	}

	public void setBytesWritten(long bytesWritten) {
		this.bytesWritten = bytesWritten;
	}

	public long getPutRequests() {
		return putRequests;
	}

	public void setPutRequests(long putRequests) {
		this.putRequests = putRequests;
	}

	public long getGetRequests() {
		return getRequests;
	}

	public void setGetRequests(long getRequests) {
		this.getRequests = getRequests;
	}

	public long getListRequests() {
		return listRequests;
	}

	public void setListRequests(long listRequests) {
		this.listRequests = listRequests;
	}

	public long getDeleteRequests() {
		return deleteRequests;
	}

	public void setDeleteRequests(long deleteRequests) {
		this.deleteRequests = deleteRequests;
	}
}
