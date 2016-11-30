/*
 * @(#)  AppAbstractBarChart.java Feb 7, 2005
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
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.charts.series.XAxisSeries;
import com.queryio.common.charts.series.XAxisStringSeries;
import com.queryio.common.charts.util.TimeValueFormatter;
import com.queryio.common.charts.util.UIGraphicsFactory;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.LineAttributes;
import com.queryio.common.exporter.dstruct.Point;

/**
 * 
 * @author Exceed Consultancy Services
 */
public abstract class AppAbstractBarChart extends AppVerticalSimpleChart {
	private static final int TICKMARK = 1;
	private static final int TICKMARKVALUEINSET = 2;
	private static final int DEFAULT_MAX_CHARS_ON_YAXIS = 3;
	protected int insetForBarOnOneside = 2;

	private int iMinWidthReqdDefaultFont = 0;
	private int iMinWidthReqd8Font = 0;

	public AppAbstractBarChart(final UserInterface userInterface, final int nodeType, final int productID) {
		super(userInterface, nodeType, productID);
	}

	public int getMaxBarsAllowed(final int chartWidth, final int chartHeight) {
		return this.getMaxBarsAllowed(chartWidth, chartHeight, XAxisSeries.TIMEVALUES);
	}

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
		dummyUserInterface.setFont(this.chartProperties.getTickFont());
		this.setEachBarWidthHHMMSSVariables(dummyUserInterface, typeOfXAxisData);

		final int dummyYAxisValue = (int) Math.pow(10, maxCharsOnYAxis - 1);

		final int shiftXAxis = dummyUserInterface.stringExtent(ChartConstants.format(dummyYAxisValue)).x
				+ dummyUserInterface.stringExtent(ChartConstants.format((dummyYAxisValue + 1) % 10)).x;
		iMaxBarsDrawable = (this.plotRect.width - shiftXAxis) / this.iMinWidthReqdDefaultFont;

		dummyUserInterface.disposeGraphics();
		dummyUserInterface.disposeImage();

		return iMaxBarsDrawable;
	}

	public void initializeSeries(final int noOfYAxis, final int capacity) {
		this.initializeSeries(noOfYAxis, capacity, XAxisSeries.TIMEVALUES);
	}

	public void initializeSeries(final int noOfYAxis, final int capacity, final int typeOfXAxisData) {
		super.initializeSeries(noOfYAxis, capacity, typeOfXAxisData);
		if (this.iMinWidthReqd8Font == 0) {
			final UserInterface dummyUserInterface = UIGraphicsFactory
					.getUserInterface(this.getUserInterface().getUserInterfaceType());
			dummyUserInterface.createGraphics(200, 200);
			this.setEachBarWidthHHMMSSVariables(dummyUserInterface, typeOfXAxisData);
			dummyUserInterface.disposeGraphics();
			dummyUserInterface.disposeImage();
		}
	}

	private void setEachBarWidthHHMMSSVariables(final UserInterface chartGraphics, final int typeOfXAxisData) {
		String maxXAxisValue = null;
		switch (typeOfXAxisData) {
		case XAxisSeries.INTVALUES: {
			maxXAxisValue = String.valueOf(Integer.MAX_VALUE);
			break;
		}
		case XAxisSeries.LONGVALUES: {
			maxXAxisValue = String.valueOf(Long.MAX_VALUE);
			break;
		}
		case XAxisSeries.STRINGVALUES: {
			maxXAxisValue = XAxisStringSeries.DUMMYMAXVALUE;
			break;
		}
		case XAxisSeries.TIMEVALUES: // $IGN_Use_break_for_each_case$
		default: {
			maxXAxisValue = TimeValueFormatter.getDummyMaxTimeValue();
			break;
		}
		}

		chartGraphics.setFont(this.chartProperties.getTickFont());
		this.iMinWidthReqdDefaultFont = chartGraphics.stringExtent(maxXAxisValue + TICKMARKVALUEINSET).x;

		chartGraphics.setFont(this.chartProperties.getTickFont());
		this.iMinWidthReqd8Font = chartGraphics.stringExtent(maxXAxisValue + TICKMARKVALUEINSET).x;
	}

	public abstract void drawSeries(UserInterface graphics);

	protected void drawXAxis(final UserInterface graphics) {
		Color oldColor;
		final XAxisSeries series = this.getXAxisSeries();

		final int startXpx = this.plotRect.x + this.shiftYaxisToRHS;
		final int endXpx = this.plotRect.x + this.plotRect.width;
		int yCor = this.plotRect.y + this.plotRect.height - this.shiftXAxisAbove;

		this.eachDataWidth = (this.plotRect.x + this.plotRect.width - startXpx) / series.getCapacity();
		this.insetForBarOnOneside = this.eachDataWidth / 4; // 25% space wud
		// be left at
		// one side of
		// the bar.

		graphics.setFont(this.chartProperties.getTickFont());

		final int FONT_HEIGHT = graphics.getFontHeight();
		final String maxValue = series.getFormattedMaxValue();
		int iTickValueWidth = graphics.stringExtent(maxValue + TICKMARKVALUEINSET).x;

		if ((this.eachDataWidth < this.iMinWidthReqd8Font)
				&& (this.eachDataWidth >= FONT_HEIGHT + TICKMARKVALUEINSET)) {
			iTickValueWidth = graphics.stringExtent(maxValue + TICKMARKVALUEINSET).y;
			yCor -= iTickValueWidth;
			this.iShiftYAxisAbove = iTickValueWidth;
		} else {
			this.iShiftYAxisAbove = 0;
		}
		this.fillArea(graphics);

		// draw X-Axis line
		LineAttributes oldLine = graphics.getLineStyle();
		graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND,
				LineAttributes.LINE_STYLE_SOLID));
		graphics.drawLine(startXpx, yCor, endXpx, yCor);
		graphics.setLineStyle(oldLine);

		oldColor = graphics.getForeground();
		graphics.setForeground(ChartConstants.COLOR_DARK_GRAY);
		// draw surrounding line (top most grid line, along x axis)
		oldLine = graphics.getLineStyle();
		graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND,
				LineAttributes.LINE_STYLE_SOLID));
		graphics.drawLine(startXpx, this.plotRect.y, endXpx, this.plotRect.y);
		graphics.setLineStyle(oldLine);
		graphics.setForeground(oldColor);

		this.xStart = startXpx;
		// draw Ticks
		String strTickValue = null;
		float tickValueLHSx;

		int currTickX;
		int nextTickX;

		for (int i = 0; i < series.getCapacity() + 1; i++) {
			currTickX = startXpx + this.eachDataWidth * i;
			nextTickX = startXpx + this.eachDataWidth * (i + 1);

			if (this.isXAxisGrid()) {
				// Draws the grid lines vertical to the X - Axis
				oldColor = graphics.getForeground();
				graphics.setForeground(ChartConstants.GRID_COLOR);
				oldLine = graphics.getLineStyle();
				graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND,
						LineAttributes.LINE_STYLE_SOLID));
				graphics.drawLine(currTickX, this.plotRect.y, currTickX, yCor);
				graphics.setLineStyle(oldLine);
				graphics.setForeground(oldColor);
			}

			boolean bDrawTickMark = true;

			// Decide the tick value, and write in center aligned.
			if (i != series.getCapacity()) {
				tickValueLHSx = currTickX;
				if (tickValueLHSx < this.plotRect.x) {
					tickValueLHSx = this.plotRect.x + this.eachDataWidth * i - TICKMARK;
				}

				if (((series != null) && (series.getFormattedValue(i).equals(String.valueOf(Long.MIN_VALUE))
						|| ((series.getFormattedValue(i).length() != 0)
								&& (series.getFormattedValue(i).charAt(0) == '-'))))
						|| ((series != null) && series.getFormattedValue(i).equals(IProductConstants.EMPTY_STRING))) {

					if ((i == 0) || (((series != null)
							&& (series.getFormattedValue(i - 1).equals(String.valueOf(Long.MIN_VALUE))
									|| (series.getFormattedValue(i - 1).charAt(0) == '-')))
							|| ((series != null)
									&& series.getFormattedValue(i - 1).equals(IProductConstants.EMPTY_STRING)))) {
						bDrawTickMark = false;
					}
				} else {

					// Draw Tick marks on the X Axis
					strTickValue = series.getFormattedValue(i);

					// Drawing the Tick values " Center Aligned ".
					final Point point = graphics.stringExtent(strTickValue);
					final float freeSpace = (nextTickX - tickValueLHSx) - point.x;
					float leaveSpaceFromDrawX = freeSpace / 2;

					if (this.eachDataWidth < this.iMinWidthReqd8Font) {
						if ((nextTickX - currTickX) >= FONT_HEIGHT + TICKMARKVALUEINSET) {
							leaveSpaceFromDrawX = ((nextTickX - tickValueLHSx) - point.y) / 2;
							graphics.drawStringVertically(String.valueOf(strTickValue),
									(int) (tickValueLHSx + leaveSpaceFromDrawX),
									(int) (yCor + this.shiftXAxisAbove * 0.30f));
						}
					} else {
						// if(i != 0 || !(series instanceof XAxisTimeSeries))
						// {
						graphics.drawString(String.valueOf(strTickValue), (int) (tickValueLHSx + leaveSpaceFromDrawX),
								yCor + INSET_FOR_TICK_VALUE_FROM_AXIS, true);
						// }
					}
				}
			} else if (((series != null)
					&& (series.getFormattedValue(series.getCapacity() - 1).equals(String.valueOf(Long.MIN_VALUE))
							|| (((series.getFormattedValue(i).length() != 0)
									&& series.getFormattedValue(series.getCapacity() - 1).charAt(0) == '-'))))
					|| ((series != null) && series.getFormattedValue(series.getCapacity() - 1)
							.equals(IProductConstants.EMPTY_STRING))) {
				bDrawTickMark = false;
			}

			if (bDrawTickMark) {
				// Draw tick marks on X axis.
				if (i != 0) {
					oldLine = graphics.getLineStyle();
					graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND,
							LineAttributes.LINE_STYLE_SOLID));
					graphics.drawLine(currTickX, yCor, currTickX, yCor + TICK_MARK_LENGTH);
					graphics.setLineStyle(oldLine);
				}
			}
		}
	}

}
