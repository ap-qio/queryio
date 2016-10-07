/*
 * @(#)  ChartTableModelFactory.java May 25, 2006
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

public class ChartTableModelFactory
{
	private ChartTableModelFactory()
	{

	}

	public static DefaultTableModel getChartTableModel(final AppChart appChart)
	{
		if (appChart instanceof AppSimpleChart)
		{
			return new AppSimpleChartTableModel((AppSimpleChart) appChart);
		}
		if (appChart instanceof AppComplexChart)
		{
			return new AppComplexChartTableModel((AppComplexChart) appChart);
		}
		if (appChart instanceof AppPieChart)
		{
			return new AppPieChartTableModel((AppPieChart) appChart);
		}
		return null;
	}
}
