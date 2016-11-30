/*
 * @(#)  LinuxNativeAPI.java
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
import com.queryio.common.util.AppLogger;
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
 */
class LinuxNativeAPI extends AbstractUnixNativeAPI {
	// private final String PROCESSCOMMAND = "ps -eo
	// pid,etime,time,vsz,thcount,user,command";
	private final String PROCESSCOMMANDOWNER = "ps -o pid,etime,time,vsz,thcount,user,command -u ";
	private final String PROCESSCOMMANDPID = "ps -o pid,etime,time,vsz,thcount,user,command -p ";

	// private final String TOPCOMMAND = "top -b -d 1 -p 0";
	// private final String TOPWITHPROCESSCOMMAND = "top -b -d 1";
	// private final String DISKINFOCOMMAND = "iostat -k -d 1"; // "iostat -k -d
	// 1 "; //
	private final String NETWORKINFOCOMMAND = "netstat -i -e";
	private final String DISKSPACEINFOCOMMAND = "df -k";
	private final String VERSIONINFOCOMMAND = "cat /proc/version";
	private final String TOPVERSIONINFO = "top -v";

	private AbstractProtocolWrapper wrapperForNetwork;
	private AbstractProtocolWrapper wrapperForDiskSpace;
	private AbstractProtocolWrapper wrapperForTop;
	private AbstractProtocolWrapper wrapperForDisk;

	private String sVersion;
	private String sTOPVersion;

	private boolean bFirstTimeTop = true;
	private boolean bFirstTimeDiskInfo = true;

	private String iostatDisks;
	// public LinuxNativeAPI(final String hostName, final String user, final
	// String pwd, final int port, final int iProtocol)
	// {
	// this(hostName, user, pwd, port, iProtocol, null, null, null, null, null);
	// }

	public LinuxNativeAPI(final String hostName, final String user, final String pwd, final String key,
			final int authMethod, final int port, final int iProtocol, final ProcessFilter processFilter,
			final String fUserName, final String fPassword, final String superUserName, final String superUserPassword,
			final boolean monitorProcess, final String ioDisks, final int refreshInterval) {
		super(hostName, user, pwd, key, authMethod, port, iProtocol, processFilter, fUserName, fPassword, superUserName,
				superUserPassword, monitorProcess, refreshInterval);
		this.iostatDisks = ioDisks;
		try {
			if (hostName.equals("127.0.0.1") || hostName.equals("localhost")) {
				this.wrapperForTop = new ShellWrapper(IOSProtocolConstants.LINUX);
				if (processFilter == null) {
					this.wrapperForDisk = new ShellWrapper(IOSProtocolConstants.LINUX);
					this.wrapperForNetwork = new ShellWrapper(IOSProtocolConstants.LINUX);
					this.wrapperForDiskSpace = new ShellWrapper(IOSProtocolConstants.LINUX);
				}
			} else if (iProtocol == IOSProtocolConstants.TELNET) {
				this.wrapperForTop = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.LINUX);
				if (processFilter == null) {
					this.wrapperForDisk = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.LINUX);
					this.wrapperForNetwork = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.LINUX);
					this.wrapperForDiskSpace = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.LINUX);
				}
			} else if (iProtocol == IOSProtocolConstants.SSH) {
				this.wrapperForTop = new SSHWrapper(hostName, user, pwd, key, authMethod, port,
						IOSProtocolConstants.LINUX);
				if (processFilter == null) {
					this.wrapperForDisk = new SSHWrapper(hostName, user, pwd, key, authMethod, port,
							IOSProtocolConstants.LINUX);
					this.wrapperForNetwork = new SSHWrapper(hostName, user, pwd, key, authMethod, port,
							IOSProtocolConstants.LINUX);
					this.wrapperForDiskSpace = new SSHWrapper(hostName, user, pwd, key, authMethod, port,
							IOSProtocolConstants.LINUX);
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
	 * method ping Test whether remote machine is reachable or not
	 * 
	 * @param host
	 * @param port
	 * @return
	 * @throws Exception
	 */
	public boolean ping() throws Exception {
		if (processFilter != null) {
			if (!this.wrapperForTop.isConnected()) {
				this.wrapperForTop.connect();
				if (!this.wrapperForTop.login()) {
					return false;
				} else if (this.dataParser == null) {
					this.sVersion = this.wrapperForTop.execute(this.VERSIONINFOCOMMAND, true);
					this.sTOPVersion = this.wrapperForTop.execute(this.TOPVERSIONINFO, true);
					this.dataParser = DataParserFactory.getDataParser(IOSProtocolConstants.LINUX, this.sVersion,
							this.sTOPVersion);
					this.dataParser.setMonitorProcesses(monitorProcesses);
				}
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
					this.sVersion = this.wrapperForNetwork.execute(this.VERSIONINFOCOMMAND, false);
					this.sTOPVersion = this.wrapperForNetwork.execute(this.TOPVERSIONINFO, false);
					this.dataParser = DataParserFactory.getDataParser(IOSProtocolConstants.LINUX, this.sVersion,
							this.sTOPVersion);
					this.dataParser.setMonitorProcesses(monitorProcesses);
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
	 * Get Disk info of the remote machine Execute iostat command with -x -d
	 * option And parse the returned string. Extract the disk info.
	 */
	public DiskInfo[] getPhysicalDiskInfo() throws Exception {
		String command = null;
		String result = null;
		if (this.bFirstTimeDiskInfo) {
			if (this.wrapperForDisk != null) {
				command = this.iostatDisks == null ? "iostat -k -d " + this.refreshInterval
						: "iostat -k -d " + this.refreshInterval + " " + this.iostatDisks;
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug(command);
				this.wrapperForDisk.execute(command, true);
			}
			this.bFirstTimeDiskInfo = false;
		}
		if (this.wrapperForDisk != null) {
			result = this.wrapperForDisk.getIostatOutputForLinux(false);
		}
		assertCommandNotFound(result, "Disk Usage", command);
		this.dataParser.parseIOStatCommand(result);
		return this.dataParser.getPhysicalDiskInfo();
	}

	private void collectProcessFilteredData() throws Exception {
		String result;
		String commandUsed;
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
			// Check if result contains PID if not then try fetching result
			// again
			if (result.indexOf("PID") == -1 || result.indexOf("pid") == -1) {
				result = this.wrapperForTop.execute(commandUsed, true);
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
		String command = null;
		String result = null;
		if (this.bFirstTimeTop) {
			if (this.wrapperForTop != null) {
				if (processFilter != null && processFilter.pid != -1) {
					command = "top -b -d " + this.refreshInterval + " -p " + processFilter.pid;
					this.wrapperForTop.execute(command, true);
					// this.wrapperForTop.execute(this.TOPWITHPROCESSCOMMAND + "
					// -p " + processFilter.pid, false);
				} else if (monitorProcesses) {
					command = "top -b -d " + this.refreshInterval;
					this.wrapperForTop.execute(command, true);
					// this.wrapperForTop.execute(this.TOPWITHPROCESSCOMMAND,
					// false);
				} else {
					command = "top -b -d " + this.refreshInterval + " -p 0";
					this.wrapperForTop.execute(command, true);
					// this.wrapperForTop.execute(this.TOPCOMMAND, false);
				}
			}
			this.bFirstTimeTop = false;
		}
		if (this.wrapperForTop != null) {
			if (processFilter != null) {
				result = this.wrapperForTop.getTopOutputForLinux();
				int i = 3;
				while (i > 0) {
					// Check if result contains PID if not then try fetching
					// result again
					if (result.indexOf(processFilter.pid) == -1) {
						result = this.wrapperForTop.getTopOutputForLinux();
						try {
							Thread.sleep(500);
						} catch (Exception e) {
							// DO NOTHING
						}
					} else {
						i = 0;
					}
					i--;
				}
			} else {
				result = this.wrapperForTop.getTopOutputForLinux();
				// Check if result contains PID if not then try fetching result
				// again
				if (result.indexOf("PID") == -1 || result.indexOf("pid") == -1) {
					result = this.wrapperForTop.getTopOutputForLinux();
				}
			}
		}
		// If we get the line command not found prompt error to user
		assertCommandNotFound(result, "CPU Usage", command);
		this.dataParser.parseTopCommand(result);
	}

	/**
	 * Get Network info of the remote machine Execute netstat command with -i
	 * option Parse the returned String. Extract the network info.
	 */
	protected NetworkInfo[] getNetworkData() throws Exception {
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

	protected AbstractProtocolWrapper getSearchLogWrapper() {
		return this.wrapperForTop;
	}

	/**
	 * @see com.queryio.sysmoncommon.sysmon.IUnixNativeAPI#searchInLogFile(java.lang.String,
	 *      java.lang.String, long)
	 */
	public LogInfo searchInLogFile(String logFile, String[] search, long lastLine) throws Exception {
		if (this.wrapperForTop != null) {
			return this.wrapperForTop.searchInLogFile(logFile, "tail --lines=+", search, lastLine);
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
}