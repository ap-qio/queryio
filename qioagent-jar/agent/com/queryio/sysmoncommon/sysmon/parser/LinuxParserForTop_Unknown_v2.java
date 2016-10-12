/*
 * @(#)  LinuxParserForTop_Unknown_v2.java Aug 24, 2005
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

public class LinuxParserForTop_Unknown_v2 extends GenericLinuxDataParser
{
	public LinuxParserForTop_Unknown_v2()
	{
		super();
	}

	public void parseTopCommand(final String output) throws ResultParsingException
	{
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
		float idleCpuTime;

		int usedMemory = 0;
		int freeMemory = 0;

		boolean bProcesses = false;
		ProcessInfo pInfo = null;
		int iPID = 0;
		String sProcessName = null;
		String sUserName = null;
		float fCpuUsage = 0;
		int iProcessMemory = 0;

		if (output != null) // output will be null if this method gets called
		// after disconnect
		{
			try
			{
				String temp = null;
				String sLine = null;

				final StringTokenizer st = new StringTokenizer(output, "\r\n\f");
				while (st.hasMoreTokens())
				{
					sLine = st.nextToken();
					/*
					 * In case of Linux 7.2 CPU states: 0.9% user, 8.6% system,
					 * 0.0% nice, 90.3% idle
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
					 * Mem: 512364K av, 456112K used, 56252K free, 56K shrd,
					 * 140636K buff
					 */
					if (sLine.startsWith("Mem:"))
					{
						final StringTokenizer line = new StringTokenizer(sLine);
						while (line.hasMoreTokens())
						{
							temp = line.nextToken();
							if (temp.indexOf("av,") != -1)
							{
								temp = line.nextToken().trim();
								try
								{
									usedMemory = this.nf.parse(temp).intValue() / 1024;
								}
								catch (final Exception e)
								{
									usedMemory = 0;
								}
								temp = line.nextToken(); // Ignore as it is
								// String used,

								temp = line.nextToken();
								temp = temp.trim();
								try
								{
									freeMemory = this.nf.parse(temp).intValue() / 1024;
								}
								catch (final Exception e)
								{
									freeMemory = 0;
								}
								this.physicalMemInfo = new MemoryInfo((usedMemory + freeMemory), usedMemory, freeMemory);
							}
						}
					}
					/* Swap: 2048276K av, 0K used, 2048276K free 57100K cached */
					if (sLine.startsWith("Swap:"))
					{
						final StringTokenizer line = new StringTokenizer(sLine);
						while (line.hasMoreTokens())
						{
							temp = line.nextToken();
							if (temp.indexOf("av,") != -1)
							{
								temp = line.nextToken().trim();
								try
								{
									usedMemory = this.nf.parse(temp).intValue() / 1000;
								}
								catch (final Exception e)
								{
									usedMemory = 0;
								}
							}
							else if (temp.indexOf("used,") != -1)
							{
								temp = line.nextToken();
								temp = temp.trim();
								try
								{
									freeMemory = this.nf.parse(temp).intValue() / 1000;
								}
								catch (final Exception e)
								{
									freeMemory = 0;
								}
							}
						}
						this.virtualMemInfo = new MemoryInfo((usedMemory + freeMemory), usedMemory, freeMemory);
					}
					/*
					 * In case of Linux 7.2 PID USER PRI NI SIZE RSS SHARE STAT
					 * %CPU %MEM TIME COMMAND 29530 root 15 0 5084 5084 3580 S
					 * 7.1 0.9 3:37 gtop
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
						temp = temp.trim();
						temp = temp.substring(0, temp.length());
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
