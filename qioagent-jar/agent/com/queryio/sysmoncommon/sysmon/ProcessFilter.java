package com.queryio.sysmoncommon.sysmon;

import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;

public class ProcessFilter 
{
	private boolean pidFilterOnly;
	private int originalpid = -1;
	int pid = -1;
	String name = null;
	String owner = null;
	String commandFilter = null;
	
	public void setPid(int pid) 
	{
		this.pid = pid;
	}
	
	public int getPid() 
	{
		return pid;
	}

	public String getOwner() 
	{
		return owner;
	}

	public String getCommandFilter() 
	{
		return commandFilter;
	}

	public void setOwner(String owner) 
	{
		this.owner = owner;
	}
	
	public void setCommandFilter(String commandFilter) 
	{
		this.commandFilter = commandFilter;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}
	
	public boolean matches(ProcessInfo processInfo)
	{
		try
		{
			if (pid != -1 && processInfo.getProcessID() == pid)
			{
				return true;
			}
			else if (!pidFilterOnly && name != null && owner != null && processInfo.getUserName() != null && 
				processInfo.getName().equals(name) && processInfo.getUserName().equals(owner))
			{
				if (commandFilter != null && commandFilter.length() > 0 && processInfo.getCommand() != null)
				{
					// Name and Owner matches, if command filter is a sub-set of p.command, 
					// return true else false
					return processInfo.getCommand().indexOf(commandFilter) != -1;
				}
				// This is based only on Name & Owner
				return true;
			}
		}
		catch (Exception ex)
		{
			// do nothing.
		}
		return false;
	}

	public boolean isPidFilterOnly() 
	{
		return pidFilterOnly;
	}

	public void verifyFilter() 
	{
		if (originalpid != -1 && (name == null || name.trim().length() == 0)
			&& (owner == null || owner.trim().length() == 0))
		{
			this.pidFilterOnly = true;
		}
		else
		{
			this.pidFilterOnly = false;
		}
	}

	public void setOriginalPid(int originalpid) 
	{
		this.originalpid = originalpid;
		setPid(originalpid);
	}
	
	public String toString() 
	{
		StringBuffer buffer = new StringBuffer();
		
		if(pid != -1)
		{
			buffer.append("PID: ");
			buffer.append(pid);
		}
		if (name != null)
		{
			buffer.append(", Name: ");
			buffer.append(name);
		}
		if (owner != null)
		{
			buffer.append(", User: ");
			buffer.append(owner);
		}
		if (commandFilter != null)
		{
			buffer.append(", Command filter: ");
			buffer.append(commandFilter);
		}
		return buffer.toString();
	}
}
