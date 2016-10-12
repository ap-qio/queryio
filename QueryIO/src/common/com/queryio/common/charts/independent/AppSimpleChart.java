/*
 * @(#) AppSimpleChart.java Feb 4, 2005 Copyright (C) 2002 Exceed Consultancy
 * Services. All Rights Reserved. This software is proprietary information of
 * Exceed Consultancy Services and constitutes valuable trade secrets of Exceed
 * Consultancy Services. You shall not disclose this information and shall use
 * it only in accordance with the terms of License. EXCEED CONSULTANCY SERVICES
 * MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.charts.independent;

import com.queryio.common.charts.components.Legend;
import com.queryio.common.charts.components.MarkerObject;
import com.queryio.common.charts.components.Title;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.charts.properties.ChartPropertiesManager;
import com.queryio.common.charts.series.Series;
import com.queryio.common.charts.series.XAxisIntegerSeries;
import com.queryio.common.charts.series.XAxisLongSeries;
import com.queryio.common.charts.series.XAxisSeries;
import com.queryio.common.charts.series.XAxisStringSeries;
import com.queryio.common.charts.series.XAxisTimeSeries;
import com.queryio.common.charts.series.YAxisSeries;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Font;
import com.queryio.common.exporter.dstruct.Rectangle;

/**
 * @author Exceed Consultancy Services
 */
public abstract class AppSimpleChart extends AppChart
{
	public static final double LOG_10 = Math.log(10);
	private int iMaxHeight = -1;

	private XAxisSeries xAxisSeries = null;
	private Series[] yAxisSeries = null;

	protected static final double angle = Math.PI * 0.5;

	protected Rectangle xAxisTitleRect = null;
	protected Rectangle yAxisTitleRect = null;
	protected Rectangle plotRect = null;

	protected int shiftXAxisAbove = 0;
	protected int shiftYaxisToRHS = 0;
	protected int eachDataWidth = 0;

	protected int xStart = 0;
	protected long xStartValue = -1;
	protected long benchmarkTime = -1;
	protected boolean startValueSet = false;
	protected float xSlope = 0;
	protected int yStart = 0;
	protected long yStartValue = 0;
	protected float ySlope = 0;
	protected int iAvailablePxs;
	protected int iMaxStringWidth;
	protected int iShiftYAxisAbove = 0;

	private int iMinYValue;
	private int iMaxYValue;
	protected boolean bMinMaxYValueSet;

	private final boolean horizontal;

	public AppSimpleChart(final UserInterface userInterface, final boolean horizontal, final int nodeType,
			final int productID)
	{
		super(userInterface, nodeType, productID);
		this.horizontal = horizontal;
		if (this.chartProperties.getTickBackgroundColour() == null)
		{
			this.chartProperties.setTickBackgroundColour(this.chartProperties.getBackgroundColour());
		}
		if (this.chartProperties.getTickTextColour() == null)
		{
			this.chartProperties.setTickTextColour(ChartConstants.COLOR_BLACK);
		}
		this.hasChartTypeChanged = false;
	}

	public final XAxisSeries getXAxisSeries()
	{
		return this.xAxisSeries;
	}

	public Series getYAxisSeries(final int index)
	{
		return this.yAxisSeries[index];
	}

	public final Series[] getYAxisSeries()
	{
		return this.yAxisSeries;
	}

	public void showXAxisTitle(final boolean title)
	{
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getShowXAxisTitle() == null))
		{
			this.chartProperties.setShowXAxisTitle(title);
		}
	}

	public void showYAxisTitle(final boolean title)
	{
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getShowYAxisTitle() == null))
		{
			this.chartProperties.setShowYAxisTitle(title);
		}
	}

	public void showXAxisGrids(final boolean grids)
	{
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getXAxisGrids() == null))
		{
			this.chartProperties.setShowXAxisGrids(grids);
		}
	}

	public void showYAxisGrids(final boolean grids)
	{
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getYAxisGrids() == null))
		{
			this.chartProperties.setShowYAxisGrids(grids);
		}
	}

	public boolean isXAxisTitle()
	{
		return this.chartProperties.getShowXAxisTitle().booleanValue();
	}

	public boolean isYAxisTitle()
	{
		return this.chartProperties.getShowYAxisTitle().booleanValue();
	}

	public final boolean isXAxisGrid()
	{
		final Boolean axisGrids = this.chartProperties.getXAxisGrids();
		return (axisGrids != null) && axisGrids.booleanValue();
	}

	public final boolean isYAxisGrid()
	{
		final Boolean axisGrids = this.chartProperties.getYAxisGrids();
		return (axisGrids != null) && axisGrids.booleanValue();
	}

	public void setXAxisTitle(final String title)
	{
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getXAxisTitle() == null))
		{
			this.chartProperties.setXAxisTitle(title);
			if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
					|| (this.chartProperties.getXAxisTitleFont() == null))
			{
				this.chartProperties.setXAxisTitleFont(this.chartProperties.getAxisFont());
			}
			if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
					|| (this.chartProperties.getXAxisTitleTextColour() == null))
			{
				this.chartProperties.setXAxisTitleTextColour(ChartConstants.COLOR_BLACK);
			}
		}
		this.configureOrientationOfXandYAxisTitles();
	}

	public void setYAxisTitle(final String title)
	{
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getYAxisTitle() == null))
		{
			this.chartProperties.setYAxisTitle(title);
			if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
					|| (this.chartProperties.getYAxisTitleFont() == null))
			{
				this.chartProperties.setYAxisTitleFont(this.chartProperties.getAxisFont());
			}
			if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
					|| (this.chartProperties.getYAxisTitleTextColour() == null))
			{
				this.chartProperties.setYAxisTitleTextColour(ChartConstants.COLOR_BLACK);
			}
		}
		this.configureOrientationOfXandYAxisTitles();
	}

	public String getXAxisTitle()
	{
		final Title axisTitle = this.chartProperties.getXAxisTitle();
		return axisTitle != null ? axisTitle.getText() : null;
	}

	public String getYAxisTitle()
	{
		final Title axisTitle = this.chartProperties.getYAxisTitle();
		return axisTitle != null ? axisTitle.getText() : null;
	}

	public final Color getAreaColor()
	{
		return this.chartProperties.getAreaColour();
	}

	public void setAreaColor(final Color color)
	{
		if ((color != null)
				&& ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS) || (this.chartProperties
						.getAreaColour() == null)))
		{
			this.chartProperties.setAreaColour(color);
		}
	}

	public void initializeSeries(final int noOfYAxis, final int capacity)
	{
		this.initializeSeries(noOfYAxis, capacity, XAxisSeries.TIMEVALUES, true);
	}

	public void initializeSeries(final int noOfYAxis, final int capacity, final boolean bShowRelativeTime)
	{
		this.initializeSeries(noOfYAxis, capacity, XAxisSeries.TIMEVALUES, bShowRelativeTime);
	}

	public void initializeSeries(final int noOfYAxis, final int capacity, final int typeOfXAxisData)
	{
		this.initializeSeries(noOfYAxis, capacity, typeOfXAxisData, true);
	}

	public void initializeSeries(final int noOfYAxis, final int capacity, final int typeOfXAxisData,
		final boolean bShowRelativeTime)
	{
		switch (typeOfXAxisData)
		{
			case XAxisSeries.INTVALUES:
			{
				this.xAxisSeries = new XAxisIntegerSeries();
				break;
			}
			case XAxisSeries.LONGVALUES:
			{
				this.xAxisSeries = new XAxisLongSeries(XAxisSeries.LONGVALUES);
				break;
			}
			case XAxisSeries.STRINGVALUES:
			{
				this.xAxisSeries = new XAxisStringSeries();
				break;
			}
			case XAxisSeries.TIMEVALUES: // $IGN_Use_break_for_each_case$
			default:
			{
				this.xAxisSeries = new XAxisTimeSeries(bShowRelativeTime);
				break;
			}
		}
		this.xAxisSeries.initialize(capacity);

		this.yAxisSeries = new YAxisSeries[noOfYAxis];

		for (int i = 0; i < this.yAxisSeries.length; i++)
		{
			this.yAxisSeries[i] = new YAxisSeries(); // $IGN_Avoid_object_instantiation_in_loops$
			this.yAxisSeries[i].initialize(capacity);
		}
	}

	/**
	 * This method sets the Background color.
	 * 
	 * @param graphics
	 */
	protected final void fillArea(final UserInterface graphics)
	{
		final Color areaColour = this.chartProperties.getAreaColour();
		if (areaColour != null)
		{
			final Color oldColor = graphics.getBackground();
			graphics.setBackground(areaColour);
			graphics.fillRectangle(this.plotRect.x + this.shiftYaxisToRHS, this.plotRect.y, this.plotRect.width
					- this.shiftYaxisToRHS, this.plotRect.height - this.shiftXAxisAbove - this.iShiftYAxisAbove);
			graphics.setBackground(oldColor);
		}
	}

	protected final void clearAll()
	{
		this.xAxisTitleRect = null;
		this.yAxisTitleRect = null;
	}

	protected final void calculateDimensions(final UserInterface graphics)
	{
		final Rectangle clippedRect = graphics.getClipping();

		final int INSET = 2;
		this.plotRect = new Rectangle(clippedRect.x, clippedRect.y + INSET, clippedRect.width - INSET,
				clippedRect.height - INSET);

		final Title axisTitle = this.chartProperties.getYAxisTitle();
		final Title axisTitle2 = this.chartProperties.getXAxisTitle();
		if (this.horizontal)
		{
			if ((axisTitle2 != null) && this.chartProperties.getShowXAxisTitle().booleanValue())
			{
				this.xAxisTitleRect = new Rectangle(clippedRect.x, clippedRect.y, axisTitle2.getMaxHeight(graphics),
						clippedRect.height);
				this.plotRect.width -= this.xAxisTitleRect.width;
				this.plotRect.x += this.xAxisTitleRect.width;
			}

			if ((axisTitle != null) && this.chartProperties.getShowYAxisTitle().booleanValue())
			{
				this.yAxisTitleRect = new Rectangle(clippedRect.x, clippedRect.y + clippedRect.height
						- axisTitle.getMaxHeight(graphics), clippedRect.width, axisTitle.getMaxHeight(graphics));
				this.plotRect.height -= this.yAxisTitleRect.height;
			}
		}
		else
		{
			// assign values to each rectangle
			if ((axisTitle2 != null) && this.chartProperties.getShowXAxisTitle().booleanValue())
			{
				this.xAxisTitleRect = new Rectangle(clippedRect.x, clippedRect.y + clippedRect.height
						- axisTitle2.getMaxHeight(graphics), clippedRect.width, axisTitle2.getMaxHeight(graphics));
				this.plotRect.height -= this.xAxisTitleRect.height;
			}

			if ((axisTitle != null) && this.chartProperties.getShowYAxisTitle().booleanValue())
			{
				this.yAxisTitleRect = new Rectangle(clippedRect.x, clippedRect.y, axisTitle.getMaxHeight(graphics),
						clippedRect.height);
				this.plotRect.width -= this.yAxisTitleRect.width;
				this.plotRect.x += this.yAxisTitleRect.width;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.appperfect.charts.AppChart#getLegend()
	 */
	public Legend[] getLegend()
	{
		final Series[] yAxisSeries = this.getYAxisSeries();
		if ((yAxisSeries == null) || (yAxisSeries.length == 0))
		{
			return null;
		}
		final Legend[] legends = new Legend[yAxisSeries.length];
		for (int i = 0; i < legends.length; i++)
		{
			legends[i] = new Legend(this.chartProperties.getYAxisSeriesText(i), AppChart.getColorForIndex(i,
					this.chartProperties), this.chartProperties.getLegendTextColour());
			legends[i].legendFont = this.chartProperties.getLegendFont();
		}

		return legends;
	}

	/**
	 * Used to specify range as min and max if graph need to show values on Y
	 * axis within a range only. method setMinMaxYValue
	 * 
	 * @param min
	 * @param max
	 *            NOTE : TO BE USED IN CASE OF VERTICAL CHARTS.
	 */
	public void setMinMaxYValue(final int min, final int max)
	{
		this.iMinYValue = min;
		this.iMaxYValue = max;
		this.bMinMaxYValueSet = true;
	}

	public String[] getXAxisMinMaxValues(final UserInterface graphics)
	{
		String[] arrMinMax = null;
		final XAxisSeries xAxisSeries = this.getXAxisSeries();

		if (xAxisSeries.getValuesType() == XAxisSeries.TIMEVALUES)
		{
			arrMinMax = new String[2];
			final XAxisTimeSeries timeSeries = (XAxisTimeSeries) xAxisSeries;
			arrMinMax[0] = timeSeries.getFormattedMinValue();
			arrMinMax[1] = timeSeries.getFormattedMaxValue();
		}
		else if (xAxisSeries.getValuesType() == XAxisSeries.LONGVALUES)
		{
			arrMinMax = new String[2];
			arrMinMax[0] = String.valueOf(((XAxisLongSeries) xAxisSeries).getMinValue());
			arrMinMax[1] = String.valueOf(((XAxisLongSeries) xAxisSeries).getMaxValue());
		}
		else if (xAxisSeries.getValuesType() == XAxisSeries.INTVALUES)
		{
			arrMinMax = new String[2];
			arrMinMax[0] = String.valueOf(((XAxisIntegerSeries) xAxisSeries).getMinValue());
			arrMinMax[1] = String.valueOf(((XAxisIntegerSeries) xAxisSeries).getMaxValue());
		}
		else if (xAxisSeries.getValuesType() == XAxisSeries.STRINGVALUES)
		{
			arrMinMax = ((XAxisStringSeries) xAxisSeries).getMinMaxValues(graphics);
		}
		return arrMinMax;
	}

	public int[] getYAxisMinMaxValues()
	{
		if (this.bMinMaxYValueSet)
		{
			return new int[] { this.iMinYValue, this.iMaxYValue };
		}

		final Series[] yAxisSeries = this.getYAxisSeries();
		YAxisSeries series = null;
		final int[] minMax = new int[] { Integer.MAX_VALUE, Integer.MIN_VALUE };
		for (int i = 0; i < yAxisSeries.length; i++)
		{
			if (this.shouldDrawSeries(i))
			{
				series = (YAxisSeries) yAxisSeries[i];
				minMax[0] = Math.min(minMax[0], series.getMinValue());
				minMax[1] = Math.max(minMax[1], series.getMaxValue());
			}
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

	public int getMaxHeight(final UserInterface graphics, final Font font)
	{
		if (this.iMaxHeight == -1)
		{
			final Font oldFont = graphics.getFont();
			graphics.setFont(font);
			this.iMaxHeight = graphics.getFontHeight();
			graphics.setFont(oldFont);
		}
		return this.iMaxHeight;
	}

	protected abstract void configureOrientationOfXandYAxisTitles();

	public abstract void drawSeries(UserInterface graphics);

	protected abstract void drawXAxis(UserInterface graphics);

	protected abstract void drawYAxis(UserInterface graphics);

	public long[] getMarkerObjectPosition(final int x, final int y)
	{
		if (this.markerList != null)
		{
			XAxisSeries series = this.getXAxisSeries();
			final int size = this.markerList.size();
			MarkerObject markerObject = null;
			for (int i = 0; i < size; i++)
			{
				markerObject = (MarkerObject) this.markerList.get(i);
				if (markerObject.contains(x, y))
				{
					long[] array = new long[2];
					if (series.getValuesType() == XAxisSeries.LONGVALUES || series.getValuesType() == XAxisSeries.TIMEVALUES)
					{
						array[0] = ((XAxisLongSeries)series).getValue(markerObject.getValueIndex());
						array[1] = array[0];
						
						if (markerObject.getValueIndex() + 1 < series.getCapacity())
						{
							array[1] = ((XAxisLongSeries)series).getValue(markerObject.getValueIndex() + 1);
						}
					}
					else if (series.getValuesType() == XAxisSeries.INTVALUES)
					{
						array[0] = ((XAxisIntegerSeries)series).getValue(markerObject.getValueIndex());
						array[1] = array[0];
						
						if (markerObject.getValueIndex() + 1 < series.getCapacity())
						{
							array[1] = ((XAxisIntegerSeries)series).getValue(markerObject.getValueIndex() + 1);
						}
					}
					return array;
				}
			}
		}
		return null;
	}

	public Color getYAxisSeriesColour(final int i)
	{
		return this.chartProperties.getYAxisSeriesColor(i);
	}

	public void setYAxisSeriesColour(final int i, final Color colour)
	{
		Color seriesColour = this.getYAxisSeriesColour(i);
		if(colour != null)
//		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS) || (seriesColour == null))
		{
			this.chartProperties.setYAxisSeriesColor(i, colour);
			seriesColour = colour;
		}
		this.yAxisSeries[i].setSeriesColor(seriesColour);
	}

	public String getYAxisSeriesText(final int i)
	{
		return this.chartProperties.getYAxisSeriesText(i);
	}

	public void setYAxisSeriesText(final int i, final String text)
	{
		String seriesText = this.getYAxisSeriesText(i);
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS) || (seriesText == null))
		{
			this.chartProperties.setYAxisSeriesText(i, text);
			seriesText = text;
		}
		this.yAxisSeries[i].setText(seriesText);
	}

	public boolean isLogarithmic()
	{
		final Boolean logarithmic = this.chartProperties.isLogarithmic();
		return logarithmic != null ? logarithmic.booleanValue() : false;
	}

	public void setLogarithmic(final boolean logarithmic)
	{
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.isLogarithmic() == null))
		{
			this.chartProperties.setLogarithmic(logarithmic);
		}
	}

	protected double getLogValue(final long value)
	{
		return value >= 1 ? Math.log(value) / LOG_10 : 0;
	}

}
