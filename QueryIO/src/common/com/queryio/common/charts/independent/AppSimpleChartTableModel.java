/*
 * @(#)  AppSimpleChartTableModel.java May 23, 2006
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

package com.queryio.common.charts.independent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.queryio.common.IProductConstants;
import com.queryio.common.charts.components.Legend;
import com.queryio.common.charts.series.Series;
import com.queryio.common.charts.series.XAxisSeries;
import com.queryio.common.charts.series.YAxisSeries;

/**
 * This class defines the table model for all simple charts.
 * 
 * @author Exceed Consultancy Services
 */
public class AppSimpleChartTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = 752600005701000030L;
	private final List xAxisValues = new ArrayList();
	private final List plotValues = new ArrayList();

	public AppSimpleChartTableModel(final AppSimpleChart chart)
	{
		this.resetValues();
		this.addColumns(chart);
		this.populateValues(chart);
	}

	/**
	 * The columns that the table should have.
	 * 
	 * @param chart
	 *            The chart whose data is being represented in table form.
	 */
	private void addColumns(final AppSimpleChart chart)
	{
		this.addColumn(chart.getXAxisTitle());
		final Legend[] legend = chart.getLegend();
		final int legendLen = legend.length;
		for (int i = 0; i < legendLen; i++)
		{
			this.addColumn(legend[i].getText());
		}
	}

	/**
	 * populates (add rows to) the table using data available from the charts
	 * 
	 * @param chart
	 *            The chart whose data is being represented in table form.
	 */
	private void populateValues(final AppSimpleChart chart)
	{
		final XAxisSeries xAxisSeries = chart.getXAxisSeries();
		final Series[] yAxisSeries = chart.getYAxisSeries();
		final int yAxisSeriesLen = yAxisSeries.length;
		int[] yAxisSeriesValues;
		int currentCount = xAxisSeries.getCurrentCount();
		for (int i = 0; i < currentCount; i++)
		{
			if (!IProductConstants.EMPTY_STRING.equals(xAxisSeries.getFormattedValue(i)))
			{
				this.xAxisValues.add(xAxisSeries.getFormattedValue(i));
				yAxisSeriesValues = new int[yAxisSeriesLen];
				for (int j = 0; j < yAxisSeriesLen; j++)
				{
					yAxisSeriesValues[j] = ((YAxisSeries) yAxisSeries[j]).getValue(i);
				}
				this.plotValues.add(yAxisSeriesValues);
			}
		}
	}

	/**
	 * reset all the values
	 */
	private void resetValues()
	{
		this.xAxisValues.clear();
		this.plotValues.clear();
	}

	public int getRowCount()
	{
		return this.xAxisValues != null ? this.xAxisValues.size() : 0;
	}

	public Object getValueAt(final int row, final int column)
	{
		if (column == 0)
		{
			return this.xAxisValues.get(row);
		}
		final int i = ((int[]) this.plotValues.get(row))[column - 1];
		if (i != Integer.MIN_VALUE)
		{
			return new Integer(i);
		}
		return IProductConstants.EMPTY_STRING;
	}

}
