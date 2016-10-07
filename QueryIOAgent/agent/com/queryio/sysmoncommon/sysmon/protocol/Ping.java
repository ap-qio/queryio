/*
 * @(#)  Ping.java
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
package com.queryio.sysmoncommon.sysmon.protocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * 
 * @author Exceed Consultancy Services
 */
public class Ping
{
	/**
	 * Method ping
	 * 
	 * @param targetIPAddress
	 * @return average response time of remote machine
	 */
	public static int ping(final String pingPath, final String targetNameOrIPAddress) throws Exception
	{
		String str;
		int iAvgRTT = -1;
		Process ping = null;
		BufferedReader in = null;
		final String osName = System.getProperty("os.name");

		try
		{
			if (osName.indexOf("Windows") != -1)
			{
				// try executing ping. IOException is thrown if ping is not in path
				try
				{
					ping = Runtime.getRuntime().exec("ping " + targetNameOrIPAddress);
				}
				catch (final IOException ioex)
				{
					//TODO - SHOULD WE LOG THIS?
				}
				if (ping == null)
				{
					ping = Runtime.getRuntime().exec(pingPath + File.pathSeparator + "ping " + targetNameOrIPAddress);
				}

				in = new BufferedReader(new InputStreamReader(ping.getInputStream()));
				while ((str = in.readLine()) != null)
				{
					if (str.trim().startsWith("Unknown"))
					{
						throw new RuntimeException("Error pinging to the target machine");
					}
					if (str.trim().startsWith("Packets"))
					{
						final String lostPercent = str.substring(str.indexOf('(') + 1, str.indexOf('%'));
						if (Integer.parseInt(lostPercent) == 100)
						{
							throw new RuntimeException("Error pinging to the target machine");
						}
					}
					if (str.trim().startsWith("Minimum"))
					{
						String sAvgRTT = str.substring(str.lastIndexOf("=") + 1);
						// remove leading spaces
						sAvgRTT = sAvgRTT.trim();
						// remove trailing 'ms'
						sAvgRTT = sAvgRTT.substring(0, sAvgRTT.length() - 2);

						iAvgRTT = (int) Float.parseFloat(sAvgRTT);
					}
				}
			}
			else if (osName.indexOf("Linux") != -1)
			{
				// try executing ping. IOException is thrown if ping is not in path
				try
				{
					ping = Runtime.getRuntime().exec("ping -c 4 " + targetNameOrIPAddress);
				}
				catch (final IOException ioex)
				{
					//TODO - SHOULD WE LOG THIS?
				}

				if (ping == null)
				{
					ping = Runtime.getRuntime().exec(pingPath + "/ping -c 4 " + targetNameOrIPAddress);
				}
				in = new BufferedReader(new InputStreamReader(ping.getInputStream()));
				while ((str = in.readLine()) != null)
				{
					if (str.trim().startsWith("Unknown"))
					{
						throw new RuntimeException("Error pinging to the target machine");
					}
					if (str.trim().endsWith("loss"))
					{
						str = str.substring(0, str.indexOf('%'));
						final String lostPercent = str.substring(str.lastIndexOf(' ') + 1);
						if (Integer.parseInt(lostPercent) == 100)
						{
							throw new RuntimeException("Error pinging to the target machine");
						}
					}

					if (str.trim().startsWith("round-trip ") || str.trim().startsWith("rtt "))
					{
						final String sRTT = str.substring(str.lastIndexOf("=") + 1);
						// remove min value
						String sAvgRTT = sRTT.substring(sRTT.indexOf("/") + 1);
						// remove max and mdev values
						sAvgRTT = sAvgRTT.substring(0, sAvgRTT.indexOf("/"));

						iAvgRTT = (int) Float.parseFloat(sAvgRTT);
					}
					// if(str.trim().startsWith("round-trip "))
					// {
					// String sRTT = str.substring(str.lastIndexOf("=") + 1);
					// //remove min value
					// String sAvgRTT = sRTT.substring(sRTT.indexOf("/") + 1);
					// //remove max and mdev values
					// sAvgRTT = sAvgRTT.substring(0, sAvgRTT.indexOf("/") - 1);
					//	
					// iAvgRTT = Integer.parseInt(sAvgRTT);
					// }
				}
			}
			else
			{
				// try executing ping. IOException is thrown if ping is not in path
				try
				{
					ping = Runtime.getRuntime().exec("ping  -s 32 -c 4 " + targetNameOrIPAddress);
				}
				catch (final IOException ioex)
				{
					//TODO - SHOULD WE LOG THIS?
				}

				if (ping == null)
				{
					ping = Runtime.getRuntime().exec(pingPath + "/ping -s 32 -c 4 " + targetNameOrIPAddress);
				}
				in = new BufferedReader(new InputStreamReader(ping.getInputStream()));
				while ((str = in.readLine()) != null)
				{
					if (str.trim().startsWith("no answer"))
					{
						throw new RuntimeException("Error pinging to the target machine");
					}
					if (str.trim().startsWith("ping: unknown"))
					{
						throw new RuntimeException("Error pinging to the target machine");
					}

					if (str.trim().startsWith("round-trip "))
					{
						final String sRTT = str.substring(str.lastIndexOf("=") + 1);
						// remove min value
						String sAvgRTT = sRTT.substring(sRTT.indexOf("/") + 1);
						// remove max value
						sAvgRTT = sAvgRTT.substring(0, sAvgRTT.indexOf("/"));

						iAvgRTT = (int) Float.parseFloat(sAvgRTT);
					}
					// if(str.trim().startsWith("round-trip "))
					// {
					// String sRTT = str.substring(str.lastIndexOf("=") + 1);
					// //remove min value
					// String sAvgRTT = sRTT.substring(sRTT.indexOf("/") + 1);
					// //remove max value
					// sAvgRTT = sAvgRTT.substring(0, sAvgRTT.indexOf("/") - 1);
					//	
					// iAvgRTT = Integer.parseInt(sAvgRTT);
					// }
				}
			}
		}
		finally
		{
			if (in != null)
			{
			    try
			    {
			        in.close();
			    }
			    catch (Exception e) {}
			}
			if (ping != null)
			{
			    InputStream err = ping.getErrorStream();
			    try
			    {
			        err.close();
			    }
			    catch (Exception e) {}
			    OutputStream out = ping.getOutputStream();
			    try
                {
			        out.close();
                }
                catch (Exception e){}
				ping.destroy();
			}
		}
		return iAvgRTT;
	}

//	public static void main(final String[] args) throws Exception
//	{
		// Output line from linux system
		// String str = "4 packets transmitted, 0 packets received, 0% packet
		// loss";
		// str = str.substring(0, str.indexOf('%'));
		// String lostPercent = str.substring(str.lastIndexOf(' ') + 1);
		// if(Integer.parseInt(lostPercent) == 100)
		// {
		// throw new RuntimeException("Error pinging to the target machine");
		// }

		// System.out.println("In main");
		// Ping.ping(null, "ecs_1");
//	}
}
