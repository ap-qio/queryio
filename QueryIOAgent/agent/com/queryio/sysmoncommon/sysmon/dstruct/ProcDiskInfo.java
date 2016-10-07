/**
 * 
 */
package com.queryio.sysmoncommon.sysmon.dstruct;

/**
 * @author Sudhan Moghe
 *
 */
public class ProcDiskInfo extends DiskInfo
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4513658613574135689L;
	private long lTotalBytesRead = 0;
	private long lTotalBytesWritten = 0;

	private long lBytesReadTimeStamp = 0;
	private long lBytesWrittenTimeStamp = 0;

	/**
	 * @param name
	 * @param readsPerSec
	 * @param writesPerSec
	 */
	public ProcDiskInfo(String name, long totalBytesRead, long totalBytesWritten)
	{
		super(name, 0, 0);
		this.lTotalBytesRead = totalBytesRead;
		this.lTotalBytesWritten = totalBytesWritten;

		final long ts = System.currentTimeMillis();
		this.lBytesReadTimeStamp = ts;
		this.lBytesWrittenTimeStamp = ts;

	}

	/**
	 * Returns the iTotalBytesRead.
	 * 
	 * @return int
	 */
	public long getTotalBytesRead() throws Exception
	{
		if ((this.lTotalBytesRead < 0) || (this.lTotalBytesRead > Long.MAX_VALUE))
		{
			throw new Exception("Total packets received value is incorrect");
		}
		return this.lTotalBytesRead;
	}

	/**
	 * Returns the iTotalBytesWritten.
	 * 
	 * @return int
	 */
	public long getTotalBytesWritten() throws Exception
	{
		if ((this.lTotalBytesWritten < 0) || (this.lTotalBytesWritten > Long.MAX_VALUE))
		{
			throw new Exception("Total packets sent value is incorrect");
		}
		return this.lTotalBytesWritten;
	}

	/**
	 * Sets the totalBytesRead.
	 * 
	 * @param totalBytesRead
	 *            The TotalBytesRead to set
	 */
	public void setTotalBytesRead(final long totalBytesRead)
	{
		// calculate the bytes read per second from the older value
		final long lCurrTimeStamp = System.currentTimeMillis();
		float lTimeStampDiffInSecs = ((float)(lCurrTimeStamp - this.lBytesReadTimeStamp)) / 1000;

		lTimeStampDiffInSecs = (lTimeStampDiffInSecs > 0 ? lTimeStampDiffInSecs : 1);

		this.fReadsPerSec = Math.max(0,
				(float) ((totalBytesRead - this.lTotalBytesRead) / lTimeStampDiffInSecs));

		this.lBytesReadTimeStamp = lCurrTimeStamp;
		this.lTotalBytesRead = totalBytesRead;
	}

	/**
	 * Sets the totalBytesWritten.
	 * 
	 * @param totalBytesWritten
	 *            The TotalBytesWritten to set
	 */
	public void setTotalBytesWritten(final long totalBytesWritten)
	{
		// calculate the bytes written per second from the older value
		final long lCurrTimeStamp = System.currentTimeMillis();
		float lTimeStampDiffInSecs = ((float)(lCurrTimeStamp - this.lBytesWrittenTimeStamp)) / 1000;

		lTimeStampDiffInSecs = (lTimeStampDiffInSecs > 0 ? lTimeStampDiffInSecs : 1);

		this.fWritesPerSec = Math.max(0,
				 ((totalBytesWritten - this.lTotalBytesWritten) / lTimeStampDiffInSecs));

		this.lBytesWrittenTimeStamp = lCurrTimeStamp;
		this.lTotalBytesWritten = totalBytesWritten;
	}
}
