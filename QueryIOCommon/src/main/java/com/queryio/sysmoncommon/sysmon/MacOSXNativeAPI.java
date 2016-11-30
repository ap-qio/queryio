/*
 * @(#)  MacOSXNativeAPI.java
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
package com.queryio.sysmoncommon.sysmon;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import com.queryio.common.IOSProtocolConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.IntHashMap;
import com.queryio.sysmoncommon.sysmon.dstruct.AIXProcessInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.LogInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NFSStat;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.UnixNetworkInfo;
import com.queryio.sysmoncommon.sysmon.protocol.AbstractProtocolWrapper;
import com.queryio.sysmoncommon.sysmon.protocol.SSHWrapper;
import com.queryio.sysmoncommon.sysmon.protocol.ShellWrapper;
import com.queryio.sysmoncommon.sysmon.protocol.TelnetWrapper;

/**
 * Provide the class level Java documentation here. Define the purpose of class
 * and if possible where and how this class is being used.
 *
 * @author Exceed Consultancy Services
 * @version 1.0
 */
class MacOSXNativeAPI extends AbstractUnixNativeAPI {

	public static boolean bOS_10_6Version = false;

	private final String SYSTEMPROFILERCOMMAND = "system_profiler SPHardwareDataType";
	private final String TOPCOMMAND;
	private final String TOPNETWORKDISKCOMMAND;
	private final String TOPCOMMAND_10_6; // top -l 0 -pid xxx

	private final String DISKSPACEINFOCOMMAND = "df -k";

	private final String PROCESSCOMMANDOWNER = "ps -o pid,etime,time,vsz,user,command -u ";
	private final String PROCESSCOMMANDPID = "ps -o pid,etime,time,vsz,user,command -p ";

	private static final String NFS_CLIENT_STATS_CMD = "nfsstat -c";
	private static final String NFS_SERVER_STATS_CMD = "nfsstat -s";

	private final String OSVERSIONCOMMAND = "sw_vers -productVersion";
	private AbstractProtocolWrapper wrapperForDiskSpace;
	private AbstractProtocolWrapper wrapperForTop_NetworkDisk;
	private AbstractProtocolWrapper wrapperForTop;
	private AbstractProtocolWrapper wrapperForNFS;

	private String sVersion;
	boolean bFirstTime = true;
	private IntHashMap ihmProcessInfo;
	private int noOfCPUs = -1;

	// public MacOSXNativeAPI(final String hostName, final String user, final
	// String pwd, final int port,
	// final int iProtocol)
	// {
	// this(hostName, user, pwd, port, iProtocol, null, null, null, null, null);
	// }

	public MacOSXNativeAPI(final String hostName, final String user, final String pwd, final String key,
			final int authMethod, final int port, final int iProtocol, final ProcessFilter processFilter,
			final String fUserName, final String fPassword, final String superUserName, final String superUserPassword,
			final boolean monitorProcess, final int refreshInterval) {
		super(hostName, user, pwd, key, authMethod, port, iProtocol, processFilter, fUserName, fPassword, superUserName,
				superUserPassword, monitorProcess, refreshInterval);

		TOPCOMMAND = "top -s " + this.refreshInterval;
		TOPNETWORKDISKCOMMAND = "top -d -s " + this.refreshInterval;
		TOPCOMMAND_10_6 = "top -d -l 0 -s " + this.refreshInterval;
		try {
			if (this.monitorProcesses) {
				ihmProcessInfo = new IntHashMap();
			}
			if (hostName.equals("127.0.0.1") || hostName.equals("localhost")) {
				this.wrapperForTop = new ShellWrapper(IOSProtocolConstants.MACOSX);
				if (processFilter == null) {
					this.wrapperForTop_NetworkDisk = new ShellWrapper(IOSProtocolConstants.MACOSX);
					this.wrapperForDiskSpace = new ShellWrapper(IOSProtocolConstants.MACOSX);
					this.wrapperForNFS = new ShellWrapper(IOSProtocolConstants.MACOSX);
				}
			} else if (iProtocol == IOSProtocolConstants.TELNET) {
				this.wrapperForTop = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.MACOSX);
				if (processFilter == null) {
					this.wrapperForTop_NetworkDisk = new TelnetWrapper(hostName, user, pwd, port,
							IOSProtocolConstants.MACOSX);
					this.wrapperForDiskSpace = new TelnetWrapper(hostName, user, pwd, port,
							IOSProtocolConstants.MACOSX);
					this.wrapperForNFS = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.MACOSX);
				}
			} else if (iProtocol == IOSProtocolConstants.SSH) {
				this.wrapperForTop = new SSHWrapper(hostName, user, pwd, key, authMethod, port,
						IOSProtocolConstants.MACOSX);
				if (processFilter == null) {
					this.wrapperForTop_NetworkDisk = new SSHWrapper(hostName, user, pwd, key, authMethod, port,
							IOSProtocolConstants.MACOSX);
					this.wrapperForDiskSpace = new SSHWrapper(hostName, user, pwd, key, authMethod, port,
							IOSProtocolConstants.MACOSX);
					this.wrapperForNFS = new SSHWrapper(hostName, user, pwd, key, authMethod, port,
							IOSProtocolConstants.MACOSX);
				}
			}
			setFirewallDetails(new AbstractProtocolWrapper[] { this.wrapperForTop, this.wrapperForTop_NetworkDisk,
					this.wrapperForDiskSpace, this.wrapperForNFS });
		} catch (final Exception ex) {
			// If the machine doesn't have telnet installed on it then it gives
			// exception while creating TelnetClient
			// So suppress this exception
		}
	}

	/**
	 * Test whether remote machine is reachable or not
	 *
	 * @return
	 * @throws Exception
	 */
	public boolean ping() throws Exception {
		if (processFilter != null) {
			this.wrapperForTop.connect();
			if (!this.wrapperForTop.login()) {
				return false;
			} else if (this.dataParser == null) {
				this.sVersion = this.wrapperForTop.execute(this.OSVERSIONCOMMAND, true);
				this.dataParser = DataParserFactory.getDataParser(IOSProtocolConstants.MACOSX, this.sVersion);
				this.dataParser.setMonitorProcesses(monitorProcesses);
			}
			return true;
		}
		if (this.wrapperForDiskSpace == null) {
			return false;
		}
		this.wrapperForDiskSpace.connect();
		if (!this.wrapperForDiskSpace.login()) {
			return false;
		}
		if (this.wrapperForTop_NetworkDisk == null) {
			return false;
		}
		this.wrapperForTop_NetworkDisk.connect();
		if (!this.wrapperForTop_NetworkDisk.login()) {
			return false;
		}
		if (this.wrapperForNFS == null) {
			return false;
		}
		this.wrapperForNFS.connect();
		if (!this.wrapperForNFS.login()) {
			return false;
		}
		if (this.wrapperForTop == null) {
			return false;
		}
		this.wrapperForTop.connect();
		if (this.wrapperForTop.login()) {
			if (this.dataParser == null) {
				this.sVersion = this.wrapperForTop.execute(this.OSVERSIONCOMMAND, true);
				bOS_10_6Version = (this.sVersion.indexOf("10.6") != -1 || this.sVersion.indexOf("10.7") != -1
						|| this.sVersion.indexOf("10.8") != -1 || this.sVersion.indexOf("10.9") != -1
						|| this.sVersion.indexOf("10.10") != -1 || this.sVersion.indexOf("10.11") != -1);
				this.dataParser = DataParserFactory.getDataParser(IOSProtocolConstants.MACOSX, this.sVersion);
				this.dataParser.setMonitorProcesses(monitorProcesses);
			}
			return true;
		}
		return false;
	}

	/**
	 * method disconnect
	 *
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		if (this.wrapperForDiskSpace != null) {
			this.wrapperForDiskSpace.disconnect();
		}
		if (this.wrapperForTop_NetworkDisk != null) {
			this.wrapperForTop_NetworkDisk.disconnect();
		}
		if (this.wrapperForNFS != null) {
			this.wrapperForNFS.disconnect();
		}
		if (this.wrapperForTop != null) {
			this.wrapperForTop.disconnect();
		}
	}

	private void collectProcessFilteredData() throws Exception {
		String result;
		String commandUsed;

		if (this.noOfCPUs == -1) {
			result = this.wrapperForTop.execute(SYSTEMPROFILERCOMMAND, true);
			this.dataParser.parseVmstatCommand(result);
			this.noOfCPUs = this.dataParser.getNoOfProcessors();
		}

		if (this.processFilter.pid != -1) {
			commandUsed = this.PROCESSCOMMANDPID + this.processFilter.pid;
			result = this.wrapperForTop.execute(commandUsed, true);
			int i = 3;
			while (i > 0) {
				// Check if result contains PID if not then try fetching result
				// again
				if (result.indexOf(processFilter.pid) == -1) {
					result = this.wrapperForTop.execute(commandUsed, true);
				} else {
					i = 0;
				}
				i--;
			}
		} else {
			commandUsed = this.PROCESSCOMMANDOWNER + this.processFilter.owner;
			result = this.wrapperForTop.execute(commandUsed, true);
			int i = 3;
			while (i > 0) {
				// Check if result contains PID if not then try fetching result
				// again
				if (result.indexOf("PID") == -1 || result.indexOf("pid") == -1) {
					result = this.wrapperForTop.execute(commandUsed, true);
				} else {
					i = 0;
				}
				i--;
			}
		}
		assertCommandNotFound(result, "Process Usage", commandUsed);
		this.dataParser.parsePrstatCommand(result);
	}

	/**
	 * method getCPUUsage
	 *
	 * @return
	 * @throws Exception
	 */
	public void collectData() throws Exception {
		if (this.processFilter != null) {
			collectProcessFilteredData();
			return;
		}

		String result = null;
		String resultNetwork = null;
		if (this.bFirstTime) {
			if (bOS_10_6Version) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Version: bOS_10_6Version");
			} else {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Version: Not bOS_10_6Version");
			}

			if (processFilter != null && this.wrapperForTop != null) {
				if (bOS_10_6Version) {
					if (this.processFilter.pid != -1) {
						this.wrapperForTop.send(this.TOPCOMMAND_10_6 + " -pid " + this.processFilter.pid);
					} else {
						this.wrapperForTop.send(this.TOPCOMMAND_10_6);
					}
				} else {
					this.wrapperForTop.send(this.TOPCOMMAND);
				}
			} else if (this.wrapperForTop != null) {
				if (bOS_10_6Version) {
					if (!this.monitorProcesses && this.wrapperForTop instanceof SSHWrapper) {
						this.wrapperForTop.send(this.TOPCOMMAND_10_6 + " -pid 0");
					} else {
						this.wrapperForTop.send(this.TOPCOMMAND_10_6);
					}
				} else {
					this.wrapperForTop.send(this.TOPCOMMAND);
				}

				if (!bOS_10_6Version && this.wrapperForTop_NetworkDisk != null) {
					this.wrapperForTop_NetworkDisk.send(this.TOPNETWORKDISKCOMMAND);
				}
			}
			this.bFirstTime = false;
		}
		// Thread.sleep(2000);
		if (this.wrapperForTop != null) {
			result = this.wrapperForTop.getTopOutputforMac();
		}
		assertCommandNotFound(result, "Top Command", bOS_10_6Version ? this.TOPCOMMAND_10_6 : this.TOPCOMMAND);

		this.dataParser.parseTopMemCommand(result);
		// if(AppLogger.getLogger().isDebugEnabled())
		// AppLogger.getLogger().debug("12");

		if (!bOS_10_6Version) {
			if (this.wrapperForTop_NetworkDisk != null) {
				resultNetwork = this.wrapperForTop_NetworkDisk.getTopOutputforMac();
			}
			// If we get the line command not found prompt error to user
			assertCommandNotFound(resultNetwork, "Network and Disk Usage", this.TOPNETWORKDISKCOMMAND);
			this.dataParser.parseTopCommand(resultNetwork);
		}
	}

	/**
	 * Get Network info of the remote machine Execute netstat command with -i
	 * option Parse the returned String. Extract the network info.
	 */
	public NetworkInfo[] getNetworkData() {
		NetworkInfo[] networkInfo = this.dataParser.getNetworkInfo();
		if ((networkInfo == null) || (networkInfo.length == 0)) {
			networkInfo = new UnixNetworkInfo[] { new UnixNetworkInfo("Network", 0, 0, 0) };
		}
		return networkInfo;
	}

	/**
	 * Get Disk info of the remote machine Execute iostat command with -x -d
	 * option And parse the returned string. Extract the disk info.
	 */
	public DiskInfo[] getPhysicalDiskInfo() {
		DiskInfo[] diskInfo = this.dataParser.getPhysicalDiskInfo();
		if ((diskInfo == null) || (diskInfo.length == 0)) {
			diskInfo = new DiskInfo[] { new DiskInfo("Disk", 0, 0) };
		}
		return diskInfo;
	}

	/**
	 * Get Disk space info of the filesystems on remote machine Execute df
	 * command with -k option and parse the returned string. Extract the disk
	 * space info.
	 *
	 * @return
	 * @throws Exception
	 */
	public DriveInfo[] getDiskSpaceInfo() throws Exception {
		String result = null;
		if (this.wrapperForDiskSpace != null) {
			result = this.wrapperForDiskSpace.execute(this.DISKSPACEINFOCOMMAND, true);
		}
		assertCommandNotFound(result, "Logical Disk Info", this.DISKSPACEINFOCOMMAND);
		this.dataParser.parseDFCommand(result);
		return this.dataParser.getDiskSpaceInfo();
	}

	public ProcessInfo[] getProcessInfo() throws Exception {
		if (this.processFilter == null) {
			return super.getProcessInfo();
		}
		ProcessInfo[] aixProcessInfo = (ProcessInfo[]) this.dataParser.getProcessInfo();
		AIXProcessInfo storedProcessInfo;
		if (aixProcessInfo != null && aixProcessInfo.length > 0) {
			HashSet set = new HashSet();
			int[] ids = ihmProcessInfo.keys();
			for (int i = 0; i < ids.length; i++) {
				set.add(new Integer(ids[i]));
			}
			for (int i = 0; i < aixProcessInfo.length; i++) {
				storedProcessInfo = (AIXProcessInfo) ihmProcessInfo.get(aixProcessInfo[i].getProcessID());
				//// if(AppLogger.getLogger().isDebugEnabled())
				//// AppLogger.getLogger().debug("ID: " +
				//// aixProcessInfo[i].getProcessID() + " found: " +
				//// (storedProcessInfo != null));
				if (storedProcessInfo != null) {
					// existing process found, update its CPU usage
					set.remove(new Integer(aixProcessInfo[i].getProcessID()));

					storedProcessInfo.update(((AIXProcessInfo) aixProcessInfo[i]).getUpTime(),
							((AIXProcessInfo) aixProcessInfo[i]).getCPUTime(), this.noOfCPUs,
							aixProcessInfo[i].getMemoryUsage(), aixProcessInfo[i].getThreadCount());
				} else {
					// new process found, add it
					ihmProcessInfo.put(aixProcessInfo[i].getProcessID(), aixProcessInfo[i]);
				}
			}
			if (!set.isEmpty()) {
				// some processes have terminated, should be removed
				for (Iterator iterator = set.iterator(); iterator.hasNext();) {
					ihmProcessInfo.remove(((Integer) iterator.next()).intValue());
				}
			}
		} else {
			ihmProcessInfo.clear();
		}
		return (ProcessInfo[]) this.ihmProcessInfo.values().toArray(new ProcessInfo[this.ihmProcessInfo.size()]);
	}

	protected AbstractProtocolWrapper getSearchLogWrapper() {
		return this.wrapperForTop;
	}

	/**
	 * @see com.queryio.sysmoncommon.sysmon.IUnixNativeAPI#searchInLogFile(java.lang.String,
	 *      java.lang.String, long)
	 */
	public LogInfo searchInLogFile(String logFile, String[] search, long lastLine) throws Exception {
		if (this.wrapperForTop != null) {
			return this.wrapperForTop.searchInLogFile(logFile, "tail +", search, lastLine);
		}
		return null;
		/*
		 * String result = null; if (this.wrapperForTop != null) { result =
		 * this.wrapperForTop.execute(this.LOGFILECOMMAND + lastLine + " " +
		 * logFile, false); } assertCommandNotFound(result, "Log File Search",
		 * this.LOGFILECOMMAND + lastLine + " " + logFile); LogInfo logInfo =
		 * new LogInfo(); if (result != null) // output will be null if this
		 * method gets called after disconnect { String sLine = null; final
		 * StringTokenizer st = new StringTokenizer(result, NEW_LINE_TOKENS);
		 * while (st.hasMoreTokens()) { sLine = st.nextToken(); if
		 * (sLine.startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT) ||
		 * sLine.trim().equals("$")) // prompt) { break; }
		 * 
		 * if(sLine.indexOf(search) != -1) { logInfo.setFound(true);
		 * logInfo.setLineNum(lastLine); logInfo.setLine(sLine); } lastLine++; }
		 * } logInfo.setLastLineRead(lastLine); return logInfo;
		 */
	}

	/**
	 * @see com.queryio.sysmoncommon.sysmon.IUnixNativeAPI#executeScript(java.lang.String)
	 */
	public void executeScript(String scriptFile) throws Exception {
		if (this.wrapperForTop != null) {
			this.wrapperForTop.execute(scriptFile, true);
		}
	}

	public NFSStat getNFSStats() throws Exception {
		String clientResult = null;
		String serverResult = null;
		if (this.wrapperForNFS != null) {
			// Read client NFS Info: nfsstat -c
			clientResult = this.wrapperForNFS.execute(NFS_CLIENT_STATS_CMD, true);

			// Read server NFS Info: nfsstat -s
			serverResult = this.wrapperForNFS.execute(NFS_SERVER_STATS_CMD, true);
		}
		assertCommandNotFound(clientResult, "NFS Client Stats", NFS_CLIENT_STATS_CMD);
		assertCommandNotFound(serverResult, "NFS Server Stats", NFS_SERVER_STATS_CMD);
		this.dataParser.parseNSFStatCommand(clientResult, serverResult);
		return this.dataParser.getNFSStat();
	}

	// public static void main(String[] args) throws Exception
	// {
	// MacOSXNativeAPI macMachine = new
	// MacOSXNativeAPI(IProductConstants.EMPTY_STRING,
	// IProductConstants.EMPTY_STRING);
	// try
	// {
	// macMachine.ping("127.0.0.1", 23);
	// for(int i = 0; i < 2; i++)
	// {
	// macMachine.collectData();
	// // if(AppLogger.getLogger().isDebugEnabled())
	// AppLogger.getLogger().debug("CPU Usage " + macMachine.getCPUUsage());
	// // if(AppLogger.getLogger().isDebugEnabled())
	// AppLogger.getLogger().debug("Physical Memory Free " +
	// macMachine.getPhysicalMemoryInfo().getAvailable());
	// // if(AppLogger.getLogger().isDebugEnabled())
	// AppLogger.getLogger().debug("Virtual Memory Free " +
	// macMachine.getVirtualMemoryInfo().getAvailable());
	//
	// UnixNetworkInfo[] networkInfo = macMachine.getNetworkInfo();
	// for(int i1 = 0; i1 < networkInfo.length; i1++)
	// {
	// // if(AppLogger.getLogger().isDebugEnabled())
	// AppLogger.getLogger().debug(networkInfo[i1].getName() + " read=" +
	// networkInfo[i1].getRecdPacketsPerSec() + ", write:" +
	// networkInfo[i1].getSentPacketsPerSec());
	// }
	// // if(AppLogger.getLogger().isDebugEnabled())
	// AppLogger.getLogger().debug("\ncollecting disk activity info");
	// macMachine.getPhysicalDiskInfo();
	// DiskInfo[] info = macMachine.getPhysicalDiskInfo();
	// for(int i1 = 0; i1 < info.length; i1++)
	// {
	// // if(AppLogger.getLogger().isDebugEnabled())
	// AppLogger.getLogger().debug(info[i1].getName() + " read = " +
	// info[i1].getReadsPerSec() + ", write = " + info[i1].getWritesPerSec());
	// }
	//
	// // if(AppLogger.getLogger().isDebugEnabled())
	// AppLogger.getLogger().debug("\ncollecting disk space info");
	// DriveInfo[] drvInfo = macMachine.getDiskSpaceInfo();
	// for(int i1 = 0; i1 < drvInfo.length; i1++)
	// {
	// // if(AppLogger.getLogger().isDebugEnabled())
	// AppLogger.getLogger().debug(drvInfo[i1].getName() + " used=" +
	// drvInfo[i1].getUsedSpace() + ", total:" + drvInfo[i1].getTotalSpace());
	// }
	//
	// // if(AppLogger.getLogger().isDebugEnabled())
	// AppLogger.getLogger().debug("collecting Process Info");
	// ProcessInfo[] processes = macMachine.getProcessInfo();
	// for(int i1 = 0; i1 < processes.length; i1++)
	// {
	// // if(AppLogger.getLogger().isDebugEnabled())
	// AppLogger.getLogger().debug(processes[i1].getName() + " Memory " +
	// processes[i1].getMemoryUsage());
	// }
	// }
	// macMachine.disconnect();
	// }
	// catch(Exception e)
	// {
	// e.printStackTrace();
	// }
	// }

}