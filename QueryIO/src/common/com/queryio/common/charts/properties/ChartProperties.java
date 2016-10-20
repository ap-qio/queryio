/*
 * @(#) ChartProperties.java May 29, 2006 Copyright (C) 2002 Exceed Consultancy
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
package com.queryio.common.charts.properties;

import com.queryio.common.charts.components.Title;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Font;
import com.queryio.common.util.IntHashMap;

/**
 * This class is a data structure for all properties that can be set for a
 * chart.
 * 
 * @author Exceed Consultancy Services
 */
public class ChartProperties
{
	private final IntHashMap properties;

	ChartProperties()
	{
		this.properties = new IntHashMap();
	}

	public Title getChartTitle()
	{
		return (Title) this.properties.get(ChartPropertiesConstants.CHART_TITLE);
	}

	public void setChartTitle(final String chartTitle)
	{
		if (chartTitle != null)
		{
			Title title = this.getChartTitle();
			if (title == null)
			{
				title = new Title(chartTitle);
				this.properties.put(ChartPropertiesConstants.CHART_TITLE, title);
			}
			else
			{
				title.setText(chartTitle);
			}
		}
	}

	public Color getChartTitleTextColour()
	{
		return (Color) this.properties.get(ChartPropertiesConstants.CHART_TITLE_TEXT_COLOUR);
	}

	public void setChartTitleTextColour(final Color colour)
	{
		if (colour != null)
		{
			Color textColour = this.getChartTitleTextColour();
			if (textColour == null)
			{
				textColour = new Color(colour.red, colour.green, colour.blue);
				this.properties.put(ChartPropertiesConstants.CHART_TITLE_TEXT_COLOUR, textColour);
			}
			else
			{
				textColour.red = colour.red;
				textColour.green = colour.green;
				textColour.blue = colour.blue;
			}
			this.getChartTitle().setTextColor(colour);
		}
	}

	public Color getXAxisTitleTextColour()
	{
		return (Color) this.properties.get(ChartPropertiesConstants.X_AXIS_TITLE_TEXT_COLOUR);
	}

	public void setXAxisTitleTextColour(final Color colour)
	{
		if (colour != null)
		{
			Color textColour = this.getXAxisTitleTextColour();
			if (textColour == null)
			{
				textColour = new Color(colour.red, colour.green, colour.blue);
				this.properties.put(ChartPropertiesConstants.X_AXIS_TITLE_TEXT_COLOUR, textColour);
			}
			else
			{
				textColour.red = colour.red;
				textColour.green = colour.green;
				textColour.blue = colour.blue;
			}
			final Title axisTitle = this.getXAxisTitle();
			if (axisTitle != null)
			{
				axisTitle.setTextColor(colour);
			}
		}
	}

	public Color getYAxisTitleTextColour()
	{
		return (Color) this.properties.get(ChartPropertiesConstants.Y_AXIS_TITLE_TEXT_COLOUR);
	}

	public void setYAxisTitleTextColour(final Color colour)
	{
		if (colour != null)
		{
			Color textColour = this.getYAxisTitleTextColour();
			if (textColour == null)
			{
				textColour = new Color(colour.red, colour.green, colour.blue);
				this.properties.put(ChartPropertiesConstants.Y_AXIS_TITLE_TEXT_COLOUR, textColour);
			}
			else
			{
				textColour.red = colour.red;
				textColour.green = colour.green;
				textColour.blue = colour.blue;
			}
			final Title axisTitle = this.getYAxisTitle();
			if (axisTitle != null)
			{
				axisTitle.setTextColor(colour);
			}
		}
	}

	public Color getBackgroundColour()
	{
		return (Color) this.properties.get(ChartPropertiesConstants.BACKGROUND_COLOUR);
	}

	public void setBackgroundColour(final Color colour)
	{
		if (colour != null)
		{
			Color backgroundColour = this.getBackgroundColour();
			if (backgroundColour == null)
			{
				backgroundColour = new Color(colour.red, colour.green, colour.blue);
				this.properties.put(ChartPropertiesConstants.BACKGROUND_COLOUR, backgroundColour);
			}
			else
			{
				backgroundColour.red = colour.red;
				backgroundColour.green = colour.green;
				backgroundColour.blue = colour.blue;
			}
		}
	}

	public Color getAlternateBackgroundColour()
	{
		return (Color) this.properties.get(ChartPropertiesConstants.ALTERNATE_BACKGROUND_COLOUR);
	}

	public void setAlternateBackgroundColour(final Color colour)
	{
		if (colour != null)
		{
			Color alternateBackgroundColour = this.getAlternateBackgroundColour();
			if (alternateBackgroundColour == null)
			{
				alternateBackgroundColour = new Color(colour.red, colour.green, colour.blue);
				this.properties.put(ChartPropertiesConstants.ALTERNATE_BACKGROUND_COLOUR, alternateBackgroundColour);
			}
			else
			{
				alternateBackgroundColour.red = colour.red;
				alternateBackgroundColour.green = colour.green;
				alternateBackgroundColour.blue = colour.blue;
			}
		}
	}

	public Boolean isStretchAlternateBackground()
	{
		return (Boolean) this.properties.get(ChartPropertiesConstants.STRETCH_ALTERNATE_BACKGROUND);
	}

	public void setStretchAlternateBackground(final boolean stretchAlternateBackground)
	{
		final Boolean b = stretchAlternateBackground ? Boolean.TRUE : Boolean.FALSE;
		this.properties.put(ChartPropertiesConstants.STRETCH_ALTERNATE_BACKGROUND, b);
	}

	public Color getAreaColour()
	{
		return (Color) this.properties.get(ChartPropertiesConstants.AREA_COLOUR);
	}

	public void setAreaColour(final Color areaColour)
	{
		if (areaColour != null)
		{
			Color colour = this.getAreaColour();
			if (colour == null)
			{
				colour = new Color(areaColour.red, areaColour.green, areaColour.blue);
				this.properties.put(ChartPropertiesConstants.AREA_COLOUR, colour);
			}
			else
			{
				colour.red = areaColour.red;
				colour.green = areaColour.green;
				colour.blue = areaColour.blue;
			}
		}
	}

	public Font getTitleFont()
	{
		return (Font) this.properties.get(ChartPropertiesConstants.TITLE_FONT);
	}

	public void setTitleFont(final Font titleFont)
	{
		if (titleFont != null)
		{
			Font font = this.getTitleFont();
			if (font == null)
			{
				font = new Font(titleFont.name, titleFont.size, titleFont.style);
				this.properties.put(ChartPropertiesConstants.TITLE_FONT, font);
			}
			else
			{
				font.name = titleFont.name;
				font.size = titleFont.size;
				font.style = titleFont.style;
			}
		}
	}

	public Font getAxisFont()
	{
		return (Font) this.properties.get(ChartPropertiesConstants.AXIS_FONT);
	}

	public void setAxisFont(final Font axisFont)
	{
		if (axisFont != null)
		{
			Font font = this.getAxisFont();
			if (font == null)
			{
				font = new Font(axisFont.name, axisFont.size, axisFont.style);
				this.properties.put(ChartPropertiesConstants.AXIS_FONT, axisFont);
			}
			else
			{
				font.name = axisFont.name;
				font.size = axisFont.size;
				font.style = axisFont.style;
			}
		}
	}

	public Font getLegendFont()
	{
		return (Font) this.properties.get(ChartPropertiesConstants.LEGEND_FONT);
	}

	public void setLegendFont(final Font legendFont)
	{
		if (legendFont != null)
		{
			Font font = this.getLegendFont();
			if (font == null)
			{
				font = new Font(legendFont.name, legendFont.size, legendFont.style);
				this.properties.put(ChartPropertiesConstants.LEGEND_FONT, legendFont);
			}
			else
			{
				font.name = legendFont.name;
				font.size = legendFont.size;
				font.style = legendFont.style;
			}
		}
	}

	public Font getTickFont()
	{
		return (Font) this.properties.get(ChartPropertiesConstants.TICK_FONT8);
	}

	public void setTickFont(final Font tickFont)
	{
		if (tickFont != null)
		{
			Font font = this.getTickFont();
			if (font == null)
			{
				font = new Font(tickFont.name, tickFont.size, tickFont.style);
				this.properties.put(ChartPropertiesConstants.TICK_FONT8, tickFont);
			}
			else
			{
				font.name = tickFont.name;
				font.size = tickFont.size;
				font.style = tickFont.style;
			}
		}
	}

	public Title getXAxisTitle()
	{
		return (Title) this.properties.get(ChartPropertiesConstants.X_AXIS_TITLE);
	}

	public void setXAxisTitle(final String xAxisTitle)
	{
		if (xAxisTitle != null)
		{
			Title title = this.getXAxisTitle();
			if (title == null)
			{
				title = new Title(xAxisTitle);
				this.properties.put(ChartPropertiesConstants.X_AXIS_TITLE, title);
			}
			else
			{
				title.setText(xAxisTitle);
			}
		}
	}

	public Title getYAxisTitle()
	{
		return (Title) this.properties.get(ChartPropertiesConstants.Y_AXIS_TITLE);
	}

	public void setYAxisTitle(final String yAxisTitle)
	{
		if (yAxisTitle != null)
		{
			Title title = this.getYAxisTitle();
			if (title == null)
			{
				title = new Title(yAxisTitle);
				this.properties.put(ChartPropertiesConstants.Y_AXIS_TITLE, title);
			}
			else
			{
				title.setText(yAxisTitle);
			}
		}
	}

	private IntHashMap getYAxisSeriesColors()
	{
		return (IntHashMap) this.properties.get(ChartPropertiesConstants.COLOURS);
	}

	public int getYAxisSeriesCount()
	{
		final IntHashMap axisSeriesText = this.getYAxisSeriesText();
		return axisSeriesText != null ? axisSeriesText.size() : 0;
	}

	public Boolean getShowChartTitle()
	{
		return (Boolean) this.properties.get(ChartPropertiesConstants.SHOW_CHART_TITLE);
	}

	public Boolean getShowXAxisTitle()
	{
		return (Boolean) this.properties.get(ChartPropertiesConstants.SHOW_X_AXIS_TITLE);
	}

	public Boolean getShowYAxisTitle()
	{
		return (Boolean) this.properties.get(ChartPropertiesConstants.SHOW_Y_AXIS_TITLE);
	}
	
	public Boolean getShowChartMarker()
	{
		return (Boolean) this.properties.get(ChartPropertiesConstants.SHOW_CHART_MARKER);
	}
	
	public Boolean getShowChartLabel()
	{
		return (Boolean) this.properties.get(ChartPropertiesConstants.SHOW_CHART_LABEL);
	}
	
	private IntHashMap getYAxisSeriesViewable()
	{
		return (IntHashMap) this.properties.get(ChartPropertiesConstants.Y_AXIS_SHOW);
	}

	public Boolean getShowYAxisSeries(final int i)
	{
		final IntHashMap seriesViewable = this.getYAxisSeriesViewable();
		return seriesViewable != null ? (Boolean) seriesViewable.get(i) : null;
	}

	public void setShowYAxisSeries(final int i, final boolean toShow)
	{
		IntHashMap seriesViewable = this.getYAxisSeriesViewable();
		if (seriesViewable == null)
		{
			seriesViewable = new IntHashMap();
			this.properties.put(ChartPropertiesConstants.Y_AXIS_SHOW, seriesViewable);
		}
		final Boolean b = toShow ? Boolean.TRUE : Boolean.FALSE;
		seriesViewable.put(i, b);
	}

	public Boolean isFitToScale()
	{
		return (Boolean) this.properties.get(ChartPropertiesConstants.FIT_TO_SCALE);
	}

	public void setFitToScale(final boolean fitToScale)
	{
		final Boolean b = fitToScale ? Boolean.TRUE : Boolean.FALSE;
		this.properties.put(ChartPropertiesConstants.FIT_TO_SCALE, b);
	}

	public void setShowChartTitle(final boolean toShow)
	{
		final Boolean b = toShow ? Boolean.TRUE : Boolean.FALSE;
		this.properties.put(ChartPropertiesConstants.SHOW_CHART_TITLE, b);
	}

	public void setShowXAxisTitle(final boolean toShow)
	{
		final Boolean b = toShow ? Boolean.TRUE : Boolean.FALSE;
		this.properties.put(ChartPropertiesConstants.SHOW_X_AXIS_TITLE, b);
	}

	public void setShowYAxisTitle(final boolean toShow)
	{
		final Boolean b = toShow ? Boolean.TRUE : Boolean.FALSE;
		this.properties.put(ChartPropertiesConstants.SHOW_Y_AXIS_TITLE, b);
	}
	
	public void setShowChartMarker(final boolean toShow)
	{
		final Boolean b = toShow ? Boolean.TRUE : Boolean.FALSE;
		this.properties.put(ChartPropertiesConstants.SHOW_CHART_MARKER, b);
	}
	
	public void setShowChartLabel(final boolean toShow)
	{
		final Boolean b = toShow ? Boolean.TRUE : Boolean.FALSE;
		this.properties.put(ChartPropertiesConstants.SHOW_CHART_LABEL, b);
	}

	public Color getYAxisSeriesColor(final int i)
	{
		final IntHashMap axisSeriesColors = this.getYAxisSeriesColors();
		return axisSeriesColors != null ? (Color) axisSeriesColors.get(i) : null;
	}

	public void setYAxisSeriesColor(final int i, final Color yAxisSeriesColor)
	{
		if (yAxisSeriesColor != null)
		{
			IntHashMap colourMap = this.getYAxisSeriesColors();
			if (colourMap == null)
			{
				colourMap = new IntHashMap();
				this.properties.put(ChartPropertiesConstants.COLOURS, colourMap);
			}
			Color colour = this.getYAxisSeriesColor(i);
			if (colour == null)
			{
				colour = new Color(yAxisSeriesColor.red, yAxisSeriesColor.green, yAxisSeriesColor.blue);
				colourMap.put(i, colour);
			}
			else
			{
				colour.red = yAxisSeriesColor.red;
				colour.green = yAxisSeriesColor.green;
				colour.blue = yAxisSeriesColor.blue;
			}
		}
	}

	public String getYAxisSeriesText(final int i)
	{
		final IntHashMap axisSeriesText = this.getYAxisSeriesText();
		return axisSeriesText != null ? (String) axisSeriesText.get(i) : null;
	}

	private IntHashMap getYAxisSeriesText()
	{
		return (IntHashMap) this.properties.get(ChartPropertiesConstants.Y_AXIS_SERIES_TEXT);
	}

	public void setYAxisSeriesText(final int i, final String text)
	{
		if (text != null)
		{
			IntHashMap textMap = this.getYAxisSeriesText();
			if (textMap == null)
			{
				textMap = new IntHashMap();
				this.properties.put(ChartPropertiesConstants.Y_AXIS_SERIES_TEXT, textMap);
			}
			textMap.put(i, text);
		}
	}

	public Boolean getXAxisGrids()
	{
		return (Boolean) this.properties.get(ChartPropertiesConstants.X_AXIS_GRIDS);
	}

	public void setShowLegend(final boolean show)
	{
		final Boolean b = show ? Boolean.TRUE : Boolean.FALSE;
		this.properties.put(ChartPropertiesConstants.SHOW_LEGEND, b);
	}

	public void setShowXAxisGrids(final boolean grids)
	{
		final Boolean b = grids ? Boolean.TRUE : Boolean.FALSE;
		this.properties.put(ChartPropertiesConstants.X_AXIS_GRIDS, b);
	}

	public Boolean getYAxisGrids()
	{
		return (Boolean) this.properties.get(ChartPropertiesConstants.Y_AXIS_GRIDS);
	}

	public Boolean getShowLegend()
	{
		return (Boolean) this.properties.get(ChartPropertiesConstants.SHOW_LEGEND);
	}

	public void setShowYAxisGrids(final boolean grids)
	{
		final Boolean b = grids ? Boolean.TRUE : Boolean.FALSE;
		this.properties.put(ChartPropertiesConstants.Y_AXIS_GRIDS, b);
	}

	public Boolean isLogarithmic()
	{
		return (Boolean) this.properties.get(ChartPropertiesConstants.LOGARITHMIC);
	}

	public void setLogarithmic(final boolean isLog)
	{
		final Boolean b = isLog ? Boolean.TRUE : Boolean.FALSE;
		this.properties.put(ChartPropertiesConstants.LOGARITHMIC, b);
	}

	public Font getXAxisTitleFont()
	{
		return (Font) this.properties.get(ChartPropertiesConstants.X_AXIS_TITLE_FONT);
	}

	public void setXAxisTitleFont(final Font font)
	{
		if (font != null)
		{
			Font xAxisTitleFont = this.getXAxisTitleFont();
			if (xAxisTitleFont == null)
			{
				xAxisTitleFont = new Font(font.name, font.size, font.style);
				this.properties.put(ChartPropertiesConstants.X_AXIS_TITLE_FONT, xAxisTitleFont);
			}
			else
			{
				xAxisTitleFont.name = font.name;
				xAxisTitleFont.size = font.size;
				xAxisTitleFont.style = font.style;
			}
			final Title axisTitle = this.getXAxisTitle();
			if (axisTitle != null)
			{
				axisTitle.setFont(font);
			}
		}
	}

	public Font getYAxisTitleFont()
	{
		return (Font) this.properties.get(ChartPropertiesConstants.Y_AXIS_TITLE_FONT);
	}

	public void setYAxisTitleFont(final Font font)
	{
		if (font != null)
		{
			Font yAxisTitleFont = this.getYAxisTitleFont();
			if (yAxisTitleFont == null)
			{
				yAxisTitleFont = new Font(font.name, font.size, font.style);
				this.properties.put(ChartPropertiesConstants.Y_AXIS_TITLE_FONT, yAxisTitleFont);
			}
			else
			{
				yAxisTitleFont.name = font.name;
				yAxisTitleFont.size = font.size;
				yAxisTitleFont.style = font.style;
			}
			final Title axisTitle = this.getYAxisTitle();
			if (axisTitle != null)
			{
				axisTitle.setFont(font);
			}
		}
	}

	public Font getChartTitleFont()
	{
		return (Font) this.properties.get(ChartPropertiesConstants.CHART_TITLE_FONT);
	}

	public void setChartTitleFont(final Font font)
	{
		if (font != null)
		{
			Font chartTitleFont = this.getChartTitleFont();
			if (chartTitleFont == null)
			{
				chartTitleFont = new Font(font.name, font.size, font.style);
				this.properties.put(ChartPropertiesConstants.CHART_TITLE_FONT, chartTitleFont);
			}
			else
			{
				chartTitleFont.name = font.name;
				chartTitleFont.size = font.size;
				chartTitleFont.style = font.style;
			}
			this.getChartTitle().setFont(font);
		}
	}

	public Color getChartTitleBackgroundColour()
	{
		return (Color) this.properties.get(ChartPropertiesConstants.CHART_TITLE_BACKGROUND_COLOUR);
	}

	public Color getTickTextColour()
	{
		return (Color) this.properties.get(ChartPropertiesConstants.TICK_TEXT_COLOUR);
	}

	public void setTickTextColour(final Color colour)
	{
		if (colour != null)
		{
			Color textColour = this.getTickTextColour();
			if (textColour == null)
			{
				textColour = new Color(colour.red, colour.green, colour.blue);
				this.properties.put(ChartPropertiesConstants.TICK_TEXT_COLOUR, textColour);
			}
			else
			{
				textColour.red = colour.red;
				textColour.green = colour.green;
				textColour.blue = colour.blue;
			}
		}
	}

	public Color getLegendTextColour()
	{
		return (Color) this.properties.get(ChartPropertiesConstants.LEGEND_TEXT_COLOUR);
	}

	public void setLegendTextColour(final Color colour)
	{
		if (colour != null)
		{
			Color textColour = this.getLegendTextColour();
			if (textColour == null)
			{
				textColour = new Color(colour.red, colour.green, colour.blue);
				this.properties.put(ChartPropertiesConstants.LEGEND_TEXT_COLOUR, textColour);
			}
			else
			{
				textColour.red = colour.red;
				textColour.green = colour.green;
				textColour.blue = colour.blue;
			}
		}
	}

	public Color getTickBackgroundColour()
	{
		return (Color) this.properties.get(ChartPropertiesConstants.TICK_BACKGROUND_COLOUR);
	}

	public void setTickBackgroundColour(final Color colour)
	{
		if (colour != null)
		{
			Color tickBackgroundColour = this.getTickBackgroundColour();
			if (tickBackgroundColour == null)
			{
				tickBackgroundColour = new Color(colour.red, colour.green, colour.blue);
				this.properties.put(ChartPropertiesConstants.TICK_BACKGROUND_COLOUR, tickBackgroundColour);
			}
			else
			{
				tickBackgroundColour.red = colour.red;
				tickBackgroundColour.green = colour.green;
				tickBackgroundColour.blue = colour.blue;
			}
		}
	}

	public Color getLegendBackgroundColour()
	{
		return (Color) this.properties.get(ChartPropertiesConstants.LEGEND_BACKGROUND_COLOUR);
	}

	public void setLegendBackgroundColour(final Color colour)
	{
		if (colour != null)
		{
			Color backgroundColour = this.getLegendBackgroundColour();
			if (backgroundColour == null)
			{
				backgroundColour = new Color(colour.red, colour.green, colour.blue);
				this.properties.put(ChartPropertiesConstants.LEGEND_BACKGROUND_COLOUR, backgroundColour);
			}
			else
			{
				backgroundColour.red = colour.red;
				backgroundColour.green = colour.green;
				backgroundColour.blue = colour.blue;
			}
		}
	}

	public void setChartTitleBackgroundColour(final Color colour)
	{
		if (colour != null)
		{
			Color backgroundColour = this.getChartTitleBackgroundColour();
			if (backgroundColour == null)
			{
				backgroundColour = new Color(colour.red, colour.green, colour.blue);
				this.properties.put(ChartPropertiesConstants.CHART_TITLE_BACKGROUND_COLOUR, backgroundColour);
			}
			else
			{
				backgroundColour.red = colour.red;
				backgroundColour.green = colour.green;
				backgroundColour.blue = colour.blue;
			}
			final Title chartTitle = this.getChartTitle();
			if (chartTitle != null)
			{
				chartTitle.setBackgroundColor(colour);
			}
		}
	}

	public Color getXAxisTitleBackgroundColour()
	{
		return (Color) this.properties.get(ChartPropertiesConstants.X_AXIS_TITLE_BACKGROUND_COLOUR);
	}

	public void setXAxisTitleBackgroundColour(final Color colour)
	{
		if (colour != null)
		{
			Color backgroundColour = this.getXAxisTitleBackgroundColour();
			if (backgroundColour == null)
			{
				backgroundColour = new Color(colour.red, colour.green, colour.blue);
				this.properties.put(ChartPropertiesConstants.X_AXIS_TITLE_BACKGROUND_COLOUR, backgroundColour);
			}
			else
			{
				backgroundColour.red = colour.red;
				backgroundColour.green = colour.green;
				backgroundColour.blue = colour.blue;
			}
			final Title axisTitle = this.getXAxisTitle();
			if (axisTitle != null)
			{
				axisTitle.setBackgroundColor(colour);
			}
		}
	}

	public Color getYAxisTitleBackgroundColour()
	{
		return (Color) this.properties.get(ChartPropertiesConstants.Y_AXIS_TITLE_BACKGROUND_COLOUR);
	}

	public void setYAxisTitleBackgroundColour(final Color colour)
	{
		if (colour != null)
		{
			Color backgroundColour = this.getYAxisTitleBackgroundColour();
			if (backgroundColour == null)
			{
				backgroundColour = new Color(colour.red, colour.green, colour.blue);
				this.properties.put(ChartPropertiesConstants.Y_AXIS_TITLE_BACKGROUND_COLOUR, backgroundColour);
			}
			else
			{
				backgroundColour.red = colour.red;
				backgroundColour.green = colour.green;
				backgroundColour.blue = colour.blue;
			}
			final Title axisTitle = this.getYAxisTitle();
			if (axisTitle != null)
			{
				axisTitle.setBackgroundColor(colour);
			}
		}
	}
}
