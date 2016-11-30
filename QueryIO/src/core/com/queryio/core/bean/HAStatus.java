package com.queryio.core.bean;

public class HAStatus {
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private boolean haEnabled;
	private String activeNodeId;
	private String standbyNodeId;

	public boolean isHaEnabled() {
		return haEnabled;
	}

	public void setHaEnabled(boolean haEnabled) {
		this.haEnabled = haEnabled;
	}

	public String getActiveNodeId() {
		return activeNodeId;
	}

	public void setActiveNodeId(String activeNodeId) {
		this.activeNodeId = activeNodeId;
	}

	public String getStandbyNodeId() {
		return standbyNodeId;
	}

	public void setStandbyNodeId(String standbyNodeId) {
		this.standbyNodeId = standbyNodeId;
	}

}
