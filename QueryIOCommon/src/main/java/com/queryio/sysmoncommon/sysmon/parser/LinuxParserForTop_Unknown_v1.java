/*
 * @(#)  LinuxParserForTop_Unknown_v1.java Aug 24, 2005
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

public class LinuxParserForTop_Unknown_v1 extends GenericLinuxDataParser {
	public LinuxParserForTop_Unknown_v1() {
		super();
	}

	public void parseTopCommand(final String output) throws ResultParsingException {
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

		/*
		 * Read the bytes and create string. Tokenize the string. Discard all
		 * the tokes till "%idle" token and then read 4th token.
		 */
		if (output != null) // output will be null if this method gets called
		// after disconnect
		{
			try {
				// boolean bSecond = false;
				String temp = null;
				String sLine = null;

				final StringTokenizer st = new StringTokenizer(output, "\r\n\f");
				while (st.hasMoreTokens()) {
					sLine = st.nextToken();
					/*
					 * In case of Linux 9 CPU states: cpu user nice system irq
					 * softirq iowait idle total 5.7% 0.3% 3.3% 0.1% 0.0% 5.5%
					 * 84.6%
					 */
					if (sLine.toLowerCase().startsWith("cpu") && (sLine.indexOf("idle") != -1)) {
						final StringTokenizer line = new StringTokenizer(sLine);
						// skip "CPU states:" token
						while (line.hasMoreTokens() && !line.nextToken().endsWith(":")) {
							// do nothing
						}
						final String sValueLine = st.nextToken();
						final StringTokenizer values = new StringTokenizer(sValueLine);
						while (line.hasMoreTokens()) {
							final String token = line.nextToken();
							temp = values.nextToken();
							if (token.indexOf("idle") != -1) {
								temp = temp.substring(0, temp.length() - 1);
								temp = temp.trim();
								try {
									idleCpuTime = this.nf.parse(temp).floatValue();
								} catch (final Exception e) {
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
					if (sLine.startsWith("Mem:")) {
						final StringTokenizer line = new StringTokenizer(sLine);
						while (line.hasMoreTokens()) {
							temp = line.nextToken();
							if (temp.indexOf("av,") != -1) {
								temp = line.nextToken().trim();
								try {
									usedMemory = this.nf.parse(temp).intValue() / 1024;
								} catch (final Exception e) {
									usedMemory = 0;
								}
								temp = line.nextToken(); // Ignore as it is
								// String used,

								temp = line.nextToken();
								temp = temp.trim();
								try {
									freeMemory = this.nf.parse(temp).intValue() / 1024;
								} catch (final Exception e) {
									freeMemory = 0;
								}
								this.physicalMemInfo = new MemoryInfo((usedMemory + freeMemory), usedMemory,
										freeMemory);
							}
						}
					}
					/*
					 * Swap: 2048276K av, 0K used, 2048276K free 57100K cached
					 */
					if (sLine.startsWith("Swap:")) {
						final StringTokenizer line = new StringTokenizer(sLine);
						while (line.hasMoreTokens()) {
							temp = line.nextToken();
							if (temp.indexOf("av,") != -1) {
								temp = line.nextToken().trim();
								try {
									usedMemory = this.nf.parse(temp).intValue() / 1000;
								} catch (final Exception e) {
									usedMemory = 0;
								}
							} else if (temp.indexOf("used,") != -1) {
								temp = line.nextToken();
								temp = temp.trim();
								try {
									freeMemory = this.nf.parse(temp).intValue() / 1000;
								} catch (final Exception e) {
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
					 * 
					 * In case of Linux 9 PID USER PRI NI SIZE RSS SHARE STAT
					 * %CPU %MEM TIME CPU COMMAND 1838 root 15 0 85524 18M 5920
					 * S 2.7 3.7 1:11 0 X
					 * 
					 * We need PID, COMMAND, %CPU and %MEM
					 */
					if (bProcesses) {
						final StringTokenizer line = new StringTokenizer(sLine);
						temp = line.nextToken(); // PID:
						try {
							iPID = this.nf.parse(temp).intValue();
						} catch (final Exception e) {
							iPID = 0;
						}
						sUserName = line.nextToken(); // SKIP USER
						line.nextToken(); // SKIP PRI
						line.nextToken(); // SKIP NI
						line.nextToken(); // SKIP SIZE
						// read RSS i.e. memory used
						temp = line.nextToken();
						try {
							iProcessMemory = this.nf.parse(temp).intValue();
							final char ch = temp.charAt(temp.length() - 1);
							if ((ch == 'M') || (ch == 'm')) {
								iProcessMemory = iProcessMemory * 1024;
							}
						} catch (final Exception e) {
							iProcessMemory = 0;
						}

						line.nextToken(); // SKIP SHARE
						line.nextToken(); // SKIP STAT

						// read %CPU i.e. cpu usage
						temp = line.nextToken();
						temp = temp.trim();
						temp = temp.substring(0, temp.length());
						try {
							fCpuUsage = this.nf.parse(temp).floatValue();
						} catch (final Exception e) {
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
						// if(bLinux9)
						{
							line.nextToken(); // SKIP CPU
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
					if (sLine.indexOf("PID USER") != -1) {
						bProcesses = true;
						if (this.processInfoList == null) {
							this.processInfoList = new LinkedList();
						} else {
							this.processInfoList.clear();
						}
					}
				}
			} catch (final Exception ex) {
				throw new ResultParsingException("Collect Data " + ex.getMessage(), output);
			}
		}
	}
}
