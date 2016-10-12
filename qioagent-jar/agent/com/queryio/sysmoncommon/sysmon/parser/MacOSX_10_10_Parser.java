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

import com.queryio.sysmoncommon.sysmon.ResultParsingException;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;

public class MacOSX_10_10_Parser extends GenericMacOSXDataParser
{

	/**
	 * @see com.appperfect.common.sysmon.AbstractDataParser#parseTopMemCommand(java.lang.String)
	 */
	public void parseTopMemCommand(final String output) throws ResultParsingException
	{
		/*
		 * 	Processes: 264 total, 3 running, 10 stuck, 251 sleeping, 1226 threads 
			2015/04/13 17:17:48
			Load Avg: 1.00, 1.22, 1.24 
			CPU usage: 4.25% user, 23.40% sys, 72.34% idle 
			SharedLibs: 14M resident, 16M data, 0B linkedit.
			MemRegions: 120242 total, 3423M resident, 82M private, 485M shared.
			PhysMem: 7711M used (1129M wired), 480M unused.
			VM: 693G vsize, 1068M framework vsize, 680833(0) swapins, 842308(0) swapouts.
			Networks: packets: 0/0B in, 0/0B out.
			Disks: 0/0B read, 0/0B written.
			
		    PID  COMMAND    %CPU TIME     #TH  #WQ #PORTS  MEM  PURG   CMPRS PGRP PPID STATE    BOOSTS %CPU_ME %CPU_OTHRS UID FAULTS    COW    MSGSENT   MSGRECV  SYSBSD    SYSMACH   CSW       PAGEINS  IDLEW   POWER USER #MREGS RPRVT VPRVT VSIZE KPRVT KSHRD
		    257  top		0.0  39:57.69 6    4   64      44M  9284K  146M  257  1    sleeping *0[1]  0.00000 0.00000    0   18259836  87428  10303511  5071614  38273196  11351059  16518508  1715118  162466  0.0   root N/A    N/A   N/A   N/A   N/A   N/A  

		 */
		String temp = null;
		String sLine = null;
		float value = 0.0f;
		
		int usedMemory = -1;
		int freeMemory = -1;
		
		float fMBRead = 0;
		float fMBSend = 0;		

		boolean bProcesses = false;
		ProcessInfo pInfo = null;
		int iPID = 0;
		String sProcessName = null;
		float fCpuUsage = 0;
		int iProcessMemory = 0;
		int iThreads = 0;
		String sMem = null;		
		
		this.fUserCPUUsageTime = 0;
		this.fSysCPUUsageTime = 0;
		this.fCpuUsageTime = 0;
		this.fWaitCPUUsageTime = 0;
		
		this.f1MinLoadAvg = 0;
		this.f5MinLoadAvg = 0;
		this.f15MinLoadAvg = 0;
				
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
		/*
		 * Read the bytes and create string. 
		 */
		if (output != null) // output will be null if this method gets called after disconnect
		{
			try
			{
				final StringTokenizer st = new StringTokenizer(output, "\r\n\f");
				while (st.hasMoreTokens())
				{
					sLine = st.nextToken();
					//Processes: 57 total, 3 running, 54 sleeping, 340 threads 
					if (sLine.startsWith("Processes:"))
		            {
					    sLine = sLine.substring("Processes:".length());
		                final StringTokenizer line = new StringTokenizer(sLine, " ");
		                temp = line.nextToken().trim();
		                try
		                {
		                    noOfProcs = nf.parse(temp).intValue();
		                }
		                catch (final Exception e)
		                {
		                    noOfProcs = 0;
		                }
		            }
					/*
					 * Load Avg: 0.32, 0.34, 0.31 
					 */
					if (sLine.startsWith("Load Avg:"))
					{
						sLine = sLine.substring(sLine.indexOf("Load Avg:") + "Load Avg:".length());
						final StringTokenizer line = new StringTokenizer(sLine, ",");
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
					}
					/*
					 * CPU usage: 3.36% user, 3.84% sys, 92.78% idle
					 */
					else if (sLine.indexOf("CPU usage:") != -1)
					{						
						sLine = sLine.substring(sLine.indexOf("CPU usage:") + "CPU usage:".length());
						final StringTokenizer line = new StringTokenizer(sLine);
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
					}
					/*
					 * OLD : PhysMem: 850M wired, 1149M active, 444M inactive, 2444M used, 1654M free.
					 * NEW : PhysMem: 8167M used (1181M wired), 23M unused.
					 * 	
					 */
					else if (sLine.startsWith("PhysMem:"))
					{
						sLine=sLine.replace("PhysMem: ", "");
						String toreplace= sLine.substring(sLine.indexOf("("), sLine.indexOf(")")+1);
						sLine=sLine.replace(toreplace, "");
						final StringTokenizer line = new StringTokenizer(sLine,",");
						freeMemory = -1; 
						usedMemory = -1;				
						while (line.hasMoreTokens())
						{
							temp = line.nextToken();
							if(temp.toLowerCase().contains("m") || temp.toLowerCase().contains("g") 
									|| temp.toLowerCase().contains("k"))
							{
								sMem = temp.substring(0, temp.length() - 1).trim();
								try
								{
									value = this.nf.parse(sMem).floatValue();
									final char ch = temp.charAt(temp.length() - 1);
									if ((ch == 'G') || (ch == 'g'))
									{
										value *= 1024;
									}
								}
								catch (final Exception e)
								{
									value = 0;
								}								
							}
							if (temp.indexOf(" used") != -1)
							{
								usedMemory = Math.round(value);
							}
							if (temp.indexOf("unused") != -1)
							{
								freeMemory = Math.round(value);
							}							
							if(usedMemory != -1 && freeMemory != -1)
							{
								this.physicalMemInfo = new MemoryInfo((usedMemory + freeMemory), usedMemory, freeMemory);
								break;
							}
						}
					}
					/*
					 * VM: 128G vsize, 1035M framework vsize, 191836(0) pageins, 0(0) pageouts.
					 * VM: 293G vsize, 1065M framework vsize, 0(0) swapins, 0(0) swapouts.
					 */
					else if (sLine.startsWith("VM:"))
					{
						sLine = sLine.substring(sLine.indexOf("VM:") + "VM:".length());
						final StringTokenizer line = new StringTokenizer(sLine,",");
						freeMemory = -1; 
						usedMemory = -1;
						while (line.hasMoreTokens())
						{
							
							temp = line.nextToken();
							String temp2 = temp;
							if(temp2.contains("framework vsize"))
								temp2=temp2.replaceAll("framework vsize", "");
							else if(temp2.contains("vsize"))
								temp2=temp2.replaceAll("vsize", "");
							if(temp2.toLowerCase().contains("m ") || temp2.toLowerCase().contains("g ") 
									|| temp2.toLowerCase().contains("k "))
							{
								temp2 = temp2.trim();
								sMem = temp2.substring(0, temp2.length() - 1);
								try
								{
									value = this.nf.parse(sMem).floatValue();
									final char ch = temp2.charAt(temp2.length() - 1);
									if ((ch == 'G') || (ch == 'g'))
									{
										value *= 1024;
									}
								}
								catch (final Exception e)
								{
									value = 0;
								}								
							}		
							if (temp.indexOf("vsize") != -1 && temp.indexOf("framework") == -1)
							{
								freeMemory = Math.round(value);
							}
							if (temp.indexOf("framework") != -1)
							{
								usedMemory = Math.round(value);
							}							
							if(usedMemory != -1 && freeMemory != -1)
							{
								this.virtualMemInfo = new MemoryInfo(freeMemory, usedMemory, (freeMemory - usedMemory));
								break;
							}		
						}
					}
					/*
					 * OLD : Networks: packets: 12248403/6949M in, 1614032/465M out.
					 * NEW : Networks: packets: 24/8265B in, 31/4855B out.

					 */
					else if (sLine.startsWith("Networks:"))
					{
						final StringTokenizer line = new StringTokenizer(sLine);
						fMBRead = -1.0f;
						fMBSend = -1.0f;						
						while (line.hasMoreTokens())
						{
							temp = line.nextToken();
							if(temp.indexOf('/') != -1)
							{
								temp = temp.substring(temp.indexOf('/')  + 1).trim();
								sMem = temp.substring(0, temp.length() - 1);
								try
								{
									value = this.nf.parse(sMem).floatValue();
									final char ch = temp.charAt(temp.length() - 1);
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
							}
							if(temp.indexOf("in") != -1)
							{
								
								fMBRead = (value != -1) ? value: 0.0f;
							}
							if(temp.indexOf("out") != -1)
							{
								fMBSend = (value != -1) ? value: 0.0f;
							}
							if(fMBRead != -1 && fMBSend != -1)
							{
								final NetworkInfo networkInfo = new NetworkInfo("Network", 0, fMBRead, fMBSend);
								this.networkInfoList.add(networkInfo);
								break;
							}
						}
					}					
					/*
					 * Disks: 248192/3528M read, 359869/5724M written.
					 */
					else if (sLine.startsWith("Disks:"))
					{
						sLine = sLine.substring(sLine.indexOf("Disks:") + "Disks:".length());
						final StringTokenizer line = new StringTokenizer(sLine);
						fMBRead = -1.0f;
						fMBSend = -1.0f;						
						while (line.hasMoreTokens())
						{
							temp = line.nextToken();
							if(temp.indexOf('/') != -1)
							{
								temp = temp.substring(temp.indexOf('/')  + 1).trim();
								sMem = temp.substring(0, temp.length() - 1);
								try
								{
									value = this.nf.parse(sMem).floatValue();
									final char ch = temp.charAt(temp.length() - 1);
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
							}
							if(temp.indexOf("read") != -1)
							{
								fMBRead = value;
								//fMBRead = (fDiskMBSend != -1) ? (value - fDiskMBSend) : 0.0f;
								//fDiskMBSend = value;
							}
							if(temp.indexOf("writ") != -1)
							{
								fMBSend = value;
								//fMBSend = (fDiskMBRead != -1) ? (value - fDiskMBRead) : 0.0f;
								//fDiskMBRead = value;								
							}
							if(fMBRead != -1 && fMBSend != -1)
							{
								final DiskInfo diskInfo = new DiskInfo("Disk", fMBRead, fMBSend);
								this.diskInfoList.add(diskInfo);						
								break;
							}
						}
					}
					/*
					 * 
					    PID  COMMAND    %CPU TIME     #TH  #WQ #PORTS  MEM  PURG   CMPRS PGRP PPID STATE    BOOSTS %CPU_ME %CPU_OTHRS UID FAULTS    COW    MSGSENT   MSGRECV  SYSBSD    SYSMACH   CSW       PAGEINS  IDLEW   POWER USER #MREGS RPRVT VPRVT VSIZE KPRVT KSHRD
					    257  top		0.0  39:57.69 6    4   64      44M  9284K  146M  257  1    sleeping *0[1]  0.00000 0.00000    0   18259836  87428  10303511  5071614  38273196  11351059  16518508  1715118  162466  0.0   root N/A    N/A   N/A   N/A   N/A   N/A  
					 */

					else if (bProcesses)
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
						try
						{
							fCpuUsage = this.nf.parse(temp).floatValue();
							fCpuUsage = Math.abs(fCpuUsage);
						}
						catch (final Exception e)
						{
							fCpuUsage = 0;
						}
						line.nextToken(); // Ignore CPU TIME:
						temp = line.nextToken(); // #TH
						temp = temp.trim();						
						try
						{
							iThreads = Integer.parseInt(temp);
							iThreads = Math.abs(iThreads);
						}
						catch (final Exception e)
						{
							iThreads = 0;
						}
						line.nextToken(); // Ignore #WQ
						line.nextToken(); // Ignore #PORTS
						temp = line.nextToken(); // MEM
						temp = temp.trim();
						sMem = temp.substring(0, temp.length() - 1);
						try
						{
							iProcessMemory = Math.round(this.nf.parse(temp).floatValue());
							iProcessMemory = Math.abs(iProcessMemory);
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
					else if (sLine.indexOf("PID") != -1 && sLine.indexOf("COMMAND") != -1 && this.monitorProcesses)
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
				ex.printStackTrace();
				throw new ResultParsingException("Collect Data", output + ex.getMessage());
			}
		}
	}
}
