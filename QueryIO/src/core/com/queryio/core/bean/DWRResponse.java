package com.queryio.core.bean;

public class DWRResponse {

	private String id;
	private String responseMessage;
	private int responseCode;
	private boolean taskSuccess;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public boolean isTaskSuccess() {
		return taskSuccess;
	}

	public void setTaskSuccess(boolean taskStatus) {
		this.taskSuccess = taskStatus;
	}

	public void setDwrResponse(boolean taskSuccess, String responseMessage, int responseCode) {
		this.taskSuccess = taskSuccess;
		this.responseMessage = responseMessage;
		this.responseCode = responseCode;
	}
}
