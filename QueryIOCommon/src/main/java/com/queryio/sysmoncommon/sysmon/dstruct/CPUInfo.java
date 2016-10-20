/*
 * @(#)  DiskInfo.java
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
package com.queryio.sysmoncommon.sysmon.dstruct;

/**
 * 
 * @author Exceed Consultancy Services
 */
public class CPUInfo
{
	private String sName;
	private float fCPUUsage;

	/**
	 * Constructs CPUInfo object
	 * 
	 * @param name -
	 *            name of the CPU
	 * @param usage -
	 *            cpu usage
	 */
	public CPUInfo(final String name, final float usage)
	{
		this.sName = name;
		this.fCPUUsage = usage;
	}

	/**
	 * Returns name of the CPU
	 * 
	 * @return CPU name
	 */
	public String getName()
	{
		return this.sName;
	}

	/**
	 * set name of the CPU
	 * 
	 * @param CPUname
	 */
	public void setName(final String name)
	{
		this.sName = name;
	}

	/**
	 * Returns cpu usage
	 * 
	 * @return cpu usage
	 */
	public float getCPUUsage() throws Exception
	{
		if ((this.fCPUUsage < 0) || (this.fCPUUsage > 100))
		{
			throw new Exception("CPU usage incorrect");
		}
		return this.fCPUUsage;
	}

	/**
	 * Set cpu usage
	 * 
	 * @param usage
	 */
	public void setCPUUsage(final float usage)
	{
		this.fCPUUsage = usage;
	}
}
