package com.queryio.common.charts.swinggraphics;

import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Font;
import com.queryio.common.exporter.dstruct.Point;
import com.queryio.common.exporter.dstruct.Rectangle;

/*
 * @(#)  SWINGTypeConverter.java Feb 7, 2005
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

/**
 * 
 * @author Exceed Consultancy Services
 */
public final class SWINGTypeConverter extends SWINGTypeConversionManager
{
	private static SWINGTypeConverter typeConverter;

	private SWINGTypeConverter()
	{
		// DO NOTHING
	}

	public static final SWINGTypeConverter getInstance()
	{
		if (typeConverter == null)
		{
			typeConverter = new SWINGTypeConverter();
		}
		return typeConverter;
	}

	public java.awt.Color getActualUIColor(final Color convertedColor)
	{
		return (java.awt.Color) this.getUIColor(convertedColor);
	}

	public java.awt.Font getActualUIFont(final Font convertedFont)
	{
		return (java.awt.Font) this.getUIFont(convertedFont);
	}

	public java.awt.Point getActualPoint(final Point convertedPoint)
	{
		return (java.awt.Point) this.getUIPoint(convertedPoint);
	}

	public java.awt.Rectangle getActualRectangle(final Rectangle convertedRectangle)
	{
		return (java.awt.Rectangle) this.getUIRectangle(convertedRectangle);
	}
}
