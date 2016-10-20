/*
 * @(#)  UserInterface.java Feb 4, 2005
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
package com.queryio.common.charts.interfaces;

import com.queryio.common.charts.util.PngOutputStream;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Font;
import com.queryio.common.exporter.dstruct.LineAttributes;
import com.queryio.common.exporter.dstruct.Point;
import com.queryio.common.exporter.dstruct.Rectangle;

/**
 * 
 * @author Exceed Consultancy Services
 */
public interface UserInterface
{
	
	boolean showSeriesSelector();

	UserInterface getScratchUserInterface();

	void createGraphics(int width, int height);

	void setExternalGraphics(Object gc);

	/**
	 * Sets the area of the receiver which can be changed by drawing operations
	 * to the rectangular area specified by the arguments.
	 * 
	 * @param x
	 *            the x coordinate of the clipping rectangle
	 * @param y
	 *            the y coordinate of the clipping rectangle
	 * @param width
	 *            the width of the clipping rectangle
	 * @param height
	 *            the height of the clipping rectangle
	 */
	void setClipping(int x, int y, int width, int height);

	/**
	 * Returns the bounding rectangle of the receiver's clipping region. If no
	 * clipping region is set, the return value will be a rectangle which covers
	 * the entire bounds of the object the receiver is drawing on.
	 * 
	 * @return the bounding rectangle of the clipping region
	 */
	Rectangle getClipping();

	/**
	 * The <em>extent</em> of a string is the width and height of the
	 * rectangular area it would cover if drawn in a particular font (in this
	 * case, the current font in the receiver).
	 * 
	 * @param string
	 *            the string to measure
	 */
	int getStringWidth(String text);

	int getFontHeight();

	void disposeGraphics();

	void setFont(Font font);

	void setForeground(Color foreGroundColor);

	Point stringExtent(String text);

	void drawStringVertically(String string, int x, int y);

	void drawString(String string, int x, int y);

	void drawString(String string, int x, int y, boolean isBackgroundTransparent);

	Color getBackground();

	void setBackground(Color color);

	void fillRectangle(Rectangle rectangle);

	Font getFont();

	void fillRectangle(int x, int y, int width, int height);

	void drawLine(int x1, int y1, int x2, int y2);

	void setClipping(Rectangle rect);

	void drawRectangle(Rectangle rect);

	Color getForeground();

	LineAttributes getLineStyle();
	
	void setLineStyle(LineAttributes lineStyle);

//	int getLineStyle();
//
//	void setLineStyle(int lineStyle);
//	
//	float getLineWidth();
//
//	void setLineWidth(float lineWidth);

	void drawRectangle(int x, int y, int width, int height);

	void drawArc(int x, int y, int width, int height, int startAngle, int endAngle);

	void fillArc(int x, int y, int width, int height, int startAngle, int endAngle);

	byte[] getPNG(int width, int height, PngOutputStream outputStream);

	int getUserInterfaceType();

	void disposeImage();

	Object getBufferedImage();

	String getDefaultFontName();

	int getDefaultFontHeight();
	// void rectangleCheck(Rectangle r);

}
