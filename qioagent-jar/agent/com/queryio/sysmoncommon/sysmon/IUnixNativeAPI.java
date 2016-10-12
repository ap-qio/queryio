package com.queryio.sysmoncommon.sysmon;

import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.LogInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NFSStat;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;

public interface IUnixNativeAPI
{
	boolean ping() throws Exception;

	/**
	 * Disconnect from remote machine
	 */
	void disconnect() throws Exception;

	int getCPUUsage() throws Exception;

	int getUserCPUUsage() throws Exception;
	float getSystemCPUUsage() throws Exception;
	int getWaitCPUUsage() throws Exception;
	
	int get1MinLoadAverage() throws Exception;
	int get5MinLoadAverage() throws Exception;
	int get15MinLoadAverage() throws Exception;
	
	MemoryInfo getPhysicalMemoryInfo() throws Exception;

	MemoryInfo getVirtualMemoryInfo() throws Exception;

	ProcessInfo[] getProcessInfo() throws Exception;
	int getProcessCount() throws Exception;

	NetworkInfo[] getNetworkInfo() throws Exception;
	NFSStat getNFSStats() throws Exception;

	DiskInfo[] getPhysicalDiskInfo() throws Exception;

	void collectData() throws Exception;

	DriveInfo[] getDiskSpaceInfo() throws Exception;

	LogInfo searchInLogFile(String logFile, String [] search, long lastLine) throws Exception;
	
	long getLastLogFileLineNumber(String logFile) throws Exception;
	
	void executeScript(String scriptFile) throws Exception;


}
