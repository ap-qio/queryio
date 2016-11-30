package com.queryio.scheduler.service;

public class SchedulerBean {

	private String name;
	private String group;
	private String reportId;
	private String notificationType;
	private String notificationMessage;
	private String emailUserIds;
	private long time;
	private int interval;
	private String selectedFormat;
	private String nodeId;
	private String jobName;
	private boolean notificationEnable;

	public boolean isNotificationEnable() {
		return notificationEnable;
	}

	public void setNotificationEnable(boolean notificationEnable) {
		this.notificationEnable = notificationEnable;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getSelectedFormat() {
		return selectedFormat;
	}

	public void setSelectedFormat(String selectedFormat) {
		this.selectedFormat = selectedFormat;
	}

	public String getEmailUserIds() {
		return emailUserIds;
	}

	public void setEmailUserIds(String emailUserIds) {
		this.emailUserIds = emailUserIds;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getNotificationMessage() {
		return notificationMessage;
	}

	public void setNotificationMessage(String notificationMessage) {
		this.notificationMessage = notificationMessage;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}
