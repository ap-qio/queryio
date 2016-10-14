/*
 * @(#)  UnixNetworkInfo.java
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

// import org.apache.log4j.Logger;

/**
 * 
 * @author Exceed Consultancy Services
 */
public class UnixNetworkInfo extends NetworkInfo
{
	private static final long UNSIGNED_32BIT_LIMIT = 4294967295l;
	private static final long serialVersionUID = 752600005701000046L;
	private long lTotalRecdPackets = 0;
	private long lTotalSentPackets = 0;

	private long lPacketsRecdTimeStamp = 0;
	private long lPacketsSentTimeStamp = 0;

	/**
	 * Method NetworkInfo.
	 * 
	 * @param name
	 * @param interfaceId
	 * @param totalRecdPackets
	 * @param totalSentPackets
	 */
	public UnixNetworkInfo(final String name, final int interfaceId, final long totalRecdPackets,
			final long totalSentPackets)
	{
		super(name, interfaceId, 0, 0);

		this.lTotalRecdPackets = totalRecdPackets;
		this.lTotalSentPackets = totalSentPackets;

		final long ts = System.currentTimeMillis();
		this.lPacketsRecdTimeStamp = ts;
		this.lPacketsSentTimeStamp = ts;

	}

	/**
	 * Returns the iTotalRecdPackets.
	 * 
	 * @return int
	 */
	public long getTotalRecdPackets() throws Exception
	{
		if ((this.lTotalRecdPackets < 0) || (this.lTotalRecdPackets > Long.MAX_VALUE))
		{
			throw new Exception("Total packets received value is incorrect");
		}
		return this.lTotalRecdPackets;
	}

	/**
	 * Returns the iTotalSentPackets.
	 * 
	 * @return int
	 */
	public long getTotalSentPackets() throws Exception
	{
		if ((this.lTotalSentPackets < 0) || (this.lTotalSentPackets > Long.MAX_VALUE))
		{
			throw new Exception("Total packets sent value is incorrect");
		}
		return this.lTotalSentPackets;
	}

	/**
	 * Sets the totalRecdPackets.
	 * 
	 * @param totalRecdPackets
	 *            The TotalRecdPackets to set
	 */
	public void setTotalRecdPackets(final long totalRecdPackets)
	{
		// calculate the packets recd per second from the older value
		// this has to be done only if it is not the first time
		// that the network Card has been found
		final long lCurrTimeStamp = System.currentTimeMillis();
		long lTimeStampDiffInSecs = (lCurrTimeStamp - this.lPacketsRecdTimeStamp) / 1000;

		lTimeStampDiffInSecs = (lTimeStampDiffInSecs > 0 ? lTimeStampDiffInSecs : 1);

		if (totalRecdPackets < lTotalRecdPackets)
		{
			//Value reached 32bit limit and wrapped around
			this.fReadMBPerSec = Math.max(0,
					(float) ((UNSIGNED_32BIT_LIMIT + totalRecdPackets - this.lTotalRecdPackets) / lTimeStampDiffInSecs));
		}
		else
		{
			this.fReadMBPerSec = Math.max(0,
					(float) ((totalRecdPackets - this.lTotalRecdPackets) / lTimeStampDiffInSecs));
		}

		this.lPacketsRecdTimeStamp = lCurrTimeStamp;
		this.lTotalRecdPackets = totalRecdPackets;
	}

	/**
	 * Sets the totalSentPackets.
	 * 
	 * @param totalSentPackets
	 *            The TotalSentPackets to set
	 */
	public void setTotalSentPackets(final long totalSentPackets)
	{
		// calculate the packets sent per second from the older value
		// this has to be done only if it is not the first time
		// that the network Card has been found
		final long lCurrTimeStamp = System.currentTimeMillis();
		long lTimeStampDiffInSecs = (lCurrTimeStamp - this.lPacketsSentTimeStamp) / 1000;

		lTimeStampDiffInSecs = (lTimeStampDiffInSecs > 0 ? lTimeStampDiffInSecs : 1);
		if (totalSentPackets < lTotalSentPackets)
		{
			//Value reached 32bit limit and wrapped around
			this.fSentMBPerSec = Math.max(0,
					(float) ((UNSIGNED_32BIT_LIMIT + totalSentPackets - this.lTotalSentPackets) / lTimeStampDiffInSecs));

		}
		else
		{
			this.fSentMBPerSec = Math.max(0,
					(float) ((totalSentPackets - this.lTotalSentPackets) / lTimeStampDiffInSecs));
		}

		this.lPacketsSentTimeStamp = lCurrTimeStamp;
		this.lTotalSentPackets = totalSentPackets;
	}
}
