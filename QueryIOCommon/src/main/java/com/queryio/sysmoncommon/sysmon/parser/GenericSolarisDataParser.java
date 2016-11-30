/*
 * @(#)  SolarisParser.java Aug 23, 2005
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

import com.queryio.sysmoncommon.sysmon.AbstractDataParser;
import com.queryio.sysmoncommon.sysmon.ResultParsingException;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.UnixNetworkInfo;
import com.queryio.sysmoncommon.sysmon.protocol.AbstractProtocolWrapper;

public class GenericSolarisDataParser extends AbstractDataParser {
	public void parseTopMemCommand(final String output) throws ResultParsingException {
		// DO NOTHING
	}

	public void parseVmstatCommand(final String output) throws ResultParsingException {
		int freeVirtualMemory = 0;
		int freePhysicalMemory = 0;

		// Command:-vmstat [Solaris 5.8]
		// ====================================================
		/*
		 * Output : procs memory page disk faults cpu r b w swap free re mf pi
		 * po fr de sr dd f0 s0 -- in sy cs us sy id 0 0 0 687432 191560 2 11 16
		 * 6 6 0 0 2 0 0 0 116 150 67 0 1 98
		 * 
		 */
		// System.out.println("vmstat output:\n" + output);
		if (output != null) // output will be null if this method gets called
		// after disconnect
		{
			try {
				String temp = null;
				String sValue = null;
				String sLine = null;
				final StringTokenizer st = new StringTokenizer(output, "\r\n\f");
				StringTokenizer stHeader = null;
				String headerLine = null;
				while (st.hasMoreTokens()) {
					sLine = st.nextToken();
					if (sLine.toLowerCase().indexOf("swap") != -1) {
						headerLine = sLine;
						continue;
					} else if (headerLine == null) {
						continue;
					}
					stHeader = new StringTokenizer(headerLine);
					final StringTokenizer stValues = new StringTokenizer(sLine);

					while (stHeader.hasMoreTokens() && stValues.hasMoreTokens()) {
						temp = stHeader.nextToken().toLowerCase();
						sValue = stValues.nextToken();
						if (temp.equals("swap")) {
							try {
								freeVirtualMemory = this.nf.parse(sValue).intValue() / 1024;
								if (this.virtualMemInfo != null) {
									this.virtualMemInfo.setAvailable(freeVirtualMemory);
									this.virtualMemInfo.setTotal(freeVirtualMemory + this.virtualMemInfo.getUsed());
								} else {
									this.virtualMemInfo = new MemoryInfo(freeVirtualMemory, 0, freeVirtualMemory);
								}
							} catch (final Exception e) {
							}
						} else if (temp.equals("free")) {
							try {
								freePhysicalMemory = this.nf.parse(sValue).intValue() / 1024;
								if (this.physicalMemInfo != null) {
									this.physicalMemInfo.setAvailable(freePhysicalMemory);
									this.physicalMemInfo.setTotal(freePhysicalMemory + this.physicalMemInfo.getUsed());
								} else {
									this.physicalMemInfo = new MemoryInfo(freePhysicalMemory, 0, freePhysicalMemory);
								}
							} catch (final Exception e) {
							}
							// break;
						}
					}
				}
			} catch (final Exception ex) {
				throw new ResultParsingException("Collect Data " + ex.getMessage(), output);
			}
		}
	}

	public void parseTopCommand(final String output) throws ResultParsingException {
		/*
		 * last pid: 14684; load averages: 0.12, 0.05, 0.12 18:08:20 67
		 * processes: 66 sleeping, 1 on cpu
		 * 
		 * Memory: 256M real, 170M free, 44M swap in use, 651M swap free
		 * 
		 * PID USERNAME LWP PRI NICE SIZE RES STATE TIME CPU COMMAND 14684 ras 1
		 * 0 0 1712K 1048K cpu 0:00 2.08% top 14680 ras 1 58 0 1000K 712K sleep
		 * 0:00 0.41% sh
		 */
		this.fCpuUsageTime = 0.0f;
		int usedMemory = 0;
		int freeMemory = 0;

		ProcessInfo pInfo = null;
		int iPID = 0;
		String sProcessName = null;
		String sUserName = null;
		float fCPUUsage = 0.0f;
		int iMemUsage = 0;
		StringTokenizer line = null;
		boolean bProcesses = false;

		// System.out.println("top -b -d 1 output:\n" + output);
		if (output != null) // output will be null if this method gets called
		// after disconnect
		{
			try {
				String temp = null;
				String sLine = null;
				final StringTokenizer st = new StringTokenizer(output, "\r\n\f");
				while (st.hasMoreTokens()) {
					sLine = st.nextToken();
					/*
					 * last pid: 14684; load averages: 0.12, 0.05, 0.12 18:08:20
					 * 67 processes: 66 sleeping, 1 on cpu
					 */
					if (sLine.indexOf("processes:") > 0) {
						line = new StringTokenizer(sLine, " ");
						temp = line.nextToken(); // read total number of process
						temp = temp.trim();
						try {
							noOfProcs = nf.parse(temp).intValue();
						} catch (final Exception e) {
							noOfProcs = 0;
						}
					}
					/*
					 * Memory: 256M real, 170M free, 44M swap in use, 651M swap
					 * free
					 */
					else if (sLine.startsWith("Memory:")) {
						line = new StringTokenizer(sLine);
						temp = line.nextToken(); // Memory:
						temp = line.nextToken();
						temp = temp.trim();
						temp = temp.substring(0, temp.length() - 1);
						try {
							usedMemory = this.nf.parse(temp).intValue();
						} catch (final Exception e) {
							usedMemory = 0;
						}
						temp = line.nextToken(); // Ignore as it is String
						// real,
						temp = line.nextToken();
						temp = temp.trim();
						temp = temp.substring(0, temp.length() - 1);
						try {
							freeMemory = this.nf.parse(temp).intValue();
						} catch (final Exception e) {
							freeMemory = 0;
						}
						this.physicalMemInfo = new MemoryInfo(usedMemory, (usedMemory - freeMemory), freeMemory);

						temp = line.nextToken(); // Ignore as it is String
						// free,
						temp = line.nextToken();
						temp = temp.trim();
						temp = temp.substring(0, temp.length() - 1);
						try {
							usedMemory = this.nf.parse(temp).intValue();
						} catch (final Exception e) {
							usedMemory = 0;
						}
						line.nextToken(); // Ignore as it is String swap,
						line.nextToken(); // Ignore as it is String in
						line.nextToken(); // Ignore as it is String use,
						temp = line.nextToken();
						temp = temp.trim();
						temp = temp.substring(0, temp.length() - 1);

						try {
							freeMemory = this.nf.parse(temp).intValue();
						} catch (final Exception e) {
							freeMemory = 0;
						}
						this.virtualMemInfo = new MemoryInfo((usedMemory + freeMemory), usedMemory, freeMemory);
					}
					/*
					 * PID USERNAME LWP PRI NICE SIZE RES STATE TIME CPU COMMAND
					 * 14684 ras 1 0 0 1712K 1048K cpu 0:00 2.08% top
					 */
					else if (bProcesses) {
						if (sLine.indexOf(AbstractProtocolWrapper.APPPERFECT_PROMPT) != -1) {
							break;
						}
						line = new StringTokenizer(sLine);
						temp = line.nextToken(); // PID:
						temp = temp.trim();
						try {
							iPID = this.nf.parse(temp).intValue();
						} catch (final Exception e) {
							iPID = 0;
						}
						sUserName = line.nextToken(); // USERNAME
						temp = line.nextToken(); // LWP
						temp = line.nextToken(); // PRI
						temp = line.nextToken(); // NICE
						temp = line.nextToken(); // SIZE
						temp = line.nextToken(); // RES
						temp = temp.trim();
						temp = temp.substring(0, temp.length() - 1);
						try {
							iMemUsage = this.nf.parse(temp).intValue();
						} catch (final Exception e) {
							iMemUsage = 0;
						}
						temp = line.nextToken(); // STATE,
						temp = line.nextToken(); // TIME
						temp = line.nextToken(); // CPU
						temp = temp.trim();
						temp = temp.substring(0, temp.length() - 1);
						try {
							fCPUUsage = Math.round(this.nf.parse(temp).floatValue());
						} catch (final Exception e) {
							fCPUUsage = 0;
						}
						sProcessName = line.nextToken(); // COMMAND,
						this.fCpuUsageTime += fCPUUsage;

						pInfo = new ProcessInfo(sProcessName, iPID, iMemUsage, fCPUUsage);
						pInfo.setUserName(sUserName);
						this.processInfoList.add(pInfo);
					} else if (sLine.indexOf("PID USERNAME") != -1) {
						if (this.processInfoList == null) {
							this.processInfoList = new LinkedList();
						} else {
							this.processInfoList.clear();
						}
						bProcesses = true;
					}
				}
			} catch (final Exception ex) {
				throw new ResultParsingException("Collect Data " + ex.getMessage(), output);
			}
		}
	}

	public void parsePrstatCommand(final String output) throws ResultParsingException {
		// Command:-prstat -ac -n 25 1
		// ====================================================
		/*
		 * Output : PID USERNAME SIZE RSS STATE PRI NICE TIME CPU PROCESS/NLWP
		 * 430 ras 1424K 1040K cpu0 58 0 0:00.00 0.4% prstat/1 399 ras 6768K
		 * 4216K sleep 59 0 0:00.03 0.3% dtterm/1 262 root 2336K 1880K sleep 48
		 * 0 0:00.00 0.1% mibiisa/12 ...(4th row) ... ...(24th row) 50 root
		 * 1232K 776K sleep 54 0 0:00.00 0.0% devfseventd/7 NPROC USERNAME SIZE
		 * RSS MEMORY TIME CPU 4 ras 10M 6704K 2.7% 0:00.03 0.7% 43 root 133M
		 * 77M 32% 0:00.05 0.1% 1 daemon 2576K 1736K 0.7% 0:00.00 0.0% Total: 48
		 * processes, 130 lwps, load averages: 0.02, 0.04, 0.11
		 * 
		 */
		this.fCpuUsageTime = 0.0f;

		// System.out.println("prstat -ac -n 25 1 output:\n" + output);
		if (output != null) // output will be null if this method gets called
		// after disconnect
		{
			int usedMemory = 0;
			int percentageUsed = 0;
			int usedVirtualMemory = 0; // shows memory in MB
			final int freeVirtualMemory = 0; // shows memory in MB

			ProcessInfo pInfo = null;
			int iPID = 0;
			String sProcessName = null;
			float fCPUUsage = 0.0f;
			int iMemUsage = 0;
			StringTokenizer line = null;
			boolean bProcesses = false;
			boolean bMemoryInfo = false;
			int tmpMemory = 0;

			try {
				String temp = null;
				String sLine = null;
				final StringTokenizer st = new StringTokenizer(output, "\r\n\f");
				while (st.hasMoreTokens()) {
					sLine = st.nextToken();

					/*
					 * NPROC USERNAME SIZE RSS MEMORY TIME CPU 4 ras 10M 6704K
					 * 2.7% 0:00.03 0.7% 43 root 133M 77M 32% 0:00.05 0.1% 1
					 * daemon 2576K 1736K 0.7% 0:00.00 0.0% Total: 48 processes,
					 * 130 lwps, load averages: 0.02, 0.04, 0.11
					 */
					if (bMemoryInfo) {
						if (sLine.indexOf("Total:") != -1) {
							final int totalMemory = ((usedMemory * 100) / percentageUsed) / 1024; // convert
							// to
							// MB
							usedMemory = usedMemory / 1024;
							this.physicalMemInfo = new MemoryInfo(totalMemory, usedMemory, totalMemory - usedMemory);
							usedVirtualMemory = usedVirtualMemory / 1024;
							this.virtualMemInfo = new MemoryInfo((usedVirtualMemory + freeVirtualMemory),
									usedVirtualMemory, freeVirtualMemory);
							bMemoryInfo = false;
							// System.out.println("Physical mem(in MB): total =
							// " + physicalMemInfo.getTotal() +", used = " +
							// physicalMemInfo.getUsed()+ ", Free = " +
							// physicalMemInfo.getAvailable());
						}
						line = new StringTokenizer(sLine);
						temp = line.nextToken(); // Ignore NPROC
						temp = line.nextToken(); // Ignore USERNAME
						temp = line.nextToken(); // Ignore SIZE
						try {
							tmpMemory = this.nf.parse(temp).intValue();
							final int ch = temp.charAt(temp.length() - 1);
							if ((ch == 'M') || (ch == 'm')) {
								tmpMemory = tmpMemory * 1024;
							}
						} catch (final Exception e) {
							tmpMemory = 0;
						}
						usedVirtualMemory += tmpMemory; // in KBytes

						temp = line.nextToken(); // RSS
						try {
							tmpMemory = this.nf.parse(temp).intValue();
							final int ch = temp.charAt(temp.length() - 1);
							if ((ch == 'M') || (ch == 'm')) {
								tmpMemory = tmpMemory * 1024;
							}
						} catch (final Exception e) {
							tmpMemory = 0;
						}
						usedMemory += tmpMemory; // in KBytes

						temp = line.nextToken(); // MEMORY
						try {
							tmpMemory = this.nf.parse(temp).intValue();
						} catch (final Exception e) {
							tmpMemory = 0;
						}
						percentageUsed += tmpMemory; // as %

						temp = line.nextToken(); // Ignore TIME
						temp = line.nextToken(); // Ignore CPU

					}
					if (sLine.indexOf("NPROC USERNAME") != -1) {
						bMemoryInfo = true;
						bProcesses = false;
					}

					if (!bProcesses && sLine.indexOf("load averages:") != -1) {
						String allLoadAvgs = sLine
								.substring(sLine.indexOf("load averages:") + "load averages:".length()).trim();
						float value = 0.0f;
						StringTokenizer stk = new StringTokenizer(allLoadAvgs, ",");
						temp = stk.nextToken().trim();
						try {
							value = this.nf.parse(temp).floatValue();
						} catch (final Exception e) {
							value = 0.0f;
						}
						f1MinLoadAvg = value;
						temp = stk.nextToken().trim();
						try {
							value = this.nf.parse(temp).floatValue();
						} catch (final Exception e) {
							value = 0.0f;
						}
						f5MinLoadAvg = value;
						temp = stk.nextToken().trim();
						try {
							value = this.nf.parse(temp).floatValue();
						} catch (final Exception e) {
							value = 0.0f;
						}
						f15MinLoadAvg = value;
					}

					/*
					 * PID USERNAME SIZE RSS STATE PRI NICE TIME CPU
					 * PROCESS/NLWP 430 ras 1424K 1040K cpu0 58 0 0:00.00 0.4%
					 * prstat/1
					 */
					if (bProcesses) {
						if (sLine.indexOf(AbstractProtocolWrapper.APPPERFECT_PROMPT) != -1) {
							break;
						}
						line = new StringTokenizer(sLine);
						temp = line.nextToken(); // PID:
						temp = temp.trim();
						try {
							iPID = this.nf.parse(temp).intValue();
						} catch (final Exception e) {
							iPID = 0;
						}
						temp = line.nextToken(); // USERNAME
						temp = line.nextToken(); // SIZE
						temp = line.nextToken(); // RSS
						// temp = temp.trim();
						// temp = temp.substring(0, temp.length() - 1);
						try {
							iMemUsage = this.nf.parse(temp).intValue();
							final char ch = temp.charAt(temp.length() - 1);
							if ((ch == 'M') || (ch == 'm')) {
								iMemUsage = iMemUsage * 1024;
							}
						} catch (final Exception e) {
							iMemUsage = 0;
						}
						temp = line.nextToken(); // STATE
						temp = line.nextToken(); // PRI
						temp = line.nextToken(); // NICE
						temp = line.nextToken(); // TIME
						temp = line.nextToken(); // CPU
						// temp = temp.trim();
						temp = temp.substring(0, temp.length() - 1);
						try {
							fCPUUsage = Math.round(this.nf.parse(temp).floatValue());
						} catch (final Exception e) {
							fCPUUsage = 0;
						}
						sProcessName = line.nextToken(); // PROCESS/NLWP,
						if (sProcessName.indexOf('/') != -1) {
							sProcessName = sProcessName.substring(0, sProcessName.lastIndexOf('/'));
						}
						this.fCpuUsageTime += fCPUUsage;

						pInfo = new ProcessInfo(sProcessName, iPID, iMemUsage, fCPUUsage);
						this.processInfoList.add(pInfo);
						// System.out.println("pname = " + pInfo.getName() + ",
						// pid = " + iPID + ", memusage = " +
						// pInfo.getMemoryUsage() );
					}
					if (sLine.indexOf("PID USERNAME") != -1) {
						if (this.processInfoList == null) {
							this.processInfoList = new LinkedList();
						} else {
							this.processInfoList.clear();
						}
						bProcesses = true;
					}
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
				throw new ResultParsingException("Collect Data " + ex.getMessage(), output);
			}
		}
	}

	public void parseNetStatCommand(final String output) throws ResultParsingException {
		/*
		 * netstat -i --- returns the network interface info for all the
		 * interfaces Name Mtu Net/Dest Address Ipkts Ierrs Opkts Oerrs Collis
		 * Queue lo0 8232 loopback localhost 85656 0 85656 0 0 0 hme0 1500
		 * ap-solaris ap-solaris 204292 0 28793 0 0 0
		 * 
		 * Name Mtu Net/Dest Address Ipkts Ierrs Opkts Oerrs Collis lo0 8252
		 * localhost localhost 85656 0 85656 0 0 hme0 1500
		 * fe80::a00:20ff:fe8e:835a/10 fe80::a00:20ff:fe8e:835a 204292 0 28793 0
		 * 0
		 * 
		 * APPPERFECT>
		 */

		if (this.networkInfoList == null) {
			this.networkInfoList = new LinkedList();
		} else {
			this.networkInfoList.clear();
		}
		/*
		 * Read the bytes and create string. Tokenize the string. Discard all
		 * the tokes till "Flags" token and then in next loop start reading
		 * network info. Every line represents a network interface. Read till
		 * the next token begins with "[userid"
		 */
		// System.out.println("netstat -i output:\n" + output);
		if (output != null) // output will be null if this method gets called
		// after disconnect
		{
			/*
			 * if (!output.startsWith("Name ")) { int nameIndex =
			 * output.indexOf("Name "); if (nameIndex != -1) { output =
			 * output.substring(nameIndex); //System.out.println("modified
			 * output >> " + output); } else { output = "Name Mtu Net/Dest
			 * Address Ipkts Ierrs Opkts Oerrs Collis Queue"; } }
			 */
			try {
				String sLine = null;
				final StringTokenizer st = new StringTokenizer(output, "\r\n\f");
				while (st.hasMoreTokens()) {
					sLine = st.nextToken();
					/*
					 * Memory: 256M real, 170M free, 44M swap in use, 651M swap
					 * free
					 */
					if ((sLine.indexOf("Name") != -1) && (sLine.indexOf("Mtu") != -1)) {
						continue;
					}
					if (sLine.startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT)) {
						break;
					}
					final StringTokenizer line = new StringTokenizer(sLine);
					final String name = line.nextToken();
					if (name.startsWith("lo")) {
						continue;
					}
					// read the interface maximum transmission unit -
					// we are not storing this in the struct of network info
					line.nextToken();
					// read the interface name -
					// we are not storing this in the struct of network info
					line.nextToken();
					// read the Address
					line.nextToken();

					// read the recieved packets
					final String recievedOK = line.nextToken();

					// read the recieved Error packets
					line.nextToken();

					// read the transmitted packets
					final String transmittedOK = line.nextToken();

					final long recvOK = Long.parseLong(recievedOK);
					final long xmitOK = Long.parseLong(transmittedOK);

					final UnixNetworkInfo nInfo = new UnixNetworkInfo(name, 0, recvOK, xmitOK);
					this.networkInfoList.add(nInfo);
				}
			} catch (final Exception ex) {
				ex.printStackTrace(System.out);
				throw new ResultParsingException("Network Usage " + ex.getMessage(), output);
			}
		}
	}

	public void parseIOStatCommand(final String output) throws ResultParsingException {
		/*
		 * device r/s w/s kr/s kw/s wait actv svc_t %w %b dad0 1.4 2.7 20.9 45.2
		 * 0.4 0.1 117.1 2 5 fd0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0 0 sd0 0.0 0.0 0.0
		 * 0.0 0.0 0.0 0.0 0 0 nfs1 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0 0
		 */
		/*
		 * Read the bytes and create string. Tokenize the string. Discard all
		 * the tokes till "%b" token and then in next loop start reading disk
		 * info. Every line represents a disk. Read till the next token begins
		 * with "[userid"
		 */
		if (this.diskInfoList == null) {
			this.diskInfoList = new LinkedList();
		} else {
			this.diskInfoList.clear();
		}
		// System.out.println("iostat -x -d 1 output:\n" + output);
		if (output != null) // output will be null if this method gets called
		// after disconnect
		{
			final StringTokenizer st = new StringTokenizer(output);
			try {
				while (st.hasMoreTokens()) {
					final String data = st.nextToken();

					if (data.equals("%b")) {
						break;
					}
				}
				while (st.hasMoreTokens()) {
					// read the disk name
					final String name = st.nextToken();
					if (name.startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT)) {
						break;
					}
					// read the readpersec
					final String sReadpersec = st.nextToken();
					// read the writepersec
					final String sWritepersec = st.nextToken();
					// discard the Kb read/sec

					st.nextToken();
					// discard the Kb written/sec
					st.nextToken();
					// discard the wait
					st.nextToken();
					// discard the actv
					st.nextToken();
					// discard the svc_t
					st.nextToken();
					// discard the %w
					st.nextToken();
					// discard the %b
					st.nextToken();
					final float fReadpersec = Float.parseFloat(sReadpersec);
					final float fWritepersec = Float.parseFloat(sWritepersec);

					final DiskInfo dInfo = new DiskInfo(name, fReadpersec, fWritepersec);
					this.diskInfoList.add(dInfo);
				}
			} catch (final Exception ex) {
				throw new ResultParsingException("Disk Info " + ex.getMessage(), output);
			}
		}
	}

	public void parseDFCommand(final String output) throws ResultParsingException {
		/*
		 * df -k --- returns the disk space info for all the mounted file
		 * systems Format in LINUX is Filesystem 1k-blocks Used Available Use%
		 * Mounted on /dev/hda2 18611412 3591672 14074316 21% / /dev/hda1 46636
		 * 5956 38272 14% /boot none 256184 0 256184 0% /dev/shm
		 */
		/*
		 * Read the bytes and create string. Tokenize the string. Discard all
		 * the tokes till "on" token and then in next loop start reading disk
		 * space info. Every line contains exactly 6 tokens. Read all lines till
		 * the next token begins with "[userid"
		 */
		StringTokenizer st = null;
		if (this.diskSpaceInfoList == null) {
			this.diskSpaceInfoList = new LinkedList();
		} else {
			this.diskSpaceInfoList.clear();
		}
		// System.out.println("df -k output:\n" + output);
		st = new StringTokenizer(output);
		try {
			while (st.hasMoreTokens()) {
				final String temp = st.nextToken();
				if (temp.startsWith("on")) {
					break;
				}
			}

			while (st.hasMoreTokens()) {
				final String sFileSystemName = st.nextToken();
				if (sFileSystemName.startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT)) {
					break;
				}

				final String sTotalSpace = st.nextToken();
				final float fTotalSpace = (float) Integer.parseInt(sTotalSpace) / 1024;

				final String sUsedSpace = st.nextToken();
				final float fUsedSpace = (float) Integer.parseInt(sUsedSpace) / 1024;

				// discard the FreeSpace info
				st.nextToken();

				// discard capacity token
				st.nextToken();
				// discard Mounted on token
				st.nextToken();

				final DriveInfo dInfo = new DriveInfo(sFileSystemName, fTotalSpace, fUsedSpace);
				this.diskSpaceInfoList.add(dInfo);
			}
		} catch (final Exception ex) {
			throw new ResultParsingException("Disk Space Info" + ex.getMessage(), output);
		}
	}

	/*
	 * public static void main(String srgs[]) throws Exception { //
	 * System.out.println("GenericSolarisDataParser.main()...start");
	 * BufferedReader br = new BufferedReader(new FileReader("C:\\vmstat.txt"));
	 * StringBuffer result = new StringBuffer(); String str; while((str =
	 * br.readLine()) != null) { result.append(str); result.append("\n"); }
	 * System.out.println("Output:\n" + result.toString());
	 * GenericSolarisDataParser gsd = new GenericSolarisDataParser();
	 * gsd.parseVmstatCommand(result.toString()); System.out.println("\n\nCPU
	 * Usage: " + gsd.getCPUUsage()); }
	 */
}
