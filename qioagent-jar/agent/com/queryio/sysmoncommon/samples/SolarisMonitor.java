/* 
 * @(#) SolarisMonitor.java May 2, 2008 
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

public class SolarisMonitor
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
		final String propertiesFileLocation = getValue(args, 0, ".");
		final String hostName = getValue(args, 1, "localhost");
		final String userName = getValue(args, 2, "root");
		final String pwd = getValue(args, 3, "");
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
			System.out.println("Creating monitor for target Solaris machine.");
			final UnixMonitor monitor = new UnixMonitor(hostName, IOSProtocolConstants.SOLARIS);
			monitor.setUserName(userName);
			monitor.setPassword(pwd);
			monitor.setPort(port);
			monitor.setMonitorProcess(false);
			monitor.setRefreshInterval(1);
			monitor.initializeProtocol(protocol);
			System.out.println("Solaris monitor has been created, will ping the host now");
			final boolean ping = monitor.ping();
			System.out.println("Ping successful: " + ping + ", It will now collect data twice");
			for (int i = 0; i < 10; i++)
			{
				System.out.println("Collecting data, iteration count: " + (i + 1));

				monitor.collectDataFromTop();
				
				System.out.println("CPU: " + monitor.getTotalCPUUsage() + " user: " + monitor.getUserCPUUsage() + " sys: " + monitor.getSystemCPUUsage() + " wait: " + monitor.getWaitCPUUsage());
				Thread.sleep(1000);
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
