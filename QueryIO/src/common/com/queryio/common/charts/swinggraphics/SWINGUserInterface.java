/*
 * @(#)  SWINGUserInterface.java Feb 7, 2005
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

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.queryio.common.IProductConstants;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.charts.util.PngEncoder;
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
public class SWINGUserInterface implements UserInterface
{
	private BufferedImage image;
	private Graphics2D graphics;
	private final SWINGTypeConverter typeConverter;
	private boolean thinLine = false;

	private PngEncoder pngEncoder = null;
	private static final int PNG_DEFAULT_COMPRESSION = 5;

	private SWINGUserInterface scratchUserInterface = null;

	public SWINGUserInterface()
	{
		this(true);
	}

	public SWINGUserInterface(final boolean createScratch)
	{
		this.typeConverter = SWINGTypeConverter.getInstance();
		if (createScratch)
		{
			this.scratchUserInterface = new SWINGUserInterface(false);
		}
	}

	public boolean showSeriesSelector() 
	{
		return false;
	}
	
	public final BufferedImage getImage()
	{
		return this.image;
	}

	public void setExternalGraphics(final Object gc)
	{
		this.graphics = (Graphics2D) gc;
	}

	public void createGraphics(final int width, final int height)
	{
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		this.graphics = this.image.createGraphics();
		this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	public void setClipping(final int x, final int y, final int width, final int height)
	{
		this.graphics.setClip(x, y, width, height);
	}

	public void disposeGraphics()
	{
		this.graphics.dispose();
	}

	public int getStringWidth(final String str)
	{
		return this.graphics.getFontMetrics().stringWidth(str);
	}

	public void setFont(final Font font)
	{
		this.graphics.setFont(this.typeConverter.getActualUIFont(font));
	}

	public Font getFont()
	{
		return this.typeConverter.getConvertedFont(this.graphics.getFont());
	}

	public void setBackground(final Color color)
	{
		this.graphics.setColor(this.typeConverter.getActualUIColor(color));
	}

	public void setForeground(final Color foreGroundColor)
	{
		this.graphics.setColor(this.typeConverter.getActualUIColor(foreGroundColor));
	}

	public Color getForeground()
	{
		return this.typeConverter.getConvertedColor(this.graphics.getColor());
	}

	public Color getBackground()
	{
		return this.typeConverter.getConvertedColor(this.graphics.getColor());
	}

	public Point stringExtent(final String text)
	{
		final FontMetrics fm = this.graphics.getFontMetrics();
		return new Point(fm.stringWidth(text), fm.getHeight());
	}

	public int getFontHeight()
	{
		return this.graphics.getFontMetrics().getHeight();
	}

	public Rectangle getClipping()
	{
		return this.typeConverter.getConvertedRectangle(this.graphics.getClip());
	}

	public void drawStringVertically(final String string, final int x1, final int y1)
	{
		final double ANGLE = Math.PI * 0.5;
		this.graphics.rotate(-ANGLE);
		final FontMetrics metrics = this.graphics.getFontMetrics();
		final int width = metrics.stringWidth(string);
		final java.awt.Rectangle r = this.graphics.getClipBounds();
		final int height = metrics.getHeight();
		final int x = r.x + (r.width - width) / 2;
		final int y = r.y + height + (r.height - height) / 2 - metrics.getDescent();
		this.graphics.drawString(string, x, y);
		this.graphics.rotate(ANGLE);
	}

	public void drawString(final String string, final int x, int y)
	{
		final FontMetrics fm = this.graphics.getFontMetrics();
		y += fm.getAscent();
		this.graphics.drawString(string, x, y);
	}

	public void fillRectangle(final Rectangle rectangle)
	{
		this.graphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}

	public void drawString(final String string, final int x, int y, final boolean isBackgroundTransparent)
	{
		final FontMetrics fm = this.graphics.getFontMetrics();
		y += fm.getAscent();

		if (isBackgroundTransparent)
		{
			final java.awt.Color oldBGColor = this.graphics.getBackground();
			this.graphics.setBackground(new java.awt.Color(oldBGColor.getRed(), oldBGColor.getGreen(), oldBGColor
					.getBlue(), 0));

			this.graphics.drawString(string, x, y);

			this.graphics.setBackground(oldBGColor);
		}
		else
		{
			this.graphics.drawString(string, x, y);
		}
	}

	public void fillRectangle(final int x, final int y, final int width, final int height)
	{
		this.graphics.fillRect(x, y, width, height);
	}

	public void drawLine(final int x1, final int y1, final int x2, final int y2)
	{
		if (thinLine)
			this.graphics.setStroke(new BasicStroke(0.05f));
		this.graphics.drawLine(x1, y1, x2, y2);
	}

	public void setClipping(final Rectangle rect)
	{
		this.graphics.setClip(this.typeConverter.getActualRectangle(rect));
	}

	public void drawRectangle(final Rectangle rect)
	{
		this.graphics.drawRect(rect.x, rect.y, rect.width, rect.height);
	}

	public LineAttributes getLineStyle()
	{
		BasicStroke bsOrig = (BasicStroke) graphics.getStroke();
		
//		float dash[] = bsOrig.getDashArray();
		//
//				if (dash == null || dash.length == 0 || dash[1] < 1)
//				{
//					return LINE_STYLE_SOLID;
//				} else
//				{
//					return LINE_STYLE_DOT;
//				}

		int cap = LineAttributes.CAP_FLAT;
		switch (bsOrig.getEndCap())
		{
			case BasicStroke.CAP_BUTT:
				cap = LineAttributes.CAP_FLAT;
				break;
			case BasicStroke.CAP_ROUND:
				cap = LineAttributes.CAP_ROUND;
				break;
			case BasicStroke.CAP_SQUARE:
				cap = LineAttributes.CAP_SQUARE;
				break;
		}
		int join = LineAttributes.JOIN_ROUND;
		switch (bsOrig.getLineJoin())
		{
			case BasicStroke.JOIN_MITER:
				join = LineAttributes.JOIN_MITER;
				break;
			case BasicStroke.JOIN_ROUND:
				join = LineAttributes.JOIN_ROUND;
				break;
			case BasicStroke.JOIN_BEVEL:
				join = LineAttributes.JOIN_BEVEL;
				break;
		}
		return new LineAttributes(bsOrig.getLineWidth(), cap, join);
	}

	public void setLineStyle(final LineAttributes lineAttr)
	{
		BasicStroke bsOrig = (BasicStroke) graphics.getStroke();
		// float dash[] = null;
		//				
		// if(lineStyle == LINE_STYLE_SOLID)
		// {
		// dash = new float[]{0, 0};
		// }
		// else if(lineStyle == LINE_STYLE_DOT)
		// {
		// dash = new float[]{2, 2};
		// }

		int cap = bsOrig.getEndCap();
		switch (lineAttr.cap)
		{
			case LineAttributes.CAP_FLAT:
				cap = BasicStroke.CAP_BUTT;
				break;
			case LineAttributes.CAP_ROUND:
				cap = BasicStroke.CAP_ROUND;
				break;
			case LineAttributes.CAP_SQUARE:
				cap = BasicStroke.CAP_SQUARE;
				break;
		}
		int join = bsOrig.getLineJoin();
		switch (lineAttr.join)
		{
			case LineAttributes.JOIN_MITER:
				join = BasicStroke.JOIN_MITER;
				break;
			case LineAttributes.JOIN_ROUND:
				join = BasicStroke.JOIN_ROUND;
				break;
			case LineAttributes.JOIN_BEVEL:
				join = BasicStroke.JOIN_BEVEL;
				break;
		}

		BasicStroke bsNew = new BasicStroke(lineAttr.width, cap, join, bsOrig
				.getMiterLimit(), bsOrig.getDashArray(), bsOrig.getDashPhase());

		graphics.setStroke(bsNew);
	}

	public void drawRectangle(final int x, final int y, final int width, final int height)
	{
		this.graphics.drawRect(x, y, width, height);
	}

	public void drawArc(final int x, final int y, final int width, final int height, final int startAngle,
			final int endAngle)
	{
		this.graphics.drawArc(x, y, width, height, startAngle, endAngle);
	}

	public void fillArc(final int x, final int y, final int width, final int height, final int startAngle,
			final int endAngle)
	{
		this.graphics.fillArc(x, y, width, height, startAngle, endAngle);
	}

	public byte[] getPNG(final int width, final int height, final PngOutputStream outputStream)
	{
		if (this.pngEncoder == null)
		{
			this.pngEncoder = new PngEncoder();
			this.pngEncoder.setCompressionLevel(PNG_DEFAULT_COMPRESSION);
		}
		final byte[] arr = this.pngEncoder.pngEncode(this.image, outputStream.toByteArray());
		outputStream.setByteArray(arr);
		outputStream.setReferenceChanged(this.pngEncoder.isModified());
		outputStream.setLastPosition(this.pngEncoder.getLastPosition());
		return arr;
	}

	public int getUserInterfaceType()
	{
		return IProductConstants.USER_INTERFACE_SWING;
	}

	public void disposeImage()
	{
		this.image = null;
	}

	public Object getBufferedImage()
	{
		return this.getImage();
	}

	/**
	 * @see com.queryio.sysmoncommon.charts.interfaces.UserInterface#getDefaultFontName()
	 */
	public String getDefaultFontName()
	{
		return "Tahoma"; //$NON-NLS-1$
	}

	/**
	 * @see com.queryio.sysmoncommon.charts.interfaces.UserInterface#getDefaultFontHeight()
	 */
	public int getDefaultFontHeight()
	{
		return 10;
	}

	public final UserInterface getScratchUserInterface()
	{
		return this.scratchUserInterface;
	}

	public void setThinLine(boolean thinLine) 
	{
		this.thinLine = thinLine;
		if (this.scratchUserInterface != null)
		{
			this.scratchUserInterface.setThinLine(this.thinLine);
		}
	}

}
