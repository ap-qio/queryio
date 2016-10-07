/* 
 * @(#) MacOSXMonitor.java May 2, 2008 
 * 
 * Copyright (C) 2002 Exceed Consultancy Services. All Rights Reserved. 
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
package com.queryio.sysmoncommon.samples;

import com.queryio.common.IOSProtocolConstants;

import com.queryio.sysmoncommon.sysmon.DataParserFactory;
import com.queryio.sysmoncommon.sysmon.ResultParsingException;
import com.queryio.sysmoncommon.sysmon.UnixMonitor;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;



public class FirewallMacOSXMonitor
{
	private static String getValue(final String[] args, final int index, final String defaultValue)
	{
		if ((args != null) && (args.length > index))
		{
			return args[index];
		}
		return defaultValue;
	}

	public static void main(final String[] args)
	{
		/*JSch.setLogger(new Logger() {
			
			public void log(int arg0, String arg1) {
				System.out.println("[JSch]: " + arg1);
				
			}
			
			public boolean isEnabled(int arg0) {
				return true;
			}
		});*/
		final String propertiesFileLocation = getValue(args, 0, ".");
		final String hostName = getValue(args, 1, "think2.apple.com");//"17.209.17.60");
		final String userName = getValue(args, 2, "amol");
		final String pwd = getValue(args, 3, "manoj123");
		int port = IOSProtocolConstants.DEFAULT_SSH_PORT;
		try
		{
			port = Integer.parseInt(getValue(args, 4, "22"));
		}
		catch (final NumberFormatException nfe)
		{

		}
		int protocol = IOSProtocolConstants.SSH;
		try
		{
			protocol = Integer.parseInt(getValue(args, 5, "5"));
		}
		catch (final NumberFormatException nfe)
		{

		}
		System.out.println("sysmon.properties file location: " + propertiesFileLocation);
		System.out.println("Initialized all the values, host: " + hostName + " user: " + userName + " protocol: "
				+ protocol + " port: " + port);
		try
		{
			DataParserFactory.initialize(propertiesFileLocation);
			System.out.println("Creating monitor for target MacOSX machine.");
			final UnixMonitor monitor = new UnixMonitor(hostName, IOSProtocolConstants.MACOSX);
			monitor.setUserName(userName);
			monitor.setPassword(pwd);
			monitor.setPort(port);
			monitor.setFirewallUserName("manojdhoble@17.244.141.69");
			monitor.setFirewallPassword("admin");
			monitor.setSuperUserName("amol");
			monitor.setSuperUserPassword("manoj123");
			monitor.setMonitorProcess(false);
			monitor.initializeProtocol(protocol);
			System.out.println("MacOSX monitor has been created, will ping the host now");
			final boolean ping = monitor.ping();
			System.out.println("Ping successful: " + ping + ", It will now collect data twice");
			for (int i = 0; i < 2; i++)
			{
				System.out.println("Collecting data, iteration count: " + (i + 1));
				monitor.collectDataFromTop();
				System.out.println("CPU Usage: " + monitor.getTotalCPUUsage());
				System.out.println("Available Physical Memory: " + monitor.getPhysicalMemoryInfo().getAvailable());
				System.out.println("Available Virtual Memory: " + monitor.getVirtualMemoryInfo().getAvailable());

				final NetworkInfo[] networkInfo = monitor.getNetworkInfo();
				for (int i1 = 0; i1 < networkInfo.length; i1++)
				{
					System.out.println("Network interface: " + networkInfo[i1].getName() + ", Reads/sec: "
							+ networkInfo[i1].getRecdPacketsPerSec() + ", Writes/sec: "
							+ networkInfo[i1].getSentPacketsPerSec());
				}
				System.out.println("collecting disk activity info");
				final DiskInfo[] info = monitor.getPhysicalDiskInfo();
				for (int i1 = 0; i1 < info.length; i1++)
				{
					System.out.println("Physical Disk: " + info[i1].getName() + ", Reads/sec: "
							+ info[i1].getReadsPerSec() + ", Writes/sec: " + info[i1].getWritesPerSec());
				}

				System.out.println("collecting logical disk info");
				final DriveInfo[] drvInfo = monitor.getLogicalDiskInfo();
				for (int i1 = 0; i1 < drvInfo.length; i1++)
				{
					System.out.println("Drive: " + drvInfo[i1].getName() + ", Used space: "
							+ drvInfo[i1].getUsedSpace() + ", Total space: " + drvInfo[i1].getTotalSpace());
				}

				System.out.println("collecting Process Info");
				final ProcessInfo[] processes = monitor.getProcessInfo();
				for (int i1 = 0; i1 < processes.length; i1++)
				{
					System.out.println(processes[i1].getProcessID() + " name: " + processes[i1].getName() + " Memory " + processes[i1].getMemoryUsage());
				}
			}
			monitor.disconnect();
		}
		catch (final ResultParsingException rpe)
		{
			System.out.println("Data fetched:\n" + rpe.getDataFetched());
			System.out.println("\nResult:\n" + rpe.getResult());
		}
		catch (final Throwable th)
		{
			th.printStackTrace(System.out);
		}

	}

}
