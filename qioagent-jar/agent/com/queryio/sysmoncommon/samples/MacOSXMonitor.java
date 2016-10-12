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

import java.util.Iterator;
import java.util.Map;

import com.queryio.common.IOSProtocolConstants;
import org.apache.log4j.BasicConfigurator;

import com.queryio.sysmoncommon.sysmon.DataParserFactory;
import com.queryio.sysmoncommon.sysmon.ResultParsingException;
import com.queryio.sysmoncommon.sysmon.UnixMonitor;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NFSStat;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;

public class MacOSXMonitor
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
		//final String propertiesFileLocation = getValue(args, 0, ".");
		final String hostName = getValue(args, 1, "localhost");
	
		try
		{
			BasicConfigurator.configure();
			DataParserFactory.initialize("/Users/aos/QueryIO/services/QueryIOAgent/webapps/agentqueryio/");
			System.out.println("Creating monitor for target MacOSX machine.");
			final UnixMonitor monitor = new UnixMonitor(hostName, IOSProtocolConstants.MACOSX);
			
			monitor.setMonitorProcess(true);
			monitor.setMonitorNFS(true);
			monitor.initializeProtocol(IOSProtocolConstants.MACOSX);
			System.out.println("MacOSX monitor has been created, will ping the host now");
			final boolean ping = monitor.ping();
			System.out.println("Ping successful: " + ping + ", It will now collect data twice");
			for (int i = 0; i < 20; i++)
			{
				System.out.println("Collecting data, iteration count: " + (i + 1));
				monitor.collectDataFromTop();
				System.out.println("************ CPU Usage ************");
				System.out.println("Total\tUser\tSystem\tWait");
				System.out.println(monitor.getTotalCPUUsage() + "\t" + monitor.getUserCPUUsage() + "\t" + monitor.getSystemCPUUsage() + "\t" + monitor.getWaitCPUUsage());
				
				System.out.println("************ Load Avg ************");
				System.out.println("1 Min\t5 Min\t15 Min");
				System.out.println(monitor.get1MinLoadAverage() + "\t" + monitor.get5MinLoadAverage() + "\t" + monitor.get15MinLoadAverage());
				
				System.out.println("************ Physical Memory ************");
				System.out.println("Available\tTotal");
				System.out.println(monitor.getPhysicalMemoryInfo().getAvailable() + "\t" + monitor.getPhysicalMemoryInfo().getTotal());

				System.out.println("************ Virtual Memory ************");
				System.out.println("Available\tTotal");
				System.out.println(monitor.getVirtualMemoryInfo().getAvailable() + "\t" + monitor.getVirtualMemoryInfo().getTotal());
				
				System.out.println("************ Network Statistics ************");
				System.out.println("Reads/sec\tWrites/sec\tName");
				final NetworkInfo[] networkInfo = monitor.getNetworkInfo();
				for (int i1 = 0; i1 < networkInfo.length; i1++)
				{
					System.out.println(networkInfo[i1].getRecdPacketsPerSec() + "\t" + networkInfo[i1].getSentPacketsPerSec() + "\t" + networkInfo[i1].getName());
				}
				System.out.println("collecting disk activity info");
				final DiskInfo[] info = monitor.getPhysicalDiskInfo();
				for (int i1 = 0; i1 < info.length; i1++)
				{
					System.out.println("Physical Disk: " + info[i1].getName() + ", Reads/sec: " + info[i1].getReadsPerSec() + ", Writes/sec: " + info[i1].getWritesPerSec());
				}

				System.out.println("collecting logical disk info");
				final DriveInfo[] drvInfo = monitor.getLogicalDiskInfo();
				for (int i1 = 0; i1 < drvInfo.length; i1++)
				{
					System.out.println("Drive: " + drvInfo[i1].getName() + ", Used space: " + drvInfo[i1].getUsedSpace() + ", Total space: " + drvInfo[i1].getTotalSpace());
				}
				System.out.println("collecting Process Info");
				final ProcessInfo[] processes = monitor.getProcessInfo();
				for (int i1 = 0; processes != null && i1 < processes.length; i1++)
				{
					System.out.println(processes[i1].getName() + " Memory " + processes[i1].getMemoryUsage());
				}
				System.out.println("collecting NFS client info");
	            NFSStat stats = monitor.getNFSStats();
	            if (stats != null)
	            {
	               Map clientStats = stats.getClientStats();
	                Iterator iterator = clientStats.keySet().iterator();
	                while (iterator.hasNext())
	                {
	                    String key = (String) iterator.next();
	                    System.out.println(key+":");
	                    Map attrbiutes = (Map) clientStats.get(key);
	                    Iterator itr = attrbiutes.keySet().iterator();
	                    while(itr.hasNext())
	                    {
	                        Object name = itr.next();
	                        System.out.println(name + ": " + attrbiutes.get(name));
	                    }
	                }
	            }
				Thread.sleep(2000);
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
