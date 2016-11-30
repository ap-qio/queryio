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
package com.queryio.common.dstruct;

/**
 * 
 * @author Exceed Consultancy Services
 */
public class AIXDiskInfo extends DiskInfo {
	private long lTotalReads = 0;
	private long lTotalWrites = 0;

	private long lReadTimeStamp = 0;
	private long lWriteTimeStamp = 0;

	public AIXDiskInfo(final String name, final long lTotalReads, final long lTotalWrites) {
		super(name, 0, 0);
		this.lTotalReads = lTotalReads;
		this.lTotalWrites = lTotalWrites;

		final long ts = System.currentTimeMillis();
		this.lReadTimeStamp = ts;
		this.lWriteTimeStamp = ts;
	}

	public long getTotalReads() throws Exception {
		if ((this.lTotalReads < 0) || (this.lTotalReads > Long.MAX_VALUE)) {
			throw new Exception("Total reads value is incorrect");
		}
		return this.lTotalReads;
	}

	public long getTotalWrites() throws Exception {
		if ((this.lTotalWrites < 0) || (this.lTotalWrites > Long.MAX_VALUE)) {
			throw new Exception("Total writes value is incorrect");
		}
		return this.lTotalWrites;
	}

	public void setTotalReads(final long totalReads) {
		// calculate the reads per second from the older value
		// this has to be done only if it is not the first time
		// that the disk has been found
		final long lCurrTimeStamp = System.currentTimeMillis();
		long lTimeStampDiffInSecs = (lCurrTimeStamp - this.lReadTimeStamp) / 1000;
		lTimeStampDiffInSecs = (lTimeStampDiffInSecs > 0 ? lTimeStampDiffInSecs : 1);
		this.fReadsPerSec = Math.max(0, (float) ((totalReads - this.lTotalReads) / lTimeStampDiffInSecs));

		this.lReadTimeStamp = lCurrTimeStamp;
		this.lTotalReads = totalReads;
	}

	public void setTotalWrites(final long totalWrites) {
		// calculate the writes per second from the older value
		// this has to be done only if it is not the first time
		// that the disk has been found
		final long lCurrTimeStamp = System.currentTimeMillis();
		long lTimeStampDiffInSecs = (lCurrTimeStamp - this.lWriteTimeStamp) / 1000;

		lTimeStampDiffInSecs = (lTimeStampDiffInSecs > 0 ? lTimeStampDiffInSecs : 1);
		this.fWritesPerSec = Math.max(0, (float) ((totalWrites - this.lTotalWrites) / lTimeStampDiffInSecs));

		this.lWriteTimeStamp = lCurrTimeStamp;
		this.lTotalWrites = totalWrites;
	}
}
