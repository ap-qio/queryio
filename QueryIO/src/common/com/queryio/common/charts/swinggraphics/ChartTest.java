package com.queryio.common.charts.swinggraphics;

import java.util.Random;

import javax.swing.JFrame;

import com.queryio.common.IProductConstants;
import com.queryio.common.charts.independent.AppChart;
import com.queryio.common.charts.independent.AppVerticalBarChart;
import com.queryio.common.charts.properties.ChartPropertiesManager;
import com.queryio.common.charts.series.Series;
import com.queryio.common.charts.series.XAxisLongSeries;
import com.queryio.common.charts.series.XAxisStringSeries;
import com.queryio.common.charts.series.YAxisSeries;
import com.queryio.common.charts.util.UIGraphicsFactory;
import com.queryio.common.exporter.dstruct.Color;

/*
 * @(#)  ChartTest.java Feb 7, 2005
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

/**
 * 
 * @author Exceed Consultancy Services
 */
public class ChartTest
{
	private static final Random rndGenerator = new Random();

	public static void main(final String[] args)
	{
		final ChartTest chartTest = new ChartTest();
		final AppChartPanel chartPanel = new AppChartPanel();

		final AppChart chartObject = chartTest.getChartObject();
		chartPanel.setChartObject(chartObject);

		final JFrame frame = new JFrame("Swing ChartTest"); //$NON-NLS-1$
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(chartPanel);
		frame.setSize(300, 300);
		frame.setVisible(true);
	}

	public AppChart getChartObject()
	{
		UIGraphicsFactory.setUserInterfaceClassLoader(this.getClass().getClassLoader());
		// AppLineChart lineChart = new
		// AppLineChart(UIGraphicsFactory.getUserInterface(UIGraphicsFactory.USER_INTERFACE_SWING));

		final AppVerticalBarChart lineChart = new AppVerticalBarChart(UIGraphicsFactory
				.getUserInterface(IProductConstants.USER_INTERFACE_SWING), ChartPropertiesManager.TYPE_RUNTIME_CHARTS,
				ChartPropertiesManager.TYPE_RUNTIME_CHARTS);

		// configure chart object
		lineChart.setChartTitle("Line Chart"); //$NON-NLS-1$
		lineChart.setXAxisTitle("Why X-Axis?"); //$NON-NLS-1$
		lineChart.setYAxisTitle("Why Y-Axis?"); //$NON-NLS-1$
		lineChart.showTitle(true);
		lineChart.showLegend(true);
		lineChart.showXAxisTitle(true);
		lineChart.showYAxisTitle(true);
		lineChart.showYAxisGrids(true);
		lineChart.showXAxisGrids(true);
		lineChart.setAreaColor(new Color(0xff, 0xff, 0xff));
		lineChart.setBackgroundColor(new Color((int) (0xE0 * 0.7), (int) (0xE0 * 0.7), (int) (0xE0 * 0.7)));

		lineChart.initializeSeries(3, 10);

		configureSeries((XAxisLongSeries) lineChart.getXAxisSeries(), (YAxisSeries[]) lineChart.getYAxisSeries(),
				lineChart);

		return lineChart;
	}

	private static void configureSeries(final Series series, final YAxisSeries[] yAxesSeries,
			final AppVerticalBarChart lineChart)
	{
		final long ts = 1074742401640L;

		XAxisLongSeries longSeries = null;
		XAxisStringSeries stringSeries = null;

		if (series instanceof XAxisLongSeries)
		{
			longSeries = (XAxisLongSeries) series;
		}
		else
		{
			stringSeries = (XAxisStringSeries) series;
		}

		for (int i = 0; i < yAxesSeries.length; i++)
		{
			// yAxesSeries[i].setText("Series " + i); //$NON-NLS-1$
			// //$IGN_String_concatenation_in_loop$
			lineChart.setYAxisSeriesText(i, "Series " + i); //$NON-NLS-1$
			// yAxesSeries[i].setSeriesColor(new Color(getNextInt(),
			// getNextInt(), getNextInt()));
			// //$IGN_Avoid_object_instantiation_in_loops$
			lineChart.setYAxisSeriesColour(i, new Color(getNextInt(), getNextInt(), getNextInt())); // $IGN_Avoid_object_instantiation_in_loops$
		}

		// configure series's data
		int value = 0;
		for (int i = 0; i < series.getCapacity(); i++)
		{
			if (longSeries != null)
			{
				longSeries.setValue(i, ts + i * 3600 + i * 7 * 100000);
			}
			else
			{
				stringSeries.setValue(i, "Abcd" + String.valueOf(200 * i)); //$NON-NLS-1$ //$IGN_String_concatenation_in_loop$
			}

			for (int j = 0; j < yAxesSeries.length; j++)
			{
				value = ((i + 1) * (j + 1)) % 9;
				yAxesSeries[j].setValue(i, value);
			}
		}
	}

	private static int getNextInt()
	{
		return rndGenerator.nextInt(256);
	}

}
