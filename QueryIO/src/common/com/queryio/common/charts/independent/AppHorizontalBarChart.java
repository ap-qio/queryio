/*
 * @(#)  AppHorizontalBarChart.java Feb 7, 2005
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
import com.queryio.common.charts.series.XAxisSeries;
import com.queryio.common.charts.series.YAxisSeries;
import com.queryio.common.exporter.dstruct.Rectangle;

/**
 * 
 * @author Exceed Consultancy Services
 */
public class AppHorizontalBarChart extends AppHorizontalSimpleChart
{
	int width;
	int totalWidth;

	public AppHorizontalBarChart(final UserInterface userInterface, final int nodeType, final int productID)
	{
		super(userInterface, nodeType, productID);
		this.bShowAlternateBackground = true;
	}

	public void drawSeries(final UserInterface graphics, final int originY)
	{
		final Series[] yAxisSeries = this.getYAxisSeries();
		final int lenYAxisSeries = yAxisSeries.length;
		if (lenYAxisSeries > 0)
		{
			// width = Math.max(1, (eachDataWidth - INSET_FOR_BAR_ON_ONESIDE *
			// 2) / yAxisSeries.length);
			this.width = Math.max(1, (this.eachDataWidth / (lenYAxisSeries + 2)));
			int each = 0;
			for (int i = 0; i < yAxisSeries.length; i++)
			{
				this.totalWidth = (lenYAxisSeries - each) * this.width;
				this.drawSeries(graphics, i, (YAxisSeries) yAxisSeries[i], (each + 1) * this.width, true, 0, originY);
				each++;
			}
		}
	}

	public void drawSeries(final UserInterface graphics)
	{
		final Series[] yAxisSeries = this.getYAxisSeries();
		final int lenYAxisSeries = yAxisSeries.length;
		if (lenYAxisSeries > 0)
		{
			// width = Math.max(1, (eachDataWidth - INSET_FOR_BAR_ON_ONESIDE *
			// 2) / yAxisSeries.length);
			this.width = Math.max(1, (this.eachDataWidth / (lenYAxisSeries + 2)));
			int each = 0;
			for (int i = 0; i < yAxisSeries.length; i++)
			{
				this.totalWidth = (lenYAxisSeries - each) * this.width;
				this.drawSeries(graphics, i, (YAxisSeries) yAxisSeries[i], (each + 1) * this.width, false, this.yStart,
						this.yStart);
				each++;
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
			final int shift, boolean renderAll, final int startY, final int maxYLimit)
	{
		if (!this.shouldDrawSeries(iYAxisSeriesIndex))
		{
			return;
		}
		int iCurrentCount = 0;
		XAxisSeries xAxisSeries = this.getXAxisSeries();
		iCurrentCount = xAxisSeries.getCurrentCount();


		int yValue;
		int xCor;
		int yCor;

		final int yStartCor = startY;
		// - shiftXAxisAbove;
		// - shiftXAxisAbove;
		for (int i = 0; i < iCurrentCount; i++)
		{

			yValue = yAxisSeries.getValue(i);
			xCor = this.resolveYCorordinate(yValue);

			yCor = yStartCor + i * this.eachDataWidth + shift;// + width;
			if (!renderAll && ((yCor + 1 + this.totalWidth) > this.plotRect.height))
			{
				break;
			}
			if (((Integer.MIN_VALUE != yValue) && (yValue > 0)) && (yCor > maxYLimit))
			{
				if (this.bShowToolTip)
				{
					final Rectangle marker = new Rectangle(this.xStart + 1, yCor + 2, xCor - yStartCor, this.width);
					final String xValue = xAxisSeries.getFormattedValue(i);
					final String formattedYValue = String.valueOf(yValue);
					this.addMarkerObject(new MarkerObject(marker, formattedYValue, xValue, i, iYAxisSeriesIndex));
				}
				graphics.setBackground(getColorForIndex(iYAxisSeriesIndex, this.chartProperties));
				graphics.fillRectangle(this.xStart + 1, yCor + 2, xCor - yStartCor, this.width);
			}
		}
	}
}
