/*
 * @(#)  XAxisTimeSeries.java Feb 4, 2005
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

import com.queryio.common.charts.util.TimeValueFormatter;

/**
 * @author Exceed Consultancy Services
 */
public class XAxisTimeSeries extends XAxisLongSeries
{
	private boolean bShowRelativeTime = false;

	public XAxisTimeSeries()
	{
		super(TIMEVALUES);
	}

	public XAxisTimeSeries(final boolean bShowRelativeTime)
	{
		super(TIMEVALUES);
		this.bShowRelativeTime = bShowRelativeTime;
	}

	public XAxisTimeSeries(final String text)
	{
		super(TIMEVALUES, text);
	}

	public String getFormattedMaxValue()
	{
		return TimeValueFormatter.getFormattedValue(this.minValue, this.maxValue, this.maxValue, this.bShowRelativeTime, this.crossesMidNight);
	}

	public String getFormattedMinValue()
	{
		return TimeValueFormatter.getFormattedValue(this.minValue, this.minValue, this.maxValue, this.bShowRelativeTime, this.crossesMidNight);
	}

	public String getFormattedValue(final int index)
	{
		return TimeValueFormatter.getFormattedValue(this.values[0], this.values[index],
			this.values[this.values.length - 1], this.bShowRelativeTime, this.crossesMidNight);
	}

	public String getFormattedValue(final long startTime, final long currTime, final long endTime)
	{
		return TimeValueFormatter.getFormattedValue(startTime, currTime, endTime, this.bShowRelativeTime, this.crossesMidNight);
	}

	public boolean isBShowRelativeTime()
	{
		return this.bShowRelativeTime;
	}
}
