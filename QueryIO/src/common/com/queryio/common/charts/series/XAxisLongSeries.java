/*
 * @(#)  XAxisLongSeries.java Feb 4, 2005
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

import java.util.Calendar;

/**
 * @author Exceed Consultancy Services
 */
public class XAxisLongSeries extends XAxisSeries
{

	private static final long TIME_24_HOURS = 1000 * 60 * 60 * 24;
	protected long minValue = Long.MAX_VALUE;

	protected long maxValue = Long.MIN_VALUE;

	protected long[] values = null;
	
	protected boolean crossesMidNight = false;

	public XAxisLongSeries(byte valuesType) 
	{
		super(valuesType);
	}
	
	public XAxisLongSeries(byte valuesType, String text) 
	{
		super(valuesType, text);
	}

	public void initialize(int capacity)
	{
		if (capacity <= 0)
		{
			capacity = 1;
		}
		this.values = new long[capacity];
		for (int i = 0; i < capacity; i++)
		{
			this.values[i] = Long.MIN_VALUE;
		}
		this.currentIndex = -1;
		this.minValue = Long.MAX_VALUE;
		this.maxValue = Long.MIN_VALUE;
	}

	public int getCapacity()
	{
		return (this.values != null ? this.values.length : 0);
	}

	public void setValue(final int index, final long value)
	{
		this.values[index] = value;
		if (Long.MIN_VALUE != value)
		{
			this.minValue = Math.min(this.minValue, value);
			this.maxValue = Math.max(this.maxValue, value);
		}
		this.currentIndex = Math.max(this.currentIndex, index);
	}

	public void setNextValue(final long value)
	{
		this.setValue(++this.currentIndex, value);
	}

	public long getValue(final int index)
	{
		return this.values[index];
	}

	public final long getMaxValue()
	{
		return this.maxValue;
	}

	public final long getMinValue()
	{
		return this.minValue;
	}

	public String getFormattedMaxValue()
	{
		return String.valueOf(this.maxValue);
	}

	public String getFormattedMinValue()
	{
		return String.valueOf(this.minValue);
	}

	public String getFormattedValue(final int index)
	{
		return getFormattedValueOf(this.values[index]);
	}

	public void setMaxValue(final long maxValue)
	{
		this.maxValue = maxValue;
	}

	public void setMinValue(final long minValue)
	{
		this.minValue = minValue;
	}
	
	public void calculateCrossedMidNight(long minTime)
	{
		if (minTime != maxValue && maxValue != Long.MAX_VALUE)
		{
			if (maxValue - minTime > TIME_24_HOURS)
			{
				crossesMidNight = true;
			}
			else
			{
				final Calendar maxCal = Calendar.getInstance();
				maxCal.setTimeInMillis(maxValue);
				final Calendar minCal = Calendar.getInstance();
				minCal.setTimeInMillis(minTime);
				crossesMidNight = (maxCal.get(Calendar.DAY_OF_YEAR) - minCal.get(Calendar.DAY_OF_YEAR) > 0);
			}
		}
	}

	public boolean isCrossedMidNight() 
	{
		return crossesMidNight;
	}
	
	public long[] returnAllValues()
	{
		return this.values;
	}
}
