/*
 * @(#) AppPieChart.java Feb 7, 2005 Copyright (C) 2002 Exceed Consultancy
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

import com.queryio.common.IProductConstants;
import com.queryio.common.charts.components.Legend;
import com.queryio.common.charts.components.Title;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.charts.properties.ChartPropertiesManager;
import com.queryio.common.charts.series.YAxisSeries;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Rectangle;

/**
 * @author Exceed Consultancy Services
 */
public class AppPieChart extends AppChart
{
	/*
	 * Pie charts have only one capacity, unlike bar chart etc. so it is set to
	 * 1.
	 */
	private static final int CAPACITY = 1;
	/* Since pie charts have only one cpacity, the index to refer it is 0. */
	private static final int YAxisIndex = 0;
	private static final int ZERO = 0;
	private YAxisSeries[] yAxisSeries = null;

	private float arcAngles[] = null;
	private int arcPercentages[] = null;

	// private boolean bShowBorder = false;

	// private static final int BORDERINSET = 20;
	// private static final int SPACEINSET = 2;

	public AppPieChart(final UserInterface userInterface, final int nodeType, final int productID)
	{
		super(userInterface, nodeType, productID);
	}

	/**
	 * The user should specify, the number of series, that the Pie chart will be
	 * having.
	 * 
	 * @param noOfYAxis
	 */
	public void initializeSeries(final int noOfYAxis)
	{
		this.yAxisSeries = new YAxisSeries[noOfYAxis];

		for (int i = 0; i < this.yAxisSeries.length; i++)
		{
			this.yAxisSeries[i] = new YAxisSeries(); // $IGN_Avoid_object_instantiation_in_loops$
			this.yAxisSeries[i].initialize(CAPACITY);
		}

		// if we don't set it to null, it doesn't do anything in
		// calculateDimensions
		// because of which drawArea doesn't reflect any change.
		this.arcAngles = null;
	}

	/**
	 * The user should pass the series index and its name.
	 * 
	 * @param index
	 * @param name
	 */
	public void setSeriesName(final int index, final String name)
	{
		if ((index >= 0) && (index < this.yAxisSeries.length))
		{
			this.yAxisSeries[index].setText(name);
			this.chartProperties.setYAxisSeriesText(index, name);
		}
	}

	/**
	 * The user should should pass the series index and its color.
	 * 
	 * @param index
	 * @param c
	 */
	public void setSeriesColour(final int index, final Color c)
	{
		if ((index >= 0) && (index < this.yAxisSeries.length))
		{
			this.yAxisSeries[index].setSeriesColor(c);
			this.chartProperties.setYAxisSeriesColor(index, c);
		}
	}

	/**
	 * The user should pass series index and its value. Pie Chart will draw arcs
	 * according to these values. If the value passed is negative, the value
	 * will be set to 0.
	 * 
	 * @param index
	 * @param value
	 */
	public void setSeriesValue(final int index, final int value)
	{
		if ((index >= 0) && (index < this.yAxisSeries.length))
		{
			this.yAxisSeries[index].setValue(YAxisIndex, Math.max(ZERO, value));
		}
	}

	protected void calculateDimensions(final UserInterface graphics, final int width, final int height)
	{
		final int x = 0;
		final int y = 0;

		final Legend[] legends = this.getLegend();
		if (legends != null)
		{
			for (int i = 0; i < legends.length; i++)
			{
				if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
						|| (this.chartProperties.getYAxisSeriesColor(i) == null))
				{
					this.chartProperties.setYAxisSeriesColor(i, legends[i].getSeriesColor());
				}
			}
		}
		if (this.isLegend())
		{
			int w = 0;
			if ((legends != null) && (legends.length > 0))
			{
				final int legendItemWidth = this.getMaxWidth(graphics);
				final int legendItemHeight = Legend.getMaxHeight(graphics, this.chartProperties.getLegendFont());
				if (legendItemHeight * legends.length > height)
				{
					final int noOfItemsInACol = (height - LEGENDSTARTINSET) / legendItemHeight;
					if (noOfItemsInACol > 0)
					{
						w = legends.length / noOfItemsInACol;
						if (legends.length % noOfItemsInACol > 0)
						{
							w++;
						}
					}
				}
				else
				{
					w = 1;
				}
				w *= legendItemWidth;
				w += 2;
			}
			// legendRect = new Rectangle(x , y + height - h, width, h);
			this.legendRect = new Rectangle(x + width - w - 2, y, w, height);
		}

		// assign values to each rectangle
		final Title chartTitle = this.chartProperties.getChartTitle();
		if (chartTitle.isTitle())
		{
			this.chartTitleRect = new Rectangle(x, y, width, y + chartTitle.getMaxHeight(graphics));
			this.chartAreaRect = new Rectangle(x, y + this.chartTitleRect.height, width, height
					- this.chartTitleRect.height);
		}
		else
		{
			this.chartAreaRect = new Rectangle(x, y, width, height);
		}

		if (this.isLegend())
		{
			this.chartAreaRect.width -= this.legendRect.width;
		}
	}

	/**
	 * This method is the one, which draws whole of the pie chart.
	 */
	public void drawArea(final UserInterface graphics)
	{
		this.calculateDimensions();
		final Rectangle r = graphics.getClipping();
		int iSquareDimension = 0;
		int x = r.x;
		int y = r.y;
		if (r.width < r.height)
		{
			y += (r.height - r.width) / 2;
			iSquareDimension = r.width;
		}
		else
		{
			x += (r.width - r.height) / 2;
			iSquareDimension = r.height;
		}

		iSquareDimension -= 4;
		x += 2;
		y += 2;

		/*
		 * if(isShowBorder()) { iSquareDimension -= BORDERINSET; x +=
		 * BORDERINSET/2; y += BORDERINSET/2; }
		 */
		float startAngleFrom = 90f;
		// TODO: Implement rendering hints in swt
		// Object renderingHint = renderingHint =
		// graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		// graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		// Arc2D.Float arc2DFloat = null;
		int arcAngle = 0;
		for (int i = this.arcAngles.length - 1; i >= 0; i--)
		{
			if (this.shouldDrawSeries(i))
			{
				arcAngle = (int) this.arcAngles[i];
				if (arcAngle > 0)
				{
					graphics.setBackground(getColorForIndex(i, this.chartProperties));
					graphics.fillArc(x, y, iSquareDimension, iSquareDimension, (int) startAngleFrom, arcAngle);
					graphics.setForeground(ChartConstants.COLOR_GRAY);
					graphics.drawArc(x, y, iSquareDimension, iSquareDimension, (int) startAngleFrom, arcAngle);
					startAngleFrom += this.arcAngles[i];
				}
			}
		}

		/*
		 * if(isShowBorder()) { int w = r.width; // if (isLegend()) // { // w +=
		 * legendRect.width; // } Color oldColor = graphics.getForeground();
		 * graphics.setForeground(ChartConstants.PIE_CHARTBORDER);
		 * //graphics.drawRectangle(r.x, r.y, r.width - SPACEINSET, r.height -
		 * SPACEINSET); graphics.drawRectangle(r.x, r.y, w - SPACEINSET,
		 * r.height - SPACEINSET); graphics.setForeground(oldColor); }
		 */
		// graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// renderingHint);
	}

	/**
	 * This mehtod will return the appropriate angles, according to all the
	 * values.
	 * 
	 * @return
	 */
	private void calculateDimensions()
	{
		int yAxisSeriesLen = 0;
		for (int i = 0; i < this.yAxisSeries.length; i++)
		{
			if (this.shouldDrawSeries(i))
			{
				yAxisSeriesLen++;
			}
		}
		if (this.arcAngles == null)
		{
			this.arcAngles = new float[yAxisSeriesLen];
			this.arcPercentages = new int[yAxisSeriesLen];
		}
		int totalValue = 0;

		for (int i = 0; i < this.yAxisSeries.length; i++)
		{
			if (this.shouldDrawSeries(i))
			{
				totalValue += this.yAxisSeries[i].getValue(YAxisIndex);
			}
		}

		for (int i = 0; i < this.yAxisSeries.length; i++)
		{
			if (this.shouldDrawSeries(i))
			{
				this.arcPercentages[i] = Math.round(this.getPercentage(this.yAxisSeries[i].getValue(YAxisIndex),
						totalValue, 100));
				this.arcAngles[i] = this.getPercentage(this.yAxisSeries[i].getValue(YAxisIndex), totalValue, 360);
			}
		}
	}

	/**
	 * @param actualValue
	 * @param totalValue
	 * @param percentageOutOf
	 * @return
	 */
	private float getPercentage(final float actualValue, final float totalValue, final int percentageOutOf)
	{
		return (totalValue == 0 ? 0.0f : actualValue * percentageOutOf / totalValue);
	}

	/**
	 * This method will return the Legend, if there happens to atleast one data
	 * in the yAxisSeries, or else it will return NULL.
	 */
	public Legend[] getLegend()
	{
		if ((this.yAxisSeries == null) || (this.yAxisSeries.length == 0))
		{
			return null;
		}
		this.calculateDimensions();
		final Legend[] legends = new Legend[this.yAxisSeries.length];
		for (int i = 0; i < legends.length; i++)
		{
			// legends[i] = new Legend(yAxisSeries[i].getText() + " (" +
			// arcPercentages[i] +" %)", yAxisSeries[i].getSeriesColor());
			legends[i] = new Legend(
					this.chartProperties.getYAxisSeriesText(i) + " (" + this.arcPercentages[i] + " %)", AppChart.getColorForIndex(i, this.chartProperties), this.chartProperties.getLegendTextColour()); //$NON-NLS-1$ //$NON-NLS-2$ //$IGN_String_concatenation_in_loop$
			legends[i].legendFont = this.chartProperties.getLegendFont();
		}
		return legends;
	}

	public String[][] getTableValues()
	{
		final int yAxisSeriesLen = this.yAxisSeries.length;
		final String[][] tableValues = new String[yAxisSeriesLen][2];
		for (int i = 0; i < yAxisSeriesLen; i++)
		{
			tableValues[i][0] = this.yAxisSeries[i].getText();
			tableValues[i][1] = IProductConstants.EMPTY_STRING + this.arcPercentages[i];
		}
		return tableValues;
	}

	public Color getSeriesColor(final int seriesIndex)
	{
		return ((this.yAxisSeries != null) && (seriesIndex != -1) && (seriesIndex < this.yAxisSeries.length)) ? getColorForIndex(
				seriesIndex, this.chartProperties)
				: null;
	}

	/**
	 * If the user opts for showing a border, then a rectangle will be drawn on
	 * the border of the pie chart. A border line would be between the
	 * ChartTitle and pieChart and similarly between the pieChart and Legend.
	 * 
	 * @param showBorder
	 */
	public void showBorder(final boolean showBorder)
	{
		// bShowBorder = showBorder;
	}

	public YAxisSeries[] getYAxisSeries()
	{
		return this.yAxisSeries;
	}

	/**
	 * This will return a boolean value, indicating if the user has opted for
	 * showin a border around the pieChart.
	 * 
	 * @return
	 */
	// private boolean isShowBorder()
	// {
	// return bShowBorder;
	// }
}
