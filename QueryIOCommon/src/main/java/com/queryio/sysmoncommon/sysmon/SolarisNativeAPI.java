/*
 * @(#)  SolarisNativeAPI.java
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

import com.queryio.common.IOSProtocolConstants;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.LogInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.protocol.AbstractProtocolWrapper;
import com.queryio.sysmoncommon.sysmon.protocol.SSHWrapper;
import com.queryio.sysmoncommon.sysmon.protocol.ShellWrapper;
import com.queryio.sysmoncommon.sysmon.protocol.TelnetWrapper;

/**
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * 
 * @author Exceed Consultancy Services
 */
class SolarisNativeAPI extends AbstractUnixNativeAPI {
	private final String TOPCOMMAND = "top -b -d 1";
	private final String PRSTATCOMMAND = "prstat -ac -n 25 1 1";
	private final String VMSTATCOMMAND = "vmstat";
	private final String NETWORKINFOCOMMAND = "netstat -i";
	private final String DISKINFOCOMMAND = "iostat -x -d 1";
	private final String DISKSPACEINFOCOMMAND = "df -k";
	// private final String PROCESSINFOCOMMAND = "ps -eo pid,pcpu,rss,comm";
	/*
	 * %d = refresh interval
	 * 
	 * uptime load avg dladm show-link -s network zpool iostat %d disk vmstat %d
	 * memory iostat %d cpu
	 * 
	 */

	private AbstractProtocolWrapper wrapperForNetwork;
	private AbstractProtocolWrapper wrapperForDiskSpace;
	private AbstractProtocolWrapper wrapperForTop;
	private AbstractProtocolWrapper wrapperForDisk;

	private boolean bFirstTimeDiskInfo = true;

	// public SolarisNativeAPI(final String hostName, final String user, final
	// String pwd, final int port,
	// final int iProtocol)
	// {
	// this(hostName, user, pwd, port, iProtocol, null, null, null, null, null);
	// }
	public SolarisNativeAPI(final String hostName, final String user, final String pwd, final String key,
			final int authMethod, final int port, final int iProtocol, final ProcessFilter processFilter,
			final String fUserName, final String fPassword, final String superUserName, final String superUserPassword,
			final boolean monitorProcess, final int refreshInterval) {
		super(hostName, user, pwd, key, authMethod, port, iProtocol, processFilter, fUserName, fPassword, superUserName,
				superUserPassword, monitorProcess, refreshInterval);
		try {
			if (hostName.equals("127.0.0.1") || hostName.equals("localhost")) {
				this.wrapperForTop = new ShellWrapper(IOSProtocolConstants.SOLARIS);
				if (processFilter == null) {
					this.wrapperForDisk = new ShellWrapper(IOSProtocolConstants.SOLARIS);
					this.wrapperForNetwork = new ShellWrapper(IOSProtocolConstants.SOLARIS);
					this.wrapperForDiskSpace = new ShellWrapper(IOSProtocolConstants.SOLARIS);
				}
			} else if (iProtocol == IOSProtocolConstants.TELNET) {
				this.wrapperForTop = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.SOLARIS);
				if (processFilter == null) {
					this.wrapperForDisk = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.SOLARIS);
					this.wrapperForNetwork = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.SOLARIS);
					this.wrapperForDiskSpace = new TelnetWrapper(hostName, user, pwd, port,
							IOSProtocolConstants.SOLARIS);
				}
			} else if (iProtocol == IOSProtocolConstants.SSH) {
				this.wrapperForTop = new SSHWrapper(hostName, user, pwd, key, authMethod, port,
						IOSProtocolConstants.SOLARIS);
				if (processFilter == null) {
					this.wrapperForDisk = new SSHWrapper(hostName, user, pwd, key, authMethod, port,
							IOSProtocolConstants.SOLARIS);
					this.wrapperForNetwork = new SSHWrapper(hostName, user, pwd, key, authMethod, port,
							IOSProtocolConstants.SOLARIS);
					this.wrapperForDiskSpace = new SSHWrapper(hostName, user, pwd, key, authMethod, port,
							IOSProtocolConstants.SOLARIS);
				}
			}
			setFirewallDetails(new AbstractProtocolWrapper[] { this.wrapperForTop, this.wrapperForDisk,
					this.wrapperForNetwork, this.wrapperForDiskSpace });
		} catch (final Exception ex) {
			// If the machine doesn't have telnet installed on it then it gives
			// exception while creating TelnetClient
			// So supress this exception
		}
	}

	/**
	 * @param host
	 * @param port
	 * @return
	 */
	public boolean ping() throws Exception {
		if (processFilter != null) {
			if (!this.wrapperForTop.isConnected()) {
				this.wrapperForTop.connect();
				if (!this.wrapperForTop.login()) {
					return false;
				}
			}
			if (this.dataParser == null) {
				this.dataParser = DataParserFactory.getDataParser(IOSProtocolConstants.SOLARIS_OLD, null);
			}
			return true;
		}

		if (this.wrapperForNetwork == null) {
			return false;
		}
		if (!this.wrapperForNetwork.isConnected()) {
			this.wrapperForNetwork.connect();
			if (!this.wrapperForNetwork.login()) {
				return false;
			}
		}
		if (!this.wrapperForDiskSpace.isConnected()) {
			this.wrapperForDiskSpace.connect();
			if (!this.wrapperForDiskSpace.login()) {
				return false;
			}
		}
		if (!this.wrapperForTop.isConnected()) {
			this.wrapperForTop.connect();
			if (!this.wrapperForTop.login()) {
				return false;
			}
		}
		if (!this.wrapperForDisk.isConnected()) {
			this.wrapperForDisk.connect();
			if (this.wrapperForDisk.login()) {
				if (this.dataParser == null) {
					this.dataParser = DataParserFactory.getDataParser(IOSProtocolConstants.SOLARIS_OLD, null);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Disconnect from remote machine
	 */
	public void disconnect() throws IOException {
		if (this.wrapperForNetwork != null) {
			this.wrapperForNetwork.disconnect();
		}
		if (this.wrapperForDiskSpace != null) {
			this.wrapperForDiskSpace.disconnect();
		}
		if (this.wrapperForTop != null) {
			this.wrapperForTop.disconnect();
		}
		if (this.wrapperForDisk != null) {
			this.wrapperForDisk.disconnect();
		}
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public DiskInfo[] getPhysicalDiskInfo() throws Exception {
		String result = null;
		if (this.bFirstTimeDiskInfo) {
			if (this.wrapperForDisk != null) {
				this.wrapperForDisk.send(this.DISKINFOCOMMAND);
			}
			this.bFirstTimeDiskInfo = false;
		}
		if (this.wrapperForDisk != null) {
			result = this.wrapperForDisk.getIostatOutputForSolaris();
		}
		assertCommandNotFound(result, "Disk Usage", this.DISKINFOCOMMAND);
		this.dataParser.parseIOStatCommand(result);
		return this.dataParser.getPhysicalDiskInfo();
	}

	/**
	 * @throws Exception
	 */
	public void collectData() throws Exception {
		String result = null;
		if (this.wrapperForTop != null) {
			if (processFilter != null && processFilter.pid != -1) {
				result = this.wrapperForTop.execute(this.TOPCOMMAND + " -p " + processFilter.pid, true);
			} else {
				result = this.wrapperForTop.execute(this.TOPCOMMAND, true);
			}
		}
		// If we get the line command not found prompt error to user
		if ((result == null) || result.trim().length() == 0 || (result.indexOf(this.COMMAND_NOT_FOUND) != -1)) {
			// try fetching data using prstat command
			if (this.wrapperForTop != null) {
				if (processFilter != null && processFilter.pid != -1) {
					result = this.wrapperForTop.execute(this.PRSTATCOMMAND + " -p " + processFilter.pid, true);
				} else {
					result = this.wrapperForTop.execute(this.PRSTATCOMMAND, true);
				}
			}
			assertCommandNotFound(result, "CPU Usage", this.TOPCOMMAND + '/' + this.PRSTATCOMMAND);
			this.dataParser.parsePrstatCommand(result);
			// get memory info from vmstat command
			if (this.wrapperForTop != null) {
				result = this.wrapperForTop.execute(this.VMSTATCOMMAND, true);
			}
			assertCommandNotFound(result, "Memory Usage", this.VMSTATCOMMAND);
			this.dataParser.parseVmstatCommand(result);
			return;
		}
		this.dataParser.parseTopCommand(result);
		// IF PRSTATCOMMAND command has not been executed earlier and CPU usage
		// we are getting is ZERO then try
		// once using VMstate command and see if its making any difference.
		if (this.getCPUUsage() == 0) {
			if (this.wrapperForTop != null) {
				result = this.wrapperForTop.execute(this.PRSTATCOMMAND, true);
			}
			assertCommandNotFound(result, "CPU Usage", this.PRSTATCOMMAND);
			this.dataParser.parsePrstatCommand(result);
		}
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public NetworkInfo[] getNetworkData() throws Exception {
		String result = null;
		if (this.wrapperForNetwork != null) {
			result = this.wrapperForNetwork.execute(this.NETWORKINFOCOMMAND, true);
		}
		assertCommandNotFound(result, "Network Usage", this.NETWORKINFOCOMMAND);
		this.dataParser.parseNetStatCommand(result);
		return this.dataParser.getNetworkInfo();
	}

	/**
	 * Get Disk space info of the filesystems on remote machine Execute df
	 * command with -k option and parse the returned string. Extract the disk
	 * space info.
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

	protected AbstractProtocolWrapper getSearchLogWrapper() {
		return this.wrapperForTop;
	}

	/**
	 * @see com.queryio.sysmoncommon.sysmon.IUnixNativeAPI#searchInLogFile(java.lang.String,
	 *      java.lang.String, long)
	 */
	public LogInfo searchInLogFile(String logFile, String[] search, long lastLine) throws Exception {
		if (this.wrapperForTop != null) {
			return this.wrapperForTop.searchInLogFile(logFile, "tail -n +", search, lastLine);
		}
		return null;
		/*
		 * String result = null; result = this.wrapperForTop.execute(, false);
		 * assertCommandNotFound(result, "Log File Search", this.LOGFILECOMMAND
		 * + lastLine + " " + logFile); LogInfo logInfo = new LogInfo(); if
		 * (result != null) // output will be null if this method gets called
		 * after disconnect { String sLine = null; final StringTokenizer st =
		 * new StringTokenizer(result, NEW_LINE_TOKENS); while
		 * (st.hasMoreTokens()) { sLine = st.nextToken(); if
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

	// public static void main(String[] args) throws Exception
	// {
	// SolarisNativeAPI solMachine = new SolarisNativeAPI("ras", "manoj");
	// try
	// {
	// solMachine.ping("192.168.2.17", 23);
	// for(int i = 0; i < 2; i++)
	// {
	// solMachine.collectData();
	// System.out.println("CPU Usage " + solMachine.getCPUUsage());
	// System.out.println("Physical Memory Free " +
	// solMachine.getPhysicalMemoryInfo().getAvailable());
	// System.out.println("Virtual Memory Free " +
	// solMachine.getVirtualMemoryInfo().getAvailable());
	//
	// UnixNetworkInfo[] networkInfo = solMachine.getNetworkInfo();
	// for(int i1 = 0; i1 < networkInfo.length; i1++)
	// {
	// System.out.println(networkInfo[i1].getName() + " read=" +
	// networkInfo[i1].getRecdPacketsPerSec() + ", write:" +
	// networkInfo[i1].getSentPacketsPerSec());
	// }
	// System.out.println("collecting disk activity info");
	// solMachine.getPhysicalDiskInfo();
	// DiskInfo[] info = solMachine.getPhysicalDiskInfo();
	// for(int i1 = 0; i1 < info.length; i1++)
	// {
	// System.out.println(info[i1].getName() + " read = " +
	// info[i1].getReadsPerSec() + ", write = " + info[i1].getWritesPerSec());
	// }
	//
	// System.out.println("collecting disk space info");
	// DriveInfo[] drvInfo = solMachine.getDiskSpaceInfo();
	// for(int i1 = 0; i1 < drvInfo.length; i1++)
	// {
	// System.out.println(drvInfo[i1].getName() + " used=" +
	// drvInfo[i1].getUsedSpace() + ", total:" + drvInfo[i1].getTotalSpace());
	// }
	//
	// System.out.println("collecting Process Info");
	// ProcessInfo[] processes = solMachine.getProcessInfo();
	// for(int i1 = 0; i1 < processes.length; i1++)
	// {
	// System.out.println(processes[i1].getName() + " Memory " +
	// processes[i1].getMemoryUsage());
	// }
	// }
	// solMachine.disconnect();
	// }
	// catch(Exception e)
	// {
	// e.printStackTrace();
	// }
	// }
}