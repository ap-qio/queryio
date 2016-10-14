/*
 * @(#)  GenericLinuxDataParser.java Aug 24, 2005
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.StringTokenizer;

import com.queryio.sysmoncommon.sysmon.AbstractDataParser;
import com.queryio.sysmoncommon.sysmon.ResultParsingException;
import com.queryio.sysmoncommon.sysmon.dstruct.AIXProcessInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.UnixNetworkInfo;
import com.queryio.sysmoncommon.sysmon.protocol.AbstractProtocolWrapper;

public class GenericLinuxDataParser extends AbstractDataParser
{
	private static final String NEW_LINE_TOKENS = "\r\n\f"; //$NON-NLS-1$

	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#parseTopMemCommand(java.lang.String)
	 */
	public void parseTopMemCommand(final String output) throws ResultParsingException
	{
		//DO NOTHING
	}
	
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
		//DO NOTHING
	}	
	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#parseTopCommand(java.lang.String)
	 */
	public void parseTopCommand(final String output) throws ResultParsingException
	{
		/*
		 * Output of "top -c" command on Redhat Linux 9
		 * 
		 * 05:53:14 up 54 min, 2 users, load average: 0.72, 0.27, 0.17 80
		 * processes: 77 sleeping, 3 running, 0 zombie, 0 stopped CPU states:
		 * cpu user nice system irq softirq iowait idle total 5.7% 0.3% 3.3%
		 * 0.1% 0.0% 5.5% 84.6% Mem: 512480k av, 424596k used, 87884k free, 0k
		 * shrd, 18844k buff 363940k active, 20740k inactive Swap: 2048276k av,
		 * 0k used, 2048276k free 180528k cached
		 * 
		 * PID USER PRI NI SIZE RSS SHARE STAT %CPU %MEM TIME CPU COMMAND 1838
		 * root 15 0 85524 18M 5920 S 2.7 3.7 1:11 0 X 4564 root 15 0 11740 11M
		 * 7904 R 2.3 2.2 0:04 0 gnome-termina
		 */

		/*
		 * Output of "top -c" command on Redhat Linux 7.2
		 * 
		 * 6:21am up 23:19, 2 users, load average: 0.06, 0.08, 0.18 75
		 * processes: 74 sleeping, 1 running, 0 zombie, 0 stopped CPU states:
		 * 0.9% user, 8.6% system, 0.0% nice, 90.3% idle Mem: 512364K av,
		 * 456112K used, 56252K free, 56K shrd, 140636K buff Swap: 2048276K av,
		 * 0K used, 2048276K free 57100K cached
		 * 
		 * PID USER PRI NI SIZE RSS SHARE STAT %CPU %MEM TIME COMMAND 29530 root
		 * 15 0 5084 5084 3580 S 7.1 0.9 3:37 gtop 689 root 14 0 1068 1068 836 R
		 * 1.1 0.2 0:07 top
		 */

		/*
		 * Output of top command on SuSE Linux 9.3
		 * 
		 * top - 14:19:53 up 62 days, 3:35, 14 users, load average: 0.01, 0.02,
		 * 0.00 Tasks: 102 total, 7 running, 93 sleeping, 0 stopped, 2 zombie
		 * Cpu(s): 0.3% user, 0.1% system, 0.0% nice, 99.6% idle Mem: 514736k
		 * total, 497232k used, 17504k free, 56024k buffers Swap: 1794736k
		 * total, 104544k used, 1690192k free, 235872k cached
		 * 
		 * PID USER PR NI VIRT RES SHR S %CPU %MEM TIME+ Command 1426 root 15 0
		 * 116m 41m 18m S 1.0 8.2 82:30.34 X 20836 jj 15 0 820 820 612 R 1.0 0.2
		 * 0:00.03 top
		 */

		/*
		 * top - 09:59:27 up 45 days, 17:17, 3 users, load average: 1.28, 0.84,
		 * 0.52 Tasks: 89 total, 1 running, 88 sleeping, 0 stopped, 0 zombie
		 * Cpu(s): 27.0% us, 7.4% sy, 0.0% ni, 65.1% id, 0.0% wa, 0.0% hi, 0.5%
		 * si Mem: 2595672k total, 2315348k used, 280324k free, 191784k buffers
		 * Swap: 1052248k total, 8k used, 1052240k free, 1088716k cached
		 * 
		 * PID USER PR NI VIRT RES SHR S %CPU %MEM TIME+ COMMAND 647 mysql 15 0
		 * 34764 15m 5040 S 2.0 0.6 0:00.08 mysqld 672 root 15 0 1952 1040 1716
		 * R 1.0 0.0 0:00.04 top
		 */

		boolean bLinux9 = false;
		float idleCpuTime = -1;

		int usedMemory = 0;
		int freeMemory = 0;

		boolean bProcesses = false;
		ProcessInfo pInfo = null;
		int iPID = 0;
		String sProcessName = null;
		String sUserName = null;
		float fCpuUsage = 0;
		int iProcessMemory = 0;

		/*
		 * Read the bytes and create string. Tokenize the string. Discard all
		 * the tokes till "%idle" token and then read 4th token.
		 */
		if (output != null) // output will be null if this method gets called
		// after disconnect
		{
			try
			{
				// boolean bSecond = false;
				String temp = null;
				String sLine = null;

				final StringTokenizer st = new StringTokenizer(output, NEW_LINE_TOKENS);
				while (st.hasMoreTokens())
				{
					sLine = st.nextToken();
					/*
					 * In case of Linux 9 CPU states: cpu user nice system irq
					 * softirq iowait idle total 5.7% 0.3% 3.3% 0.1% 0.0% 5.5%
					 * 84.6%
					 */
					if (sLine.toLowerCase().startsWith("cpu")
							&& ((sLine.indexOf("idle") != -1) || (sLine.indexOf("id,") != -1)))
					{
						final StringTokenizer line = new StringTokenizer(sLine, " \t,");
						// skip "CPU states:" token(or even "Cpu(s):" token)
						while (line.hasMoreTokens() && !line.nextToken().endsWith(":"))
						{
							// do nothing
						}
						if (sLine.indexOf('%') == -1) // no %, so assuming
						// values specified on
						// the next line
						{
							final String sValueLine = st.nextToken();
							final StringTokenizer values = new StringTokenizer(sValueLine);
							while (line.hasMoreTokens())
							{
								final String token = line.nextToken();
								temp = values.nextToken();
								if ((token.indexOf("idle") != -1) || token.equals("id"))
								{
									temp = temp.substring(0, temp.length() - 1);
									temp = temp.trim();
									try
									{
										idleCpuTime = this.nf.parse(temp).floatValue();
									}
									catch (final Exception e)
									{
										e.printStackTrace();
										idleCpuTime = 100;
									}
									this.fCpuUsageTime = 100 - idleCpuTime;
								}
							}
						}
						else
						// % found, so assuming values specified on this line
						{
							/*
							 * CPU states: 0.9% user, 8.6% system, 0.0% nice,
							 * 90.3% idle OR Cpu(s): 0.3% user, 0.1% system,
							 * 0.0% nice, 99.6% idle OR Cpu(s): 27.0% us, 7.4%
							 * sy, 0.0% ni, 65.1% id, 0.0% wa, 0.0% hi, 0.5% si
							 */
							String sPrevToken;
							String token = null;
							while (line.hasMoreTokens())
							{
								sPrevToken = token;
								token = line.nextToken();
								if ((token.indexOf("idle") != -1) || token.equals("id"))
								{
									temp = sPrevToken.substring(0, sPrevToken.length() - 1);
									temp = temp.trim();
									try
									{
										idleCpuTime = this.nf.parse(temp).floatValue();
									}
									catch (final Exception e)
									{
										e.printStackTrace();
										idleCpuTime = 100;
									}
									this.fCpuUsageTime = 100 - idleCpuTime;
								}
							}
						}
					}
					/*
					 * Mem: 512364K av, 456112K used, 56252K free, 56K shrd,
					 * 140636K buff
					 */
					if (sLine.startsWith("Mem:"))
					{
						final StringTokenizer line = new StringTokenizer(sLine);
						String sPrevToken;
						temp = null;
						while (line.hasMoreTokens())
						{
							sPrevToken = temp;
							temp = line.nextToken();
							if (temp.indexOf("used") != -1)
							{
								try
								{
									usedMemory = this.nf.parse(sPrevToken).intValue() / 1024;
								}
								catch (final Exception e)
								{
									usedMemory = 0;
								}
							}
							if (temp.indexOf("free") != -1)
							{
								try
								{
									freeMemory = this.nf.parse(sPrevToken).intValue() / 1024;
								}
								catch (final Exception e)
								{
									freeMemory = 0;
								}
							}
						}
						// System.out.println("Phy mem " + usedMemory + " " +
						// freeMemory );
						this.physicalMemInfo = new MemoryInfo((usedMemory + freeMemory), usedMemory, freeMemory);
					}
					/* Swap: 2048276K av, 0K used, 2048276K free 57100K cached */
					if (sLine.startsWith("Swap:"))
					{
						final StringTokenizer line = new StringTokenizer(sLine);
						String sPrevToken;
						temp = null;
						while (line.hasMoreTokens())
						{
							sPrevToken = temp;
							temp = line.nextToken();
							if (temp.indexOf("used") != -1)
							{
								try
								{
									usedMemory = this.nf.parse(sPrevToken).intValue() / 1000; // TODO why is
									// this devided
									// by 1000
								}
								catch (final Exception e)
								{
									usedMemory = 0;
								}
							}
							if (temp.indexOf("free") != -1)
							{
								try
								{
									freeMemory = this.nf.parse(sPrevToken).intValue() / 1000;
								}
								catch (final Exception e)
								{
									freeMemory = 0;
								}
							}
						}
						// System.out.println("Virtual mem " + usedMemory + " "
						// + freeMemory );
						this.virtualMemInfo = new MemoryInfo((usedMemory + freeMemory), usedMemory, freeMemory);
					}
					/*
					 * In case of Linux 7.2 PID USER PRI NI SIZE RSS SHARE STAT
					 * %CPU %MEM TIME COMMAND 29530 root 15 0 5084 5084 3580 S
					 * 7.1 0.9 3:37 gtop
					 * 
					 * In case of Linux 9 PID USER PRI NI SIZE RSS SHARE STAT
					 * %CPU %MEM TIME CPU COMMAND 1838 root 15 0 85524 18M 5920
					 * S 2.7 3.7 1:11 0 X
					 * 
					 * We need PID, COMMAND, %CPU and %MEM
					 */
					if (bProcesses)
					{
						if (!sLine.trim().equals(""))
						{
							final StringTokenizer line = new StringTokenizer(sLine);
							if (!((line.countTokens() == 12) || (line.countTokens() == 13)))
							{
								continue;
							}
							temp = line.nextToken(); // PID:
							try
							{
								iPID = this.nf.parse(temp).intValue();
							}
							catch (final Exception e)
							{
								iPID = 0;
							}
							sUserName = line.nextToken(); // SKIP USER
							line.nextToken(); // SKIP PRI
							line.nextToken(); // SKIP NI
							line.nextToken(); // SKIP SIZE
							// read RSS i.e. memory used
							temp = line.nextToken();
							try
							{
								iProcessMemory = this.nf.parse(temp).intValue();
								final char ch = temp.charAt(temp.length() - 1);
								if ((ch == 'M') || (ch == 'm'))
								{
									iProcessMemory = iProcessMemory * 1024;
								}
							}
							catch (final Exception e)
							{
								iProcessMemory = 0;
							}

							line.nextToken(); // SKIP SHARE
							line.nextToken(); // SKIP STAT

							// read %CPU i.e. cpu usage
							temp = line.nextToken();
							// temp = temp.trim();
							try
							{
								fCpuUsage = this.nf.parse(temp).floatValue();
							}
							catch (final Exception e)
							{
								fCpuUsage = 0;
							}

							// read %MEM i.e. memory used
							temp = line.nextToken();
							// try
							// {
							// iProcessMemory = nf.parse(temp).intValue();
							// }
							// catch(Exception e)
							// {
							// iProcessMemory = 0;
							// }

							line.nextToken(); // SKIP TIME
							if (bLinux9)
							{
								line.nextToken(); // SKIP CPU
							}
							sProcessName = line.nextToken(); // COMMAND
							// System.out.println("Process " + sProcessName + "
							// " + iPID + " " + iProcessMemory + " " + fCpuUsage
							// );
							pInfo = new ProcessInfo(sProcessName, iPID, iProcessMemory, fCpuUsage);
							pInfo.setUserName(sUserName);
							this.processInfoList.add(pInfo);
						}
					}
					/*
					 * In case of Linux 7.2 PID USER PRI NI SIZE RSS SHARE STAT
					 * %CPU %MEM TIME COMMAND *
					 * 
					 * In case of Linux 9 PID USER PRI NI SIZE RSS SHARE STAT
					 * %CPU %MEM TIME CPU COMMAND *
					 */
					if (sLine.indexOf("PID USER") != -1)
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
						if (sLine.indexOf("CPU COMMAND") != -1)
						{
							bLinux9 = true;
						}
					}
				}
			}
			catch (final Exception ex)
			{
				throw new ResultParsingException("Collect Data " + ex.getMessage(), output);
			}
		}
	}

	public void parseNetStatCommand(final String output) throws ResultParsingException
	{
		/*
		 * netstat -i -e --- returns the extended network interface info for all the interfaces
		 * 
		  bond0   Link encap:Ethernet  HWaddr 00:30:48:CF:9B:18  
		          inet addr:17.209.103.12  Bcast:17.209.103.255  Mask:255.255.255.0
		          inet6 addr: fe80::230:48ff:fecf:9b18/64 Scope:Link
		          UP BROADCAST RUNNING MASTER MULTICAST  MTU:1500  Metric:1
		          RX packets:11854677260 errors:0 dropped:0 overruns:0 frame:0
		          TX packets:20230554721 errors:0 dropped:0 overruns:0 carrier:0
		          collisions:0 txqueuelen:0 
		          RX bytes:5744669549117 (5.2 TiB)  TX bytes:20232387831537 (18.4 TiB)
		 */

		if (this.networkInfoList == null)
		{
			this.networkInfoList = new LinkedList();
		}
		else
		{
			this.networkInfoList.clear();
		}
		/*
		 * Read the bytes and create string. Tokenize the string on new line.
		 * Discard first line. Each network interface is represented by set of 8
		 * lines. Two interfaces are separated by a blank line. Read till the
		 * next token begins with "[userid"
		 */
		final StringTokenizer st = new StringTokenizer(output, NEW_LINE_TOKENS);
		while (st.hasMoreTokens())
		{
			if (st.nextToken().startsWith("Kernel"))
			{
				break;
			}
		}
		String name = null;
		String ipInterface = null;
		String sLine = null;
		try
		{
			String rxOK = "";
			String txOK = "";
			while (st.hasMoreTokens())
			{
				sLine = st.nextToken().trim();
				if(sLine == null || sLine.length() == 0 || sLine.startsWith("Interrupt") || sLine.startsWith("Memory"))
				{
					continue;
				}
				if(sLine.indexOf("Link encap:") != -1 && sLine.indexOf("Ethernet") != -1)
				{
					name = sLine;
				}
				
				if (sLine.startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT))
				{
					break;
				}

				rxOK = "";
				txOK = "";
				while (st.hasMoreTokens())
				{
					sLine = st.nextToken().trim();
					if (sLine.startsWith("lo")) // do not read local loop back details
					{
						continue;
					}
					
					if(sLine.indexOf("inet addr:") != -1 && sLine.indexOf("inet addr:127.0.0.1") == -1)
					{
						ipInterface = sLine;
					}
					
					//RX bytes:7532360691 (7.0 GiB)  TX bytes:7532360691 (7.0 GiB)
					if (sLine.startsWith("RX bytes:")) // last line of current interface
					{
						sLine = sLine.substring(sLine.indexOf("RX bytes:") + "RX bytes:".length());
						if(sLine.indexOf(' ') != -1)
						{
							rxOK = sLine.substring(0, sLine.indexOf(' '));
						}

						sLine = sLine.substring(sLine.indexOf("TX bytes:") + "TX bytes:".length());
						if(sLine.indexOf(' ') != -1)
						{
							txOK = sLine.substring(0, sLine.indexOf(' '));
						}
						
						break;
					}
				}
				if (name != null && !name.startsWith("lo") && ipInterface != null)
				{
					rxOK = ((rxOK == null) || (rxOK.trim().length() == 0)) ? "0" : rxOK.trim();
					txOK = ((txOK == null) || (txOK.trim().length() == 0)) ? "0" : txOK.trim();

					long recvOK = Long.parseLong(rxOK) / 1024;
					long sentOK = Long.parseLong(txOK) / 1024;
					if(name.indexOf(' ') != -1)
					{
						name = name.substring(0, name.indexOf(' '));
					}
					final UnixNetworkInfo nInfo = new UnixNetworkInfo(name, 0, recvOK, sentOK);
					this.networkInfoList.add(nInfo);						
					name = null;
					ipInterface = null;
				}
			}
		}
		catch (final Exception ex)
		{
			throw new ResultParsingException("Network Usage " + ex.getMessage(), output);
		}
	}

	public void parseIOStatCommand(final String output) throws ResultParsingException
	{
		/*
		 * iostat -x -d --- returns the disk statitistics for all the disks
			Device:            tps    kB_read/s    kB_wrtn/s    kB_read    kB_wrtn
			sda              11.70         4.78       206.17   27307739 1178443612
			sdb               0.00         0.00         0.59       4103    3351548
			sdc               0.00         0.00         0.00       1678       8384
			dm-0              0.15         0.00         0.59       2601    3359812
		*/
		/*
		 * Read the bytes and create string. Tokenize the string. Discard all
		 * the tokes till "Blk_wrtn" token and then in next loop start reading
		 * disk info. Every line represents a disk. Read till the next token
		 * begins with "[userid"
		 */

		
		String sLine = null;
		String tempName = null;
		if (output != null) // result will be null if this method gets called after disconnect
		{
			boolean bDisks = false;
			final StringTokenizer st = new StringTokenizer(output, "\r\n\f");
			while (st.hasMoreTokens())
			{
				sLine = st.nextToken();
				if (sLine.indexOf("Device:") != -1 && sLine.indexOf("kB_read/s") != -1)
				{
					bDisks = true;
					if (this.diskInfoList == null)
					{
						this.diskInfoList = new LinkedList();
					}
					else
					{
						this.diskInfoList.clear();
					}
					continue;
				}
				if(bDisks)
				{
					final StringTokenizer line = new StringTokenizer(sLine);
					tempName = line.nextToken(); // Device Name:
					line.nextToken(); // tps
					// read the read per sec
					final String sReadpersec = line.nextToken();
					// read the write per sec
					final String sWritepersec = line.nextToken();					
					
					final float fReadpersec = Float.parseFloat(sReadpersec);
					final float fWritepersec = Float.parseFloat(sWritepersec);

					final DiskInfo dInfo = new DiskInfo(tempName, fReadpersec, fWritepersec);
					this.diskInfoList.add(dInfo);
				}
			}			
		}
	}

	public void parseDFCommand(final String output) throws ResultParsingException
	{
		/*
		 * df -k --- returns the disk space info for all the mounted file
		 * systems
		 * 
		 * Format in LINUX is Filesystem 1k-blocks Used Available Use% Mounted
		 * on /dev/hda2 18611412 3591672 14074316 21% / /dev/hda1 46636 5956
		 * 38272 14% /boot none 256184 0 256184 0% /dev/shm
		 */

		StringTokenizer st = null;
		if (this.diskSpaceInfoList == null)
		{
			this.diskSpaceInfoList = new LinkedList();
		}
		else
		{
			this.diskSpaceInfoList.clear();
		}
		st = new StringTokenizer(output, "\n\r"); //$NON-NLS-1$
		try
		{
			boolean bParse = false;
			while (st.hasMoreTokens())
			{
				String temp = st.nextToken();
				if ((temp.toLowerCase().indexOf("filesystem") != -1) && (temp.toLowerCase().indexOf("mounted") != -1)
						&& (temp.toLowerCase().indexOf("on") != -1))
				{
					bParse = true;
					continue;
				}
				if (bParse)
				{
					StringTokenizer st1 = new StringTokenizer(temp);
					final String sFileSystemName = st1.nextToken();
					if (sFileSystemName.startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT))
					{
						break;
					}

					if (!st1.hasMoreTokens())
					{
						if (st.hasMoreTokens())
						{
							temp = st.nextToken();
							st1 = new StringTokenizer(temp.trim());
							st1.hasMoreTokens();
						}
						else
						{
							continue;
						}
					}

					final String sTotalSpace = st1.nextToken();
					final float fTotalSpace = (float) Long.parseLong(sTotalSpace) / 1024;

					final String sUsedSpace = st1.nextToken();
					final float fUsedSpace = (float) Long.parseLong(sUsedSpace) / 1024;

					// discard the FreeSpace info
					st1.nextToken();

					// discard Use% token
					st1.nextToken();
					// discard Mounted on token
					st1.nextToken();

					final DriveInfo dInfo = new DriveInfo(sFileSystemName, fTotalSpace, fUsedSpace);
					this.diskSpaceInfoList.add(dInfo);
				}
			}
		}
		catch (final Exception ex)
		{
			throw new ResultParsingException("Disk Space Info " + ex.getMessage(), output);
		}
	}

	public static void main(String[] args) throws Exception 
	{
		GenericLinuxDataParser parser = new GenericLinuxDataParser();
		File file = new File("F:\\Inbox\\motest1_netstat.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		StringWriter writer = new StringWriter();
		while (line != null) 
		{
			writer.write(line);
			writer.write(System.getProperty("line.separator"));
			line = reader.readLine();
		}
		reader.close();
//		System.out.println(writer.toString());
		parser.parseNetStatCommand(writer.toString());
		// System.out.println("CPU: " + parser.getCPUUsage());
//		parser.parseIOStatCommand(writer.toString());
		NetworkInfo[] arrr = parser.getNetworkInfo();
		for(int i = 0; i < arrr.length; i++)
		{
			System.out.println(arrr[i].getName() + " recieved " + ((UnixNetworkInfo)arrr[i]).getTotalRecdPackets() * 1024 + " sent " +  ((UnixNetworkInfo)arrr[i]).getTotalSentPackets() * 1024);
		}
//		parser.parseDFCommand(writer.toString());
	}
	
}
