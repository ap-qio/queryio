package com.queryio.core.bean;

import java.io.Serializable;

public class Node implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String nodeType;
	private Integer hostId;
	private String status;
	private String jmxPort;
	private String serviceStatus;
	private String hiveServiceStatus;
	private boolean monitor;

	public String getJmxPort() {
		return jmxPort;
	}

	public void setJmxPort(String jmxPort) {
		this.jmxPort = jmxPort;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	public String getHiveServiceStatus() {
		return hiveServiceStatus;
	}

	public void setHiveServiceStatus(String hiveServiceStatus) {
		this.hiveServiceStatus = hiveServiceStatus;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}

	public String toString() {
		return "nodeId : " + id + " nodeType : " + nodeType + " hostId :" + hostId + " status :" + status;
	}

	public boolean equals(Object o) {
		if (o instanceof Node) {
			Node node = (Node) o;
			if (this.id.equals(node.id))
				return true;
		}
		return false;
	}

	public int hashCode() {
		int value = 1;
		value += value * 31 + (id != null ? id.hashCode() : 0);
		value += value * 31 + (nodeType != null ? nodeType.hashCode() : 0);
		value += value * 31 + (hostId != null ? hostId.hashCode() : 0);
		value += value * 31 + (status != null ? status.hashCode() : 0);
		value += value * 31 + (jmxPort != null ? jmxPort.hashCode() : 0);
		return value;
	}

	public boolean isMonitor() {
		return monitor;
	}

	public void setMonitor(boolean monitoring) {
		this.monitor = monitoring;
	}
}
