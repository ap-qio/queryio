/*
 * @(#)  RstatClient.java
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

import java.io.IOException;
import java.net.InetAddress;

import org.acplt.oncrpc.OncRpcClient;
import org.acplt.oncrpc.OncRpcClientStub;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrVoid;

import com.queryio.sysmoncommon.sysmon.IUnixNativeAPI;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.LogInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NFSStat;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.UnixNetworkInfo;

/**
 * @author Exceed Consultancy Services
 * 
 */

/*
 * The class <code>rstatClient</code> implements the client stub proxy for the
 * RSTATPROG remote program. It provides method stubs which, when called, in
 * turn call the appropriate remote method (procedure).
 */
public class RstatClient extends OncRpcClientStub implements IUnixNativeAPI {

	/**
	 * Constructs a <code>rstatClient</code> client stub proxy object from which
	 * the RSTATPROG remote program can be accessed.
	 * 
	 * @param host
	 *            Internet address of host where to contact the remote program.
	 * @param protocol
	 *            {@link org.acplt.oncrpc.OncRpcProtocols Protocol} to be used
	 *            for ONC/RPC calls.
	 * @throws OncRpcException
	 *             if an ONC/RPC error occurs.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public RstatClient(final InetAddress host, final int protocol) throws OncRpcException, IOException {
		super(host, RstatConstants.RSTATPROG, 3, 0, protocol);
	}

	/**
	 * Constructs a <code>RstatClient</code> client stub proxy object from which
	 * the RSTATPROG remote program can be accessed.
	 * 
	 * @param host
	 *            Internet address of host where to contact the remote program.
	 * @param port
	 *            Port number at host where the remote program can be reached.
	 * @param protocol
	 *            {@link org.acplt.oncrpc.OncRpcProtocols Protocol} to be used
	 *            for ONC/RPC calls.
	 * @throws OncRpcException
	 *             if an ONC/RPC error occurs.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public RstatClient(final InetAddress host, final int port, final int protocol) throws OncRpcException, IOException {
		super(host, RstatConstants.RSTATPROG, 3, port, protocol);
	}

	/**
	 * Constructs a <code>RstatClient</code> client stub proxy object from which
	 * the RSTATPROG remote program can be accessed.
	 * 
	 * @param client
	 *            ONC/RPC client connection object implementing a particular
	 *            protocol.
	 * @throws OncRpcException
	 *             if an ONC/RPC error occurs.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public RstatClient(final OncRpcClient clnt) throws OncRpcException, IOException {
		super(clnt);
	}

	/**
	 * Constructs a <code>RstatClient</code> client stub proxy object from which
	 * the RSTATPROG remote program can be accessed.
	 * 
	 * @param host
	 *            Internet address of host where to contact the remote program.
	 * @param program
	 *            Remote program number.
	 * @param version
	 *            Remote program version number.
	 * @param protocol
	 *            {@link org.acplt.oncrpc.OncRpcProtocols Protocol} to be used
	 *            for ONC/RPC calls.
	 * @throws OncRpcException
	 *             if an ONC/RPC error occurs.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public RstatClient(final InetAddress host, final int program, final int version, final int protocol)
			throws OncRpcException, IOException {
		super(host, program, version, 0, protocol);
	}

	/**
	 * Constructs a <code>RstatClient</code> client stub proxy object from which
	 * the RSTATPROG remote program can be accessed.
	 * 
	 * @param host
	 *            Internet address of host where to contact the remote program.
	 * @param program
	 *            Remote program number.
	 * @param version
	 *            Remote program version number.
	 * @param port
	 *            Port number at host where the remote program can be reached.
	 * @param protocol
	 *            {@link org.acplt.oncrpc.OncRpcProtocols Protocol} to be used
	 *            for ONC/RPC calls.
	 * @throws OncRpcException
	 *             if an ONC/RPC error occurs.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public RstatClient(final InetAddress host, final int program, final int version, final int port, final int protocol)
			throws OncRpcException, IOException {
		super(host, program, version, port, protocol);
	}

	/**
	 * Call remote procedure RSTATPROC_PING.
	 * 
	 * @return Result from remote procedure call (of type statstime).
	 * @throws OncRpcException
	 *             if an ONC/RPC error occurs.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public void RSTATPROC_PING() throws OncRpcException {
		final XdrVoid args$ = XdrVoid.XDR_VOID;
		final XdrVoid result$ = XdrVoid.XDR_VOID;
		this.client.call(RstatConstants.RSTATPROC_PING, RstatConstants.RSTATVERS_TIME, args$, result$);
		return;
	}

	/*
	 * Get statstime data from remote meachine and return cpuUsage
	 */
	public int getCPUUsage() throws OncRpcException {
		final Statstime statstime = this.RSTATPROC_STATS_3();
		final int totalCpuTicks = statstime.cp_time[0] + statstime.cp_time[1] + statstime.cp_time[2]
				+ statstime.cp_time[3];
		final int idleCpu = statstime.cp_time[3] * 100 / totalCpuTicks;
		final int cpuUsage = 100 - idleCpu;
		return cpuUsage;
	}

	public int getUserCPUUsage() throws Exception {
		return 0; // IMPLEMENTATION PENDING
	}

	public float getSystemCPUUsage() throws Exception {
		return 0; // IMPLEMENTATION PENDING
	}

	public int getWaitCPUUsage() throws Exception {
		return 0; // IMPLEMENTATION PENDING
	}

	public int get1MinLoadAverage() throws Exception {
		return 0; // IMPLEMENTATION PENDING
	}

	public int get5MinLoadAverage() throws Exception {
		return 0; // IMPLEMENTATION PENDING
	}

	public int get15MinLoadAverage() throws Exception {
		return 0; // IMPLEMENTATION PENDING
	}

	/*
	 * Get statstime data from remote meachine and return NetworkInfo
	 */
	public NetworkInfo[] getNetworkInfo() throws OncRpcException {
		final Statstime statstime = this.RSTATPROC_STATS_3();
		final NetworkInfo[] networkInfo = new NetworkInfo[1];
		networkInfo[0] = new UnixNetworkInfo("Network", 0, statstime.if_ipackets, statstime.if_opackets);
		return networkInfo;
	}

	/**
	 * Call remote procedure RSTATPROC_STATS_3.
	 * 
	 * @return Result from remote procedure call (of type statstime).
	 * @throws OncRpcException
	 *             if an ONC/RPC error occurs.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public Statstime RSTATPROC_STATS_3() throws OncRpcException {
		final XdrVoid args$ = XdrVoid.XDR_VOID;
		final Statstime result$ = new Statstime();
		this.client.call(RstatConstants.RSTATPROC_STATS_3, RstatConstants.RSTATVERS_TIME, args$, result$);
		return result$;
	}

	public void collectData() throws Exception {
		// do nothing

	}

	public void disconnect() throws IOException, OncRpcException {
		this.close();
	}

	public DriveInfo[] getDiskSpaceInfo() throws Exception {
		throw new RuntimeException("Logical Disk info is not supported by RSTAT");
	}

	public DiskInfo[] getPhysicalDiskInfo() throws Exception {
		throw new RuntimeException("Physical Disk info is not supported by RSTAT");
	}

	public MemoryInfo getPhysicalMemoryInfo() throws Exception {
		throw new RuntimeException("Physical Memory info is not supported by RSTAT");
	}

	public ProcessInfo[] getProcessInfo() throws Exception {
		throw new RuntimeException("Process info is not supported by RSTAT");
	}

	public int getProcessCount() throws Exception {
		throw new RuntimeException("Process count is not supported by RSTAT");
	}

	public MemoryInfo getVirtualMemoryInfo() throws Exception {
		throw new RuntimeException("Virtual Memory info is not supported by RSTAT");
	}

	public LogInfo searchInLogFile(String logFile, String[] search, long lastLine) {
		throw new RuntimeException("Search info in log file is not supported by RSTAT");
	}

	public void executeScript(String scriptFile) {
		throw new RuntimeException("Execution of a remote script file is not supported by RSTAT");
	}

	public NFSStat getNFSStats() throws Exception {
		throw new RuntimeException("NFS Stats is not supported by RSTAT");
	}

	public boolean ping() throws Exception {
		this.RSTATPROC_PING();
		return true;
	}

	public long getLastLogFileLineNumber(String logFile) throws Exception {
		return 1;
	}
}
// End of RstatClient.java
