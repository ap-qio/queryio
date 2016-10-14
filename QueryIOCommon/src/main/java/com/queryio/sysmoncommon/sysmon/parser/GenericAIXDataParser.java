package com.queryio.sysmoncommon.sysmon.parser;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.StringTokenizer;

import com.queryio.sysmoncommon.sysmon.AbstractDataParser;
import com.queryio.sysmoncommon.sysmon.ResultParsingException;
import com.queryio.sysmoncommon.sysmon.dstruct.AIXDiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.AIXProcessInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.UnixNetworkInfo;
import com.queryio.sysmoncommon.sysmon.protocol.AbstractProtocolWrapper;

public class GenericAIXDataParser extends AbstractDataParser
{
	/**
	 * Do not remove this method as its added to avoid class cast exception. 
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#getPhysicalDiskInfo()
	 */
	public DiskInfo[] getPhysicalDiskInfo()
	{
		if(diskInfoList != null)
		{
			return (AIXDiskInfo[])diskInfoList.toArray(new AIXDiskInfo[diskInfoList.size()]);
		}
		return null;		
	}	
	/**
	 * Do not remove this method as its added to avoid class cast exception. 
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#getProcessInfo()
	 */
	public ProcessInfo[] getProcessInfo()
	{
		if(processInfoList != null)
		{
			return (AIXProcessInfo[])processInfoList.toArray(new AIXProcessInfo[processInfoList.size()]);
		}
		return null;
	}	
	
	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#parsePrstatCommand(java.lang.String)
	 */
	public void parsePrstatCommand(String output) throws ResultParsingException
	{
		//DO NOTHING
	}
	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#parseTopMemCommand(java.lang.String)
	 */
	public void parseTopMemCommand(String output) throws ResultParsingException
	{
		//DO NOTHING		
	}	
	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#parseTopCommand(java.lang.String)
	 */
	public void parseTopCommand(String output) throws ResultParsingException
	{
		/*
			COMMAND    PID     ELAPSED      TIME   VSZ THCNT     USER
			init         1 29-05:53:32  00:00:49   588     1     root
			java     29318  3-05:44:22  00:06:37 60648    65     root
		*/
		if (this.processInfoList == null)
		{
			this.processInfoList = new LinkedList();
		}
		else
		{
			this.processInfoList.clear();
		}
	
		if (output != null) // output will be null if this method gets called after disconnect
		{
			String sLine = null;
			String [] headers = null;
			final StringTokenizer st = new StringTokenizer(output, NEW_LINE_TOKENS);
			ProcessInfo pInfo;
			boolean bCanAdd = false;
			while (st.hasMoreTokens())
			{
				sLine = st.nextToken();
				if (sLine.startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT) || sLine.trim().equals("$")) // prompt)
				{
					break;
				}				
				else if (headers == null)
				{
					String lowercase = sLine.toLowerCase().trim();
					if (lowercase.startsWith("pid ") || lowercase.startsWith("command") || lowercase.startsWith("user"))
					{
						headers = getTokens(sLine.toLowerCase().trim());
						continue;
					}
				}
				if (headers == null)
				{
					continue;
				}
				
				String sProcessName = null;
				String sUserName = null;
				int iPID = 0;
				int iProcessMemory = 0;
				long upTime = 0;
				long processorTime = 0;
				int iThreadCount = 0;

				final String [] values = getTokens(sLine);
				int n = Math.min(headers.length, values.length);
				try
				{	bCanAdd = true;
					for (int i = 0; i < n; i++) 
					{
						if (headers[i].equals("command"))
						{
							if(values[i].trim().length() == 0)
							{
								bCanAdd = false;
							}
							else							
							{
								sProcessName = values[i];
							}
						}
						else if (headers[i].equals("pid"))
						{
							if(values[i].trim().length() == 0)
							{
								bCanAdd = false;
							}
							else
							{
								iPID = this.nf.parse(values[i]).intValue();
							}
						}
						else if (headers[i].equals("elapsed"))
						{
							try
							{
								upTime = parsePSTime(values[i]);
							}
							catch(Exception e)
							{
								bCanAdd = false;
							}
						}
						else if (headers[i].equals("time"))
						{
							try
							{
								processorTime = parsePSTime(values[i]);
							}
							catch(Exception e)
							{
								bCanAdd = false;
							}							
						}
						else if (headers[i].equals("%mem") || headers[i].equals("vsz")
								|| headers[i].equals("sz"))
						{
							if(values[i].trim().length() == 0)
							{
								bCanAdd = false;
							}
							else
							{							
								iProcessMemory = this.nf.parse(values[i]).intValue();
							}
						}
						else if (headers[i].equals("thcnt"))
						{
							if(values[i].trim().length() == 0)
							{
								bCanAdd = false;
							}
							else
							{							
								iThreadCount = this.nf.parse(values[i]).intValue();
							}
						}
						else if (headers[i].equals("user"))
						{
							if(values[i].trim().length() == 0)
							{
								bCanAdd = false;
							}
							else
							{
								sUserName = values[i];
							}
						}
					}
					if(bCanAdd)
					{
						String fullProcessName = sProcessName;
						// No need to check indexOf, bcoz if its -1, still it will begin from 0 which is
						// proper.
						sProcessName = fullProcessName.substring(fullProcessName.lastIndexOf('/') + 1);
						pInfo = new AIXProcessInfo(sProcessName, iPID, iProcessMemory, iThreadCount, upTime, processorTime);
						if (n < values.length)
						{
							StringBuffer command = new StringBuffer(fullProcessName);
							for (int j = n; j < values.length; j++) 
							{
								command.append(' ');
								command.append(values[j]);
							}
							pInfo.setCommand(command.toString());
						}
						pInfo.setUserName(sUserName);
						if (this.processFilter == null || this.processFilter.matches(pInfo))
						{
							this.processInfoList.add(pInfo);
							if (this.processFilter != null)
							{
								// even break will do the same, but return is better to avoid confusion
								// of which loop to break from.
								return;
							}
						}
					}
				}
				catch (Exception ex)
				{
					//DO NOTHING
				}
			}
		}
	}
	
	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#parseNetStatCommand(java.lang.String)
	 */
	public void parseNetStatCommand(String output) throws ResultParsingException
	{
		/*
			$ netstat -i
			Name  Mtu   Network     Address              Ipkts Ierrs    Opkts Oerrs  Coll
			en0   1500  link#2      0.9.6b.2e.be.3c     251284     0   128748     0     0
			en0   1500  192.168.1   ap-aix              251284     0   128748     0     0
			lo0   16896 link#1                           10095     0    11270     0     0
			lo0   16896 127         localhost            10095     0    11270     0     0
			lo0   16896 ::1                              10095     0    11270     0     0
			$
		*/
		if (this.networkInfoList == null)
		{
			this.networkInfoList = new LinkedList();
		}
		else
		{
			this.networkInfoList.clear();
		}
		if (output != null) // output will be null if this method gets called after disconnect
		{
			String sLine = null;
			String [] headers = null;

			final StringTokenizer st = new StringTokenizer(output, NEW_LINE_TOKENS);
			HashSet readNetworks = new HashSet();
			while (st.hasMoreTokens())
			{
				sLine = st.nextToken();
				if (sLine.startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT) || sLine.trim().equals("$")) // prompt)
				{
					break;
				}				
				else if (sLine.toLowerCase().startsWith("name"))
				{
					headers = getTokens(sLine.toLowerCase());
					continue;
				}
				else if (headers == null)
				{
					continue;
				}

				final String [] values = getTokens(sLine);
				int n = Math.min(headers.length, values.length);
				String name = null;
				long recvOK = 0;
				long sentOK = 0;
				try
				{				
					for (int i = 0; i < n; i++) 
					{
						if (headers[i].equals("name"))
						{
							// do not read local loopback details OR network card with same name has been read
							if (values[i].startsWith("lo") || readNetworks.contains(values[i]) || values[i].equals("$"))
							{
								break;
							}
							name = values[i];
							readNetworks.add(values[i]);
						}
						else if (headers[i].equals("ipkts"))
						{
							recvOK = this.nf.parse(values[i]).longValue();
						}
						else if (headers[i].equals("opkts"))
						{
							sentOK = this.nf.parse(values[i]).longValue();
						}
					}
					if (name != null)
					{
						this.networkInfoList.add(new UnixNetworkInfo(name, 0, recvOK, sentOK));
					}
				}
				catch (Exception ex)
				{
					//DO NOTHING
				}
			}
		}
	}
	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#parseIOStatCommand(java.lang.String)
	 */
	public void parseIOStatCommand(String output) throws ResultParsingException
	{
		/*
			$ iostat -d 1 1
			
			System configuration: lcpu=1 drives=2
			
			Disks:        % tm_act     Kbps      tps    Kb_read   Kb_wrtn
			hdisk0           5.0     160.0      11.0          0       160
			cd0              0.0       0.0       0.0          0         0
			$
		*/
		if (this.diskInfoList == null)
		{
			this.diskInfoList = new LinkedList();
		}
		else
		{
			this.diskInfoList.clear();
		}
		if (output != null) // output will be null if this method gets called after disconnect
		{
			String sLine = null;
			String [] headers = null;

			final StringTokenizer st = new StringTokenizer(output, NEW_LINE_TOKENS);
			while (st.hasMoreTokens())
			{
				sLine = st.nextToken();
				if (sLine.startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT) || sLine.trim().equals("$")) // prompt)
				{
					break;
				}					
				else if (sLine.toLowerCase().startsWith("disks:"))
				{
					String [] hdrs = getTokens(sLine.toLowerCase());
					headers = new String[hdrs.length - 1];
					int ctr = 0;
					for (int i = 0; i < hdrs.length; i++) 
					{
						if (hdrs[i].equals("%"))
						{
							continue;
						}
						headers[ctr++] = hdrs[i];
					}					
					continue;
				}
				else if (headers == null)
				{
					continue;
				}

				final String [] values = getTokens(sLine);
				int n = Math.min(headers.length, values.length);
				String name = null;
				long lReads = 0;
				long lWrites = 0;
				try
				{
					for (int i = 0; i < n; i++) 
					{
						if (headers[i].equals("disks:"))
						{
							if (values[i].equals("$")) // prompt
							{
								break;
							}
							name = values[i];
						}
						else if (headers[i].equals("kb_read"))
						{
							lReads = this.nf.parse(values[i]).longValue() * 1024;
						}
						else if (headers[i].equals("kb_wrtn"))
						{
							lWrites = this.nf.parse(values[i]).longValue() * 1024;
						}
					}
					if (name != null)
					{
						this.diskInfoList.add(new AIXDiskInfo(name, lReads, lWrites));
					}
				}
				catch (Exception ex)
				{
					//DO NOTHING
				}					
			}
		}
	}
	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#parseDFCommand(java.lang.String)
	 */
	public void parseDFCommand(String output) throws ResultParsingException
	{
		/*
			$ df -k
			Filesystem    1024-blocks      Free %Used    Iused %Iused Mounted on
			/dev/hd4            65536     38940   41%     1658     6% /
			/dev/hd2          1572864    412172   74%    24540     7% /usr
			/dev/hd9var         65536     57084   13%      381     3% /var
			/dev/hd3           131072    125688    5%       53     1% /tmp
			/dev/hd1          2097152   1161064   45%      243     1% /home
			/proc                   -         -    -         -     -  /proc
			/dev/hd10opt       524288    249408   53%     3147     3% /opt
			$
		*/
		if (this.diskSpaceInfoList == null)
		{
			this.diskSpaceInfoList = new LinkedList();
		}
		else
		{
			this.diskSpaceInfoList.clear();
		}
		if (output != null) // output will be null if this method gets called after disconnect
		{
			String sLine = null;
			String [] headers = null;

			final StringTokenizer st = new StringTokenizer(output, NEW_LINE_TOKENS);
			while (st.hasMoreTokens())
			{
				sLine = st.nextToken();
				if (sLine.startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT) || sLine.trim().equals("$")) // prompt)
				{
					break;
				}					
				else if (sLine.toLowerCase().startsWith("filesystem"))
				{
					headers = getTokens(sLine.toLowerCase());
					continue;
				}
				else if (headers == null)
				{
					continue;
				}

				final String [] values = getTokens(sLine);
				int n = Math.min(headers.length, values.length);
				String name = null;
				float fTotalSpace = 0;
				float fFreeSpace = 0;
				try
				{
					for (int i = 0; i < n; i++) 
					{
						if (headers[i].equals("filesystem"))
						{
							if (values[i].equals("$")) // prompt
							{
								break;
							}
							name = values[i];
						}
						else if (headers[i].equals("1024-blocks"))
						{
							fTotalSpace = this.nf.parse(values[i]).longValue() / 1024;
						}
						else if (headers[i].equals("free"))
						{
							fFreeSpace = this.nf.parse(values[i]).longValue() / 1024;
						}
					}
					if (name != null)
					{
						this.diskSpaceInfoList.add(new DriveInfo(name, fTotalSpace, fTotalSpace - fFreeSpace));
					}
				}
				catch (Exception ex)
				{
					//DO NOTHING
				}					
			}
		}
	}
	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#parseVmstatCommand(java.lang.String)
	 */
	public void parseVmstatCommand(String output) throws ResultParsingException
	{
		/*
			System Configuration: lcpu=1 mem=2048MB
			
			kthr    memory              page              faults        cpu
			----- ----------- ------------------------ ------------ -----------
			 r  b   avm   fre  re  pi  po  fr   sr  cy  in   sy  cs us sy id wa
			 1  1 154408 231439   0   0   0   0    0   0  32 1693 445 18  2 80  1		
		*/
		this.fCpuUsageTime = 0;	
		if (output != null && output.trim().length() > 0) // output will be null if this method gets called after disconnect
		{
			String sLine = null;
			String lowerCaseLine = null;
			String [] headers = null;

			final StringTokenizer st = new StringTokenizer(output, NEW_LINE_TOKENS);
			while (st.hasMoreTokens())
			{
				sLine = st.nextToken();
				
				if (sLine.startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT) || sLine.trim().equals("$")) // prompt)
				{
					break;
				}
				
				lowerCaseLine = sLine.toLowerCase();
				
				if (lowerCaseLine.indexOf("lcpu=") != -1 || lowerCaseLine.indexOf("mem=") != -1)
				{
					int lcpuIndex = lowerCaseLine.indexOf("lcpu=");
					int memIndex = lowerCaseLine.indexOf("mem=");
					
					if (lcpuIndex != -1)
					{
						int spaceIndex = lowerCaseLine.indexOf(' ', lcpuIndex + 5);
						try
						{
							noOfCPUs = Integer.parseInt(sLine.substring(lcpuIndex + 5, spaceIndex));
						}
						catch (Exception ex)
						{
							System.err.println("Error parsing No of logical CPUs");
						}
					}
					
					if (memIndex != -1)
					{
						// total physical memory
						String token = sLine.substring(memIndex + 4);
						int n = token.length();
						int endIndex = -1;
						for (int i = 0; i < n; i++) 
						{
							if (!Character.isDigit(token.charAt(i)))
							{
								endIndex = i;
								break;
							}
						}
						if (endIndex != -1)
						{
							try
							{
								this.physicalMemInfo = new MemoryInfo(
									this.nf.parse(token.substring(0, endIndex)).intValue(), 0, 0);
							}
							catch (Exception ex)
							{
								//DO NOTHING
							}
						}
					}
					continue;
				}
				else if (lowerCaseLine.indexOf("r  b   avm   fre") != -1)
				{
					headers = getTokens(lowerCaseLine);
					continue;
				}
				else if (headers == null)
				{
					continue;
				}
				if (virtualMemInfo == null)
				{
					this.virtualMemInfo = new MemoryInfo(0, 0, 0);
				}
				final String [] values = getTokens(sLine);
				int n = Math.min(headers.length, values.length);
				try
				{				
					for (int i = 0; i < n; i++) 
					{
						if (headers[i].equals("avm"))
						{
							// active virtual memory i.e. used virtual memory
							int activeVirtualMemory = (this.nf.parse(values[i]).intValue() * 4) / 1024;
							this.virtualMemInfo.setUsed(activeVirtualMemory);
						}
						else if (headers[i].equals("fre"))
						{
							int freeVirtualMemory = (this.nf.parse(values[i]).intValue() * 4) / 1024;
							this.virtualMemInfo.setAvailable(freeVirtualMemory);
						}
						else if (headers[i].equals("id"))
						{
							this.fCpuUsageTime = (100 - this.nf.parse(values[i]).intValue());
						}	
					}
					this.virtualMemInfo.setTotal(this.virtualMemInfo.getAvailable() + this.virtualMemInfo.getUsed());
				}
				catch (Exception ex)
				{
					//DO NOTHING
				}						
			}
			this.fCpuUsageTime = Math.min(this.fCpuUsageTime, 100.0f);
		}
	}	

	
//	public static void main(String[] args)  throws Exception
//	{
//		GenericAIXDataParser parser = new GenericAIXDataParser();
//		parser.hashCode();
//		/*
//		com.queryio.sysmoncommon.sysmon.ProcessFilter processFilter = new com.queryio.sysmoncommon.sysmon.ProcessFilter(false);
//		processFilter.setName("dtexec");
//		processFilter.setOwner("root");
//		processFilter.setCommandFilter("4_103_1");
//		parser.setProcessFilter(processFilter);
//		*/
//		parser.parsePSTime("29-05:53:32");
//		parser.parsePSTime("00:02:59");
//		parser.parsePSTime("05:52:26");
//		java.io.File file = new java.io.File("E:\\AkMdRd20\\Inbox\\parse.txt");
//		java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
//		String line = reader.readLine();
//		java.io.StringWriter writer = new java.io.StringWriter();
//		while (line != null) 
//		{
//			writer.write(line);
//			writer.write(System.getProperty("line.separator"));
//			line = reader.readLine();
//		}
//		reader.close();
//		// System.out.println(writer.toString());
//		/*
//		// Memory & CPU Usage
//		parser.parseVmstatCommand(writer.toString());
//		System.out.println("CPU Usage: " + parser.getCPUUsage());
//		System.out.println("Virtual Memory: free " + parser.getVirtualMemoryInfo().getAvailable() + " total: " + parser.getVirtualMemoryInfo().getTotal());
//		System.out.println("Physical Memory: total " + parser.getPhysicalMemoryInfo().getTotal() + " free: " + parser.getPhysicalMemoryInfo().getAvailable());
//		*/
//		/*
//		// Processes
//		parser.parseTopCommand(writer.toString());
//		ProcessInfo [] processes = parser.getProcessInfo();
//		for (int i = 0; i < processes.length; i++) 
//		{
////			System.out.println("new ProcessInfo(\"" + processes[i].getName() + "\", " + 
////				processes[i].getProcessID() + ", " + processes[i].getMemoryUsage() + ", " + 
////				processes[i].getProcessorTime() + ", " + processes[i].getMemoryUsage() + ", " +
////				processes[i].getThreadCount() + ");");
//			System.out.println(processes[i].getName() + " " + processes[i].getProcessID() +  " " + 
//				processes[i].getProcessorTime() + " " + processes[i].getMemoryUsage() + " " + 
//				processes[i].getThreadCount() + " " + processes[i].getUserName() + " " +
//				processes[i].getCommand());
//		}
//		*/
//		/*
//		// Network Info
//		parser.parseNetStatCommand(writer.toString());
//		UnixNetworkInfo [] nInfo = parser.getNetworkInfo();
//		for (int i = 0; i < nInfo.length; i++) 
//		{
//			System.out.println(nInfo[i].getName() + " " + nInfo[i].getTotalRecdPackets() +  " " + nInfo[i].getTotalSentPackets());
//		}
//		*/
//		/*
//		// Drive Info
//		parser.parseDFCommand(writer.toString());
//		DriveInfo [] dInfo = parser.getDiskSpaceInfo();
//		int totalSpace = 0;
//		int totalUsedSpace = 0;
//		for (int i = 0; i < dInfo.length; i++) 
//		{
//			totalSpace += dInfo[i].getTotalSpace();
//			totalUsedSpace += dInfo[i].getUsedSpace();
//			//System.out.println(dInfo[i].getTotalSpace() +  "\t" + dInfo[i].getUsedSpace() + "\t" + dInfo[i].getName());
//		}
//		System.out.println("TotalSpace: " + totalSpace + " Used space: " + totalUsedSpace);
//		*/
//		/*
//		// Disk Info
//		parser.parseIOStatCommand(writer.toString());
//		DiskInfo [] dkInfo = parser.getPhysicalDiskInfo();
//		for (int i = 0; i < dkInfo.length; i++) 
//		{
//			System.out.println(dkInfo[i].getName() + " " + dkInfo[i].getReadsPerSec()  +  " " + 
//				dkInfo[i].getWritesPerSec() + " " + ((AIXDiskInfo)dkInfo[i]).getTotalReads() + " " + 
//				((AIXDiskInfo)dkInfo[i]).getTotalWrites());
//		}
//		*/
//	}
}
/*
 * public void parseTopasCommand(String output) throws ResultParsingException
	{
		if (output != null) //output will be null if this method gets called after disconnect
		{
			if(networkInfoList == null)
			{
				networkInfoList = new LinkedList();
			}
			else
			{
				networkInfoList.clear();
			}
			
			if(diskInfoList == null)
			{
				diskInfoList = new LinkedList();	
			}
			else
			{
				diskInfoList.clear();
			}
			
			if (processInfoList == null)
			{
				processInfoList = new LinkedList();
			}
			else
			{
				processInfoList.clear();
			}
			try
			{
				String sLine = null;
				StringTokenizer st = new StringTokenizer(output, NEW_LINE_TOKENS);
				while (st.hasMoreTokens())
				{
					sLine = st.nextToken().trim();
					//System.err.println(sLine);
					if(sLine.trim().startsWith("Idle")) // Calculate CPU usage
					{
						StringTokenizer values = new StringTokenizer(sLine);
						if(values.hasMoreTokens())
						{
							values.nextToken(); // skip first token, its for string "Idle"
							if(values.hasMoreTokens())
							{
								String temp = values.nextToken();
								temp = temp.trim();
								float idleCpuTime = 0;
								try
								{
									idleCpuTime = nf.parse(temp).floatValue();
								}
								catch(Exception e)
								{
									idleCpuTime = 100;
								}
								fCpuUsageTime = 100 - idleCpuTime;
								//System.out.println("fCpuUsageTime : " + fCpuUsageTime); 
							}
						}							
					}
					if(sLine.startsWith("Network") || sLine.startsWith("Interf") || (sLine.indexOf("I-Pack") != -1 &&  sLine.indexOf("O-Pack") != -1)) // Calculate Network data
					{
						//System.out.println("**********Network Info starts**********");
						sLine = st.nextToken();
						StringTokenizer values = new StringTokenizer(sLine);
						while(st.hasMoreTokens() && !sLine.startsWith("Disk") && values.countTokens() >= 6)
						{
							String interfaceName = values.nextToken(); 
							values.nextToken(); // skip next token
							String temp = values.nextToken();
							temp = temp.trim();
							float recvPackets = 0;
							try
							{
								recvPackets = nf.parse(temp).floatValue();
							}
							catch(Exception e)
							{
								recvPackets = 0;
							}
							
							temp = values.nextToken();
							temp = temp.trim();
							float sentPackets = 0;
							try
							{
								sentPackets = nf.parse(temp).floatValue();
							}
							catch(Exception e)
							{
								sentPackets = 0;
							}
							networkInfoList.add(new UnixNetworkInfo(interfaceName, 0, (long)recvPackets, (long)sentPackets));
							//System.out.println("Network Name " + interfaceName + " recvPackets " + recvPackets + " sentPackets " + sentPackets);

							sLine = st.nextToken();
							values = new StringTokenizer(sLine);
						}
						//System.out.println("**********Network Info Ends**********");
					}
					if(sLine.trim().startsWith("Disk") || (sLine.indexOf("KB-Read") != -1 &&  sLine.indexOf("KB-Writ") != -1) ) // Disk Info
					{
						//System.out.println("**********Disk Info starts**********");
						sLine = st.nextToken(); // values line
						StringTokenizer values = new StringTokenizer(sLine);
						while(st.hasMoreTokens() && !sLine.trim().startsWith("Name")  && values.countTokens() >= 6)
						{
							String diskName = values.nextToken(); 
							values.nextToken(); // skip next token
							values.nextToken(); // skip next token
							values.nextToken(); // skip next token
							String temp = values.nextToken();
							temp = temp.trim();
							float readPerSec = 0;
							try
							{
								readPerSec = nf.parse(temp).floatValue();
							}
							catch(Exception e)
							{
								readPerSec = 0;
							}
							
							temp = values.nextToken();
							temp = temp.trim();
							float writePerSec = 0;
							try
							{
								writePerSec = nf.parse(temp).floatValue();
							}
							catch(Exception e)
							{
								writePerSec = 0;
							}
							diskInfoList.add(new DiskInfo(diskName, readPerSec, writePerSec));
							//System.out.println("Disk Name " + diskName + " readPerSec " + readPerSec + " writePerSec " + writePerSec);
							sLine = st.nextToken();
							values = new StringTokenizer(sLine);
						}
						//System.out.println("**********Disk Info Ends**********");
					}
					if(sLine.trim().startsWith("Name")) // Disk Info
					{
						//System.out.println("**********Process Info starts**********");
						while(st.hasMoreTokens())
						{
							sLine = st.nextToken(); // values line
							StringTokenizer values = new StringTokenizer(sLine);
							if(values.countTokens() >= 5)
							{
								String processName = values.nextToken(); 
								String temp = values.nextToken(); // PID
								temp = temp.trim();
								int pid = 0;
								try
								{
									pid = nf.parse(temp).intValue();
								}
								catch(Exception e)
								{
									pid = 0;
								}
								
								temp = values.nextToken(); // cpu%
								temp = temp.trim();
								float cpuUsage = 0;
								try
								{
									cpuUsage = nf.parse(temp).floatValue();
								}
								catch(Exception e)
								{
									cpuUsage = 0;
								}
								
								temp = values.nextToken(); // paging space
								temp = temp.trim();
								float memUsage = 0;
								try
								{
									memUsage = nf.parse(temp).floatValue();
								}
								catch(Exception e)
								{
									memUsage = 0;
								}
								processInfoList.add(new ProcessInfo(processName, pid, (int)memUsage, cpuUsage));
								//System.out.println("processName " + processName + " pid " + pid + " memUsage " + memUsage + " cpuUsage " + cpuUsage);
							}								
						}
						//System.out.println("**********Process Info Ends**********");
					}
				}
				
				StringTokenizer st1 = new StringTokenizer(output, NEW_LINE_TOKENS);
				while (st1.hasMoreTokens())
				{
					sLine = st1.nextToken();
					if(sLine.indexOf("Faults") != -1 && sLine.indexOf("Real,MB") != -1)
					{
						StringTokenizer values = new StringTokenizer(sLine);
						String tokenValue = values.nextToken();
						while(!tokenValue.equals("Faults") && values.hasMoreTokens())
						{
							tokenValue = values.nextToken();
						}
						tokenValue = values.hasMoreTokens() ? values.nextToken():"0.0";
						
						float pageFaultRate = 0;
						try
						{
							pageFaultRate = nf.parse(tokenValue).floatValue();
						}
						catch(Exception e)
						{
							pageFaultRate = 0;
						}
						
						while(!tokenValue.equals("Real,MB") && values.hasMoreTokens())
						{
							tokenValue = values.nextToken();
						}
						tokenValue = values.hasMoreTokens() ? values.nextToken():"0";
						int totalPhysicalMem = 0;
						try
						{
							totalPhysicalMem = nf.parse(tokenValue).intValue();
						}
						catch(Exception e)
						{
							e.printStackTrace();
							totalPhysicalMem = 0;
						}
						physicalMemInfo = new MemoryInfo(totalPhysicalMem, 0, 0);
						physicalMemInfo.setPageFaultRate(pageFaultRate);
						//System.out.println("Total Physical MB " + totalPhysicalMem + " Faults " + pageFaultRate);
					}
					else if(sLine.indexOf("Size,MB") != -1)
					{
						StringTokenizer values = new StringTokenizer(sLine);
						String tokenValue = values.nextToken();
						while(!tokenValue.equals("Size,MB") && values.hasMoreTokens())
						{
							tokenValue = values.nextToken();
						}
						tokenValue = values.hasMoreTokens() ? values.nextToken():"0";
						
						int totalVirtualMem = 0;
						try
						{
							totalVirtualMem = nf.parse(tokenValue).intValue();
						}
						catch(Exception e)
						{
							e.printStackTrace();
							totalVirtualMem = 0;
						}
						
						sLine = st1.nextToken();
						values = new StringTokenizer(sLine);
						tokenValue = values.nextToken();
						while(!tokenValue.trim().equals("Used") && values.hasMoreTokens())
						{
							tokenValue = values.nextToken();
						}
						tokenValue = values.hasMoreTokens() ? values.nextToken():"0";
						
						float percentageUsed = 0;
						try
						{
							percentageUsed = nf.parse(tokenValue).floatValue();
						}
						catch(Exception e)
						{
							percentageUsed = 0;
						}
						int usedMemory = (int)(percentageUsed * totalVirtualMem / 100);
						virtualMemInfo = new MemoryInfo(totalVirtualMem, usedMemory, totalVirtualMem - usedMemory);
						//System.out.println("Total virtual MB " + totalVirtualMem + " used Mem " + usedMemory + " free mem " + (totalVirtualMem - usedMemory));
					}
				}
			}
			catch (Exception ex)
			{
				throw new ResultParsingException("Collect Data Top As command " + ex.getMessage(), output);
			}
		}
	}
*/
