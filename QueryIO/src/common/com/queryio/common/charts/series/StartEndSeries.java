/*
 * @(#)  StartEndSeries.java Feb 4, 2005
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
package com.queryio.common.charts.series;

/**
 * @author Exceed Consultancy Services
 */
public class StartEndSeries extends Series
{
	private long[] start = null;

	private long[] end = null;

	private long minValue = Long.MAX_VALUE;

	private long maxValue = Long.MIN_VALUE;

	private int currentIndex = -1;

	/**
	 * 
	 */
	StartEndSeries()
	{
		super();
	}

	/**
	 * @param text
	 */
	StartEndSeries(final String text)
	{
		super(text);
	}

	public void initialize(int capacity)
	{
		if (capacity <= 0)
		{
			capacity = 1;
		}
		this.start = new long[capacity];
		this.end = new long[capacity];
		this.init(0, capacity);
		this.currentIndex = -1;
		this.minValue = Long.MAX_VALUE;
		this.maxValue = Long.MIN_VALUE;
	}

	private void init(final int startIndex, final int length)
	{
		for (int i = startIndex; i < length; i++)
		{
			this.start[i] = Long.MIN_VALUE;
			this.end[i] = Long.MIN_VALUE;
		}
	}

	public int getCapacity()
	{
		return (this.start != null ? this.start.length : 0);
	}

	public void setValue(final int index, final long startValue, final long endValue)
	{
		if (index >= this.getCapacity())
		{
			final int newLen = Math.round(this.getCapacity() * 1.75f);
			// increase the size of series object by increment
			final long[] destStart = new long[newLen];
			System.arraycopy(this.start, 0, destStart, 0, this.start.length);
			this.start = destStart;

			final long[] destEnd = new long[newLen];
			System.arraycopy(this.end, 0, destEnd, 0, this.end.length);
			this.end = destEnd;

			this.init(this.start.length, destStart.length - this.start.length);
		}

		this.start[index] = startValue;
		this.end[index] = endValue;
		if (Long.MIN_VALUE != startValue)
		{
			this.minValue = Math.min(this.minValue, startValue);
		}
		if (Long.MIN_VALUE != endValue)
		{
			this.maxValue = Math.max(this.maxValue, endValue);
		}
		this.currentIndex = Math.max(this.currentIndex, index);
	}

	public void setNextValue(final long startValue, final long endValue)
	{
		this.setValue(++this.currentIndex, startValue, endValue);
	}

	public void updateEndValue(final long endValue)
	{
		this.setValue(this.currentIndex, this.start[this.currentIndex], endValue);
	}

	public long getStartValue(final int index)
	{
		return this.start[index];
	}

	public long getEndValue(final int index)
	{
		return this.end[index];
	}

	/**
	 * method getMaxValue
	 * 
	 * @return
	 */
	public final long getMaxValue()
	{
		return this.maxValue;
	}

	/**
	 * method getMinValue
	 * 
	 * @return
	 */
	public final long getMinValue()
	{
		return this.minValue;
	}

	public int getCurrentCount()
	{
		return this.currentIndex + 1;
	}

}
