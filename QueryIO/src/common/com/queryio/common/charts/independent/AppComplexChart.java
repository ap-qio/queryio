/*
 * @(#)  AppComplexChart.java Feb 7, 2005
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

import com.queryio.common.charts.components.Legend;
import com.queryio.common.charts.components.MarkerObject;
import com.queryio.common.charts.components.Title;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.charts.properties.ChartPropertiesManager;
import com.queryio.common.charts.series.ComplexSeries;
import com.queryio.common.charts.series.SeriesCollection;
import com.queryio.common.charts.series.StartEndSeries;
import com.queryio.common.charts.util.TimeValueFormatter;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Font;
import com.queryio.common.exporter.dstruct.LineAttributes;
import com.queryio.common.exporter.dstruct.Rectangle;
import com.queryio.common.util.PlatformHandler;

/**
 * 
 * @author Exceed Consultancy Services
 */
public class AppComplexChart extends AppChart implements IScrollableChart {
	private static final double ANGLE = Math.PI * 0.5;

	private Rectangle yAxisRect = null;

	private Rectangle xAxisTitleRect = null;

	private Rectangle yAxisTitleRect = null;

	private Legend[] legends = null;

	protected Rectangle plotRect = null;

	private SeriesCollection collection = null;

	private int shiftYaxisToRHS = -1;
	private int shiftXAxisAbove = 0;
	int barht = 0;

	protected int yStart = 0;
	private long yStartValue = 0;
	private float ySlope = 0;

	private long iMinYValue;
	private long iMaxYValue;
	protected boolean bMinMaxYValueSet;

	private boolean scrollable = false;

	private void init() {
		// // configure X-Axis
		// if ( chartProperties.getXAxisTitle() == null )
		// {
		// if (chartProperties.getXAxisTitleFont() == null)
		// {
		// chartProperties.setXAxisTitleFont(chartProperties.getAxisFont());
		// }
		// }
		//
		//
		// // configure Y-Axis
		// if ( chartProperties.getYAxisTitle() == null )
		// {
		// if (chartProperties.getYAxisTitleFont() == null)
		// {
		// chartProperties.setYAxisTitleFont(chartProperties.getAxisFont());
		// }
		// }

		this.bShowAlternateBackground = true;
		if (this.chartProperties.isStretchAlternateBackground() == null) {
			this.chartProperties.setStretchAlternateBackground(true);
		}
		final Color backgroundColour = this.chartProperties.getBackgroundColour();
		this.chartProperties.setTickBackgroundColour(backgroundColour);
		if (this.chartProperties.getXAxisTitleBackgroundColour() == null) {
			this.chartProperties.setXAxisTitleBackgroundColour(backgroundColour);
		}
		if (this.chartProperties.getYAxisTitleBackgroundColour() == null) {
			this.chartProperties.setYAxisTitleBackgroundColour(backgroundColour);
		}
		if (this.chartProperties.getChartTitleBackgroundColour() == null) {
			this.chartProperties.setChartTitleBackgroundColour(backgroundColour);
		}
		if (this.chartProperties.getTickTextColour() == null) {
			this.chartProperties.setTickTextColour(ChartConstants.COLOR_BLACK);
		}
	}

	/**
	 * @param productID
	 * 
	 */
	public AppComplexChart(final UserInterface userInterface, final int nodeType, final int productID) {
		this(userInterface, -1, nodeType, productID);
	}

	/**
	 * @param productID
	 * 
	 */
	public AppComplexChart(final UserInterface userInterface, final int initialCapacity, final int nodeType,
			final int productID) {
		this(userInterface, initialCapacity, -1, nodeType, productID);
	}

	/**
	 * @param productID
	 * 
	 */
	public AppComplexChart(final UserInterface userInterface, final int intialCapacity, final int increment,
			final int nodeType, final int productID) {
		super(userInterface, nodeType, productID);
		if (intialCapacity == -1) {
			this.collection = new SeriesCollection();
		} else if (increment == -1) {
			this.collection = new SeriesCollection(intialCapacity);
		} else {
			this.collection = new SeriesCollection(intialCapacity, increment);
		}
		this.init();
	}

	public Rectangle getYAxisTitleBounds() {
		return this.yAxisTitleRect;
	}

	public Rectangle getYAxisBounds() {
		return this.yAxisRect;
	}

	public Rectangle getXAxisTitleBounds() {
		return this.xAxisTitleRect;
	}

	public Rectangle getSeriesPlotBounds() {
		return new Rectangle(this.plotRect.x, this.plotRect.y, this.plotRect.width + 1,
				this.plotRect.height - INSET_FOR_TICK_VALUE_FROM_TICK_MARK + 2);
	}

	public int getTotalPlotHeight() {
		final int noOfComplexSeries = this.collection.getCurrentCount();
		int tickCount = noOfComplexSeries;
		if (tickCount == 0) {
			tickCount = 1;
		}
		return this.barht * tickCount;
	}

	public int getTotalChartHeight(UserInterface graphics, int width, int height) {
		calculateDimensions(graphics, width, height);
		final int noOfComplexSeries = this.collection.getCurrentCount();
		int tickCount = noOfComplexSeries;
		if (tickCount == 0) {
			tickCount = 1;
		}
		final Font defaultTickFont = this.chartProperties.getTickFont();
		final int maxHt = getMaxHeight(graphics, defaultTickFont);
		int newHeight = (2 * maxHt + INSET_FOR_TICK_VALUE_FROM_TICK_MARK) * tickCount;
		if (newHeight > chartAreaRect.height) {
			height += newHeight - chartAreaRect.height;
		}
		return height;
	}
	// /**
	// *
	// * It will return the no of SeriesCollections that can be accomodated in
	// the given rectangular area.
	// * @param width
	// * @param height
	// * @return
	// */
	// public int getVisibleCollectionCount(int width, int height)
	// {
	// UserInterface scratchUserInterface =
	// getUserInterface().getScratchUserInterface();
	// scratchUserInterface.createGraphics(width, height);
	// calculateDimensions(scratchUserInterface, width, height);
	// scratchUserInterface.setClipping(chartAreaRect);
	// calculateDimensions(scratchUserInterface);
	// calculateShiftOfXAxis(scratchUserInterface);
	// int visibleCount = 0;
	// int yCor = plotRect.y + shiftXAxisAbove;// + shiftXAxisAbove/2;
	// while(yCor + shiftXAxisAbove <= plotRect.y + plotRect.height)
	// {
	// yCor += shiftXAxisAbove;
	// visibleCount ++;
	// }
	// scratchUserInterface.disposeImage();
	// scratchUserInterface.disposeGraphics();
	// return visibleCount;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.charts.AppChart#drawArea(GC)
	 */
	public void drawArea(final UserInterface graphics) {
		final Color background = graphics.getBackground();
		final Color foreground = graphics.getForeground();
		final Rectangle oldClip = graphics.getClipping();

		this.calculateDimensions(graphics);

		final Font axisFont = this.chartProperties.getAxisFont();
		if (this.isXAxisTitle()) {
			graphics.setClipping(this.xAxisTitleRect);
			final Title axisTitle = this.chartProperties.getXAxisTitle();
			if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
					|| (this.chartProperties.getXAxisTitleFont() == null)) {
				this.chartProperties.setXAxisTitleFont(axisFont);
			}
			if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
					|| (this.chartProperties.getXAxisTitleBackgroundColour() == null)) {
				this.chartProperties.setXAxisTitleBackgroundColour(this.chartProperties.getBackgroundColour());
			}
			if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
					|| (this.chartProperties.getXAxisTitleTextColour() == null)) {
				this.chartProperties.setXAxisTitleTextColour(ChartConstants.COLOR_BLACK);
			}
			axisTitle.setOrientation(Title.ORIENTATION_VERTICAL);
			axisTitle.setBackgroundColor(this.chartProperties.getXAxisTitleBackgroundColour());
			axisTitle.setTextColor(this.chartProperties.getXAxisTitleTextColour());
			axisTitle.draw(graphics, -ANGLE);
		}

		if (this.isYAxisTitle()) {
			graphics.setClipping(this.yAxisTitleRect);
			final Title axisTitle = this.chartProperties.getYAxisTitle();
			if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
					|| (this.chartProperties.getYAxisTitleFont() == null)) {
				this.chartProperties.setYAxisTitleFont(axisFont);
			}
			if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
					|| (this.chartProperties.getYAxisTitleTextColour() == null)) {
				this.chartProperties.setYAxisTitleTextColour(ChartConstants.COLOR_BLACK);
			}
			if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
					|| (this.chartProperties.getYAxisTitleBackgroundColour() == null)) {
				this.chartProperties.setYAxisTitleBackgroundColour(this.chartProperties.getBackgroundColour());
			}
			axisTitle.setOrientation(Title.ORIENTATION_HORIZONTAL);
			axisTitle.setBackgroundColor(this.chartProperties.getYAxisTitleBackgroundColour());
			axisTitle.setTextColor(this.chartProperties.getYAxisTitleTextColour());
			axisTitle.draw(graphics, 0);
		}
		// if grids are present, drawGrids
		graphics.setClipping(this.plotRect);

		this.calculateShiftsOfXandYAxis(graphics);

		final Color areaColour = this.chartProperties.getAreaColour();
		if (areaColour != null) {
			graphics.setBackground(areaColour);
			graphics.fillRectangle(this.plotRect.x + this.shiftYaxisToRHS, this.plotRect.y,
					this.plotRect.x + this.plotRect.width,
					this.plotRect.height - this.shiftXAxisAbove / 2 - INSET_FOR_TICK_VALUE_FROM_TICK_MARK);
		}

		// draw Y-axis
		this.drawYAxis(graphics);

		if (!this.scrollable) {
			// draw X-Axis
			this.drawXAxis(graphics);

			// draw Series
			this.drawSeries(graphics);

			this.clearAll();
		}

		graphics.setClipping(oldClip);
		graphics.setBackground(background);
		graphics.setForeground(foreground);
	}

	public void drawScrollableArea(final UserInterface graphics, int originY, final int width, final int maximum) {
		final Color background = graphics.getBackground();
		final Color foreground = graphics.getForeground();
		// draw X-Axis
		this.drawXAxis(graphics, originY, width, maximum);

		graphics.setBackground(background);
		graphics.setForeground(foreground);

		// draw Series
		this.drawSeries(graphics, originY);

		// draw surrounding line
		graphics.drawLine(this.shiftYaxisToRHS, -originY, width, -originY);
		graphics.drawLine(width - 17, -originY, width - 17, maximum);
	}

	private void drawXAxis(final UserInterface graphics) {
		final Color background = graphics.getBackground();
		final Color foreground = graphics.getForeground();
		graphics.setFont(this.chartProperties.getTickFont());

		final int startYpx = this.plotRect.y + this.plotRect.height - this.shiftXAxisAbove / 2
				- INSET_FOR_TICK_VALUE_FROM_TICK_MARK;
		final int endYpx = this.plotRect.y;
		final int xCor = this.plotRect.x + this.shiftYaxisToRHS;

		final int noOfComplexSeries = this.collection.getCurrentCount();
		int tickCount = noOfComplexSeries;// * 2;
		if (tickCount == 0) {
			tickCount = 1;
		}

		String value = null;
		int seriesCtr = 0;
		int y = this.plotRect.y;

		for (int i = 0; i < tickCount; i++) {
			if (y + this.barht + this.barht / 2 > this.plotRect.y + this.plotRect.height) {
				break;
			}
			if (seriesCtr < this.collection.getCurrentCount()) {
				value = this.collection.getComplexSeries(seriesCtr).getName();
				// graphics.drawString(value, plotRect.x + shiftYaxisToRHS -
				// graphics.stringExtent(value).x -
				// INSET_FOR_TICK_VALUE_FROM_AXIS, y + barht/4, true);
				graphics.setBackground(this.chartProperties.getTickBackgroundColour());
				graphics.setForeground(this.chartProperties.getTickTextColour());
				graphics.drawString(value, this.plotRect.x + INSET_FOR_TICK_VALUE_FROM_AXIS, y + this.barht / 4, true);
				seriesCtr++;
			}
			y += this.barht;
			if (this.isXAxisGrid()) {
				// int oldLine = graphics.getLineStyle();
				// graphics.setLineStyle(UserInterface.LINE_STYLE_DOT);
				graphics.setBackground(background);
				graphics.setForeground(ChartConstants.GRID_COLOR);
				final LineAttributes oldLine = graphics.getLineStyle();
				graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
				graphics.drawLine(this.plotRect.x + /* shiftYaxisToRHS + */1, y, this.plotRect.x + this.plotRect.width,
						y);
				graphics.setLineStyle(oldLine);
				// graphics.setLineStyle(oldLine);
			}
			// graphics.drawLine(xCor - TICK_MARK_LENGTH, y, xCor, y);
		}

		if (this.isXAxisGrid()) {
			// int oldLine = graphics.getLineStyle();
			// graphics.setLineStyle(UserInterface.LINE_STYLE_DOT);
			graphics.setBackground(background);
			graphics.setForeground(ChartConstants.GRID_COLOR);
			// graphics.setForeground(new Color(255, 0, 0));
			while (y + this.barht + this.barht / 2 < this.plotRect.y + this.plotRect.height) {
				y += this.barht;
				// The following condition is for not over writing the already
				// drawn X-Axis, with the grid line
				if (y == this.plotRect.y + this.plotRect.height - this.shiftXAxisAbove / 2
						- INSET_FOR_TICK_VALUE_FROM_TICK_MARK) {
					break;
				}

				final LineAttributes oldLine = graphics.getLineStyle();
				graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
				graphics.drawLine(this.plotRect.x + this.shiftYaxisToRHS + 1, y, this.plotRect.x + this.plotRect.width,
						y);
				graphics.setLineStyle(oldLine);
			}

			// graphics.setLineStyle(oldLine);
		}
		graphics.setBackground(background);
		graphics.setForeground(foreground);
		// draw X-Axis line
		final LineAttributes oldLine = graphics.getLineStyle();
		graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
		graphics.drawLine(xCor, startYpx, xCor, endYpx);
		graphics.setLineStyle(oldLine);

		// draw surrounding line
		graphics.drawLine(this.plotRect.x + this.plotRect.width - 1, startYpx,
				this.plotRect.x + this.plotRect.width - 1, endYpx);
	}

	private void drawXAxis(final UserInterface graphics, int originY, final int width, final int originHt) {
		final Color background = graphics.getBackground();
		final Color foreground = graphics.getForeground();
		final int maximum = originY + originHt;
		final int xCor = this.shiftYaxisToRHS;
		if (this.bShowAlternateBackground) {
			this.showAlternateBackgroundColors(graphics, 0, maximum, this.barht,
					this.chartProperties.isStretchAlternateBackground().booleanValue() ? -1 : this.shiftYaxisToRHS,
					width);
		}
		graphics.setFont(this.chartProperties.getTickFont());

		// draw Y-Axis grids
		if (this.isYAxisGrid()) {
			this.drawYAxisGrids(graphics, xCor, width, originY, maximum);
		}

		final int noOfComplexSeries = this.collection.getCurrentCount();
		int tickCount = noOfComplexSeries;
		if (tickCount == 0) {
			tickCount = 1;
		}

		String value = null;
		int seriesCtr = 0;
		int y = 0;

		for (int i = 0; i < tickCount; i++) {
			if (seriesCtr < this.collection.getCurrentCount()) {
				if (y >= originY) {
					value = this.collection.getComplexSeries(seriesCtr).getName();
					// graphics.drawString(value, shiftYaxisToRHS -
					// graphics.stringExtent(value).x -
					// INSET_FOR_TICK_VALUE_FROM_AXIS, y + barht/4, true);
					graphics.setBackground(this.chartProperties.getTickBackgroundColour());
					graphics.setForeground(this.chartProperties.getTickTextColour());
					graphics.drawString(value, INSET_FOR_TICK_VALUE_FROM_AXIS, y + this.barht / 4, true);
				}
				seriesCtr++;
			}
			y += this.barht;
			if (this.isXAxisGrid() && (y >= originY)) {
				// int oldLine = graphics.getLineStyle();
				// graphics.setLineStyle(UserInterface.LINE_STYLE_DOT);
				graphics.setBackground(background);
				graphics.setForeground(ChartConstants.GRID_COLOR);
				final LineAttributes oldLine = graphics.getLineStyle();
				graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
				graphics.drawLine(0, y, width, y);
				graphics.setLineStyle(oldLine);
				// graphics.setLineStyle(oldLine);
			}
			// graphics.drawLine(xCor - TICK_MARK_LENGTH, y, xCor, y);
		}

		if (this.isXAxisGrid()) {
			// int oldLine = graphics.getLineStyle();
			// graphics.setLineStyle(UserInterface.LINE_STYLE_DOT);
			graphics.setBackground(background);
			graphics.setForeground(ChartConstants.GRID_COLOR);
			// graphics.setForeground(new Color(255, 0, 0));
			while (y + this.barht + this.barht / 2 < maximum) {
				y += this.barht;
				// The following condition is for not over writing the already
				// drawn X-Axis, with the grid line
				if (y == maximum - this.shiftXAxisAbove / 2 - INSET_FOR_TICK_VALUE_FROM_TICK_MARK) {
					break;
				}
				final LineAttributes oldLine = graphics.getLineStyle();
				graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
				graphics.drawLine(this.shiftYaxisToRHS + 1, y, width, y);
				graphics.setLineStyle(oldLine);
			}

			// graphics.setLineStyle(oldLine);
		}

		graphics.setBackground(background);
		graphics.setForeground(foreground);

		// draw X-Axis line
		final LineAttributes oldLine = graphics.getLineStyle();
		graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
		graphics.drawLine(xCor, -originY, xCor, maximum);
		graphics.setLineStyle(oldLine);
	}

	private void drawSeries(final UserInterface graphics, final int originY) {
		ComplexSeries cmplxSeries = null;
		StartEndSeries seSeries = null;
		int yCor = 0;
		final int width = Math.max(1, this.barht / 3);
		// final int factor = shiftXAxisAbove/2;
		int xStartCor = 0;
		int xEndCor = 0;
		for (int i = 0; i < this.collection.getCurrentCount(); i++) {
			if (yCor >= originY) {
				cmplxSeries = this.collection.getComplexSeries(i);
				for (int j = 0; j < cmplxSeries.getSeriesCount(); j++) {
					if (this.shouldDrawSeries(j)) {
						seSeries = cmplxSeries.getSeries(j);
						graphics.setBackground(getColorForIndex(j, this.chartProperties));
						StringBuffer buffer = new StringBuffer();
						for (int k = 0; k < seSeries.getCurrentCount(); k++) {
							final long startValue = seSeries.getStartValue(k);
							final long endValue = seSeries.getEndValue(k);
							xStartCor = this.resolveCoordinate(startValue);
							xEndCor = this.resolveCoordinate(endValue);

							if (this.bShowToolTip) {
								final Rectangle marker = new Rectangle(xStartCor + 1, yCor + width, xEndCor - xStartCor,
										width + 2);
								final String xVal = seSeries.getText();

								final String yVal = TimeValueFormatter.getFormattedValue(0, (endValue - startValue),
										endValue, true);

								buffer.setLength(0);
								buffer.append("duration: "); //$NON-NLS-1$
								buffer.append(xVal);
								buffer.append(PlatformHandler.LINE_SEPARATOR);
								buffer.append("state: ");//$NON-NLS-1$
								buffer.append(yVal);
								this.addMarkerObject(new MarkerObject(marker, buffer.toString()));
							}

							graphics.fillRectangle(xStartCor + 1, yCor + width, xEndCor - xStartCor, width + 2);
						}
					}
				}
			}
			yCor += this.barht;
		}
	}

	private void drawSeries(final UserInterface graphics) {
		ComplexSeries cmplxSeries = null;
		StartEndSeries seSeries = null;
		int yCor = this.plotRect.y;
		final int width = Math.max(1, this.barht / 3);
		// final int factor = shiftXAxisAbove/2;
		int xStartCor = this.plotRect.x + this.shiftYaxisToRHS;
		int xEndCor = this.plotRect.x + this.shiftYaxisToRHS;
		for (int i = 0; i < this.collection.getCurrentCount(); i++) {
			if (yCor + this.barht + this.barht / 2 > this.plotRect.y + this.plotRect.height) {
				break;
			}
			cmplxSeries = this.collection.getComplexSeries(i);
			StringBuffer buffer = new StringBuffer();
			for (int j = 0; j < cmplxSeries.getSeriesCount(); j++) {
				if (this.shouldDrawSeries(j)) {
					seSeries = cmplxSeries.getSeries(j);
					graphics.setBackground(getColorForIndex(j, this.chartProperties));
					for (int k = 0; k < seSeries.getCurrentCount(); k++) {
						final long startValue = seSeries.getStartValue(k);
						final long endValue = seSeries.getEndValue(k);
						xStartCor = this.resolveCoordinate(startValue);
						xEndCor = this.resolveCoordinate(endValue);

						final Rectangle marker = new Rectangle(xStartCor + 1, yCor + width, xEndCor - xStartCor,
								width + 2);
						final String xVal = seSeries.getText();

						final String yVal = TimeValueFormatter.getFormattedValue(0, (endValue - startValue), endValue,
								true);
						buffer.setLength(0);
						buffer.append("duration: "); //$NON-NLS-1$
						buffer.append(xVal);
						buffer.append(PlatformHandler.LINE_SEPARATOR);
						buffer.append("state: ");//$NON-NLS-1$
						buffer.append(yVal);
						this.addMarkerObject(new MarkerObject(marker, buffer.toString()));
						graphics.fillRectangle(xStartCor + 1, yCor + width, xEndCor - xStartCor, width + 2);
					}
				}
			}
			yCor += this.barht;
		}
	}

	private int resolveCoordinate(final long value) {
		return (int) (this.yStart + (value - this.yStartValue) * this.ySlope);
	}

	private void drawYAxis(final UserInterface graphics) {
		final Color background = graphics.getBackground();
		final Color foreground = graphics.getForeground();
		graphics.setFont(this.chartProperties.getTickFont());
		final int startXpx = this.plotRect.x + this.shiftYaxisToRHS;
		final int endXpx = this.plotRect.x + this.plotRect.width;
		final int ht = this.plotRect.height - this.shiftXAxisAbove / 2 - INSET_FOR_TICK_VALUE_FROM_TICK_MARK;
		final int yCor = this.plotRect.y + ht - 2;

		this.yAxisRect = new Rectangle(this.plotRect.x, yCor, this.plotRect.width + 1, yCor - ht + 4);

		if (this.bShowAlternateBackground && !this.scrollable) {
			this.showAlternateBackgroundColors(graphics, this.plotRect.y,
					this.plotRect.height - this.shiftXAxisAbove / 2 - INSET_FOR_TICK_VALUE_FROM_TICK_MARK, this.barht,
					this.chartProperties.isStretchAlternateBackground().booleanValue() ? this.plotRect.x
							: (this.plotRect.x + this.shiftYaxisToRHS),
					this.plotRect.x + this.plotRect.width);
		}

		// draw Y-Axis line
		final LineAttributes oldLine = graphics.getLineStyle();
		graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
		graphics.drawLine(startXpx, yCor, endXpx, yCor);
		graphics.setLineStyle(oldLine);

		if (this.isYAxisGrid()) {
			this.drawYAxisGrids(graphics, startXpx, endXpx, this.plotRect.y, yCor);
		}

		final long minMax[] = this.getMinMaxValues();
		final long startYval = minMax[0] + (minMax[0] == 0 ? 0 : -1);
		final long endYval = minMax[1] + 1;

		final int maxStringWidth = graphics
				.stringExtent(TimeValueFormatter.getFormattedValue(startYval, endYval, endYval, true)).x;
		final int tickWidth = 2 * maxStringWidth;
		final int availablePxs = endXpx - startXpx - maxStringWidth / 2;
		int tickCount = availablePxs / tickWidth;
		if (tickCount == 0) {
			tickCount = 1;
		}

		float tickInterval = (float) (endYval - startYval) / tickCount;
		if (tickInterval == 0) {
			tickInterval = 1.0f;
		}
		tickCount = Math.round((endYval - startYval) / tickInterval) + 1;

		this.yStart = startXpx;
		this.yStartValue = startYval;
		this.ySlope = tickWidth / tickInterval;

		// draw Ticks
		int x = startXpx;
		String value = null;
		float drawX;
		for (int i = 0; i < tickCount; i++) {
			// if (isYAxisGrid())
			// {
			// oldColor = graphics.getForeground();
			// graphics.setForeground(ChartConstants.GRID_COLOR);
			// int oldLine = graphics.getLineStyle();
			// graphics.setLineStyle(UserInterface.LINE_STYLE_DOT);
			// graphics.setForeground(ChartConstants.GRID_COLOR);
			// graphics.drawLine(x, plotRect.y, x, yCor);
			// graphics.setLineStyle(oldLine);
			// graphics.setForeground(oldColor);
			// }

			value = TimeValueFormatter.getFormattedValue(startYval, startYval + i * (long) tickInterval, endYval, true);

			graphics.setBackground(background);
			graphics.setForeground(foreground);
			graphics.drawLine(x, yCor, x, yCor + (i != 0 ? TICK_MARK_LENGTH : 0));

			drawX = x - graphics.stringExtent(value).x * 0.5f;
			if (drawX < this.plotRect.x) {
				drawX = this.plotRect.x;
			}

			if (i != 0) {
				graphics.setBackground(this.chartProperties.getTickBackgroundColour());
				graphics.setForeground(this.chartProperties.getTickTextColour());
				graphics.drawString(value, (int) drawX, (yCor + INSET_FOR_TICK_VALUE_FROM_TICK_MARK), true);
			}

			x += tickWidth;
		}

		graphics.setBackground(background);
		graphics.setForeground(foreground);

		// draw surrounding line
		graphics.drawLine(startXpx, this.plotRect.y, endXpx, this.plotRect.y);
		final int startYpx = this.plotRect.y + this.plotRect.height - this.shiftXAxisAbove / 2
				- INSET_FOR_TICK_VALUE_FROM_TICK_MARK - 2;
		final int endYpx = this.plotRect.y;
		graphics.drawLine(this.plotRect.x + this.plotRect.width - 1, startYpx,
				this.plotRect.x + this.plotRect.width - 1, endYpx);
	}

	private void drawYAxisGrids(final UserInterface graphics, final int startXpx, final int endXpx, final int startY,
			final int endY) {
		final Color background = graphics.getBackground();
		final Color foreground = graphics.getForeground();
		final long minMax[] = this.getMinMaxValues();
		final long startYval = minMax[0] + (minMax[0] == 0 ? 0 : -1);
		final long endYval = minMax[1] + 1;

		final int maxStringWidth = graphics
				.stringExtent(TimeValueFormatter.getFormattedValue(startYval, endYval, endYval, true)).x;
		final int tickWidth = 2 * maxStringWidth;
		final int availablePxs = endXpx - startXpx - maxStringWidth / 2;
		int tickCount = availablePxs / tickWidth;
		if (tickCount == 0) {
			tickCount = 1;
		}

		float tickInterval = (float) (endYval - startYval) / tickCount;
		if (tickInterval == 0) {
			tickInterval = 1.0f;
		}
		tickCount = Math.round((endYval - startYval) / tickInterval) + 1;
		int x = startXpx;
		for (int i = 0; i < tickCount; i++) {
			graphics.setForeground(ChartConstants.GRID_COLOR);
			final LineAttributes oldLine = graphics.getLineStyle();
			graphics.setLineStyle(new LineAttributes(0.75f, LineAttributes.CAP_FLAT, LineAttributes.JOIN_ROUND));
			graphics.drawLine(x, startY, x, endY);
			graphics.setLineStyle(oldLine);
			x += tickWidth;
		}
		graphics.setBackground(background);
		graphics.setForeground(foreground);
	}

	private long[] getMinMaxValues() {
		if (this.bMinMaxYValueSet) {
			return new long[] { this.iMinYValue, this.iMaxYValue };
		}

		final long[] minMax = new long[] { Long.MAX_VALUE, Long.MIN_VALUE };
		ComplexSeries complexSeries = null;
		StartEndSeries seSeries = null;
		for (int i = 0; i < this.collection.getCurrentCount(); i++) {
			complexSeries = this.collection.getComplexSeries(i);
			for (int j = 0; j < complexSeries.getSeriesCount(); j++) {
				if (this.shouldDrawSeries(j)) {
					seSeries = complexSeries.getSeries(j);
					minMax[0] = Math.min(minMax[0], seSeries.getMinValue());
					minMax[1] = Math.max(minMax[1], seSeries.getMaxValue());
				}
			}
		}

		if ((minMax[0] == Long.MAX_VALUE) && (minMax[1] == Long.MIN_VALUE)) {
			minMax[0] = System.currentTimeMillis() - 60000L;
			minMax[1] = System.currentTimeMillis();
		}
		return minMax;
	}

	private void clearAll() {
		this.xAxisTitleRect = null;
		this.yAxisTitleRect = null;
	}

	private void calculateShiftsOfXandYAxis(final UserInterface graphics) {
		graphics.setFont(this.chartProperties.getTickFont());
		this.shiftYaxisToRHS = -1;
		for (int i = 0; i < this.collection.getCurrentCount(); i++) {
			this.shiftYaxisToRHS = Math.max(this.shiftYaxisToRHS,
					graphics.stringExtent(this.collection.getComplexSeries(i).getName()).x
							+ INSET_FOR_TICK_VALUE_FROM_AXIS);
		}
		this.shiftYaxisToRHS += INSET_FOR_TICK_VALUE_FROM_TICK_MARK;
		this.calculateShiftOfXAxis(graphics);
	}

	private void calculateShiftOfXAxis(final UserInterface graphics) {
		final Font defaultTickFont = this.chartProperties.getTickFont();
		graphics.setFont(defaultTickFont);
		final int maxHt = getMaxHeight(graphics, defaultTickFont);
		this.shiftXAxisAbove = 2 * maxHt + INSET_FOR_TICK_VALUE_FROM_TICK_MARK;
		this.barht = this.shiftXAxisAbove;
		// if (barht * collection.getCurrentCount() < plotRect.height -
		// shiftXAxisAbove/2)
		// {
		// barht = (plotRect.height - shiftXAxisAbove/2)/
		// (collection.getCurrentCount() <= 0 ? 1 :
		// collection.getCurrentCount());
		// }
	}

	private void calculateDimensions(final UserInterface graphics) {
		final Rectangle r = graphics.getClipping();

		final int inset = 1;
		this.plotRect = new Rectangle(r.x, r.y + inset, r.width - inset, r.height);
		// assign values to each rectangle
		if (this.isXAxisTitle()) {
			this.xAxisTitleRect = new Rectangle(r.x, r.y,
					this.chartProperties.getXAxisTitle().getMaxHeight(graphics) + 2, r.height);
			this.plotRect.width -= this.xAxisTitleRect.width;
			this.plotRect.x += this.xAxisTitleRect.width;
		}
		if (this.isYAxisTitle()) {
			final int maxHt = this.chartProperties.getYAxisTitle().getMaxHeight(graphics);
			this.yAxisTitleRect = new Rectangle(r.x, r.y + r.height - maxHt - 4, r.width, maxHt + 4);
			this.plotRect.height -= this.yAxisTitleRect.height;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.charts.AppChart#getLegend()
	 */
	public final Legend[] getLegend() {
		return this.legends;
	}

	public void setLegend(final String[] names, final Color[] colors) {
		if ((names != null) && (colors != null) && (names.length == colors.length)) {
			this.legends = new Legend[names.length];
			Color axisSeriesColor;
			String axisSeriesText;
			for (int i = 0; i < names.length; i++) {
				axisSeriesText = this.chartProperties.getYAxisSeriesText(i);
				if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS) || (axisSeriesText == null)) {
					this.chartProperties.setYAxisSeriesText(i, names[i]);
				}
				axisSeriesColor = this.chartProperties.getYAxisSeriesColor(i);
				if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS) || (axisSeriesColor == null)) {
					this.chartProperties.setYAxisSeriesColor(i, colors[i]);
				}
				this.legends[i] = new Legend(this.chartProperties.getYAxisSeriesText(i),
						AppChart.getColorForIndex(i, this.chartProperties), this.chartProperties.getLegendTextColour());
				this.legends[i].legendFont = this.chartProperties.getLegendFont();
			}
		}
	}

	/**
	 * method getAreaColor
	 * 
	 * @return
	 */
	public final Color getAreaColor() {
		return this.chartProperties.getAreaColour();
	}

	/**
	 * method getChartTitle
	 * 
	 * @return
	 */
	public String getXAxisTitle() {
		final Title axisTitle = this.chartProperties.getXAxisTitle();
		return axisTitle == null ? null : axisTitle.getText();
	}

	/**
	 * method getChartTitle
	 * 
	 * @return
	 */
	public String getYAxisTitle() {
		final Title axisTitle = this.chartProperties.getYAxisTitle();
		return axisTitle == null ? null : axisTitle.getText();
	}

	/**
	 * method setAreaColor
	 * 
	 * @param color
	 */
	public void setAreaColor(final Color color) {
		if ((color != null) && ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getAreaColour() == null))) {
			this.chartProperties.setAreaColour(color);
		}
	}

	/**
	 * method setAxisFont
	 * 
	 * @param title
	 */
	public void setAxisFont(final String fontName, final int fontStyle, final int fontSize) {
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getAxisFont() == null)) {
			final Font font = new Font(fontName, fontSize, fontStyle);
			this.chartProperties.setXAxisTitleFont(font);
			this.chartProperties.setYAxisTitleFont(font);
		}
	}

	/**
	 * method setXAxisTitle
	 * 
	 * @param title
	 */
	public void setXAxisTitle(final String title) {
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getXAxisTitle() == null)) {
			this.chartProperties.setXAxisTitle(title);
			this.chartProperties.setXAxisTitleFont(this.chartProperties.getAxisFont());
		}
	}

	/**
	 * method setXAxisTitle
	 * 
	 * @param title
	 */
	public void setYAxisTitle(final String title) {
		final Title axisTitle = this.chartProperties.getYAxisTitle();
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS) || (axisTitle == null)) {
			this.chartProperties.setYAxisTitle(title);
			this.chartProperties.getYAxisTitle().setOrientation(Title.ORIENTATION_VERTICAL);
			this.chartProperties.setYAxisTitleFont(this.chartProperties.getAxisFont());
		}
	}

	public void showXAxisGrids(final boolean grids) {
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getXAxisGrids() == null)) {
			this.chartProperties.setShowXAxisGrids(grids);
		}
	}

	public void showYAxisGrids(final boolean grids) {
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getYAxisGrids() == null)) {
			this.chartProperties.setShowYAxisGrids(grids);
		}
	}

	/**
	 * method showXAxisTitle
	 * 
	 * @param title
	 */
	public void showXAxisTitle(final boolean title) {
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getShowXAxisTitle() == null)) {
			this.chartProperties.setShowXAxisTitle(title);
		}
	}

	/**
	 * method showYAxisTitle
	 * 
	 * @param title
	 */
	public void showYAxisTitle(final boolean title) {
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getShowYAxisTitle() == null)) {
			this.chartProperties.setShowYAxisTitle(title);
		}
	}

	/**
	 * method isXAxisTitle
	 * 
	 * @return
	 */
	public boolean isXAxisTitle() {
		final Boolean showXAxisTitle = this.chartProperties.getShowXAxisTitle();
		return (showXAxisTitle != null) && showXAxisTitle.booleanValue();
	}

	/**
	 * method isYAxisTitle
	 * 
	 * @return
	 */
	public boolean isYAxisTitle() {
		final Boolean showYAxisTitle = this.chartProperties.getShowYAxisTitle();
		return (showYAxisTitle != null) && showYAxisTitle.booleanValue();
	}

	public final boolean isXAxisGrid() {
		final Boolean axisGrids = this.chartProperties.getXAxisGrids();
		return (axisGrids != null) && axisGrids.booleanValue();
	}

	public final boolean isYAxisGrid() {
		final Boolean axisGrids = this.chartProperties.getYAxisGrids();
		return (axisGrids != null) && axisGrids.booleanValue();
	}

	/**
	 * 
	 * method getSeriesCollection
	 * 
	 * @return
	 */
	public final SeriesCollection getSeriesCollection() {
		return this.collection;
	}

	private static int maxHeight = -1;

	public static int getMaxHeight(final UserInterface graphics, final Font tickFont) {
		if (maxHeight == -1) {
			final Font oldFont = graphics.getFont();
			graphics.setFont(tickFont);
			maxHeight = graphics.getFontHeight();
			graphics.setFont(oldFont);
		}
		return maxHeight;
	}

	protected void showAlternateBackgroundColors(final UserInterface graphics, final int startYPosition,
			final int availablePixels, final int eachDataWidth, int startXPosition, final int endXPosition) {
		// if (chartTitleRect != null)
		// {
		// availablePixels -= chartTitleRect.height;
		// }

		startXPosition++;
		final Color oldBackGroundColor = graphics.getBackground();
		final Color arrAlternateColors[] = new Color[] { this.getAreaColor(),
				this.chartProperties.getAlternateBackgroundColour() };

		final int endYPosition = startYPosition + availablePixels - 1;
		int currYPosition = startYPosition;

		final int iRectanglesWidth = endXPosition - startXPosition - 1;
		int iRectanglesHeight = eachDataWidth;
		int addtion = 0;

		/*
		 * if(isXAxisGrid()) { for(int colorIndex = 0; currYPosition <=
		 * endYPosition + eachDataWidth; currYPosition += eachDataWidth,
		 * colorIndex = colorIndex == 0 ? 1 : 0) {
		 * graphics.setBackground(arrAlternateColors[colorIndex]);
		 * graphics.fillRectangle(startXPosition, currYPosition -
		 * iRectanglesHeight + 1, iRectanglesWidth, iRectanglesHeight - 1); } }
		 * else { for(int colorIndex = 0; currYPosition <= endYPosition +
		 * eachDataWidth; currYPosition += eachDataWidth, colorIndex =
		 * colorIndex == 0 ? 1 : 0) {
		 * graphics.setBackground(arrAlternateColors[colorIndex]);
		 * 
		 * if(currYPosition - eachDataWidth <= endYPosition + eachDataWidth) {
		 * graphics.fillRectangle(startXPosition, currYPosition -
		 * iRectanglesHeight, iRectanglesWidth, iRectanglesHeight); } else {
		 * graphics.fillRectangle(startXPosition, currYPosition -
		 * iRectanglesHeight + 1, iRectanglesWidth, iRectanglesHeight - 1); } }
		 * }
		 */
		for (boolean first = true; currYPosition < endYPosition; currYPosition += eachDataWidth, first = !first) {
			if (endYPosition - currYPosition < eachDataWidth) {
				iRectanglesHeight = (endYPosition - currYPosition);
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

	/**
	 * Used to specify range as min and max if graph need to show values on Y
	 * axis within a range only. method setMinMaxYValue
	 * 
	 * @param min
	 * @param max
	 * 
	 */
	public void setMinMaxYValue(final long min, final long max) {
		this.iMinYValue = min;
		this.iMaxYValue = max;
		this.bMinMaxYValueSet = true;
	}

	public boolean isScrollable() {
		return this.scrollable;
	}

	public void setScrollable(final boolean scrollable) {
		this.scrollable = scrollable;
	}

	public int getIncrement() {
		return this.barht;
	}

}
