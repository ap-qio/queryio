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
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.LineAttributes;
import com.queryio.common.exporter.dstruct.Rectangle;

/**
 * 
 * @author Exceed Consultancy Services
 */
public class AppHorizontalLineChart extends AppHorizontalSimpleChart
{
	private boolean marker = false;

	public AppHorizontalLineChart(final UserInterface userInterface, final int nodeType, final int productID)
	{
		super(userInterface, nodeType, productID);
		this.bShowAlternateBackground = true;
	}

	public final boolean isMarker()
	{
		return this.marker;
	}

	public void showMarker(final boolean marker)
	{
		this.marker = marker;
	}

	public void drawSeries(final UserInterface graphics, final int originY)
	{
		final Series[] yAxisSeries = this.getYAxisSeries();
		final int lenYAxisSeries = yAxisSeries.length;
		if (lenYAxisSeries > 0)
		{
			for (int i = 0; i < yAxisSeries.length; i++)
			{
				this.drawSeries(graphics, i, (YAxisSeries) yAxisSeries[i], true, 0, originY);
			}
		}
	}

	public void drawSeries(final UserInterface graphics)
	{
		final Series[] yAxisSeries = this.getYAxisSeries();
		final int lenYAxisSeries = yAxisSeries.length;
		if (lenYAxisSeries > 0)
		{
			for (int i = 0; i < yAxisSeries.length; i++)
			{
				this.drawSeries(graphics, i, (YAxisSeries) yAxisSeries[i], false, this.yStart, this.yStart);
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
	private void drawSeries(final UserInterface graphics, final int iYAxisSeriesIndex, 
		final YAxisSeries yAxisSeries, boolean renderAll, final int startY, final int maxYLimit)
	{
		if (!this.shouldDrawSeries(iYAxisSeriesIndex))
		{
			return;
		}
		
		Color oldForeground = graphics.getForeground();
		graphics.setBackground(getColorForIndex(iYAxisSeriesIndex, this.chartProperties));
		graphics.setForeground(getColorForIndex(iYAxisSeriesIndex, this.chartProperties));
		
		int iCurrentCount = 0;
		XAxisSeries xAxisSeries = this.getXAxisSeries();
		iCurrentCount = xAxisSeries.getCurrentCount();


		int yValue;
		int xCor;
		int yCor;
		int prevXCor = 0;
		int prevYCor = 0;
		int prevYValue = Integer.MIN_VALUE;

		final int yStartCor = startY;
		final int xConstant = xStart - shiftXAxisAbove;
		for (int i = 0; i < iCurrentCount; i++)
		{
			yValue = yAxisSeries.getValue(i);
			xCor = this.resolveYCorordinate(yValue);
			yCor = yStartCor + ((i + 1) * this.eachDataWidth) - this.eachDataWidth/2;
			if (!renderAll && ((yCor + 1) > this.plotRect.height))
			{
				break;
			}
			
			if (this.bShowToolTip)
			{
				final Rectangle marker = new Rectangle(xConstant + xCor - 3, yCor - 3, 6, 6);
				String formattedXValue = getXAxisSeries().getFormattedValue(i);
				this.addMarkerObject(new MarkerObject(marker, String.valueOf(yValue), formattedXValue, i, 
					iYAxisSeriesIndex));
			}
			
			if (Integer.MIN_VALUE != yValue)
			{
				if (this.isMarker())
				{
					graphics.drawRectangle(xConstant + xCor - 2, yCor - 2, 4, 4);
					graphics.fillRectangle(xConstant + xCor - 2, yCor - 2, 4, 4);
				}
				if (Integer.MIN_VALUE != prevYValue)
				{
					
					final LineAttributes oldLine = graphics.getLineStyle();
					graphics.setLineStyle(new LineAttributes(2.25f, LineAttributes.CAP_ROUND, LineAttributes.JOIN_ROUND, LineAttributes.LINE_STYLE_SOLID));
					graphics.drawLine(xConstant + prevXCor, prevYCor, xConstant + xCor, yCor);
					graphics.setLineStyle(oldLine);
				}
			}
			prevXCor = xCor;
			prevYCor = yCor;
			prevYValue = yValue;
		}
		graphics.setForeground(oldForeground);
	}
}
