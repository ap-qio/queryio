/*
 * @(#)  SWINGTypeConversionManager.java Feb 7, 2005
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
package com.queryio.common.charts.swinggraphics;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.queryio.common.charts.interfaces.TypeConversionInterface;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Font;
import com.queryio.common.exporter.dstruct.Point;
import com.queryio.common.exporter.dstruct.Rectangle;

/**
 * 
 * @author Exceed Consultancy Services
 */
public class SWINGTypeConversionManager implements TypeConversionInterface {
	private static final int FONT_ADJUSTMENT = 1;

	public Color getConvertedColor(final Object uiColor) {
		final java.awt.Color uiColorObj = (java.awt.Color) uiColor;
		return new Color(uiColorObj.getRed(), uiColorObj.getGreen(), uiColorObj.getBlue());
	}

	public Object getUIColor(final Color convertedColor) {
		return new java.awt.Color(convertedColor.red, convertedColor.green, convertedColor.blue);
	}

	public Font getConvertedFont(final Object uiFont) {
		final java.awt.Font uiFontObj = (java.awt.Font) uiFont;

		int style = Font.STYLE_PLAIN;
		if (uiFontObj.isBold()) {
			style = Font.STYLE_BOLD;
		} else if (uiFontObj.isItalic()) {
			style = Font.STYLE_ITALIC;
		}
		return new Font(uiFontObj.getName(), uiFontObj.getSize() - FONT_ADJUSTMENT, style);
	}

	public Object getUIFont(final Font convertedFont) {
		return new java.awt.Font(convertedFont.name, convertedFont.style, convertedFont.size + FONT_ADJUSTMENT);
	}

	public Point getConvertedPoint(final Object uiPoint) {
		final java.awt.Point uiPointObj = (java.awt.Point) uiPoint;
		return new Point(uiPointObj.x, uiPointObj.y);
	}

	public Object getUIPoint(final Point convertedPoint) {
		return new java.awt.Point(convertedPoint.x, convertedPoint.y);
	}

	public Rectangle getConvertedRectangle(final Object uiRectangle) {
		Rectangle rectangle = null;
		if (uiRectangle != null) {
			final Shape uiShapeObj = (Shape) uiRectangle;
			final Rectangle2D rectangle2D = uiShapeObj.getBounds2D();
			rectangle = new Rectangle((int) rectangle2D.getX(), (int) rectangle2D.getY(), (int) rectangle2D.getWidth(),
					(int) rectangle2D.getHeight());
		}
		return rectangle;
	}

	public Object getUIRectangle(final Rectangle convertedRectangle) {
		return (convertedRectangle != null) ? new java.awt.Rectangle(convertedRectangle.x, convertedRectangle.y,
				convertedRectangle.width, convertedRectangle.height) : null;
	}

}
