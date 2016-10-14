/* 
 * @(#) LinuxMonitor.java May 2, 2008 
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

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.IOSProtocolConstants;

import com.queryio.sysmoncommon.sysmon.DataParserFactory;
import com.queryio.sysmoncommon.sysmon.UnixMonitor;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;

public class LinuxMonitor
{
//	private static String getValue(final String[] args, final int index, final String defaultValue)
//	{
//		if ((args != null) && (args.length > index))
//		{
//			return args[index];
//		}
//		return defaultValue;
//	}

	public static NetworkInfo[] getNetworkInfo()
	{
		UnixMonitor monitor = null;
		try
		{
			DataParserFactory.initialize(EnvironmentalConstants.getAppHome() + "/Users/eshan/Desktop/");
			monitor = new UnixMonitor("localhost", IOSProtocolConstants.LINUX);
			monitor.setMonitorProcess(true);
			monitor.setMonitorNFS(true);
			monitor.setIOStatDisks("genevad");
			monitor.initializeProtocol(IOSProtocolConstants.LINUX);			
			System.out.println("Linux monitor has been created, will ping the host now");
			final boolean ping = monitor.ping();
			System.out.println("Ping successful: " + ping + ", It will now collect data twice");
			
			return monitor.getNetworkInfo();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				monitor.disconnect();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void main(String[] args) 
	{
		final NetworkInfo[] networkInfo = getNetworkInfo();
		
		System.out.println("length: " + networkInfo.length);
		for (int i1 = 0; i1 < networkInfo.length; i1++)
		{
			try 
			{
				System.out.println(networkInfo[i1].getRecdPacketsPerSec() + "\t" + networkInfo[i1].getSentPacketsPerSec() + "\t" + networkInfo[i1].getName());
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
}
