package com.queryio.sysmoncommon.engine;

import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.queryio.common.IOSProtocolConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.remote.SystemStatistics;
import com.queryio.common.util.AppLogger;
import com.queryio.ipmi.IPMIMonitor;
import com.queryio.sysmoncommon.sysmon.DataParserFactory;
import com.queryio.sysmoncommon.sysmon.UnixMonitor;
import com.queryio.sysmoncommon.sysmon.WindowsMonitorController;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;

public class SystemMonitor {
	public static final String OS_NAME = System.getProperty("os.name"); //$NON-NLS-1$

	private static UnixMonitor monitor = null;
	public static WindowsMonitorController windowMonitor = null;

	public static boolean isLinux(final String osName) {
		return (osName != null) && osName.toLowerCase().startsWith("linux"); //$NON-NLS-1$
	}

	public static boolean isMacOS(final String osName) {
		return (osName != null) && osName.toLowerCase().startsWith("mac"); //$NON-NLS-1$
	}

	public static boolean isWindowsOS(final String osName) {
		return (osName != null) && osName.toLowerCase().startsWith("windows"); //$NON-NLS-1$
	}

	public static String startMonitorWrapper() {

		int monitorType;

		try {
			if (isLinux(OS_NAME)) {
				monitorType = IOSProtocolConstants.LINUX;
			} else if (isMacOS(OS_NAME)) {
				monitorType = IOSProtocolConstants.MACOSX;
			} else {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("OS not supported for Network and Disk I/O");
				return QueryIOConstants.RETURN_FAILURE + "_OS not supported for Network and Disk I/O";
			}

			monitor = new UnixMonitor("127.0.0.1", monitorType);
			monitor.setMonitorProcess(true);
			monitor.setMonitorNFS(false);
			monitor.initializeProtocol(monitorType);
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("Unix Monitor for Network and Disk I/O failed with Exception: " + e.getMessage(), e);
			return QueryIOConstants.RETURN_FAILURE + "_Unix Monitor for Network and Disk I/O failed.";
		}
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Unix Monitor for Network and Disk I/O started successfully.");
		return QueryIOConstants.RETURN_SUCCESS + "_Unix Monitor for Network and Disk I/O started successfully.";
	}

	public static void startMonitoring() throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Monitoring started");

		startMonitorWrapper();

		if (monitor != null) {
			monitor.ping();
		}
	}

	public static String startWindowsMonitorWrapper(String username, String password) {
		int monitorType = -1;
		try {
			if (isWindowsOS(OS_NAME)) {
				monitorType = IOSProtocolConstants.WINDOWS;
			} else {
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("OS not supported for Network and
				// Disk I/O");
				return QueryIOConstants.RETURN_FAILURE + "_OS not supported for Network and Disk I/O";
			}

			windowMonitor = new WindowsMonitorController();

		} catch (Exception e) {
			// AppLogger.getLogger().fatal("Unix Monitor for Network and Disk
			// I/O failed with Exception: " + e.getMessage(), e);
			return QueryIOConstants.RETURN_FAILURE + "_Windows Monitor for Network and Disk I/O failed.";
		}
		// if(AppLogger.getLogger().isDebugEnabled()){
		// AppLogger.getLogger().debug("Unix Monitor for Network and Disk I/O
		// started successfully.");
		// }
		return QueryIOConstants.RETURN_SUCCESS + "_Windows Monitor for Network and Disk I/O started successfully.";
	}

	public static void startWindowsMonitoring(String installDir, String username, String password) throws Exception {
		if (!installDir.endsWith(java.io.File.separator))
			installDir = installDir.concat(java.io.File.separator);
		System.setProperty("queryio.dll.home", installDir + "QueryIOServers");
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Monitoring started");

		startWindowsMonitorWrapper(username, password);
		// windowMonitor.ping("localhost");
	}

	public static void stopMonitoring() {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Monitoring stopped");

		if (monitor != null) {
			try {
				monitor.disconnect();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			monitor = null;
		}
		if (windowMonitor != null) {
			try {
				windowMonitor.disconnect();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			windowMonitor = null;
		}
	}

	public static String stopMonitorWrapper() {
		try {
			if (monitor != null)
				monitor.disconnect();
			if (windowMonitor != null)
				windowMonitor.disconnect();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Monitor could not be disconnected. " + e.getMessage(), e);
			return QueryIOConstants.RETURN_FAILURE + " _Monitor could not be disconnected.";
		}

		return QueryIOConstants.RETURN_SUCCESS + " _Monitor disconnected successfully.";
	}

	public static SystemStatistics getSystemStatistics() {
		long reqId = System.currentTimeMillis();

		Thread.currentThread().setName("REQ_" + reqId);

		// if(AppLogger.getLogger().isDebugEnabled())
		// AppLogger.getLogger().debug("Monitoring request " + reqId + "
		// reveived");
		NetworkInfo[] networkInfo = null;
		DiskInfo[] diskInfo = null;
		ArrayList list = null;
		float recdPacketsPerSec = 0;
		float sentPacketsPerSec = 0;
		float cpuUsage = 0;
		float ramFree = 0;
		float ramTotal = 0;
		try {
			if (isLinux(OS_NAME)) {
				list = getLinuxMachineStatistics();
				if (list != null) {
					networkInfo = (NetworkInfo[]) list.get(0);
					diskInfo = (DiskInfo[]) list.get(1);
					cpuUsage = (Float) list.get(2);
					ramFree = ((MemoryInfo) list.get(3)).getAvailable();
					ramTotal = ((MemoryInfo) list.get(3)).getTotal();
				}
			} else if (isMacOS(OS_NAME)) {
				list = getMacOSMachineStatistics();
				if (list != null) {
					networkInfo = (NetworkInfo[]) list.get(0);
					diskInfo = (DiskInfo[]) list.get(1);
					cpuUsage = (Float) list.get(2);
					ramFree = ((MemoryInfo) list.get(3)).getAvailable();
					ramTotal = ((MemoryInfo) list.get(3)).getTotal();
				}
			} else if (isWindowsOS(OS_NAME)) {
				list = getWindowsOSMachineStatistics();
				if (list != null) {
					networkInfo = (NetworkInfo[]) list.get(0);
					diskInfo = (DiskInfo[]) list.get(1);
					cpuUsage = (Float) list.get(2);
					ramFree = ((MemoryInfo) list.get(3)).getAvailable();
					ramTotal = ((MemoryInfo) list.get(3)).getTotal();
				}
			} else if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("OS not supported");

			if (networkInfo != null) {
				for (int i1 = 0; i1 < networkInfo.length; i1++) {
					recdPacketsPerSec += networkInfo[i1].getRecdPacketsPerSec();
					sentPacketsPerSec += networkInfo[i1].getSentPacketsPerSec();
				}
			} else {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Network Info could not be obtained");
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getSystemStatistics() failed with exception: " + e.getMessage(), e);
		}

		// if(AppLogger.getLogger().isDebugEnabled())
		// AppLogger.getLogger().debug("isMacOS(): " + isMacOS(OS_NAME));
		// if(AppLogger.getLogger().isDebugEnabled())
		// AppLogger.getLogger().debug("recdPacketsPerSec: " +
		// recdPacketsPerSec);
		// if(AppLogger.getLogger().isDebugEnabled())
		// AppLogger.getLogger().debug("sentPacketsPerSec: " +
		// sentPacketsPerSec);

		SystemStatistics response = new SystemStatistics();

		response.setRecdPacketsPerSec(recdPacketsPerSec);
		response.setSentPacketsPerSec(sentPacketsPerSec);
		response.setDiskInfo(diskInfo);
		response.setCpuUsage(cpuUsage);
		response.setRamFree(ramFree);
		response.setRamTotal(ramTotal);
		if (isWindowsOS(OS_NAME))
			response.setDiskHealthStatistics(DiskMonitor.getDiskHealthStatistics());
		response.setIpmiTemperatureMap(IPMIMonitor.getTemperatureStatus());
		response.setIpmiFanMap(IPMIMonitor.getFanStatus());
		response.setIpmiVoltageMap(IPMIMonitor.getVoltageStatus());

		// if(AppLogger.getLogger().isDebugEnabled())
		// AppLogger.getLogger().debug("Monitoring request " + reqId + "
		// returned at " + System.currentTimeMillis());

		return response;
	}

	public static ArrayList getMacOSMachineStatistics() throws Exception {
		NetworkInfo[] networkInfo = null;
		DiskInfo[] diskInfo = null;
		float cpuUssage = 0;
		MemoryInfo memoryInfo = null;

		boolean interrupted = false;

		try {
			Thread monitorThread = new Thread() {
				public void run() {
					try {
						monitor.collectDataFromTop();
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
				}
			};

			monitorThread.start();

			long start = System.currentTimeMillis();
			long diff = 0;
			while (diff <= 5) {
				Thread.sleep(1000);
				diff = (System.currentTimeMillis() - start) / 1000;

				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Time remaining for monitor
				// thread " + Thread.currentThread().getName() + " is " +
				// (5-diff+1) + " seconds.");

				if (!monitorThread.isAlive()) {
					networkInfo = monitor.getNetworkInfo();
					diskInfo = monitor.getPhysicalDiskInfo();
					cpuUssage = monitor.getSystemCPUUsage();
					memoryInfo = monitor.getPhysicalMemoryInfo();

					break;
				}
			}

			if (monitorThread.isAlive()) {
				AppLogger.getLogger().fatal("Monitor thread for request " + Thread.currentThread().getName()
						+ " timed out...interrupting monitor thread at " + System.currentTimeMillis());

				interrupted = true;

				monitorThread.interrupt();
			} else {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Monitor thread " + Thread.currentThread().getName() + " ok");
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getMacOSMachineStatistics() failed with Exception: ", e);
		} finally {
			if (interrupted) {
				AppLogger.getLogger().fatal("Monitor thread " + Thread.currentThread().getName()
						+ " timed out...Disconnecting system monitor");

				try {
					monitor.disconnect();
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}

				AppLogger.getLogger().fatal("Initializing system monitor");

				try {
					monitor.ping();
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
		}

		ArrayList list = new ArrayList();
		list.add(networkInfo);
		list.add(diskInfo);
		list.add(cpuUssage);
		list.add(memoryInfo);
		return list;
	}

	public static ArrayList getWindowsOSMachineStatistics() throws Exception {
		NetworkInfo[] networkInfo = null;
		DiskInfo[] diskInfo = null;
		float cpuUssage = 0;
		MemoryInfo memoryInfo = null;
		boolean interrupted = false;

		try {
			Thread monitorThread = new Thread() {
				public void run() {
					try {
						windowMonitor.collectData();
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
				}
			};

			monitorThread.start();

			long start = System.currentTimeMillis();
			long diff = 0;
			while (diff <= 15) {
				Thread.sleep(1000);
				diff = (System.currentTimeMillis() - start) / 1000;

				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Time remaining for monitor
				// thread " + Thread.currentThread().getName() + " is " +
				// (15-diff+1) + " seconds.");

				if (!monitorThread.isAlive()) {
					networkInfo = windowMonitor.getNetworkInfo();
					diskInfo = windowMonitor.getPhysicalDiskInfo();
					cpuUssage = windowMonitor.getSystemCPUUsage();
					memoryInfo = windowMonitor.getPhysicalMemoryInfo();

					// if(AppLogger.getLogger().isDebugEnabled())
					// AppLogger.getLogger().debug("Detected CPU Usage: " +
					// cpuUssage);

					break;
				}
			}

			if (monitorThread.isAlive()) {
				AppLogger.getLogger().fatal("Monitor thread for request " + Thread.currentThread().getName()
						+ " timed out...interrupting monitor thread at " + System.currentTimeMillis());

				interrupted = true;

				monitorThread.interrupt();
			} else {
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Monitor thread " +
				// Thread.currentThread().getName() + " ok");
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getLinuxMachineStatistics() failed with Exception: ", e);
		} finally {
			if (interrupted) {
				AppLogger.getLogger().fatal("Monitor thread " + Thread.currentThread().getName()
						+ " timed out...Disconnecting system monitor");

				try {
					windowMonitor.disconnect();
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}

				AppLogger.getLogger().fatal("Initializing system monitor");

				// try {
				// windowMonitor.ping();
				// } catch(Exception e) {
				// AppLogger.getLogger().fatal(e.getMessage(), e);
				// }
			}
		}

		ArrayList list = new ArrayList();
		list.add(networkInfo);
		list.add(diskInfo);
		list.add(cpuUssage);
		list.add(memoryInfo);

		return list;

	}

	public static ArrayList getLinuxMachineStatistics() throws Exception {
		boolean interrupted = false;
		NetworkInfo[] networkInfo = null;
		DiskInfo[] diskInfo = null;
		float cpuUssage = 0;
		MemoryInfo memoryInfo = null;

		try {
			Thread monitorThread = new Thread() {
				public void run() {
					try {
						monitor.collectDataFromTop();
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
				}
			};

			monitorThread.start();

			long start = System.currentTimeMillis();
			long diff = 0;
			while (diff <= 15) {
				Thread.sleep(1000);
				diff = (System.currentTimeMillis() - start) / 1000;

				if (!monitorThread.isAlive()) {
					networkInfo = monitor.getNetworkInfo();
					diskInfo = monitor.getPhysicalDiskInfo();
					cpuUssage = monitor.getSystemCPUUsage();
					memoryInfo = monitor.getPhysicalMemoryInfo();

					// if(AppLogger.getLogger().isDebugEnabled())
					// AppLogger.getLogger().debug("Detected CPU Usage: " +
					// cpuUssage);

					break;
				}
			}

			if (monitorThread.isAlive()) {
				AppLogger.getLogger().fatal("Monitor thread for request " + Thread.currentThread().getName()
						+ " timed out...interrupting monitor thread at " + System.currentTimeMillis());

				interrupted = true;

				monitorThread.interrupt();
			} else {
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Monitor thread " +
				// Thread.currentThread().getName() + " ok");
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getLinuxMachineStatistics() failed with Exception: ", e);
		} finally {
			if (interrupted) {
				AppLogger.getLogger().fatal("Monitor thread " + Thread.currentThread().getName()
						+ " timed out...Disconnecting system monitor");

				try {
					monitor.disconnect();
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}

				AppLogger.getLogger().fatal("Initializing system monitor");

				try {
					monitor.ping();
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
		}

		ArrayList list = new ArrayList();
		list.add(networkInfo);
		list.add(diskInfo);
		list.add(cpuUssage);
		list.add(memoryInfo);

		return list;
	}

	public static void main(String[] args) throws Exception {
		/*
		 * if(args.length != 1) { System.err.
		 * println("USAGE : java SystemMonitor /path/to/QueryIO_services");
		 * return; }
		 */
		DataParserFactory.initialize("/Users/aos/QueryIO/QueryIOPackage/QueryIOAgent/webapps/agentqueryio");
		AppLogger.setLogger(Logger.getLogger("Test"));
		BasicConfigurator.configure();
		startMonitoring();
		SystemStatistics s = getSystemStatistics();
		System.out.println("ram free   : " + s.getRamFree());
		DiskInfo[] info = s.getDiskInfo();
		System.out.println("Disk--------");
		for (int i = 0; i < info.length; i++) {
			System.out.println("diskName : " + info[i].getName());
			System.out.println("reads : " + info[i].getReadsPerSec());
			System.out.println("write : " + info[i].getWritesPerSec());
		}
		System.out.println("network recd : " + s.getRecdPacketsPerSec());
		System.out.println("network sent : " + s.getSentPacketsPerSec());
		System.out.println("OS_NAME : " + OS_NAME);
	}
}