package com.queryio.common.charts.components;

import com.queryio.common.exporter.dstruct.Rectangle;
import com.queryio.common.util.PlatformHandler;

public class MarkerObject
{
	private final Rectangle rectangle;
	private String toolTipText;

	protected int valueIndex;
	protected int ySeriesIndex;
	private boolean legendMarker = false;
	private boolean seriesVisible = false;
	
	public MarkerObject(final Rectangle rect, String toolTipText)
	{
		this.rectangle = rect;
		this.toolTipText = toolTipText;
	}
	public MarkerObject(final Rectangle rect, final String xValue, final String yValue)
	{
		this.rectangle = rect;
		final StringBuffer buffer = new StringBuffer();
		buffer.append("x : "); //$NON-NLS-1$
		buffer.append(xValue);
		buffer.append(PlatformHandler.LINE_SEPARATOR);
		buffer.append("y : ");//$NON-NLS-1$
		buffer.append(yValue);
		toolTipText = buffer.toString();
	}

	public MarkerObject(Rectangle  rect, String xValue, String yValue, int valueIndex, int ySeriesIndex)
	{
		this(rect, xValue, yValue);
		this.valueIndex = valueIndex;
		this.ySeriesIndex = ySeriesIndex;
	}
	
	public boolean contains(final int x, final int y)
	{
		if (this.rectangle.contains(x, y))
		{
			return true;
		}
		return false;
	}

	public String getToolTipText()
	{
		return toolTipText;
	}

	public int getValueIndex()
	{
		return this.valueIndex;
	}
	
	public int getSeriesIndex() 
	{
		return ySeriesIndex;
	}
	
	public void setSeriesIndex(int seriesIndex) 
	{
		ySeriesIndex = seriesIndex;
	}
	
	public boolean isLegendMarker() 
	{
		return legendMarker;
	}
	
	public void setLegendMarker(boolean legendMarker) 
	{
		this.legendMarker = legendMarker;
	}
	
	public boolean isSeriesVisible() 
	{
		return seriesVisible;
	}
	
	public void setSeriesVisible(boolean seriesVisible) 
	{
		this.seriesVisible = seriesVisible;
	}
	
	public void setToolTipText(String toolTipText) 
	{
		this.toolTipText = toolTipText;
	}

}
