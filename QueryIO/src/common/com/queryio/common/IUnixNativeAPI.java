package com.queryio.common;

import com.queryio.common.dstruct.DiskInfo;
import com.queryio.common.dstruct.DriveInfo;
import com.queryio.common.dstruct.LogInfo;
import com.queryio.common.dstruct.MemoryInfo;
import com.queryio.common.dstruct.NFSStat;
import com.queryio.common.dstruct.NetworkInfo;
import com.queryio.common.dstruct.ProcessInfo;

public interface IUnixNativeAPI {
	boolean ping() throws Exception;

	/**
	 * Disconnect from remote machine
	 */
	void disconnect() throws Exception;

	int getCPUUsage() throws Exception;

	int getUserCPUUsage() throws Exception;

	int getSystemCPUUsage() throws Exception;

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

	LogInfo searchInLogFile(String logFile, String[] search, long lastLine) throws Exception;

	long getLastLogFileLineNumber(String logFile) throws Exception;

	void executeScript(String scriptFile) throws Exception;

}
