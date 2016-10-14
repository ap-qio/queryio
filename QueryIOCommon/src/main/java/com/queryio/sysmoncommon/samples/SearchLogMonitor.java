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

import com.queryio.common.IOSProtocolConstants;

import com.queryio.sysmoncommon.sysmon.DataParserFactory;
import com.queryio.sysmoncommon.sysmon.ProcessFilter;
import com.queryio.sysmoncommon.sysmon.ResultParsingException;
import com.queryio.sysmoncommon.sysmon.UnixMonitor;
import com.queryio.sysmoncommon.sysmon.dstruct.LogInfo;

public class SearchLogMonitor
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
		UnixMonitor monitor = null;
		try
		{
			DataParserFactory.initialize(propertiesFileLocation);
			System.out.println("Creating monitor for target Linux machine.");
			monitor = new UnixMonitor(hostName, IOSProtocolConstants.MACOSX);
			monitor.setUserName(userName);
			monitor.setPassword(pwd);
			monitor.setPort(port);
			monitor.setProcessFilter(new ProcessFilter());
			monitor.setMonitorProcess(false);
			monitor.initializeProtocol(protocol);			
			System.out.println("Monitor has been created, will ping the host now");
			final boolean ping = monitor.ping();
			System.out.println("Ping successful: " + ping + ", It will now monitor the log file");
			long lastLineRead = 1;
			LogInfo logInfo = null;
			while(true)
			{
				logInfo = monitor.searchInLogFile("/workfolder/junk.txt", new String [] {"Special"}, lastLineRead);
				System.out.println("LogInfo: " + logInfo);
				if (lastLineRead == logInfo.getLastLineRead()) break;
				lastLineRead = logInfo.getLastLineRead();
				Thread.sleep(7000);
			}
			
			
		}
		catch (final ResultParsingException rpe)
		{
			rpe.printStackTrace();
			System.out.println("Data fetched:\n" + rpe.getDataFetched());
			System.out.println("\nResult:\n" + rpe.getResult());
		}
		catch (final Throwable th)
		{
			th.printStackTrace(System.out);
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
		
	}

}
