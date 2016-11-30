package com.queryio.scheduler.service;

public class TriggerDetailBean {

	private String jobName = null;
	private String jobGroup = null;
	private String startTime = null;
	private String endTime = null;
	private String status = null;
	private String reasonForFailure = null;
	private Integer id = null;

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public Integer getID() {
		return id;
	}

	public void setID(Integer id) {
		this.id = id;
	}

	public String getReasonForFailure() {
		return reasonForFailure;
	}

	public void setReasonForFailure(String reasonForFailure) {
		this.reasonForFailure = reasonForFailure;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
