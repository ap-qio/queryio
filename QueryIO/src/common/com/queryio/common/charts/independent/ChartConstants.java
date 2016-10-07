/*
 * @(#)  ChartConstants.java Feb 4, 2005
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

import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Rectangle;

/**
 * 
 * @author Exceed Consultancy Services
 */
public class ChartConstants
{
	private ChartConstants()
	{
		// DO Nothing - Private Constructor to prevent instantiation of class.
	}

	// public static final Font TITLE_FONT;
	// public static final Font AXIS_FONT;
	// public static final Font LEGEND_FONT;
	// public static final Font TICK_FONT_DEFAULT;
	// public static final Font TICK_FONT8;

	// public static final Color COLOR_RED = new Color(0xff, 0x00, 0x00);
	public static final Color COLOR_WHITE = new Color(0xff, 0xff, 0xff);
	public static final Color COLOR_BLACK = new Color(0x00, 0x00, 0x00);
	public static final Color COLOR_ALTERNATE = new Color(0xf8, 0xf8, 0xf8);
	public static final Color COLOR_GRAY = new Color(0xe0, 0xe0, 0xe0);
	public static final Color COLOR_LIGHT_GRAY = new Color(0xeb, 0xeb, 0xeb);//Used for plot area in AM
	public static final Color COLOR_DARK_GRAY = new Color(0x80, 0x80, 0x80);
	// public static final Color GRID_COLOR = new Color((int)(0xe0 * 0.99),
	// (int)(0xe0 * 0.99), (int)(0xe0 * 0.99));
	public static final Color GRID_COLOR = new Color(0xe6, 0xe6, 0xe6);
//	public static final Color GRID_COLOR = COLOR_ALTERNATE;
	public static final Color PIE_CHARTBORDER = COLOR_BLACK;

	public static final int GRID_LINE_THICKNESS = 1;

	static
	{
		// String sFontName = "Tahoma";
		// TITLE_FONT = new Font(sFontName, 10, Font.STYLE_BOLD);
		// AXIS_FONT = new Font(sFontName, 9, Font.STYLE_PLAIN);
		// LEGEND_FONT = new Font(sFontName, 8, Font.STYLE_PLAIN);
		// TICK_FONT_DEFAULT = new Font(sFontName, 7, Font.STYLE_PLAIN);
		// TICK_FONT8 = new Font(sFontName, 6, Font.STYLE_PLAIN);
	}

	public static void rectangleCheck(final UserInterface userInterface, final Rectangle rect, final Color fillColor)
	{
		final Color oldColor = userInterface.getBackground();
		userInterface.setBackground(fillColor);
		userInterface.fillRectangle(rect);
		userInterface.setBackground(oldColor);
	}

	public static String format(final int value)
	{
		return String.valueOf(value);
	}
}
