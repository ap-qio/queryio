package com.queryio.core.monitor.beans;

public class SystemAttribute 
{
	private String attributeName;
	private String objectName;
	private String columnName;
	private String description;
	private String dataType;
	private String groupName;
	
	public SystemAttribute(String attributeName, String objectName, String columnName, String description, String dataType, String groupName)
	{
		this.attributeName = attributeName;
		this.objectName = objectName;
		this.columnName = columnName;
		this.dataType = dataType;
		this.groupName = groupName;
	}
	
	public String getAttributeName() 
	{
		return attributeName;
	}
	
	public void setAttributeName(String attributeName) 
	{
		this.attributeName = attributeName;
	}
	
	public String getObjectname() 
	{
		return objectName;
	}
	
	public void setObjectname(String objectname) 
	{
		this.objectName = objectname;
	}
	
	public String getColumnName() 
	{
		return columnName;
	}
	
	public void setColumnName(String columnName) 
	{
		this.columnName = columnName;
	}
	
	public String getDataType() 
	{
		return dataType;
	}

	public void setDataType(String dataType) 
	{
		this.dataType = dataType;
	}
	
	public String getGroupName() 
	{
		return groupName;
	}
	
	public void setGroupName(String groupName) 
	{
		this.groupName = groupName;
	}

	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String description) 
	{
		this.description = description;
	}
}
