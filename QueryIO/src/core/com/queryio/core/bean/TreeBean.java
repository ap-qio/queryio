package com.queryio.core.bean;

import java.util.ArrayList;

public class TreeBean {
	private String nodeId = null;

	private ArrayList volumes = new ArrayList();

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public void addChild(final Volume volume) {
		if (this.volumes == null) {
			this.volumes = new ArrayList();
		}
		if (!this.volumes.contains(volume)) {
			this.volumes.add(volume);
		}
	}

	public ArrayList getVolumes() {
		return volumes;
	}

	public void setVolumes(ArrayList volumes) {
		this.volumes = volumes;
	}
}
