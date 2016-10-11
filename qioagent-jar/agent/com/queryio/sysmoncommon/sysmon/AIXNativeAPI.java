/**
 * 
 */
package com.queryio.sysmoncommon.sysmon;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.queryio.common.IOSProtocolConstants;
import com.queryio.common.util.IntHashMap;
import com.queryio.sysmoncommon.sysmon.dstruct.AIXDiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.AIXProcessInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.LogInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;
import com.queryio.sysmoncommon.sysmon.protocol.AbstractProtocolWrapper;
import com.queryio.sysmoncommon.sysmon.protocol.SSHWrapper;
import com.queryio.sysmoncommon.sysmon.protocol.ShellWrapper;
import com.queryio.sysmoncommon.sysmon.protocol.TelnetWrapper;

/**
 * @author manoj
 *
 */
public class AIXNativeAPI extends AbstractUnixNativeAPI
{
	private final String VMSTATCOMMAND = "vmstat -t 1 1";
	private final String PROCESSCOMMAND = "ps -eo pid,etime,time,vsz,thcount,user,comm";
	private final String PROCESSCOMMANDOWNER = "ps -o pid,etime,time,vsz,thcount,user,args -f -l -u ";
	private final String PROCESSCOMMANDPID = "ps -o pid,etime,time,vsz,thcount,user,comm -p ";
	//private final String PROCESSCMD = "ps -eo pid,args -f -l -uroot";
	//ps -o comm,pid,etime,time,vsz,thcount,user -p 35172
	private final String NETWORKINFOCOMMAND = "netstat -i";
	private final String DISKINFOCOMMAND = "iostat -d 1 1";
	private final String DISKSPACEINFOCOMMAND = "df -k";

	private AbstractProtocolWrapper wrapperForVMStat;
	private AbstractProtocolWrapper wrapperForNetwork;
	private AbstractProtocolWrapper wrapperForDiskSpace;
	private AbstractProtocolWrapper wrapperForProcess;
	private AbstractProtocolWrapper wrapperForDiskInfo;
	private LinkedList llDiskInfo;
	private IntHashMap ihmProcessInfo;
	private boolean bDiskFirstTime = true;
	private int noOfCPUs = 1;

//	public AIXNativeAPI(String hostName, String user, String pwd, int port, int iProtocol, 
//		final boolean monitorProcess)
//	{
//		this(hostName, user, pwd, port, iProtocol, monitorProcess, null, null, null, null, null);
//	}
	public AIXNativeAPI(final String hostName, final String user, final String pwd, final String key, final int authMethod, final int port, 
		final int iProtocol, final ProcessFilter processFilter, final String fUserName, final String fPassword, 
		final String superUserName, final String superUserPassword, final boolean monitorProcess, final int refreshInterval)
	{
		super(hostName, user, pwd, key, authMethod, port, iProtocol, processFilter, fUserName,
			fPassword, superUserName, superUserPassword, monitorProcess, refreshInterval);
		try
		{
			if (this.monitorProcesses)
			{
				ihmProcessInfo = new IntHashMap();
			}
			if (hostName.equals("127.0.0.1") || hostName.equals("localhost"))
			{
				this.wrapperForVMStat = new ShellWrapper(IOSProtocolConstants.AIX);
				if(this.monitorProcesses)
				{
					this.wrapperForProcess = new ShellWrapper(IOSProtocolConstants.AIX);
				}
				if (this.processFilter == null)
				{
					this.wrapperForDiskInfo = new ShellWrapper(IOSProtocolConstants.AIX);
					this.wrapperForNetwork = new ShellWrapper(IOSProtocolConstants.AIX);
					this.wrapperForDiskSpace = new ShellWrapper(IOSProtocolConstants.AIX);
				}
			}
			else if (iProtocol == IOSProtocolConstants.TELNET)
			{
				this.wrapperForVMStat = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.AIX);
				if(this.monitorProcesses)
				{
					this.wrapperForProcess = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.AIX);
				}
				if (this.processFilter == null)
				{
					this.wrapperForDiskInfo = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.AIX);
					this.wrapperForNetwork = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.AIX);
					this.wrapperForDiskSpace = new TelnetWrapper(hostName, user, pwd, port, IOSProtocolConstants.AIX);
				}
			}
			else if (iProtocol == IOSProtocolConstants.SSH)
			{
				this.wrapperForVMStat = new SSHWrapper(hostName, user, pwd, key, authMethod, port, IOSProtocolConstants.AIX);
				if(this.monitorProcesses)
				{
					this.wrapperForProcess = new SSHWrapper(hostName, user, pwd, key, authMethod, port, IOSProtocolConstants.AIX);
				}
				if (this.processFilter == null)
				{
					this.wrapperForDiskInfo = new SSHWrapper(hostName, user, pwd, key, authMethod, port, IOSProtocolConstants.AIX);
					this.wrapperForNetwork = new SSHWrapper(hostName, user, pwd, key, authMethod, port, IOSProtocolConstants.AIX);
					this.wrapperForDiskSpace = new SSHWrapper(hostName, user, pwd, key, authMethod, port, IOSProtocolConstants.AIX);
				}
			}
			setFirewallDetails(new AbstractProtocolWrapper [] {
				this.wrapperForVMStat, this.wrapperForProcess, this.wrapperForDiskInfo, this.wrapperForNetwork, 
				this.wrapperForDiskSpace});
		}
		catch(Exception ex)
		{
			// If the machine doesn't have telnet installed on it then it gives exception while creating TelnetClient
			// So supress this exception 
		}
	}	
	/**
	 * @see com.queryio.sysmoncommon.sysmon.IUnixNativeAPI#ping()
	 */
	public boolean ping() throws Exception
	{
		if (this.processFilter != null)
		{
			if (!this.wrapperForVMStat.isConnected())
			{
				this.wrapperForVMStat.connect();
				if (!this.wrapperForVMStat.login())
				{
					return false;
				}
			}
			if (this.wrapperForProcess != null && !this.wrapperForProcess.isConnected())
			{
				this.wrapperForProcess.connect();
				if (!this.wrapperForProcess.login())
				{
					return false;
				}
			}		
			if (this.dataParser == null)
			{
				this.dataParser = DataParserFactory.getDataParser(IOSProtocolConstants.AIX, null, null);
				if (this.processFilter != null && this.dataParser != null)
				{
					this.dataParser.setProcessFilter(this.processFilter);
				}
			}
			return true;
		}

		if (this.wrapperForNetwork == null)
		{
			return false;
		}
		if (!this.wrapperForNetwork.isConnected())
		{
			this.wrapperForNetwork.connect();
			if (!this.wrapperForNetwork.login())
			{
				return false;
			}
		}
		if (!this.wrapperForDiskSpace.isConnected())
		{
			this.wrapperForDiskSpace.connect();
			if (!this.wrapperForDiskSpace.login())
			{
				return false;
			}
		}
		if (!this.wrapperForVMStat.isConnected())
		{
			this.wrapperForVMStat.connect();
			if (!this.wrapperForVMStat.login())
			{
				return false;
			}
		}
		if (this.wrapperForProcess != null && !this.wrapperForProcess.isConnected())
		{
			this.wrapperForProcess.connect();
			if (!this.wrapperForProcess.login())
			{
				return false;
			}
		}		
		if (!this.wrapperForDiskInfo.isConnected())
		{
			this.wrapperForDiskInfo.connect();
			if (this.wrapperForDiskInfo.login())
			{
				if (this.dataParser == null)
				{
					this.dataParser = DataParserFactory.getDataParser(IOSProtocolConstants.AIX, null, null);
				}
				return true;
			}
		}
		return false;
	}	
	/**
	 * Disconnect from remote machine
	 */
	public void disconnect() throws IOException
	{
		if (this.wrapperForNetwork != null)
		{
			this.wrapperForNetwork.disconnect();
		}
		if (this.wrapperForDiskSpace != null)
		{
			this.wrapperForDiskSpace.disconnect();
		}
		if (this.wrapperForVMStat != null)
		{
			this.wrapperForVMStat.disconnect();
		}
		if (this.wrapperForDiskInfo != null)
		{
			this.wrapperForDiskInfo.disconnect();
		}
		if (this.wrapperForProcess != null)
		{
			this.wrapperForProcess.disconnect();
		}		
	}	
	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractUnixNativeAPI#collectData()
	 */
	public void collectData() throws Exception
	{
		String result = null;
		if (this.wrapperForVMStat != null)
		{
			result = this.wrapperForVMStat.execute(this.VMSTATCOMMAND, true);
		}
		assertCommandNotFound(result, "CPU / Memory Usage", this.VMSTATCOMMAND);
		this.dataParser.parseVmstatCommand(result);
		this.noOfCPUs = this.dataParser.getNoOfProcessors();
		if(this.monitorProcesses)
		{
			if (this.wrapperForProcess != null)
			{
				if (this.processFilter != null)
				{
					if(this.processFilter.pid != -1)
					{
						result = this.wrapperForProcess.execute(this.PROCESSCOMMANDPID  + this.processFilter.pid, false);
						int i = 5;
						while(i > 0)
						{
							//Check if result contains PID if not then try fetching result again 
							if(result.indexOf(processFilter.pid) == -1)
							{
								result = this.wrapperForProcess.execute(this.PROCESSCOMMANDPID + this.processFilter.pid, false);
							}
							if(result.indexOf("PID") != -1 || result.indexOf("pid") != -1)
							{
								break;
							}
							i--;
							Thread.sleep(500);
						}
					}
					else
					{
						int i = 5;
						while(i > 0)
						{
							//Check if result contains PID if not then try fetching result again 
							if(result.indexOf("PID") == -1 || result.indexOf("pid") == -1)
							{
								result = this.wrapperForProcess.execute(this.PROCESSCOMMANDOWNER + this.processFilter.owner, false);
							}
							if(result.indexOf("PID") != -1 || result.indexOf("pid") != -1)
							{
								break;
							}
							i--;
							Thread.sleep(500);
						}
					}
				}
				else
				{
					result = this.wrapperForProcess.execute(this.PROCESSCOMMAND, false);
					//Check if result contains PID if not then try fetching result again 
					if(result.indexOf("PID") == -1 || result.indexOf("pid") == -1)
					{
						result = this.wrapperForProcess.execute(this.PROCESSCOMMAND, true);
					}
				}
			}
			assertCommandNotFound(result, "Process Usage", this.PROCESSCOMMAND);
			this.dataParser.parseTopCommand(result);
		}
	}
	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractUnixNativeAPI#getDiskSpaceInfo()
	 */
	public DriveInfo[] getDiskSpaceInfo() throws Exception
	{
		String result = null;
		if (this.wrapperForDiskSpace != null)
		{
			result = this.wrapperForDiskSpace.execute(this.DISKSPACEINFOCOMMAND, true);
		}
		assertCommandNotFound(result, "Logical Disk Info", this.DISKSPACEINFOCOMMAND);
		this.dataParser.parseDFCommand(result);
		return this.dataParser.getDiskSpaceInfo();
	}	
	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractUnixNativeAPI#getNetworkData()
	 */
	public NetworkInfo[] getNetworkData() throws Exception
	{
		String result = null;
		if (this.wrapperForNetwork != null)
		{
			result = this.wrapperForNetwork.execute(this.NETWORKINFOCOMMAND, true);
		}
		assertCommandNotFound(result, "Network Usage", this.NETWORKINFOCOMMAND);
		this.dataParser.parseNetStatCommand(result);
		return this.dataParser.getNetworkInfo();
	}
	
	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractUnixNativeAPI#getPhysicalDiskInfo()
	 */
	public DiskInfo[] getPhysicalDiskInfo() throws Exception
	{
		String result = null;
		if (this.wrapperForDiskInfo != null)
		{
			result = this.wrapperForDiskInfo.execute(this.DISKINFOCOMMAND, true);
		}
		assertCommandNotFound(result, "Disk Usage", this.DISKINFOCOMMAND);
		this.dataParser.parseIOStatCommand(result);
		AIXDiskInfo[] aixDiskInfo = (AIXDiskInfo[])this.dataParser.getPhysicalDiskInfo();
		
		if (aixDiskInfo != null)
		{
			// diskInfo LinkList is null
			if (this.llDiskInfo == null)
			{
				this.llDiskInfo = new LinkedList();
			}

			for (int i = 0; i < aixDiskInfo.length; ++i)
			{
				// if the NetworkInfo is not being monitored for the first time
				// then find the the object in the LinkedList
				boolean bFound = false;
				if (!this.bDiskFirstTime)
				{
					AIXDiskInfo itrDiskInfo;
					final int iSize = this.llDiskInfo.size();

					// search for the NetworkInfo Object in the LinkedList
					for (int j = 0; j < iSize; j++)
					{
						itrDiskInfo = (AIXDiskInfo) this.llDiskInfo.get(j);

						// if found then break
						if (itrDiskInfo.getName().equals(aixDiskInfo[i].getName()))
						{
							// set the totalPackets recd and sent in the object
							itrDiskInfo.setTotalReads(aixDiskInfo[i].getTotalReads());
							itrDiskInfo.setTotalWrites(aixDiskInfo[i].getTotalWrites());
							bFound = true;
							break;
						}
					}
				}
				if (!bFound)
				{
					this.llDiskInfo.add(aixDiskInfo[i]);
				}
			}
			this.bDiskFirstTime = false;
		}
		if (this.llDiskInfo != null)
		{
			return (DiskInfo[]) this.llDiskInfo.toArray(new DiskInfo[this.llDiskInfo.size()]);
		}
		return null;
	}
	
	public ProcessInfo[] getProcessInfo() throws Exception 
	{
		AIXProcessInfo[] aixProcessInfo = (AIXProcessInfo[])this.dataParser.getProcessInfo();
		AIXProcessInfo storedProcessInfo;
		if (aixProcessInfo != null && aixProcessInfo.length > 0)
		{
			HashSet set = new HashSet();
			int [] ids = ihmProcessInfo.keys();
			for (int i = 0; i < ids.length; i++) 
			{
				set.add(new Integer(ids[i]));
			}
			for (int i = 0; i < aixProcessInfo.length; i++) 
			{
				storedProcessInfo = (AIXProcessInfo)ihmProcessInfo.get(aixProcessInfo[i].getProcessID());
				//System.out.println(aixProcessInfo[i].getName() + " Memory: " + aixProcessInfo[i].getMemoryUsage() + " ID: " + aixProcessInfo[i].getProcessID() + " found: " + (storedProcessInfo != null));
				if (storedProcessInfo != null)
				{
					// existing process found, update its CPU usage
					set.remove(new Integer(aixProcessInfo[i].getProcessID()));
					
					storedProcessInfo.update(aixProcessInfo[i].getUpTime(), aixProcessInfo[i].getCPUTime(), 
						noOfCPUs, aixProcessInfo[i].getMemoryUsage(), aixProcessInfo[i].getThreadCount());
				}
				else
				{
					// new process found, add it
					ihmProcessInfo.put(aixProcessInfo[i].getProcessID(), aixProcessInfo[i]);
				}
			}
			if (!set.isEmpty())
			{
				// some processes have terminated, should be removed
				for (Iterator iterator = set.iterator(); iterator.hasNext();) 
				{
					ihmProcessInfo.remove(((Integer) iterator.next()).intValue());
				}
			}
		}
		else
		{
			ihmProcessInfo.clear();
		}
		return (ProcessInfo[]) this.ihmProcessInfo.values().toArray(new ProcessInfo[this.ihmProcessInfo.size()]);
	}
	
	protected AbstractProtocolWrapper getSearchLogWrapper()
	{
		return this.wrapperForVMStat;
	}

	public LogInfo searchInLogFile(String logFile, String [] search, long lastLine) throws Exception
	{
		if (this.wrapperForVMStat != null)
		{
			return this.wrapperForVMStat.searchInLogFile(logFile, "tail +", search, lastLine);
		}
		return null;
		/*
		String result = null;
		if (this.wrapperForVMStat != null)
		{
			result = this.wrapperForVMStat.execute(this.LOGFILECOMMAND + lastLine + " " + logFile, false);
		}
		assertCommandNotFound(result, "Log File Search", this.LOGFILECOMMAND + lastLine + " " + logFile);
		LogInfo logInfo = new LogInfo();
		if (result != null) // output will be null if this method gets called after disconnect
		{
			String sLine = null;
			final StringTokenizer st = new StringTokenizer(result, NEW_LINE_TOKENS);
			while (st.hasMoreTokens())
			{
				sLine = st.nextToken();
				if (sLine.startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT) || sLine.trim().equals("$")) // prompt)
				{
					break;
				}		
				
				if(sLine.indexOf(search) != -1)
				{
					logInfo.setFound(true);
					logInfo.setLineNum(lastLine);
					logInfo.setLine(sLine);
				}
				lastLine++;
			}
		}
		logInfo.setLastLineRead(lastLine);
		return logInfo;
		*/
	}
	
	public void executeScript(String scriptFile) throws Exception
	{
		if (this.wrapperForVMStat != null)
		{
			this.wrapperForVMStat.execute(scriptFile, true);
		}
	}	
}
