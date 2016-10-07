package com.queryio.core.bean;

public class SearchedHostDetail
{
	private String hostIP;
	private boolean isAlreadyAdded;
	private boolean isAvailable;
	private boolean nameNode;
	private boolean dataNode;
	private boolean resourceManager;
	private boolean nodeManager;
	
	public boolean isNameNode() {
		return nameNode;
	}
	public void setNameNode(boolean nameNode) {
		this.nameNode = nameNode;
	}
	public boolean isDataNode() {
		return dataNode;
	}
	public void setDataNode(boolean dataNode) {
		this.dataNode = dataNode;
	}
	public boolean isResourceManager() {
		return resourceManager;
	}
	public void setResourceManager(boolean resourceManager) {
		this.resourceManager = resourceManager;
	}
	public boolean isNodeManager() {
		return nodeManager;
	}
	public void setNodeManager(boolean nodeManager) {
		this.nodeManager = nodeManager;
	}
	public boolean isAvailable() {
		return isAvailable;
	}
	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	public String getHostIP() {
		return hostIP;
	}
	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}
	public boolean isAlreadyAdded() {
		return isAlreadyAdded;
	}
	public void setAlreadyAdded(boolean isAlreadyAdded) {
		this.isAlreadyAdded = isAlreadyAdded;
	}
}
