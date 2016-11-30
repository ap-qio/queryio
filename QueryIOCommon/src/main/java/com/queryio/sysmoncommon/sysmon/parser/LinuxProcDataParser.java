/**
 * 
 */
package com.queryio.sysmoncommon.sysmon.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import com.queryio.sysmoncommon.sysmon.ResultParsingException;
import com.queryio.sysmoncommon.sysmon.dstruct.AIXProcessInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NFSStat;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.UnixNetworkInfo;
import com.queryio.sysmoncommon.sysmon.protocol.AbstractProtocolWrapper;

/**
 * @author Sudhan Moghe
 *
 */
public class LinuxProcDataParser extends GenericLinuxDataParser {
	// private static int linux_version_major;
	// private static int linux_version_minor;
	// private static int linux_version_patch;
	// private boolean useDiskStats = false;
	private boolean bFirstTimeCpuUsage = true;
	private long lastUserCPUTime;
	private long lastNiceCPUTime;
	// private long lastTimeStamp;
	private long lastSystemCPUTime;
	private long lastIdleCPUTime;
	private long lastWaitCPUTime;
	private long lastTotalCPUTime;

	private String iostatGrepDisks = null;

	public void setIostatGrepDisks(String iostatGrepDisks) {
		this.iostatGrepDisks = iostatGrepDisks;
	}

	// public LinuxProcDataParser(String sVersion)
	// {
	// /*
	// * Result is of the form
	// * Linux version 2.6.18-92.el5 (brewbuilder@ls20-bc2-13.build.redhat.com)
	// (gcc version 4.1.2 20071124 (Red Hat 4.1.2-41)) #1 SMP Tue Apr 29
	// 13:16:15 EDT 2008
	// */
	// String[] versionStringTokens = split(sVersion, WHITE_SPACE_PATTERN, 4);
	// //We are interested in 3rd token, no need to parse beyond that
	// if (versionStringTokens.length > 3)
	// {
	// String[] versionTokens = split(versionStringTokens[2], "[\\.-]", 0);
	// linux_version_major = Integer.parseInt(versionTokens[0]);
	// linux_version_minor = Integer.parseInt(versionTokens[1]);
	// linux_version_patch = Integer.parseInt(versionTokens[2]);
	// }
	// if (linux_version_major == 2 && (linux_version_minor > 5 ||
	// (linux_version_minor == 5 && linux_version_patch >= 69)))
	// {
	// this.useDiskStats = true;
	// }
	// }
	//
	// public boolean isUseDiskStats()
	// {
	// return useDiskStats;
	// }

	public void parseLoadAverages(String result) {

		if (result != null && result.trim().length() > 0) {
			// Result is of the form "0.20 0.18 0.12 1/80 11206";
			String[] loadAvgs = split(result, WHITE_SPACE_PATTERN, 4);

			f1MinLoadAvg = Float.parseFloat(loadAvgs[0]);
			f5MinLoadAvg = Float.parseFloat(loadAvgs[1]);
			f15MinLoadAvg = Float.parseFloat(loadAvgs[2]);
		}
	}

	public void parseProcStat(String result) {
		if (result != null && result.trim().length() > 0) {
			/*
			 * Result is of the form Format varies with architecture but the
			 * first 4 values we are interested in are common.
			 * 
			 * @formatter:off cpu 209841 1554 21720 118519346 72939 154 27168
			 * cpu0 42536 798 4841 14790880 14778 124 3117 cpu1 24184 569 3875
			 * 14794524 30209 29 3130 cpu2 28616 11 2182 14818198 4020 1 3493
			 * cpu3 35350 6 2942 14811519 3045 0 3659 cpu4 18209 135 2263
			 * 14820076 12465 0 3373 cpu5 20795 35 1866 14825701 4508 0 3615
			 * cpu6 21607 0 2201 14827053 2325 0 3334 cpu7 18544 0 1550 14831395
			 * 1589 0 3447 intr 15239682 14857833 6 0 6 6 0 5 0 1 0 0 0 29 0 2 0
			 * 0 0 0 0 0 0 94982 0 286812 ctxt 4209609 btime 1078711415
			 * processes 21905 procs_running 1 procs_blocked 0
			 * 
			 * @formatter:on Where cpu � Measures the number of jiffies (1/100
			 * of a second for x86 systems) that the system has been in user
			 * mode, user mode with low priority (nice), system mode, idle task,
			 * I/O wait, IRQ (hardirq), and softirq respectively. The IRQ
			 * (hardirq) is the direct response to a hardware event. The IRQ
			 * takes minimal work for queuing the "heavy" work up for the
			 * softirq to execute. The softirq runs at a lower priority than the
			 * IRQ and therefore may be interrupted more frequently. The total
			 * for all CPUs is given at the top, while each individual CPU is
			 * listed below with its own statistics. The following example is a
			 * 4-way Intel Pentium Xeon configuration with multi-threading
			 * enabled, therefore showing four physical processors and four
			 * virtual processors totaling eight processors.
			 */

			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug("CPU Result: " + result);

			String[] cpuLines = split(result, NEW_LINE_PATTERN, 0);
			long userCPUTime = 0;
			long niceCPUTime = 0;
			long systemCPUTime = 0;
			long idleCPUTime = 0;
			long waitCPUTime = 0;
			long totalCPUTime = 0;

			for (int i = 0; i < cpuLines.length; i++) {
				if (cpuLines[i].startsWith("cpu")) {
					String[] cpuTimes = split(cpuLines[i], WHITE_SPACE_PATTERN, 0);

					if (cpuTimes[0].equals("cpu")) {
						userCPUTime = Long.parseLong(cpuTimes[1]);
						niceCPUTime = Long.parseLong(cpuTimes[2]);
						systemCPUTime = Long.parseLong(cpuTimes[3]);
						idleCPUTime = Long.parseLong(cpuTimes[4]);
						waitCPUTime = Long.parseLong(cpuTimes[5]);
						totalCPUTime = userCPUTime + niceCPUTime + systemCPUTime + idleCPUTime + waitCPUTime;
						for (int j = 6; j < cpuTimes.length; j++) {
							totalCPUTime += Long.parseLong(cpuTimes[j]);
						}
					}
				}
			}

			if (!bFirstTimeCpuUsage) {
				// calculate the value by finding difference from last fetch
				float divisor = 100f;
				long timeDiffInJiffies = totalCPUTime - lastTotalCPUTime;
				fUserCPUUsageTime = ((float) (userCPUTime + niceCPUTime - (lastUserCPUTime + lastNiceCPUTime))
						/ timeDiffInJiffies) * divisor;
				fSysCPUUsageTime = ((float) (systemCPUTime - lastSystemCPUTime) / timeDiffInJiffies) * divisor;
				// fCpuUsageTime = 100 - (((float) (idleCPUTime -
				// lastIdleCPUTime) / timeDiffInJiffies) * divisor);
				fCpuUsageTime = (((float) (timeDiffInJiffies - (idleCPUTime - lastIdleCPUTime)) / timeDiffInJiffies)
						* divisor);
				fWaitCPUUsageTime = ((float) (waitCPUTime - lastWaitCPUTime) / timeDiffInJiffies) * divisor;
				if (fCpuUsageTime < 0) {
					fCpuUsageTime = fUserCPUUsageTime + fSysCPUUsageTime + fWaitCPUUsageTime;
				}
			} else {
				bFirstTimeCpuUsage = false;
			}

			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug("fUserCPUUsageTime: " +
			// fUserCPUUsageTime);
			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug("fSysCPUUsageTime: " +
			// fSysCPUUsageTime);
			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug("fCpuUsageTime: " + fCpuUsageTime);
			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug("fWaitCPUUsageTime: " +
			// fWaitCPUUsageTime);

			lastUserCPUTime = userCPUTime;
			lastNiceCPUTime = niceCPUTime;
			lastSystemCPUTime = systemCPUTime;
			lastIdleCPUTime = idleCPUTime;
			lastWaitCPUTime = waitCPUTime;
			lastTotalCPUTime = totalCPUTime;
		}
	}

	public void parseProcMeminfo(String result) {
		if (result != null && result.trim().length() > 0) {
			/*
			 * Result is of the form
			 * 
			 * @formatter:off MemTotal: 255908 kB MemFree: 69936 kB Buffers:
			 * 15812 kB Cached: 115124 kB SwapCached: 0 kB Active: 92700 kB
			 * Inactive: 63792 kB HighTotal: 0 kB HighFree: 0 kB LowTotal:
			 * 255908 kB LowFree: 69936 kB SwapTotal: 524280 kB SwapFree: 524280
			 * kB Dirty: 4 kB Writeback: 0 kB Mapped: 42236 kB Slab: 25912 kB
			 * Committed_AS: 118680 kB PageTables: 1236 kB VmallocTotal: 3874808
			 * kB VmallocUsed: 1416 kB VmallocChunk: 3872908 kB HugePages_Total:
			 * 0 HugePages_Free: 0 Hugepagesize: 4096 kB
			 * 
			 * From RHL-7.2-Manual total: used: free: shared: buffers: cached:
			 * Mem: 261709824 253407232 8302592 0 120745984 48689152 Swap:
			 * 402997248 8192 402989056 MemTotal: 255576 kB MemFree: 8108 kB
			 * MemShared: 0 kB Buffers: 117916 kB Cached: 47548 kB Active:
			 * 135300 kB Inact_dirty: 29276 kB Inact_clean: 888 kB Inact_target:
			 * 0 kB HighTotal: 0 kB HighFree: 0 kB LowTotal: 255576 kB LowFree:
			 * 8108 kB SwapTotal: 393552 kB SwapFree: 393544 kB
			 * 
			 * @formatter:on
			 */

			String[] memoryInfoLines = split(result, NEW_LINE_PATTERN, 0);
			int physicalMemoryTotal = 0;
			int physicalMemoryFree = 0;
			int virtualMemoryTotal = 0;
			int virtualMemoryFree = 0;
			for (int i = 0; i < memoryInfoLines.length; i++) {
				if (memoryInfoLines[i].startsWith("MemTotal:")) {
					String sMemTotal = split(memoryInfoLines[i], WHITE_SPACE_PATTERN, 0)[1];
					physicalMemoryTotal = Integer.parseInt(sMemTotal) / 1024; // Convert
																				// to
																				// MB

				} else if (memoryInfoLines[i].startsWith("MemFree:")) {
					String sMemFree = split(memoryInfoLines[i], WHITE_SPACE_PATTERN, 0)[1];
					physicalMemoryFree = Integer.parseInt(sMemFree) / 1024; // Convert
																			// to
																			// MB
				} else if (memoryInfoLines[i].startsWith("SwapTotal:")) {
					String sSwapTotal = split(memoryInfoLines[i], WHITE_SPACE_PATTERN, 0)[1];
					virtualMemoryTotal = Integer.parseInt(sSwapTotal) / 1024; // Convert
																				// to
																				// MB
				} else if (memoryInfoLines[i].startsWith("SwapFree:")) {
					String sSwapFree = split(memoryInfoLines[i], WHITE_SPACE_PATTERN, 0)[1];
					virtualMemoryFree = Integer.parseInt(sSwapFree) / 1024; // Convert
																			// to
																			// MB
					break;
				}
			}
			this.physicalMemInfo = new MemoryInfo(physicalMemoryTotal, (physicalMemoryTotal - physicalMemoryFree),
					physicalMemoryFree);
			this.virtualMemInfo = new MemoryInfo(virtualMemoryTotal, (virtualMemoryTotal - virtualMemoryFree),
					virtualMemoryFree);
		}
	}

	public void parseTopCommand(String output) throws ResultParsingException {
		// Do Nothing
	}

	public void parseNetStatCommand(String output) throws ResultParsingException {
		if (this.networkInfoList == null) {
			this.networkInfoList = new LinkedList();
		} else {
			this.networkInfoList.clear();
		}
		if (output != null && output.trim().length() > 0) {
			/*
			 * Result is of the form
			 * 
			 * @formatter:off Inter-| Receive | Transmit face |bytes packets
			 * errs drop fifo frame compressed multicast|bytes packets errs drop
			 * fifo colls carrier compressed lo: 2776770 11307 0 0 0 0 0 0
			 * 2776770 11307 0 0 0 0 0 0 eth0: 1215645 2751 0 0 0 0 0 0 1782404
			 * 4324 0 0 0 427 0 0 ppp0: 1622270 5552 1 0 0 0 0 0 354130 5669 0 0
			 * 0 0 0 0 tap0: 7714 81 0 0 0 0 0 0 7714 81 0 0 0 0 0 0
			 * 
			 * @formatter:on
			 */
			String[] networkInfoLines = split(output, NEW_LINE_PATTERN, 0);
			for (int i = 2; i < networkInfoLines.length; ++i) {
				if (networkInfoLines[i].startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT)) {
					break;
				}
				if (networkInfoLines[i].trim().startsWith("lo:")) {
					continue;
				}
				String[] networkInfo = split(networkInfoLines[i], "[\\s+:]", 0);
				String name = networkInfo[0].trim();
				// Note: If required we can find out the index from the header
				// line and use them instead of the hard coded ones.
				long bytesRecv = Long.parseLong(networkInfo[1]) / 1024;
				long bytesSend = Long.parseLong(networkInfo[9]) / 1024;

				final UnixNetworkInfo nInfo = new UnixNetworkInfo(name, 0, bytesRecv, bytesSend);
				this.networkInfoList.add(nInfo);
			}
		}
	}

	public void parseIOStatCommand(final String output) throws ResultParsingException {
		/*
		 * iostat -x -d --- returns the disk statistics for all the disks
		 * Device: tps kB_read/s kB_wrtn/s kB_read kB_wrtn sda 11.70 4.78 206.17
		 * 27307739 1178443612 sdb 0.00 0.00 0.59 4103 3351548 sdc 0.00 0.00
		 * 0.00 1678 8384 dm-0 0.15 0.00 0.59 2601 3359812
		 */
		/*
		 * Read the bytes and create string. Tokenize the string. Discard all
		 * the tokes till "Blk_wrtn" token and then in next loop start reading
		 * disk info. Every line represents a disk. Read till the next token
		 * begins with "[userid"
		 */

		String sLine = null;
		String tempName = null;
		if (output != null) // result will be null if this method gets called
							// after disconnect
		{
			boolean bDisks = false;
			boolean bNFSDisks = false;
			boolean cleared = false;
			final StringTokenizer st = new StringTokenizer(output, "\r\n\f");
			while (st.hasMoreTokens()) {
				sLine = st.nextToken();
				if (sLine.indexOf("Device:") != -1
						&& (sLine.indexOf("kB_read/s") != -1 || sLine.indexOf("rkB_nor/s") != -1)) {
					bDisks = sLine.indexOf("kB_read/s") != -1;
					bNFSDisks = sLine.indexOf("rkB_nor/s") != -1;

					if (!cleared) {
						cleared = true;
						if (this.diskInfoList == null) {
							this.diskInfoList = new LinkedList();
						} else {
							this.diskInfoList.clear();
						}
					}
					continue;
				}

				if (sLine.trim().length() > 0) {
					if (bDisks) {
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
					} else if (bNFSDisks) {
						// Device: rkB_nor/s wkB_nor/s rkB_dir/s wkB_dir/s
						// rkB_svr/s wkB_svr/s rops/s wops/s
						final StringTokenizer line = new StringTokenizer(sLine);
						tempName = line.nextToken(); // Device Name:
						if (iostatGrepDisks == null || tempName.indexOf(iostatGrepDisks) != -1) {
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
		}
	}

	public List parseProcessIdList(String output) throws ResultParsingException {
		ArrayList pidList = new ArrayList();
		if (output != null && output.trim().length() > 0) {
			String[] procFiles = split(output, WHITE_SPACE_PATTERN, 0);
			for (int i = 0; i < procFiles.length; ++i) {
				if (DECIMAL_NUMBER_PATTERN.matcher(procFiles[i]).matches()) {
					pidList.add(procFiles[i]);
				}
			}
		}
		noOfProcs = pidList.size();
		return pidList;
	}

	// Parse /proc/PID/stat of single process
	public void parseProcProcessInfo(String output, boolean psResult) throws ResultParsingException {
		if (psResult) {
			super.parsePrstatCommand(output);
			if (processInfoList.size() > 0) {
				// set PID in process filter, so that we start using /proc file
				ProcessInfo pInfo = (ProcessInfo) processInfoList.get(0);
				try {
					processFilter.setPid(pInfo.getProcessID());
				} catch (Exception e) {
					// ignore
				}
			}
			return;
		}
		if (output != null && output.trim().length() > 0) {
			/*
			 * Format of the result
			 * 
			 * @formatter:off
			 * 
			 * 1 (init) S 0 0 0 0 -1 8388864 3706 282132 19 212 3 154 284 279 16
			 * 0 1 6714 115 1626112 130 4294967295 134512640 134540142
			 * 2948335296 2948334000 2817780216 0 0 1475401980 671819267 0 0 0 0
			 * 0 0 0
			 * 
			 * Field Mapping is as follows pid: 1 tcomm: (init) state: S ppid: 0
			 * pgid: 0 sid: 0 tty_nr: 0 tty_pgrp: -1 flags: 8388864 min_flt:
			 * 3706 cmin_flt: 282132 maj_flt: 19 cmaj_flt: 212 utime: 0.030000
			 * stime: 1.540000 cutime: 2.840000 cstime: 2.790000 priority: 16
			 * nice: 0 num_threads: 1 it_real_value: 67.140000 start_time: 11.07
			 * 09:07 (11158.38s) vsize: 1626112 rss: 130 rsslim: 4294967295
			 * start_code: 134512640 end_code: 134540142 start_stack: 2948335296
			 * esp: 2948334000 eip: 2817780216 pending: 0000000000000000
			 * blocked: 0000000000000000 sigign: 0000000057f0d8fc sigcatch:
			 * 00000000280b2603 wchan: 0 zero1: 0 zero2: 0 exit_signal:
			 * 0000000000000000 cpu: 0 rt_priority: 0 policy: 0
			 * 
			 * 
			 * Field Content pid process id tcomm filename of the executable
			 * state state (R is running, S is sleeping, D is sleeping in an
			 * uninterruptible wait, Z is zombie, T is traced or stopped) ppid
			 * process id of the parent process pgrp pgrp of the process sid
			 * session id tty_nr tty the process uses tty_pgrp pgrp of the tty
			 * flags task flags min_flt number of minor faults cmin_flt number
			 * of minor faults with child's maj_flt number of major faults
			 * cmaj_flt number of major faults with child's utime user mode
			 * jiffies stime kernel mode jiffies cutime user mode jiffies with
			 * child's cstime kernel mode jiffies with child's priority priority
			 * level nice nice level num_threads number of threads it_real_value
			 * (obsolete, always 0) start_time time the process started after
			 * system boot vsize virtual memory size rss resident set memory
			 * size rsslim current limit in bytes on the rss start_code address
			 * above which program text can run end_code address below which
			 * program text can run start_stack address of the start of the
			 * stack esp current value of ESP eip current value of EIP pending
			 * bitmap of pending signals blocked bitmap of blocked signals
			 * sigign bitmap of ignored signals sigcatch bitmap of catched
			 * signals wchan address where process went to sleep 0 (place
			 * holder) 0 (place holder) exit_signal signal to send to parent
			 * thread on exit task_cpu which CPU the task is scheduled on
			 * rt_priority realtime priority policy scheduling policy (man
			 * sched_setscheduler) blkio_ticks time spent waiting for block IO
			 * gtime guest time of the task in jiffies cgtime guest time of the
			 * task children in jiffies
			 * 
			 * @formatter:on
			 */
			final StringTokenizer st = new StringTokenizer(output, "\r\n\f");
			while (st.hasMoreTokens()) {
				String line = st.nextToken();
				String[] processInfoValues = split(line, WHITE_SPACE_PATTERN, 0);
				int iPID = 0;
				String sProcessName = null;
				String sUserName = "Not Resolved"; // FIXME
				int iProcessMemory = 0;
				long upTime = 0;
				long processorTime = 0;
				int iThreadCount = 0;

				if (processInfoValues.length > 22) {
					iPID = Integer.parseInt(processInfoValues[0]);
					sProcessName = processInfoValues[1];
					if (sProcessName != null && sProcessName.charAt(0) == '(') {
						sProcessName = sProcessName.substring(1, sProcessName.length() - 1);
					}
					// sUserName= //TODO: We can get UID from /proc/PID/status
					processorTime = Integer.parseInt(processInfoValues[13]) + Integer.parseInt(processInfoValues[14]);
					iThreadCount = Integer.parseInt(processInfoValues[19]);
					iProcessMemory = (int) (Long.parseLong(processInfoValues[22]) / 1024); // Converted
																							// to
																							// KB

					AIXProcessInfo pInfo = new AIXProcessInfo(sProcessName, iPID, iProcessMemory, iThreadCount, upTime,
							processorTime);
					// TODO: Need to read /proc/pid/cmdline to get command with
					// all parameters
					pInfo.setCommand(sProcessName);
					pInfo.setUserName(sUserName);
					if (this.processFilter == null || this.processFilter.matches(pInfo)) {
						this.processInfoList.add(pInfo);
						if (this.processFilter != null) {
							return;
						}
					}
				} /*
					 * else { System.out.println("Wrong process: " + line); }
					 */

			}
		}
	}

	public void parseNSFStatCommand(String clientStats, String serverStats) {
		if (this.nfsStats == null) {
			this.nfsStats = new NFSStat();
		}

		if (clientStats != null && clientStats.trim().length() > 0) {
			/*
			 * Format of the result
			 * 
			 * @formatter:off net 0 0 0 0 rpc 3677368 0 0 proc2 18 0 0 0 0 0 0 0
			 * 0 0 0 0 0 0 0 0 0 0 0 proc3 22 0 893490 13013 33641 412691 6
			 * 876145 1418606 12963 781 16 0 10408 390 1059 0 0 2052 2082 20 2 0
			 * proc4 35 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
			 * 0 0 0 0 0 0 0
			 * 
			 * 
			 * Fields Ref
			 * (http://www.sourcefiles.org/Log_Analyzers/nfsstats-0.1.pl) net -
			 * ignore RPC 1 => calls 2 => retrans 3 => authrefrsh
			 * 
			 * proc 0 => name 1 => number of remaining fields on the line V2 2
			 * => null, 3 => getattr, 4 => setattr, 5 => root, 6 => lookup, 7 =>
			 * readlink, 8 => read, 9 => wrcache, 10 => write, 11 => create 12
			 * => remove, 13 => rename, 14 => link, 15 => symlink, 16 => mkdir,
			 * 17 => rmdir, 18 => readdir, 19 => fsstat
			 * 
			 * V3 2 => null, 3 => getattr, 4 => setattr, 5 => lookup, 6 =>
			 * access, 7 => readlink, 8 => read, 9 => write, 10 => create, 11 =>
			 * mkdir, 12 => symlink, 13 => mknod, 14 => remove, 15 => rmdir, 16
			 * => rename, 17 => link, 18 => readdir, 19 => readdirplus, 20 =>
			 * fsstat, 21 => fsinfo, 22 => pathconf, 23 => commit
			 * 
			 * V4 2 => null, 3 => read, 4 => write, 5 => commit, 6 => open, 7 =>
			 * open_conf, 8 => open_noat, 9 => open_dgrd, 10 => close, 11 =>
			 * setattr 12 => fsinfo, 13 => renew, 14 => setclntid, 15 =>
			 * confirm, 16 => lock, 17 => lockt, 18 => locku, 19 => access, 20
			 * => getattr, 21 => lookup, 22 => lookup_root, 23 => remove, 24 =>
			 * rename, 25 => link, 26 => symlink, 27 => create, 28 => pathconf,
			 * 29 => statfs 30 => readlink, 31 => readdir, 32 => server_caps, 33
			 * => delegreturn
			 * 
			 * @formatter:on
			 */

			String[] nfsstatsLines = split(clientStats, NEW_LINE_PATTERN, 0);
			for (int i = 0; i < nfsstatsLines.length; i++) {

				if (nfsstatsLines[i].startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT)) {
					break;
				}
				if (nfsstatsLines[i].trim().length() == 0) {
					continue;
				}
				String[] nfsStatsArr = split(nfsstatsLines[i], WHITE_SPACE_PATTERN, 0);
				if ("net".equals(nfsStatsArr[0])) {
					continue;
				}
				if ("rpc".equals(nfsStatsArr[0])) {
					// 1 => calls
					nfsStats.setClientStatsValue("rpc", "calls", new Long(nfsStatsArr[1]));
					// 2 => retrans
					nfsStats.setClientStatsValue("rpc", "retrans", new Long(nfsStatsArr[2]));
					// 3 => authrefrsh
					nfsStats.setClientStatsValue("rpc", "authrefrsh", new Long(nfsStatsArr[3]));
				} else if ("proc2".equals(nfsStatsArr[0])) {
					if (Integer.parseInt(nfsStatsArr[3]) > 0 && nfsStatsArr.length > 19) {
						// 2 => null
						nfsStats.setClientStatsValue("nfs v2", "null", new Long(nfsStatsArr[2]));
						// 3 => getattr
						nfsStats.setClientStatsValue("nfs v2", "getattr", new Long(nfsStatsArr[3]));
						// 4 => setattr
						nfsStats.setClientStatsValue("nfs v2", "setattr", new Long(nfsStatsArr[4]));
						// 5 => root
						nfsStats.setClientStatsValue("nfs v2", "root", new Long(nfsStatsArr[5]));
						// 6 => lookup
						nfsStats.setClientStatsValue("nfs v2", "lookup", new Long(nfsStatsArr[6]));
						// 7 => readlink
						nfsStats.setClientStatsValue("nfs v2", "readlink", new Long(nfsStatsArr[7]));
						// 8 => read
						nfsStats.setClientStatsValue("nfs v2", "read", new Long(nfsStatsArr[8]));
						// 9 => wrcache
						nfsStats.setClientStatsValue("nfs v2", "wrcache", new Long(nfsStatsArr[9]));
						// 10 => write
						nfsStats.setClientStatsValue("nfs v2", "write", new Long(nfsStatsArr[10]));
						// 11 => create
						nfsStats.setClientStatsValue("nfs v2", "create", new Long(nfsStatsArr[11]));
						// 12 => remove
						nfsStats.setClientStatsValue("nfs v2", "remove", new Long(nfsStatsArr[12]));
						// 13 => rename
						nfsStats.setClientStatsValue("nfs v2", "rename", new Long(nfsStatsArr[13]));
						// 14 => link
						nfsStats.setClientStatsValue("nfs v2", "link", new Long(nfsStatsArr[14]));
						// 15 => symlink
						nfsStats.setClientStatsValue("nfs v2", "symlink", new Long(nfsStatsArr[15]));
						// 16 => mkdir
						nfsStats.setClientStatsValue("nfs v2", "mkdir", new Long(nfsStatsArr[16]));
						// 17 => rmdir
						nfsStats.setClientStatsValue("nfs v2", "rmdir", new Long(nfsStatsArr[17]));
						// 18 => readdir
						nfsStats.setClientStatsValue("nfs v2", "readdir", new Long(nfsStatsArr[18]));
						// 19 => fsstat
						nfsStats.setClientStatsValue("nfs v2", "fsstat", new Long(nfsStatsArr[19]));
					}
				} else if ("proc3".equals(nfsStatsArr[0])) {
					if (Integer.parseInt(nfsStatsArr[3]) > 0 && nfsStatsArr.length > 23) {
						// 2 => null
						nfsStats.setClientStatsValue("nfs v3", "null", new Long(nfsStatsArr[2]));
						// 3 => getattr
						nfsStats.setClientStatsValue("nfs v3", "getattr", new Long(nfsStatsArr[3]));
						// 4 => setattr
						nfsStats.setClientStatsValue("nfs v3", "setattr", new Long(nfsStatsArr[4]));
						// 5 => lookup
						nfsStats.setClientStatsValue("nfs v3", "lookup", new Long(nfsStatsArr[5]));
						// 6 => access
						nfsStats.setClientStatsValue("nfs v3", "access", new Long(nfsStatsArr[6]));
						// 7 => readlink
						nfsStats.setClientStatsValue("nfs v3", "readlink", new Long(nfsStatsArr[7]));
						// 8 => read
						nfsStats.setClientStatsValue("nfs v3", "read", new Long(nfsStatsArr[8]));
						// 9 => write
						nfsStats.setClientStatsValue("nfs v3", "write", new Long(nfsStatsArr[9]));
						// 10 => create
						nfsStats.setClientStatsValue("nfs v3", "create", new Long(nfsStatsArr[10]));
						// 11 => mkdir
						nfsStats.setClientStatsValue("nfs v3", "mkdir", new Long(nfsStatsArr[11]));
						// 12 => symlink
						nfsStats.setClientStatsValue("nfs v3", "symlink", new Long(nfsStatsArr[12]));
						// 13 => mknod
						nfsStats.setClientStatsValue("nfs v3", "mknod", new Long(nfsStatsArr[13]));
						// 14 => remove
						nfsStats.setClientStatsValue("nfs v3", "remove", new Long(nfsStatsArr[14]));
						// 15 => rmdir
						nfsStats.setClientStatsValue("nfs v3", "rmdir", new Long(nfsStatsArr[15]));
						// 16 => rename
						nfsStats.setClientStatsValue("nfs v3", "rename", new Long(nfsStatsArr[16]));
						// 17 => link
						nfsStats.setClientStatsValue("nfs v3", "link", new Long(nfsStatsArr[17]));
						// 18 => readdir
						nfsStats.setClientStatsValue("nfs v3", "readdir", new Long(nfsStatsArr[18]));
						// 19 => readdirplus
						nfsStats.setClientStatsValue("nfs v3", "readdirplus", new Long(nfsStatsArr[19]));
						// 20 => fsstat
						nfsStats.setClientStatsValue("nfs v3", "fsstat", new Long(nfsStatsArr[20]));
						// 21 => fsinfo
						nfsStats.setClientStatsValue("nfs v3", "fsinfo", new Long(nfsStatsArr[21]));
						// 22 => pathconf
						nfsStats.setClientStatsValue("nfs v3", "pathconf", new Long(nfsStatsArr[22]));
						// 23 => commit
						nfsStats.setClientStatsValue("nfs v3", "commit", new Long(nfsStatsArr[23]));
					}
				} else if ("proc4".equals(nfsStatsArr[0])) {
					if (Integer.parseInt(nfsStatsArr[3]) > 0 && nfsStatsArr.length > 33) {
						// 2 => null
						nfsStats.setClientStatsValue("nfs v4", "null", new Long(nfsStatsArr[2]));
						// 3 => read
						nfsStats.setClientStatsValue("nfs v4", "read", new Long(nfsStatsArr[3]));
						// 4 => write
						nfsStats.setClientStatsValue("nfs v4", "write", new Long(nfsStatsArr[4]));
						// 5 => commit
						nfsStats.setClientStatsValue("nfs v4", "commit", new Long(nfsStatsArr[5]));
						// 6 => open
						nfsStats.setClientStatsValue("nfs v4", "open", new Long(nfsStatsArr[6]));
						// 7 => open_conf
						nfsStats.setClientStatsValue("nfs v4", "openconf", new Long(nfsStatsArr[7]));
						// 8 => open_noat
						nfsStats.setClientStatsValue("nfs v4", "opennoat", new Long(nfsStatsArr[8]));
						// 9 => open_dgrd
						nfsStats.setClientStatsValue("nfs v4", "opendgrd", new Long(nfsStatsArr[9]));
						// 10 => close
						nfsStats.setClientStatsValue("nfs v4", "close", new Long(nfsStatsArr[10]));
						// 11 => setattr
						nfsStats.setClientStatsValue("nfs v4", "setattr", new Long(nfsStatsArr[11]));
						// 12 => fsinfo
						nfsStats.setClientStatsValue("nfs v4", "fsinfo", new Long(nfsStatsArr[12]));
						// 13 => renew
						nfsStats.setClientStatsValue("nfs v4", "renew", new Long(nfsStatsArr[13]));
						// 14 => setclntid
						nfsStats.setClientStatsValue("nfs v4", "setclntid", new Long(nfsStatsArr[14]));
						// 15 => confirm
						nfsStats.setClientStatsValue("nfs v4", "confirm", new Long(nfsStatsArr[15]));
						// 16 => lock
						nfsStats.setClientStatsValue("nfs v4", "lock", new Long(nfsStatsArr[16]));
						// 17 => lockt
						nfsStats.setClientStatsValue("nfs v4", "lockt", new Long(nfsStatsArr[17]));
						// 18 => locku
						nfsStats.setClientStatsValue("nfs v4", "locku", new Long(nfsStatsArr[18]));
						// 19 => access
						nfsStats.setClientStatsValue("nfs v4", "access", new Long(nfsStatsArr[19]));
						// 20 => getattr
						nfsStats.setClientStatsValue("nfs v4", "getattr", new Long(nfsStatsArr[20]));
						// 21 => lookup
						nfsStats.setClientStatsValue("nfs v4", "lookup", new Long(nfsStatsArr[21]));
						// 22 => lookup_root
						nfsStats.setClientStatsValue("nfs v4", "lookuproot", new Long(nfsStatsArr[22]));
						// 23 => remove
						nfsStats.setClientStatsValue("nfs v4", "remove", new Long(nfsStatsArr[23]));
						// 24 => rename
						nfsStats.setClientStatsValue("nfs v4", "rename", new Long(nfsStatsArr[24]));
						// 25 => link
						nfsStats.setClientStatsValue("nfs v4", "link", new Long(nfsStatsArr[25]));
						// 26 => symlink
						nfsStats.setClientStatsValue("nfs v4", "symlink", new Long(nfsStatsArr[26]));
						// 27 => create
						nfsStats.setClientStatsValue("nfs v4", "create", new Long(nfsStatsArr[27]));
						// 28 => pathconf
						nfsStats.setClientStatsValue("nfs v4", "pathconf", new Long(nfsStatsArr[28]));
						// 29 => statfs
						nfsStats.setClientStatsValue("nfs v4", "statfs", new Long(nfsStatsArr[29]));
						// 30 => readlink
						nfsStats.setClientStatsValue("nfs v4", "readlink", new Long(nfsStatsArr[30]));
						// 31 => readdir
						nfsStats.setClientStatsValue("nfs v4", "readdir", new Long(nfsStatsArr[31]));
						// 32 => server_caps
						nfsStats.setClientStatsValue("nfs v4", "servercaps", new Long(nfsStatsArr[32]));
						// 33 => delegreturn
						nfsStats.setClientStatsValue("nfs v4", "delegreturn", new Long(nfsStatsArr[33]));
					}
				}
			}
		}

		if (serverStats != null && serverStats.trim().length() > 0) {
			/*
			 * $ cat /proc/net/rpc/nfsd rc 0 959398 336415498 fh 0 0 0 0 0 io
			 * 743250564 1224442800 th 16 9193 1322.293 121.816 246.724 134.568
			 * 138.140 84.223 32.349 37.372 7.716 28.454 ra 32 2044764 555 263
			 * 206 115 119 82 89 98 64 50896 net 337376442 0 337375903 1956 rpc
			 * 337333487 12 12 0 0 proc2 18 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
			 * proc3 22 41 322956721 70371 521208 10500197 134 2096930 779219
			 * 48538 4760 1 0 40535 4351 11538 0 0 19386 57668 47 0 222804 proc4
			 * 2 0 0
			 * 
			 */
			String[] nfsstatsLines = split(serverStats, NEW_LINE_PATTERN, 0);
			for (int i = 0; i < nfsstatsLines.length; i++) {
				if (nfsstatsLines[i].startsWith(AbstractProtocolWrapper.APPPERFECT_PROMPT)) {
					break;
				}
				if (nfsstatsLines[i].trim().length() == 0) {
					continue;
				}
				String[] nfsStatsArr = split(nfsstatsLines[i], WHITE_SPACE_PATTERN, 0);

				if ("rc".equals(nfsStatsArr[0])) {
					/*
					 * * rc (reply cache): <hits> <misses> <nocache>
					 */
					// 1 => hits
					nfsStats.setServerStatsValue("reply cache", "hits", new Long(nfsStatsArr[1]));
					// 2 => misses
					nfsStats.setServerStatsValue("reply cache", "misses", new Long(nfsStatsArr[2]));
					// 3 => nocache
					nfsStats.setServerStatsValue("reply cache", "nocache", new Long(nfsStatsArr[3]));
				} else if ("fh".equals(nfsStatsArr[0])) {
					// * fh (filehandle): <stale> <total-lookups> <anonlookups>
					// <dir-not-in-cache> <nodir-not-in-cache>
					// 'server.fhcache.count.lookups',
					// 'server.fhcache.count.anon',
					// 'server.fhcache.count.nocache_dir',
					// 'server.fhcache.count.nocache_nondir',
					// 'server.fhcache.count.stale'} = @parts;
					// 1 => stale
					nfsStats.setServerStatsValue("file handle", "stale", new Long(nfsStatsArr[1]));
					// 2 => total-lookups
					nfsStats.setServerStatsValue("file handle", "total-lookups", new Long(nfsStatsArr[2]));
					// 3 => anonlookups
					nfsStats.setServerStatsValue("file handle", "anonlookups", new Long(nfsStatsArr[3]));
					// 4 => total-lookups
					nfsStats.setServerStatsValue("file handle", "dir-not-in-cache", new Long(nfsStatsArr[4]));
					// 5 => total-lookups
					nfsStats.setServerStatsValue("file handle", "nodir-not-in-cache", new Long(nfsStatsArr[5]));
				} else if ("io".equals(nfsStatsArr[0])) {
					// * io (input/output): <bytes-read> <bytes-written>
					// - bytes-read: bytes read directly from disk
					// - bytes-written: bytes written to disk
					// 1 => bytes-read
					nfsStats.setServerStatsValue("io", "bytes-read", new Long(nfsStatsArr[1]));
					// 2 => bytes-written
					nfsStats.setServerStatsValue("io", "bytes-written", new Long(nfsStatsArr[2]));
				} else if ("th".equals(nfsStatsArr[0])) {
					// * th (threads): <threads> <fullcnt> <10%-20%> <20%-30%>
					// ... <90%-100%> <100%>
					// 1 => threads
					nfsStats.setServerStatsValue("threads", "threads", new Long(nfsStatsArr[1]));
					// 2 => fullcnt
					nfsStats.setServerStatsValue("threads", "fullcnt", new Long(nfsStatsArr[2]));
					// //3 => 10%-20%
					// nfsStats.setServerStatsValue("threads", "10%-20%", new
					// Double(nfsStatsArr[3]));
					// //4 => 20%-30%
					// nfsStats.setServerStatsValue("threads", "20%-30%", new
					// Double(nfsStatsArr[4]));
					// //5 => 30%-40%
					// nfsStats.setServerStatsValue("threads", "30%-40%", new
					// Double(nfsStatsArr[5]));
					// //6 => 40%-50%
					// nfsStats.setServerStatsValue("threads", "40%-50%", new
					// Double(nfsStatsArr[6]));
					// //7 => 50%-60%
					// nfsStats.setServerStatsValue("threads", "50%-60%", new
					// Double(nfsStatsArr[7]));
					// //8 => 60%-70%
					// nfsStats.setServerStatsValue("threads", "60%-70%", new
					// Double(nfsStatsArr[8]));
					// //9 => 70%-80%
					// nfsStats.setServerStatsValue("threads", "70%-80%", new
					// Double(nfsStatsArr[9]));
					// //10 => 80%-90%
					// nfsStats.setServerStatsValue("threads", "80%-90%", new
					// Double(nfsStatsArr[10]));
					// //11 => 90%-100%
					// nfsStats.setServerStatsValue("threads", "90%-100%", new
					// Double(nfsStatsArr[11]));
					// //12 => 100%
					// nfsStats.setServerStatsValue("threads", "100%", new
					// Double(nfsStatsArr[12]));
				} else if ("ra".equals(nfsStatsArr[0])) {
					// * ra (read-ahead): <cache-size> <10%> <20%> ... <100%>
					// <not-found>
					// - cache-size: always the double of number threads
					// - 10%, 20% ... 100%: how deep it found what was looking
					// for. I *suppose*
					// this means how far the cached block is from the original
					// block that was
					// first requested.
					// - not-found: not found in the read-ahead cache

					// 1 => cache-size
					nfsStats.setServerStatsValue("read ahead", "cache-size", new Long(nfsStatsArr[1]));
					// 2 => 10%
					nfsStats.setServerStatsValue("read ahead", "10%", new Long(nfsStatsArr[2]));
					// 3 => 20%
					nfsStats.setServerStatsValue("read ahead", "20%", new Long(nfsStatsArr[3]));
					// 4 => 30%
					nfsStats.setServerStatsValue("read ahead", "30%", new Long(nfsStatsArr[4]));
					// 5 => 40%
					nfsStats.setServerStatsValue("read ahead", "40%", new Long(nfsStatsArr[5]));
					// 6 => 50%
					nfsStats.setServerStatsValue("read ahead", "50%", new Long(nfsStatsArr[6]));
					// 7 => 60%
					nfsStats.setServerStatsValue("read ahead", "60%", new Long(nfsStatsArr[7]));
					// 8 => 70%
					nfsStats.setServerStatsValue("read ahead", "70%", new Long(nfsStatsArr[8]));
					// 9 => 80%
					nfsStats.setServerStatsValue("read ahead", "80%", new Long(nfsStatsArr[9]));
					// 10 => 90%
					nfsStats.setServerStatsValue("read ahead", "90%", new Long(nfsStatsArr[10]));
					// 11 => 100%
					nfsStats.setServerStatsValue("read ahead", "100%", new Long(nfsStatsArr[11]));
					// 12 => not-found
					nfsStats.setServerStatsValue("read ahead", "not found", new Long(nfsStatsArr[12]));
				} else if ("net".equals(nfsStatsArr[0])) {
					// * net: <netcnt> <netudpcnt> <nettcpcnt> <nettcpconn>
					// 1 => netcnt
					nfsStats.setServerStatsValue("net", "netcnt", new Long(nfsStatsArr[1]));
					// 2 => netudpcnt
					nfsStats.setServerStatsValue("net", "netudpcnt", new Long(nfsStatsArr[2]));
					// 3 => nettcpcnt
					nfsStats.setServerStatsValue("net", "nettcpcnt", new Long(nfsStatsArr[3]));
					// 4 => nettcpconn
					nfsStats.setServerStatsValue("net", "nettcpconn", new Long(nfsStatsArr[4]));
				} else if ("rpc".equals(nfsStatsArr[0])) {
					// {'calls', 'badcalls', 'badauth', 'badclnt', 'xdrcall'}
					// 1 => calls
					nfsStats.setServerStatsValue("rpc", "calls", new Long(nfsStatsArr[1]));
					// 2 => badcalls
					nfsStats.setServerStatsValue("rpc", "badcalls", new Long(nfsStatsArr[2]));
					// 3 => badauth
					nfsStats.setServerStatsValue("rpc", "badauth", new Long(nfsStatsArr[3]));
					// 4 => badclnt
					nfsStats.setServerStatsValue("rpc", "badclnt", new Long(nfsStatsArr[4]));
					// 5 => xdrcall
					nfsStats.setServerStatsValue("rpc", "xdrcall", new Long(nfsStatsArr[5]));
				} else if ("proc2".equals(nfsStatsArr[0])) {
					if (Integer.parseInt(nfsStatsArr[3]) > 0 && nfsStatsArr.length > 19) {
						// 2 => null
						nfsStats.setServerStatsValue("nfs v2", "null", new Long(nfsStatsArr[2]));
						// 3 => getattr
						nfsStats.setServerStatsValue("nfs v2", "getattr", new Long(nfsStatsArr[3]));
						// 4 => setattr
						nfsStats.setServerStatsValue("nfs v2", "setattr", new Long(nfsStatsArr[4]));
						// 5 => root
						nfsStats.setServerStatsValue("nfs v2", "root", new Long(nfsStatsArr[5]));
						// 6 => lookup
						nfsStats.setServerStatsValue("nfs v2", "lookup", new Long(nfsStatsArr[6]));
						// 7 => readlink
						nfsStats.setServerStatsValue("nfs v2", "readlink", new Long(nfsStatsArr[7]));
						// 8 => read
						nfsStats.setServerStatsValue("nfs v2", "read", new Long(nfsStatsArr[8]));
						// 9 => wrcache
						nfsStats.setServerStatsValue("nfs v2", "wrcache", new Long(nfsStatsArr[9]));
						// 10 => write
						nfsStats.setServerStatsValue("nfs v2", "write", new Long(nfsStatsArr[10]));
						// 11 => create
						nfsStats.setServerStatsValue("nfs v2", "create", new Long(nfsStatsArr[11]));
						// 12 => remove
						nfsStats.setServerStatsValue("nfs v2", "remove", new Long(nfsStatsArr[12]));
						// 13 => rename
						nfsStats.setServerStatsValue("nfs v2", "rename", new Long(nfsStatsArr[13]));
						// 14 => link
						nfsStats.setServerStatsValue("nfs v2", "link", new Long(nfsStatsArr[14]));
						// 15 => symlink
						nfsStats.setServerStatsValue("nfs v2", "symlink", new Long(nfsStatsArr[15]));
						// 16 => mkdir
						nfsStats.setServerStatsValue("nfs v2", "mkdir", new Long(nfsStatsArr[16]));
						// 17 => rmdir
						nfsStats.setServerStatsValue("nfs v2", "rmdir", new Long(nfsStatsArr[17]));
						// 18 => readdir
						nfsStats.setServerStatsValue("nfs v2", "readdir", new Long(nfsStatsArr[18]));
						// 19 => fsstat
						nfsStats.setServerStatsValue("nfs v2", "fsstat", new Long(nfsStatsArr[19]));
					}
				} else if ("proc3".equals(nfsStatsArr[0])) {
					if (Integer.parseInt(nfsStatsArr[3]) > 0 && nfsStatsArr.length > 23) {
						// 2 => null
						nfsStats.setServerStatsValue("nfs v3", "null", new Long(nfsStatsArr[2]));
						// 3 => getattr
						nfsStats.setServerStatsValue("nfs v3", "getattr", new Long(nfsStatsArr[3]));
						// 4 => setattr
						nfsStats.setServerStatsValue("nfs v3", "setattr", new Long(nfsStatsArr[4]));
						// 5 => lookup
						nfsStats.setServerStatsValue("nfs v3", "lookup", new Long(nfsStatsArr[5]));
						// 6 => access
						nfsStats.setServerStatsValue("nfs v3", "access", new Long(nfsStatsArr[6]));
						// 7 => readlink
						nfsStats.setServerStatsValue("nfs v3", "readlink", new Long(nfsStatsArr[7]));
						// 8 => read
						nfsStats.setServerStatsValue("nfs v3", "read", new Long(nfsStatsArr[8]));
						// 9 => write
						nfsStats.setServerStatsValue("nfs v3", "write", new Long(nfsStatsArr[9]));
						// 10 => create
						nfsStats.setServerStatsValue("nfs v3", "create", new Long(nfsStatsArr[10]));
						// 11 => mkdir
						nfsStats.setServerStatsValue("nfs v3", "mkdir", new Long(nfsStatsArr[11]));
						// 12 => symlink
						nfsStats.setServerStatsValue("nfs v3", "symlink", new Long(nfsStatsArr[12]));
						// 13 => mknod
						nfsStats.setServerStatsValue("nfs v3", "mknod", new Long(nfsStatsArr[13]));
						// 14 => remove
						nfsStats.setServerStatsValue("nfs v3", "remove", new Long(nfsStatsArr[14]));
						// 15 => rmdir
						nfsStats.setServerStatsValue("nfs v3", "rmdir", new Long(nfsStatsArr[15]));
						// 16 => rename
						nfsStats.setServerStatsValue("nfs v3", "rename", new Long(nfsStatsArr[16]));
						// 17 => link
						nfsStats.setServerStatsValue("nfs v3", "link", new Long(nfsStatsArr[17]));
						// 18 => readdir
						nfsStats.setServerStatsValue("nfs v3", "readdir", new Long(nfsStatsArr[18]));
						// 19 => readdirplus
						nfsStats.setServerStatsValue("nfs v3", "readdirplus", new Long(nfsStatsArr[19]));
						// 20 => fsstat
						nfsStats.setServerStatsValue("nfs v3", "fsstat", new Long(nfsStatsArr[20]));
						// 21 => fsinfo
						nfsStats.setServerStatsValue("nfs v3", "fsinfo", new Long(nfsStatsArr[21]));
						// 22 => pathconf
						nfsStats.setServerStatsValue("nfs v3", "pathconf", new Long(nfsStatsArr[22]));
						// 23 => commit
						nfsStats.setServerStatsValue("nfs v3", "commit", new Long(nfsStatsArr[23]));
					}
				} else if ("proc4".equals(nfsStatsArr[0])) {
					if (Integer.parseInt(nfsStatsArr[3]) > 0 && nfsStatsArr.length > 33) {
						// 2 => null
						nfsStats.setServerStatsValue("nfs v4", "null", new Long(nfsStatsArr[2]));
						// 3 => read
						nfsStats.setServerStatsValue("nfs v4", "read", new Long(nfsStatsArr[3]));
						// 4 => write
						nfsStats.setServerStatsValue("nfs v4", "write", new Long(nfsStatsArr[4]));
						// 5 => commit
						nfsStats.setServerStatsValue("nfs v4", "commit", new Long(nfsStatsArr[5]));
						// 6 => open
						nfsStats.setServerStatsValue("nfs v4", "open", new Long(nfsStatsArr[6]));
						// 7 => open_conf
						nfsStats.setServerStatsValue("nfs v4", "openconf", new Long(nfsStatsArr[7]));
						// 8 => open_noat
						nfsStats.setServerStatsValue("nfs v4", "opennoat", new Long(nfsStatsArr[8]));
						// 9 => open_dgrd
						nfsStats.setServerStatsValue("nfs v4", "opendgrd", new Long(nfsStatsArr[9]));
						// 10 => close
						nfsStats.setServerStatsValue("nfs v4", "close", new Long(nfsStatsArr[10]));
						// 11 => setattr
						nfsStats.setServerStatsValue("nfs v4", "setattr", new Long(nfsStatsArr[11]));
						// 12 => fsinfo
						nfsStats.setServerStatsValue("nfs v4", "fsinfo", new Long(nfsStatsArr[12]));
						// 13 => renew
						nfsStats.setServerStatsValue("nfs v4", "renew", new Long(nfsStatsArr[13]));
						// 14 => setclntid
						nfsStats.setServerStatsValue("nfs v4", "setclntid", new Long(nfsStatsArr[14]));
						// 15 => confirm
						nfsStats.setServerStatsValue("nfs v4", "confirm", new Long(nfsStatsArr[15]));
						// 16 => lock
						nfsStats.setServerStatsValue("nfs v4", "lock", new Long(nfsStatsArr[16]));
						// 17 => lockt
						nfsStats.setServerStatsValue("nfs v4", "lockt", new Long(nfsStatsArr[17]));
						// 18 => locku
						nfsStats.setServerStatsValue("nfs v4", "locku", new Long(nfsStatsArr[18]));
						// 19 => access
						nfsStats.setServerStatsValue("nfs v4", "access", new Long(nfsStatsArr[19]));
						// 20 => getattr
						nfsStats.setServerStatsValue("nfs v4", "getattr", new Long(nfsStatsArr[20]));
						// 21 => lookup
						nfsStats.setServerStatsValue("nfs v4", "lookup", new Long(nfsStatsArr[21]));
						// 22 => lookup_root
						nfsStats.setServerStatsValue("nfs v4", "lookuproot", new Long(nfsStatsArr[22]));
						// 23 => remove
						nfsStats.setServerStatsValue("nfs v4", "remove", new Long(nfsStatsArr[23]));
						// 24 => rename
						nfsStats.setServerStatsValue("nfs v4", "rename", new Long(nfsStatsArr[24]));
						// 25 => link
						nfsStats.setServerStatsValue("nfs v4", "link", new Long(nfsStatsArr[25]));
						// 26 => symlink
						nfsStats.setServerStatsValue("nfs v4", "symlink", new Long(nfsStatsArr[26]));
						// 27 => create
						nfsStats.setServerStatsValue("nfs v4", "create", new Long(nfsStatsArr[27]));
						// 28 => pathconf
						nfsStats.setServerStatsValue("nfs v4", "pathconf", new Long(nfsStatsArr[28]));
						// 29 => statfs
						nfsStats.setServerStatsValue("nfs v4", "statfs", new Long(nfsStatsArr[29]));
						// 30 => readlink
						nfsStats.setServerStatsValue("nfs v4", "readlink", new Long(nfsStatsArr[30]));
						// 31 => readdir
						nfsStats.setServerStatsValue("nfs v4", "readdir", new Long(nfsStatsArr[31]));
						// 32 => server_caps
						nfsStats.setServerStatsValue("nfs v4", "servercaps", new Long(nfsStatsArr[32]));
						// 33 => delegreturn
						nfsStats.setServerStatsValue("nfs v4", "delegreturn", new Long(nfsStatsArr[33]));
					}
				}
			}
		}
	}
}
