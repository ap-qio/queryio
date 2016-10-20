/*
 * @(#)  AppHorizontalStackChart.java Feb 7, 2005
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
public class AppHorizontalStackChart extends AppHorizontalSimpleChart
{
	public AppHorizontalStackChart(final UserInterface userInterface, final int nodeType, final int productID)
	{
		super(userInterface, nodeType, productID);
		this.bShowAlternateBackground = true;
	}

	public void drawSeries(final UserInterface graphics, final int originY)
	{
		this.drawSeries(graphics, true, 0);
	}

	public void drawSeries(final UserInterface graphics)
	{
		this.drawSeries(graphics, false, this.yStart);
	}

	private void drawSeries(final UserInterface graphics, boolean renderAll, final int startY)
	{
		final Series[] yAxisSeries = this.getYAxisSeries();
		if (yAxisSeries.length > 0)
		{
			int yCor;
			int xCor;
			int height;
			int yValue;
			int prevXCor;
			int width;
			YAxisSeries ySeries;

			int iCurrentCount = this.getXAxisSeries().getCurrentCount();

			width = Math.max(1, (this.eachDataWidth / 3));
			for (int i = 0; i < iCurrentCount; i++)
			{
				yCor = this.yStart + (i + 1) * this.eachDataWidth;
				yCor = startY + (i * this.eachDataWidth) + width;
				// + shiftXAxisAbove;
				prevXCor = this.xStart + 1;
				for (int j = 0; j < yAxisSeries.length; j++)
				{
					if (!this.shouldDrawSeries(j))
					{
						continue;
					}
					ySeries = (YAxisSeries) yAxisSeries[j];
					yValue = ySeries.getValue(i);
					xCor = this.resolveYCorordinate(yValue);
					if (!renderAll && ((yCor + 2 * width) > this.plotRect.height))
					{
						break;
					}
					if ((Integer.MIN_VALUE != yValue) && (yValue != 0))
					{
						height = xCor - this.yStart;
						if (this.bShowToolTip)
						{
							final Rectangle marker = new Rectangle(prevXCor, yCor + 1, height, width);
							final String xValue = this.getXAxisSeries().getFormattedValue(i);
							final String formattedYValue = String.valueOf(yValue);
							this.addMarkerObject(new MarkerObject(marker, formattedYValue, xValue, i, j));
						}
						graphics.setBackground(getColorForIndex(j, this.chartProperties));
						graphics.fillRectangle(prevXCor, yCor + 1, height, width);
						prevXCor += height;
					}
				}
			}

		}
	}

	public int[] getYAxisMinMaxValues()
	{
		if (this.bMinMaxYValueSet)
		{
			return super.getYAxisMinMaxValues();
		}

		final Series[] yAxisSeries = this.getYAxisSeries();
		YAxisSeries series = null;
		final int[] minMax = new int[] { Integer.MAX_VALUE, Integer.MIN_VALUE };
		for (int i = 0; i < yAxisSeries.length; i++)
		{
			if (this.shouldDrawSeries(i))
			{
				series = (YAxisSeries) yAxisSeries[i];
				if (Integer.MAX_VALUE != series.getMinValue())
				{
					minMax[0] = Math.min(minMax[0], series.getMinValue());
				}
			}
		}
		int capacity = 0;
		if (series != null)
		{
			capacity = series.getCapacity();
		}
		for (int j = 0; j < capacity; j++)
		{
			int value = Integer.MIN_VALUE;
			for (int i = 0; i < yAxisSeries.length; i++)
			{
				if (this.shouldDrawSeries(i))
				{
					series = (YAxisSeries) yAxisSeries[i];
					if (Integer.MIN_VALUE == value)
					{
						value = series.getValue(j);
					}
					else
					{
						value += series.getValue(j);
					}
				}
			}
			minMax[1] = Math.max(minMax[1], value);
		}
		if ((minMax[0] == Integer.MAX_VALUE) && (minMax[1] == Integer.MIN_VALUE))
		{
			minMax[0] = 0;
			minMax[1] = 2;
		}
		else if (minMax[0] == minMax[1])
		{
			if (minMax[0] >= 1)
			{
				minMax[0] -= 1;
			}
			minMax[1] += 1;
		}
		return minMax;
	}

}
