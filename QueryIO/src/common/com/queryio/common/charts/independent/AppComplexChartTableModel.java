/*
 * @(#)  AppComplexChartTableModel.java May 26, 2006
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

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.queryio.common.charts.series.ComplexSeries;
import com.queryio.common.charts.series.SeriesCollection;
import com.queryio.common.charts.series.StartEndSeries;
import com.queryio.common.util.CommonResourceManager;
import com.queryio.common.util.ResourceManager;

/**
 * This class defines the table model for all complex charts.
 * 
 * @author Exceed Consultancy Services
 */
public class AppComplexChartTableModel extends DefaultTableModel
{
	private static final transient ResourceManager RM = CommonResourceManager.loadResources("Apcommon_CommonUI"); //$NON-NLS-1$
	private static final long serialVersionUID = 752600005701000028L;
	private final List values = new ArrayList();
	private List row;
	private boolean hasYAxisTitle;

	public AppComplexChartTableModel(final AppComplexChart chart)
	{
		this.resetValues();
		this.addColumns(chart);
		this.populateValues(chart);
	}

	/**
	 * reset all the values
	 */
	private void resetValues()
	{
		this.values.clear();
		this.hasYAxisTitle = false;
	}

	/**
	 * populates (add rows to) the table using data available from the charts
	 * 
	 * @param chart
	 *            The chart whose data is being represented in table form.
	 */
	private void populateValues(final AppComplexChart chart)
	{
		final SeriesCollection seriesCollection = chart.getSeriesCollection();
		final int currentCount = seriesCollection.getCurrentCount();
		ComplexSeries complexSeries;
		int complexSeriesCount;
		StartEndSeries startEndSeries;
		int startEndSeriesCurrentCount;
		for (int i = 0; i < currentCount; i++)
		{
			complexSeries = seriesCollection.getComplexSeries(i);
			complexSeriesCount = complexSeries.getSeriesCount();
			for (int j = 0; j < complexSeriesCount; j++)
			{
				startEndSeries = complexSeries.getSeries(j);
				startEndSeriesCurrentCount = startEndSeries.getCurrentCount();
				for (int k = 0; k < startEndSeriesCurrentCount; k++)
				{
					this.row = new ArrayList();
					if (this.hasYAxisTitle)
					{
						this.row.add(complexSeries.getName());
					}

					this.row.add(new Time(startEndSeries.getStartValue(k)));
					this.row.add(new Time(startEndSeries.getEndValue(k)));
					this.row.add(startEndSeries.getText());
					this.values.add(this.row);
				}
			}
		}
	}

	/**
	 * The columns that the table should have.
	 * 
	 * @param chart
	 *            The chart whose data is being represented in table form.
	 */
	private void addColumns(final AppComplexChart chart)
	{
		final String axisTitle = chart.getYAxisTitle();
		if ((axisTitle != null) && (axisTitle.length() > 0))
		{
			this.addColumn(axisTitle);
			this.hasYAxisTitle = true;
		}
		this.addColumn(RM.getString("COL_START_TIME")); //$NON-NLS-1$
		this.addColumn(RM.getString("COL_END_TIME")); //$NON-NLS-1$
		this.addColumn(RM.getString("COL_LEGEND")); //$NON-NLS-1$
	}

	public int getRowCount()
	{
		return this.values != null ? this.values.size() : 0;
	}

	public Object getValueAt(final int row, final int column)
	{
		return ((List) this.values.get(row)).get(column);
	}

}
