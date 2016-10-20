/*
 * @(#)  ChartFormatting.java Feb 8, 2005
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
package com.queryio.common.charts.util;

/**
 * 
 * @author Exceed Consultancy Services
 */
import com.queryio.common.charts.independent.AppAbstractBarChart;
import com.queryio.common.charts.independent.AppHorizontalBarChart;
import com.queryio.common.charts.independent.AppLineChart;
import com.queryio.common.charts.independent.AppPieChart;
import com.queryio.common.charts.independent.AppSimpleChart;
import com.queryio.common.charts.independent.ChartConstants;
import com.queryio.common.charts.series.YAxisSeries;
import com.queryio.common.exporter.dstruct.Color;

public final class ChartFormatting
{
	// chart series colours
	private static final Color CLR_SUCCESSFUL = new Color(0x00, 0xff, 0x00); // green
	private static final Color CLR_FAILED = new Color(0xff, 0x00, 0x00); // red
	private static final Color CLR_TIMEDOUT = new Color(0xff, 0xc8, 0x00); // orange
	private static final Color CLR_NOT_PLAYED = new Color(0xff, 0xff, 0x00); // yellow
	private static final Color CLR_IGNORED = new Color(0x00, 0x00, 0xff); // blue

	private ChartFormatting()
	{
		// Private constructor to prevent object instantiation of this class
	}

	/**
	 * In this method we set the Color of the series, background of the the Pie
	 * chart.
	 * 
	 * @param pieChart
	 */
	public static void formatAppPieChart(final AppPieChart pieChart)
	{
		pieChart.setSeriesColour(0, CLR_SUCCESSFUL);
		pieChart.setSeriesColour(1, CLR_FAILED);
		pieChart.setSeriesColour(2, CLR_TIMEDOUT);
		pieChart.setSeriesColour(3, CLR_NOT_PLAYED);
		pieChart.setSeriesColour(4, CLR_IGNORED);
	}

	/**
	 * In this method we set the Color of the series, background of the the Pie
	 * chart.
	 * 
	 * @param pieChart
	 */
	public static void formatAppPieChartWithoutTimedOut(final AppPieChart pieChart)
	{
		pieChart.setSeriesColour(0, CLR_SUCCESSFUL);
		pieChart.setSeriesColour(1, CLR_FAILED);
		pieChart.setSeriesColour(2, CLR_NOT_PLAYED);
		pieChart.setSeriesColour(3, CLR_IGNORED);
	}

	/**
	 * 
	 * @param chart
	 */
	public static void formatLineChart(final AppLineChart chart)
	{
		setAreaColor(chart);
		final YAxisSeries[] yAxesSeries = (YAxisSeries[]) chart.getYAxisSeries();
		for (int i = 0; i < yAxesSeries.length; i++)
		{
			switch (i)
			{
				case 0:
				{
					// yAxesSeries[0].setSeriesColor(CLR_SUCCESSFUL);
					chart.setYAxisSeriesColour(0, CLR_SUCCESSFUL);
					break;
				}
				case 1:
				{
					// yAxesSeries[1].setSeriesColor(CLR_FAILED);
					chart.setYAxisSeriesColour(1, CLR_FAILED);
					break;
				}
				case 2:
				{
					// yAxesSeries[2].setSeriesColor(CLR_IGNORED);
					chart.setYAxisSeriesColour(2, CLR_IGNORED);
					break;
				}
				case 3:
				{
					// yAxesSeries[3].setSeriesColor(CLR_NOT_PLAYED);
					chart.setYAxisSeriesColour(3, CLR_NOT_PLAYED);
					break;
				}
				case 4:
				{
					// yAxesSeries[4].setSeriesColor(CLR_TIMEDOUT);
					chart.setYAxisSeriesColour(4, CLR_TIMEDOUT);
					break;
				}
			}
		}
	}

	/**
	 * 
	 * @param chart
	 */
	public static void formatLineChartWithTimedOut(final AppLineChart chart)
	{
		setAreaColor(chart);
		final YAxisSeries[] yAxesSeries = (YAxisSeries[]) chart.getYAxisSeries();
		for (int i = 0; i < yAxesSeries.length; i++)
		{
			switch (i)
			{
				case 0:
				{
					// yAxesSeries[0].setSeriesColor(CLR_SUCCESSFUL);
					chart.setYAxisSeriesColour(0, CLR_SUCCESSFUL);
					break;
				}
				case 1:
				{
					// yAxesSeries[1].setSeriesColor(CLR_FAILED);
					chart.setYAxisSeriesColour(1, CLR_FAILED);
					break;
				}
				case 2:
				{
					// yAxesSeries[2].setSeriesColor(CLR_TIMEDOUT);
					chart.setYAxisSeriesColour(2, CLR_TIMEDOUT);
					break;
				}
				case 3:
				{
					// yAxesSeries[3].setSeriesColor(CLR_NOT_PLAYED);
					chart.setYAxisSeriesColour(3, CLR_NOT_PLAYED);
					break;
				}
				case 4:
				{
					// yAxesSeries[4].setSeriesColor(CLR_IGNORED);
					chart.setYAxisSeriesColour(4, CLR_IGNORED);
					break;
				}
			}
		}
	}

	/**
	 * 
	 * @param barChart
	 */
	public static void formatBarChart(final AppAbstractBarChart barChart)
	{
		setAreaColor(barChart);
		final YAxisSeries[] yAxesSeries = (YAxisSeries[]) barChart.getYAxisSeries();
		for (int i = 0; i < yAxesSeries.length; i++)
		{
			switch (i)
			{
				case 0:
				{
					// yAxesSeries[0].setSeriesColor(CLR_SUCCESSFUL);
					barChart.setYAxisSeriesColour(0, CLR_SUCCESSFUL);
					break;
				}
				case 1:
				{
					// yAxesSeries[1].setSeriesColor(CLR_FAILED);
					barChart.setYAxisSeriesColour(1, CLR_FAILED);
					break;
				}
				case 2:
				{
					// yAxesSeries[2].setSeriesColor(CLR_IGNORED);
					barChart.setYAxisSeriesColour(2, CLR_IGNORED);
					break;
				}
				case 3:
				{
					// yAxesSeries[3].setSeriesColor(CLR_NOT_PLAYED);
					barChart.setYAxisSeriesColour(3, CLR_NOT_PLAYED);
					break;
				}
				case 4:
				{
					// yAxesSeries[4].setSeriesColor(CLR_TIMEDOUT);
					barChart.setYAxisSeriesColour(4, CLR_TIMEDOUT);
					break;
				}
			}
		}
	}

	/**
	 * 
	 * @param barChart
	 */
	public static void formatBarChartWithTimedOut(final AppAbstractBarChart barChart)
	{
		setAreaColor(barChart);
		final YAxisSeries[] yAxesSeries = (YAxisSeries[]) barChart.getYAxisSeries();
		for (int i = 0; i < yAxesSeries.length; i++)
		{
			switch (i)
			{
				case 0:
				{
					// yAxesSeries[0].setSeriesColor(CLR_SUCCESSFUL);
					barChart.setYAxisSeriesColour(0, CLR_SUCCESSFUL);
					break;
				}
				case 1:
				{
					// yAxesSeries[1].setSeriesColor(CLR_FAILED);
					barChart.setYAxisSeriesColour(1, CLR_FAILED);
					break;
				}
				case 2:
				{
					// yAxesSeries[2].setSeriesColor(CLR_TIMEDOUT);
					barChart.setYAxisSeriesColour(2, CLR_TIMEDOUT);
					break;
				}
				case 3:
				{
					// yAxesSeries[3].setSeriesColor(CLR_NOT_PLAYED);
					barChart.setYAxisSeriesColour(3, CLR_NOT_PLAYED);
					break;
				}
				case 4:
				{
					// yAxesSeries[4].setSeriesColor(CLR_IGNORED);
					barChart.setYAxisSeriesColour(4, CLR_IGNORED);
					break;
				}
			}
		}
	}

	public static void setAreaColor(final AppSimpleChart barChart)
	{
		/*
		if (barChart.getUserInterface().getUserInterfaceType() == IProductConstants.USER_INTERFACE_SWT)
		{
			barChart.setAreaColor(ChartConstants.COLOR_WHITE);
		}
		else
		{
			barChart.setAreaColor(ChartConstants.COLOR_LIGHT_GRAY);
		}
		*/
		barChart.setAreaColor(ChartConstants.COLOR_WHITE);
	}

	public static void formatHorizontalBarChart(final AppHorizontalBarChart barChart)
	{
		setAreaColor(barChart);
		final YAxisSeries[] yAxesSeries = (YAxisSeries[]) barChart.getYAxisSeries();
		for (int i = 0; i < yAxesSeries.length; i++)
		{
			switch (i)
			{
				case 0:
				{
					// yAxesSeries[0].setSeriesColor(CLR_SUCCESSFUL);
					barChart.setYAxisSeriesColour(0, CLR_SUCCESSFUL);
					break;
				}
				case 1:
				{
					// yAxesSeries[1].setSeriesColor(CLR_FAILED);
					barChart.setYAxisSeriesColour(1, CLR_FAILED);
					break;
				}
				case 2:
				{
					// yAxesSeries[2].setSeriesColor(CLR_IGNORED);
					barChart.setYAxisSeriesColour(2, CLR_IGNORED);
					break;
				}
				case 3:
				{
					// yAxesSeries[3].setSeriesColor(CLR_NOT_PLAYED);
					barChart.setYAxisSeriesColour(3, CLR_NOT_PLAYED);
					break;
				}
				case 4:
				{
					// yAxesSeries[4].setSeriesColor(CLR_TIMEDOUT);
					barChart.setYAxisSeriesColour(4, CLR_TIMEDOUT);
					break;
				}
			}
		}
	}

	public static void formatHorizontalBarChartWithTimedOut(final AppHorizontalBarChart barChart)
	{
		setAreaColor(barChart);
		final YAxisSeries[] yAxesSeries = (YAxisSeries[]) barChart.getYAxisSeries();
		for (int i = 0; i < yAxesSeries.length; i++)
		{
			switch (i)
			{
				case 0:
				{
					// yAxesSeries[0].setSeriesColor(CLR_SUCCESSFUL);
					barChart.setYAxisSeriesColour(0, CLR_SUCCESSFUL);
					break;
				}
				case 1:
				{
					// yAxesSeries[1].setSeriesColor(CLR_FAILED);
					barChart.setYAxisSeriesColour(1, CLR_FAILED);
					break;
				}
				case 2:
				{
					// yAxesSeries[2].setSeriesColor(CLR_TIMEDOUT);
					barChart.setYAxisSeriesColour(2, CLR_TIMEDOUT);
					break;
				}
				case 3:
				{
					// yAxesSeries[3].setSeriesColor(CLR_NOT_PLAYED);
					barChart.setYAxisSeriesColour(3, CLR_NOT_PLAYED);
					break;
				}
				case 4:
				{
					// yAxesSeries[4].setSeriesColor(CLR_IGNORED);
					barChart.setYAxisSeriesColour(4, CLR_IGNORED);
					break;
				}
			}
		}
	}

	public static void formatSimpleChart(final AppSimpleChart barChart)
	{
		setAreaColor(barChart);
		final YAxisSeries[] yAxesSeries = (YAxisSeries[]) barChart.getYAxisSeries();
		for (int i = 0; i < yAxesSeries.length; i++)
		{
			switch (i)
			{
				case 0:
				{
					// yAxesSeries[0].setSeriesColor(CLR_SUCCESSFUL);
					barChart.setYAxisSeriesColour(0, CLR_SUCCESSFUL);
					break;
				}
				case 1:
				{
					// yAxesSeries[1].setSeriesColor(CLR_FAILED);
					barChart.setYAxisSeriesColour(1, CLR_FAILED);
					break;
				}
				case 2:
				{
					// yAxesSeries[2].setSeriesColor(CLR_IGNORED);
					barChart.setYAxisSeriesColour(2, CLR_IGNORED);
					break;
				}
				case 3:
				{
					// yAxesSeries[3].setSeriesColor(CLR_NOT_PLAYED);
					barChart.setYAxisSeriesColour(3, CLR_NOT_PLAYED);
					break;
				}
				case 4:
				{
					// yAxesSeries[4].setSeriesColor(CLR_TIMEDOUT);
					barChart.setYAxisSeriesColour(4, CLR_TIMEDOUT);
					break;
				}
			}
		}
	}

	public static void formatSimpleChartWithTimedOut(final AppSimpleChart barChart)
	{
		setAreaColor(barChart);
		final YAxisSeries[] yAxesSeries = (YAxisSeries[]) barChart.getYAxisSeries();
		for (int i = 0; i < yAxesSeries.length; i++)
		{
			switch (i)
			{
				case 0:
				{
					// yAxesSeries[0].setSeriesColor(CLR_SUCCESSFUL);
					barChart.setYAxisSeriesColour(0, CLR_SUCCESSFUL);
					break;
				}
				case 1:
				{
					// yAxesSeries[1].setSeriesColor(CLR_FAILED);
					barChart.setYAxisSeriesColour(1, CLR_FAILED);
					break;
				}
				case 2:
				{
					// yAxesSeries[2].setSeriesColor(CLR_TIMEDOUT);
					barChart.setYAxisSeriesColour(2, CLR_TIMEDOUT);
					break;
				}
				case 3:
				{
					// yAxesSeries[3].setSeriesColor(CLR_NOT_PLAYED);
					barChart.setYAxisSeriesColour(3, CLR_NOT_PLAYED);
					break;
				}
				case 4:
				{
					// yAxesSeries[4].setSeriesColor(CLR_IGNORED);
					barChart.setYAxisSeriesColour(4, CLR_IGNORED);
					break;
				}
			}
		}
	}

}
