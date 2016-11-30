/*
 * @(#)  GetIpAddress.java Oct 3, 2004
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
package com.queryio.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.queryio.common.IOSProtocolConstants;
import com.queryio.common.util.PlatformHandler;
import com.queryio.common.util.StreamPumper;

/**
 * 
 * 
 * @author Exceed Consultancy Services
 * @version 1.0
 */
public class GetIpAddress
{
	private static final String UNIX_COMMAND1 = "/sbin/ifconfig -a"; //$NON-NLS-1$
	private static final String UNIX_COMMAND2 = "ifconfig -a"; //$NON-NLS-1$
	private static final String WINDOWS_COMMAND = "ipconfig /all"; //$NON-NLS-1$

	private String primaryIpAddress = null;
	private String primaryHostId = null;
	private ArrayList ipAddresses = null;
	private List hostIds = null;

	public void fetchAllDetails() throws Exception
	{
		this.primaryIpAddress = this.getIpAddress();
		this.fetchAllIpAddresses();
	}

	public String getPrimaryHostID()
	{
		return this.primaryHostId;
	}

	public String getPrimaryIPAddress()
	{
		if (this.primaryIpAddress == null)
		{
			try
			{
				this.primaryIpAddress = this.getIpAddress();
			}
			catch (final Exception ex)
			{
				// DO NOTHING
			}
		}
		return this.primaryIpAddress;
	}

	public ArrayList getAllIpAddresses()
	{
		return this.ipAddresses;
	}

	public List getAllHostIDs()
	{
		return this.hostIds;
	}

	private String getIpAddress() throws Exception
	{
		String ipAddress = null;
		if (PlatformHandler.isWindows())
		{
			ipAddress = this.getWindowsIp();
		}
		else
		{
			this.fetchAllIpAddresses();
			if (this.ipAddresses.size() > 0)
			{
				ipAddress = (String) this.ipAddresses.get(0);
			}
		}
		return ipAddress;
	}

	private void fetchAllIpAddresses() throws Exception
	{
		final boolean windows = PlatformHandler.isWindows();
		String command = null;
		boolean passEnvs = false;
		if (windows)
		{
			command = WINDOWS_COMMAND;
		}
		else
		{
			final File ifConfig = new File("/sbin/ifconfig"); //$NON-NLS-1$
			if (ifConfig.exists())
			{
				command = UNIX_COMMAND1;
			}
			else
			{
				command = UNIX_COMMAND2;
				passEnvs = true;
			}
		}
		String[] env = null;
		if (passEnvs)
		{
			// we want to add /sbin to the path variable.
			final Map mapEnv = PlatformHandler.getEnvVariables();
			final Iterator iterator = mapEnv.keySet().iterator();
			String key = null;
			final StringBuffer value = new StringBuffer();
			boolean hasPathVariable = false;
			final ArrayList envs = new ArrayList(mapEnv.size() + 1);
			while (iterator.hasNext())
			{
				key = (String) iterator.next();
				final String keyValue = (String) mapEnv.get(key);
				if ((key != null) && (keyValue != null))
				{
					value.append(key);
					value.append('=');
					if (!hasPathVariable && key.equalsIgnoreCase("path")) //$NON-NLS-1$
					{
						hasPathVariable = true;
						value.append("/sbin"); //$NON-NLS-1$
						value.append(File.pathSeparatorChar);
					}
					value.append(keyValue);
					envs.add(value.toString());
					value.setLength(0);
				}
			}
			if (!hasPathVariable)
			{
				envs.add("PATH=/sbin:"); //$NON-NLS-1$
			}
			env = new String[envs.size()];
			envs.toArray(env);
		}

		final Process targetProcess = Runtime.getRuntime().exec(command, env);
		final StringWriter inputWriter = new StringWriter();
		final StreamPumper spInput = new StreamPumper(new BufferedReader(new InputStreamReader(targetProcess
				.getInputStream())), inputWriter);
		spInput.start();
		final StringWriter errorWriter = new StringWriter();
		if (targetProcess.getErrorStream() != null)
		{
			final StreamPumper spError = new StreamPumper(new BufferedReader(new InputStreamReader(targetProcess
					.getErrorStream())), errorWriter);
			spError.start();
		}
		targetProcess.waitFor();
		int count = 0;
		while (!spInput.isProcessCompleted() && (count < 5))
		{
			Thread.sleep(100);
			count++;
		}

		if (errorWriter.getBuffer().length() > 0)
		{
			AppLogger.getLogger().info("Following error occured while executing the process: " + errorWriter.toString()); //$NON-NLS-1$
		}

		final BufferedReader reader = new BufferedReader(new StringReader(inputWriter.toString()));
		if (windows)
		{
			this.getWindowsIp(reader);
			return;
		}
		final boolean linux = PlatformHandler.isLinux();
		this.getUnixIp(reader, linux);
	}

	private String getWindowsIp() throws Exception
	{
		try
		{
			InetAddress.getLocalHost().getAddress();
			return InetAddress.getLocalHost().getHostAddress();
		}
		catch (final UnknownHostException e1)
		{
			// e1.printStackTrace();
		}
		return null;
	}

	/*
	 * Ethernet adapter LAN:
	 * 
	 * Connection-specific DNS Suffix . : IP Address. . . . . . . . . . . . :
	 * 192.168.0.30 Subnet Mask . . . . . . . . . . . : 255.255.255.0 Default
	 * Gateway . . . . . . . . . : 192.168.0.1
	 * 
	 * Ethernet adapter DSL:
	 */
	private void getWindowsIp(final BufferedReader reader) throws Exception
	{
		this.getPrimaryIPAddress();
		this.ipAddresses = new ArrayList(5);
		this.hostIds = new ArrayList(5);
		String line = reader.readLine();
		String address;
		String hostId = null;
		while (line != null)
		{
			line = line.trim();
			if (line.startsWith("Physical Address. ")) //$NON-NLS-1$
			{
				final int spaceIndex = line.lastIndexOf(' '); //$NON-NLS-1$
				hostId = line.substring(spaceIndex + 1);
				this.hostIds.add(hostId);
			}
			else if (line.startsWith("IP Address. ") || line.startsWith("IPv4 Address. ")) //$NON-NLS-1$
			{
				final int spaceIndex = line.lastIndexOf(' '); //$NON-NLS-1$
				address = line.substring(spaceIndex + 1);
				if (line.startsWith("IPv4 Address. ")) //$NON-NLS-1$
				{
					if (address.indexOf("(Preferred)") != -1)
					{
						address = address.substring(0, address.indexOf("(Preferred)"));
					}
				}
				if (!IOSProtocolConstants.LOOPBACKADDRESS.equals(address))
				{
					this.ipAddresses.add(address);
					if (address.equals(this.primaryIpAddress))
					{
						this.primaryHostId = hostId;
					}
				}
			}
			line = reader.readLine();
		}
	}

	// ******** ifconfig -a ********

	// ********* Linux *********
	// eth0 Link encap:Ethernet HWaddr 00:50:BF:51:9E:4C
	// inet addr:192.168.0.17 Bcast:192.168.0.255 Mask:255.255.255.0
	// UP BROADCAST RUNNING MULTICAST MTU:1500 Metric:1
	// RX packets:32326 errors:9 dropped:0 overruns:0 frame:0
	// TX packets:404 errors:0 dropped:0 overruns:0 carrier:0
	// collisions:0 txqueuelen:100
	// RX bytes:3093149 (2.9 Mb) TX bytes:57048 (55.7 Kb)
	// Interrupt:11 Base address:0x6000
	//
	// lo Link encap:Local Loopback
	// inet addr:127.0.0.1 Mask:255.0.0.0
	// UP LOOPBACK RUNNING MTU:16436 Metric:1
	// RX packets:12 errors:0 dropped:0 overruns:0 frame:0
	// TX packets:12 errors:0 dropped:0 overruns:0 carrier:0
	// collisions:0 txqueuelen:0
	// RX bytes:800 (800.0 b) TX bytes:800 (800.0 b)

	// ********* Solaris/MAC OSX *********
	// lo0: flags=1000849<UP,LOOPBACK,RUNNING,MULTICAST,IPv4> mtu 8232 index 1
	// inet 127.0.0.1 netmask ff000000
	// hme0: flags=1000843<UP,BROADCAST,RUNNING,MULTICAST,IPv4> mtu 1500 index 2
	// inet 192.168.0.51 netmask ffffff00 broadcast 192.168.0.255
	// ether 8:0:20:c5:57:ad
	/**
	 * @param reader
	 */
	private void getUnixIp(final BufferedReader reader, final boolean linux) throws Exception
	{
		final String INET_SPACE = "inet "; //$NON-NLS-1$
		final String HW_ADDR = linux ? " HWaddr " : "ether "; //$NON-NLS-1$ //$NON-NLS-2$

		boolean fetched = false;
		this.ipAddresses = new ArrayList(5);
		this.hostIds = new ArrayList(5);
		String hostId = null;
		String line = reader.readLine();
		while (line != null)
		{
			line = line.trim();
			if (line.indexOf(HW_ADDR) != -1)
			{
				final int startIndex = line.indexOf(HW_ADDR) + HW_ADDR.length();
				hostId = line.substring(startIndex);
				this.hostIds.add(hostId);
				if (!fetched)
				{
					this.primaryHostId = hostId;
					fetched = true;
				}
			}
			else if (line.startsWith(INET_SPACE))
			{
				final int startIndex = linux ? line.indexOf(':', INET_SPACE.length()) + 1 : INET_SPACE.length();
				final String address = line.substring(startIndex, line.indexOf(' ', INET_SPACE.length()));
				if (!IOSProtocolConstants.LOOPBACKADDRESS.equals(address))
				{
					this.ipAddresses.add(address);
				}
			}
			line = reader.readLine();
		}
		
		if(ipAddresses.size() == 0){
			this.ipAddresses.add(IOSProtocolConstants.LOOPBACKADDRESS);
		}
	}

	public String getHostName() throws Exception
	{
		final InetAddress localAddr = InetAddress.getLocalHost();
		return localAddr.getHostName();
	}
	
	public static String getCanonicalHostName(String host)
	{
		String sHostName = null;
		InetAddress inetaddress = null;
		try
		{
			inetaddress = (host == null || host.trim().length() == 0)?
				InetAddress.getLocalHost():InetAddress.getByName(host);
			final Class __Class = inetaddress.getClass();
			// Called using reflection as not available in JDK 13
			final Method __method = __Class.getMethod("getCanonicalHostName", (Class[])null); //$NON-NLS-1$
			__method.setAccessible(true);
			try
			{
				sHostName = (String) __method.invoke(inetaddress, (Object [])null);
				if (sHostName.equals(inetaddress.getHostAddress()))
				{
					sHostName = inetaddress.getHostName();
				}
			}
			catch (final IllegalAccessException iae)
			{
				sHostName = inetaddress.getHostName();
			}
			catch (final InvocationTargetException ite)
			{
				sHostName = inetaddress.getHostName();
			}
		}
		catch (final NoSuchMethodException nsme)
		{
			sHostName = inetaddress != null ? inetaddress.getHostName():host;
		}
		catch (final UnknownHostException uhe)
		{
			sHostName = inetaddress != null ? inetaddress.getHostName():host;
		}
		return sHostName != null ? sHostName:host;
	}

	public String getCanonicalHostName() throws Exception
	{
		return getCanonicalHostName(null);
	}

	public static boolean isLocalMachine(final String hostName)
	{
		if ((hostName == null) || "".equals(hostName) || IOSProtocolConstants.LOOPBACKADDRESS.equals(hostName)
				|| IOSProtocolConstants.LOCALHOST.equals(hostName)) //$NON-NLS-1$	//$NON-NLS-2$
		{
			// new InetAddress(null) returns local machine's address.
			return true;
		}

		final GetIpAddress gp = new GetIpAddress();
		try
		{
			return (gp.getCanonicalHostName().equals(hostName) || hostName.equals(gp.getPrimaryIPAddress()));
		}
		catch (final Exception ex)
		{

		}
		return false;
	}

	
	public String getCommaSeparatedListOfIPAddresses()
	{
		final StringBuffer buffer = new StringBuffer();
		final int n = ipAddresses.size();
		for(int i = 0; i < n; i++)
		{
			buffer.append((String)ipAddresses.get(i));
			if (i < n - 1)
			{
				buffer.append(',');
			}
		}
		return buffer.toString();
	}
	
	public static void main(final String[] args) throws Exception
	{
		// Has been verified for Windows, Linux & MAC OSX.
		final GetIpAddress gp = new GetIpAddress();
		gp.fetchAllDetails();
		System.out.println("Host name: " + gp.getCanonicalHostName() + " Primary IP Address: " + gp.getPrimaryIPAddress() + " Primary Host ID: " + gp.getPrimaryHostID()); //$NON-NLS-1$	//$NON-NLS-2$
		System.out.println("All IP addresses: " + gp.getAllIpAddresses()); //$NON-NLS-1$
		String ipAddressList = gp.getCommaSeparatedListOfIPAddresses();
		System.out.println(ipAddressList);
		String[] ipAddresses = ipAddressList.split(",");
		for(int i = 0; i < ipAddresses.length; i++)
		{
			System.out.println(i + " : " + ipAddresses[i]);
		}
	}
}
