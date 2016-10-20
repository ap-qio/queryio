/*
 * @(#)  Legend.java Feb 4, 2005
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
package com.queryio.common.charts.components;

import com.queryio.common.charts.independent.ChartConstants;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Font;
import com.queryio.common.exporter.dstruct.Rectangle;

/**
 * 
 * @author Exceed Consultancy Services
 */
public final class Legend
{
	public static final int ISTRIKETHROUGHLENGTH = 10;

	private final String text;

	private final Color seriesColor;

	private static final int GAPBETWEENRECTANDTEXT = 10 + 3;

	public Font legendFont;

	private int width = -1;

	private final Color legendColour;

	private static int maxHeight = -1;

	public Legend(final String text, final Color seriesColor, final Color legendColour)
	{
		this.text = text;
		this.seriesColor = seriesColor;
		this.legendColour = legendColour;
	}

	/**
	 * Legend#draw
	 * 
	 * @param graphics
	 * @param r
	 */
	public MarkerObject draw(final UserInterface graphics, final Rectangle r, boolean selected, int ySeriesIndex)
	{
		MarkerObject mo = null;
		graphics.setFont(this.legendFont);

		final int height = graphics.getFontHeight();

		int x = r.x;
		final int y = r.y + (r.height - height) / 2;

		final int squareDim = 7;
		
		if (graphics.showSeriesSelector())
		{
			// Show/Hide rectangle
			graphics.setForeground(ChartConstants.COLOR_BLACK);
			graphics.setBackground(ChartConstants.COLOR_BLACK);
			Rectangle marker = new Rectangle(x, y + squareDim - 3, squareDim, squareDim - 1);
			mo = new MarkerObject(marker, selected ? "Hide" : "Show");
			mo.setSeriesIndex(ySeriesIndex);
			mo.setLegendMarker(true);
			mo.setSeriesVisible(selected);
			
			graphics.drawRectangle(x, y + squareDim - 5, 8, 8);
			if (selected) 
			{
				int x1 = x + 1;
				int y1 = y + squareDim - 2;
				int x2 = x + 3;
				int y2 = y + 9;
				graphics.drawLine(x1, y1, x2, y2);
				graphics.drawLine(x2, y2, x1 + 7, y1 - 2);
			}
			/*
			 * graphics.drawArc(x, y + 3, 8, 8, 0, 360); if (selected) {
			 * graphics.fillArc(x + 2, y + 5, 5, 5, 0, 360); }
			 */
			x += 12;
		}
		graphics.setBackground(this.seriesColor);
		graphics.setForeground(this.seriesColor);
		if (graphics.showSeriesSelector())
		{
			graphics.fillRectangle(x, y + squareDim - 4, squareDim + 2, squareDim - 1);
		}
		else
		{
			graphics.fillRectangle(x + 4, y + squareDim - 4, squareDim, squareDim - 2);
		}
		// graphics.drawLine(x - iStrikeThroughLength, y + squareDim -1, x +
		// squareDim + iStrikeThroughLength - 1, y + squareDim -1);

		graphics.setForeground(ChartConstants.COLOR_BLACK);
		//graphics.drawRectangle(x, y + squareDim - 4, squareDim + 2, squareDim - 1);
		if (graphics.showSeriesSelector())
		{
			graphics.drawRectangle(x, y + squareDim - 4, squareDim + 2, squareDim - 1);
		}
		else
		{
			graphics.drawRectangle(x + 4, y + squareDim - 4, squareDim, squareDim - 2);
		}
		final Color foreground = graphics.getForeground();
		graphics.setForeground(this.legendColour);
		
		if (graphics.showSeriesSelector())
		{
			graphics.drawString(this.text, x + GAPBETWEENRECTANDTEXT, y, true);
		}
		else
		{
			graphics.drawString(this.text, x + GAPBETWEENRECTANDTEXT, y + 1, true);
		}
		
		graphics.setForeground(foreground);

		// The following lines are drawn for giving the shawdowed engrraved
		// effect for the legend colors.
		// graphics.drawLine(x, y + squareDim - 3, x + squareDim - 1, y +
		// squareDim - 3);
		// graphics.drawLine(x, y + squareDim - 3, x, y + squareDim - 3 +
		// squareDim - 1);
		return mo;
	}

	/**
	 * Legend#getMaxWidth
	 * 
	 * @param graphics
	 * @return
	 */
	public int getMaxWidth(final UserInterface graphics)
	{
		if (this.width == -1)
		{
			final Font oldFont = graphics.getFont();
			graphics.setFont(this.legendFont);
			this.width = graphics.stringExtent(this.text).x;
			// calculate the maxHeight here itself
			maxHeight = graphics.getFontHeight() + 2;
			graphics.setFont(oldFont);
		}
		return this.width;
	}

	public static int getMaxHeight(final UserInterface graphics, final Font font)
	{
		if (maxHeight == -1)
		{
			final Font oldFont = graphics.getFont();
			graphics.setFont(font);
			maxHeight = graphics.getFontHeight() + 2;
			graphics.setFont(oldFont);
		}
		return maxHeight;
	}

	/**
	 * method getText
	 * 
	 * @return
	 */
	public final String getText()
	{
		return this.text;
	}

	public Color getSeriesColor()
	{
		return this.seriesColor;
	}
}
