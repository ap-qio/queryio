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

import java.io.Serializable;

/**
 * 
 * @author Exceed Consultancy Services
 */
public class DiskInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7805113197484043696L;
	private final String sName;
	protected float fReadsPerSec;
	protected float fWritesPerSec;

	/**
	 * Constructs DiskInfo object
	 * 
	 * @param name -
	 *            name of the dsik
	 * @param readsPerSec -
	 *            no of reads per second
	 * @param writesPerSec -
	 *            no of writes per second
	 */
	public DiskInfo(final String name, final float readsPerSec, final float writesPerSec)
	{
		this.sName = name;
		this.fReadsPerSec = readsPerSec;
		this.fWritesPerSec = writesPerSec;
	}

	/**
	 * Returns name of the disk
	 * 
	 * @return disk name
	 */
	public String getName()
	{
		return this.sName;
	}

	/**
	 * Returns reads per second of the disk
	 * 
	 * @return no of reads per second
	 */
	public float getReadsPerSec() throws Exception
	{
		if ((this.fReadsPerSec < 0) || (this.fReadsPerSec > Float.POSITIVE_INFINITY))
		{
			throw new Exception("Reads per sec value is incorrect");
		}
		return this.fReadsPerSec;
	}

	public void setReadsPerSec(float fReadsPerSec)
	{
		this.fReadsPerSec = fReadsPerSec;
	}
	
	/**
	 * Returns writes per second of the disk
	 * 
	 * @return no of writes per second
	 */
	public float getWritesPerSec() throws Exception
	{
		if ((this.fWritesPerSec < 0) || (this.fWritesPerSec > Float.POSITIVE_INFINITY))
		{
			throw new Exception("Writes per sec value is incorrect");
		}
		return this.fWritesPerSec;
	}
	
	public void setWritesPerSec(float fWritesPerSec)
	{
		this.fWritesPerSec = fWritesPerSec;
	}
}
