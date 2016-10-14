/*
 * @(#)  AppVeticalBarChart.java Feb 7, 2005
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

import com.queryio.common.charts.components.MarkerObject;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.charts.series.Series;
import com.queryio.common.charts.series.YAxisSeries;
import com.queryio.common.exporter.dstruct.Rectangle;

/**
 * 
 * @author Exceed Consultancy Services
 */
public final class AppVerticalBarChart extends AppAbstractBarChart
{
	int width;

	public AppVerticalBarChart(final UserInterface userInterface, final int nodeType, final int productID)
	{
		super(userInterface, nodeType, productID);
	}

	public void drawSeries(final UserInterface graphics)
	{
		final Series[] yAxisSeries = this.getYAxisSeries();
		int lenYAxisSeries = 0;
		for (int i = 0; i < yAxisSeries.length; i++)
		{
			if (this.shouldDrawSeries(i))
			{
				lenYAxisSeries++;
			}
		}
		if (lenYAxisSeries > 0)
		{
			this.width = Math.max(1, (this.eachDataWidth - this.insetForBarOnOneside * 2) / lenYAxisSeries);
			int each = 0;
			for (int i = 0; i < yAxisSeries.length; i++)
			{
				if (this.shouldDrawSeries(i))
				{
					this.drawSeries(graphics, i, (YAxisSeries) yAxisSeries[i], each * this.width
							+ this.insetForBarOnOneside);
					each++;
				}
			}
		}
	}

	/**
	 * Original drawSeries method - Amol
	 * 
	 * @param graphics
	 * @param yAxisSeries
	 * @param shift
	 */
	private void drawSeries(final UserInterface graphics, final int iYAxisSeriesIndex, final YAxisSeries yAxisSeries,
			final int shift)
	{
		int iCurrentCount = this.getXAxisSeries().getCurrentCount();

		int yValue;
		int xCor;
		int yCor;

		for (int i = 0; i < iCurrentCount; i++)
		{
			yValue = yAxisSeries.getValue(i);
			xCor = this.xStart + i * this.eachDataWidth + shift;
			yCor = this.resolveYCorordinate(yValue);
			if ((Integer.MIN_VALUE != yValue) && (yCor < this.yStart))
			{
				if (this.bShowToolTip)
				{
					final Rectangle marker = new Rectangle(xCor + 1, yCor, this.width, this.yStart - yCor);
					final String xValue = this.getXAxisSeries().getFormattedValue(i);
					final String formattedYValue = String.valueOf(yValue);
					this.addMarkerObject(new MarkerObject(marker, formattedYValue, xValue, i, iYAxisSeriesIndex));
				}
				graphics.setBackground(getColorForIndex(iYAxisSeriesIndex, this.chartProperties));
				graphics.fillRectangle(xCor + 1, yCor, this.width, this.yStart - yCor);
			}
		}
	}
}
