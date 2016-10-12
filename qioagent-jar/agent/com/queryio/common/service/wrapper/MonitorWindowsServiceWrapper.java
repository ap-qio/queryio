package com.queryio.common.service.wrapper;

import com.queryio.common.util.AppLogger;

import com.queryio.sysmoncommon.pdh.PdhNativeAPI;
import com.queryio.sysmoncommon.pdh.dstruct.AttributeNode;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;


public class MonitorWindowsServiceWrapper
{
	private String controllerId;
	private String monitorHostName;
	private String userName;
	private String password;

	private String domainName = null;

	public MonitorWindowsServiceWrapper(final String monitorHostName, final String controllerId)
	{
		this.setMonitorHostName(monitorHostName);
		this.controllerId = controllerId;
	}
	
	public void setMonitorHostName(final String host)
	{
		this.monitorHostName = host;
	}
	
	public void setUserName(final String username)
	{
		this.userName = username;
	}
	
	public void setPassword(final String pswd)
	{
		this.password = pswd;
	}

	public void setControllerId(final String controllerId)
	{
		this.controllerId = controllerId;
	}

	public void setDomainName(final String domainName)
	{
		if ((domainName == null) || (domainName.trim().length() > 0))
		{
			this.domainName = domainName;
		}
	}

	public boolean connectToMachine()
	{
		return PdhNativeAPI.connectForCredential(this.domainName, this.monitorHostName, this.userName, this.password);
	}

	public boolean disconnectFromMachine()
	{
		return PdhNativeAPI.disconnectForHost(this.domainName, this.monitorHostName);
	}

	public boolean collectData(final AttributeNode[] nodes)
	{
		return PdhNativeAPI.connectMachine(this.controllerId, this.monitorHostName, true, nodes);
	}

	public AttributeNode[] fetchCollectedData()
	{
		AttributeNode[] controllerData;
		AppLogger.getLogger().debug("Calling PdhNativeLib");
		AppLogger.getLogger().debug("controllerId : " + controllerId);
		controllerData = PdhNativeAPI.collectData(this.controllerId);
		AppLogger.getLogger().debug("controllerData == null : " + (controllerData == null));
		return controllerData;
	}

	public AttributeNode[] fetchAttributes(final String[] objectNames)
	{
		AttributeNode[] fetchedAttributes;
			fetchedAttributes = PdhNativeAPI.fetchAttributes(this.controllerId, this.monitorHostName,
					true, objectNames);
		return fetchedAttributes;
	}

	public NetworkInfo[] getNetworkInfo()
	{
		NetworkInfo[] networkInfos;
			networkInfos = PdhNativeAPI.getNetworkInfo(this.controllerId, this.monitorHostName,
					true);
		return networkInfos;
	}

	public ProcessInfo[] getProcessInfo()
	{
		ProcessInfo[] processes;
			processes = PdhNativeAPI
					.getProcessInfo(this.controllerId, this.monitorHostName, true);
		return processes;

	}

	public void enableNativeLog(final boolean debug)
	{
		PdhNativeAPI.setDebug(debug);
	}

}
