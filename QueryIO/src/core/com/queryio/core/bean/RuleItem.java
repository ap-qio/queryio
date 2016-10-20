package com.queryio.core.bean;

public class RuleItem 
{
	private String ruleId;
	private String hostName;
	private String severity;
	private String attribute;
	private String description;
	private String notificationType;
	private String nodeId;
	private boolean ruleIgnored;
	
	public String getRuleId() 
	{
		return ruleId;
	}
	
	public void setRuleId(String ruleId) 
	{
		this.ruleId = ruleId;
	}
	
	public String getHostName() 
	{
		return hostName;
	}
	
	public void setHostName(String hostname) 
	{
		this.hostName = hostname;
	}
	
	public String getSeverity() 
	{
		return severity;
	}
	
	public void setSeverity(String severity) 
	{
		this.severity = severity;
	}
	
	public String getAttribute() 
	{
		return attribute;
	}
	
	public void setAttribute(String attribute) 
	{
		this.attribute = attribute;
	}
	
	public String getDescription() 
	{
		return description;
	}
	
	public void setDescription(String description) 
	{
		this.description = description;
	}
	
	public boolean isRuleIgnored() 
	{
		return ruleIgnored;
	}

	public void setRuleIgnored(boolean ruleIgnored) 
	{
		this.ruleIgnored = ruleIgnored;
	}

	public String getNodeId() 
	{
		return nodeId;
	}

	public void setNodeId(String nodeId) 
	{
		this.nodeId = nodeId;
	}

	public String getNotificationType() 
	{
		return notificationType;
	}

	public void setNotificationType(String notificationType) 
	{
		this.notificationType = notificationType;
	}
}
