/* 
 * @(#) LinuxMonitor.java May 2, 2008 
 *             
 *    Copyright  (C) 2002 Exceed Consultancy Services. All Rights Reserved. 
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

public class LogMonitor {
	private static String getValue(final String[] args, final int index, final String defaultValue) {
		if ((args != null) && (args.length > index)) {
			return args[index];
		}
		return defaultValue;
	}

	public static void main(final String[] args) {
		final String propertiesFileLocation = "";
		final String hostName = "";
		final String userName = getValue(args, 2, "");
		final String pwd = getValue(args, 3, "");
		int port = 22;
		int protocol = IOSProtocolConstants.SSH;
		int osType = IOSProtocolConstants.LINUX;
		String logFile = "/ngs/app/gsxwebld/server_instances/instance_1/server/default/log/server.log";

		System.out.println("sysmon.properties file location: " + propertiesFileLocation);
		System.out.println("Initialized all the values, host: " + hostName + " user: " + userName + " protocol: "
				+ protocol + " port: " + port);
		try {
			DataParserFactory.initialize(propertiesFileLocation);
			System.out.println("Creating log monitor for target machine.");
			final UnixMonitor monitor = new UnixMonitor(hostName, osType);
			monitor.setUserName(userName);
			monitor.setPassword(pwd);
			monitor.setPort(port);
			monitor.setSuperUserName("");
			monitor.setSuperUserPassword("");
			monitor.setMonitorProcess(false);
			monitor.setProcessFilter(new ProcessFilter());
			monitor.initializeProtocol(protocol);
			System.out.println("Log monitor has been created, will ping the host now");
			final boolean ping = monitor.ping();
			System.out.println("Ping successful: " + ping);
			LogInfo logInfo = null;
			long lastLine = monitor.getLastLogFileLineNumber(logFile);
			System.out.println("starting from: " + lastLine);
			lastLine = 1;
			String[] search = { "Exception", "exception" };

			for (int i = 0; i < 2; i++) {
				logInfo = monitor.searchInLogFile(logFile, search, lastLine);
				if (logInfo != null) {
					System.out.println(logInfo.toString());
					System.out.println("Now " + logInfo.getLastLineRead());
					for (int j = 0; j < Math.min(logInfo.getMatchedItems().size(), 100); j++) {
						System.out.println(logInfo.getMatchedItems().get(j));
					}
					lastLine = logInfo != null ? logInfo.getLastLineRead() : 1;
				}
			}
			monitor.disconnect();
		} catch (final ResultParsingException rpe) {
			System.out.println("Data fetched:\n" + rpe.getDataFetched());
			System.out.println("\nResult:\n" + rpe.getResult());
		} catch (final Throwable th) {
			th.printStackTrace(System.out);
		}

	}

}


