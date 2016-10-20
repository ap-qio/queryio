/*
 * @(#)  AppPieChartTableModel May 23, 2006
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

import javax.swing.table.DefaultTableModel;

import com.queryio.common.util.CommonResourceManager;
import com.queryio.common.util.ResourceManager;

/**
 * This class defines the table model for all pie charts.
 * 
 * @author Exceed Consultancy Services
 */
public class AppPieChartTableModel extends DefaultTableModel
{
	private static final transient ResourceManager RM = CommonResourceManager.loadResources("Apcommon_CommonUI"); //$NON-NLS-1$
	private static final long serialVersionUID = 752600005701000029L;
	private String[][] tableValues;

	public AppPieChartTableModel(final AppPieChart chart)
	{
		this.addColumns();
		this.populateValues(chart);
	}

	/**
	 * populates (add rows to) the table using data available from the charts
	 * 
	 * @param chart
	 *            The chart whose data is being represented in table form.
	 */
	private void populateValues(final AppPieChart chart)
	{
		this.tableValues = chart.getTableValues();
	}

	/**
	 * The columns that the table should have.
	 * 
	 * @param chart
	 *            The chart whose data is being represented in table form.
	 */
	private void addColumns()
	{
		this.addColumn(RM.getString("COL_LEGEND"));
		this.addColumn(RM.getString("COL_PERCENTAGE"));
	}

	public int getRowCount()
	{
		return this.tableValues != null ? this.tableValues.length : 0;
	}

	public Object getValueAt(final int row, final int column)
	{
		return this.tableValues[row][column];
	}
}
