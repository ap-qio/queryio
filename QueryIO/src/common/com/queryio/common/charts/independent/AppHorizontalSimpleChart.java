/*
 * @(#)  AppHorizontalSimpleChart.java Feb 7, 2005
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

import com.queryio.common.IProductConstants;
import com.queryio.common.charts.components.Title;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.charts.series.XAxisSeries;
import com.queryio.common.charts.series.XAxisTimeSeries;
import com.queryio.common.charts.util.UIGraphicsFactory;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Font;
import com.queryio.common.exporter.dstruct.LineAttributes;
import com.queryio.common.exporter.dstruct.Point;
import com.queryio.common.exporter.dstruct.Rectangle;

/**
 * 
 * @author Exceed Consultancy Services
 */
public abstract class AppHorizontalSimpleChart extends AppSimpleChart implements IScrollableChart {
	private static final int TICKMARK = 1;
	private static final int TICKMARKVALUEINSET = 2;
	private static final int DEFAULT_MAX_CHARS_ON_YAXIS = 3;
	protected int insetForBarOnOneside = 2;

	private int iMinWidthReqdDefaultFont = 0;

	private Rectangle yAxisRect = null;
	private boolean scrollable = false;

	public void drawScrollableArea(final UserInterface graphics, int originY, final int width, final int maximum) {
		// draw X-Axis
		this.drawXAxis(graphics, originY, width, maximum);

		// draw Series
		this.drawSeries(graphics, originY);

		// draw surrounding line
		graphics.drawLine(this.shiftYaxisToRHS, -originY, width, -originY);
		graphics.drawLine(width - 17, -originY, width - 17, maximum);

	}

	public abstract void drawSeries(UserInterface graphics, int originY);

	public int getIncrement() {
		return this.eachDataWidth;
	}

	public Rectangle getSeriesPlotBounds() {
		return new Rectangle(this.plotRect.x, this.plotRect.y, this.plotRect.width + 1, this.plotRect.height + 1);
	}

	public int getTotalPlotHeight() {
		int currentCount = this.getXAxisSeries().getCurrentCount();
		return this.eachDataWidth * (currentCount > 0 ? currentCount : 1);
	}

	public int getTotalChartHeight(UserInterface graphics, int width, int height) {
		calculateDimensions(graphics, width, height);
		int currentCount = this.getXAxisSeries().getCurrentCount();
		final Font oldFont = graphics.getFont();
		final Font defaultTickFont = this.chartProperties.getTickFont();
		graphics.setFont(defaultTickFont);
		int locatEachDataWidth = graphics.getFontHeight() * 2 + TICKMARKVALUEINSET - 2;
		graphics.setFont(oldFont);

		int newHeight = locatEachDataWidth * (currentCount > 0 ? (currentCount + 2) : 1);
		if (newHeight > chartAreaRect.height) {
			height += newHeight - chartAreaRect.height;
		}
		return height;
	}

	public Rectangle getXAxisTitleBounds() {
		return this.xAxisTitleRect;
	}

	public Rectangle getYAxisBounds() {
		return this.yAxisRect;
	}

	public Rectangle getYAxisTitleBounds() {
		return this.yAxisTitleRect;
	}

	public boolean isScrollable() {
		return this.scrollable;
	}

	public void setScrollable(final boolean scrollable) {
		this.scrollable = scrollable;
	}

	public AppHorizontalSimpleChart(final UserInterface userInterface, final int nodeType, final int productID) {
		super(userInterface, true, nodeType, productID);
		if (this.chartProperties.isStretchAlternateBackground() == null) {
			this.chartProperties.setStretchAlternateBackground(true);
		}
	}

	// /*
	// * (non-Javadoc)
	// * @see
	// com.queryio.sysmoncommon.charts.independent.AppSimpleChart#showYAxisTitle(boolean)
	// */
	// public void showYAxisTitle(boolean title)
	// {
	// // since we don't want to show Y-Axis title in horizontal charts.
	// yAxisTitle.showTitle(false);
	// }

	/**
	 * @see com.queryio.sysmoncommon.charts.swt.AppSimpleChart#configureOrientationOfXandYAxisTitles()
	 */
	protected void configureOrientationOfXandYAxisTitles() {
		this.chartProperties.getXAxisTitle().setOrientation(Title.ORIENTATION_VERTICAL);
	}

	/**
	 * @see com.queryio.sysmoncommon.charts.swt.AppChart#drawArea(org.eclipse.swt.graphics.GC)
	 */
	public void drawArea(final UserInterface graphics) {
		final Color background = graphics.getBackground();
		final Color foreground = graphics.getForeground();

		final Rectangle oldClip = graphics.getClipping();

		this.calculateDimensions(graphics);

		Title axisTitle = this.chartProperties.getYAxisTitle();
		if ((axisTitle != null) && this.chartProperties.getShowYAxisTitle().booleanValue()) {
			graphics.setClipping(this.yAxisTitleRect);
			axisTitle.setBackgroundColor(this.chartProperties.getYAxisTitleBackgroundColour());
			axisTitle.setTextColor(this.chartProperties.getYAxisTitleTextColour());
			axisTitle.draw(graphics, -ANGLE);
		}

		axisTitle = this.chartProperties.getXAxisTitle();
		if ((axisTitle != null) && this.chartProperties.getShowXAxisTitle().booleanValue()) {
			graphics.setClipping(this.xAxisTitleRect);
			axisTitle.setBackgroundColor(this.chartProperties.getXAxisTitleBackgroundColour());
			axisTitle.setTextColor(this.chartProperties.getXAxisTitleTextColour());
			axisTitle.draw(graphics, 0);
		}
		graphics.setBackground(background);
		graphics.setForeground(foreground);
		graphics.setClipping(this.plotRect);
		final Font defaultTickFont = this.chartProperties.getTickFont();
		graphics.setFont(defaultTickFont);

		final String[] minMax = this.getXAxisMinMaxValues(graphics);
		this.shiftYaxisToRHS = Math.max(graphics.stringExtent(minMax[0]).x, graphics.stringExtent(minMax[1]).x)
				+ INSET_FOR_TICK_VALUE_FROM_AXIS;
		this.shiftXAxisAbove = this.getMaxHeight(graphics, defaultTickFont) + INSET_FOR_TICK_VALUE_FROM_TICK_MARK;
		this.shiftYaxisToRHS += INSET_FOR_TICK_VALUE_FROM_TICK_MARK;

		// eachDataWidth = (yCor - (chartTitleRect == null ? 0 :
		// chartTitleRect.height )) / series.getCapacity();
		this.fillArea(graphics);
		this.eachDataWidth = graphics.getFontHeight() * 2 + TICKMARKVALUEINSET - 2;

		// draw Y-axis
		this.drawYAxis(graphics);

		if (!this.scrollable) {
			// draw X-axis
			this.drawXAxis(graphics);

			// draw Series values
			this.drawSeries(graphics);

			this.clearAll();
		}
		graphics.setClipping(oldClip);
	}

	/**
	 * method getMaxBarsAllowed
	 * 
	 * @param chartWidth
	 * @param chartHeight
	 * @return
	 */
	public int getMaxBarsAllowed(final int chartWidth, final int chartHeight) {
		return this.getMaxBarsAllowed(chartWidth, chartHeight, XAxisSeries.TIMEVALUES);
	}

	/**
	 * method getMaxBarsAllowed
	 * 
	 * @param chartWidth
	 * @param chartHeight
	 * @param typeOfXAxisData
	 * @return
	 */
	public int getMaxBarsAllowed(final int chartWidth, final int chartHeight, final int typeOfXAxisData) {
		return this.getMaxBarsAllowed(chartWidth, chartHeight, DEFAULT_MAX_CHARS_ON_YAXIS, typeOfXAxisData);
	}

	/**
	 * Given the width and height dimensions of the chart, this method will
	 * calculate and return, how many bars can be accomodated in the chart (the
	 * maximum number of bars).
	 */
	public int getMaxBarsAllowed(final int chartWidth, final int chartHeight, int maxCharsOnYAxis,
			final int typeOfXAxisData) {
		int iMaxBarsDrawable = 0;
		maxCharsOnYAxis = maxCharsOnYAxis <= 0 ? DEFAULT_MAX_CHARS_ON_YAXIS : maxCharsOnYAxis;

		final UserInterface dummyUserInterface = UIGraphicsFactory
				.getUserInterface(this.getUserInterface().getUserInterfaceType());
		dummyUserInterface.createGraphics(chartWidth, chartHeight);

		this.calculateDimensions(dummyUserInterface, chartWidth, chartHeight);
		dummyUserInterface.setClipping(this.chartAreaRect);
		this.calculateDimensions(dummyUserInterface);
		final Font defaultTickFont = this.chartProperties.getTickFont();
		dummyUserInterface.setFont(defaultTickFont);
		this.setEachBarWidthHHMMSSVariables(dummyUserInterface, typeOfXAxisData);

		int shiftXAxis = this.getMaxHeight(dummyUserInterface, defaultTickFont) + INSET_FOR_TICK_VALUE_FROM_TICK_MARK;
		if (this.yAxisTitleRect != null) {
			shiftXAxis += this.yAxisTitleRect.height;
		}

		iMaxBarsDrawable = (this.plotRect.height - shiftXAxis) / this.iMinWidthReqdDefaultFont;

		dummyUserInterface.disposeGraphics();
		dummyUserInterface.disposeImage();
		return iMaxBarsDrawable;
	}

	/**
	 * method setEachBarWidthHHMMSSVariables
	 * 
	 * @param chartGraphics
	 * @param typeOfXAxisData
	 */
	private void setEachBarWidthHHMMSSVariables(final UserInterface chartGraphics, final int typeOfXAxisData) {
		this.iMinWidthReqdDefaultFont = chartGraphics.getFontHeight() * 2 + TICKMARKVALUEINSET;
	}

	protected void drawXAxis(final UserInterface graphics) {
		final Color background = graphics.getBackground();
		final Color foreground = graphics.getForeground();
		final int Y_COR_OF_TOP_BORDER = this.plotRect.y;

		final XAxisSeries series = this.getXAxisSeries();

		final int startXpx = this.plotRect.x + this.shiftYaxisToRHS;
		final int endXpx = this.plotRect.x + this.plotRect.width;
		// int yCor = plotRect.y + plotRect.height - shiftXAxisAbove;
		final int yCor = this.plotRect.y + this.plotRect.height - this.shiftXAxisAbove;
		final int startXpxOnY = this.plotRect.y;

		this.insetForBarOnOneside = this.eachDataWidth / 4; // 25% space wud
		// be left at
		// one side of
		// the bar.

		final Font defaultTickFont = this.chartProperties.getTickFont();
		graphics.setFont(defaultTickFont);
		this.iShiftYAxisAbove = 0;

		this.xStart = startXpx;

		// draw Ticks
		String strTickValue = null;
		float tickValueLHSx;

		int currTickXOnY;
		int nextTickXOnY;

		for (int indexOfSeries = 0; indexOfSeries < series.getCapacity() + 1; indexOfSeries++) {
			currTickXOnY = startXpxOnY + this.eachDataWidth * indexOfSeries;
			nextTickXOnY = startXpxOnY + this.eachDataWidth * (indexOfSeries + 1);
			if (currTickXOnY > (this.plotRect.height - this.shiftXAxisAbove - 5)) {
				break;
			}

			if (this.isXAxisGrid()) {
				// Draws the grid lines vertical to the X - Axis
				graphics.setForeground(ChartConstants.GRID_COLOR);
				final LineAttributes oldLine = graphics.getLineStyle();
				graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
				graphics.drawLine(this.plotRect.x + 1, currTickXOnY, this.plotRect.x + this.plotRect.width - 2,
						currTickXOnY);
				graphics.setLineStyle(oldLine);
			}
			// boolean bDrawTickMark = true;

			// Decide the tick value, and write in center aligned.
			if (indexOfSeries != series.getCapacity()) {
				tickValueLHSx = currTickXOnY;
				if (tickValueLHSx < this.plotRect.y) {
					tickValueLHSx = this.plotRect.y + this.eachDataWidth * indexOfSeries - TICKMARK;
				}

				if (((series != null) && (series.getFormattedValue(indexOfSeries).equals(String.valueOf(Long.MIN_VALUE))
						|| ((series != null)
								&& series.getFormattedValue(indexOfSeries).equals(IProductConstants.EMPTY_STRING))
						|| (series.getFormattedValue(indexOfSeries).charAt(0) == '-')))) {
					// do nothing
				} else {
					// Draw Tick marks on the X Axis
					strTickValue = series.getFormattedValue(indexOfSeries);

					// Drawing the Tick values " Center Aligned ".
					Font oldFont = graphics.getFont();
					graphics.setFont(defaultTickFont);
					final Point point = graphics.stringExtent(strTickValue);
					graphics.setFont(oldFont);

					final float freeSpace = (nextTickXOnY - tickValueLHSx) - point.y;
					final float leaveSpaceFromDrawX = freeSpace / 2;

					oldFont = graphics.getFont();
					graphics.setFont(defaultTickFont);
					if ((indexOfSeries != series.getCapacity()) || !(series instanceof XAxisTimeSeries)) {
						graphics.setBackground(this.chartProperties.getTickBackgroundColour());
						graphics.setForeground(this.chartProperties.getTickTextColour());
						graphics.drawString(String.valueOf(strTickValue),
								(startXpx - point.x - INSET_FOR_TICK_VALUE_FROM_AXIS),
								(int) (currTickXOnY + leaveSpaceFromDrawX), true);
					}
					graphics.setFont(oldFont);
				}
			}
			/*
			 * else if ( ( xAxisSeriesLongValues != null &&
			 * (xAxisSeriesLongValues.getFormattedValue(series.getCapacity()-1).
			 * equals(String.valueOf(Long.MIN_VALUE)) ||
			 * xAxisSeriesLongValues.getFormattedValue(series.getCapacity()-1).
			 * startsWith("-")) ) ||(xAxisSeriesStringValues != null &&
			 * xAxisSeriesStringValues.getFormattedValue(series.getCapacity()-1)
			 * .equals("")) ) { bDrawTickMark = false; }
			 */

			/*
			 * if(bDrawTickMark && currTickXOnY != Y_COR_OF_TOP_BORDER) { //
			 * Draw tick marks of X axis on Y axis. if(indexOfSeries != 0) {
			 * graphics.drawLine(startXpx - TICK_MARK_LENGTH, currTickXOnY,
			 * startXpx, currTickXOnY); } }
			 */
		}

		graphics.setBackground(background);
		graphics.setForeground(foreground);

		// draw X-Axis line
		final LineAttributes oldLine = graphics.getLineStyle();
		graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
		graphics.drawLine(startXpx, yCor, startXpx, 0);
		graphics.setLineStyle(oldLine);
		// Drawing surrounding line (TopMost Border line)
		graphics.drawLine(startXpx, Y_COR_OF_TOP_BORDER, endXpx, Y_COR_OF_TOP_BORDER);
	}

	protected void drawXAxis(final UserInterface graphics, final int originY, final int width, final int originHt) {
		final Color background = graphics.getBackground();
		final Color foreground = graphics.getForeground();
		final int Y_COR_OF_TOP_BORDER = originY;
		final int maximum = originY + originHt;

		final XAxisSeries series = this.getXAxisSeries();
		int count = series.getCurrentCount();

		final int startXpx = this.shiftYaxisToRHS;

		final int endXpx = width;
		// int yCor = plotRect.y + plotRect.height - shiftXAxisAbove;
		final int yCor = maximum;

		this.insetForBarOnOneside = this.eachDataWidth / 4; // 25% space wud
		// be left at
		// one side of
		// the bar.

		final Font defaultTickFont = this.chartProperties.getTickFont();
		graphics.setFont(defaultTickFont);
		this.iShiftYAxisAbove = 0;

		this.xStart = startXpx;

		if (this.bShowAlternateBackground) {
			this.showAlternateBackgroundColors(graphics, 0, maximum, this.eachDataWidth,
					this.chartProperties.isStretchAlternateBackground().booleanValue() ? -1 : this.shiftYaxisToRHS,
					width);
		}
		if (this.isYAxisGrid()) {
			this.drawYAxisGrids(graphics, startXpx, width, originY, maximum);
		}

		// draw Ticks
		String strTickValue = null;

		int currTickXOnY;

		for (int indexOfSeries = 0; indexOfSeries < count; indexOfSeries++) {
			currTickXOnY = this.eachDataWidth * indexOfSeries;
			if (this.isXAxisGrid() && (currTickXOnY >= originY)) {
				// Draws the grid lines vertical to the X - Axis
				graphics.setForeground(ChartConstants.GRID_COLOR);
				graphics.setBackground(background);
				final LineAttributes oldLine = graphics.getLineStyle();
				graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
				graphics.drawLine(0, currTickXOnY, width - 2, currTickXOnY);
				graphics.setLineStyle(oldLine);
			}

			if (((series != null) && (series.getFormattedValue(indexOfSeries).equals(String.valueOf(Long.MIN_VALUE))
					|| (series.getFormattedValue(indexOfSeries).charAt(0) == '-')))
					|| ((series != null)
							&& series.getFormattedValue(indexOfSeries).equals(IProductConstants.EMPTY_STRING))) {
				// do nothing
			} else {
				// Draw Tick marks on the X Axis
				strTickValue = series.getFormattedValue(indexOfSeries);

				// Drawing the Tick values " Center Aligned ".
				Font oldFont = graphics.getFont();
				graphics.setFont(defaultTickFont);
				graphics.setFont(oldFont);

				oldFont = graphics.getFont();
				graphics.setFont(defaultTickFont);
				if ((indexOfSeries != count) || !(series instanceof XAxisTimeSeries)) {
					graphics.setBackground(this.chartProperties.getTickBackgroundColour());
					graphics.setForeground(this.chartProperties.getTickTextColour());
					graphics.drawString(String.valueOf(strTickValue), INSET_FOR_TICK_VALUE_FROM_AXIS,
							currTickXOnY + this.eachDataWidth / 4, true);
				}
				graphics.setFont(oldFont);
			}
		}

		graphics.setBackground(background);
		graphics.setForeground(foreground);

		// draw X-Axis line
		final LineAttributes oldLine = graphics.getLineStyle();
		graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
		graphics.drawLine(startXpx, yCor, startXpx, 0);
		graphics.setLineStyle(oldLine);
		// Drawing surrounding line (TopMost Border line)
		graphics.drawLine(startXpx, Y_COR_OF_TOP_BORDER, endXpx, Y_COR_OF_TOP_BORDER);
	}

	/**
	 * @see com.queryio.sysmoncommon.charts.swt.AppSimpleChart#drawYAxis(org.eclipse.swt.graphics.GC)
	 */
	protected final void drawYAxis(final UserInterface graphics) {
		// Color background = graphics.getBackground();
		final Color foreground = graphics.getForeground();

		final int X_COR_OF_RHS_BORDER = this.plotRect.x + this.plotRect.width - 1;

		final int ht = this.plotRect.height - this.shiftXAxisAbove;
		final int yCor = this.plotRect.y + ht;
		this.yAxisRect = new Rectangle(this.plotRect.x, yCor, this.plotRect.width + 1, yCor - ht - 1);

		if (this.bShowAlternateBackground && !this.scrollable) {
			this.showAlternateBackgroundColors(graphics, this.plotRect.y,
					this.plotRect.height - this.shiftXAxisAbove - INSET_FOR_TICK_VALUE_FROM_AXIS,
					this.eachDataWidth, this.chartProperties.isStretchAlternateBackground().booleanValue()
							? (this.plotRect.x + 1) : (this.plotRect.x + this.shiftYaxisToRHS),
					this.plotRect.x + this.plotRect.width);
		}

		final int INSET = 1;
		if (this.isYAxisGrid() && !this.scrollable) {
			this.drawYAxisGrids(graphics, this.plotRect.x + this.shiftYaxisToRHS, this.plotRect.width,
					this.plotRect.y - INSET, yCor);
		}

		final int minMax[] = this.getYAxisMinMaxValues();
		graphics.setFont(this.chartProperties.getTickFont());

		final int startYpx = this.plotRect.y - INSET;
		final int xCor = this.plotRect.x + this.shiftYaxisToRHS;
		final int availablePxs = ((this.plotRect.width - 1) - this.shiftYaxisToRHS) - (this.scrollable ? 17 : 0);

		int startYval = this.bMinMaxYValueSet ? minMax[0] : (minMax[0] + (minMax[0] == 0 ? 0 : -1));
		int endYval = this.bMinMaxYValueSet ? minMax[1] : (minMax[1] + 1);

		final int factor = (endYval - startYval > 5 ? 5 : 1);

		if (!this.bMinMaxYValueSet) {
			if (startYval % factor != 0) {
				startYval -= (startYval % factor);
			}

			if (endYval % factor != 0) {
				endYval += (factor - endYval % factor);
			}
		}

		/*
		 * the count of ticks that can be shown on Y axis with provided drawing
		 * area
		 */
		int maxWidthOfOneTickValue = graphics.stringExtent(IProductConstants.EMPTY_STRING + minMax[1]).x;
		maxWidthOfOneTickValue += (maxWidthOfOneTickValue / 2) + 2;
		int tickCount = availablePxs / maxWidthOfOneTickValue;
		if (tickCount <= 0) {
			tickCount = 1;
		}

		if (this.bMinMaxYValueSet) {
			/*
			 * find the tickCount which will divide (endYval - startYval)
			 * exactly
			 */
			for (int i = tickCount; i >= 1; i--) {
				if ((endYval - startYval) % i == 0) {
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
		if (tickInterval == 0) {
			tickInterval = 1;
		}
		if (!this.bMinMaxYValueSet && (tickInterval % factor != 0)) {
			// convert tick interval into multiple of factor
			tickInterval += (factor - tickInterval % factor);
		}
		if (!this.bMinMaxYValueSet) {
			tickCount = Math.round(Math.abs(endYval - startYval) / tickInterval) + 1;
		}

		final int edw = availablePxs / tickCount;

		this.yStart = startYpx;
		this.yStartValue = startYval;
		this.ySlope = (float) edw / tickInterval;

		int currYPos = this.plotRect.x + this.shiftYaxisToRHS;
		String value = null;
		for (int i = 0; i < tickCount + 1; i++) {
			// if (isYAxisGrid() && (i != 0))
			// {
			// oldColor = graphics.getForeground();
			// graphics.setForeground(ChartConstants.GRID_COLOR);
			// // do not show dotted-line for horizontal charts.
			// //int oldLine = graphics.getLineStyle();
			// //graphics.setLineStyle(UserInterface.LINE_STYLE_DOT);
			// graphics.drawLine(currYPos, plotRect.y + 1, currYPos, yCor);
			// //graphics.setLineStyle(oldLine);
			// graphics.setForeground(oldColor);
			// }
			graphics.setBackground(this.chartProperties.getTickBackgroundColour());
			graphics.setForeground(this.chartProperties.getTickTextColour());
			if ((i != 0) && (currYPos != X_COR_OF_RHS_BORDER)) {
				graphics.drawLine(currYPos, yCor, currYPos, yCor + TICK_MARK_LENGTH);
			}
			value = ChartConstants.format(startYval + i * tickInterval);
			if (this.scrollable || (i != tickCount)) {
				graphics.drawString(value, currYPos - graphics.stringExtent(value).x / 2,
						yCor + INSET_FOR_TICK_VALUE_FROM_TICK_MARK, true);
			} else {
				graphics.drawString(value, currYPos - graphics.stringExtent(value).x,
						yCor + INSET_FOR_TICK_VALUE_FROM_TICK_MARK, true);
			}
			currYPos += edw;
		}
		graphics.setBackground(ChartConstants.COLOR_BLACK);
		graphics.setForeground(foreground);

		// draw Y-Axis line
		final LineAttributes oldLine = graphics.getLineStyle();
		graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
		graphics.drawLine(xCor, yCor, xCor + this.plotRect.width, yCor);
		graphics.setLineStyle(oldLine);

		// Drawing surrounding line (RHS Border line)
		graphics.drawLine(X_COR_OF_RHS_BORDER, yCor, X_COR_OF_RHS_BORDER, 0);
	}

	private void drawYAxisGrids(final UserInterface graphics, final int startXpx, final int endXpx, final int startY,
			final int endY) {
		final int minMax[] = this.getYAxisMinMaxValues();
		graphics.setFont(this.chartProperties.getTickFont());

		final int availablePxs = ((endXpx - 1) - this.shiftYaxisToRHS);

		int startYval = this.bMinMaxYValueSet ? minMax[0] : (minMax[0] + (minMax[0] == 0 ? 0 : -1));
		int endYval = this.bMinMaxYValueSet ? minMax[1] : (minMax[1] + 1);

		final int factor = (endYval - startYval > 5 ? 5 : 1);

		if (!this.bMinMaxYValueSet) {
			if (startYval % factor != 0) {
				startYval -= (startYval % factor);
			}

			if (endYval % factor != 0) {
				endYval += (factor - endYval % factor);
			}
		}

		/*
		 * the count of ticks that can be shown on Y axis with provided drawing
		 * area
		 */
		int maxWidthOfOneTickValue = graphics.stringExtent(IProductConstants.EMPTY_STRING + minMax[1]).x;
		maxWidthOfOneTickValue += (maxWidthOfOneTickValue / 2) + 2;
		int tickCount = availablePxs / maxWidthOfOneTickValue;
		if (tickCount <= 0) {
			tickCount = 1;
		}

		if (this.bMinMaxYValueSet) {
			/*
			 * find the tickCount which will divide (endYval - startYval)
			 * exactly
			 */
			for (int i = tickCount; i >= 1; i--) {
				if ((endYval - startYval) % i == 0) {
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
		if (tickInterval == 0) {
			tickInterval = 1;
		}
		if (!this.bMinMaxYValueSet && (tickInterval % factor != 0)) {
			// convert tick interval into multiple of factor
			tickInterval += (factor - tickInterval % factor);
		}
		if (!this.bMinMaxYValueSet) {
			tickCount = Math.round(Math.abs(endYval - startYval) / tickInterval) + 1;
		}
		final int edw = availablePxs / tickCount;

		int currYPos = startXpx;
		Color oldColor;
		for (int i = 0; i < tickCount + 1; i++) {
			if (i != 0) {
				oldColor = graphics.getForeground();
				graphics.setForeground(ChartConstants.GRID_COLOR);
				// do not show dotted-line for horizontal charts.
				// int oldLine = graphics.getLineStyle();
				// graphics.setLineStyle(UserInterface.LINE_STYLE_DOT);
				final LineAttributes oldLine = graphics.getLineStyle();
				graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
				graphics.drawLine(currYPos, startY + 1, currYPos, endY);
				graphics.setLineStyle(oldLine);
				// graphics.setLineStyle(oldLine);
				graphics.setForeground(oldColor);
			}
			currYPos += edw;
		}
	}

	protected void showAlternateBackgroundColors(final UserInterface graphics, final int startYPosition,
			final int availablePixels, final int eachDataWidth, final int startXPosition, final int endXPosition) {
		// if (chartTitleRect != null)
		// {
		// availablePixels -= chartTitleRect.height;
		// }

		final Color oldBackGroundColor = graphics.getBackground();
		int currYPosition = startYPosition;
		final int endYPosition = startYPosition + availablePixels;
		final int iRectanglesWidth = endXPosition - startXPosition - 1;
		int iRectanglesHeight = eachDataWidth;
		int addtion = 0;
		final Color arrAlternateColors[] = new Color[] { this.getAreaColor(),
				this.chartProperties.getAlternateBackgroundColour() };
		for (boolean first = true; currYPosition < endYPosition; currYPosition += eachDataWidth, first = !first) {
			if (endYPosition - currYPosition < eachDataWidth) {
				iRectanglesHeight = (endYPosition - currYPosition) + 2;
			}
			// iRectanglesHeight = Math.min(eachDataWidth, endYPosition -
			// currYPosition);
			graphics.setBackground(arrAlternateColors[first ? 0 : 1]);
			addtion = (!this.isXAxisGrid() && (currYPosition + eachDataWidth < endYPosition)) ? 0 : 1;
			graphics.fillRectangle(startXPosition, currYPosition + addtion, iRectanglesWidth,
					iRectanglesHeight + addtion);
		}
		graphics.setBackground(oldBackGroundColor);
	}

	protected int resolveYCorordinate(final long value) {
		final float retVal = this.yStart + this.ySlope * (value - this.yStartValue);
		return (int) retVal;
	}
}
