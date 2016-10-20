/*
 * @(#)  Series.java Feb 4, 2005
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

import com.queryio.common.exporter.dstruct.Color;

/**
 * 
 * @author Exceed Consultancy Services
 */
public abstract class Series
{
	private String text;

	private Color seriesColor = null;

	public Series()
	{
		this(null);
	}

	public Series(final String text)
	{
		this.text = text;
	}

	/**
	 * method getSeriesColor
	 * 
	 * @return
	 */
	public final Color getSeriesColor()
	{
		return this.seriesColor;
	}

	/**
	 * method getText
	 * 
	 * @return
	 */
	public final String getText()
	{
		return this.text;
	}

	/**
	 * method setSeriesColor
	 * 
	 * @param color
	 */
	public void setSeriesColor(final Color color)
	{
		this.seriesColor = color;
	}

	/**
	 * method setText
	 * 
	 * @param string
	 */
	public void setText(final String string)
	{
		this.text = string;
	}

	public abstract int getCapacity();

	public abstract void initialize(int capacity);

}
