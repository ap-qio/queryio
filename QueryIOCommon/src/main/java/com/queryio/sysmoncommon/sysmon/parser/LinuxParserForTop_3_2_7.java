/**
 * 
 */
package com.queryio.sysmoncommon.sysmon.parser;

import java.util.LinkedList;
import java.util.StringTokenizer;

import com.queryio.sysmoncommon.sysmon.ResultParsingException;
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;

public class LinuxParserForTop_3_2_7 extends GenericLinuxDataParser {
	public LinuxParserForTop_3_2_7() {
		super();
	}

	public void parseTopCommand(final String output) throws ResultParsingException {
		/*
		 * top - 21:14:25 up 66 days, 3:38, 4 users, load average: 0.12, 0.14,
		 * 0.09 Tasks: 376 total, 1 running, 375 sleeping, 0 stopped, 0 zombie
		 * Cpu(s): 1.8%us, 0.4%sy, 0.0%ni, 97.1%id, 0.1%wa, 0.0%hi, 0.7%si,
		 * 0.0%st Mem: 49449564k total, 25302548k used, 24147016k free, 370860k
		 * buffers Swap: 32764556k total, 651832k used, 32112724k free,
		 * 18464664k cached
		 * 
		 * PID USER PR NI VIRT RES SHR S %CPU %MEM TIME+ COMMAND 5376 logez 19 0
		 * 5912m 5.2g 9264 S 17.6 11.0 567:56.01 java 20947 admin 15 0 12868
		 * 1224 736 R 2.0 0.0 0:00.01 top 1 root 15 0 10348 588 560 S 0.0 0.0
		 * 0:02.12 init
		 */
		float value = 0.0f;

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
		 * Read the bytes and create string.
		 */
		if (output != null) // output will be null if this method gets called
							// after disconnect
		{
			try {
				String temp = null;
				String sMem = null;
				String sLine = null;
				String sTemp = null;

				final StringTokenizer st = new StringTokenizer(output, "\r\n\f");
				while (st.hasMoreTokens()) {
					sLine = st.nextToken();
					/*
					 * top - 21:14:25 up 66 days, 3:38, 4 users, load average:
					 * 0.12, 0.14, 0.09
					 */
					if (sLine.indexOf("load average:") != -1) {
						sLine = sLine.substring(sLine.indexOf("load average:") + "load average:".length());
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
					 * Cpu(s): 1.8%us, 0.4%sy, 0.0%ni, 97.1%id, 0.1%wa, 0.0%hi,
					 * 0.7%si, 0.0%st Cpu(s): 0.0%us, 0.0%sy, 0.0%ni,100.0%id,
					 * 0.0%wa, 0.0%hi, 0.0%si, 0.0%st
					 */
					if (sLine.toLowerCase().startsWith("cpu(s):") && (sLine.indexOf("id") != -1)) {
						sLine = sLine.substring(sLine.indexOf("Cpu(s):") + "Cpu(s):".length());
						final StringTokenizer line = new StringTokenizer(sLine, ",");
						while (line.hasMoreTokens()) {
							temp = line.nextToken();
							if (temp.indexOf("id") != -1) {
								temp = temp.substring(0, temp.indexOf("%")).trim();
								try {
									value = this.nf.parse(temp).floatValue();
								} catch (final Exception e) {
									value = 100.0f;
								}
								this.fCpuUsageTime = 100 - value;
							}
							if (temp.indexOf("us") != -1) {
								temp = temp.substring(0, temp.indexOf("%")).trim();
								try {
									value = this.nf.parse(temp).floatValue();
								} catch (final Exception e) {
									value = 0.0f;
								}
								this.fUserCPUUsageTime = value;
							}
							if (temp.indexOf("sy") != -1) {
								temp = temp.substring(0, temp.indexOf("%")).trim();
								try {
									value = this.nf.parse(temp).floatValue();
								} catch (final Exception e) {
									value = 0.0f;
								}
								this.fSysCPUUsageTime = value;
							}
							if (temp.indexOf("wa") != -1) {
								temp = temp.substring(0, temp.indexOf("%")).trim();
								try {
									value = this.nf.parse(temp).floatValue();
								} catch (final Exception e) {
									value = 0.0f;
								}
								this.fWaitCPUUsageTime = value;
							}
						}
					}
					/*
					 * Mem: 49449564k total, 25302548k used, 24147016k free,
					 * 370860k buffers Swap: 32764556k total, 651832k used,
					 * 32112724k free, 18464664k cached
					 */
					if (sLine.startsWith("Mem:") || sLine.startsWith("Swap:")) {
						boolean bSwap = false;
						int memory = 0;
						if (sLine.indexOf("Swap:") != -1) {
							bSwap = true;
							sLine = sLine.substring(sLine.indexOf("Swap:") + "Swap:".length());
						} else {
							sLine = sLine.substring(sLine.indexOf("Mem:") + "Mem:".length());
						}
						final StringTokenizer line = new StringTokenizer(sLine, ",");
						StringTokenizer token = null;
						while (line.hasMoreTokens()) {
							temp = line.nextToken();
							if (temp.indexOf("used") != -1 || temp.indexOf("free") != -1) {
								token = new StringTokenizer(temp);
								sTemp = token.nextToken();
								if (sTemp.toLowerCase().endsWith("m") || sTemp.toLowerCase().endsWith("g")
										|| sTemp.toLowerCase().endsWith("k")) {
									sMem = sTemp.substring(0, sTemp.length() - 1).trim();
									try {
										memory = this.nf.parse(sMem).intValue();
										final char ch = sTemp.charAt(sTemp.length() - 1);
										if ((ch == 'G') || (ch == 'g')) {
											memory *= 1024;
										}
										if ((ch == 'K') || (ch == 'k')) {
											memory /= 1024;
										}
									} catch (final Exception e) {
										memory = 0;
									}
								}
								if (temp.indexOf("used") != -1) {
									if (bSwap) {
										usedVirtualMemory = memory;
									} else {
										usedMemory = memory;
									}
								} else if (temp.indexOf("free") != -1) {
									if (bSwap) {
										freeVirtualMemory = memory;
									} else {
										freeMemory = memory;
									}
								}
							}
						}
						if (bSwap) {
							this.virtualMemInfo = new MemoryInfo((usedVirtualMemory + freeVirtualMemory),
									usedVirtualMemory, freeVirtualMemory);
						} else {
							this.physicalMemInfo = new MemoryInfo((usedMemory + freeMemory), usedMemory, freeMemory);
						}
					}
					/*
					 * PID USER PR NI VIRT RES SHR S %CPU %MEM TIME+ COMMAND 1
					 * root 15 0 6120 676 560 S 0 0.0 0:02.93 init PID USERNAME
					 * PRI NICE SIZE RES STATE TIME WCPU CPU COMMAND 1 root 15 0
					 * 1364K 80K sleep 0:03 0.00% 0.00% init
					 */
					if (bProcesses && this.monitorProcesses) {
						final StringTokenizer line = new StringTokenizer(sLine);
						temp = line.nextToken(); // PID:
						try {
							iPID = this.nf.parse(temp).intValue();
						} catch (final Exception e) {
							iPID = 0;
						}
						sUserName = line.nextToken(); // SKIP USERNAME
						line.nextToken(); // SKIP PRI
						line.nextToken(); // SKIP NICE
						line.nextToken(); // SKIP SIZE
						// read RES i.e. memory used
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
						line.nextToken(); // SKIP SHR
						line.nextToken(); // SKIP S
						// read %CPU i.e. cpu usage
						temp = line.nextToken();
						try {
							fCpuUsage = this.nf.parse(temp).floatValue();
						} catch (final Exception e) {
							fCpuUsage = 0;
						}
						line.nextToken(); // SKIP %MEM
						line.nextToken(); // SKIP TIME+

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
					if (sLine.indexOf("PID") != -1 && sLine.indexOf("USER") != -1 && this.monitorProcesses) {
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
