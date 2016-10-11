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
import java.util.List;

import com.queryio.common.IOSProtocolConstants;

import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.LogInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NFSStat;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.parser.LinuxProcDataParser;
import com.queryio.sysmoncommon.sysmon.protocol.AbstractProtocolWrapper;
import com.queryio.sysmoncommon.sysmon.protocol.SSHWrapper;
import com.queryio.sysmoncommon.sysmon.protocol.ShellWrapper;
import com.queryio.sysmoncommon.sysmon.protocol.TelnetWrapper;

/**
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
class LinuxProcNativeAPI extends AbstractUnixNativeAPI
{
	private static final String NETWORK_INFO_CMD = "cat /proc/net/dev";
	private static final String DISK_SPACE_CMD = "df -k";
//	private static final String VERSION_INFO_CMD = "cat /proc/version";
	private static final String NFS_CLIENT_STATS_CMD = "cat /proc/net/rpc/nfs";
	private static final String NFS_SERVER_STATS_CMD = "cat /proc/net/rpc/nfsd";
	
	private final String PROCESSCOMMANDOWNER = "ps -o pid,etime,time,vsz,thcount,user,command -u ";
	
	private AbstractProtocolWrapper wrapperForProc;
	private AbstractProtocolWrapper wrapperForDisk;

//	private String sVersion;

	private boolean bFirstTimeDiskInfo = true;
	
	private String iostatDisks;
	private boolean monitorNFS;
	
	public LinuxProcNativeAPI(final String hostName, final String user, final String pwd, final String key, final int authMethod, final int port, final int iProtocol, 
		final ProcessFilter processFilter, final String fUserName, final String fPassword,  final String superUserName, 
		final String superUserPassword, final boolean monitorProcess, final String ioDisks, final boolean monitorNFS, final int refreshInterval)
	{
		super(hostName, user, pwd, key, authMethod, port, iProtocol, processFilter, fUserName,
			fPassword, superUserName, superUserPassword, monitorProcess, refreshInterval);
		this.iostatDisks = ioDisks;
		this.monitorNFS = monitorNFS;
		try
		{
			if (hostName.equals("127.0.0.1") || hostName.equals("localhost"))
			{
				this.wrapperForProc = new ShellWrapper(IOSProtocolConstants.LINUX);
				if (processFilter == null)
					this.wrapperForDisk = new ShellWrapper(IOSProtocolConstants.LINUX);
			}
			else if (iProtocol == IOSProtocolConstants.TELNET)
			{
				this.wrapperForProc = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.LINUX);
				if (processFilter == null)
					this.wrapperForDisk = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.LINUX);
			}
			else if (iProtocol == IOSProtocolConstants.SSH)
			{
				this.wrapperForProc = new SSHWrapper(hostName, user, pwd, key, authMethod, port, IOSProtocolConstants.LINUX);
				if (processFilter == null)
					this.wrapperForDisk = new SSHWrapper(hostName, user, pwd, key, authMethod, port, IOSProtocolConstants.LINUX);
			}
			setFirewallDetails(new AbstractProtocolWrapper [] {
				this.wrapperForProc, this.wrapperForDisk});
		}
		catch (final Exception ex)
		{
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
	public boolean ping() throws Exception
	{
		if (!this.wrapperForProc.isConnected())
		{
			this.wrapperForProc.connect();
			if (!this.wrapperForProc.login())
			{
				return false;
			}
			//this.sVersion = this.wrapperForProc.execute(VERSION_INFO_CMD, false);
		}
		if (processFilter == null)
		{
			if (!this.wrapperForDisk.isConnected())
			{
				this.wrapperForDisk.connect();
				if (!this.wrapperForDisk.login())
				{
					return false;
				}
			}
		}
		this.dataParser = new LinuxProcDataParser();
		((LinuxProcDataParser)this.dataParser).setIostatGrepDisks(this.iostatDisks);
		this.dataParser.setMonitorProcesses(monitorProcesses);
		return true;
	}

	/**
	 * Disconnect from remote machine
	 */
	public void disconnect() throws IOException
	{
		if (this.wrapperForProc != null)
		{
			this.wrapperForProc.disconnect();
		}
	}
	
	/**
	 * Get Disk info of the remote machine from /proc/diskstats or /proc/partitions
	 * And parse the returned string. Extract the disk info.
	 */
	public DiskInfo[] getPhysicalDiskInfo() throws Exception
	{
		String command = null;
		String result = null;
		if (this.bFirstTimeDiskInfo)
		{
			if (this.wrapperForDisk != null)
			{
				StringBuffer buffer = new StringBuffer("iostat ");
				buffer.append(this.monitorNFS ? "-nk -d " : "-k -d ");
				buffer.append(this.refreshInterval);
				if (this.iostatDisks != null && this.iostatDisks.trim().length() > 0)
				{
					buffer.append(" ");
					buffer.append(this.iostatDisks);
				}
				command = buffer.toString();
				this.wrapperForDisk.execute(command, true);
			}
			this.bFirstTimeDiskInfo = false;
		}
		if (this.wrapperForDisk != null)
		{
			result = this.wrapperForDisk.getIostatOutputForLinux(this.monitorNFS);
			int i = 3;
			while(i > 0)
			{
				//Check if result contains PID if not then try fetching result again 
				if(result == null || result.trim().length() == 0)
				{
					result = this.wrapperForDisk.getIostatOutputForLinux(this.monitorNFS);
				}
				else
				{
					i = 0;
				}
				i--;
			}
		}
		assertCommandNotFound(result, "Disk Usage", command);
		this.dataParser.parseIOStatCommand(result);
		return this.dataParser.getPhysicalDiskInfo();
	}
	
	private void collectProcessFilteredData() throws Exception
	{
		String result = null;
		String commandUsed = null;
		boolean bPSResult = false;
		if(this.processFilter.pid != -1)
		{
			commandUsed = "cat /proc/" + this.processFilter.pid +"/stat";
			result = this.wrapperForProc.execute(commandUsed, true);
		}
		else
		{
			commandUsed = this.PROCESSCOMMANDOWNER + this.processFilter.owner;
			result = this.wrapperForProc.execute(commandUsed, true);
			//Check if result contains PID if not then try fetching result again 
			if(result.indexOf("PID") == -1 || result.indexOf("pid") == -1)
			{
				result = this.wrapperForProc.execute(commandUsed, true);
			}
			bPSResult = true;
		}
		assertCommandNotFound(result, "Process Usage", commandUsed);
		dataParser.resetProcessInfoList();
        this.dataParser.parseProcProcessInfo(result, bPSResult);
	}

	/**
	 * method getCPUUsage
	 * 
	 * @return
	 * @throws Exception
	 */
	public void collectData() throws Exception
	{
		if (this.processFilter != null)
		{
			collectProcessFilteredData();
			return;
		}
		//String command = null;
		String result = null;
		if (this.wrapperForProc != null)
		{
		    result = wrapperForProc.execute("ls /proc", true);
		    List pidList = dataParser.parseProcessIdList(result); //also used for finding count of running processes 
			if(monitorProcesses)
			{
				dataParser.resetProcessInfoList();
				/*
				for (Iterator iterator = pidList.iterator(); iterator.hasNext();)
				{
					String pid = (String) iterator.next();
					result = this.wrapperForProc.execute("cat /proc/" + pid +"/stat", false);
					assertCommandNotFound(result, pid, command); 

					dataParser.parseProcProcessInfo(result, false);
				}
				*/
				StringBuffer buffer = new StringBuffer("cat ");
				if (pidList.size() > 0) {
					for (int i = 0; i < pidList.size(); i++) {
						
						if (buffer.length() == 0) {
							buffer.append("cat ");
						}
						buffer.append(" /proc/");
						buffer.append(pidList.get(i));
						buffer.append("/stat");
						
						if ((i+1) % 100 == 0) {
							result = this.wrapperForProc.execute(buffer.toString(), true);
							buffer.setLength(0);
							dataParser.parseProcProcessInfo(result, false);
						}
					}
					if (buffer.length() > 0) {
						result = this.wrapperForProc.execute(buffer.toString(), true);
						dataParser.parseProcProcessInfo(result, false);
					}
				}
			}
			collectLoadAverages();
			collectCPUInfo();
			collectMemoryInfo();
		}
		//this.dataParser.parseTopCommand(result);
	}

	private void collectCPUInfo() throws Exception
	{
		//Read CPU : /proc/stat
		String result = wrapperForProc.execute("cat /proc/stat", true);
		dataParser.parseProcStat(result);
	}
	
	private void collectLoadAverages() throws Exception
	{
		//Read Load Average : /proc/loadavg
		String result = this.wrapperForProc.execute("cat /proc/loadavg", true);
		dataParser.parseLoadAverages(result);
	}
	
	private void collectMemoryInfo() throws Exception
	{
		//Read Memory info : /proc/meminfo
		String result = wrapperForProc.execute("cat /proc/meminfo", true);
		dataParser.parseProcMeminfo(result);
	}

	/**
	 * Get Network info of the remote machine Execute netstat command with -i
	 * option Parse the returned String. Extract the network info.
	 */
	protected NetworkInfo[] getNetworkData() throws Exception
	{
		String result = null;
		if (this.wrapperForProc != null)
		{
			//Read Network Info: /proc/net/dev
			result = this.wrapperForProc.execute(NETWORK_INFO_CMD, true);
		}
		assertCommandNotFound(result, "Network Usage", NETWORK_INFO_CMD);
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
	public DriveInfo[] getDiskSpaceInfo() throws Exception
	{
		String result = null;
		if (this.wrapperForProc != null)
		{
			//NOTE: DF uses system call statfs to get the info. Could not find required info in /proc
			result = this.wrapperForProc.execute(DISK_SPACE_CMD, true);
		}
		assertCommandNotFound(result, "Logical Disk Info", DISK_SPACE_CMD);
		this.dataParser.parseDFCommand(result);
		return this.dataParser.getDiskSpaceInfo();
	}
	
	protected AbstractProtocolWrapper getSearchLogWrapper()
	{
		return this.wrapperForProc;
	}
	
	/**
	 * @see com.queryio.sysmoncommon.sysmon.IUnixNativeAPI#searchInLogFile(java.lang.String, java.lang.String, long)
	 */
	public LogInfo searchInLogFile(String logFile, String []search, long lastLine) throws Exception
	{
		if (this.wrapperForProc != null)
		{
			return this.wrapperForProc.searchInLogFile(logFile, "tail --lines=+", search, lastLine);
		}
		return null;
	}
	
	/**
	 * @see com.queryio.sysmoncommon.sysmon.IUnixNativeAPI#executeScript(java.lang.String)
	 */
	public void executeScript(String scriptFile) throws Exception
	{
		if (this.wrapperForProc != null)
		{
			this.wrapperForProc.execute(scriptFile, true);
		}
	}
	
	public NFSStat getNFSStats() throws Exception
    {
	    String clientResult = null;
	    String serverResult = null;
        if (this.wrapperForProc != null)
        {
            //Read client NFS Info: /proc/net/rpc/nfs
            clientResult = this.wrapperForProc.execute(NFS_CLIENT_STATS_CMD, true);
            
            //Read server NFS Info: /proc/net/rpc/nfsd
            serverResult = this.wrapperForProc.execute(NFS_SERVER_STATS_CMD, true);
        }
        assertCommandNotFound(clientResult, "NFS Client Stats", NFS_CLIENT_STATS_CMD);
        assertCommandNotFound(serverResult, "NFS Server Stats", NFS_SERVER_STATS_CMD);
        this.dataParser.parseNSFStatCommand(clientResult, serverResult);
        return this.dataParser.getNFSStat();
    }
}