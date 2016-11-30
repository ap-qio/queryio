package com.queryio.sysmoncommon.sysmon;

import com.queryio.common.IOSProtocolConstants;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.LogInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.protocol.AbstractProtocolWrapper;
import com.queryio.sysmoncommon.sysmon.protocol.SSHWrapper;
import com.queryio.sysmoncommon.sysmon.protocol.ShellWrapper;
import com.queryio.sysmoncommon.sysmon.protocol.TelnetWrapper;

class ZFSNativeAPI extends AbstractUnixNativeAPI {
	private final String NETWORKINFOCOMMAND = "dladm show-link -s";
	private final String LOAD_AVG_COMMAND = "uptime";
	private final String IOSTAT_COMMAND;
	private final String VMSTAT_COMMAND;
	private final String DISK_COMMAND;
	/*
	 * %d = refresh interval uptime load avg dladm show-link -s network zpool
	 * iostat %d disk vmstat %d memory iostat %d cpu
	 */
	private AbstractProtocolWrapper wrapperForLoadAvg;
	private AbstractProtocolWrapper wrapperForNetwork;
	private AbstractProtocolWrapper wrapperForDisk;
	private AbstractProtocolWrapper wrapperForMemory;
	private AbstractProtocolWrapper wrapperForCPU;
	private AbstractProtocolWrapper[] wrappers;

	private boolean bFirstTimeCollectData = true;

	public ZFSNativeAPI(String hostName, String user, String pwd, String key, int authMethod, int port, int iProtocol,
			ProcessFilter processFilter, String fUserName, String fPassword, String superUserName,
			String superUserPassword, boolean monitorProcess, int interval) {
		super(hostName, user, pwd, key, authMethod, port, iProtocol, processFilter, fUserName, fPassword, superUserName,
				superUserPassword, monitorProcess, interval);
		this.IOSTAT_COMMAND = "iostat " + this.refreshInterval;
		this.VMSTAT_COMMAND = "vmstat " + this.refreshInterval;
		this.DISK_COMMAND = "zpool iostat " + this.refreshInterval;
		try {
			wrappers = new AbstractProtocolWrapper[5];
			if (hostName.equals("127.0.0.1") || hostName.equals("localhost")) {
				for (int i = 0; i < wrappers.length; i++) {
					wrappers[i] = new ShellWrapper(IOSProtocolConstants.SOLARIS);
				}
			} else if (iProtocol == IOSProtocolConstants.TELNET) {
				for (int i = 0; i < wrappers.length; i++) {
					wrappers[i] = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.SOLARIS);
				}
			} else if (iProtocol == IOSProtocolConstants.SSH) {
				for (int i = 0; i < wrappers.length; i++) {
					wrappers[i] = new SSHWrapper(hostName, user, pwd, key, authMethod, port,
							IOSProtocolConstants.SOLARIS);
				}
			}
			setFirewallDetails(wrappers);
			wrapperForLoadAvg = wrappers[0];
			wrapperForNetwork = wrappers[1];
			wrapperForDisk = wrappers[2];
			wrapperForMemory = wrappers[3];
			wrapperForCPU = wrappers[4];
		} catch (final Exception ex) {
			// If the machine doesn't have telnet installed on it then it gives
			// exception while creating TelnetClient
			// So supress this exception
		}

	}

	public boolean ping() throws Exception {
		for (int i = 0; i < wrappers.length; i++) {
			if (!this.wrappers[i].isConnected()) {
				this.wrappers[i].connect();
				if (!this.wrappers[i].login()) {
					return false;
				}
			}
		}
		if (this.dataParser == null) {
			this.dataParser = DataParserFactory.getDataParser(IOSProtocolConstants.SOLARIS, null);
		}
		return true;
	}

	public void disconnect() throws Exception {
		for (int i = 0; i < wrappers.length; i++) {
			this.wrappers[i].disconnect();
		}
	}

	protected AbstractProtocolWrapper getSearchLogWrapper() {
		return this.wrapperForLoadAvg;
	}

	public LogInfo searchInLogFile(String logFile, String[] search, long lastLine) throws Exception {
		if (this.wrapperForLoadAvg != null) {
			return this.wrapperForLoadAvg.searchInLogFile(logFile, "tail -n +", search, lastLine);
		}
		return null;
	}

	public void executeScript(String scriptFile) throws Exception {
		if (this.wrapperForLoadAvg != null) {
			this.wrapperForLoadAvg.execute(scriptFile, true);
		}
	}

	public void collectData() throws Exception {
		String result;
		// collect Load Avg
		result = this.wrapperForLoadAvg.execute(this.LOAD_AVG_COMMAND, true);
		if (result != null) {
			this.dataParser.parseLoadAverages(result);
		}

		if (bFirstTimeCollectData) {
			this.wrapperForDisk.send(this.DISK_COMMAND);
			this.wrapperForCPU.send(this.IOSTAT_COMMAND);
			this.wrapperForMemory.send(this.VMSTAT_COMMAND);
		}
		// collect CPU
		result = this.wrapperForCPU.waitfor(AbstractProtocolWrapper.APPPERFECT_PROMPT, true);
		this.dataParser.parseIOStatCommand(result);

		// collect Physical && Virtual Memory
		result = this.wrapperForMemory.waitfor(AbstractProtocolWrapper.APPPERFECT_PROMPT, true);
		this.dataParser.parseVmstatCommand(result);

		// collect Disk Space & Disk I/O
		result = this.wrapperForDisk.waitfor(AbstractProtocolWrapper.APPPERFECT_PROMPT, true);
		if (result == null || result.trim().length() == 0) {
			this.wrapperForDisk.send(this.DISK_COMMAND);
			result = this.wrapperForDisk.waitfor(AbstractProtocolWrapper.APPPERFECT_PROMPT, true);
		}
		this.dataParser.parseDFCommand(result);

		bFirstTimeCollectData = false;
	}

	protected NetworkInfo[] getNetworkData() throws Exception {
		String result = null;
		if (this.wrapperForNetwork != null) {
			result = this.wrapperForNetwork.execute(this.NETWORKINFOCOMMAND, true);
		}
		assertCommandNotFound(result, "Network Usage", this.NETWORKINFOCOMMAND);
		this.dataParser.parseNetStatCommand(result);
		return this.dataParser.getNetworkInfo();
	}

	public DiskInfo[] getPhysicalDiskInfo() throws Exception {
		return this.dataParser.getPhysicalDiskInfo();
	}

	public DriveInfo[] getDiskSpaceInfo() throws Exception {
		return this.dataParser.getDiskSpaceInfo();
	}

}
