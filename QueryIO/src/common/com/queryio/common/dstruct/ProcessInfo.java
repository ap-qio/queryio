/*
 * @(#)  ProcessInfo.java
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
package com.queryio.common.dstruct;

import java.io.Serializable;

/**
 * 
 * @author Exceed Consultancy Services
 */
public class ProcessInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8248845680156063639L;
	private final String sName;
	protected final int iProcessId;
	protected int iMemoryUsage;
	protected float fProcessorTime;
	protected int iThreadCount;
	private int iPeakMemoryUsage;
	private String userName = null;
	private String command = null;

	/**
	 * Constructs ProcessInfo object
	 * 
	 * @param name -
	 *            process name
	 * @param id -
	 *            process id
	 * @param memUsage -
	 *            memory usage by the process
	 * @param processorTime -
	 *            Total processor time used by the process
	 */
	public ProcessInfo(final String name, final int id, final int memUsage, final float processorTime)
	{
		this.sName = name;
		this.iProcessId = id;
		this.iMemoryUsage = memUsage;
		this.fProcessorTime = processorTime;
	}

	/**
	 * Constructs ProcessInfo object
	 * 
	 * @param name -
	 *            process name
	 * @param id -
	 *            process id
	 * @param memUsage -
	 *            memory usage by the process
	 * @param processorTime -
	 *            Total processor time used by the process
	 * @param memUsagePeak -
	 *            Peak memory usage
	 * @param threadCount -
	 *            thread Count
	 */
	public ProcessInfo(final String name, final int id, final int memUsage, final float processorTime,
			final int memUsagePeak, final int threadCount)
	{
		this(name, id, memUsage, processorTime);
		this.iPeakMemoryUsage = memUsagePeak;
		this.iThreadCount = threadCount;
	}

	/**
	 * Returns name of the proess
	 * 
	 * @return String
	 */
	public String getName()
	{
		return this.sName;
	}

	/**
	 * Returns Process Id
	 * 
	 * @return int
	 */
	public int getProcessID() throws Exception
	{
		if ((this.iProcessId < 0) || (this.iProcessId > Integer.MAX_VALUE))
		{
			throw new Exception("Process Id incorrect");
		}
		return this.iProcessId;
	}

	/**
	 * Returns memory used of the process
	 * 
	 * @return int
	 */
	public int getMemoryUsage() throws Exception
	{
		if ((this.iMemoryUsage < 0) || (this.iMemoryUsage > Integer.MAX_VALUE))
		{
			throw new Exception("Memory usage value is incorrect");
		}
		return this.iMemoryUsage;
	}

	/**
	 * Returns Processor Time used by the process
	 * 
	 * @return float
	 */
	public int getProcessorTime() throws Exception
	{
		if ((this.fProcessorTime < 0) || (this.fProcessorTime > Float.POSITIVE_INFINITY))
		{
			throw new Exception("Processor time value is incorrect");
		}
		return Math.round(this.fProcessorTime);
	}

	/**
	 * Method getPeakMemoryUsage
	 * 
	 * @return int
	 */
	public int getPeakMemoryUsage()
	{
		if ((this.iPeakMemoryUsage < 0) || (this.iPeakMemoryUsage > Integer.MAX_VALUE))
		{
			throw new RuntimeException("Peak memory usage value is incorrect");
		}
		return this.iPeakMemoryUsage;
	}

	/**
	 * Method getThreadCount
	 * 
	 * @return int
	 */
	public int getThreadCount()
	{
		if ((this.iThreadCount < 0) || (this.iThreadCount > Integer.MAX_VALUE))
		{
			throw new RuntimeException("Thread count value is incorrect");
		}
		return this.iThreadCount;
	}

	public String getUserName() 
	{
		return userName;
	}

	public void setUserName(String userName) 
	{
		this.userName = userName;
	}

	public String getCommand() 
	{
		return command;
	}

	public void setCommand(String command) 
	{
		this.command = command;
	}
}
