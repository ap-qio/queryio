package com.queryio.sysmoncommon.sysmon;

import java.util.LinkedList;

import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NFSStat;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.UnixNetworkInfo;
import com.queryio.sysmoncommon.sysmon.protocol.AbstractProtocolWrapper;

/*
 * @(#)  AbstractUnixNativeAPI.java
 *
 * Copyright (C) 2002 Exceed Consultancy Services. All Rights Reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL 
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

abstract class AbstractUnixNativeAPI implements IUnixNativeAPI {
	protected static final String NEW_LINE_TOKENS = "\r\n\f"; //$NON-NLS-1$
	protected final String COMMAND_NOT_FOUND = "command not found";

	protected AbstractDataParser dataParser;

	protected String hostName;
	protected String user;
	protected String pwd;
	protected String privateKeyFile;
	protected int authMethod;
	protected int port;
	protected int protocol;
	protected ProcessFilter processFilter;
	protected String firewallUserName = "";
	protected String firewallPassword = "";
	protected String superUserName = "";
	protected String superUserPassword = "";
	protected boolean monitorProcesses;
	protected int refreshInterval;

	private boolean bFirstTime = true;
	private LinkedList llNetworkInfo;

	/**
	 * @param user
	 * @param pwd
	 * @param key
	 * @param authMethod
	 */
	protected AbstractUnixNativeAPI(final String hostName, final String user, final String pwd, final String key,
			final int authMethod, final int port, final int iProtocol, final ProcessFilter processFilter,
			final String fUserName, final String fPassword, final String superUserName, final String superUserPassword,
			final boolean monitorProcess, final int interval) {
		this.hostName = hostName;
		this.user = user;
		this.pwd = pwd;
		this.privateKeyFile = key;
		this.authMethod = authMethod;
		this.port = port;
		this.protocol = iProtocol;
		this.processFilter = processFilter;
		this.firewallUserName = fUserName;
		this.firewallPassword = fPassword;
		this.superUserName = superUserName;
		this.superUserPassword = superUserPassword;
		this.monitorProcesses = monitorProcess;
		this.refreshInterval = interval;
	}

	/**
	 * Get cpu usage of the remote machine Execute iostat command with -c option
	 * Parse the returned String. Extract the idle time of cpu and calculte the
	 * CPU usage time.
	 */
	final public int getCPUUsage() throws Exception {
		return this.dataParser.getCPUUsage();
	}

	final public int getUserCPUUsage() throws Exception {
		return this.dataParser.getUserCPUUsage();
	}

	final public float getSystemCPUUsage() throws Exception {
		return this.dataParser.getSystemCPUUsage();
	}

	final public int getWaitCPUUsage() throws Exception {
		return this.dataParser.getWaitCPUUsage();
	}

	final public int get1MinLoadAverage() throws Exception {
		return this.dataParser.get1MinLoadAverage();
	}

	final public int get5MinLoadAverage() throws Exception {
		return this.dataParser.get5MinLoadAverage();
	}

	final public int get15MinLoadAverage() throws Exception {
		return this.dataParser.get15MinLoadAverage();
	}

	/**
	 * Get Physical Memory info of the remote machine Execute free command with
	 * -o option Parse the returned String. Extract the total, used and free
	 * physical memory
	 */
	final public MemoryInfo getPhysicalMemoryInfo() throws Exception {
		return this.dataParser.getPhysicalMemoryInfo();
	}

	/**
	 * Get Virtual Memory info of the remote machine Execute free command with
	 * -o option Parse the returned String. Extract the total, used and free
	 * virtual memory
	 */
	final public MemoryInfo getVirtualMemoryInfo() throws Exception {
		return this.dataParser.getVirtualMemoryInfo();
	}

	/**
	 * Get Process info of the remote machine Execute ps command with -eo
	 * pid,pcpu,rss,comm option Parse the returned String. Extract the process
	 * info.
	 */
	public ProcessInfo[] getProcessInfo() throws Exception {
		return this.dataParser.getProcessInfo();
	}

	/**
	 * Get count of Processes running on the remote machine.
	 */
	public int getProcessCount() throws Exception {
		return this.dataParser.getProcessCount();
	}

	/**
	 * Get Network info of the remote machine Execute netstat command with -i
	 * option Parse the returned String. Extract the network info.
	 */
	protected abstract NetworkInfo[] getNetworkData() throws Exception;

	public final NetworkInfo[] getNetworkInfo() throws Exception {
		final NetworkInfo[] networkInfoArr = this.getNetworkData();
		if (networkInfoArr != null) {
			// networkInfo LinkList is null
			if (this.llNetworkInfo == null) {
				this.llNetworkInfo = new LinkedList();
			}

			for (int i = 0; i < networkInfoArr.length; ++i) {
				// if the NetworkInfo is not being monitored for the first time
				// then find the the object in the LinkedList
				boolean bFound = false;
				if (!this.bFirstTime) {
					NetworkInfo netInfo;
					final int iSize = this.llNetworkInfo.size();

					// search for the NetworkInfo Object in the LinkedList
					for (int j = 0; j < iSize; j++) {
						netInfo = (NetworkInfo) this.llNetworkInfo.get(j);

						// if found then break
						if ((netInfo.getInterfaceId() == networkInfoArr[i].getInterfaceId())
								&& (netInfo.getName()).equals(networkInfoArr[i].getName())) {
							if (networkInfoArr[i] instanceof UnixNetworkInfo && netInfo instanceof UnixNetworkInfo) {
								// set the totalPackets recd and sent in the
								// object
								((UnixNetworkInfo) netInfo).setTotalRecdPackets(
										((UnixNetworkInfo) networkInfoArr[i]).getTotalRecdPackets());
								((UnixNetworkInfo) netInfo).setTotalSentPackets(
										((UnixNetworkInfo) networkInfoArr[i]).getTotalSentPackets());
							} else {
								netInfo.setRecdPacketsPerSec(networkInfoArr[i].getRecdPacketsPerSec());
								netInfo.setSentPacketsPerSec(networkInfoArr[i].getSentPacketsPerSec());
							}

							bFound = true;
							break;
						}
					}
				}
				if (!bFound) {
					this.llNetworkInfo.add(networkInfoArr[i]);
				}
			}
			this.bFirstTime = false;
		}
		if (this.llNetworkInfo != null) {
			return (NetworkInfo[]) this.llNetworkInfo.toArray(new NetworkInfo[this.llNetworkInfo.size()]);
		}
		return null;
	}

	protected abstract AbstractProtocolWrapper getSearchLogWrapper();

	public final long getLastLogFileLineNumber(String logFile) throws Exception {
		long newLastLineFound = -1;
		AbstractProtocolWrapper wrapper = getSearchLogWrapper();
		if (wrapper != null) {
			// Fire "wc -l fileName" command to get the line numbers in the file
			String result = wrapper.execute("wc -l " + logFile, true);
			if (result != null) {
				result = result.trim();
				int spaceIndex = result.indexOf(' ');
				if (spaceIndex != -1) {
					try {
						newLastLineFound = Long.parseLong(result.substring(0, spaceIndex));
					} catch (Exception ex) {
						// do nothing
					}
				}
			}
		}
		return newLastLineFound;
	}

	/**
	 * Get Disk info of the remote machine Execute iostat command with -x -d
	 * option And parse the returned string. Extract the disk info.
	 */
	abstract public DiskInfo[] getPhysicalDiskInfo() throws Exception;

	/**
	 * method getCPUUsage
	 * 
	 * @return
	 * @throws Exception
	 */
	abstract public void collectData() throws Exception;

	/**
	 * Get Disk space info of the filesystems on remote machine Execute df
	 * command with -k option and parse the returned string. Extract the disk
	 * space info.
	 * 
	 * @return
	 * @throws Exception
	 */
	abstract public DriveInfo[] getDiskSpaceInfo() throws Exception;

	public NFSStat getNFSStats() throws Exception {
		// Default Implementation
		return null;
	}

	protected void assertCommandNotFound(String result, String first, String command) throws CommandNotFoundException {
		if ((result == null) || /* (result.trim().length() == 0) || */(result.indexOf(this.COMMAND_NOT_FOUND) != -1)) {
			throw new CommandNotFoundException(first, command);
		}
	}

	protected void setFirewallDetails(AbstractProtocolWrapper[] wrappers) {
		for (int i = 0; i < wrappers.length; i++) {
			if (wrappers[i] != null) {
				wrappers[i].setFirewallUserName(this.firewallUserName);
				wrappers[i].setFirewallPassword(this.firewallPassword);
				wrappers[i].setSuperUserName(this.superUserName);
				wrappers[i].setSuperUserPassword(this.superUserPassword);
			}
		}
	}

}
