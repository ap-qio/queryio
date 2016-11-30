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

public class MacOSX_10_6_Parser extends GenericMacOSXDataParser {

	/**
	 * @see com.queryio.sysmoncommon.sysmon.AbstractDataParser#parseTopMemCommand(java.lang.String)
	 */
	public void parseTopMemCommand(final String output) throws ResultParsingException {
		/*
		 * Processes: 57 total, 3 running, 54 sleeping, 340 threads 2010/04/14
		 * 10:15:58 Load Avg: 0.32, 0.34, 0.31 CPU usage: 3.36% user, 3.84% sys,
		 * 92.78% idle SharedLibs: 8260K resident, 2824K data, 0B linkedit.
		 * MemRegions: 9172 total, 845M resident, 19M private, 224M shared.
		 * PhysMem: 850M wired, 1149M active, 444M inactive, 2444M used, 1654M
		 * free. VM: 128G vsize, 1035M framework vsize, 191836(0) pageins, 0(0)
		 * pageouts. Networks: packets: 12248403/6949M in, 1614032/465M out.
		 * Disks: 248192/3528M read, 359869/5724M written.
		 * 
		 * PID COMMAND %CPU TIME #TH #WQ #PORTS #MREGS RPRVT RSHRD RSIZE VPRVT
		 * VSIZE PGRP PPID STATE UID FAULTS COW MSGSENT MSGRECV SYSBSD SYSMACH
		 * CSW PAGEINS USER 10686 top 3.8 00:00.17 1/1 0 24 33 660K 244K 1228K
		 * 18M 2378M 10686 10601 running 0 2513+ 53 88207+ 44088+ 1058+ 44361+
		 * 86+ 0 root
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

		// this.physicalMemInfo = null;
		// this.virtualMemInfo = null;

		if (this.networkInfoList == null) {
			this.networkInfoList = new LinkedList();
		} else {
			this.networkInfoList.clear();
		}
		if (this.diskInfoList == null) {
			this.diskInfoList = new LinkedList();
		} else {
			this.diskInfoList.clear();
		}
		/*
		 * Read the bytes and create string.
		 */
		if (output != null) // output will be null if this method gets called
							// after disconnect
		{
			try {
				final StringTokenizer st = new StringTokenizer(output, "\r\n\f");
				while (st.hasMoreTokens()) {
					sLine = st.nextToken();
					// Processes: 57 total, 3 running, 54 sleeping, 340 threads
					if (sLine.startsWith("Processes:")) {
						sLine = sLine.substring("Processes:".length());
						final StringTokenizer line = new StringTokenizer(sLine, " ");
						temp = line.nextToken().trim();
						try {
							noOfProcs = nf.parse(temp).intValue();
						} catch (final Exception e) {
							noOfProcs = 0;
						}
					}
					/*
					 * Load Avg: 0.32, 0.34, 0.31
					 */
					if (sLine.startsWith("Load Avg:")) {
						sLine = sLine.substring(sLine.indexOf("Load Avg:") + "Load Avg:".length());
						final StringTokenizer line = new StringTokenizer(sLine, ",");
						temp = line.nextToken().trim();
						try {
							value = this.nf.parse(temp).floatValue();
						} catch (final Exception e) {
							value = 0.0f;
						}
						f1MinLoadAvg = value;
						temp = line.nextToken().trim();
						try {
							value = this.nf.parse(temp).floatValue();
						} catch (final Exception e) {
							value = 0.0f;
						}
						f5MinLoadAvg = value;
						temp = line.nextToken().trim();
						try {
							value = this.nf.parse(temp).floatValue();
						} catch (final Exception e) {
							value = 0.0f;
						}
						f15MinLoadAvg = value;
					}
					/*
					 * CPU usage: 3.36% user, 3.84% sys, 92.78% idle
					 */
					else if (sLine.indexOf("CPU usage:") != -1) {
						sLine = sLine.substring(sLine.indexOf("CPU usage:") + "CPU usage:".length());
						final StringTokenizer line = new StringTokenizer(sLine);
						while (line.hasMoreTokens()) {
							temp = line.nextToken();
							if (temp.indexOf("%") != -1) {
								temp = temp.substring(0, temp.indexOf("%"));
								try {
									value = this.nf.parse(temp).floatValue();
								} catch (final Exception e) {
									value = -1.0f;
								}
							}
							if (temp.indexOf("user") != -1) {
								this.fUserCPUUsageTime = value != -1.0f ? value : 0.0f;
							}
							if (temp.indexOf("sys") != -1) {
								this.fSysCPUUsageTime = value != -1.0f ? value : 0.0f;
							}
							if (temp.indexOf("idle") != -1) {
								this.fCpuUsageTime = value != -1.0f ? (100.0f - value) : 0.0f;
							}
						}
					}
					/*
					 * PhysMem: 850M wired, 1149M active, 444M inactive, 2444M
					 * used, 1654M free.
					 */
					else if (sLine.startsWith("PhysMem:")) {
						sLine = sLine.substring(sLine.indexOf("PhysMem:") + "PhysMem:".length());
						final StringTokenizer line = new StringTokenizer(sLine);
						freeMemory = -1;
						usedMemory = -1;
						while (line.hasMoreTokens()) {
							temp = line.nextToken();
							if (temp.toLowerCase().endsWith("m") || temp.toLowerCase().endsWith("g")
									|| temp.toLowerCase().endsWith("k")) {
								sMem = temp.substring(0, temp.length() - 1).trim();
								try {
									value = this.nf.parse(sMem).floatValue();
									final char ch = temp.charAt(temp.length() - 1);
									if ((ch == 'G') || (ch == 'g')) {
										value *= 1024;
									}
								} catch (final Exception e) {
									value = 0;
								}
							}
							if (temp.indexOf("used") != -1) {
								usedMemory = Math.round(value);
							}
							if (temp.indexOf("free") != -1) {
								freeMemory = Math.round(value);
							}
							if (usedMemory != -1 && freeMemory != -1) {
								this.physicalMemInfo = new MemoryInfo((usedMemory + freeMemory), usedMemory,
										freeMemory);
								break;
							}
						}
					}
					/*
					 * VM: 128G vsize, 1035M framework vsize, 191836(0) pageins,
					 * 0(0) pageouts.
					 */
					else if (sLine.startsWith("VM:")) {
						sLine = sLine.substring(sLine.indexOf("VM:") + "VM:".length());
						final StringTokenizer line = new StringTokenizer(sLine);
						freeMemory = -1;
						usedMemory = -1;
						while (line.hasMoreTokens()) {
							temp = line.nextToken();
							if (temp.toLowerCase().endsWith("m") || temp.toLowerCase().endsWith("g")
									|| temp.toLowerCase().endsWith("k")) {
								temp = temp.trim();
								sMem = temp.substring(0, temp.length() - 1);
								try {
									value = this.nf.parse(sMem).floatValue();
									final char ch = temp.charAt(temp.length() - 1);
									if ((ch == 'G') || (ch == 'g')) {
										value *= 1024;
									}
								} catch (final Exception e) {
									value = 0;
								}
							}
							if (temp.indexOf("vsize") != -1) {
								freeMemory = Math.round(value);
							}
							if (temp.indexOf("framework") != -1) {
								usedMemory = Math.round(value);
							}
							if (usedMemory != -1 && freeMemory != -1) {
								this.virtualMemInfo = new MemoryInfo(freeMemory, usedMemory, (freeMemory - usedMemory));
								break;
							}
						}
					}
					/*
					 * Networks: packets: 12248403/6949M in, 1614032/465M out.
					 */
					else if (sLine.startsWith("Networks:")) {
						sLine = sLine.substring(sLine.indexOf("Networks:") + "Networks:".length());
						final StringTokenizer line = new StringTokenizer(sLine);
						fMBRead = -1.0f;
						fMBSend = -1.0f;
						while (line.hasMoreTokens()) {
							temp = line.nextToken();
							if (temp.indexOf('/') != -1) {
								temp = temp.substring(temp.indexOf('/') + 1).trim();
								sMem = temp.substring(0, temp.length() - 1);
								try {
									value = this.nf.parse(sMem).floatValue();
									final char ch = temp.charAt(temp.length() - 1);
									if ((ch == 'B') || (ch == 'b')) {
										value /= 1024;
									} else if ((ch == 'K') || (ch == 'k')) {
										// do nothing
									} else if ((ch == 'M') || (ch == 'g')) {
										value *= 1024;
									} else if ((ch == 'G') || (ch == 'g')) {
										value *= (1024 * 1024);
									}
								} catch (final Exception e) {
									value = 0;
								}
							}
							if (temp.indexOf("in") != -1) {
								fMBRead = (value != -1) ? value : 0.0f;
							}
							if (temp.indexOf("out") != -1) {
								fMBSend = (value != -1) ? value : 0.0f;
							}
							if (fMBRead != -1 && fMBSend != -1) {
								final NetworkInfo networkInfo = new NetworkInfo("Network", 0, fMBRead, fMBSend);
								this.networkInfoList.add(networkInfo);
								break;
							}
						}
					}
					/*
					 * Disks: 248192/3528M read, 359869/5724M written.
					 */
					else if (sLine.startsWith("Disks:")) {
						sLine = sLine.substring(sLine.indexOf("Disks:") + "Disks:".length());
						final StringTokenizer line = new StringTokenizer(sLine);
						fMBRead = -1.0f;
						fMBSend = -1.0f;
						while (line.hasMoreTokens()) {
							temp = line.nextToken();
							if (temp.indexOf('/') != -1) {
								temp = temp.substring(temp.indexOf('/') + 1).trim();
								sMem = temp.substring(0, temp.length() - 1);
								try {
									value = this.nf.parse(sMem).floatValue();
									final char ch = temp.charAt(temp.length() - 1);
									if ((ch == 'B') || (ch == 'b')) {
										value /= 1024;
									} else if ((ch == 'K') || (ch == 'k')) {
										// do nothing
									} else if ((ch == 'M') || (ch == 'g')) {
										value *= 1024;
									} else if ((ch == 'G') || (ch == 'g')) {
										value *= (1024 * 1024);
									}
								} catch (final Exception e) {
									value = 0;
								}
							}
							if (temp.indexOf("read") != -1) {
								fMBRead = value;
								// fMBRead = (fDiskMBSend != -1) ? (value -
								// fDiskMBSend) : 0.0f;
								// fDiskMBSend = value;
							}
							if (temp.indexOf("writ") != -1) {
								fMBSend = value;
								// fMBSend = (fDiskMBRead != -1) ? (value -
								// fDiskMBRead) : 0.0f;
								// fDiskMBRead = value;
							}
							if (fMBRead != -1 && fMBSend != -1) {
								final DiskInfo diskInfo = new DiskInfo("Disk", fMBRead, fMBSend);
								this.diskInfoList.add(diskInfo);
								break;
							}
						}
					}
					/*
					 * PID COMMAND %CPU TIME #TH #WQ #PORTS #MREGS RPRVT RSHRD
					 * RSIZE VPRVT VSIZE PGRP PPID STATE UID FAULTS COW MSGSENT
					 * MSGRECV SYSBSD SYSMACH CSW PAGEINS USER 10686 top 3.8
					 * 00:00.17 1/1 0 24 33 660K 244K 1228K 18M 2378M 10686
					 * 10601 running 0 2513+ 53 88207+ 44088+ 1058+ 44361+ 86+ 0
					 * root
					 */
					else if (bProcesses) {
						final StringTokenizer line = new StringTokenizer(sLine);

						temp = line.nextToken(); // PID:
						try {
							iPID = this.nf.parse(temp).intValue();
						} catch (final Exception e) {
							iPID = 0;
						}
						sProcessName = line.nextToken(); // COMMAND

						temp = line.nextToken();
						temp = temp.trim();
						try {
							fCpuUsage = this.nf.parse(temp).floatValue();
						} catch (final Exception e) {
							fCpuUsage = 0;
						}
						line.nextToken(); // Ignore CPU TIME:
						// line.nextToken(); // Ignore #TH
						temp = line.nextToken(); // Ignore #TH
						temp = temp.trim();

						try {
							iThreads = Integer.parseInt(temp);
						} catch (final Exception e) {
							iThreads = 0;
						}
						line.nextToken(); // Ignore #WQ
						line.nextToken(); // Ignore #PORTS
						line.nextToken(); // Ignore #MREGS
						line.nextToken(); // Ignore RPRVT
						line.nextToken(); // Ignore RSHRD
						temp = line.nextToken(); // RSIZE:
						temp = temp.trim();
						sMem = temp.substring(0, temp.length() - 1);
						try {
							iProcessMemory = Math.round(this.nf.parse(temp).floatValue());
							final char ch = temp.charAt(temp.length() - 1);
							if ((ch == 'M') || (ch == 'm')) {
								iProcessMemory *= 1024;
							}
						} catch (final Exception e) {
							iProcessMemory = 0;
						}
						pInfo = new ProcessInfo(sProcessName, iPID, iProcessMemory, fCpuUsage, iProcessMemory,
								iThreads);
						this.processInfoList.add(pInfo);
					}
					/*
					 * PID COMMAND %CPU TIME #TH #PRTS #MREGS RPRVT RSHRD RSIZE
					 * VSIZE
					 */
					else if (sLine.indexOf("PID") != -1 && sLine.indexOf("COMMAND") != -1 && this.monitorProcesses) {
						bProcesses = true;
						if (this.processInfoList == null) {
							this.processInfoList = new LinkedList();
						} else {
							this.processInfoList.clear();
						}
					}
				}
			} catch (final Exception ex) {
				System.err.println("sLine: " + sLine);
				ex.printStackTrace();
				throw new ResultParsingException("Collect Data", output + ex.getMessage());
			}
		}
	}
}
