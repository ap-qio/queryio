package com.queryio.core.bean;

public class QueryIOService {

	String nodeId = null;
	String serviceType = null;
	String status = null;
	
	public QueryIOService(String nodeId, String serviceType, String status) {
		this.nodeId = nodeId;
		this.serviceType = serviceType;
		this.status = status;
	}
	
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	

}
