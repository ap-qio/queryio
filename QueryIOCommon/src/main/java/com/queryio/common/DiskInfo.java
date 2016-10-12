package com.queryio.common;

public class DiskInfo 
{
	String name;
	String capacity;
	String availableSpace;
	
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public String getCapacity() 
	{
		return capacity;
	}
	
	public void setCapacity(String capacity) 
	{
		this.capacity = capacity;
	}
	
	public String getAvailableSpace() 
	{
		return availableSpace;
	}
	
	public void setAvailableSpace(String free) 
	{
		this.availableSpace = free;
	}
}
