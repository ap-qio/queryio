/*
 * @(#)  MacOSXParser.java Aug 23, 2005
 *
 * Copyright (C) 2002 - 2005 Exceed Consultancy Services. All Rights Reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.sysmoncommon.sysmon.parser;

import java.util.LinkedList;
import java.util.StringTokenizer;

import com.queryio.common.util.AppLogger;

import com.queryio.sysmoncommon.sysmon.AbstractDataParser;
import com.queryio.sysmoncommon.sysmon.ResultParsingException;
import com.queryio.sysmoncommon.sysmon.dstruct.AIXProcessInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NFSStat;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;
import com.queryio.sysmoncommon.sysmon.protocol.AbstractProtocolWrapper;

public class GenericMacOSXDataParser extends AbstractDataParser
{
	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#parsePrstatCommand(java.lang.String)
	 */
	public void parsePrstatCommand(final String output) throws ResultParsingException
	{
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
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#parseVmstatCommand(java.lang.String)
	 */
	public void parseVmstatCommand(final String output) throws ResultParsingException
	{
		if (output != null) // output will be null if this method gets called after disconnect
		{
			try
			{
				final StringTokenizer st = new StringTokenizer(output, "\r\n\f");
				String sLine;
				final String CPU_CORE_LINE = "Total Number Of Cores:";
				while (st.hasMoreTokens())
				{
					sLine = st.nextToken().trim();
					if (sLine.startsWith(CPU_CORE_LINE))
					{
						try
						{
							noOfCPUs = Integer.parseInt(sLine.substring(CPU_CORE_LINE.length()).trim());
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
							noOfCPUs = 1;
						}
						break;
					}
				}
			}
			catch (final Exception e)
			{
				throw new ResultParsingException("Collect Data", output);
			}
		}
	}	
	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#parseTopCommand(java.lang.String)
	 */
	public void parseTopCommand(final String output) throws ResultParsingException
	{
		String sLine = null;

		float fPacketsRead = 0;
		float fPacketsSend = 0;
		if (this.networkInfoList == null)
		{
			this.networkInfoList = new LinkedList();
		}
		else
		{
			this.networkInfoList.clear();
		}
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
			try
			{
				final StringTokenizer st = new StringTokenizer(output, "\r\n\f");
				while (st.hasMoreTokens())
				{
					sLine = st.nextToken();
					/* Networks: 395 ipkts/293K 354 opkts /62K\ */
					if (sLine.indexOf("Networks:") != -1)
					{
						/*
						sLine = sLine.substring(sLine.indexOf("Networks:"));
						final StringTokenizer line = new StringTokenizer(sLine);
						temp = line.nextToken(); // Ignore token being
						// Networks:
						temp = line.nextToken();
						temp = temp.trim();
						try
						{
							fPacketsRead = this.nf.parse(temp).floatValue();
						}
						catch (final Exception e)
						{
							fPacketsRead = 0;
						}
						temp = line.nextToken(); // Ignore token being
						// ipkts/293K

						temp = line.nextToken();
						temp = temp.trim();
						try
						{
							fPacketsSend = this.nf.parse(temp).floatValue();
						}
						catch (final Exception e)
						{
							fPacketsSend = 0;
						}
						final UnixNetworkInfo networkInfo = new UnixNetworkInfo("Network", 0, (int) fPacketsRead,
								(int) fPacketsSend);
						this.networkInfoList.add(networkInfo);
						*/
						// Networks:          3 ipkts/     0K            1 opkts /0K
						//sLine = sLine.substring(sLine.indexOf("Networks:") + "Networks:".length());
						fPacketsRead = -1.0f;
						fPacketsSend = -1.0f;
						int firstIndex = sLine.indexOf('/');
						if (firstIndex != -1)
						{
							sLine = sLine.substring(firstIndex + 1).trim();
							firstIndex = sLine.indexOf(' ');
							String sMem = sLine.substring(0, firstIndex);
							fPacketsRead = parseValue(sMem);
							


							firstIndex = sLine.indexOf('/');
							if (firstIndex != -1)
							{
								sLine = sLine.substring(firstIndex + 1).trim();
								fPacketsSend = parseValue(sLine);
							}
						}
						if(fPacketsRead != -1 && fPacketsSend != -1)
						{
							final NetworkInfo networkInfo = new NetworkInfo("Network", 0, fPacketsRead, fPacketsSend);
							this.networkInfoList.add(networkInfo);
							//break;
						}
					}
					/* Disks: 5417 reads/92754K 2196 writes/30480K\ */
					if (sLine.startsWith("Disks:"))
					{
						sLine = sLine.substring(6).trim();
						
						int firstIndex = sLine.indexOf('/');
						int secondIndex = sLine.lastIndexOf('/');
						String temp = sLine.substring(firstIndex + 1).trim();
						String reads = temp.substring(0, temp.indexOf(' '));
						String writes = sLine.substring(secondIndex + 1);
						fPacketsRead = parseValue(reads);
						fPacketsSend = parseValue(writes);
						/*
						final StringTokenizer line = new StringTokenizer(sLine);
						//temp = line.nextToken(); // Ignore token being Disks:
						temp = line.nextToken();
						temp = temp.trim();

						try
						{
							fPacketsRead = this.nf.parse(temp).floatValue();
						}
						catch (final Exception e)
						{
							e.printStackTrace();
							fPacketsRead = 0;
						}
						temp = line.nextToken(); // Ignore token being
						// reads/92754K

						temp = line.nextToken();
						temp = temp.trim();

						try
						{
							fPacketsSend = this.nf.parse(temp).floatValue();
						}
						catch (final Exception e)
						{
							fPacketsSend = 0;
						}
						*/
						final DiskInfo diskInfo = new DiskInfo("Disk", fPacketsRead, fPacketsSend);
						this.diskInfoList.add(diskInfo);
					}
					if (sLine.startsWith("VM:"))
					{
						break;
					}
				}
			}
			catch (final Exception e)
			{
				throw new ResultParsingException("Collect Data", output);
			}
		}
	}
	
	private float parseValue(String sMem)
	{
		float value = 0.0f;
		try
		{
			char ch = sMem.charAt(sMem.length() - 1);
			sMem = sMem.substring(0, sMem.length() - 1);
			value = this.nf.parse(sMem).floatValue();
			if ((ch == 'B') || (ch == 'b'))
			{
				value /= 1024;
			}
			else if ((ch == 'K') || (ch == 'k'))
			{
				// do nothing
			}
			else if ((ch == 'M') || (ch == 'g'))
			{
				value *= 1024;
			}
			else if ((ch == 'G') || (ch == 'g'))
			{
				value *= (1024*1024);
			}
		}
		catch (final Exception e)
		{
			value = 0;
		}
		return value;
	}

	public void parseNetStatCommand(final String output) throws ResultParsingException
	{

	}

	public void parseIOStatCommand(final String output) throws ResultParsingException
	{

	}

	public void parseDFCommand(final String output) throws ResultParsingException
	{
		/*
		 * df -k --- returns the disk space info for all the mounted file systems
		 * 
		 * Format in LINUX is 
		 * Filesystem 1k-blocks Used Available Use% Mounted on 
		 * /dev/hda2 18611412 3591672 14074316 21% / 
		 * /dev/hda1 46636 5956 38272 14% 
		 * /boot none 256184 0 256184 0% 
		 * /dev/shm
		 */

		/*
		 * Read the bytes and create string. Tokenize the string. Discard all
		 * the tokes till "on" token and then in next loop start reading disk
		 * space info. Every line contains exactly 6 tokens. Read all lines till
		 * the next token begins with "[userid"
		 * Filesystem    1024-blocks      Used Available Capacity  iused    ifree %iused  Mounted on
		   /dev/disk0s2    487546976  94835168 392455808    20% 23772790 98113952   20%   /
		   devfs                 184       184         0   100%      639        0  100%   /dev
		   /dev/disk1s2    487546976 172275116 315271860    36% 43068777 78817965   35%   /Volumes/Server HD
		   map -hosts              0         0         0   100%        0        0  100%   /net
		   map auto_home           0         0         0   100%        0        0  100%   /home

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
			try
			{
				String sLine = null;
				float fUsedSpace = 0;
				float fTotalSpace = 0;
				final StringTokenizer st = new StringTokenizer(output, "\n");
				while (st.hasMoreTokens())
				{
					sLine = st.nextToken();
					/*
					 * Memory: 256M real, 170M free, 44M swap in use, 651M swap
					 * free
					 */
					if (sLine.startsWith("Filesystem"))
					{
						continue;
					}
					if (sLine.startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT))
					{
						break;
					}

					final StringTokenizer line = new StringTokenizer(sLine);
					String sFileSystemName = line.nextToken();

					while (line.hasMoreTokens())
					{
						final String sTotalSpace = line.nextToken();
						try
						{
							fTotalSpace = (float) Integer.parseInt(sTotalSpace) / 1024;
						}
						catch (final Exception e)
						{
							sFileSystemName += sTotalSpace;
							continue;
						}
						final String sUsedSpace = line.nextToken();
						try
						{
							fUsedSpace = (float) Integer.parseInt(sUsedSpace) / 1024;
						}
						catch (final Exception e)
						{
							fUsedSpace = 0;
						}
						// discard the FreeSpace info
						line.nextToken();
						// discard Use% token
						line.nextToken();
						// discard Mounted on token
						line.nextToken();
					}
					final DriveInfo dInfo = new DriveInfo(sFileSystemName, fTotalSpace, fUsedSpace);
					this.diskSpaceInfoList.add(dInfo);
				}
			}
			catch (final Exception ex)
			{
				ex.printStackTrace();
				AppLogger.getLogger().fatal(ex.getMessage());
				throw new ResultParsingException("Disk Space Info " + ex.getMessage(), output);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#parseTopMemCommand(java.lang.String)
	 */
	public void parseTopMemCommand(final String output) throws ResultParsingException
	{
		String temp = null;
		String sLine = null;
		//float idleCpuTime;

		int usedMemory = 0;
		int freeMemory = 0;

		boolean bProcesses = false;
		ProcessInfo pInfo = null;
		int iPID = 0;
		String sProcessName = null;
		float fCpuUsage = 0;
		int iProcessMemory = 0;
		int iThreads = 0;
		String sMem = null;
		/*
		 * Read the bytes and create string. Tokenize the string. Discard all
		 * the tokes till "%idle" token and then read 4th token.
		 */
		if (output != null) // output will be null if this method gets called
		// after disconnect
		{
			try
			{
				final StringTokenizer st = new StringTokenizer(output, "\n");
				while (st.hasMoreTokens())
				{
					sLine = st.nextToken();
					if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("SLINE: " + sLine);
					/*
					 * Load Avg: 0.21, 0.12, 0.05 CPU usage: 2.9% user, 7.6% sys, 89.5% idle
					 * Load Avg:  0.01,  0.02,  0.09    CPU usage:  0.12% user,  0.25% sys, 99.63% idle
					 */
					if (sLine.startsWith("Load Avg:"))
					{
						String lowercase = sLine.toLowerCase();
						int index = lowercase.indexOf("cpu usage:");
						
						String cpuUsage = sLine.substring(index).trim();
						cpuUsage = cpuUsage.substring("CPU usage:".length());
						
						String loadAvg = sLine.substring(0, index).trim();
						loadAvg = loadAvg.substring("Load Avg:".length());
						float value = 0.0f;
						StringTokenizer line = new StringTokenizer(loadAvg, ",");
						temp = line.nextToken().trim();
						try
						{
							value = this.nf.parse(temp).floatValue();
						}
						catch (final Exception e)
						{
							value = 0.0f;
						}						
						f1MinLoadAvg = value;
						temp = line.nextToken().trim();
						try
						{
							value = this.nf.parse(temp).floatValue();
						}
						catch (final Exception e)
						{
							value = 0.0f;
						}						
						f5MinLoadAvg = value;
						temp = line.nextToken().trim();
						try
						{
							value = this.nf.parse(temp).floatValue();
						}
						catch (final Exception e)
						{
							value = 0.0f;
						}						
						f15MinLoadAvg = value;
						
						
						line = new StringTokenizer(cpuUsage);
						while (line.hasMoreTokens())
						{
							temp = line.nextToken();
							if (temp.indexOf("%") != -1)
							{
								temp = temp.substring(0, temp.indexOf("%"));
								try
								{
									value = this.nf.parse(temp).floatValue();
								}
								catch (final Exception e)
								{
									value = -1.0f;
								}
							}
							if (temp.indexOf("user") != -1)
							{
								this.fUserCPUUsageTime = value != -1.0f ? value : 0.0f;
							}
							if (temp.indexOf("sys") != -1)
							{
								this.fSysCPUUsageTime = value != -1.0f ? value : 0.0f;
							}
							if(temp.indexOf("idle") != -1)
							{
								this.fCpuUsageTime = value != -1.0f ? (100.0f - value) : 0.0f;
							}
						}
						/*
						final StringTokenizer line = new StringTokenizer(sLine);
						while (line.hasMoreTokens())
						{
							temp = line.nextToken();
							if (temp.indexOf("sys") != -1)
							{
								temp = line.nextToken();
								temp = temp.trim();
								if (temp.indexOf("%") != -1)
								{
									temp = temp.substring(0, temp.indexOf("%"));
								}
								try
								{
									idleCpuTime = this.nf.parse(temp).floatValue();
								}
								catch (final Exception e)
								{
									idleCpuTime = 100;
								}
								this.fCpuUsageTime = 100 - idleCpuTime;
							}
						}
						*/
					}
					/*
					 * PhysMem: 36.0M wired, 74.1M active, 42.0M inactive, 152M
					 * used, 103M free
					 */
					if (sLine.startsWith("PhysMem:"))
					{
						final StringTokenizer line = new StringTokenizer(sLine);
						while (line.hasMoreTokens())
						{
							temp = line.nextToken();
							if (temp.indexOf("inactive,") != -1)
							{
								temp = line.nextToken();
								temp = temp.trim();
								sMem = temp.substring(0, temp.length() - 1);
								try
								{
									usedMemory = Math.round(this.nf.parse(sMem).floatValue());
									final char ch = temp.charAt(temp.length() - 1);
									if ((ch == 'G') || (ch == 'g'))
									{
										usedMemory *= 1024;
									}
								}
								catch (final Exception e)
								{
									usedMemory = 0;
								}
								temp = line.nextToken(); // Ignore as it is
								// String used,

								temp = line.nextToken();
								temp = temp.trim();
								sMem = temp.substring(0, temp.length() - 1);
								try
								{
									freeMemory = Math.round(this.nf.parse(temp).floatValue());
									final char ch = temp.charAt(temp.length() - 1);
									if ((ch == 'G') || (ch == 'g'))
									{
										freeMemory *= 1024;
									}
								}
								catch (final Exception e)
								{
									freeMemory = 0;
								}
								this.physicalMemInfo = new MemoryInfo((usedMemory + freeMemory), usedMemory, freeMemory);
							}
						}
					}
					/* VM: 1.97G + 60.8M 10659(0) pageins, 0(0) pageouts */
					if (sLine.startsWith("VM:"))
					{
						final StringTokenizer line = new StringTokenizer(sLine);
						temp = line.nextToken(); // Ignore token being VM:
						temp = line.nextToken();
						temp = temp.trim(); // 1.97G + 60.8M
						sMem = temp.substring(0, temp.length() - 1);
						try
						{
							freeMemory = Math.round(this.nf.parse(temp).floatValue());
							final char ch = temp.charAt(temp.length() - 1);
							if ((ch == 'G') || (ch == 'g'))
							{
								freeMemory *= 1024;
							}
						}
						catch (final Exception e)
						{
							freeMemory = 0;
						}

						temp = line.nextToken(); // +
						temp = line.nextToken(); // 60.8M
						temp = temp.trim(); // 60.8M
						sMem = temp.substring(0, temp.length() - 1);
						try
						{
							usedMemory = Math.round(this.nf.parse(temp).floatValue());
							final char ch = temp.charAt(temp.length() - 1);
							if ((ch == 'G') || (ch == 'g'))
							{
								usedMemory *= 1024;
							}
						}
						catch (final Exception e)
						{
							usedMemory = 0;
						}
						this.virtualMemInfo = new MemoryInfo(freeMemory, usedMemory, (freeMemory - usedMemory));
					}
					/* 335 top 3.8% 0:00.44 1 16 26 252K 464K 628K 27.1M */
					if (bProcesses)
					{
						final StringTokenizer line = new StringTokenizer(sLine);

						temp = line.nextToken(); // PID:
						try
						{
							iPID = this.nf.parse(temp).intValue();
						}
						catch (final Exception e)
						{
							iPID = 0;
						}
						sProcessName = line.nextToken(); // COMMAND

						temp = line.nextToken();
						temp = temp.trim();
						if (temp.endsWith("%"))
						{
							temp = temp.substring(0, temp.length() - 1);
						}
						try
						{
							fCpuUsage = this.nf.parse(temp).floatValue();
						}
						catch (final Exception e)
						{
							fCpuUsage = 0;
						}
						line.nextToken(); // Ignore TIME:
						
						temp = line.nextToken(); // Ignore #TH
						temp = temp.trim();
						
						try
						{
							iThreads = Integer.parseInt(temp);
						}
						catch (final Exception e)
						{
							iThreads = 0;
						}
						
						line.nextToken(); // Ignore #PRTS
						line.nextToken(); // Ignore #MREGS
						line.nextToken(); // Ignore RPRVT
						line.nextToken(); // Ignore RSHRD
						temp = line.nextToken(); // RSIZE:
						temp = temp.trim();
						sMem = temp.substring(0, temp.length() - 1);
						try
						{
							iProcessMemory = Math.round(this.nf.parse(temp).floatValue());
							final char ch = temp.charAt(temp.length() - 1);
							if ((ch == 'M') || (ch == 'm'))
							{
								iProcessMemory *= 1024;
							}
						}
						catch (final Exception e)
						{
							iProcessMemory = 0;
						}
						pInfo = new ProcessInfo(sProcessName, iPID, iProcessMemory, fCpuUsage, iProcessMemory, iThreads);
						this.processInfoList.add(pInfo);
					}
					/*
					 * PID COMMAND %CPU TIME #TH #PRTS #MREGS RPRVT RSHRD RSIZE
					 * VSIZE
					 */
					if (sLine.indexOf("PID COMMAND") != -1)
					{
						bProcesses = true;
						if (this.processInfoList == null)
						{
							this.processInfoList = new LinkedList();
						}
						else
						{
							this.processInfoList.clear();
						}
					}
				}
			}
			catch (final Exception ex)
			{
				AppLogger.getLogger().fatal(ex.getMessage(), ex);
				throw new ResultParsingException("Collect Data", output + ex.getMessage());
			}
		}
	}

	public void parseNSFStatCommand(String clientStats, String serverStats)
    {
        if (this.nfsStats == null)
        {
            this.nfsStats = new NFSStat();
        }
        
        if (clientStats != null && clientStats.trim().length() > 0)
        {
            /* Format of the clientStats
             * 
             * @formatter:off
                    Client Info:
                    RPC Counts:
                      Getattr   Setattr    Lookup  Readlink      Read     Write    Create    Remove
                         6480       493    112205         0     16577    160253      7790      7743
                       Rename      Link   Symlink     Mkdir     Rmdir   Readdir  RdirPlus    Access
                           42         2         0        18         2     10030         0    145815
                        Mknod    Fsstat    Fsinfo  PathConf    Commit
                            0      1328         4         2         0
                    RPC Info:
                     TimedOut   Invalid X Replies   Retries  Requests
                            0         0         0         0    468788
                    Cache Info:
                    Attr Hits    Misses Lkup Hits    Misses BioR Hits    Misses BioW Hits    Misses
                       408938     37912     13625     97102     36569      8881       981    160253
                    BioRLHits    Misses BioD Hits    Misses DirE Hits    Misses
                            0         0        93     10093     10123         0


            @formatter:on
                 */
            String[] nfsstatsLines = split(clientStats, NEW_LINE_PATTERN, 0);
            String stats = null;
            for (int i = 0; i < nfsstatsLines.length; i++)
            {
                
                if (nfsstatsLines[i].startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT))
                {
                    break;
                }
                nfsstatsLines[i] = nfsstatsLines[i].trim();
                if (nfsstatsLines[i].length() == 0)
                {
                    continue;
                }
                
                if (nfsstatsLines[i].startsWith("RPC Counts"))
                {
                    /*
                     * RPC Counts:
                      Getattr   Setattr    Lookup  Readlink      Read     Write    Create    Remove
                         6480       493    112205         0     16577    160253      7790      7743
                       Rename      Link   Symlink     Mkdir     Rmdir   Readdir  RdirPlus    Access
                           42         2         0        18         2     10030         0    145815
                        Mknod    Fsstat    Fsinfo  PathConf    Commit
                            0      1328         4         2         0
                     */
                    stats = "RPC Counts";
                    
                    for (int k = 0; k < 3; k++)
                    {
                        if (i+2 < nfsstatsLines.length)
                        {
                            ++i;
                            String[] attrs = split(nfsstatsLines[i], WHITE_SPACE_PATTERN, 0);
                            ++i;
                            String[] values = split(nfsstatsLines[i], WHITE_SPACE_PATTERN, 0);
                            
                            int length = attrs.length > values.length ? values.length : attrs.length;
                            for (int j = 0; j < length; j++)
                            {
                                nfsStats.setClientStatsValue(stats, attrs[j], new Long(values[j]));
                            }
                        }
                    }
                }
                else if (nfsstatsLines[i].startsWith("RPC Info"))
                {
                    /*
                     * RPC Info:
                     TimedOut   Invalid X Replies   Retries  Requests
                            0         0         0         0    468788
                     */
                    stats = "RPC Info";
                    if (i+2 < nfsstatsLines.length)
                    {
                        ++i;
                        String[] attrs = split(nfsstatsLines[i], WHITE_SPACE_PATTERN, 0);
                        ++i;
                        String[] values = split(nfsstatsLines[i], WHITE_SPACE_PATTERN, 0);
                        boolean prependX = false;
                        for (int j = 0, k=0; j < attrs.length && k < values.length; j++, k++)
                        {
                            String attr = attrs[j];
                            if (j == 2 && attr.equals("X"))
                            {
                                prependX = true;
                                ++j;
                            }
                            if (prependX)
                            {
                                attr = "X " + attrs[j];
                            }
                            nfsStats.setClientStatsValue(stats, attr, new Long(values[k]));
                        }
                    }
                }
                else if (nfsstatsLines[i].startsWith("Cache Info"))
                {
                    /*
                     * Cache Info:
                        Attr Hits    Misses Lkup Hits    Misses BioR Hits    Misses BioW Hits    Misses
                           408938     37912     13625     97102     36569      8881       981    160253
                        BioRLHits    Misses BioD Hits    Misses DirE Hits    Misses
                                0         0        93     10093     10123         0
                     */
                    stats = "Cache Info";
                    for (int l = 0; l < 2; l++)
                    {
                        if (i+2 < nfsstatsLines.length)
                        {
                            ++i;
                            String[] attrs = split(nfsstatsLines[i], WHITE_SPACE_PATTERN, 0);
                            ++i;
                            String[] values = split(nfsstatsLines[i], WHITE_SPACE_PATTERN, 0);
                            String group = null;
                            for (int j = 0, k=0; j < attrs.length && k < values.length; j++, k++)
                            {
                                String attr = attrs[j];
                                if (attr.startsWith("BioRL"))
                                {
                                    group = "BioRL";
                                    attr = "Hits";
                                }
                                else if (attr.equals("Attr") || attr.equals("Lkup") || attr.equals("BioR") || attr.equals("BioW") 
                                        || attr.equals("BioD") || attr.equals("DirE"))
                                {
                                    group = attr;
                                    ++j;
                                    attr = attrs[j];
                                }
                                if (group != null)
                                {
                                    attr = group + " " + attr;
                                }
                                nfsStats.setClientStatsValue(stats, attr, new Long(values[k]));
                            }
                        }
                    }
                }
            }
        }
        
        if (serverStats != null && serverStats.trim().length() > 0)
        {
            /*
             * Server Info:
                RPC Counts:
                  Getattr   Setattr    Lookup  Readlink      Read     Write    Create    Remove
                        0         0         0         0         0         0         0         0
                   Rename      Link   Symlink     Mkdir     Rmdir   Readdir  RdirPlus    Access
                        0         0         0         0         0         0         0         0
                    Mknod    Fsstat    Fsinfo  PathConf    Commit
                        0         0         0         0         0
                Server Ret-Failed
                                0
                Server Faults
                            0
                Server Cache Stats:
                   Inprog      Idem  Non-idem    Misses
                        0         0         0         0
                Server Write Gathering:
                 WriteOps  WriteRPC   Opsaved
                        0         0         0

             */
            String[] nfsstatsLines = split(serverStats, NEW_LINE_PATTERN, 0);
            String stats = null;
            for (int i = 0; i < nfsstatsLines.length; i++)
            {
                
                if (nfsstatsLines[i].startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT))
                {
                    break;
                }
                nfsstatsLines[i] = nfsstatsLines[i].trim();
                if (nfsstatsLines[i].length() == 0)
                {
                    continue;
                }
                
                if (nfsstatsLines[i].startsWith("RPC Counts"))
                {
                    /*
                     * RPC Counts:
                      Getattr   Setattr    Lookup  Readlink      Read     Write    Create    Remove
                            0         0         0         0         0         0         0         0
                       Rename      Link   Symlink     Mkdir     Rmdir   Readdir  RdirPlus    Access
                            0         0         0         0         0         0         0         0
                        Mknod    Fsstat    Fsinfo  PathConf    Commit
                            0         0         0         0         0
                     */
                    stats = "RPC Counts";
                    
                    for (int k = 0; k < 3; k++)
                    {
                        if (i+2 < nfsstatsLines.length)
                        {
                            ++i;
                            String[] attrs = split(nfsstatsLines[i], WHITE_SPACE_PATTERN, 0);
                            ++i;
                            String[] values = split(nfsstatsLines[i], WHITE_SPACE_PATTERN, 0);
                            
                            int length = attrs.length > values.length ? values.length : attrs.length;
                            for (int j = 0; j < length; j++)
                            {
                                nfsStats.setServerStatsValue(stats, attrs[j], new Long(values[j]));
                            }
                        }
                    }
                }
                else if (nfsstatsLines[i].startsWith("Server Ret-Failed"))
                {
                    /*
                     * Server Ret-Failed
                                0
                     */
                    stats = "Server Ret-Failed";
                    ++i;
                    if (i < nfsstatsLines.length)
                    {
                        nfsStats.setServerStatsValue(stats, stats, new Long(nfsstatsLines[i].trim()));
                    }
                }
                else if (nfsstatsLines[i].startsWith("Server Faults"))
                {
                    /*
                     * Server Faults
                            0
                     */
                    stats = "Server Faults";
                    ++i;
                    if (i < nfsstatsLines.length)
                    {
                        nfsStats.setServerStatsValue(stats, stats, new Long(nfsstatsLines[i].trim()));
                    }
                }
                else if (nfsstatsLines[i].startsWith("Server Cache Stats"))
                {
                    /*
                     * Server Cache Stats:
                       Inprog      Idem  Non-idem    Misses
                            0         0         0         0
                     */
                    stats = "Server Cache Stats";
                    if (i+2 < nfsstatsLines.length)
                    {
                        ++i;
                        String[] attrs = split(nfsstatsLines[i], WHITE_SPACE_PATTERN, 0);
                        ++i;
                        String[] values = split(nfsstatsLines[i], WHITE_SPACE_PATTERN, 0);
                        int length = attrs.length > values.length ? values.length : attrs.length;
                        for (int j = 0; j < length; j++)
                        {
                            nfsStats.setServerStatsValue(stats, attrs[j], new Long(values[j]));
                        }
                    }
                }
                else if (nfsstatsLines[i].startsWith("Server Write Gathering"))
                {
                    /*
                     * Server Write Gathering:
                         WriteOps  WriteRPC   Opsaved
                                0         0         0
                     */
                    stats = "Server Write Gathering";
                    if (i+2 < nfsstatsLines.length)
                    {
                        ++i;
                        String[] attrs = split(nfsstatsLines[i], WHITE_SPACE_PATTERN, 0);
                        ++i;
                        String[] values = split(nfsstatsLines[i], WHITE_SPACE_PATTERN, 0);
                        int length = attrs.length > values.length ? values.length : attrs.length;
                        for (int j = 0; j < length; j++)
                        {
                            nfsStats.setServerStatsValue(stats, attrs[j], new Long(values[j]));
                        }
                    }
                }
            }
        }
    }
}
