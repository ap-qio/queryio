/*
 * @(#)  AppLineChart.java Feb 4, 2005
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
import com.queryio.common.charts.properties.ChartPropertiesManager;
import com.queryio.common.charts.series.Series;
import com.queryio.common.charts.series.XAxisIntegerSeries;
import com.queryio.common.charts.series.XAxisLongSeries;
import com.queryio.common.charts.series.XAxisSeries;
import com.queryio.common.charts.series.XAxisTimeSeries;
import com.queryio.common.charts.series.YAxisSeries;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.LineAttributes;
import com.queryio.common.exporter.dstruct.Rectangle;

/**
 * 
 * @author Exceed Consultancy Services
 */
public final class AppLineChart extends AppVerticalSimpleChart
{
//	private boolean marker = false;
//	private boolean label = false;
	private boolean scatterPlot = false;
	private boolean isStepChart = false;

	public AppLineChart(final UserInterface userInterface, final int nodeType, final int productID)
	{
		this(userInterface, nodeType, productID, false); 
	}
	
	public AppLineChart(final UserInterface userInterface, final int nodeType, final int productID, boolean isStepChart)
	{
		super(userInterface, nodeType, productID);
		this.setFitToScale(true);
		this.isStepChart = isStepChart; 
	}

	private void setFitToScale(final boolean b)
	{
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.isFitToScale() == null))
		{
			this.chartProperties.setFitToScale(b);
		}
	}

	public boolean isFitToScale()
	{
		return this.chartProperties.isFitToScale().booleanValue();
	}

	public final boolean isMarker()
	{
		return this.chartProperties.getShowChartMarker() != null ? this.chartProperties.getShowChartMarker().booleanValue() : false;
	}

	public void showMarker(final boolean marker)
	{
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getShowChartMarker() == null))
		{
			this.chartProperties.setShowChartMarker(marker);
		}
	}
	
	public final boolean isLabel()
	{
		return this.chartProperties.getShowChartLabel() != null ? this.chartProperties.getShowChartLabel().booleanValue() : false;
	}

	public void showLabel(final boolean label)
	{
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getShowChartLabel() == null))
		{
			this.chartProperties.setShowChartLabel(label);
		}
	}	

	public boolean isScatterPlot() 
	{
		return scatterPlot;
	}

	public void setScatterPlot(boolean scatterPlot) 
	{
		this.scatterPlot = scatterPlot;
	}

	protected void drawXAxis(final UserInterface graphics)
	{
		final XAxisSeries series = (XAxisSeries) this.getXAxisSeries();
		if (series.getValuesType() == XAxisSeries.LONGVALUES ||
			series.getValuesType() == XAxisSeries.TIMEVALUES)
		{
			XAxisLongSeries longSeries = (XAxisLongSeries)series;
			if(!longSeries.isCrossedMidNight())
			{
				long crossedMidNightTime = -1;
				if (startValueSet)
				{
					crossedMidNightTime = xStartValue;
				}
				else if (Long.MAX_VALUE != longSeries.getMinValue())
				{
					crossedMidNightTime = longSeries.getMinValue();
				}
				if (crossedMidNightTime != -1)
				{
					longSeries.calculateCrossedMidNight(crossedMidNightTime);
				}
			}
		}
		
		if (!this.isMarker() || scatterPlot)
		{
			super.drawXAxis(graphics);
			return;
		}

		final Color background = graphics.getBackground();
		final Color foreground = graphics.getForeground();
		this.fillArea(graphics);
		graphics.setFont(this.chartProperties.getTickFont());

		final int startXpx = this.plotRect.x + this.shiftYaxisToRHS;
		final int endXpx = startXpx + this.plotRect.width - this.shiftYaxisToRHS;
		final int yCor = this.plotRect.y + this.plotRect.height - this.shiftXAxisAbove;

		// draw X-Axis line
		final LineAttributes oldLine = graphics.getLineStyle();
		graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
		graphics.drawLine(startXpx, yCor, endXpx, yCor);
		graphics.setLineStyle(oldLine);
		
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
			// It will be one minute less than the time value provided by user.
			startXval = Math.max(0L, startXval - 60 * 1000);
			// It will be one minute more than the time value provided by user.
			endXval = startXval + 60 * 1000;
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
		tickCount = Math.round((endXval - startXval) / tickInterval) + 1;
		tickWidth = Math.max(tickWidth, ((endXpx - 1) - startXpx) / (tickCount - 1));
		this.eachDataWidth = (int) Math.ceil((tickWidth * tickCount - tickWidth / 2) / (series.getCurrentCount() + 1));

		this.xStart = startXpx;
		this.xStartValue = startXval;
		this.xSlope = tickWidth / tickInterval;
		this.xEndValue = endXval;

		// draw Ticks
		String value = null;
		float drawX;
		float widthX;
		long tmpStartVal = startXval;
		if (!this.isFitToScale() && (this.benchmarkTime > -1))
		{
			tmpStartVal = this.benchmarkTime;
		}
		final int n = series.getCurrentCount();
		final int yGridCor = this.plotRect.y + this.plotRect.height - this.shiftXAxisAbove;
		boolean drawTick;
		float rhsLimit = 0.0f;
		for (int i = 0; i < n; i++)
		{
			long xValue;
			int xCor;
			if (series.getValuesType() == XAxisSeries.TIMEVALUES || series.getValuesType() == XAxisSeries.LONGVALUES)
			{
				xValue = ((XAxisLongSeries)series).getValue(i);
				xCor = this.resolveXCorordinate(xValue);
				if (series.getValuesType() == XAxisSeries.TIMEVALUES)
				{
					value = ((XAxisTimeSeries) series).getFormattedValue(tmpStartVal, xValue, this.xEndValue);
				}
				else
				{
					value = XAxisSeries.getFormattedValueOf(xValue);
				}
			}
			else
			{
				xValue = ((XAxisIntegerSeries)series).getValue(i);
				xCor = this.resolveXCorordinate(xValue);
				value = XAxisSeries.getFormattedValueOf(((XAxisIntegerSeries)series).getValue(i));
			}
			widthX = graphics.stringExtent(value).x;
			if (i == n - 1)
			{
				drawX = endXpx - widthX;
			}
			else
			{
				drawX = xCor - widthX * 0.5f;
			}
			
			if (drawX < this.plotRect.x)
			{
				drawX = this.plotRect.x;
			}
			
			drawTick = (/*i != 0 &&*/ drawX > rhsLimit) || !(series instanceof XAxisTimeSeries);
			if (drawTick)
			{
				rhsLimit = drawX + 1.5f * widthX;
			}
			
			if (this.isXAxisGrid() && drawTick)
			{
				final Color oldForeColor = graphics.getForeground();
				graphics.setForeground(ChartConstants.GRID_COLOR);
				final LineAttributes oldLineAttr = graphics.getLineStyle();
				graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND, LineAttributes.LINE_STYLE_DOT));
				graphics.drawLine(xCor, this.plotRect.y, xCor, yGridCor);
				graphics.setLineStyle(oldLineAttr);
				graphics.setForeground(oldForeColor);
			}			

			if (drawTick)
			{
				graphics.setBackground(this.chartProperties.getTickBackgroundColour());
				graphics.setForeground(this.chartProperties.getTickTextColour());
				graphics.drawLine(xCor, yCor, xCor, yCor + TICK_MARK_LENGTH);
			}
			if (drawTick)
			{
				// TODO: could cause problem due to type-casting !!!
				graphics.drawString(value, (int) drawX, yCor + INSET_FOR_TICK_VALUE_FROM_TICK_MARK, true);
			}
		}

		graphics.setForeground(ChartConstants.COLOR_DARK_GRAY);
		graphics.setBackground(background);
		// draw surrounding line
		graphics.drawLine(startXpx, this.plotRect.y, endXpx, this.plotRect.y);
		graphics.setForeground(foreground);
	}
	
	
	/**
	 * 
	 */
	public void drawSeries(final UserInterface graphics)
	{
		// TODO: Implement rendering hints in swt
		// Object renderingHint = renderingHint =
		// graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		// graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		final Color background = graphics.getBackground();
		final Series[] yAxisSeries = this.getYAxisSeries();
		for (int i = 0; i < yAxisSeries.length; i++)
		{
			if (this.shouldDrawSeries(i))
			{
				Color seriesColor = getColorForIndex(i, this.chartProperties);
				graphics.setBackground(seriesColor);
				graphics.setForeground(seriesColor);
				this.drawSeries(graphics, (YAxisSeries) yAxisSeries[i], i, background, seriesColor);
			}
		}
	}
	
	private void drawSeries(final UserInterface graphics, final YAxisSeries yAxisSeries, final int yIndex, Color background, Color seriesColor)
	{
		final XAxisSeries series = this.getXAxisSeries();

		long xValue;
		long xNextValue = 0;
		int yValue;
		int xCor = 100;
		int yCor = 100;

		int prevXCor = xCor;
		int prevYCor = yCor;
		int prevYValue = Integer.MIN_VALUE;

		final int n = series.getCurrentCount();
		boolean unadjusted = true;
		long tmpStartVal = this.xStartValue;
		if (!this.isFitToScale() && (this.benchmarkTime > -1))
		{
			tmpStartVal = this.benchmarkTime;
		}
		Rectangle prevDrawRect = null;
		for (int i = 0; i < n; i++)
		{
			if (series.getValuesType() == XAxisSeries.TIMEVALUES || series.getValuesType() == XAxisSeries.LONGVALUES)
			{
				xValue = ((XAxisLongSeries)series).getValue(i);
				if (i < n - 1)
				{
					xNextValue = ((XAxisLongSeries)series).getValue(i + 1);
				}
			}
			else
			{
				xValue = ((XAxisIntegerSeries)series).getValue(i);
				if (i < n - 1)
				{
					xNextValue = ((XAxisIntegerSeries)series).getValue(i + 1);
				}
			}
			
			unadjusted = true;

			if ((i < n - 1) && (xNextValue != Long.MIN_VALUE && xNextValue < this.xStartValue))
			{
				continue;
			}
			else if (xValue < this.xStartValue)
			{
				xValue = this.xStartValue;
				unadjusted = false;
			}
			yValue = yAxisSeries.getValue(i);
			xCor = this.resolveXCorordinate(xValue);
			yCor = this.resolveYCorordinate(yValue);
			
			final Rectangle drawRect = scatterPlot ? new Rectangle(xCor - 2, yCor - 2, 4, 4):
					new Rectangle(xCor - 1, yCor - 1, 2, 2);
			if (this.bShowToolTip)
			{
				final Rectangle marker = scatterPlot ? new Rectangle(xCor - 5, yCor - 5, 8, 8):
					new Rectangle(xCor - 3, yCor - 3, 6, 6);
				String formattedXValue;
				if (series.getValuesType() == XAxisSeries.TIMEVALUES)
				{
					formattedXValue = ((XAxisTimeSeries) series).getFormattedValue(tmpStartVal, xValue,
						this.xEndValue);
				}
				else
				{
					formattedXValue = XAxisSeries.getFormattedValueOf(xValue);
				}
				this.addMarkerObject(new MarkerObject(marker, formattedXValue, String.valueOf(yValue), i, yIndex));
			}

			if (Integer.MIN_VALUE != yValue)
			{
				if (!scatterPlot && Integer.MIN_VALUE != prevYValue)
				{
					final LineAttributes oldLine = graphics.getLineStyle();
					graphics.setLineStyle(new LineAttributes(1.35f, LineAttributes.CAP_ROUND, LineAttributes.JOIN_ROUND));
					
					if(this.isStepChart)
					{
						graphics.drawLine(prevXCor, prevYCor, xCor, prevYCor);
						graphics.drawLine(xCor, prevYCor, xCor, yCor);
					}
					else
					{
						graphics.drawLine(prevXCor, prevYCor, xCor, yCor);
					}
					
					graphics.setLineStyle(oldLine);
				}
				if (this.isMarker() && unadjusted)
				{
					graphics.drawArc(drawRect.x, drawRect.y, drawRect.width, drawRect.height, 0, 360);
					graphics.fillArc(drawRect.x, drawRect.y, drawRect.width, drawRect.height, 0, 360);
					//graphics.drawRectangle(drawRect.x, drawRect.y, drawRect.width, drawRect.height);
					//graphics.fillRectangle(drawRect.x, drawRect.y, drawRect.width, drawRect.height);
				}
				if (this.isLabel() && (i - 1) != 0  && ((i-1) %4 == 0) && prevYValue != yStartValue)
				{
					//graphics.setBackground(background);
					//graphics.setBackground(getAreaColor());
					graphics.setForeground(ChartConstants.COLOR_BLACK);
					graphics.drawString(String.valueOf(prevYValue), prevDrawRect.x + prevDrawRect.width + 4, prevDrawRect.y - 3 * prevDrawRect.height, true);
					graphics.setBackground(seriesColor);
					graphics.setForeground(seriesColor);
				}
				

			}
			prevXCor = xCor;
			prevYCor = yCor;
			prevYValue = yValue;
			prevDrawRect = drawRect;
		}
	}

	public void setXAxisStartValue(final long value)
	{
		this.xStartValue = value;
		this.startValueSet = true;
	}
	
	public Rectangle getChartAreaBoundary() 
	{
		return chartAreaRect != null ? new Rectangle(xStart, this.plotRect.y, chartAreaRect.width, this.plotRect.y + this.plotRect.height - this.shiftXAxisAbove):null;
	}
}
