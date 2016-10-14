/*
 * @(#)  LinuxParserForTop_3_5_1.java Aug 24, 2005
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
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;

public class LinuxParserForTop_3_5_1 extends GenericLinuxDataParser
{
	public LinuxParserForTop_3_5_1()
	{
		super();
	}

	public void parseTopCommand(final String output) throws ResultParsingException
	{
		/*
		 * last pid: 13951; load averages: 0.04, 0.62, 0.53 12:11:55 81
		 * processes: 1 running, 80 sleeping CPU states: 0.0% user, 0.0% nice,
		 * 0.0% system, 100% idle Memory: 482M used, 12M free, 123M buf Swap:
		 * 1408K used, 1018M free
		 * 
		 * PID USERNAME PRI NICE SIZE RES STATE TIME WCPU CPU COMMAND 1 root 15
		 * 0 1364K 80K sleep 0:03 0.00% 0.00% init 3555 root 15 0 146M 13M sleep
		 * 0:01 0.00% 0.00% X 3671 root 15 0 67M 11M sleep 0:01 0.00% 0.00%
		 * nautilus 3669 root 15 0 20M 8092K sleep 0:00 0.00% 0.00% gnome-panel
		 * 3679 root 15 0 24M 9104K sleep 0:00 0.00% 0.00% rhn-applet-gui 3640
		 * root 15 0 11M 5676K sleep 0:00 0.00% 0.00% gconfd-2 3382 root 15 0
		 * 18M 2344K sleep 0:00 0.00% 0.00% httpd 3571 root 15 0 18M 5480K sleep
		 * 0:00 0.00% 0.00% gnome-session 3655 root 15 0 17M 3296K sleep 0:00
		 * 0.00% 0.00% gnome-settings 3273 root 25 0 3504K 0K sleep 0:00 0.00%
		 * 0.00% sshd 3653 root 15 0 12M 2968K sleep 0:00 0.00% 0.00% metacity
		 */

		float idleCpuTime;

		int usedMemory = 0;
		int freeMemory = 0;
		int usedVirtualMemory = 0;
		int freeVirtualMemory = 0;

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

				final StringTokenizer st = new StringTokenizer(output, "\r\n\f");
				while (st.hasMoreTokens())
				{
					sLine = st.nextToken();
					/*
					 * In case of Linux 9 CPU states: cpu user nice system irq
					 * softirq iowait idle total 5.7% 0.3% 3.3% 0.1% 0.0% 5.5%
					 * 84.6%
					 */
					if (sLine.toLowerCase().startsWith("cpu") && (sLine.indexOf("idle") != -1))
					{
						final StringTokenizer line = new StringTokenizer(sLine);
						String sPrevToken;
						String token = null;
						while (line.hasMoreTokens())
						{
							sPrevToken = token;
							token = line.nextToken();
							if (token.indexOf("idle") != -1)
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
					/*
					 * Memory: 482M used, 12M free, 123M buf Swap: 1408K used,
					 * 1018M free
					 */
					if (sLine.startsWith("Memory:"))
					{
						final StringTokenizer line = new StringTokenizer(sLine);
						String sPrevToken;
						temp = null;
						boolean bSwap = false;
						int memory = 0;
						while (line.hasMoreTokens())
						{
							sPrevToken = temp;
							temp = line.nextToken();
							if (temp.indexOf("used") != -1)
							{
								try
								{
									memory = this.nf.parse(sPrevToken).intValue() / 1024;
								}
								catch (final Exception e)
								{
									memory = 0;
								}
								if (bSwap)
								{
									usedVirtualMemory = memory;
								}
								else
								{
									usedMemory = memory;
								}
							}
							if (temp.indexOf("free") != -1)
							{
								try
								{
									memory = this.nf.parse(sPrevToken).intValue() / 1024;
								}
								catch (final Exception e)
								{
									memory = 0;
								}
								if (bSwap)
								{
									freeVirtualMemory = memory;
								}
								else
								{
									freeMemory = memory;
								}
							}
							if (temp.indexOf("Swap:") != -1)
							{
								bSwap = true;
							}
						}
						this.physicalMemInfo = new MemoryInfo((usedMemory + freeMemory), usedMemory, freeMemory);
						if (bSwap)
						{
							this.virtualMemInfo = new MemoryInfo((usedVirtualMemory + freeVirtualMemory),
									usedVirtualMemory, freeVirtualMemory);
						}
					}
					/*
					 * PID USERNAME PRI NICE SIZE RES STATE TIME WCPU CPU
					 * COMMAND 1 root 15 0 1364K 80K sleep 0:03 0.00% 0.00% init
					 */
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
						sUserName = line.nextToken(); // SKIP USERNAME
						line.nextToken(); // SKIP PRI
						line.nextToken(); // SKIP NICE
						line.nextToken(); // SKIP SIZE
						// read RES i.e. memory used
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

						line.nextToken(); // SKIP STATE
						line.nextToken(); // SKIP TIME
						line.nextToken(); // SKIP WCPU

						// read %CPU i.e. cpu usage
						temp = line.nextToken();
						try
						{
							fCpuUsage = this.nf.parse(temp).floatValue();
						}
						catch (final Exception e)
						{
							fCpuUsage = 0;
						}

						sProcessName = line.nextToken(); // COMMAND
						pInfo = new ProcessInfo(sProcessName, iPID, iProcessMemory, fCpuUsage);
						pInfo.setUserName(sUserName);
						this.processInfoList.add(pInfo);
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
					}
				}
			}
			catch (final Exception ex)
			{
				throw new ResultParsingException("Collect Data " + ex.getMessage(), output);
			}
		}
	}
}
