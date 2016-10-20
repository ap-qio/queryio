package com.queryio.core.bean;

import java.sql.Timestamp;

public class HadoopService {
	private String nodeId;
	private Timestamp timeOfCall;	
	private String type;
	private String outputFilePath;	
	private String status;	
	
	public Timestamp getTimeOfCall() {
		return timeOfCall;
	}
	public void setTimeOfCall(Timestamp timeOfCall) {
		this.timeOfCall = timeOfCall;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOutputFilePath() {
		return outputFilePath;
	}
	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
}
