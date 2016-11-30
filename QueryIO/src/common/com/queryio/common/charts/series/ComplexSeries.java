/*
 * @(#)  ComplexSeries.java Feb 4, 2005
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
public class ComplexSeries {
	private final String name;

	ComplexSeries(final String name) {
		this.name = name;
	}

	StartEndSeries[] startEndSeries = null;

	void intialize(final int seriesCount) {
		this.startEndSeries = new StartEndSeries[seriesCount];
	}

	public void intializeSeries(final int startEndSeriesIndex, final String name, final Color seriesColor,
			final int capacity) {
		this.startEndSeries[startEndSeriesIndex] = new StartEndSeries(name);
		this.startEndSeries[startEndSeriesIndex].setSeriesColor(seriesColor);
		this.startEndSeries[startEndSeriesIndex].initialize(capacity);
	}

	public StartEndSeries getSeries(final int index) {
		return this.startEndSeries[index];
	}

	public final int getSeriesCount() {
		return this.startEndSeries.length;
	}

	/**
	 * method getName
	 * 
	 * @return
	 */
	public final String getName() {
		return this.name;
	}

}
