/*
 * @(#)  PdhNativeAPI.java
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
package com.queryio.sysmoncommon.pdh;

import java.io.File;

import com.queryio.sysmoncommon.pdh.dstruct.AttributeNode;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.WindowsNetworkInfo;

public class PdhNativeAPI {
	/*
	 * Load the library that implements Native interface
	 */
	static {
		try {
			try {
				final String dllHome = System.getProperty("queryio.dll.home"); //$NON-NLS-1$
				System.out.println("dllHome : " + dllHome);
				boolean loaded = false;
				if (dllHome != null) {
					final File file = new File(dllHome, "lib" + File.separatorChar + "pdhlib.dll"); //$NON-NLS-1$ //$NON-NLS-2$
					if (file.exists()) {
						System.load(file.getCanonicalPath());
						loaded = true;
						System.out.println("yersssss : " + file.getAbsolutePath());
					}
				}

				if (!loaded) {
					System.loadLibrary("pdhlib"); //$NON-NLS-1$
				}
			} catch (final Throwable th) {
				System.loadLibrary("pdhlib"); //$NON-NLS-1$
			}
		} catch (final Throwable th) {
			th.printStackTrace();
			System.err.println("pdhlib.dll not found"); //$NON-NLS-1$
		}
	}

	public native static void setDebug(boolean flag);

	// This method only checks whether machine is accessible or not
	// Not related with network ping concept
	public native static boolean ping(String target);

	public native static AttributeNode[] fetchAttributes(String controlerId, String machineIpOrName,
			boolean isLocalMachine, String[] objectNames);

	public native static boolean connectMachine(String controlerId, String machineIpOrName, boolean isLocalMachine,
			AttributeNode[] node);

	public native static AttributeNode[] collectData(String controlerId);

	public native static WindowsNetworkInfo[] getNetworkInfo(String machineID, String target, boolean isLocalMachine);

	public native static ProcessInfo[] getProcessInfo(String machineID, String terget, boolean isLocalMachine);

	public native static boolean connectForCredential(String domainName, String machineIp, String userName,
			String password);

	public native static boolean disconnectForHost(String domainName, String machineIp);

}
