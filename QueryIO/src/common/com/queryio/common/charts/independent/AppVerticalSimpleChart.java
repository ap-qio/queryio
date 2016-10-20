/*
 * @(#) AppVerticalSimpleChart.java Feb 4, 2005 Copyright (C) 2002 Exceed
 * Consultancy Services. All Rights Reserved. This software is proprietary
 * information of Exceed Consultancy Services and constitutes valuable trade
 * secrets of Exceed Consultancy Services. You shall not disclose this
 * information and shall use it only in accordance with the terms of License.
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.charts.independent;

import com.queryio.common.charts.components.Title;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.charts.series.XAxisIntegerSeries;
import com.queryio.common.charts.series.XAxisLongSeries;
import com.queryio.common.charts.series.XAxisSeries;
import com.queryio.common.charts.series.XAxisTimeSeries;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Font;
import com.queryio.common.exporter.dstruct.LineAttributes;
import com.queryio.common.exporter.dstruct.Rectangle;

/**
 * @author Exceed Consultancy Services
 */
public abstract class AppVerticalSimpleChart extends AppSimpleChart
{
	protected long xEndValue;
	private int height;

	public AppVerticalSimpleChart(final UserInterface userInterface, final int nodeType, final int productID)
	{
		super(userInterface, false, nodeType, productID);
	}

	protected void configureOrientationOfXandYAxisTitles()
	{
		final Title axisTitle = this.chartProperties.getYAxisTitle();
		if (axisTitle != null)
		{
			axisTitle.setOrientation(Title.ORIENTATION_VERTICAL);
		}
	}

	public void drawArea(final UserInterface graphics)
	{
		final Color background = graphics.getBackground();
		final Color foreground = graphics.getForeground();
		final Rectangle oldClip = graphics.getClipping();

		this.calculateDimensions(graphics);

		if (this.chartProperties.getShowXAxisTitle().booleanValue())
		{
			graphics.setClipping(this.xAxisTitleRect);
			final Title axisTitle = this.chartProperties.getXAxisTitle();
			axisTitle.setBackgroundColor(this.chartProperties.getXAxisTitleBackgroundColour());
			axisTitle.setTextColor(this.chartProperties.getXAxisTitleTextColour());
			axisTitle.draw(graphics, 0);
		}

		final Title axisTitle = this.chartProperties.getYAxisTitle();
		if (this.chartProperties.getShowYAxisTitle().booleanValue())
		{
			graphics.setClipping(this.yAxisTitleRect);
			axisTitle.setBackgroundColor(this.chartProperties.getYAxisTitleBackgroundColour());
			axisTitle.setTextColor(this.chartProperties.getYAxisTitleTextColour());
			axisTitle.draw(graphics, -ANGLE);
		}

		graphics.setBackground(background);
		graphics.setForeground(foreground);

		graphics.setClipping(this.plotRect);
		final Font defaultTickFont = this.chartProperties.getTickFont();
		graphics.setFont(defaultTickFont);

		final int minMax[] = this.getYAxisMinMaxValues();
		int startYval = minMax[0] + (minMax[0] == 0 ? 0 : -1);
		int endYval = minMax[1] + 1;
		final int factor = (endYval - startYval > 5 ? 5 : 1);

		if (startYval % factor != 0)
		{
			startYval -= (startYval % factor);
		}

		if (endYval % factor != 0)
		{
			endYval += (factor - endYval % factor);
		}

		int iMaxYValue = factor * ((endYval / factor) + 1);
		if (this.isLogarithmic())
		{
			iMaxYValue = (int) Math.pow(10, (int)this.getLogValue(iMaxYValue) + 1);
		}

		this.shiftYaxisToRHS = Math.max(graphics.stringExtent(ChartConstants.format(startYval)).x, graphics
				.stringExtent(ChartConstants.format(iMaxYValue)).x) + INSET_FOR_TICK_VALUE_FROM_TICK_MARK + 1;
		if (this instanceof AppLineChart)
		{
			this.shiftXAxisAbove = this.getMaxHeight(graphics, defaultTickFont) + INSET_FOR_TICK_VALUE_FROM_TICK_MARK;
		}
		else if (this instanceof AppAbstractBarChart)
		{
			this.shiftXAxisAbove = this.getMaxHeight(graphics, defaultTickFont) + INSET_FOR_TICK_VALUE_FROM_AXIS;
		}

		// draw X-axis
		this.drawXAxis(graphics);

		// draw Y-axis
		this.drawYAxis(graphics);

		// draw Series values
		this.drawSeries(graphics);

		graphics.setClipping(oldClip);

		this.clearAll();

	}
	
	protected void drawXAxis(final UserInterface graphics)
	{
		final Color background = graphics.getBackground();
		final Color foreground = graphics.getForeground();
		this.fillArea(graphics);
		// Color background = graphics.getBackground();
		// graphics.setBackground(chartProperties.getTickBackgroundColour());
		final XAxisSeries series = (XAxisSeries) this.getXAxisSeries();
		graphics.setFont(this.chartProperties.getTickFont());
		graphics.setForeground(ChartConstants.COLOR_DARK_GRAY);

		final int startXpx = this.plotRect.x + this.shiftYaxisToRHS;
		final int endXpx = startXpx + this.plotRect.width - this.shiftYaxisToRHS;
		final int yCor = this.plotRect.y + this.plotRect.height - this.shiftXAxisAbove;

		// draw X-Axis line
		graphics.drawLine(startXpx, yCor, endXpx, yCor);

		long startXval;
		long endXval;
		boolean minMaxSame;
		if (series.getValuesType() == XAxisSeries.LONGVALUES ||
			series.getValuesType() == XAxisSeries.TIMEVALUES)
		{
			XAxisLongSeries longSeries = (XAxisLongSeries)series;
			startXval = (this.startValueSet ? this.xStartValue : (Long.MAX_VALUE == longSeries.getMinValue() ? 
				0L : (longSeries.getMinValue() + (longSeries.getMinValue() == 0 ? 0 : -1))));
			endXval = longSeries.getMaxValue() + 1;
			minMaxSame = longSeries.getMinValue() == longSeries.getMaxValue();
		}
		else
		{
			XAxisIntegerSeries integerSeries = (XAxisIntegerSeries)series;
			startXval = (this.startValueSet ? this.xStartValue : (Integer.MAX_VALUE == integerSeries.getMinValue() ? 
				0L : (integerSeries.getMinValue() + (integerSeries.getMinValue() == 0 ? 0 : -1))));
			endXval = integerSeries.getMaxValue() + 1;
			minMaxSame = integerSeries.getMinValue() == integerSeries.getMaxValue();
		}
		if (minMaxSame) // If all values in X Axis are same.
		{
			if (series.getValuesType() == XAxisSeries.INTVALUES)
			{
				// do nothing. its already set
			}
			else
			{
				// It will be one minute less than the time value provided by user.
				startXval = Math.max(0L, startXval - 60 * 1000);
				// It will be one minute more than the time value provided by user.
				endXval = startXval + 60 * 1000;
			}
		}
		else if (endXval < startXval)
		{
			endXval = startXval + 60 * 1000;
		}

		final int maxStringWidth = graphics.stringExtent(series.getFormattedMaxValue()).x;
		this.iMaxStringWidth = maxStringWidth;
		int tickWidth = 2 * maxStringWidth;
		final int availablePxs = endXpx - startXpx - maxStringWidth / 2;

		this.iAvailablePxs = availablePxs;
		int tickCount = availablePxs / tickWidth;
		if (tickCount == 0)
		{
			tickCount = 1;
		}

		float tickInterval = (float) (endXval - startXval) / tickCount;
		if (tickInterval == 0)
		{
			tickInterval = 1.0f;
		}
		else if (series.getValuesType() == XAxisSeries.INTVALUES)
		{
			if ((float)Math.round(tickInterval) != tickInterval)
			{
				tickInterval = Math.round(tickInterval) + 1;
			}
		}
		tickCount = Math.round((endXval - startXval) / tickInterval) + 1;
		tickWidth = Math.max(tickWidth, ((endXpx - 1) - startXpx) / (tickCount - 1));
		this.eachDataWidth = (int) Math.ceil((tickWidth * tickCount - tickWidth / 2) / (series.getCurrentCount() + 1));

		this.xStart = startXpx;
		this.xStartValue = startXval;
		this.xSlope = tickWidth / tickInterval;
		this.xEndValue = endXval;

		// draw Ticks
		int currXPos = startXpx;
		String value = null;
		float drawX;
		float widthX;
		long tmpStartVal = startXval;
		if ((this instanceof AppLineChart) && !((AppLineChart) this).isFitToScale() && (this.benchmarkTime > -1))
		{
			tmpStartVal = this.benchmarkTime;
		}
		for (int i = 0; i < tickCount; i++)
		{
			if (this.isXAxisGrid())
			{
				graphics.setBackground(background);
				graphics.setForeground(ChartConstants.GRID_COLOR);
				final LineAttributes oldLine = graphics.getLineStyle();
				graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND, LineAttributes.LINE_STYLE_DOT));
				graphics.drawLine(currXPos, this.plotRect.y, currXPos, yCor);
				graphics.setLineStyle(oldLine);
				graphics.setLineStyle(oldLine);
			}
			
			if (series.getValuesType() == XAxisSeries.TIMEVALUES)
			{
				value = ((XAxisTimeSeries) series).getFormattedValue(tmpStartVal, 
					startXval + i * (long) tickInterval, endXval);
			}
			else if (series.getValuesType() == XAxisSeries.INTVALUES)
			{
				currXPos = resolveXCorordinate(startXval + i * Math.round(tickInterval));
				value = XAxisSeries.getFormattedValueOf(startXval + i * Math.round(tickInterval));
			}
			else
			{
				value = XAxisSeries.getFormattedValueOf(startXval + i * (long) tickInterval);
			}
			if (i != 0)
			{
				graphics.setBackground(this.chartProperties.getTickBackgroundColour());
				graphics.setForeground(this.chartProperties.getTickTextColour());
				graphics.drawLine(currXPos, yCor, currXPos, yCor + TICK_MARK_LENGTH);
			}
			widthX = graphics.stringExtent(value).x;
			drawX = currXPos - widthX * 0.5f;
			if (i == tickCount - 1)
			{
				drawX = endXpx - widthX;
			}
			if (drawX < this.plotRect.x)
			{
				drawX = this.plotRect.x;
			}
			if ((i != 0) || !(series.getValuesType() == XAxisSeries.TIMEVALUES))
			{
				// TODO: could cause problem due to type-casting !!!
				graphics.drawString(value, (int) drawX, yCor + INSET_FOR_TICK_VALUE_FROM_TICK_MARK, true);
			}

			currXPos += tickWidth;
		}

		graphics.setForeground(ChartConstants.COLOR_DARK_GRAY);
		graphics.setBackground(background);
		// draw surrounding line
		graphics.drawLine(startXpx, this.plotRect.y, endXpx, this.plotRect.y);
		graphics.setForeground(foreground);
	}

	protected final void drawYAxis(final UserInterface graphics)
	{
		final Color background = graphics.getBackground();
		final Color foreground = graphics.getForeground();

		final int minMax[] = this.getYAxisMinMaxValues();
		// Color background = graphics.getBackground();
		// graphics.setBackground(chartProperties.getTickBackgroundColour());
		graphics.setFont(this.chartProperties.getTickFont());

		final int startYpx = this.plotRect.y + this.plotRect.height - this.shiftXAxisAbove - this.iShiftYAxisAbove;
		final int endYpx = this.plotRect.y;
		final int xCor = this.plotRect.x + this.shiftYaxisToRHS;
		final int availablePxs = startYpx - endYpx - this.shiftXAxisAbove / 6;

		// draw Y-Axis line
		final LineAttributes oldLine = graphics.getLineStyle();
		graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
		graphics.setForeground(ChartConstants.COLOR_DARK_GRAY);
		graphics.drawLine(xCor, startYpx, xCor, endYpx);
		graphics.setLineStyle(oldLine);

		int startYval;
		int endYval;
		int factor = 1;
		final boolean logarithmic = this.isLogarithmic();
		if (!logarithmic)
		{
			startYval = this.bMinMaxYValueSet ? minMax[0] : (minMax[0] + (minMax[0] == 0 ? 0 : -1));
			endYval = this.bMinMaxYValueSet ? minMax[1] : (minMax[1] + 1);
			factor = (endYval - startYval > 5 ? 5 : 1);
			if (!this.bMinMaxYValueSet)
			{
				if (startYval % factor != 0)
				{
					startYval -= (startYval % factor);
				}

				if (endYval % factor != 0)
				{
					endYval += (factor - endYval % factor);
				}
			}
		}
		else
		{
			startYval = 0;
			endYval = (int) this.getLogValue(this.getEndYVal(minMax[1]));
		}

		int tickCount;
		/*
		 * the count of ticks that can be shown on Y axis with provided drawing
		 * area
		 */
		tickCount = availablePxs / this.shiftXAxisAbove;
		if (tickCount <= 0)
		{
			tickCount = 1;
		}
		if (this.bMinMaxYValueSet)
		{
			/*
			 * find the tickCount which will divide (endYval - startYval)
			 * exactly
			 */
			for (int i = tickCount; i >= 1; i--)
			{
				if ((endYval - startYval) % i == 0)
				{
					tickCount = i;
					break;
				}
			}
		}

		/*
		 * distance between two ticks i.e tick interval. Calculate from (max -
		 * min) on y axis)
		 */
		int tickInterval = Math.abs(endYval - startYval) / tickCount;
		if (tickInterval == 0)
		{
			tickInterval = 1;
		}
		if (!this.bMinMaxYValueSet && (tickInterval % factor != 0))
		{
			// convert tick interval into multiple of factor
			tickInterval += (factor - tickInterval % factor);
		}
		if (!this.bMinMaxYValueSet)
		{
			if (!logarithmic)
			{
				tickCount = Math.round(Math.abs(endYval - startYval) / tickInterval) + 1;
			}
			else
			{
				tickCount = String.valueOf(minMax[1]).length();
			}
		}

		this.height = availablePxs / tickCount;
		this.yStart = startYpx;
		this.yStartValue = startYval;
		this.ySlope = (float) this.height / tickInterval;
		int y = startYpx;

		String value = null;
		int drawY;
		for (int i = 0; i < tickCount + 1; i++)
		{
			if (this.isYAxisGrid() && (i != 0))
			{
				graphics.setBackground(background);
				graphics.setForeground(ChartConstants.GRID_COLOR);
				final LineAttributes oldLineAttr = graphics.getLineStyle();
				graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND, LineAttributes.LINE_STYLE_DOT));
				graphics.drawLine(this.plotRect.x + this.shiftYaxisToRHS, y, this.plotRect.x + this.plotRect.width, y);
				graphics.setLineStyle(oldLineAttr);
			}

			if (!logarithmic)
			{
				value = ChartConstants.format(startYval + i * tickInterval);
			}
			else
			{
				value = ChartConstants.format((int) Math.pow(10, i));
			}
			graphics.setBackground(this.chartProperties.getTickBackgroundColour());
			graphics.setForeground(this.chartProperties.getTickTextColour());
			if (i != 0)
			{
				graphics.drawLine(xCor - TICK_MARK_LENGTH, y, xCor, y);
			}
			drawY = y - this.shiftXAxisAbove / 4;
			graphics.drawString(value, this.plotRect.x + this.shiftYaxisToRHS - graphics.stringExtent(value).x - TICK_MARK_LENGTH - 1, drawY, true);
			y -= this.height;
		}

		graphics.setBackground(background);
//		graphics.setForeground(ChartConstants.COLOR_DARK_GRAY);
//		// draw surrounding line
//		graphics.drawLine(this.plotRect.x + this.plotRect.width - 1, startYpx, this.plotRect.x + this.plotRect.width
//				- 1, endYpx);
		graphics.setForeground(foreground);
	}

	private int getEndYVal(final int i)
	{
		return (int) Math.pow(10, String.valueOf(i).length());
	}

	protected int resolveXCorordinate(final long value)
	{
		return (int) (this.xStart + (value - this.xStartValue) * this.xSlope);
	}

	protected int resolveYCorordinate(final long value)
	{
		return (int) (this.yStart - (this.isLogarithmic() ? this.getLogValue(value) : value - this.yStartValue)
				* this.ySlope);
	}

	/**
	 * Use this method to set initial start time which will be used for
	 * continuous chart.
	 * 
	 * @param benchmarkTime
	 */
	public void setInitialStartTime(final long benchmarkTime)
	{
		this.benchmarkTime = benchmarkTime;
	}

}
