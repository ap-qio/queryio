package com.queryio.sysmoncommon.sysmon;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.acplt.oncrpc.OncRpcProtocols;

import com.queryio.common.IOSProtocolConstants;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.LogInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NFSStat;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;
import com.queryio.sysmoncommon.sysmon.protocol.RstatClient;
import com.queryio.sysmoncommon.sysmon.protocol.RstatConstants;

public class UnixMonitor implements IOSProtocolConstants {
	/* IP address of target machine */
	private String sTargetIPAddress;

	/* OS of target machine */
	private final int iTargetOS;

	/*
	 * Protocol to use to connect to remote machine For MacOSX, Linux and
	 * Solaris OS only
	 */
	private int iProtocol = UNDEFINED;

	/* Maintaines connection state if OS is MacOSX/Linux/Solaris */
	private boolean connectionInitialized = false;

	private String userName = null;
	private String password = null;
	private String privateKeyFile = null;
	private int authMethod;
	private int port = -1;
	private String firewallUserName = "";
	private String firewallPassword = "";
	private String superUserName = "";
	private String superUserPassword = "";

	private boolean monitorProcess = false;
	private String iostatDisks = null;
	private boolean monitorNFS = false;
	private int refreshInterval = 1;

	private ProcessFilter processFilter = null;

	private IUnixNativeAPI unixMachine;

	public UnixMonitor(final String targetIPAddress, final int os) throws UnknownHostException {
		this.iTargetOS = os;
		this.sTargetIPAddress = targetIPAddress;
		/*
		 * if (!LOOPBACKADDRESS.equalsIgnoreCase(targetIPAddress) &&
		 * !LOCALHOST.equalsIgnoreCase(targetIPAddress)) { this.sTargetIPAddress
		 * = InetAddress.getByName(targetIPAddress).getHostAddress(); } else {
		 * this.sTargetIPAddress = targetIPAddress; }
		 */
	}

	/**
	 * @param protocol
	 * @throws Exception
	 */
	public void initializeProtocol(final int protocol) throws Exception {
		// initializeProtocol(protocol, true, null);
		// }
		//// /**
		//// * @param protocol
		//// * @throws Exception
		//// */
		//// public void initializeProtocol(final int protocol, final boolean
		// bMonitorProcess) throws Exception
		//// {
		//// initializeProtocol(protocol, bMonitorProcess, null);
		//// }
		// /**
		// * @param protocol
		// * @param bMonitorProcess
		// * @throws Exception
		// */
		// public void initializeProtocol(final int protocol, final boolean
		// bMonitorProcess, final String ioDisks) throws Exception
		// {
		this.iProtocol = protocol;
		if (this.iProtocol == RSTAT) {
			this.unixMachine = new RstatClient(InetAddress.getByName(this.sTargetIPAddress), RstatConstants.RSTATPROG,
					RstatConstants.RSTATVERS_TIME, OncRpcProtocols.ONCRPC_UDP);
		} else {
			switch (this.iTargetOS) {
			case LINUX_OLD: {
				this.unixMachine = new LinuxNativeAPI(this.sTargetIPAddress, this.userName, this.password,
						this.privateKeyFile, this.authMethod, this.port, this.iProtocol, this.processFilter,
						this.firewallUserName, this.firewallPassword, this.superUserName, this.superUserPassword,
						this.monitorProcess, this.iostatDisks, this.refreshInterval);
				break;
			}
			case LINUX: {
				this.unixMachine = new LinuxProcNativeAPI(this.sTargetIPAddress, this.userName, this.password,
						this.privateKeyFile, this.authMethod, this.port, this.iProtocol, this.processFilter,
						this.firewallUserName, this.firewallPassword, this.superUserName, this.superUserPassword,
						this.monitorProcess, this.iostatDisks, this.monitorNFS, this.refreshInterval);
				break;
			}
			case MACOSX: {
				this.unixMachine = new MacOSXNativeAPI(this.sTargetIPAddress, this.userName, this.password,
						this.privateKeyFile, this.authMethod, this.port, this.iProtocol, this.processFilter,
						this.firewallUserName, this.firewallPassword, this.superUserName, this.superUserPassword,
						this.monitorProcess, this.refreshInterval);
				break;
			}
			case SOLARIS_OLD: {
				this.unixMachine = new SolarisNativeAPI(this.sTargetIPAddress, this.userName, this.password,
						this.privateKeyFile, this.authMethod, this.port, this.iProtocol, this.processFilter,
						this.firewallUserName, this.firewallPassword, this.superUserName, this.superUserPassword,
						this.monitorProcess, this.refreshInterval);
				break;
			}
			case SOLARIS: {
				this.unixMachine = new ZFSNativeAPI(this.sTargetIPAddress, this.userName, this.password,
						this.privateKeyFile, this.authMethod, this.port, this.iProtocol, this.processFilter,
						this.firewallUserName, this.firewallPassword, this.superUserName, this.superUserPassword,
						this.monitorProcess, this.refreshInterval);
				break;
			}
			case AIX: {
				this.unixMachine = new AIXNativeAPI(this.sTargetIPAddress, this.userName, this.password,
						this.privateKeyFile, this.authMethod, this.port, this.iProtocol, this.processFilter,
						this.firewallUserName, this.firewallPassword, this.superUserName, this.superUserPassword,
						this.monitorProcess, this.refreshInterval);
				break;
			}
			}
		}
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public void setKeyFile(final String keyFile) {
		this.privateKeyFile = keyFile;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public void setPort(final int port) {
		this.port = port;
	}

	/**
	 * Checks whether the data can be fetched from target machine or not.
	 * 
	 * @return status of remote machine
	 */
	public boolean ping() throws Exception {
		this.connectionInitialized = this.unixMachine.ping();

		return this.connectionInitialized;
	}

	private void checkConnectionStatus() throws Exception {
		if (!this.connectionInitialized) {
			throw new RuntimeException("Connection to remote machine is not initialized");
		}
	}

	/**
	 * Get aggregate CPU usage of target machine
	 * 
	 * @return cpuUsage - total cpu usage of target machine
	 */
	public int getTotalCPUUsage() throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.getCPUUsage();
	}

	/**
	 * Get aggregate CPU usage of target machine
	 * 
	 * @return cpuUsage - total cpu usage of target machine
	 */
	public int getUserCPUUsage() throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.getUserCPUUsage();
	}

	/**
	 * Get aggregate CPU usage of target machine
	 * 
	 * @return cpuUsage - total cpu usage of target machine
	 */
	public float getSystemCPUUsage() throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.getSystemCPUUsage();
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public int getWaitCPUUsage() throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.getWaitCPUUsage();
	}

	/**
	 * Get aggregate CPU usage of target machine
	 * 
	 * @return cpuUsage - total cpu usage of target machine
	 */
	public int get1MinLoadAverage() throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.get1MinLoadAverage();
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public int get5MinLoadAverage() throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.get5MinLoadAverage();
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public int get15MinLoadAverage() throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.get15MinLoadAverage();
	}

	/**
	 * Get Physical Disk info of target machine Rstat does not support this
	 * attribute.
	 * 
	 * @return DiskInfo[] - Array of DiskInfo of target machine
	 */
	public DiskInfo[] getPhysicalDiskInfo() throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.getPhysicalDiskInfo();
	}

	/**
	 * Get Physical Memory info of target machine. Rstat does not support this
	 * attribute
	 * 
	 * @return MemoryInfo - Physical memory info of target machine
	 */
	public MemoryInfo getPhysicalMemoryInfo() throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.getPhysicalMemoryInfo();
	}

	/**
	 * Get Virtual Memory info of target machine. Rstat does not support this
	 * attribute
	 * 
	 * @return MemoryInfo - Virtual memory info of target machine
	 */
	public MemoryInfo getVirtualMemoryInfo() throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.getVirtualMemoryInfo();
	}

	/**
	 * Get count of Processes running on target machine.
	 * 
	 * @return processCount - count of processes running on target machine
	 */
	public int getProcessCount() throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.getProcessCount();
	}

	/**
	 * Get Process info of all the processes running on target machine. Rstat
	 * does not support this attribute
	 * 
	 * @return ProcessInfo[] - info of processes running on target machine
	 */
	public ProcessInfo[] getProcessInfo() throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.getProcessInfo();
	}

	/**
	 * Get Network info of target machine. Rstat gives aggregate info of all the
	 * network interfaces(including loopback) on target machine
	 * 
	 * @return NetworkInfo[] - network info of target machine
	 */
	public NetworkInfo[] getNetworkInfo() throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.getNetworkInfo();
	}

	public NFSStat getNFSStats() throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.getNFSStats();
	}

	/**
	 * Get Logical Disk info (File System info for Linux/Solaris ) of target
	 * machine Rstat does not support this attribute.
	 * 
	 * @return DriveInfo[] - Array of Logical Disk info of target machine
	 */
	public DriveInfo[] getLogicalDiskInfo() throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.getDiskSpaceInfo();
	}

	/**
	 * @param logFile
	 * @param search
	 * @param lastLine
	 * @return
	 * @throws Exception
	 */
	public LogInfo searchInLogFile(String logFile, String[] search, long lastLine) throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.searchInLogFile(logFile, search, lastLine);
	}

	/**
	 * @param logFile
	 * @param search
	 * @param lastLine
	 * @return
	 * @throws Exception
	 */
	public long getLastLogFileLineNumber(String logFile) throws Exception {
		this.checkConnectionStatus();
		return this.unixMachine.getLastLogFileLineNumber(logFile);
	}

	/**
	 * @param scriptFile
	 * @throws Exception
	 */
	public void executeScript(String scriptFile) throws Exception {
		this.checkConnectionStatus();
		this.unixMachine.executeScript(scriptFile);
	}

	/**
	 * @throws Exception
	 */
	public void collectDataFromTop() throws Exception {
		this.checkConnectionStatus();
		this.unixMachine.collectData();
	}

	/**
	 * disconnect
	 * 
	 * @throws Exception
	 */
	public void disconnect() throws Exception {
		this.checkConnectionStatus();
		this.unixMachine.disconnect();
	}

	/**
	 * @param processFilter
	 */
	public void setProcessFilter(ProcessFilter processFilter) {
		this.processFilter = processFilter;
	}

	public void setFirewallUserName(String firewallUserName) {
		this.firewallUserName = firewallUserName;
	}

	public void setFirewallPassword(String firewallPassword) {
		this.firewallPassword = firewallPassword;
	}

	public void setSuperUserName(String superUserName) {
		this.superUserName = superUserName;
	}

	public void setSuperUserPassword(String superUserPassword) {
		this.superUserPassword = superUserPassword;
	}

	public void setMonitorProcess(boolean monitor) {
		this.monitorProcess = monitor;
	}

	public void setMonitorNFS(boolean monitor) {
		this.monitorNFS = monitor;
	}

	public void setIOStatDisks(String ioDisks) {
		this.iostatDisks = ioDisks;
	}

	public void setRefreshInterval(int interval) {
		this.refreshInterval = interval;
	}

	public void setAuthMethod(int authMethod) {
		this.authMethod = authMethod;
	}
}
