/*
 * @(#)  MemoryInfo.java
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
 * @author Exceed Consultancy Services
 * 
 */
public class MemoryInfo
{
	private int iTotal;
	private int iUsed;
	private int iAvailable;
	private float fPageFaultRate;

	/**
	 * Constructs MemoryInfo object
	 * 
	 * @param total -
	 *            total memory
	 * @param used -
	 *            used memory
	 * @param available -
	 *            available memory
	 */
	public MemoryInfo(final int total, final int used, final int available)
	{
		this.iTotal = total;
		this.iUsed = used;
		this.iAvailable = available;
		this.fPageFaultRate = -1;
	}

	/**
	 * Constructs MemoryInfo object
	 * 
	 * @param total -
	 *            total memory
	 * @param used -
	 *            used memory
	 * @param available -
	 *            available memory
	 * @param pageFault -
	 *            page faults per sec
	 */
	public MemoryInfo(final int total, final int used, final int available, final float pageFaultRate)
	{
		this.iTotal = total;
		this.iUsed = used;
		this.iAvailable = available;
		this.fPageFaultRate = pageFaultRate;
	}

	/**
	 * Returns Total memory
	 * 
	 * @return total
	 */
	public int getTotal() throws Exception
	{
		if ((this.iTotal < 0) || (this.iTotal > Float.POSITIVE_INFINITY))
		{
			throw new Exception("Total memory incorrect");
		}
		return this.iTotal;
	}

	/**
	 * Set Total memory
	 * 
	 * @param total
	 */
	public void setTotal(final int total)
	{
		this.iTotal = total;
	}

	/**
	 * Returns Used memory
	 * 
	 * @return used
	 */
	public int getUsed() throws Exception
	{
		if ((this.iUsed < 0) || (this.iUsed > Float.POSITIVE_INFINITY))
		{
			throw new Exception("Used memory is incorrect");
		}
		return this.iUsed;
	}

	/**
	 * setUsed
	 * 
	 * @param mem
	 */
	public void setUsed(final int mem)
	{
		this.iUsed = mem;
	}

	/**
	 * Returns Available memory
	 * 
	 * @return available
	 */
	public int getAvailable()
	{
		return this.iAvailable;
	}

	/**
	 * Set Available memory
	 * 
	 * @param available
	 */
	public void setAvailable(final int available)
	{
		this.iAvailable = available;
	}

	/**
	 * Returns Virtual Memory page faults per sec For Windows OS only
	 * 
	 * @return no. of page faults per sec
	 */
	public float getPageFaultRate()
	{
		return this.fPageFaultRate;
	}

	/**
	 * Set Virtual Memory page faults per sec For Windows OS only
	 * 
	 * @param faultRate
	 */
	public void setPageFaultRate(final float faultRate)
	{
		this.fPageFaultRate = faultRate;
	}
}
