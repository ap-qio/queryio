package com.queryio.core.bean;

public class Status 
{
	private String nodeId;
	private long timestamp;
	private int status;
	
	public String getNodeId() 
	{
		return nodeId;
	}
	
	public void setNodeId(String nodeId) 
	{
		this.nodeId = nodeId;
	}
	
	public long getTimestamp() 
	{
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) 
	{
		this.timestamp = timestamp;
	}
	
	public int getStatus() 
	{
		return status;
	}
	
	public void setStatus(int status) 
	{
		this.status = status;
	}
}
