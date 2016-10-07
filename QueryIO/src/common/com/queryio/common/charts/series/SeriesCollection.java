/*
 * @(#)  SeriesCollection.java Feb 7, 2005
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
 * 
 * @author Exceed Consultancy Services
 */
public class SeriesCollection
{
	private int intialCapacity = 10;
	private int increment = 5;

	private int currentIndex = -1;

	private ComplexSeries[] series = null;

	public SeriesCollection()
	{
		this.init();
	}

	public SeriesCollection(final int intialCapacity)
	{
		this.intialCapacity = intialCapacity;
		this.init();
	}

	public SeriesCollection(final int intialCapacity, final int increment)
	{
		this.intialCapacity = intialCapacity;
		this.increment = increment;
		this.init();
	}

	private void init()
	{
		this.series = new ComplexSeries[this.intialCapacity];
		this.currentIndex = -1;
	}

	public int addSeries(final String name, final int capacity)
	{
		final int index = this.currentIndex + 1;
		if (index >= this.series.length)
		{
			// increase the size of series object by increment
			final ComplexSeries[] destSeries = new ComplexSeries[this.series.length + this.increment];
			System.arraycopy(this.series, 0, destSeries, 0, this.series.length);
			this.series = destSeries;
		}
		this.series[index] = new ComplexSeries(name);
		this.series[index].intialize(capacity);
		++this.currentIndex;

		return this.currentIndex;
	}

	/**
	 * SeriesCollection # removeAllSeries
	 * 
	 */
	public void removeAllSeries()
	{
		this.init();
	}

	public ComplexSeries removeComplexSeries(final int index)
	{
		final ComplexSeries removedSeries = this.getComplexSeries(index);
		if (removedSeries != null)
		{
			// shuffle the series
			final ComplexSeries[] destSeries = new ComplexSeries[this.series.length - 1];
			System.arraycopy(this.series, 0, destSeries, 0, index - 1);
			System.arraycopy(this.series, index + 1, destSeries, index, this.series.length - index);
			this.series = destSeries;
			--this.currentIndex;
		}
		return removedSeries;
	}

	public ComplexSeries removeComplexSeries(final String complexSeriesName)
	{
		return this.removeComplexSeries(this.find(complexSeriesName));
	}

	public ComplexSeries getComplexSeries(final int index)
	{
		return (index == -1 ? null : this.series[index]);
	}

	public ComplexSeries getComplexSeries(final String complexSeriesName)
	{
		return this.getComplexSeries(this.find(complexSeriesName));
	}

	private int find(final String complexSeriesName)
	{
		for (int i = 0; i < this.getCurrentCount(); i++)
		{
			if (this.series[i].getName().equals(complexSeriesName))
			{
				return i;
			}
		}
		return -1;
	}

	public int getCurrentCount()
	{
		return this.currentIndex + 1;
	}
}
