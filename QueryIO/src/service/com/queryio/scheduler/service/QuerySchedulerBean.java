package com.queryio.scheduler.service;


public class QuerySchedulerBean {

	private String name;
	private String group;
	
	private String notificationType;
	private String notificationMessage;
	private String emailUserIds;
	private long time;
	private String selectedFormat;
	private String query;
	private int interval;
	private String nameNode;
	private String username;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	private boolean notificationEnable;
	
	public boolean isNotificationEnable() {
		return notificationEnable;
	}
	public void setNotificationEnable(boolean notificationEnable) {
		this.notificationEnable = notificationEnable;
	}
	public void setNameNode(String nameNode){
		this.nameNode = nameNode;
	}
	public String getNameNode(){
		return nameNode;
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
	public String getEmailUserIds() {
		return emailUserIds;
	}
	public void setEmailUserIds(String emailUserIds) {
		this.emailUserIds = emailUserIds;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getSelectedFormat() {
		return selectedFormat;
	}
	public void setSelectedFormat(String selectedFormat) {
		this.selectedFormat = selectedFormat;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	
	
	 
	
	
	
}
