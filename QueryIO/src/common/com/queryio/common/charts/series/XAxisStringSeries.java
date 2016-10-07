/*
 * @(#)  XAxisStringSeries.java Feb 4, 2005
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

import com.queryio.common.IProductConstants;
import com.queryio.common.charts.interfaces.UserInterface;

/**
 * @author Exceed Consultancy Services
 */
public class XAxisStringSeries extends XAxisSeries
{
	public static final String DUMMYMAXVALUE = "OOOOOOOOOO"; //$NON-NLS-1$

	protected String[] values = null;

	private String minValue = DUMMYMAXVALUE;

	private String maxValue = IProductConstants.EMPTY_STRING;


	public XAxisStringSeries()
	{
		super(STRINGVALUES);
	}

	public XAxisStringSeries(final String text)
	{
		super(STRINGVALUES, text);
	}

	public void initialize(int capacity)
	{
		if (capacity <= 0)
		{
			capacity = 1;
		}

		this.values = new String[capacity];
		for (int i = 0; i < capacity; i++)
		{
			this.values[i] = IProductConstants.EMPTY_STRING;
		}
		this.currentIndex = -1;
		this.minValue = DUMMYMAXVALUE;
		this.maxValue = IProductConstants.EMPTY_STRING;
	}

	public int getCapacity()
	{
		return (this.values != null ? this.values.length : 0);
	}

	public void setValue(final int index, final String value)
	{
		this.values[index] = value;

		if (this.minValue.length() > value.length())
		{
			this.minValue = value;
		}

		if (this.maxValue.length() < value.length())
		{
			this.maxValue = value;
		}
		this.currentIndex = Math.max(this.currentIndex, index);
	}

	public void setNextValue(final String value)
	{
		this.setValue(++this.currentIndex, value);
	}

	public final String getMaxValue()
	{
		return this.maxValue;
	}

	public final String getMinValue()
	{
		return this.minValue;
	}

	public String getFormattedValue(final int index)
	{
		if(index>=values.length)	return "";
		return this.values[index];
	}

	public String[] getMinMaxValues(final UserInterface graphics)
	{
		int minValue = Integer.MAX_VALUE;
		int maxValue = Integer.MIN_VALUE;

		String strMinValue = IProductConstants.EMPTY_STRING;
		String strMaxValue = IProductConstants.EMPTY_STRING;

		for (int i = 0; i < this.values.length; i++)
		{
			if (minValue < graphics.getStringWidth(this.values[i]))
			{
				minValue = graphics.getStringWidth(this.values[i]);
				strMinValue = this.values[i];
			}

			if (graphics.getStringWidth(this.values[i]) > maxValue)
			{
				maxValue = graphics.getStringWidth(this.values[i]);
				strMaxValue = this.values[i];
			}
		}

		return new String[] { strMinValue, strMaxValue };
	}

	public String getFormattedMaxValue() 
	{
		return getMaxValue();
	}

	public String getFormattedMinValue() 
	{
		return getMinValue();
	}
}
