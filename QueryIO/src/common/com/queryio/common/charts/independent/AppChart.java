/*
 * @(#) AppChart.java Feb 4, 2005 Copyright (C) 2002 Exceed Consultancy
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

import java.awt.Graphics2D;
import java.util.ArrayList;

import com.queryio.common.charts.components.Legend;
import com.queryio.common.charts.components.MarkerObject;
import com.queryio.common.charts.components.Title;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.charts.properties.ChartProperties;
import com.queryio.common.charts.properties.ChartPropertiesManager;
import com.queryio.common.charts.swinggraphics.SWINGUserInterface;
import com.queryio.common.charts.util.PngOutputStream;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Font;
import com.queryio.common.exporter.dstruct.Rectangle;


/**
 * @author Exceed Consultancy Services
 */
public abstract class AppChart
{
	protected int nodeType;
	protected ChartProperties chartProperties;

	protected final UserInterface userInterface;

	protected Rectangle chartTitleRect = null;

	protected Rectangle legendRect = null;

	protected Rectangle chartAreaRect = null;

	private static Color cDefaultColors[] = null;

	protected int iTotalLegendWidth = 0;

	private PngOutputStream outputStream = null;

	protected boolean bShowAlternateBackground;

	private static final int LEGENDITEMINSET = 8 + Legend.iStrikeThroughLength;
	private static final int LEGENDITEMINSET_SELECT = 20 + Legend.iStrikeThroughLength;
	protected static final int LEGENDSTARTINSET = 5;

	protected static final int TICK_MARK_LENGTH = 3;

	protected static final int INSET_FOR_TICK_VALUE_FROM_AXIS = 3;
	protected static final int INSET_FOR_TICK_VALUE_FROM_TICK_MARK = INSET_FOR_TICK_VALUE_FROM_AXIS + TICK_MARK_LENGTH
			- 1;

	protected ArrayList markerList;

	protected boolean bShowToolTip = false;
	protected boolean hasChartTypeChanged;
	private boolean exporting = false;

	protected static Color getColorForIndex(final int index, final ChartProperties properties)
	{
		final Color axisSeriesColor = properties.getYAxisSeriesColor(index);
		if (axisSeriesColor != null)
		{
			return axisSeriesColor;
		}
		else if (cDefaultColors == null)
		{
			final Color defaultColors[] = {
					new Color(0xff, 0x00, 0x00), // red
					new Color(0x00, 0x00, 0xff), // blue
					new Color(0x00, 0xff, 0x00), // green
					new Color(0xff, 0xff, 0x00), // yellow
					new Color(0xff, 0xc8, 0x00), // orange
					new Color(0xff, 0x00, 0xff), // magenta
					new Color(0x00, 0xff, 0xff), // cyan
					new Color(0xff, 0xaf, 0xaf), // pink
					new Color(0x80, 0x80, 0x80), // gray
					new Color(0xc0, 0x00, 0x00),
					new Color(0x00, 0x00, 0xC0),
					new Color(0x00, 0xC0, 0x00),
					new Color(0xC0, 0xC0, 0x00),
					new Color(0xC0, 0x00, 0xC0),
					new Color(0x00, 0xC0, 0xC0),
					new Color(0x40, 0x40, 0x40), // darkGray
					new Color(0xFF, 0x40, 0x40), 
					new Color(0x40, 0x40, 0xFF),
					new Color(0x40, 0xFF, 0x40),
					new Color(0xFF, 0xFF, 0x40),
					new Color(0xFF, 0x40, 0xFF),
					new Color(0x40, 0xFF, 0xFF),
					new Color(0xc0, 0xc0, 0xc0), // lightGray
					new Color(0x80, 0x00, 0x00), new Color(0x00, 0x00, 0x80), new Color(0x00, 0x80, 0x00),
					new Color(0x80, 0x80, 0x00), new Color(0x80, 0x00, 0x80), new Color(0x00, 0x80, 0x80),
					new Color(0xFF, 0x80, 0x80), new Color(0x80, 0x80, 0xFF), new Color(0x80, 0xFF, 0x80),
					new Color(0xFF, 0xFF, 0x80), new Color(0xFF, 0x80, 0xFF), new Color(0x80, 0xFF, 0xFF) };
			cDefaultColors = defaultColors;
		}
		final int i = index % cDefaultColors.length;
		return cDefaultColors[i];
	}

	private void initializeFonts()
	{
		final String sFontName = this.userInterface.getDefaultFontName();
		int iFontHeight = this.userInterface.getDefaultFontHeight();
		if (iFontHeight < 10)
		{
			iFontHeight = 10;
		}
		if (this.chartProperties.getTitleFont() == null)
		{
			this.chartProperties.setTitleFont(new Font(sFontName, iFontHeight, Font.STYLE_BOLD));
		}
		if (this.chartProperties.getAxisFont() == null)
		{
			this.chartProperties.setAxisFont(new Font(sFontName, (iFontHeight - 1), Font.STYLE_BOLD));
		}
		if (this.chartProperties.getLegendFont() == null)
		{
			this.chartProperties.setLegendFont(new Font(sFontName, (iFontHeight - 2), Font.STYLE_PLAIN));
		}
		if (this.chartProperties.getTickFont() == null)
		{
			this.chartProperties.setTickFont(new Font(sFontName, (iFontHeight - 2), Font.STYLE_PLAIN));
		}
	}

	public AppChart(final UserInterface userInterface, final int nodeType, final int productID)
	{
		this.userInterface = userInterface;
		this.nodeType = nodeType;
		this.chartProperties = ChartPropertiesManager.getChartProperties(productID, nodeType);
		this.initializeFonts();
		if (this.chartProperties.getBackgroundColour() == null)
		{
			this.chartProperties.setBackgroundColour(ChartConstants.COLOR_WHITE);
		}
		if (this.chartProperties.getChartTitleBackgroundColour() == null)
		{
			this.chartProperties.setChartTitleBackgroundColour(ChartConstants.COLOR_WHITE);
		}
		if (this.chartProperties.getAlternateBackgroundColour() == null)
		{
			this.chartProperties.setAlternateBackgroundColour(ChartConstants.COLOR_ALTERNATE);
		}
		if (this.chartProperties.getLegendBackgroundColour() == null)
		{
			this.chartProperties.setLegendBackgroundColour(ChartConstants.COLOR_WHITE);
		}
		if (this.chartProperties.getLegendTextColour() == null)
		{
			this.chartProperties.setLegendTextColour(ChartConstants.COLOR_BLACK);
		}
	}

	/**
	 * method getChartTitle
	 * 
	 * @return
	 */
	public String getChartTitle()
	{
		final Title chartTitle = this.chartProperties.getChartTitle();
		if (chartTitle == null)
		{
			return null;
		}
		return chartTitle.getText();
	}

	/**
	 * method isLegend
	 * 
	 * @return
	 */
	public final boolean isLegend()
	{
		final Boolean showLegend = this.chartProperties.getShowLegend();
		return showLegend != null ? showLegend.booleanValue() : false;
	}

	/**
	 * method isTitle
	 * 
	 * @return
	 */
	public boolean isTitle()
	{
		final Boolean showChartTitle = this.chartProperties.getShowChartTitle();
		return showChartTitle != null ? showChartTitle.booleanValue() : false;
	}

	/**
	 * method setChartTitle
	 * 
	 * @param title
	 */
	public void setChartTitle(final String title)
	{
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS) || (this.getChartTitle() == null))
		{
			this.chartProperties.setChartTitle(title);
			if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
					|| (this.chartProperties.getChartTitleFont() == null))
			{
				this.chartProperties.setChartTitleFont(this.chartProperties.getTitleFont());
			}
			if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
					|| (this.chartProperties.getChartTitleTextColour() == null))
			{
				this.chartProperties.setChartTitleTextColour(ChartConstants.COLOR_BLACK);
			}
		}
	}

	/**
	 * Use this method to set chart title arguments which will be replaced using
	 * MessageFormat.format() method.
	 * 
	 * @param args
	 */
	public void setChartTitleArguments(final String[] args)
	{
		final Title title = this.chartProperties.getChartTitle();
		if (title != null)
		{
			title.setArguments(args);
		}
	}

	/**
	 * method showLegend
	 * 
	 * @param legend
	 */
	public void showLegend(final boolean legend)
	{
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.getShowLegend() == null))
		{
			this.chartProperties.setShowLegend(legend);
		}
	}

	/**
	 * @param show
	 */
	public void showAlternateBackground(final boolean show)
	{
		this.bShowAlternateBackground = show;
	}

	/**
	 * method showTitle
	 * 
	 * @param title
	 */
	public void showTitle(final boolean title)
	{
		final Boolean showChartTitle = this.chartProperties.getShowChartTitle();
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS) || (showChartTitle == null))
		{
			this.chartProperties.setShowChartTitle(title);
		}
	}

	public Rectangle getTitleBounds()
	{
		return this.chartTitleRect;
	}

	public Rectangle getLegendBounds()
	{
		return this.legendRect;
	}

	public byte[] getPNG(final int width, final int height, final byte[] bytesToUse)
	{
		exporting = true;
		try
		{
			this.createImage(width, height);
			if (this.outputStream == null)
			{
				this.outputStream = new PngOutputStream(bytesToUse);
			}
			else
			{
				this.outputStream.reset();
				this.outputStream.setByteArray(bytesToUse);
			}
			return this.userInterface.getPNG(width, height, this.outputStream);
		}
		finally
		{
			exporting = false;
		}
	}

	public void resetOutputStream()
	{
		this.outputStream = null;
	}

	public boolean hasReferenceChanged()
	{
		return (this.outputStream != null ? this.outputStream.hasReferenceChanged() : false);
	}

	public int getLastPosition()
	{
		return (this.outputStream != null ? this.outputStream.getLastPosition() : 0);
	}

	public final synchronized void createImage(final int width, final int height)
	{
		if (this.markerList != null)
		{
			this.markerList.clear();
		}
		this.userInterface.createGraphics(width, height);
		this.userInterface.setClipping(0, 0, width, height);
		this.drawRaw(this.userInterface, width, height);
		this.userInterface.disposeGraphics();
	}

	private void drawRaw(final UserInterface chartGraphics, final int width, final int height)
	{
		chartGraphics.setBackground(this.getBackgroundColor());
		chartGraphics.fillRectangle(0, 0, width, height);

		// calculate the space required to draw the chart
		this.calculateDimensions(chartGraphics, width, height);

		final Rectangle oldClip = chartGraphics.getClipping();

		// draw Title
		if (this.isTitle())
		{
			chartGraphics.setClipping(this.chartTitleRect);
			chartGraphics.setBackground(this.chartProperties.getChartTitleBackgroundColour());
			chartGraphics.setForeground(this.chartProperties.getChartTitleTextColour());
			this.chartProperties.getChartTitle().draw(chartGraphics, 0);
		}

		// draw Area
		chartGraphics.setClipping(this.chartAreaRect);
		this.drawArea(chartGraphics);

		if (this.isLegend())
		{
			// draw Legend
			this.drawLegend(chartGraphics);
		}
		chartGraphics.setClipping(oldClip);
	}

	public final void drawLegend(final UserInterface graphics)
	{
		graphics.setClipping(this.legendRect);

		final Legend[] legends = this.getLegend();
		int lenLegend = (legends != null) ? legends.length:0;
		if (lenLegend > 0)
		{
			int x = this.legendRect.x;
			int y = this.legendRect.y;
			final int iMaxWidth = this.getMaxWidth(graphics);
			final int iMaxHeight = Legend.getMaxHeight(graphics, this.chartProperties.getLegendFont());
			int divisor = 1;
			final int numerator = (this.iTotalLegendWidth <= this.legendRect.width) ? this.iTotalLegendWidth
					: this.legendRect.width;
			if (iMaxWidth > 0)
			{
				divisor = numerator / iMaxWidth;
			}
			if (numerator % iMaxWidth - LEGENDSTARTINSET > 0)
			{
				divisor--;
			}
			if (divisor == 0)
			{
				divisor = 1;
			}
			int iTotalLegendHeight = (iMaxHeight * lenLegend) / divisor;
			if (divisor > 1)
			{
				iTotalLegendHeight += 2 * LEGENDSTARTINSET;
			}

			if (this.iTotalLegendWidth <= this.legendRect.width)
			{
				x += (this.legendRect.width - this.iTotalLegendWidth) / 2;
			}

			if (iTotalLegendHeight <= this.legendRect.height)
			{
				y += (this.legendRect.height - iTotalLegendHeight) / 2;

			}

			final Rectangle r = new Rectangle(x, y, Math.min(this.iTotalLegendWidth, this.legendRect.width - 1), Math
					.min(iTotalLegendHeight, this.legendRect.height - 1));

			final Color oldColor = graphics.getBackground();
			graphics.setBackground(this.chartProperties.getLegendBackgroundColour());
			graphics.fillRectangle(r.x + 1, r.y + 1, r.width - 1, r.height - 1);
			graphics.setBackground(oldColor);

			graphics.setForeground(ChartConstants.COLOR_BLACK);
//			graphics.drawRectangle(r);

			x += LEGENDSTARTINSET;
			MarkerObject mo;
			if (this.iTotalLegendWidth <= this.legendRect.width) // Rendering all legend items in a single row.
			{
				for (int i = 0; i < legends.length; i++)
				{
					if (legends[i].getText() != null)
					{
						final int individualWidth = legends[i].getMaxWidth(graphics) + (graphics.showSeriesSelector() ? LEGENDITEMINSET_SELECT: LEGENDITEMINSET);
						legends[i].legendFont = this.chartProperties.getLegendFont();
						mo = legends[i].draw(graphics, new Rectangle(x, y, individualWidth, iMaxHeight), this.shouldDrawSeries(i), i);
						this.addMarkerObject(mo);
						x += individualWidth;
					}
				}
			}
			else
			{
				for (int i = 0; i < legends.length; i++)
				{
					if (legends[i].getText() != null)
					{
						legends[i].legendFont = this.chartProperties.getLegendFont();
						mo = legends[i].draw(graphics, new Rectangle(x, y, iMaxWidth, iMaxHeight), this.shouldDrawSeries(i), i);
						this.addMarkerObject(mo);
						x += iMaxWidth;
						if (x + iMaxWidth - LEGENDSTARTINSET > this.legendRect.x + this.legendRect.width)
						{
							x = this.legendRect.x + LEGENDSTARTINSET;
							y += iMaxHeight;
						}
					}
				}
			}
		}
		graphics.setBackground(this.getBackgroundColor());
	}

	protected final boolean shouldDrawSeries(final int i)
	{
		if (this.chartProperties.getShowYAxisSeries(i) == null)
		{
			this.chartProperties.setShowYAxisSeries(i, true);
			return true;
		}
//		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
//				|| (this.chartProperties.getShowYAxisSeries(i) == null))
//		{
//			this.chartProperties.setShowYAxisSeries(i, true);
//			return true;
//		}
		return this.chartProperties.getShowYAxisSeries(i).booleanValue();
	}

	public abstract void drawArea(UserInterface graphics);

	public abstract Legend[] getLegend();

	protected void calculateDimensions(final UserInterface graphics, final int width, final int height)
	{
		final int x = 0;
		final int y = 0;

		if (this.isLegend())
		{
			int h = 0;
			final Legend[] legends = this.getLegend();
			int lenLegend = (legends != null) ? legends.length:0;
			if (lenLegend > 0)
			{
				final int legendItemWidth = this.getMaxWidth(graphics);
				final int legendItemHeight = Legend.getMaxHeight(graphics, this.chartProperties.getLegendFont());

				if (this.iTotalLegendWidth > width)
				{
					final int noOfItemsInARow = (width - LEGENDSTARTINSET) / legendItemWidth;
					if (noOfItemsInARow > 0)
					{
						h = lenLegend / noOfItemsInARow;
						if (lenLegend % noOfItemsInARow > 0)
						{
							h++;
						}
					}
				}
				else
				{
					h = 1;
				}
				h *= legendItemHeight;
				h += 2;
			}
			this.legendRect = new Rectangle(x, y + height - h, width, h);
		}
		else
		{
			this.legendRect = null;
		}

		// assign values to each rectangle
		final Title chartTitle = this.chartProperties.getChartTitle();
		if ((chartTitle != null) && this.chartProperties.getShowChartTitle().booleanValue())
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
			this.chartAreaRect.height -= this.legendRect.height;
		}
		
		//Leave 2 pixel
		this.chartAreaRect.height -= 2;
		this.chartAreaRect.width -= 2;
	}

	protected int getMaxWidth(final UserInterface graphics)
	{
		this.iTotalLegendWidth = 0;
		final Legend[] legends = this.getLegend();
		int maxWidth = 0;
		int width = -1;
		for (int i = 0; i < legends.length; i++)
		{
			if (legends[i].getText() != null)
			{
				width = legends[i].getMaxWidth(graphics);
				maxWidth = Math.max(maxWidth, width);
				this.iTotalLegendWidth += width + (graphics.showSeriesSelector() ? LEGENDITEMINSET_SELECT: LEGENDITEMINSET);
			}
		}
		maxWidth += (graphics.showSeriesSelector() ? LEGENDITEMINSET_SELECT: LEGENDITEMINSET);
		this.iTotalLegendWidth += LEGENDSTARTINSET;
		return maxWidth;
	}

	/**
	 * method getBackgroundColor
	 * 
	 * @return
	 */
	public final Color getBackgroundColor()
	{
		return this.chartProperties.getBackgroundColour();
	}

	/**
	 * method setBackgroundColor
	 * 
	 * @param color
	 */
	public void setBackgroundColor(final Color color)
	{
		if (color != null)
		{
			if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS) || (this.getBackgroundColor() == null))
			{
				this.chartProperties.setBackgroundColour(color);
			}
		}
	}

	public void setAlternateBackgroundColor(final Color color)
	{
		if (color != null)
		{
			if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
					|| (this.chartProperties.getAlternateBackgroundColour() == null))
			{
				this.chartProperties.setAlternateBackgroundColour(color);
			}
		}
	}

	public final boolean isStrechAlternateBackground()
	{
		final Boolean stretchAlternateBackground = this.chartProperties.isStretchAlternateBackground();
		return stretchAlternateBackground != null ? stretchAlternateBackground.booleanValue() : false;
	}

	public void setStrechAlternateBackground(final boolean b)
	{
		if ((this.nodeType == ChartPropertiesManager.TYPE_RUNTIME_CHARTS)
				|| (this.chartProperties.isStretchAlternateBackground() == null))
		{
			this.chartProperties.setStretchAlternateBackground(b);
		}
	}

	/**
	 * @return
	 */
	public final UserInterface getUserInterface()
	{
		return this.userInterface;
	}

	public MarkerObject getMarkerObject(final int x, final int y)
	{
		if (this.markerList != null)
		{
			MarkerObject markerObject;
			for (int i = 0; i < this.markerList.size(); i++)
			{
				markerObject = (MarkerObject) this.markerList.get(i);
				if (markerObject.contains(x, y))
				{
					return markerObject;
				}
			}
		}
		return null;
	}

	protected void addMarkerObject(final MarkerObject markerObject)
	{
		if (markerObject != null) 
		{
			if (this.markerList == null)
			{
				this.markerList = new ArrayList();
			}
			if (this.markerList.contains(markerObject))
			{
				return;
			}
			this.markerList.add(markerObject);
		}
	}

	public void showToolTip(final boolean flag)
	{
		this.bShowToolTip = flag;
	}
	
	private void adjustFontsForPDF(Font font, int factor)
	{
		if (font != null)
		{
			font.size += factor;
		}
	}

	public void drawChartForPDF(final Graphics2D g2, final int width, final int height)
	{
		final SWINGUserInterface swingInterface = new SWINGUserInterface();
		swingInterface.setThinLine(true);
		swingInterface.setExternalGraphics(g2);
		swingInterface.setBackground(this.getBackgroundColor());
		swingInterface.fillRectangle(0, 0, width, height);
		swingInterface.setBackground(new Color(0, 0, 0)); // black color
		swingInterface.drawRectangle(0, 0, width, height);
		
		int factor = -4;
		adjustFontsForPDF(this.chartProperties.getTitleFont(), factor);
		adjustFontsForPDF(this.chartProperties.getAxisFont(), factor);
		adjustFontsForPDF(this.chartProperties.getLegendFont(), factor);
		adjustFontsForPDF(this.chartProperties.getTickFont(), factor);
		adjustFontsForPDF(this.chartProperties.getXAxisTitleFont(), factor);
		adjustFontsForPDF(this.chartProperties.getYAxisTitleFont(), factor);
		adjustFontsForPDF(this.chartProperties.getChartTitleFont(), factor);
		
		// calculate the space required to draw the chart
		this.calculateDimensions(swingInterface, width, height);

		final Rectangle oldClip = swingInterface.getClipping();

		// draw Title
		if (this.isTitle())
		{
			swingInterface.setClipping(this.chartTitleRect);
			this.chartProperties.getChartTitle().draw(swingInterface, 0);
		}

		// draw Area
		swingInterface.setClipping(this.chartAreaRect);
		this.drawArea(swingInterface);

		if (this.isLegend())
		{
			// draw Legend
			this.drawLegend(swingInterface);
		}
		swingInterface.setClipping(oldClip);
		
		factor = -factor;
		adjustFontsForPDF(this.chartProperties.getTitleFont(), factor);
		adjustFontsForPDF(this.chartProperties.getAxisFont(), factor);
		adjustFontsForPDF(this.chartProperties.getLegendFont(), factor);
		adjustFontsForPDF(this.chartProperties.getTickFont(), factor);
		adjustFontsForPDF(this.chartProperties.getXAxisTitleFont(), factor);
		adjustFontsForPDF(this.chartProperties.getYAxisTitleFont(), factor);
		adjustFontsForPDF(this.chartProperties.getChartTitleFont(), factor);
	}

	public int getNodeType()
	{
		return this.nodeType;
	}

	public boolean isChartTypeChanged()
	{
		return this.hasChartTypeChanged;
	}

	public void setChartTypeChanged(final boolean hasChartTypeChanged)
	{
		this.hasChartTypeChanged = hasChartTypeChanged;
	}

	public ChartProperties getChartProperties() 
	{
		return chartProperties;
	}

	public boolean isExporting() 
	{
		return exporting;
	}

	public Rectangle getChartAreaBoundary() 
	{
		return chartAreaRect != null ? new Rectangle(chartAreaRect.x, chartAreaRect.y, chartAreaRect.width, chartAreaRect.height):null;
	}
}
